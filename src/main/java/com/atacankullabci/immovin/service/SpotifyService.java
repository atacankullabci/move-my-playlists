package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {
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
}
