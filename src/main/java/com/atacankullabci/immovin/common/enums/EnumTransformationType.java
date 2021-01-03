package com.atacankullabci.immovin.common.enums;

public enum EnumTransformationType {
    LIBRARY("transform.xslt"),
    ALBUM("albums.xslt"),
    PLAYLIST("playlist.xslt");

    private String transformationFileName;

    EnumTransformationType(String transformationFileName) {
        this.transformationFileName = transformationFileName;
    }

    public String getTransformationFileName() {
        return transformationFileName;
    }
}
