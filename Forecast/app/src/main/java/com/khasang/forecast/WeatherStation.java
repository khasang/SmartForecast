package com.khasang.forecast;

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

    abstract void updateWeather(Coordinate coordinate, PositionManager manager);

    abstract void updateHourlyWeather(Coordinate coordinate, PositionManager manager);

    abstract void updateWeeklyWeather(Coordinate coordinate, PositionManager manager);
}
