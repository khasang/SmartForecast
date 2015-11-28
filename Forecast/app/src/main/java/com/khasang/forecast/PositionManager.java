package com.khasang.forecast;

import android.content.Context;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionManager {
    public static final double KELVIN_CELSIUS_DELTA = 273.15;
    public static final double KPA_TO_MM_HG = 1.33322;
    public static final double KM_TO_MILES = 0.62137;
    public static final double METER_TO_FOOT = 3.28083;
    private WeatherStation currStation;
    private Position currPosition;
    private HashMap<String, WeatherStation> stations;
    private Map<String, Position> mPositions;
    private Context mContext;

    public PositionManager(Context context) {
        mContext = context;
    }

    public void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf
                .addOpenWeatherMap()
                .create();
    }

    public void initPositions(List<String> favorites) throws NullPointerException{
        PositionFactory positionFactory = new PositionFactory(mContext);
        positionFactory = positionFactory.addCurrentPosition();
        for (String pos : favorites) {
            positionFactory = positionFactory.addFavouritePosition(pos);
        }
        mPositions = positionFactory.getPositions();
    }

    public Position getFavoritePosition(String name){
        return mPositions.get(name);
    }
    
    public Weather getWeather() {
        return getWeather(currStation.getServiceType());
    }

    public Weather getWeather(WeatherStationFactory.ServiceType stationType) {
        return getWeather(stationType, GregorianCalendar.getInstance());
    }

    public Weather getWeather(WeatherStationFactory.ServiceType stationType, Calendar с) {
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
    public enum TEMPERATURE {KELVIN, CELSIUS, FAHRENHEIT}
    public enum SPEED {METER_PER_SECOND, FOOT_PER_SECOND, KM_PER_HOURS, MILES_PER_HOURS}
    public enum PRESSURE {HPA, MM_HG}

    // Установка режима отображения температуры
    TEMPERATURE settingsTemperature = TEMPERATURE.KELVIN;
    double formatTemperature(double temperature) {
        switch (settingsTemperature) {
            case KELVIN:
                break;
            case CELSIUS:
                kelvinToCelsius(temperature);
                break;
            case FAHRENHEIT:
                kelvinToFahrenheit(temperature);
                break;
        }
        return temperature;
    }
    // Установка режима отображения скорости
    SPEED formatSpeed = SPEED.METER_PER_SECOND;
    double formatSpeed(double speed) {
        switch (formatSpeed) {
            case METER_PER_SECOND:
                break;
            case FOOT_PER_SECOND:
                meterInSecondToFootInSecond(speed);
                break;
            case KM_PER_HOURS:
                meterInSecondToKmInHours(speed);
                break;
            case MILES_PER_HOURS:
                meterInSecondToMilesInHour(speed);
                break;
        }
        return speed;
    }
    // Установка режима отображения давления
    PRESSURE formatPressure = PRESSURE.HPA;
    double formatPressure(double pressure) {
        switch (formatPressure) {
            case HPA:
                break;
            case MM_HG:
                kpaToMmHg(pressure);
                break;
        }
        return pressure;
    }

    // Непосредственно методы преобразований
    // Преобразование из Кельвина в Цельсий
    public double kelvinToCelsius(double temperature) {
        double celsiusTemperature = temperature - KELVIN_CELSIUS_DELTA;
        return celsiusTemperature;
    }
    // Преобразование из Кельвина в Фаренгейт
    public double kelvinToFahrenheit(double temperature) {
        double fahrenheitTemperature = (kelvinToCelsius(temperature) * 9 / 5) + 32;
        return fahrenheitTemperature;
    }
    // Преобразование из метров в секунду в футы в секунду
    public double meterInSecondToFootInSecond(double speed) {
        double footInSecond = speed * METER_TO_FOOT;
        return footInSecond;
    }
    // Преобразование из метров в секунду в километры в час
    public double meterInSecondToKmInHours(double speed) {
        double kmInHours = speed * 3.6;
        return kmInHours;
    }
    // Преобразование из метров в секунду в мили в час
    public double meterInSecondToMilesInHour(double speed) {
        double milesInHours = meterInSecondToKmInHours(speed) * KM_TO_MILES;
        return milesInHours;
    }
    // Преобразование из килопаскалей в мм.рт.ст.
    public  double kpaToMmHg (double pressure) {
        double mmHg = pressure / KPA_TO_MM_HG;
        return mmHg;
    }

}