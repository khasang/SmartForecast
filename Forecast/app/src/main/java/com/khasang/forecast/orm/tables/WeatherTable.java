package com.khasang.forecast.orm.tables;

import com.orm.SugarRecord;

/**
 * Created by maxim.kulikov on 16.03.2016.
 */
public class WeatherTable extends SugarRecord {

    public String stationName;
    public String town;
    public String date;
    public double temperature;
    public double temperatureMin;
    public double temperatureMax;
    public double pressure;
    public int humidity;
    public String description;
    public String windDirection;
    public double windSpeed;
    public String precipitationType;

    public WeatherTable() {
    }

    public WeatherTable(String stationName, String town, String date, double temperature, double temperatureMin,
                        double temperatureMax, double pressure, int humidity, String description, String windDirection,
                        double windSpeed, String precipitationType) {

        this.stationName = stationName;
        this.town = town;
        this.date = date;
        this.temperature = temperature;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.pressure = pressure;
        this.humidity = humidity;
        this.description = description;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.precipitationType = precipitationType;
    }
}
