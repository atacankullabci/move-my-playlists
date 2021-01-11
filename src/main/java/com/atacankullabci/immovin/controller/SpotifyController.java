package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Album;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.SpotifyAlbum;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.dto.SpotifyResponseDTO;
import com.atacankullabci.immovin.repository.AlbumRepository;
import com.atacankullabci.immovin.repository.UserRepository;
import com.atacankullabci.immovin.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://movemyplaylists.com", "https://movemyplaylists.com", "http://localhost:4200"})
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    public SpotifyController(SpotifyService spotifyService, UserRepository userRepository, AlbumRepository albumRepository) {
        this.spotifyService = spotifyService;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }

    @GetMapping("/search/albums")
    public ResponseEntity<List<SpotifyAlbum>> searchAlbum(@RequestHeader("id") String userId,
                                                          @RequestParam String albumId) {
        Optional<User> user = this.userRepository.findById(userId);
        Optional<Album> album = this.albumRepository.findById(albumId);

        List<SpotifyAlbum> albumContentList = new ArrayList<>();

        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            SpotifyResponseDTO spotifyAlbum = this.spotifyService.getAlbumContentByAlbumNameAndAlbumArtist(user.get(), album.orElse(null));
            albumContentList = this.spotifyService.getSpotifyAlbumContentByAlbumId(user.get(), spotifyAlbum);
        }
        return ResponseEntity.ok().body(albumContentList);
    }

    @PostMapping("/migrate/tracks")
    public ResponseEntity<Boolean> migrateTracks(@RequestHeader("id") String userId) {
        Optional<User> user = this.userRepository.findById(userId);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.requestSpotifyTrackIds(user.get());
        }
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/migrate/albums")
    public ResponseEntity<Boolean> migrateAlbums(@RequestHeader("id") String userId) {
        Optional<User> user = this.userRepository.findById(userId);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.requestSpotifyAlbumIds(user.get());
        }
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/migrate/playlists")
    public ResponseEntity<List<Playlist>> migratePlaylist(@RequestHeader("id") String userId,
                                                          @RequestBody List<Playlist> playlists) throws Exception {
        Optional<User> user = this.userRepository.findById(userId);
        if (user.isPresent()) {
            spotifyService.checkUserAuthorization(user.get());
            this.spotifyService.addPlaylistsToSpotify(user.get(), playlists);
        }
        return ResponseEntity.ok(playlists);
    }
}
