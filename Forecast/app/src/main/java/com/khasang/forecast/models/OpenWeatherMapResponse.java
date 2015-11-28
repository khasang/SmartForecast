package com.khasang.forecast.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenWeatherMapResponse {
    private Weather[] weather;
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private Rain rain;
    private long dt;
    private Sys sys;
    private long id;
    private String name;
    private List<HourlyForecastList> list = new ArrayList<>();

    public Weather[] getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Rain getRain() {
        return rain;
    }

    public long getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<HourlyForecastList> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "OpenWeatherMapResponse{" +
                "weather=" + Arrays.toString(weather) +
                ", main=" + main +
                ", wind=" + wind +
                ", clouds=" + clouds +
                ", rain=" + rain +
                ", dt=" + dt +
                ", sys=" + sys +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", list=" + list +
                '}';
    }
}
