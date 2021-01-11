package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.*;
import com.atacankullabci.immovin.dto.PlaylistDTO;
import com.atacankullabci.immovin.dto.SpotifyResponseDTO;
import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.UserDTO;
import com.atacankullabci.immovin.repository.InProgressMapRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpotifyService {

    static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    @Value("${spotify.clientId}")
    private String spotifyClientId;

    @Value("${spotify.clientSecret}")
    private String spotifyClientSecret;

    private static final String baseSearchQueryStr = "https://api.spotify.com/v1/search?q=";
    private static final String getFirstTrackIdJsonPath = "$.tracks.items[0].id";
    private static final String getFirstTrackURIJsonPath = "$.tracks.items[0].uri";

    private static final String getFirstAlbumIdJsonPath = "$.albums.items[0].id";
    private static final String getFirstAlbumURIJsonPath = "$.albums.items[0].uri";

    private static final String getAlbumIdJsonPath = "$.albums.items[0].[\'id\', \'uri\']";
    private static final String getAlbumContentJsonPath = "$.items[*].[\'id\', \'uri\', \'duration_ms\', \'name\', \'track_number\']";

    private final InProgressMapRepository inProgressMapRepository;
    private final UserRepository userRepository;

    public SpotifyService(InProgressMapRepository inProgressMapRepository, UserRepository userRepository) {
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
        map.add("redirect_uri", "http://movemyplaylists.com/callback/");
        map.add("client_id", spotifyClientId);
        map.add("client_secret", spotifyClientSecret);
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
        String authStr = spotifyClientId + ":" + spotifyClientSecret;
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

    public void addPlaylistsToSpotify(User user, List<Playlist> playlists) throws Exception {
        startUserProcess(user);

        try {
            Map<String, List<SpotifyResponseDTO>> playlistMap = new HashMap<>();
            String newPlaylistId;
            List<SpotifyResponseDTO> trackList;

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity httpEntity = getAuthHttpEntity(user);


            for (Playlist playlist : playlists) {
                newPlaylistId = createPlaylist(playlist.getName(), user);
                trackList = getAllSpotifyTracksFromMediaContentList(playlist.getMediaContents(),
                        restTemplate,
                        httpEntity);
                playlistMap.put(newPlaylistId, trackList);
            }

            for (String playlistId : playlistMap.keySet()) {
                populatePlaylist(playlistId, playlistMap.get(playlistId), user);
            }
        } catch (Exception ex) {
            throw new Exception();
        } finally {
            endUserProcess(user);
        }
    }

    private void endUserProcess(User user) {
        InProgressMap inProgress = this.inProgressMapRepository.findById(user.getId()).get();
        inProgress.setInProgress(false);
        this.inProgressMapRepository.save(inProgress);
    }

    private void startUserProcess(User user) {
        this.inProgressMapRepository.save(new InProgressMap(user.getId(), true));
    }

    public void populatePlaylist(String playlistId, List<SpotifyResponseDTO> tracks, User user) {
        String url = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";
        List<String> trackUriList = tracks.stream().map(SpotifyResponseDTO::getUri).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(trackUriList)) {
            spotifyBatchRequest(trackUriList, url, user, 100, "uris", HttpMethod.POST);
        }
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
            try {
                restTemplate.exchange(requestUrl, httpMethod, httpEntity, Void.class);
            } catch (Exception e) {
                logger.info("There was a problem in batch request in url : " + requestUrl);
            }
            requestBody.clear();
            requestBodyList.clear();
        }
        requestBodyList.clear();
        for (int i = 0; i < length - (batch * batchSize); i++) {
            requestBodyList.add(list.get((batch * batchSize) + i));
        }
        requestBody.put(bodyParameterName, requestBodyList);
        httpEntity = new HttpEntity(requestBody, headers);
        try {
            restTemplate.exchange(requestUrl, httpMethod, httpEntity, Void.class);
        } catch (Exception e) {
            logger.info("There was a problem in batch request in url : " + requestUrl);
        }
        requestBody.clear();
    }

    public void addTracksToSpotify(List<SpotifyResponseDTO> spotifyTrackIdList, User user) {
        String url = "https://api.spotify.com/v1/me/tracks";

        List<String> trackIdList = spotifyTrackIdList.stream().map(SpotifyResponseDTO::getId).collect(Collectors.toList());

        spotifyBatchRequest(trackIdList, url, user, 50, "ids", HttpMethod.PUT);
    }

    public void addAlbumsToSpotify(List<SpotifyResponseDTO> spotifyAlbumIdList, User user) {
        String url = "https://api.spotify.com/v1/me/albums";

        List<String> albumIdList = spotifyAlbumIdList.stream().map(SpotifyResponseDTO::getId).collect(Collectors.toList());

        spotifyBatchRequest(albumIdList, url, user, 50, "ids", HttpMethod.PUT);
    }

    public void requestSpotifyTrackIds(User user) {
        startUserProcess(user);

        HttpEntity request = getAuthHttpEntity(user);
        RestTemplate restTemplate = new RestTemplate();

        List<SpotifyResponseDTO> spotifyTrackList = getAllSpotifyTracksFromMediaContentList(user.getMediaContentList(),
                restTemplate,
                request);

        addTracksToSpotify(spotifyTrackList, user);

        endUserProcess(user);
    }

    public void requestSpotifyAlbumIds(User user) {
        startUserProcess(user);

        HttpEntity request = getAuthHttpEntity(user);
        RestTemplate restTemplate = new RestTemplate();
        List<SpotifyResponseDTO> spotifyAlbumList = getAllSpotifyAlbumFromMediaContentList(user.getAlbumList(),
                restTemplate,
                request);

        addAlbumsToSpotify(spotifyAlbumList, user);

        endUserProcess(user);
    }

    public void deleteAllTracksFromLikedSongs() {

    }

    public List<String> getUserSavedTracks() {
        // GET https://api.spotify.com/v1/me/tracks // getuser saved tracks

        return new ArrayList<>();
    }

    private List<SpotifyResponseDTO> getAllSpotifyAlbumFromMediaContentList(List<Album> albumList,
                                                                            RestTemplate restTemplate,
                                                                            HttpEntity request) {
        ResponseEntity<String> response;
        SpotifyResponseDTO spotifyAlbum;
        String url;
        Set<SpotifyResponseDTO> spotifyAlbumList = new HashSet<>();
        for (Album album : albumList) {
            if (album != null) {
                url = getAlbumQueryWithArtistName(album);
                logger.info("Requested URL : " + url);
                response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (response.getBody().getBytes().length > 1000) {
                    spotifyAlbum = getAlbumFromResponse(response.getBody());
                    spotifyAlbumList.add(spotifyAlbum);
                }
            }
        }
        return new ArrayList<>(spotifyAlbumList);
    }

    public SpotifyResponseDTO getAlbumContentByAlbumNameAndAlbumArtist(User user, Album album) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity request = getAuthHttpEntity(user);
        String url = getAlbumQueryWithArtistName(album);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        logger.info("Requested URL : " + url);

        return getAlbumIdFromResponse(response.getBody());
    }

    public List<SpotifyAlbum> getSpotifyAlbumContentByAlbumId(User user, SpotifyResponseDTO spotifyAlbum) {
        String url = "https://api.spotify.com/v1/albums/" + spotifyAlbum.getId() + "/tracks";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity request = getAuthHttpEntity(user);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return getAlbumContent(response.getBody());
    }

    private List<SpotifyResponseDTO> getAllSpotifyTracksFromMediaContentList(List<MediaContent> mediaContentList,
                                                                             RestTemplate restTemplate,
                                                                             HttpEntity request) {
        ResponseEntity<String> response;
        SpotifyResponseDTO spotifyTrack;
        String url;
        List<SpotifyResponseDTO> spotifyTrackList = new ArrayList<>();
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
                            logger.info("Unmatched media content : " + mediaContent);
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

    private static List<SpotifyAlbum> getAlbumContent(String response) {
        List<SpotifyAlbum> albumContents = JsonPath.parse(response).read(getAlbumContentJsonPath, List.class);
        return albumContents;
    }

    private static SpotifyResponseDTO getTrackFromResponse(String response) {
        return new SpotifyResponseDTO(JsonPath.read(response, getFirstTrackIdJsonPath),
                JsonPath.read(response, getFirstTrackURIJsonPath));
    }

    private SpotifyResponseDTO getAlbumFromResponse(String response) {
        return new SpotifyResponseDTO(JsonPath.read(response, getFirstAlbumIdJsonPath),
                JsonPath.read(response, getFirstAlbumURIJsonPath));
    }

    private SpotifyResponseDTO getAlbumIdFromResponse(String response) {
        SpotifyResponseDTO spotifyResponseDTO = JsonPath.parse(response).read(getAlbumIdJsonPath, SpotifyResponseDTO.class);
        return spotifyResponseDTO;
    }

    private static String getTrackWithOnlyTrackName(MediaContent mediaContent) {
        return baseSearchQueryStr + "track:" + mediaContent.getTrackName().trim()
                + "&type=track";
    }

    private static String getTrackQueryWithoutAlbum(MediaContent mediaContent) {
        return baseSearchQueryStr + "artist:" + mediaContent.getArtistName().trim()
                + " track:" + mediaContent.getTrackName().trim()
                + "&type=track";
    }

    private static String getTrackQuery(MediaContent mediaContent) {
        return baseSearchQueryStr + "artist:" + mediaContent.getArtistName().trim()
                + " track:" + mediaContent.getTrackName().trim()
                + " album:" + mediaContent.getAlbumName().trim()
                + "&type=track";
    }

    private static String getAlbumQueryWithArtistName(Album album) {
        return baseSearchQueryStr + "album:" + album.getAlbumName().trim()
                + " artist:" + album.getAlbumArtist().trim() + "&type=album";
    }
}
