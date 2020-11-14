package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Client;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.ClientRepository;
import com.atacankullabci.immovin.repository.MediaContentRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.FileValidationService;
import com.atacankullabci.immovin.service.LibraryTransformer;
import com.atacankullabci.immovin.service.ObjectHandler;
import com.atacankullabci.immovin.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://imovin.club", "https://imovin.club", "http://localhost:4200"})
public class FileController {

    private final ObjectHandler objectHandler;
    private ClientRepository clientRepository;
    private SpotifyService spotifyService;
    private UserRepository userRepository;
    private FileValidationService fileValidationService;
    private MediaContentRepository mediaContentRepository;

    public FileController(ObjectHandler objectHandler, ClientRepository clientRepository, UserRepository userRepository, SpotifyService spotifyService, FileValidationService fileValidationService, MediaContentRepository mediaContentRepository) {
        this.objectHandler = objectHandler;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.spotifyService = spotifyService;
        this.fileValidationService = fileValidationService;
        this.mediaContentRepository = mediaContentRepository;
    }

    @PostMapping(value = "/map", consumes = "multipart/form-data")
    public ResponseEntity<?> mapper(@RequestParam("file") MultipartFile libraryFile,
                                    @RequestHeader("client-ip") String ip,
                                    @RequestHeader("id") String id,
                                    @RequestHeader("parse-playlist") boolean option) throws Exception {
        this.fileValidationService.validateLibraryFile(libraryFile);

        List<MediaContent> mediaContentList = null;
        List<Playlist> playlists = null;
        try {
            mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());
            // Make async operation for this method if possible
            mediaContentList = LibraryTransformer.tameMediaContent(mediaContentList);

            this.mediaContentRepository.saveAll(mediaContentList);

            if (option) {
                playlists = objectHandler.getUserPlaylists(libraryFile.getBytes());
            }

            Optional<User> user = this.userRepository.findById(id);
            if (user.isPresent()) {
                user.get().setMediaContentList(mediaContentList);
                user.get().setPlaylists(playlists);
                this.userRepository.save(user.get());
            }
            this.clientRepository.save(new Client(ip, Instant.now()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(Arrays.asList(mediaContentList, playlists));
    }

    @PostMapping("/migrate/tracks")
    public ResponseEntity<Boolean> migrate(@RequestHeader("id") String id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.requestSpotifyTrackIds(user.get());
        }
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/migrate/playlists")
    public ResponseEntity<List<Playlist>> migratePlaylist(@RequestHeader("id") String id,
                                                          @RequestBody List<Playlist> playlists) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.addPlaylistsToSpotify(user.get(), playlists);
        }
        return ResponseEntity.ok(playlists);
    }
}
