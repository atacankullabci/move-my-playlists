package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.User;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientService {

    private final CacheService cacheService;

    public ClientService(CacheService cacheService) {
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

    public void getSpotifyJWTTokenFromCache() {
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

        for (String code : this.cacheService.getAllInactivatedCaches()) {
            map.add("code", code);

            request = new HttpEntity<>(map, headers);

            try {
                response = restTemplate
                        .exchange(url, HttpMethod.POST, request, TokenDTO.class);
                this.cacheService.updateCode(code);
            } catch (HttpClientErrorException e) {
                System.err.println(e);
                continue;
            }
        }
        System.out.println(response);
    }

    public User getUserInfo(TokenDTO tokenDTO) {
        String url = "https://api.spotify.com/v1/me";
        ResponseEntity<User> response = null;
        HttpEntity<MultiValueMap<String, String>> request;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenDTO.getAccess_token());

        request = new HttpEntity<>(headers);

        response = restTemplate.exchange(url, HttpMethod.GET, request, User.class);
        return response.getBody();
    }

    public void queryTrackOnSpotify(MediaContent mediaContent) {
    }
}
