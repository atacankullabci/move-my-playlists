package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.InProgressMap;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.dto.PlaylistDTO;
import com.atacankullabci.immovin.dto.TokenDTO;
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
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpotifyService {

    private static final long serialVersionUID = 1L;
    static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    private static final String baseTrackQueryStr = "https://api.spotify.com/v1/search?q=";
    private static final String getFirstTrackIdJsonPath = "$.tracks.items[0].id";

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

    public void addPlaylistsToSpotify(User user, List<String> playlistNames) {
        List<Playlist> playlists = new ArrayList<>();
        for (Playlist playlist : user.getPlaylists()) {
            for (String name : playlistNames) {
                if (playlist.getName().equals(name)) {
                    playlists.add(playlist);
                }
            }
        }

        List<PlaylistDTO> spotifyPlaylists = createPlaylists(playlistNames, user);

        /*Map<String, List<String>> spotifyPlaylistMap = new HashMap<>();
        HttpEntity request = getAuthHttpEntity(user);
        RestTemplate restTemplate = new RestTemplate();

        for (Playlist playlist : playlists) {
            spotifyPlaylistMap.put(playlist.getName(),
                    getAllTrackIdsFromMediaContentList(playlist.getMediaContents(), restTemplate, request));
        }*/
    }

    public List<PlaylistDTO> createPlaylists(List<String> playlistNames, User user) {
        String createPlaylistUrl = "https://api.spotify.com/v1/users/" + user.getSpotifyUser().getUsername() + "/playlists";
        HttpHeaders headers = getAuthHttpHeader(user);
        HttpEntity<MultiValueMap<String, String>> httpEntity;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PlaylistDTO> response = null;

        Map<String, String> requestBody = new HashMap<>();

        List<PlaylistDTO> responsePlaylistDTO = new ArrayList<>();

        for (String playListName : playlistNames) {
            requestBody.put("name", playListName);
            httpEntity = new HttpEntity(requestBody, headers);
            response = restTemplate.exchange(createPlaylistUrl, HttpMethod.POST, httpEntity, PlaylistDTO.class);
            responsePlaylistDTO.add(response.getBody());
            requestBody.clear();
        }

        return responsePlaylistDTO;
    }

    public void addTracksToSpotify(List<String> spotifyTrackIdList, String accessToken) {
        String url = "https://api.spotify.com/v1/me/tracks";

        List<String> idList = new ArrayList<>();

        Map<String, List<String>> map = new HashMap<>();
        HttpEntity<MultiValueMap<String, String>> request;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RestTemplate restTemplate = new RestTemplate();

        int length = spotifyTrackIdList.size();
        int batch = length / 50;
        for (int i = 0; i < batch; i++) {
            for (int j = 0; j < 50; j++) {
                idList.add(spotifyTrackIdList.get((i * 50) + j));
            }
            map.put("ids", idList);
            request = new HttpEntity(map, headers);
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            map.clear();
            idList.clear();
        }
        idList.clear();
        for (int i = 0; i < length - (batch * 50); i++) {
            idList.add(spotifyTrackIdList.get((batch * 50) + i));
            map.put("ids", idList);
            request = new HttpEntity(map, headers);
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            map.clear();
        }

        User user = this.userRepository.findByToken_AccessToken(accessToken);
        InProgressMap inProgress = this.inProgressMapRepository.findById(user.getId()).get();
        inProgress.setInProgress(false);
        this.inProgressMapRepository.save(inProgress);
    }

    @Async
    public void requestSpotifyTrackIds(User user) {
        this.inProgressMapRepository.save(new InProgressMap(user.getId(), true));

        HttpEntity request = getAuthHttpEntity(user);
        RestTemplate restTemplate = new RestTemplate();

        List<String> trackIdList = getAllTrackIdsFromMediaContentList(user.getMediaContentList(), restTemplate, request);

        addTracksToSpotify(trackIdList, user.getToken().getAccessToken());
    }

    private List<String> getAllTrackIdsFromMediaContentList(List<MediaContent> mediaContentList,
                                                            RestTemplate restTemplate,
                                                            HttpEntity request) {
        ResponseEntity<String> response;
        String url, spotifyId;
        List<String> trackIdList = new ArrayList<>();
        for (MediaContent mediaContent : mediaContentList) {
            url = getTrackQuery(mediaContent);
            logger.info("Requested URL : " + url);
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            if (response.getBody().getBytes().length > 1000) {
                spotifyId = getIdFromResponse(response.getBody());
                saveSpotifyTrackId(trackIdList, spotifyId, mediaContent);
            } else {
                url = getTrackQueryWithoutAlbum(mediaContent);
                response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (response.getBody().getBytes().length > 1000) {
                    spotifyId = getIdFromResponse(response.getBody());
                    saveSpotifyTrackId(trackIdList, spotifyId, mediaContent);
                } else {
                    url = getTrackWithOnlyTrackName(mediaContent);
                    response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                    if (response.getBody().getBytes().length > 1000) {
                        spotifyId = getIdFromResponse(response.getBody());
                        saveSpotifyTrackId(trackIdList, spotifyId, mediaContent);
                    } else {
                        //unmatchedMediaContentList.add(mediaContent);
                    }
                }
            }
        }
        return trackIdList;
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

    private void saveSpotifyTrackId(List<String> trackIdList, String spotifyId, MediaContent mediaContent) {
        trackIdList.add(spotifyId);
        cacheService.put(mediaContent.getTrackId(), spotifyId);
    }

    private static String getIdFromResponse(String response) {
        return JsonPath.read(response, getFirstTrackIdJsonPath);
    }

    private static String getTrackWithOnlyTrackName(MediaContent mediaContent) {
        return baseTrackQueryStr + mediaContent.getTrackName() + "&type=track";
    }

    private static String getTrackQueryWithoutAlbum(MediaContent mediaContent) {
        return baseTrackQueryStr + mediaContent.getArtistName() + " "
                + mediaContent.getTrackName() + "&type=track";
    }

    private static String getTrackQuery(MediaContent mediaContent) {
        return baseTrackQueryStr + mediaContent.getArtistName() + " "
                + mediaContent.getTrackName() + " "
                + mediaContent.getAlbumName() + "&type=track";
    }
}
