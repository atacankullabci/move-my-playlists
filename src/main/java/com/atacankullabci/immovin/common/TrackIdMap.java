package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "TrackIdMap")
public class TrackIdMap implements Serializable {
    @Id
    private String iTunesTrackId;
    private String spotifyTrackId;

    public TrackIdMap() {
    }

    public TrackIdMap(String iTunesTrackId, String spotifyTrackId) {
        this.iTunesTrackId = iTunesTrackId;
        this.spotifyTrackId = spotifyTrackId;
    }

    public String getiTunesTrackId() {
        return iTunesTrackId;
    }

    public void setiTunesTrackId(String iTunesTrackId) {
        this.iTunesTrackId = iTunesTrackId;
    }

    public String getSpotifyTrackId() {
        return spotifyTrackId;
    }

    public void setSpotifyTrackId(String spotifyTrackId) {
        this.spotifyTrackId = spotifyTrackId;
    }

    @Override
    public String toString() {
        return "TrackIdMap{" +
                "iTunesTrackId='" + iTunesTrackId + '\'' +
                ", spotifyTrackId='" + spotifyTrackId + '\'' +
                '}';
    }
}
