package com.khasang.forecast;

import java.util.Date;
import java.util.Map;

/**
 * Created by Veda on 24.11.15.
 */
public class Location implements Observer {

    private String name;
    private int cityID;
    private Coordinate coordinate;
    private Map<WeatherStation.Builder.WEATHER_STATION, Map<Date, Weather>> weather;

    //fixme: класс Date считается устаревшим, не лучше ли использовать класс Calender ?
//    private Date chooseNearestForecastDate(WeatherStation.Builder.WEATHER_STATION ws, Date date) {
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

    @Override
    public void setWeather(WeatherStation.Builder.WEATHER_STATION ws, Date dt, Weather weather) {
        Map<Date, Weather> map = this.weather.get(ws);
        map.put(dt, weather);
        this.weather.put(ws,map);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Weather getWeather(WeatherStation.Builder.WEATHER_STATION ws, Date date) {
        return weather.get(ws).get(date);
    }

    // если новые коррдинаты выходят за рамки локации, то возвращает true
    public boolean isChanged(Coordinate coordinate) {
        return (this.coordinate.compareTo(coordinate) != 0);
    }
}





