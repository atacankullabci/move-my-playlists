package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class User {
    @Id
    private String id;
    private SpotifyUser spotifyUser;
    private Token token;
    private List<MediaContent> mediaContentList;
    private List<Playlist> playlists;

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

    public SpotifyUser getSpotifyUser() {
        return spotifyUser;
    }

    public void setSpotifyUser(SpotifyUser spotifyUser) {
        this.spotifyUser = spotifyUser;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public List<MediaContent> getMediaContentList() {
        return mediaContentList;
    }

    public void setMediaContentList(List<MediaContent> mediaContentList) {
        this.mediaContentList = mediaContentList;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", spotifyUser=" + spotifyUser +
                ", token=" + token +
                ", mediaContentList=" + mediaContentList +
                ", playlists=" + playlists +
                '}';
    }
}
