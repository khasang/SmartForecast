package com.khasang.forecast;

/**
 * Created by novoselov on 24.11.2015.
 */

public abstract class WeatherStation {
    String weatherStationName;

    WeatherStationFactory.ServiceType serviceType;

    public WeatherStationFactory.ServiceType getServiceType() {
        return serviceType;
    }

    public String getWeatherStationName() {
        return weatherStationName;
    }

    abstract void updateWeather(int cityID, Coordinate coordinate);

    abstract void updateHourlyWeather(int cityID, Coordinate coordinate);

    abstract void updateWeeklyWeather(int cityID, Coordinate coordinate);
}
