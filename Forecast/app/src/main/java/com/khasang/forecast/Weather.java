package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Weather {
    private int temperature;
    private int temperatureFeeling;
    private int pressure;
    private int humidity;
    private Wind wind;
    private Precipitation precipitation;

    public Weather () {

    }

    public Weather(int temperature) {
        this.temperature = temperature;
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

    public void setWind(Wind.WIND_DIRECTION wd, double wp) {
        if (this.wind == null) {
            this.wind = new Wind(wd, wp);
        } else {
            wind.setDirection(wd);
            wind.setSpeed(wp);
        }
    }

    public Wind.WIND_DIRECTION getWindDirection() {
        return wind.getDirection();
    }

    public double getWindPower() {
        return wind.getSpeed();
    }

    public void setPrecipitation(Precipitation.Type type, int probability) {
        if (this.precipitation == null) {
            this.precipitation = new Precipitation(type, probability);
        } else {
            this.precipitation.setType(type);
            this.precipitation.setProbability(probability);
        }
    }

    public Precipitation.Type getPrecipitation() {
        return precipitation.getType();
    }

    public int getPrecipitationProbability() {
        return precipitation.getProbability();
    }
}
