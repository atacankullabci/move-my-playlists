package com.atacankullabci.immovin.dto;

public class TrackDTO {
    private String id;
    private String uri;

    public TrackDTO() {
    }

    public TrackDTO(String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "TrackDTO{" +
                "id='" + id + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
