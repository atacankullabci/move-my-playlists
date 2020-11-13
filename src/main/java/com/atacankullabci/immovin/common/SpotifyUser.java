package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;

public class SpotifyUser {
    @Id
    private String id;
    private String username;
    private String externalUrl;
    private SpotifyUserImage userImage;

    public SpotifyUser() {
    }

    public SpotifyUser(String id, String username, String externalUrl, SpotifyUserImage userImage) {
        this.id = id;
        this.username = username;
        this.externalUrl = externalUrl;
        this.userImage = userImage;
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

    public SpotifyUserImage getUserImage() {
        return userImage;
    }

    public void setUserImage(SpotifyUserImage userImage) {
        this.userImage = userImage;
    }

    @Override
    public String toString() {
        return "SpotifyUser{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", externalUrl='" + externalUrl + '\'' +
                ", userImage=" + userImage +
                '}';
    }
}

