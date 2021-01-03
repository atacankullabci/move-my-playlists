package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
public class User {

    @Id
    private String id;
    private Instant registerDate;
    private SpotifyUser spotifyUser;
    private List<MediaContent> mediaContentList;
    private List<Album> albumList;
    private List<Playlist> playlists;
    private Token token;

    public User() {
    }

    public User(SpotifyUser spotifyUser, Token token) {
        this.spotifyUser = spotifyUser;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Instant registerDate) {
        this.registerDate = registerDate;
    }

    public SpotifyUser getSpotifyUser() {
        return spotifyUser;
    }

    public void setSpotifyUser(SpotifyUser spotifyUser) {
        this.spotifyUser = spotifyUser;
    }

    public List<MediaContent> getMediaContentList() {
        return mediaContentList;
    }

    public void setMediaContentList(List<MediaContent> mediaContentList) {
        this.mediaContentList = mediaContentList;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", registerDate=" + registerDate +
                ", spotifyUser=" + spotifyUser +
                ", mediaContentList=" + mediaContentList +
                ", albumList=" + albumList +
                ", playlists=" + playlists +
                ", token=" + token +
                '}';
    }
}
