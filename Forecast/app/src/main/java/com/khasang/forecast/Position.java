package com.khasang.forecast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Veda on 24.11.15.
 */

public class Position implements iLocation {
    private String name;
    private int cityID;     // Надо подумать нужно ли ....
    private Coordinate coordinate;
    private Map<WeatherStationFactory.WEATHER_SERVICE_TYPE, Map<Calendar, Weather>> weather;

    public void setLocationName(String name) {
        this.name = name;
    }

    public String getLocationName() {
        return name;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Weather getWeather(WeatherStationFactory.WEATHER_SERVICE_TYPE ws, Calendar date) {
        Weather weatherOfDate = null;
        for (Map.Entry<WeatherStationFactory.WEATHER_SERVICE_TYPE, Map<Calendar, Weather>> entry : weather.entrySet()) {
            if (entry.getKey() == ws) {
                weatherOfDate = entry.getValue().get(date);
            }
        }
        return weatherOfDate;
    }

    @Override
    public void setWeather(WeatherStationFactory.WEATHER_SERVICE_TYPE ws, Calendar date, Weather weather) {
        for (Map.Entry<WeatherStationFactory.WEATHER_SERVICE_TYPE, Map<Calendar, Weather>> entry : this.weather.entrySet()) {
            if (entry.getKey() == ws) {
                entry.getValue().put(date, weather);
            }
        }
    }

    @Override
    public Coordinate getPosition() {
        return null;
    }
}





