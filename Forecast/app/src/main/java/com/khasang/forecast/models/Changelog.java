package com.khasang.forecast.models;

import java.util.Date;

public class Changelog {

    private String version;
    private Date date;
    private String image;
    private int changes;

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
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getChanges() {
        return changes;
    }

    public void setChanges(int changes) {
        this.changes = changes;
    }
}
