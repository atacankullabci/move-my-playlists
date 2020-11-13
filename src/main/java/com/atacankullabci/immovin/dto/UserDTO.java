package com.atacankullabci.immovin.dto;

import java.util.Arrays;

public class UserDTO {
    private String id;
    private String display_name;
    private ExternalUrlDTO external_urls;
    private ImageDTO[] images;

    public UserDTO() {
    }

    public UserDTO(String id, String display_name, ExternalUrlDTO external_urls, ImageDTO[] images) {
        this.id = id;
        this.display_name = display_name;
        this.external_urls = external_urls;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public ExternalUrlDTO getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(ExternalUrlDTO external_urls) {
        this.external_urls = external_urls;
    }

    public ImageDTO[] getImages() {
        return images;
    }

    public void setImages(ImageDTO[] images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", display_name='" + display_name + '\'' +
                ", external_urls=" + external_urls +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
