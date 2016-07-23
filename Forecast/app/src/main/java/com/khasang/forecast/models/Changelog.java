package com.khasang.forecast.models;

import java.util.Date;

public class Changelog {

    private int versionResId;
    private Date date;
    private Image image;
    private int changesResId;

    public int getVersionResId() {
        return versionResId;
    }

    public void setVersionResId(int versionResId) {
        this.versionResId = versionResId;
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
