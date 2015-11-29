package com.khasang.forecast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Роман on 26.11.2015.
 */
public class WeatherStationFactory {
    public enum ServiceType {OPEN_WEATHER_MAP}

    private HashMap<String, WeatherStation> stations;

    public WeatherStationFactory() {
        stations = new HashMap<String, WeatherStation>();
    }

    public WeatherStationFactory addOpenWeatherMap() {
        WeatherStation ws = new OpenWeatherMap();
        String name = "OpenWeatherMap";
        ws.serviceType = ServiceType.OPEN_WEATHER_MAP;
        stations.put(name, ws);
        return this;
    }

    /*

    При добадении новых сервисов добавить в билдер строитель для каждого сервиса по типу
    public Builder addOpenWeatherMap

    */

    public HashMap<String, WeatherStation> create() {
        return stations;
    }

}
