package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.UserDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyService {

    private final String baseTrackQueryStr = "https://api.spotify.com/v1/search?q=";

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

    public List<String> getTrackQueryList(List<MediaContent> mediaContentList) {
        String query = "";
        List<String> trackQueryList = new ArrayList<>();

        for (MediaContent content : mediaContentList) {
            query += "track:" + content.getTrackName() + " artist:" + content.getArtistName();
            trackQueryList.add(baseTrackQueryStr + urlEncode(query) + "&type=track");
            query = "";
        }

        return trackQueryList;
    }

    private static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }


}
