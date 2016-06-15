package com.khasang.forecast.stations;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;

import java.util.HashMap;

/**
 * Created by Роман on 26.11.2015.
 */
public class WeatherStationFactory {

    private HashMap<ServiceType, WeatherStation> stations;

    public WeatherStationFactory() {
        stations = new HashMap<>();
    }

    public WeatherStationFactory addOpenWeatherMap() {
        WeatherStation ws = new OpenWeatherMap();
        ws.serviceType = ServiceType.OPEN_WEATHER_MAP;
        stations.put(ServiceType.OPEN_WEATHER_MAP, ws);
        return this;
    }

    public HashMap<ServiceType, WeatherStation> create() {
        return stations;
    }

    /* TODO

    При добадении новых сервисов добавить в билдер строитель для каждого сервиса по типу
    public Builder addOpenWeatherMap

    */

    public enum ServiceType {
        OPEN_WEATHER_MAP {
            @Override
            public String toString() {
                return MyApplication.getAppContext().getString(R.string.service_name_open_weather_map);
            }
        };

        @Override
        public abstract String toString();
    }

}
