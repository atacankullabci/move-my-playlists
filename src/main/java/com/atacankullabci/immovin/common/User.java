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
                ", token=" + token +
                '}';
    }
}
