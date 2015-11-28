package com.khasang.forecast;

import java.util.ArrayList;

/**
 * Created by Роман on 26.11.2015.
 */
public class WeatherStationFactory {
    public enum ServiceType {OPEN_WEATHER_MAP}

    private ArrayList<WeatherStation> stations;

    public WeatherStationFactory() {
        stations = new ArrayList<WeatherStation>();
    }

    public WeatherStationFactory addOpenWeatherMap() {
        WeatherStation ws = new OpenWeatherMap();
        ws.weatherStationName = String.valueOf(R.string.service_name_open_weather_map);
        ws.serviceType = ServiceType.OPEN_WEATHER_MAP;
        stations.add(ws);
        return this;
    }

    /*

    При добадении новых сервисов добавить в билдер строитель для каждого сервиса по типу
    public Builder addOpenWeatherMap

    */

    public ArrayList<WeatherStation> create() {
        return stations;
    }

}
