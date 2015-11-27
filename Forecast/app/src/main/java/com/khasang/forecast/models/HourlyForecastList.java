package com.khasang.forecast.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HourlyForecastList {

    private Temp temp;
    private double pressure;
    private int humidity;
    private double speed;
    private int deg;
    private double rain;
    private int dt;
    private Main main;
    private ArrayList<Weather> weather = new ArrayList<>();
    private Wind wind;
    private String dtTxt;
    private Snow snow;
    @SerializedName("rain")
    private Rain rainn;

    @Override
    public String toString() {
        return "ForecastList{" +
                "temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", speed=" + speed +
                ", deg=" + deg +
                ", rain=" + rain +
                ", dt=" + dt +
                ", main=" + main +
                ", weather=" + weather +
                ", wind=" + wind +
                ", dtTxt='" + dtTxt + '\'' +
                '}';
    }

    public Temp getTemp() {
        return temp;
    }

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDeg() {
        return deg;
    }

    public double getRain() {
        return rain;
    }

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
}
