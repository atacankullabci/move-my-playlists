package com.atacankullabci.immovin.common;

import java.util.List;

public class Playlist {
    private String name;
    private List<MediaContent> mediaContents;

    public Playlist() {
    }

    public Playlist(String name, List<MediaContent> mediaContents) {
        this.name = name;
        this.mediaContents = mediaContents;
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
                "name='" + name + '\'' +
                ", mediaContents=" + mediaContents +
                '}';
    }
}
