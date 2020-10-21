package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://imovin.club", "http://localhost:4200"})
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<Void> isValidUser(@RequestHeader("username") String username,
                                            @RequestHeader("external-url") String externalUrl,
                                            @RequestHeader("code") String code) {
        return userRepository.findByUsernameAndExternalUrlAndAndCode(username, externalUrl, code) == null ?
                ResponseEntity.notFound().build() : ResponseEntity.ok().build();
    }
}
