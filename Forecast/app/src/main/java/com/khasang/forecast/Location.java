package com.khasang.forecast;

import java.util.Date;
import java.util.Map;

/**
 * Created by Veda on 24.11.15.
 */
public class Location implements Observer{

    private String name;
    private int cityID;
    private Coordinate coordinate;
    private Map<WeatherStationFactory.WEATHER_STATION, Map<Date, Weather>> weather;

//    private Date chooseNearestForecastDate(WeatherStationFactory.WEATHER_STATION ws, Date date) {
//
//    }

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

    public Weather getWeather(WeatherStationFactory.WEATHER_STATION ws, Date date) {
        Weather weatherOfDate = null;
        for (Map.Entry<WeatherStationFactory.WEATHER_STATION, Map<Date, Weather>> entry : weather.entrySet()) {
            if (entry.getKey() == ws) {
                weatherOfDate = entry.getValue().get(date);
            }
        }
        return weatherOfDate;
    }

    @Override
    public void setWeather(WeatherStationFactory.WEATHER_STATION ws, Date date, Weather weather) {
        for (Map.Entry<WeatherStationFactory.WEATHER_STATION, Map<Date, Weather>> entry : this.weather.entrySet()) {
            if (entry.getKey() == ws) {
                entry.getValue().put(date,weather);
            }
        }
    }
}





