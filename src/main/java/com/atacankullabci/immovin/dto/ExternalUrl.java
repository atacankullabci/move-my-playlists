package com.atacankullabci.immovin.dto;

public class ExternalUrl {
    private String spotify;

    public ExternalUrl() {
    }

    public ExternalUrl(String spotify) {
        this.spotify = spotify;
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    @Override
    public String toString() {
        return "ExternalUrl{" +
                "spotify='" + spotify + '\'' +
                '}';
    }
}
