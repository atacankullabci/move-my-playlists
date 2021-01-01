package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.MediaContentRepository;
import com.atacankullabci.immovin.repository.PlaylistRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.FileValidationService;
import com.atacankullabci.immovin.service.LibraryTransformer;
import com.atacankullabci.immovin.service.ObjectHandler;
import com.atacankullabci.immovin.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://movemyplaylists.com", "https://movemyplaylists.com", "http://localhost:4200"})
public class FileController {

    private final ObjectHandler objectHandler;
    private SpotifyService spotifyService;
    private UserRepository userRepository;
    private FileValidationService fileValidationService;
    private MediaContentRepository mediaContentRepository;
    private PlaylistRepository playlistRepository;

    public FileController(ObjectHandler objectHandler, UserRepository userRepository, SpotifyService spotifyService, FileValidationService fileValidationService, MediaContentRepository mediaContentRepository, PlaylistRepository playlistRepository) {
        this.objectHandler = objectHandler;
        this.userRepository = userRepository;
        this.spotifyService = spotifyService;
        this.fileValidationService = fileValidationService;
        this.mediaContentRepository = mediaContentRepository;
        this.playlistRepository = playlistRepository;
    }

    @PostMapping(value = "/map", consumes = "multipart/form-data")
    public ResponseEntity<?> mapper(@RequestParam("file") MultipartFile libraryFile,
                                    @RequestHeader("id") String id,
                                    @RequestHeader("parse-playlist") boolean option) throws Exception {
        this.fileValidationService.validateLibraryFile(libraryFile);

        List<MediaContent> mediaContentList = null;
        List<Playlist> playlists = null;
        Optional<User> optionalUser = this.userRepository.findById(id);

        try {
            mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());

            // TODO: Try async operation for this method if possible
            mediaContentList = LibraryTransformer.tameMediaContent(mediaContentList);

            this.mediaContentRepository.saveAll(mediaContentList);

            User user = optionalUser.orElse(null);
            user.setMediaContentList(mediaContentList);
            this.userRepository.save(user);

            if (option) {
                playlists = objectHandler.getUserPlaylists(libraryFile.getBytes(), user.getId());
                this.playlistRepository.saveAll(playlists);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(Arrays.asList(mediaContentList, playlists));
    }

    @PostMapping("/migrate/tracks")
    public ResponseEntity<Boolean> migrateTracks(@RequestHeader("id") String id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.requestSpotifyTrackIds(user.get());
        }
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/migrate/albums")
    public ResponseEntity<Boolean> migrateAlbums(@RequestHeader("id") String id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.requestSpotifyAlbumIds(user.get());
        }
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/migrate/playlists")
    public ResponseEntity<List<Playlist>> migratePlaylist(@RequestHeader("id") String id,
                                                          @RequestBody List<Playlist> playlists) throws Exception {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.addPlaylistsToSpotify(user.get(), playlists);
        }
        return ResponseEntity.ok(playlists);
    }
}
