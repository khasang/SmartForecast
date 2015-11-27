package com.khasang.forecast.models;

import java.util.ArrayList;
import java.util.List;

public class DailyForecastList {

    public int dt;
    public Temp temp;
    public double pressure;
    public int humidity;
    public List<Weather> weather = new ArrayList<>();
    public double speed;
    public int deg;
    public int clouds;
    public double snow;
    public double rain;

    public int getDt() {
        return dt;
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

    public List<Weather> getWeather() {
        return weather;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDeg() {
        return deg;
    }

    public int getClouds() {
        return clouds;
    }

    public double getSnow() {
        return snow;
    }

    public double getRain() {
        return rain;
    }

    @Override
    public String toString() {
        return "DailyForecastList{" +
                "dt=" + dt +
                ", temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", weather=" + weather +
                ", speed=" + speed +
                ", deg=" + deg +
                ", clouds=" + clouds +
                ", snow=" + snow +
                ", rain=" + rain +
                '}';
    }
}
