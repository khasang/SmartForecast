package com.khasang.forecast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Veda on 24.11.15.
 */

public class Position implements ILocation {
    private String name;
    private int cityID;
    private Coordinate coordinate;
    private Map<WeatherStationFactory.ServiceType, Map<Calendar, Weather>> weather;

    public Position() {
        weather = new HashMap<>();
    }

    public void addWeatherStation(WeatherStationFactory.ServiceType ws) {
        weather.put(ws, new HashMap<Calendar, Weather>());
    }

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

    public Set<Calendar> getAllDates(WeatherStationFactory.ServiceType ws) {
        return weather.get(ws).keySet();
    }

    public Weather getWeather(WeatherStationFactory.ServiceType ws, Calendar date) {
        //      Weather weatherOfDate = null;
        return weather.get(ws).get(date);
/*
        for (Map.Entry<WeatherStationFactory.ServiceType, Map<Calendar, Weather>> entry : weather.entrySet()) {
            if (entry.getKey() == ws) {
                weatherOfDate = entry.getValue().get(date);
            }
        }
        return weatherOfDate;
        */
    }

    @Override
    public void setWeather(WeatherStationFactory.ServiceType ws, Calendar date, Weather weather) {
        for (Map.Entry<WeatherStationFactory.ServiceType, Map<Calendar, Weather>> entry : this.weather.entrySet()) {
            if (entry.getKey() == ws) {
                entry.getValue().put(date, weather);
            }
        }
    }

    @Override
    public Coordinate getPosition() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        if (cityID == position.cityID) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 17 * result + (name == null ? 0 : name.hashCode());
        result = 17 * result + cityID;
        return result;
    }
}





