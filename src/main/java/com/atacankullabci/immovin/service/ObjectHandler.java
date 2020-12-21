package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.enums.EnumTransformationType;
import com.atacankullabci.immovin.repository.MediaContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ObjectHandler {

    static final Logger logger = LoggerFactory.getLogger(ObjectHandler.class);

    private final MediaContentRepository mediaContentRepository;

    public ObjectHandler(MediaContentRepository mediaContentRepository) {
        this.mediaContentRepository = mediaContentRepository;
    }

    public List<Playlist> getUserPlaylists(byte[] file) {
        logger.info("Playlists xml transformation started");

        LibraryTransformer transformer = new LibraryTransformer();
        String rawPlaylistContent = "";
        try {
            rawPlaylistContent = transformer.transform(file, EnumTransformationType.PLAYLIST);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String[] playlistContentLineArr = rawPlaylistContent.split("\\r?\\n");
        String[] playListItem, trackIds;

        List<Playlist> playlists = new ArrayList<>();
        Playlist playlist;
        for (String pl : playlistContentLineArr) {
            playListItem = pl.split("#");
            if (playListItem.length < 2) {
                continue;
            }
            trackIds = playListItem[1].split(",");

            if (playListItem.length == 2) {
                playlist = new Playlist(playListItem[0], getPlaylistMediaContent(trackIds));
                playlists.add(playlist);
            }
        }

        return playlists;
    }

    private List<MediaContent> getPlaylistMediaContent(String[] trackIds) {
        List<MediaContent> playlistMediaContentList = new ArrayList<>();
        Optional<MediaContent> mediaContent;

        for (String id : trackIds) {
            mediaContent = this.mediaContentRepository.findById(id);
            if (mediaContent.isPresent()) {
                playlistMediaContentList.add(mediaContent.get());
            }
        }
        return playlistMediaContentList;
    }

    public List<MediaContent> getMediaContentList(byte[] file) {
        logger.info("Library xml transformation started");

        LibraryTransformer transformer = new LibraryTransformer();
        String rawMediaContent = "";
        try {
            rawMediaContent = transformer.transform(file, EnumTransformationType.LIBRARY);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String[] mediaContentLineArr = rawMediaContent.split("\\r?\\n");
        String[] mediaContentArr;

        List<MediaContent> mediaContentList = new ArrayList<>();
        MediaContent mediaContent;
        for (String mediaContentLineElem : mediaContentLineArr) {
            mediaContentArr = mediaContentLineElem.split("#");
            mediaContent = new MediaContent(mediaContentArr[0], mediaContentArr[1],
                    mediaContentArr[2], mediaContentArr[3], mediaContentArr[4]);
            mediaContentList.add(mediaContent);
        }
        return mediaContentList;
    }
}
