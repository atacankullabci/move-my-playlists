package com.atacankullabci.immovin.dto;

public class UserDTO {
    private String display_name;
    private ExternalUrl external_urls;

    public UserDTO() {
    }

    public UserDTO(String display_name, ExternalUrl external_urls) {
        this.display_name = display_name;
        this.external_urls = external_urls;
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

    @Override
    public String toString() {
        return "User{" +
                "display_name='" + display_name + '\'' +
                ", external_urls=" + external_urls +
                '}';
    }
}
