package com.khasang.forecast.models;

public class Developer {

    private String name;
    private String description;
    private int resId;

    public Developer(String name, String description, int resId) {
        this.name = name;
        this.description = description;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getResId() {
        return resId;
    }
}
