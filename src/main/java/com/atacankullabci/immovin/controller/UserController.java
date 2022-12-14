package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Album;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://movemyplaylists.com", "https://movemyplaylists.com", "http://localhost:4200"})
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity isValidUser(@RequestHeader("id") String id) {
        Optional<User> user = userRepository.findById(id);
        return user.isPresent() ?
                ResponseEntity.ok().body(user.get().getSpotifyUser()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{id}/albums")
    public ResponseEntity<List<Album>> getUserAlbumList(@PathVariable("id") String userId) {
        return ResponseEntity.ok().body(this.userRepository.findById(userId).get().getAlbumList());
    }
}
