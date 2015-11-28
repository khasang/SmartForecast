package com.khasang.forecast.models;

public class City {

    private long id;
    private String name;
    private String country;
    private int population;
    private Sys sys;

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", population=" + population +
                ", sys=" + sys +
                '}';
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public int getPopulation() {
        return population;
    }

    public Sys getSys() {
        return sys;
    }
}