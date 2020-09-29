package com.atacankullabci.immovin.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mediaContent")
@XmlAccessorType(XmlAccessType.FIELD)
public class MediaContent {
    private Album album;
    private Artist artist;
    private Track track;

    public MediaContent() {
    }

    public MediaContent(Album album, Artist artist, Track track) {
        this.album = album;
        this.artist = artist;
        this.track = track;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    @Override
    public String toString() {
        return "MediaContent{" +
                "album=" + album +
                ", artist=" + artist +
                ", track=" + track +
                '}';
    }
}
