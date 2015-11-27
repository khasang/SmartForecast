package com.khasang.forecast.models;

public class Sys {
    private String country;
    private long sunset;
    private long sunrise;

    @Override
    public String toString() {
        return "Sys{" +
                "country='" + country + '\'' +
                ", sunset=" + sunset +
                ", sunrise=" + sunrise +
                '}';
    }
}
