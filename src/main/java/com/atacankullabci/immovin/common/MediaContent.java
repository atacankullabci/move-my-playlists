package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MediaContent {
    @Id
    private String id;
    @Transient
    private String trackId;
    private String trackName;
    private String artistName;
    private String albumName;
    private String albumArtist;
    private String genre;

    public MediaContent() {
    }

    public MediaContent(String trackId, String trackName, String artistName, String albumName, String albumArtist, String genre) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.albumArtist = albumArtist;
        this.genre = genre;
        this.id = String.valueOf(this.hashCode());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
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

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MediaContent)) {
            return false;
        }

        MediaContent mediaContent = (MediaContent) o;

        return mediaContent.trackName.equals(trackName) &&
                mediaContent.albumName.equals(albumName) &&
                mediaContent.albumArtist.equals(albumArtist);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + trackName.hashCode();
        result = 31 * result + albumName.hashCode();
        result = 31 * result + albumArtist.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MediaContent{" +
                "id='" + id + '\'' +
                ", trackId='" + trackId + '\'' +
                ", trackName='" + trackName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", albumName='" + albumName + '\'' +
                ", albumArtist='" + albumArtist + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }
}
