package com.atacankullabci.immovin.dto;

public class ImageDTO {
    private String height;
    private String width;
    private String url;

    public ImageDTO() {
    }

    public ImageDTO(String height, String width, String url) {
        this.height = height;
        this.width = width;
        this.url = url;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
                "height='" + height + '\'' +
                ", width='" + width + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
