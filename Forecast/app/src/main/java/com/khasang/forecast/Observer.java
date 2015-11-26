package com.khasang.forecast;

import java.util.Date;

/**
 * Created by novoselov on 24.11.2015.
 */
public interface Observer {
    void setWeather(WeatherStation.WEATHER_STATION ws, Date dt, Weather weather);
    Coordinate getCoordinate ();
}
