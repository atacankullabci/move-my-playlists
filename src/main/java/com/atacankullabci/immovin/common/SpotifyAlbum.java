package com.atacankullabci.immovin.common;

public class SpotifyAlbum {
    private String track_number;
    private String name;
    private String duration_ms;

    public SpotifyAlbum() {
    }

    public SpotifyAlbum(String track_number, String name, String duration_ms) {
        this.track_number = track_number;
        this.name = name;
        this.duration_ms = duration_ms;
    }

    public String getTrack_number() {
        return track_number;
    }

    public void setTrack_number(String track_number) {
        this.track_number = track_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(String duration_ms) {
        this.duration_ms = duration_ms;
    }

    @Override
    public String toString() {
        return "SpotifyAlbum{" +
                "track_number='" + track_number + '\'' +
                ", name='" + name + '\'' +
                ", duration_ms='" + duration_ms + '\'' +
                '}';
    }
}
