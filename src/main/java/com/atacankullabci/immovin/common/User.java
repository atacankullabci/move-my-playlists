package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class User {
    @Id
    private String id;
    private String username;
    private String externalUrl;
    private Token token;
    private List<MediaContent> mediaContentList;

    public User() {
    }

    public User(String username, String externalUrl, Token token) {
        this.username = username;
        this.externalUrl = externalUrl;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
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
                ", username='" + username + '\'' +
                ", externalUrl='" + externalUrl + '\'' +
                ", token=" + token +
                ", mediaContentList=" + mediaContentList +
                '}';
    }
}
