package com.khasang.forecast.interfaces;

import com.khasang.forecast.position.Weather;
import com.khasang.forecast.stations.WeatherStation;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by roman on 08.03.16.
 */
public interface IWeatherReceiver {
    boolean receiveHourlyWeatherFirst();
    void updateInterface(WeatherStation.ResponseType responseType, Map<Calendar, Weather> forecast);
}
