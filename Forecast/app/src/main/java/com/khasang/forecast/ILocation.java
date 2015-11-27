package com.khasang.forecast;

import java.util.Calendar;

/**
 * Created by Veda on 24.11.15.
 */
public interface ILocation {
    public void setWeather(WeatherStationFactory.ServiceType ws, Calendar date, Weather weather);

    Coordinate getPosition();
}
