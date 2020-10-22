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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://imovin.club", "http://localhost:4200"})
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
                                                     @RequestHeader("username") String username,
                                                     @RequestHeader("external-url") String externalUrl) {
        List<MediaContent> mediaContentList = null;
        try {
            mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());
            mediaContentList = LibraryTransformer.tameMediaContent(mediaContentList);
            User user = this.userRepository.findBySpotifyUser_UsernameAndSpotifyUser_ExternalUrl(username, externalUrl);
            user.setMediaContentList(mediaContentList);
            this.userRepository.save(user);
            this.clientRepository.save(new Client(ip, Instant.now()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(mediaContentList);
    }

    @GetMapping("/migrate")
    public ResponseEntity<Void> migrate(@RequestHeader("username") String username,
                                        @RequestHeader("external-url") String externalUrl) {
        User user = this.userRepository.findBySpotifyUser_UsernameAndSpotifyUser_ExternalUrl(username, externalUrl);
        this.spotifyService.requestSpotifyTrackIds(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Client>> getAllList() {
        return ResponseEntity.ok().body(this.clientRepository.findAll());
    }
}
