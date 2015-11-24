package com.khasang.forecast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by novoselov on 24.11.2015.
 */
public abstract class WeatherStation implements Observable {
    private ArrayList<Observer> locations;      // Создавать в конструкторе
    Builder.WEATHER_STATION stationName;

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
    abstract public void updateWeatherInfo();

    abstract public void updateWeatherInfo(Date date);

    abstract void requestDateFromStation(Date dt, Coordinate coordinate);

    abstract void sendDataToLocation();

    public static class Builder {
        private ArrayList<WeatherStation> stations;

        // Сейчас  толко OpenWeatherMap, при добавдении новых сервисов расширять enum
        public enum WEATHER_STATION {
            OPEN_WEATHER_MAP;
        }

        public Builder() {
            stations = new ArrayList<WeatherStation>();
        }

        public Builder addOpenWeatherMap(ArrayList<Observer> locations) {
            WeatherStation ws = new OpenWeatherMap();
            ws.locations = locations;
            ws.stationName = WEATHER_STATION.OPEN_WEATHER_MAP;
            stations.add(ws);
            return this;
        }

        /*
        При добадении новых сервисов добавить в билдер строитель для каждого сервиса по типу
        public Builder addOpenWeatherMap (ArrayList <Observer> locations)
        */

        public ArrayList<WeatherStation> build() {
            return stations;
        }
    }
}
