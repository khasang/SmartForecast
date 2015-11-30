package com.khasang.forecast;

import com.khasang.forecast.activities.WeatherActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionManager {
    public static final double KELVIN_CELSIUS_DELTA = 273.15;
    public static final double KPA_TO_MM_HG = 1.33322;
    public static final double KM_TO_MILES = 0.62137;
    public static final double METER_TO_FOOT = 3.28083;

    public void setCurrPosition(String positionName) {

    }

    public Position getPosition(String positionName) {
        return null;
    }

    public Object getCurrPosition() {
        return null;
    }

    public enum TemperatureMetrics {KELVIN, CELSIUS, FAHRENHEIT}

    public enum SpeedMetrics {METER_PER_SECOND, FOOT_PER_SECOND, KM_PER_HOURS, MILES_PER_HOURS}

    public enum PressureMetrics {HPA, MM_HG}

    TemperatureMetrics settingsTemperatureMetrics = TemperatureMetrics.KELVIN;
    SpeedMetrics formatSpeedMetrics = SpeedMetrics.METER_PER_SECOND;
    PressureMetrics formatPressureMetrics = PressureMetrics.HPA;

    private WeatherStation currStation;
    private Position currPosition;
    private HashMap<WeatherStationFactory.ServiceType, WeatherStation> stations;
    private HashMap<String, Position> positions;
    private WeatherActivity mActivity;

    public PositionManager(WeatherActivity activity) {
        mActivity = activity;
        List<String> pos = new ArrayList<>();
        pos.add("Москва");
        pos.add("Тула");
        initStations();         //  Пока тут
        initPositions(pos);     //  Пока тут
        currPosition = positions.get("Moscow");
        currStation = stations.get(WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP);
    }

    public void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf
                .addOpenWeatherMap(mActivity.getString(R.string.service_name_open_weather_map))
                .create();
    }

    public void initPositions(List<String> favorites) {
        PositionFactory positionFactory = new PositionFactory(mActivity.getApplicationContext());
        positionFactory.addCurrentPosition();
        for (String pos : favorites) {
            positionFactory.addFavouritePosition(pos);
        }
        positions = positionFactory.getPositions();
    }

    public void addPosition(String name) {
        PositionFactory positionFactory = new PositionFactory(mActivity, positions);
        positionFactory.addFavouritePosition(name);
        positions = positionFactory.getPositions();
    }

    public void removePosition(String name) {
        if (positions.containsKey(name)) {
            positions.remove(name);
        }
    }

    public void setCurrentPosition(String name) {
        if (positions.containsKey(name)) {
            currPosition = positions.get(name);
        }
    }

    public Position getFavoritePosition(String name) {
        if (positions.containsKey(name)) {
            return positions.get(name);
        }
        return null;
    }

    public Weather getWeather() {
        return getWeather(currStation.getServiceType());
    }

    public Weather getWeather(WeatherStationFactory.ServiceType stationType) {
        return getWeather(stationType, GregorianCalendar.getInstance());
    }

    public Weather getWeather(WeatherStationFactory.ServiceType stationType, Calendar necessaryDate) {
        final Set<Calendar> dates = currPosition.getAllDates(stationType);
        if (dates.size() == 0) {
            return null;
        }
        final List<Calendar> sortedDates = new ArrayList<Calendar>(dates);
        Collections.sort(sortedDates);
        Calendar baseDate;
        Calendar dateAtList = null;
        for (int i = 0; i < sortedDates.size(); i++) {
            baseDate = sortedDates.get(i);
            if (necessaryDate.before(baseDate)) {
                if (i == 0) {
                    dateAtList = baseDate;
                } else {
                    Calendar prevDate = sortedDates.get(i - 1);
                    long diff1 = baseDate.getTimeInMillis() - necessaryDate.getTimeInMillis();
                    long diff2 = necessaryDate.getTimeInMillis() - prevDate.getTimeInMillis();
                    if (diff1 < diff2) {
                        dateAtList = baseDate;
                    } else {
                        dateAtList = prevDate;
                    }
                }
                break;
            }
        }
        if (dateAtList == null) {
            dateAtList = sortedDates.get(sortedDates.size() - 1);
        }
        return currPosition.getWeather(stationType, dateAtList);
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

    public Weather updateCurrent() {
 //       Coordinate coordinate = currPosition.getCoordinate();
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(55.75996355993382);
        coordinate.setLongitude(37.639561146497726);

        currStation.updateWeather(coordinate, this);

        return null;
    }

    public Weather updateHourly() {
        currStation.updateHourlyWeather(currPosition.getCoordinate(), this);
        return null;
    }

    public Weather updateWeekly() {
        currStation.updateWeeklyWeather(currPosition.getCoordinate(), this);
        return null;
    }

    private Position getPositionByCoordinate(Coordinate coordinate) {
        for (Position pos : positions.values()) {
            if (pos.getCoordinate().compareTo(coordinate) == 0) {
                return pos;
            }
        }
        return null;
    }

    public void onResponseReceived(Coordinate coordinate, Map<Calendar, Weather> weather) {
        /*
        Position position = getPositionByCoordinate(coordinate);
        // Позиция не обнаружена, выход
        if (position == null) {
            return;
        }
*/
        HashMap.Entry<Calendar, Weather> firstEntry = (Map.Entry<Calendar, Weather>) weather.entrySet().iterator().next();
        currPosition.setWeather(currStation.getServiceType(), firstEntry.getKey(), firstEntry.getValue());
        if (currPosition.getCoordinate().compareTo(coordinate) == 0) {
            mActivity.updateInterface(firstEntry.getValue());
        }
/*
        for (Map.Entry<Calendar, Weather> entry: weather.entrySet()) {
            currPosition.setWeather(currStation.getServiceType(), entry.getKey(), entry.getValue());
            mActivity.updateInterface(entry.getValue());
        }
*/
        // position.setWeather();

        //TODO Сообщить активити что бы она обновила свои данные
//        mActivity.updateInterface(position.getLocationName(), weather.getTemperature(), weather.getPrecipitation(),
//                weather.getPressure(), weather.getWindPower(), weather.getHumidity(), "");
    }

    // Установка режима отображения температуры
    double formatTemperature(double temperature) {

        switch (settingsTemperatureMetrics) {
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
    double formatSpeed(double speed) {
        switch (formatSpeedMetrics) {
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
    double formatPressure(double pressure) {
        switch (formatPressureMetrics) {
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
    public double kpaToMmHg(double pressure) {
        double mmHg = pressure / KPA_TO_MM_HG;
        return mmHg;
    }

}