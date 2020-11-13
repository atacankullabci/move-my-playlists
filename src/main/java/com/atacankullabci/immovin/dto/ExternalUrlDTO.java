package com.atacankullabci.immovin.dto;

public class ExternalUrlDTO {
    private String spotify;

    public ExternalUrlDTO() {
    }

    public ExternalUrlDTO(String spotify) {
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
