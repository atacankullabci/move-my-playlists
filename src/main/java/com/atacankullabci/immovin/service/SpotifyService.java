package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.UserDTO;
import com.atacankullabci.immovin.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
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

    private static final String baseTrackQueryStr = "https://api.spotify.com/v1/search?q=";
    private static final String getFirstTrackIdJsonPath = "$.tracks.items[0].id";

    private final UserRepository userRepository;
    private final CacheService cacheService;

    public SpotifyService(UserRepository userRepository, CacheService cacheService) {
        this.userRepository = userRepository;
        this.cacheService = cacheService;
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

    public boolean addTracksToSpotify(List<String> spotifyTrackIdList, String accessToken) {
        String url = "https://api.spotify.com/v1/me/tracks";

        List<String> idList = new ArrayList<>();

        Map<String, List<String>> map = new HashMap<>();
        HttpEntity<MultiValueMap<String, String>> request;

        ResponseEntity<Void> response = null; // Spotify returns no body
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

        return true;
    }

    @Async
    public List<MediaContent> requestSpotifyTrackIds(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken().getAccessToken());

        HttpEntity request = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        List<String> trackIdList = new ArrayList<>();
        List<MediaContent> unmatchedMediaContentList = new ArrayList<>();

        String url = "";
        String spotifyId;

        for (MediaContent mediaContent : user.getMediaContentList()) {
            //Check for cache
            String trackSpotifyId = cacheService.get(mediaContent.getTrackId());
            if (trackSpotifyId == null) {
                url = getTrackQuery(mediaContent);
                System.out.println("Denenen URL : " + url);
                response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (response.getBody().getBytes().length > 1000) {
                    spotifyId = getIdFromResponse(response.getBody());
                    trackIdList.add(spotifyId);
                    cacheService.put(mediaContent.getTrackId(), spotifyId);
                } else {
                    url = getTrackQueryWithoutAlbum(mediaContent);
                    response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                    if (response.getBody().getBytes().length > 1000) {
                        spotifyId = getIdFromResponse(response.getBody());
                        trackIdList.add(spotifyId);
                        cacheService.put(mediaContent.getTrackId(), spotifyId);
                    } else {
                        url = getTrackWithOnlyTrackName(mediaContent);
                        response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                        if (response.getBody().getBytes().length > 1000) {
                            spotifyId = getIdFromResponse(response.getBody());
                            trackIdList.add(spotifyId);
                            cacheService.put(mediaContent.getTrackId(), spotifyId);
                        } else {
                            unmatchedMediaContentList.add(mediaContent);
                        }
                    }
                }
            } else {
                trackIdList.add(trackSpotifyId);
            }
        }
        addTracksToSpotify(trackIdList, user.getToken().getAccessToken());

        return unmatchedMediaContentList;
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
