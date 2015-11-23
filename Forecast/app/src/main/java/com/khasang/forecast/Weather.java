package com.khasang.forecast;

/**
 * Created by baradist on 24.11.2015.
 */
public class Weather {
    private int temperature;
    private int temperatureFeeling;
    private int pressure;
    private int humidity;
    private Wind wind;
    private Precipitation precipitation;

    public Weather() {
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTemperatureFeeling() {
        return temperatureFeeling;
    }

    public void setTemperatureFeeling(int temperatureFeeling) {
        this.temperatureFeeling = temperatureFeeling;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Precipitation getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Precipitation precipitation) {
        this.precipitation = precipitation;
    }
}
