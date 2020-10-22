package com.atacankullabci.immovin.dto;

import java.util.Arrays;

public class UserDTO {
    private String display_name;
    private ExternalUrl external_urls;
    private ImageDTO[] images;

    public UserDTO() {
    }

    public UserDTO(String display_name, ExternalUrl external_urls, ImageDTO[] images) {
        this.display_name = display_name;
        this.external_urls = external_urls;
        this.images = images;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public ExternalUrl getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(ExternalUrl external_urls) {
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
                "display_name='" + display_name + '\'' +
                ", external_urls=" + external_urls +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
