package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Token;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.MapperService;
import com.atacankullabci.immovin.service.SpotifyService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("callback")
@CrossOrigin(origins = {"http://imovin.club", "https://imovin.club", "http://localhost:4200"})
public class ResponseController {

    private SpotifyService spotifyService;
    private MapperService mapperService;
    private UserRepository userRepository;

    public ResponseController(SpotifyService spotifyService, MapperService mapperService, UserRepository userRepository) {
        this.spotifyService = spotifyService;
        this.mapperService = mapperService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public void getURICode(@RequestParam(name = "code") String code, HttpServletResponse response) {
        System.out.println(code);
        try {
            Token token = this.mapperService.mapToken(this.spotifyService.getJWTToken(code));
            User user = this.mapperService.mapUser(this.spotifyService.getUserInfo(token.getAccessToken()), token);

            this.userRepository.save(user);

            response.sendRedirect("https://imovin.club/?id=" + user.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
