package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.dto.TokenDTO;
import com.atacankullabci.immovin.dto.User;
import com.atacankullabci.immovin.service.CacheService;
import com.atacankullabci.immovin.service.ClientService;
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
    private ClientService clientService;
    private SpotifyService spotifyService;

    public ResponseController(CacheService cacheService, ClientService clientService, SpotifyService spotifyService) {
        this.cacheService = cacheService;
        this.clientService = clientService;
        this.spotifyService = spotifyService;
    }

    @GetMapping
    public void getURICode(@RequestParam(name = "code") String code, HttpServletResponse response) {
        this.cacheService.put(code);
        System.out.println(code);
        try {
            TokenDTO tokenDTO = this.clientService.getJWTToken(code);
            User user = this.spotifyService.getUserInfo(tokenDTO);

            //response.sendRedirect("http://localhost:4200/?code=" + code +
            //    "&username=" + user.getDisplay_name() + "&externalUrl=" + user.getExternal_urls().getSpotify());
            response.sendRedirect("http://imovin.club/?code=" + code +
                    "&username=" + user.getDisplay_name() + "&externalUrl=" + user.getExternal_urls().getSpotify());
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
