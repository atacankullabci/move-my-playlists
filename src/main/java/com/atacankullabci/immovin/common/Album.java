package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

@Document
@CompoundIndex(def = "{'albumName':1, 'albumArtist':-1}", name = "albumName_albumArtist_compound_index", unique = true)
public class Album implements Comparable<Album> {

    @Id
    private String id;
    private String albumName;
    private String albumArtist;
    private String genre;
    private String year;
    private String trackCount;

    public Album() {
    }

    public Album(String albumName, String albumArtist, String genre, String year, String trackCount) {
        this.albumName = albumName;
        this.albumArtist = albumArtist;
        this.genre = genre;
        this.year = year;
        this.trackCount = trackCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(String trackCount) {
        this.trackCount = trackCount;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumName='" + albumName + '\'' +
                ", albumArtist='" + albumArtist + '\'' +
                ", genre='" + genre + '\'' +
                ", year='" + year + '\'' +
                ", trackCount='" + trackCount + '\'' +
                '}';
    }

    @Override
    public int compareTo(Album o) {
        if (!StringUtils.isEmpty(o.getAlbumName())) {
            return this.getAlbumName().compareTo(o.getAlbumName());
        }
        return -1;
    }
}
