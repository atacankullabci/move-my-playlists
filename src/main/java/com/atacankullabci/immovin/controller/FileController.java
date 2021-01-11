package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Album;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.AlbumRepository;
import com.atacankullabci.immovin.repository.MediaContentRepository;
import com.atacankullabci.immovin.repository.PlaylistRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.FileService;
import com.atacankullabci.immovin.service.FileValidationService;
import com.atacankullabci.immovin.service.LibraryTransformer;
import com.atacankullabci.immovin.service.ObjectHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://movemyplaylists.com", "https://movemyplaylists.com", "http://localhost:4200"})
public class FileController {

    private final ObjectHandler objectHandler;
    private final UserRepository userRepository;
    private final FileValidationService fileValidationService;
    private final FileService fileService;
    private final PlaylistRepository playlistRepository;
    private final MediaContentRepository mediaContentRepository;
    private final AlbumRepository albumRepository;

    public FileController(ObjectHandler objectHandler, UserRepository userRepository, FileValidationService fileValidationService, FileService fileService, PlaylistRepository playlistRepository, MediaContentRepository mediaContentRepository, AlbumRepository albumRepository) {
        this.objectHandler = objectHandler;
        this.userRepository = userRepository;
        this.fileValidationService = fileValidationService;
        this.fileService = fileService;
        this.playlistRepository = playlistRepository;
        this.mediaContentRepository = mediaContentRepository;
        this.albumRepository = albumRepository;
    }


    @PostMapping(value = "/map", consumes = "multipart/form-data")
    public ResponseEntity<?> mapper(@RequestParam("file") MultipartFile libraryFile,
                                    @RequestHeader("id") String id,
                                    @RequestHeader("parse-playlist") boolean option) throws Exception {
        this.fileValidationService.validateLibraryFile(libraryFile);

        List<MediaContent> mediaContentList = null;
        List<Album> albumList = null;
        List<Playlist> playlists = null;
        Optional<User> optionalUser = this.userRepository.findById(id);
        User user = optionalUser.orElse(null);

        // Tracks
        mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());

        // TODO: Try async operation for this method if possible
        mediaContentList = LibraryTransformer.tameMediaContent(mediaContentList);

        // If user is willing to combine previous transfers
        // mediaContentList = this.fileService.combineNewVersionMediaContentList(this.mediaContentRepository.findAll(), mediaContentList);

        this.mediaContentRepository.saveAll(mediaContentList);
        user.setMediaContentList(mediaContentList);

        // Album
        albumList = this.objectHandler.getAlbumList(libraryFile.getBytes());
        Collections.sort(albumList);
        this.albumRepository.saveAll(albumList);
        user.setAlbumList(albumList);

        // Playlist
        if (option) {
            playlists = objectHandler.getUserPlaylists(libraryFile.getBytes(), user);
            this.playlistRepository.saveAll(playlists);
            user.setPlaylists(playlists);
        }

        this.userRepository.save(user);

        return ResponseEntity.ok().body(Arrays.asList(mediaContentList, playlists));
    }
}
