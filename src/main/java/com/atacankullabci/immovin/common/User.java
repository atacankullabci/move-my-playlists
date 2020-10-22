package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class User {
    @Id
    private String id;
    private SpotifyUser spotifyUser;
    private String code;
    private Token token;
    private List<MediaContent> mediaContentList;

    public User() {
    }

    public User(SpotifyUser spotifyUser, String code, Token token) {
        this.spotifyUser = spotifyUser;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", spotifyUser=" + spotifyUser +
                ", code='" + code + '\'' +
                ", token=" + token +
                ", mediaContentList=" + mediaContentList +
                '}';
    }
}
