package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.repository.MediaContentRepository;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectHandler {

    private final MediaContentRepository mediaContentRepository;

    public ObjectHandler(MediaContentRepository mediaContentRepository) {
        this.mediaContentRepository = mediaContentRepository;
    }

    public List<MediaContent> getMediaContentList(byte[] file) {
        LibraryTransformer transformer = new LibraryTransformer();
        String rawMediaContent = "";
        try {
            rawMediaContent = transformer.transform(file);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String[] mediaContentLineArr = rawMediaContent.split("\\r?\\n");
        String[] mediaContentArr;

        List<MediaContent> mediaContentList = new ArrayList<>();
        MediaContent mediaContent;
        for (String mediaContentLineElem : mediaContentLineArr) {
            mediaContentArr = mediaContentLineElem.split("#");
            if (mediaContentArr.length == 5) {
                mediaContent = new MediaContent(mediaContentArr[0], mediaContentArr[1],
                        mediaContentArr[2], mediaContentArr[3], mediaContentArr[4]);
                mediaContentList.add(mediaContent);
            }
        }

        this.mediaContentRepository.saveAll(mediaContentList);
        return mediaContentList;
    }
}
