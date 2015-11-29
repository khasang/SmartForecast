package com.khasang.forecast;

import android.content.Context;

import com.khasang.forecast.activity.WeatherActivity;

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

    public enum TemperatureMetrics {KELVIN, CELSIUS, FAHRENHEIT}

    public enum SpeedMetrics {METER_PER_SECOND, FOOT_PER_SECOND, KM_PER_HOURS, MILES_PER_HOURS}

    public enum PressureMetrics {HPA, MM_HG}

    TemperatureMetrics settingsTemperatureMetrics = TemperatureMetrics.KELVIN;
    SpeedMetrics formatSpeedMetrics = SpeedMetrics.METER_PER_SECOND;
    PressureMetrics formatPressureMetrics = PressureMetrics.HPA;

    private WeatherStation currStation;
    private Position currPosition;
    private HashMap<String, WeatherStation> stations;
    private Map<String, Position> positions;
    private Context mActivity;
    private String posName;

    public PositionManager(Context context) {
        posName = "Moscow";
        mActivity = context;
        ArrayList <String> pos = new ArrayList<>();
        pos.add(posName);
        initStations();         //  Пока тут
        initPositions(pos);     //  Пока тут
    }

    /**
     * Метод инициализации списка сервисов для получения информации о погодных условиях.
     */
    public void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf
                .addOpenWeatherMap()
                .create();
    }

    /**
     * Метод инициализации списка избранных местоположений (городов)
     * @param favorites коллекция {@link List} типа {@link String}, содержащий названия городов
     */
    public void initPositions(List<String> favorites) {
        PositionFactory positionFactory = new PositionFactory(mActivity);
        positionFactory.addCurrentPosition();
        for (String pos : favorites) {
            positionFactory.addFavouritePosition(pos);
        }
        positions = positionFactory.getPositions();
    }

    public void addFaVouritePosition(String name){
        PositionFactory factory = new PositionFactory(mActivity);
        factory.addFavouritePosition(name);
    }
    /**
     * Перегруженный метод, с помощью которого получаем сохраненные погодные данные от текущей станции, на текущую дату
     * @return обьект типа {@link Weather}
     */
    public Weather getWeather() {
        return getWeather(currStation.getServiceType());
    }

    /**
     * Перегруженный метод, с помощью которого получаем сохраненные погодные данные от заданной станции
     * @param stationType объект типа {@link com.khasang.forecast.WeatherStationFactory.ServiceType}, содержащий погодный сервис, с которого получены данные
     * @return обьект типа {@link Weather}
     */
    public Weather getWeather(WeatherStationFactory.ServiceType stationType) {
        return getWeather(stationType, GregorianCalendar.getInstance());
    }

    /**
     * Перегруженный метод, с помощью которого получаем сохраненные погодные данные от заданной станции  на заданную дату
     * @param stationType объект типа {@link com.khasang.forecast.WeatherStationFactory.ServiceType}, содержащий погодный сервис, с которого получены данные
     * @param necessaryDate объект типа {@link Calendar} - необходимо выбрать наиболее приближенные к этой дате погодные данные
     * @return обьект типа {@link Weather}
     */
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
                    Calendar prevDate = sortedDates.get(i-1);
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

    /**
     * Метод, с помощью которого получаем сохраненные погодные данные от текущей станции  на сутки
     * @return массив типа {@link Weather}
     */
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

    /**
     * Метод, с помощью которого получаем сохраненные погодные данные от текущей станции  на неделю
     * @return массив типа {@link Weather}
     */
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

    /**
     * Метод, вызывемый активити, для обновления текущей погоды от текущей погодной станции
     */
    public void updateCurrent() {
        currStation.updateWeather(currPosition.getCoordinate(), this);
    }

    /**
     * Метод, вызывемый активити, для обновления погоды на сутки
     */
    public void updateHourly() {
        currStation.updateHourlyWeather(currPosition.getCoordinate(), this);
    }

    /**
     * Метод, вызывемый активити, для обновления погоды на неделю
     */
    public void updateWeekly() {
        currStation.updateWeeklyWeather(currPosition.getCoordinate(), this);
    }

    /**
     * Пролучение локации из списка локаций
     * @param name объект типа {@link String}, содержащий названия города
     * @return обьект типа {@link Position}
     */
    public Position getPosition(String name) {
        return positions.get(name);
    }

    /**
     * Пролучение локации из списка локаций
     * @param coordinate объект типа {@link Coordinate}, указывающий на местоположение локации
     * @return обьект типа {@link Position}
     */
    private Position getPosition (Coordinate coordinate) {
        for (Position pos : positions.values()) {
            if (pos.getCoordinate().equals(coordinate)) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Метод для обновления погодных данных. Вызывается погодным сервисом, когда он получает актуальные данные
     * @param coordinate объект типа {@link Coordinate}, указывающий на местоположение локации для которой получены характеристики погодных условий
     * @param weather обьект типа {@link Weather}, содержащий погодные характеристики
     */
    public void onResponseReceived(Coordinate coordinate, Weather weather) {
        //TODO Положить данные в Position
        Position position = getPosition(coordinate);
        // position.setWeather();

        //TODO Сообщить активити что бы она обновила свои данные
//        mActivity.updateInterface(position.getLocationName(), weather.getTemperature(), weather.getPrecipitation(),
//                weather.getPressure(), weather.getWindPower(), weather.getHumidity(), "");
    }

    /**
     * Метод для преобразования температуры в заданную пользователем метрику
     * @param temperature температура на входе (в Кельвинах)
     * @return температура в выбранной пользователем метрике
     */
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

    /**
     * Метод для преобразования скорости ветра в заданную пользователем метрику
     * @param speed преобразуемая скорость
     * @return скорость в выбранной пользователем метрике
     */
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

    /**
     * Метод для преобразования давления в заданную пользователем метрику
     * @param pressure преобразуемое давление
     * @return давление в выбранной пользователем метрике
     */
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

    // Установить текущую позицию
    public void setCurrPosition(String name){
        currPosition = getPosition(name);
    }


    //
    public Position getCurrPosition(){
        return currPosition;
    }
}