package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.dto.TokenDTO;
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

    public void getSpotifyJWTToken() {
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

    public void queryTrackOnSpotify(MediaContent mediaContent) {
    }
}
