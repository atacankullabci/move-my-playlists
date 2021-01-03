package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.Album;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.Playlist;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.common.enums.EnumTransformationType;
import com.atacankullabci.immovin.repository.MediaContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ObjectHandler {

    static final Logger logger = LoggerFactory.getLogger(ObjectHandler.class);

    private final MediaContentRepository mediaContentRepository;

    private final LibraryTransformer libraryTransformer;

    public ObjectHandler(MediaContentRepository mediaContentRepository, LibraryTransformer libraryTransformer) {
        this.mediaContentRepository = mediaContentRepository;
        this.libraryTransformer = libraryTransformer;
    }

    private List<MediaContent> getPlaylistMediaContent(String[] trackIds, List<MediaContent> mediaContentList) {
        List<MediaContent> playlistMediaContentList = new ArrayList<>();
        for (String id : trackIds) {
            for (MediaContent mediaContent : mediaContentList) {
                if (id.equals(mediaContent.getTrackId())) {
                    playlistMediaContentList.add(mediaContent);
                }
            }
        }
        return playlistMediaContentList;
    }

    public List<MediaContent> getMediaContentList(byte[] file) {
        logger.info("Library xml transformation started");

        String rawMediaContent = "";
        try {
            rawMediaContent = libraryTransformer.transform(file, EnumTransformationType.LIBRARY);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String[] mediaContentLineArr = rawMediaContent.split("\\r?\\n");
        String[] mediaContentArr;

        List<MediaContent> mediaContentList = new ArrayList<>();
        MediaContent mediaContent;
        for (String mediaContentLineElem : mediaContentLineArr) {
            mediaContentArr = mediaContentLineElem.split("#");
            if (mediaContentArr.length == 6) {
                mediaContent = new MediaContent(mediaContentArr[0].trim(), mediaContentArr[1].trim(),
                        mediaContentArr[2].trim(), mediaContentArr[3].trim(), mediaContentArr[4].trim(), mediaContentArr[5].trim());
                mediaContentList.add(mediaContent);
            }
        }
        return mediaContentList;
    }

    public List<Album> getAlbumList(byte[] file) {
        logger.info("Albums xml transformation started");

        String albumContent = "";
        try {
            albumContent = libraryTransformer.transform(file, EnumTransformationType.ALBUM);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String[] albumContentLineArr = albumContent.split("\\r?\\n");
        String[] albumContentArr;

        Map<String, Album> albumList = new HashMap<>();

        Album album;
        for (String albumLineElem : albumContentLineArr) {
            albumContentArr = albumLineElem.split("\\t");
            if (albumContentArr.length == 5) {
                album = new Album(albumContentArr[0].trim(), albumContentArr[1].trim(),
                        albumContentArr[2].trim(), albumContentArr[3].trim(), albumContentArr[4].trim());
                albumList.put(generateMD5(album), album);
            }
        }
        return new ArrayList<>(albumList.values());
    }

    private static String generateMD5(Album album) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md.update((album.getAlbumName().trim() + album.getAlbumArtist().trim()).getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public List<Playlist> getUserPlaylists(byte[] file, User user) {
        logger.info("Playlists xml transformation started");

        String rawPlaylistContent = "";
        try {
            rawPlaylistContent = libraryTransformer.transform(file, EnumTransformationType.PLAYLIST);
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
                playlist = new Playlist(playListItem[0], getPlaylistMediaContent(trackIds, user.getMediaContentList()));
                playlists.add(playlist);
            }
        }

        return playlists;
    }
}
