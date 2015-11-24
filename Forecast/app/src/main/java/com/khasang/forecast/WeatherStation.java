package com.khasang.forecast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by novoselov on 24.11.2015.
 */
public abstract class WeatherStation implements Observable {
    private ArrayList<Observer> locations;      // Создавать в конструкторе
    WEATHER_STATION stationName;

    public enum WEATHER_STATION {OPEN_WEATHER_MAP;}

    @Override
    public void registerObserver(Observer observer) {
        locations.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        locations.remove(observer);
    }

    @Override
    public void notifyObserver(Observer observer, Date date, Weather weather) {
        observer.setWeather(stationName, date, weather);
    }

    //Обновить данные на текущую дату
    abstract public void updateWeatherInfo ();
    abstract public void updateWeatherInfo (Date date);

    abstract void requestDateFromStation(Date dt, Coordinate coordinate);
    abstract void sendDataToLocation ();

}
