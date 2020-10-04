package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.Album;
import com.atacankullabci.immovin.common.Artist;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Track;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectHandler {

    private final LibraryParser libraryParser;

    public ObjectHandler(LibraryParser libraryParser) {
        this.libraryParser = libraryParser;
    }

    public List<MediaContent> getMediaContentList() {
        NodeList albumList = libraryParser.getNodeList(LibraryParser.albumsXpath);
        NodeList trackList = libraryParser.getNodeList(LibraryParser.tracksXpath);
        NodeList artistList = libraryParser.getNodeList(LibraryParser.artistsXpath);

        int listLength = 0;
        MediaContent mediaContent;
        List<MediaContent> mediaContentList = new ArrayList<>();

        if (albumList.getLength() == trackList.getLength() && artistList.getLength() == albumList.getLength()) {
            listLength = albumList.getLength();
            for (int i = 0; i < listLength; i++) {
                mediaContent = new MediaContent(new Album(albumList.item(i).getNodeValue()),
                        new Artist(artistList.item(i).getNodeValue()), new Track(trackList.item(i).getNodeValue()));
                mediaContentList.add(mediaContent);
            }
        }
        return mediaContentList;
    }
}
