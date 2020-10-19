package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.UserDTO;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyService {

    private static final String baseTrackQueryStr = "https://api.spotify.com/v1/search?q=";
    private static final String getFirstTrackIdJsonPath = "$.tracks.items[0].id";

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

    public List<String> requestSpotifyTrackIds(User user) {
        List<String> urlList = getTrackQueryList(user.getMediaContentList());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken().getAccessToken());

        HttpEntity request = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;

        List<String> trackIdList = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        String url = "";

        for (MediaContent mediaContent : user.getMediaContentList()) {
            System.out.println("Denenen URL : " + url);
            url = getTrackQuery(mediaContent);
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            if (response.getBody().getBytes().length > 1000) {
                trackIdList.add(getIdFromResponse(response.getBody()));
            } else {
                url = getTrackQueryWithoutAlbum(mediaContent);
                response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (response.getBody().getBytes().length > 1000) {
                    trackIdList.add(getIdFromResponse(response.getBody()));
                } else {
                    notFound.add(url);
                }
            }
        }

        return trackIdList;
    }

    private static String getIdFromResponse(String response) {
        return JsonPath.read(response, getFirstTrackIdJsonPath);
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

    private static List<String> getTrackQueryList(List<MediaContent> mediaContentList) {
        String query = "";
        List<String> trackQueryList = new ArrayList<>();

        for (MediaContent content : mediaContentList) {
            query += content.getArtistName() + " " + content.getTrackName() + " " + content.getAlbumName();
            trackQueryList.add(baseTrackQueryStr + query + "&type=track");
            query = "";
        }

        return trackQueryList;
    }
}
