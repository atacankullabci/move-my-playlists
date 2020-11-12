package com.atacankullabci.immovin.dto;

public class PlaylistDTO {
    private String id;

    public PlaylistDTO() {
    }

    public PlaylistDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PlaylistDTO{" +
                "id='" + id + '\'' +
                '}';
    }
}
