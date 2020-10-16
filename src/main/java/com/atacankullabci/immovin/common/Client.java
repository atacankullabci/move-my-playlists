package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
public class Client {
    @Id
    private String id;
    private String ip;
    private Instant issueDate;
    private List<MediaContent> mediaContentList;

    public Client() {
    }

    public Client(String ip, Instant issueDate, List<MediaContent> mediaContentList) {
        this.ip = ip;
        this.issueDate = issueDate;
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

    public Instant getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Instant issueDate) {
        this.issueDate = issueDate;
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
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", issueDate=" + issueDate +
                ", mediaContentList=" + mediaContentList +
                '}';
    }
}
