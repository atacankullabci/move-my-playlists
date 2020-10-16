package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Client {
    @Id
    private String id;
    private String ip;
    private List<MediaContent> mediaContentList;

    public Client() {
    }

    public Client(String ip, List<MediaContent> mediaContentList) {
        this.ip = ip;
        this.mediaContentList = mediaContentList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<MediaContent> getMediaContentList() {
        return mediaContentList;
    }

    public void setMediaContentList(List<MediaContent> mediaContentList) {
        this.mediaContentList = mediaContentList;
    }

    @Override
    public String toString() {
        return "Client{" +
                "ip='" + ip + '\'' +
                ", mediaContentList=" + mediaContentList +
                '}';
    }
}
