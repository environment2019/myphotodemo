package com.example.myphoto.persistence;

import java.io.Serializable;

public class PhotoRecord implements Serializable {

    private static final long serialVersionUID = 8735132092273200831L;

    private String originalImagePathName;
    private String thumbnailPathName;
    private String createDatetime;
    private String customImageName;

    public PhotoRecord(String originalName, String thumbnail, String createDatetime, String customImageName) {
        this.originalImagePathName = originalName;
        this.thumbnailPathName = thumbnail;
        this.createDatetime = createDatetime;
        this.customImageName = customImageName;
    }

    public String getOriginalImagePathName() {
        return originalImagePathName;
    }

    public String getThumbnailPathName() {
        return thumbnailPathName;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public String getCustomImageName() {
        return customImageName;
    }

}
