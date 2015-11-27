package com.khasang.forecast.models;

import java.util.ArrayList;

public class HourlyForecastList {

    private int dt;
    private Main main;
    private ArrayList<Weather> weather = new ArrayList<>();
    private Wind wind;
    private String dtTxt;
    private Snow snow;
    private Rain rain;
    private Clouds clouds;

    public int getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public Snow getSnow() {
        return snow;
    }

    public Rain getRain() {
        return rain;
    }

    public Clouds getClouds() {
        return clouds;
    }

    @Override
    public String toString() {
        return "HourlyForecastList{" +
                "dt=" + dt +
                ", main=" + main +
                ", weather=" + weather +
                ", wind=" + wind +
                ", dtTxt='" + dtTxt + '\'' +
                ", snow=" + snow +
                ", rain=" + rain +
                ", clouds=" + clouds +
                '}';
    }
}
