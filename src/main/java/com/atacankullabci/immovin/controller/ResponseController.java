package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Token;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.CacheService;
import com.atacankullabci.immovin.service.MapperService;
import com.atacankullabci.immovin.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("callback")
@CrossOrigin(origins = {"http://imovin.club", "http://localhost:4200"})
public class ResponseController {

    private final CacheService cacheService;
    private SpotifyService spotifyService;
    private MapperService mapperService;
    private UserRepository userRepository;

    public ResponseController(CacheService cacheService, SpotifyService spotifyService, MapperService mapperService, UserRepository userRepository) {
        this.cacheService = cacheService;
        this.spotifyService = spotifyService;
        this.mapperService = mapperService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public void getURICode(@RequestParam(name = "code") String code, HttpServletResponse response) {
        this.cacheService.put(code);
        System.out.println(code);
        try {
            Token token = this.mapperService.mapToken(this.spotifyService.getJWTToken(code));
            User user = this.mapperService.mapUser(this.spotifyService.getUserInfo(token.getAccessToken()), token);
            user.setCode(code);

            this.userRepository.save(user);

            response.sendRedirect("http://imovin.club/?username=" + user.getUsername() +
                    "&externalUrl=" + user.getExternalUrl() +
                    "&code=" + user.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/caches")
    public ResponseEntity<List<String>> getAllInactiveCaches() {
        return ResponseEntity.ok().body(this.cacheService.getAllInactivatedCaches());
    }

    @GetMapping("/all/caches")
    public ResponseEntity<Map<String, Boolean>> getAllCaches() {
        return ResponseEntity.ok().body(this.cacheService.getCodeMap());
    }
}
