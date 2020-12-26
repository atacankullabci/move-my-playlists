package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.StateMap;
import com.atacankullabci.immovin.common.Token;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.StateMapRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.MapperService;
import com.atacankullabci.immovin.service.SpotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("callback")
@CrossOrigin(origins = {"http://movemyplaylists.com", "https://movemyplaylists.com", "http://localhost:4200"})
public class ResponseController {

    static final Logger logger = LoggerFactory.getLogger(ResponseController.class);

    private SpotifyService spotifyService;
    private MapperService mapperService;
    private UserRepository userRepository;
    private StateMapRepository stateMapRepository;


    public ResponseController(SpotifyService spotifyService, MapperService mapperService, UserRepository userRepository, StateMapRepository stateMapRepository) {
        this.spotifyService = spotifyService;
        this.mapperService = mapperService;
        this.userRepository = userRepository;
        this.stateMapRepository = stateMapRepository;
    }

    @GetMapping
    public void getURICode(@RequestParam(name = "code", required = false) String code,
                           @RequestParam(name = "error", required = false) String error,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) throws Exception {

        Optional<StateMap> optStateMap = this.stateMapRepository.findById(state);
        StateMap stateMap = optStateMap.orElseThrow(Exception::new);

        if (error != null) {
            logger.info("User did not accept the request");
            if (error != null) {
                try {
                    stateMap.setValidity(false);
                    this.stateMapRepository.save(stateMap);
                    response.sendRedirect("https://movemyplaylists.com/?error=access-denied");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        if (code != null) {
            logger.info("User code has been received");
            try {
                Token token = this.mapperService.mapToken(this.spotifyService.getJWTToken(code));
                User user = this.mapperService.mapUser(this.spotifyService.getUserInfo(token.getAccessToken()), token);
                user.setRegisterDate(Instant.now());

                this.userRepository.save(user);

                stateMap.setValidity(false);
                this.stateMapRepository.save(stateMap);
                response.sendRedirect("https://movemyplaylists.com/?id=" + user.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
