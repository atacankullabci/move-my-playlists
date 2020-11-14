package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.InProgressMap;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.dto.PlaylistDTO;
import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.TrackDTO;
import com.atacankullabci.immovin.dto.UserDTO;
import com.atacankullabci.immovin.repository.InProgressMapRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpotifyService {

    static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    private static final String baseTrackQueryStr = "https://api.spotify.com/v1/search?q=";
    private static final String getFirstTrackIdJsonPath = "$.tracks.items[0].id";
    private static final String getFirstTrackURIJsonPath = "$.tracks.items[0].uri";

    private final CacheService cacheService;
    private InProgressMapRepository inProgressMapRepository;
    private UserRepository userRepository;

    public SpotifyService(CacheService cacheService, InProgressMapRepository inProgressMapRepository, UserRepository userRepository) {
        this.cacheService = cacheService;
        this.inProgressMapRepository = inProgressMapRepository;
        this.userRepository = userRepository;
    }

    public TokenDTO getJWTToken(String code) {
        String url = "https://accounts.spotify.com/api/token";
        HttpEntity<MultiValueMap<String, String>> request;
        ResponseEntity<TokenDTO> response = null;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", "http://imovin.club/callback/");
        map.add("client_id", "b5ead0205230451d877d487a856a30a9");
        map.add("client_secret", "3e18969a0fc94531b04357edc447461f");
        map.add("code", code);

        request = new HttpEntity<>(map, headers);
        response = restTemplate
                .exchange(url, HttpMethod.POST, request, TokenDTO.class);
        return response.getBody();
    }

    public void refreshToken(User user) {
        String url = "https://accounts.spotify.com/api/token";
        HttpEntity<MultiValueMap<String, String>> request;
        ResponseEntity<TokenDTO> response = null;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String authStr = "b5ead0205230451d877d487a856a30a9:3e18969a0fc94531b04357edc447461f";
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(authStr.getBytes()));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", user.getToken().getRefreshToken());

        request = new HttpEntity<>(map, headers);
        response = restTemplate
                .exchange(url, HttpMethod.POST, request, TokenDTO.class);

        user.getToken().setAccessToken(response.getBody().getAccess_token());
        this.userRepository.save(user);
    }

    public UserDTO getUserInfo(String accessToken) {
        String url = "https://api.spotify.com/v1/me";
        ResponseEntity<UserDTO> response = null;
        HttpEntity<MultiValueMap<String, String>> request;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        request = new HttpEntity<>(headers);

        response = restTemplate.exchange(url, HttpMethod.GET, request, UserDTO.class);
        return response.getBody();
    }

    public void checkUserAuthorization(User user) {
        try {
            getUserInfo(user.getToken().getAccessToken());
        } catch (HttpClientErrorException exception) {
            logger.info("Refreshing user token");
            refreshToken(user);
        }
    }

    public void addPlaylistsToSpotify(User user, List<Playlist> playlists) {
        startUserProcess(user);

        Map<String, List<TrackDTO>> playlistMap = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity httpEntity = getAuthHttpEntity(user);

        for (Playlist playlist : playlists) {
            playlistMap.put(createPlaylist(playlist.getName(), user),
                    getAllSpotifyTracksFromMediaContentList(playlist.getMediaContents(), restTemplate, httpEntity));
        }

        for (String playlistId : playlistMap.keySet()) {
            populatePlaylist(playlistId, playlistMap.get(playlistId), user);
        }

        endUserProcess(user);
    }

    private void endUserProcess(User user) {
        InProgressMap inProgress = this.inProgressMapRepository.findById(user.getId()).get();
        inProgress.setInProgress(false);
        this.inProgressMapRepository.save(inProgress);
    }

    private void startUserProcess(User user) {
        this.inProgressMapRepository.save(new InProgressMap(user.getId(), true));
    }

    public void populatePlaylist(String playlistId, List<TrackDTO> tracks, User user) {
        String url = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";
        List<String> trackUriList = tracks.stream().map(TrackDTO::getUri).collect(Collectors.toList());

        spotifyBatchRequest(trackUriList, url, user, 100, "uris", HttpMethod.POST);
    }

    public String createPlaylist(String playListName, User user) {
        String createPlaylistUrl = "https://api.spotify.com/v1/users/" + user.getSpotifyUser().getId() + "/playlists";
        HttpHeaders headers = getAuthHttpHeader(user);
        HttpEntity<MultiValueMap<String, String>> httpEntity;
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", playListName);
        httpEntity = new HttpEntity(requestBody, headers);
        return restTemplate.exchange(createPlaylistUrl, HttpMethod.POST, httpEntity, PlaylistDTO.class).getBody().getId();
    }

    private void spotifyBatchRequest(List<String> list,
                                     String requestUrl,
                                     User user,
                                     int batchSize,
                                     String bodyParameterName,
                                     HttpMethod httpMethod) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity;
        HttpHeaders headers = getAuthHttpHeader(user);
        Map<String, List<String>> requestBody = new HashMap<>();

        int length = list.size();
        int batch = length / batchSize;
        List<String> requestBodyList = new ArrayList<>();

        for (int i = 0; i < batch; i++) {
            for (int j = 0; j < batchSize; j++) {
                requestBodyList.add(list.get((i * batchSize) + j));
            }
            requestBody.put(bodyParameterName, requestBodyList);
            httpEntity = new HttpEntity(requestBody, headers);
            restTemplate.exchange(requestUrl, httpMethod, httpEntity, Void.class);
            requestBody.clear();
            requestBodyList.clear();
        }
        requestBodyList.clear();
        for (int i = 0; i < length - (batch * batchSize); i++) {
            requestBodyList.add(list.get((batch * batchSize) + i));
        }
        requestBody.put(bodyParameterName, requestBodyList);
        httpEntity = new HttpEntity(requestBody, headers);
        restTemplate.exchange(requestUrl, httpMethod, httpEntity, Void.class);
        requestBody.clear();
    }

    public void addTracksToSpotify(List<String> spotifyTrackIdList, User user) {
        String url = "https://api.spotify.com/v1/me/tracks";

        spotifyBatchRequest(spotifyTrackIdList, url, user, 50, "ids", HttpMethod.PUT);

        endUserProcess(user);
    }

    @Async
    public void requestSpotifyTrackIds(User user) {
        startUserProcess(user);

        HttpEntity request = getAuthHttpEntity(user);
        RestTemplate restTemplate = new RestTemplate();

        List<TrackDTO> spotifyTrackList = getAllSpotifyTracksFromMediaContentList(user.getMediaContentList(),
                restTemplate,
                request);

        addTracksToSpotify(spotifyTrackList.stream().map(TrackDTO::getId).collect(Collectors.toList()),
                user);
    }

    private List<TrackDTO> getAllSpotifyTracksFromMediaContentList(List<MediaContent> mediaContentList,
                                                                   RestTemplate restTemplate,
                                                                   HttpEntity request) {
        ResponseEntity<String> response;
        TrackDTO spotifyTrack;
        String url;
        List<TrackDTO> spotifyTrackList = new ArrayList<>();
        for (MediaContent mediaContent : mediaContentList) {
            if (mediaContent != null) {
                url = getTrackQuery(mediaContent);
                logger.info("Requested URL : " + url);
                response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (response.getBody().getBytes().length > 1000) {
                    spotifyTrack = getTrackFromResponse(response.getBody());
                    spotifyTrackList.add(spotifyTrack);
                } else {
                    url = getTrackQueryWithoutAlbum(mediaContent);
                    response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                    if (response.getBody().getBytes().length > 1000) {
                        spotifyTrack = getTrackFromResponse(response.getBody());
                        spotifyTrackList.add(spotifyTrack);
                    } else {
                        url = getTrackWithOnlyTrackName(mediaContent);
                        response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                        if (response.getBody().getBytes().length > 1000) {
                            spotifyTrack = getTrackFromResponse(response.getBody());
                            spotifyTrackList.add(spotifyTrack);
                        } else {
                            //unmatchedMediaContentList.add(mediaContent);
                        }
                    }
                }
            }
        }
        return spotifyTrackList;
    }

    private HttpEntity getAuthHttpEntity(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken().getAccessToken());
        return new HttpEntity(headers);
    }

    private HttpHeaders getAuthHttpHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken().getAccessToken());
        return headers;
    }

    private static TrackDTO getTrackFromResponse(String response) {
        return new TrackDTO(JsonPath.read(response, getFirstTrackIdJsonPath),
                JsonPath.read(response, getFirstTrackURIJsonPath));
    }

    private static String getTrackWithOnlyTrackName(MediaContent mediaContent) {
        return baseTrackQueryStr + mediaContent.getTrackName().trim() + "&type=track";
    }

    private static String getTrackQueryWithoutAlbum(MediaContent mediaContent) {
        return baseTrackQueryStr + mediaContent.getArtistName().trim()
                + mediaContent.getTrackName() + "&type=track";
    }

    private static String getTrackQuery(MediaContent mediaContent) {
        return baseTrackQueryStr + mediaContent.getArtistName().trim()
                + mediaContent.getTrackName()
                + mediaContent.getAlbumName()
                + "&type=track";
    }
}
