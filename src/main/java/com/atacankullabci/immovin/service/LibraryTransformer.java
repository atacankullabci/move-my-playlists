package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.enums.EnumTransformationType;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;

public class LibraryTransformer {

    public String transform(byte[] fileContent, EnumTransformationType transformationType) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        ClassLoader classLoader = getClass().getClassLoader();
        Source xslt = null;
        if (transformationType == EnumTransformationType.LIBRARY) {
            xslt = new StreamSource(classLoader.getResourceAsStream("transform.xslt"));
        } else if (transformationType == EnumTransformationType.PLAYLIST) {
            xslt = new StreamSource(classLoader.getResourceAsStream("playlist.xslt"));
        }
        Transformer transformer = factory.newTransformer(xslt);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent);
        Source text = new StreamSource(byteArrayInputStream);

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(text, result);
        return stringWriter.toString();
    }

    public static List<MediaContent> tameMediaContent(List<MediaContent> mediaContentList) {
        for (MediaContent mediaContent : mediaContentList) {
            if (mediaContent.getAlbumName().contains("-")) {
                int dashIndex = mediaContent.getAlbumName().indexOf("-");
                mediaContent.setAlbumName(mediaContent.getAlbumName().substring(0, dashIndex));
            }
            if (mediaContent.toString().contains("(") || mediaContent.toString().contains(")")) {
                omitParantheses(mediaContent);
            }
            if (mediaContent.toString().contains("[") || mediaContent.toString().contains("]")) {
                omitSquareBrackets(mediaContent);
            }
            if (mediaContent.toString().contains("/")) {
                omitSlashes(mediaContent);
            }
        }
        return mediaContentList;
    }

    private static void omitSlashes(MediaContent mediaContent) {
        mediaContent.setAlbumName(mediaContent.getAlbumName().replace('/', ' '));
        mediaContent.setArtistName(mediaContent.getArtistName().replace('/', ' '));
        mediaContent.setTrackName(mediaContent.getTrackName().replace('/', ' '));
    }

    private static void omitParantheses(MediaContent mediaContent) {
        String regex = "\\((.*?)\\)";
        mediaContent.setAlbumName(mediaContent.getAlbumName().replaceAll(regex, ""));
        mediaContent.setArtistName(mediaContent.getArtistName().replaceAll(regex, ""));
        mediaContent.setTrackName(mediaContent.getTrackName().replaceAll(regex, ""));
    }

    private static void omitSquareBrackets(MediaContent mediaContent) {
        String regex = "\\[(.*?)\\]";
        mediaContent.setAlbumName(mediaContent.getAlbumName().replaceAll(regex, ""));
        mediaContent.setArtistName(mediaContent.getArtistName().replaceAll(regex, ""));
        mediaContent.setTrackName(mediaContent.getTrackName().replaceAll(regex, ""));
    }
}
