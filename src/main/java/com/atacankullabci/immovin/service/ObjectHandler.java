package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectHandler {

    public List<MediaContent> getMediaContentList(byte[] file) {
        String rawMediaContent = "";
        try {
            rawMediaContent = LibraryTransformer.transform(file);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String[] mediaContentLineArr = rawMediaContent.split("\\r?\\n");
        String[] mediaContentArr;

        List<MediaContent> mediaContentList = new ArrayList<>();
        MediaContent mediaContent;
        for (String mediaContentLineElem : mediaContentLineArr) {
            mediaContentArr = mediaContentLineElem.split("#");
            if (mediaContentArr.length == 3) {
                mediaContent = new MediaContent(mediaContentArr[0], mediaContentArr[1], mediaContentArr[2]);
                mediaContentList.add(mediaContent);
            }
        }
        return mediaContentList;
    }
}
