package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Playlist {
    @Id
    private String id;
    private String name;
    private List<MediaContent> mediaContents;

    public Playlist() {
    }

    public Playlist(String name, List<MediaContent> mediaContents) {
        this.name = name;
        this.mediaContents = mediaContents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MediaContent> getMediaContents() {
        return mediaContents;
    }

    public void setMediaContents(List<MediaContent> mediaContents) {
        this.mediaContents = mediaContents;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mediaContents=" + mediaContents +
                '}';
    }
}
