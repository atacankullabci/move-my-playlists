package com.atacankullabci.immovin.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mediaContent")
@XmlAccessorType(XmlAccessType.FIELD)
public class MediaContent {
    private String trackName;
    private String artistName;
    private String albumName;

    public MediaContent() {
    }

    public MediaContent(String trackName, String artistName, String albumName) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumName = albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public String toString() {
        return "MediaContent{" +
                "trackName='" + trackName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}
