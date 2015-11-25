package com.khasang.forecast;

import java.util.ArrayList;

/**
 * Created by novoselov on 24.11.2015.
 */

public abstract class WeatherStation {
    String weatherStationName;

    WeatherStationFactory.WEATHER_SERVICE_TYPE serviceType;

    public WeatherStationFactory.WEATHER_SERVICE_TYPE getServiceType() {
        return serviceType;
    }

    public String getWeatherStationName() {
        return weatherStationName;
    }

    abstract void updateWeather(iLocation loc);

    abstract void updateHourlyWeather(iLocation loc);

    abstract void updateWeeklyWeather(iLocation loc);
}
