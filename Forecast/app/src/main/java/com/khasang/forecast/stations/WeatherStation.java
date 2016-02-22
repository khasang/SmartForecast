package com.khasang.forecast.stations;

import com.khasang.forecast.position.Coordinate;

import java.util.LinkedList;

/**
 * Created by novoselov on 24.11.2015.
 */

public abstract class WeatherStation {

    public enum ResponseType {CURRENT, HOURLY, DAILY};

    WeatherStationFactory.ServiceType serviceType;

    public WeatherStationFactory.ServiceType getServiceType() {
        return serviceType;
    }

    public String getWeatherStationName() {
        return serviceType.toString();
    }

    public abstract void updateWeather(LinkedList<ResponseType> requestQueue, int cityID, Coordinate coordinate);

    public abstract void updateHourlyWeather(LinkedList<ResponseType> requestList, int cityID, Coordinate coordinate);

    public abstract void updateWeeklyWeather(LinkedList<ResponseType> requestList, int cityID, Coordinate coordinate);
}
