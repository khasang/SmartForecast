package com.khasang.forecast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionManager {
    public static final int KELVIN_CELSIUS_DELTA = 273;
    private WeatherStation currStation;
    private Position currPosition;
    private ArrayList<WeatherStation> stations;
    private ArrayList<Position> positions;

    public PositionManager() {

    }

    public void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf.addOpenWeatherMap().create();
    }

    public void initPositions(List<String> favorites) {
        PositionFactory positionFactory = new PositionFactory();
        positionFactory = positionFactory.addCurrentPosition();
        for (String pos : favorites) {
            positionFactory = positionFactory.addFavouritePosition(pos);
        }
        positions = positionFactory.create();
    }

    public Weather getWeather() {
        return getWeather(currStation.getServiceType());
    }

    public Weather getWeather(WeatherStationFactory.WEATHER_SERVICE_TYPE stationType) {
        return getWeather(stationType, GregorianCalendar.getInstance());
    }

    public Weather getWeather(WeatherStationFactory.WEATHER_SERVICE_TYPE stationType, Calendar с) {
        // TODO Видирать погоду по ближайшей дате из Position и возвращать ее
        return new Weather();
    }

    public Weather[] getHourlyWeather() {
        final int HOUR_PERIOD = 4;
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
        calendar.set(Calendar.MINUTE, 0);
        Weather[] weather = new Weather[7];
        for (int i = 0; i < 7; i++) {
            weather[i] = getWeather(currStation.getServiceType(), calendar);
            calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
        }
        return weather;
    }

    public Weather[] getWeeklyWeather() {
        Weather[] weather = new Weather[7];
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        for (int i = 0; i < 7; i++) {
            weather[i] = getWeather(currStation.getServiceType(), calendar);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return weather;
    }

    public void updateCurrent() {
        currStation.updateWeather(currPosition.getCoordinate(), this);
    }

    public void updateHourly() {
        currStation.updateHourlyWeather(currPosition.getCoordinate(), this);
    }

    public void updateWeekly() {
        currStation.updateWeeklyWeather(currPosition.getCoordinate(), this);
    }

    public void onResponseReceived(Coordinate coordinate, Weather weather) {
        //TODO Положить данные в Position
        //TODO Сообщить активити что бы она обновила свои данные
    }

    // TODO Добавить функции пеобразования температуры и других параметров
    // Преобразование из Кельвина в Цельсий
    public int kelvinToCelsius(int temperature) {
        int celsiusTemperature = temperature - KELVIN_CELSIUS_DELTA;
        return celsiusTemperature;
    }
    // Преобразование из Кельвина в Фаренгейт
    public int kelvinToFahrenheit(int temperature) {
        int fahrenheitTemperature = (kelvinToCelsius(temperature) * 9 / 5) + 32;
        return fahrenheitTemperature;
    }

}
