package com.khasang.forecast.models;

import java.util.Date;

public class Changelog {

    private String version;
    private Date date;
    private String imageUrl;
    private int changesRes;
    private int imageWidth;
    private int imageHeight;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getChangesRes() {
        return changesRes;
    }

    public void setChangesRes(int changesRes) {
        this.changesRes = changesRes;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
