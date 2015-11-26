package com.khasang.forecast;

import java.util.Calendar;

/**
 * Created by Veda on 24.11.15.
 */
public interface ILocation {
    public void setWeather(WeatherStationFactory.WEATHER_SERVICE_TYPE ws, Calendar date, Weather weather);

    Coordinate getPosition();
}
