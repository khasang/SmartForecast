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

    public String getCountry() {
        return country;
    }

    public long getSunset() {
        return sunset;
    }

    public long getSunrise() {
        return sunrise;
    }
}
