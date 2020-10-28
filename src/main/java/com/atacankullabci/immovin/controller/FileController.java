package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Client;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.ClientRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.LibraryTransformer;
import com.atacankullabci.immovin.service.ObjectHandler;
import com.atacankullabci.immovin.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://imovin.club", "https://imovin.club", "http://localhost:4200"})
public class FileController {

    private final ObjectHandler objectHandler;
    private ClientRepository clientRepository;
    private UserRepository userRepository;
    private SpotifyService spotifyService;

    public FileController(ObjectHandler objectHandler, ClientRepository clientRepository, UserRepository userRepository, SpotifyService spotifyService) {
        this.objectHandler = objectHandler;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.spotifyService = spotifyService;
    }

    @PostMapping(value = "/map", consumes = "multipart/form-data")
    public ResponseEntity<List<MediaContent>> mapper(@RequestParam("file") MultipartFile libraryFile,
                                                     @RequestHeader("client-ip") String ip,
                                                     @RequestHeader("id") String id) {
        List<MediaContent> mediaContentList = null;
        try {
            mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());
            // Make async operation for this method
            mediaContentList = LibraryTransformer.tameMediaContent(mediaContentList);
            Optional<User> user = this.userRepository.findById(id);
            if (user.isPresent()) {
                user.get().setMediaContentList(mediaContentList);
                this.userRepository.save(user.get());
            }
            this.clientRepository.save(new Client(ip, Instant.now()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(mediaContentList);
    }

    @PostMapping("/migrate")
    public ResponseEntity<Boolean> migrate(@RequestHeader("id") String id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            this.spotifyService.requestSpotifyTrackIds(user.get());
        }
        return ResponseEntity.ok().body(true);
    }
}
