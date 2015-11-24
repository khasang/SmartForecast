package com.khasang.forecast;

import java.util.Date;

/**
 * Created by Veda on 24.11.15.
 */
public interface Observer {
    void setWeather(WeatherStation.Builder.WEATHER_STATION ws, Date dt, Weather weather);

    Coordinate getCoordinate();
}
