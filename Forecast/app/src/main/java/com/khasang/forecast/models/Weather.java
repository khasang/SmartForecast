package com.khasang.forecast.models;

public class Weather {

    private int id;
    private String main;
    private String description;
    private String icon;

    @Override
    public String toString() {
        return "Weather{" +
                "id=" + id +
                ", main='" + main + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
