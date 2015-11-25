package com.khasang.forecast;

import java.util.Date;

/**
 * Created by Veda on 24.11.15.
 */
public interface Observer {

    public void setWeather(WeatherStationFactory.WEATHER_STATION ws, Date date, Weather weather);
}
