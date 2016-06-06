package com.khasang.forecast.models;

import java.util.Date;

public class Changelog {

    private String version;
    private Date date;
    private Image image;
    private int changesResId;

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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getChangesResId() {
        return changesResId;
    }

    public void setChangesResId(int changesResId) {
        this.changesResId = changesResId;
    }
}
