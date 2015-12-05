package com.khasang.forecast;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.khasang.forecast.activities.WeatherActivity;
import com.khasang.forecast.sqlite.SQLiteProcessData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionManager {
    public static final double KELVIN_CELSIUS_DELTA = 273.15;
    public static final double KPA_TO_MM_HG = 1.33322;
    public static final double KM_TO_MILES = 0.62137;
    public static final double METER_TO_FOOT = 3.28083;
    public static final String MY_PREFF = "MY_PREFF";

    public Context context;

    private SharedPreferences preferences;
    private String[] positionsKey;

    public enum TemperatureMetrics {KELVIN, CELSIUS, FAHRENHEIT}

    public enum SpeedMetrics {METER_PER_SECOND, FOOT_PER_SECOND, KM_PER_HOURS, MILES_PER_HOURS}

    public enum PressureMetrics {HPA, MM_HG}

    TemperatureMetrics settingsTemperatureMetrics;
    SpeedMetrics formatSpeedMetrics;
    PressureMetrics formatPressureMetrics;

    private WeatherStation currStation;     //TODO loadStation
    private Position currPosition;
    private HashMap<WeatherStationFactory.ServiceType, WeatherStation> stations;
    private volatile HashMap<String, Position> positions;
    private WeatherActivity mActivity;
    private SQLiteProcessData dbManager;

    private static PositionManager instance = new PositionManager();

    private PositionManager() { }

    public static PositionManager getInstance() {
        return instance;
    }

    public void initManager(WeatherActivity activity) {
        this.mActivity = activity;

        dbManager = new SQLiteProcessData(mActivity.context);
        settingsTemperatureMetrics = dbManager.loadTemperatureMetrics();
        formatSpeedMetrics = dbManager.loadSpeedMetrics();
        formatPressureMetrics = dbManager.loadPressureMetrics();

        initStations();
        initPositions();
    }

    /**
     * Метод инициализации списка сервисов для получения информации о погодных условиях.
     */
    private void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf
                .addOpenWeatherMap(mActivity.getString(R.string.service_name_open_weather_map))
                .create();
        currStation = stations.get(dbManager.loadStation());
    }

    /**
     * Метод инициализации списка местоположений, вызывается из активити
     */
    private void initPositions() {
        //loadPreferences();    Здесь загружать список городов
        HashMap<String, Coordinate> pos = dbManager.loadTownList();
        initPositions(pos);
    }

    /**
     * Метод инициализации списка местоположений, которые добавлены в "Избранное" (городов)
     *
     * @param favorites коллекция {@link List} типа {@link String}, содержащий названия городов
     */
    private void initPositions(HashMap<String, Coordinate> favorites) {
        PositionFactory positionFactory = new PositionFactory(mActivity.getApplicationContext());
        positionFactory.addCurrentPosition(stations.keySet());
        for (HashMap.Entry<String, Coordinate> entry : favorites.entrySet()) {
            positionFactory.addFavouritePosition(entry.getKey(), entry.getValue());
        }
        positions = positionFactory.getPositions();
        HashMap.Entry<String, Coordinate> firstEntry = (Map.Entry<String, Coordinate>) favorites.entrySet().iterator().next();;
        currPosition = positions.get(firstEntry.getKey());
    }

    /**
     * Метод, с помощью которого добавляем новую локацию в список "Избранных"
     * Вызывается когда пользователь добавляет новый город в список.
     *
     * @param name объект типа {@link String}, содержащий название города
     */
    public void addPosition(String name) {
        PositionFactory positionFactory = new PositionFactory(mActivity, positions);
        positionFactory.addFavouritePosition(name, dbManager);
        positions = positionFactory.getPositions();
    }

    /**
     * Метод, с помощью которого получаем список местоположений
     * Вызывается когда пользователь открывает диалог городов
     *
     * @return возвращает коллекцию {@link Set} типа {@link String}, содержащий названия городов
     */
    public Set<String> getPositions() {
        return positions.keySet();
    }

    /**
     * Метод, с помощью которого получаем название рекущей локации
     *
     * @return возвращает {@link String}, содержащий названия города
     */
    public String getCurrentPositionName() {
        return currPosition.getLocationName();
    }

    /**
     * Метод, с помощью которого удаляем локацию в списка "Избранных"
     * Вызывается, когда пользователь удяляет город из списка
     *
     * @param name объект типа {@link String}, содержащий название города
     */
    public void removePosition(String name) {
        if (positions.containsKey(name)) {
            positions.remove(name);
        }
    }

    public void removePositions() {
        positions.clear();
    }

    /**
     * Метод, с помощью которого из списока "Избранных" выбираем другую локацию в качестве текущей
     *
     * @param name объект типа {@link String}, содержащий название города
     */
    public void setCurrentPosition(String name) {
        if (positions.containsKey(name)) {
            currPosition = positions.get(name);
        }
    }

//    /**
//     * Запись настроек выбора параметров и ключей
//     */
//    protected void savePreferences() {
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("temp", settingsTemperatureMetrics.toString());
//        editor.putString("speed", formatSpeedMetrics.toString());
//        editor.putString("pressure", formatPressureMetrics.toString());
//        int i = 0;
//        for (Map.Entry<String, Position> entry : positions.entrySet()) {
//            editor.putString("" + i++, entry.getKey());
//        }
//        editor.commit();
//    }

//    /**
//     * Чтение настроек выбора параметров и ключей, второе значение по умолчанию
//     */
//    private void loadPreferences() {
//        preferences = mActivity.getSharedPreferences(MY_PREFF, Activity.MODE_PRIVATE);
//        String temp = preferences.getString("temp", "KELVIN");
//        String speed = preferences.getString("speed", "METER_PER_SECOND");
//        String pressure = preferences.getString("pressure", "HPA");
//        positionsKey = new String[positions.size()];
//        for (int i = 0; i < positions.size(); i++) {
//            positionsKey[i] = preferences.getString("i", "");
//        }
//    }
//
//    /**
//     * Перегруженный метод, с помощью которого получаем сохраненные погодные данные от текущей станции, на текущую дату
//     *
//     * @return обьект типа {@link Weather}
//     */
//    public Weather getWeather() {
//        return getWeather(currStation.getServiceType());
//    }
//
//    /**
//     * Перегруженный метод, с помощью которого получаем сохраненные погодные данные от заданной станции
//     *
//     * @param stationType объект типа {@link com.khasang.forecast.WeatherStationFactory.ServiceType}, содержащий погодный сервис, с которого получены данные
//     * @return обьект типа {@link Weather}
//     */
//    public Weather getWeather(WeatherStationFactory.ServiceType stationType) {
//        return getWeather(stationType, GregorianCalendar.getInstance());
//    }
//
//    /**
//     * Перегруженный метод, с помощью которого получаем сохраненные погодные данные от заданной станции  на заданную дату
//     *
//     * @param stationType   объект типа {@link com.khasang.forecast.WeatherStationFactory.ServiceType}, содержащий погодный сервис, с которого получены данные
//     * @param necessaryDate объект типа {@link Calendar} - необходимо выбрать наиболее приближенные к этой дате погодные данные
//     * @return обьект типа {@link Weather}
//     */
//    public Weather getWeather(WeatherStationFactory.ServiceType stationType, Calendar necessaryDate) {
//        final Set<Calendar> dates = currPosition.getAllDates(stationType);
//        if (dates.size() == 0) {
//            return null;
//        }
//        final List<Calendar> sortedDates = new ArrayList<Calendar>(dates);
//        Collections.sort(sortedDates);
//        Calendar baseDate;
//        Calendar dateAtList = null;
//        for (int i = 0; i < sortedDates.size(); i++) {
//            baseDate = sortedDates.get(i);
//            if (necessaryDate.before(baseDate)) {
//                if (i == 0) {
//                    dateAtList = baseDate;
//                } else {
//                    Calendar prevDate = sortedDates.get(i - 1);
//                    long diff1 = baseDate.getTimeInMillis() - necessaryDate.getTimeInMillis();
//                    long diff2 = necessaryDate.getTimeInMillis() - prevDate.getTimeInMillis();
//                    if (diff1 < diff2) {
//                        dateAtList = baseDate;
//                    } else {
//                        dateAtList = prevDate;
//                    }
//                }
//                break;
//            }
//        }
//        if (dateAtList == null) {
//            dateAtList = sortedDates.get(sortedDates.size() - 1);
//        }
//        return currPosition.getWeather(stationType, dateAtList);
//    }
//
//    /**
//     * Метод, с помощью которого получаем сохраненные погодные данные от текущей станции  на сутки
//     *
//     * @return массив типа {@link Weather}
//     */
//    public Weather[] getHourlyWeather() {
//        final int HOUR_PERIOD = 4;
//        Calendar calendar = GregorianCalendar.getInstance();
//        calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
//        calendar.set(Calendar.MINUTE, 0);
//        Weather[] weather = new Weather[7];
//        for (int i = 0; i < 7; i++) {
//            weather[i] = getWeather(currStation.getServiceType(), calendar);
//            calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
//        }
//        return weather;
//    }
//
//    /**
//     * Метод, с помощью которого получаем сохраненные погодные данные от текущей станции  на неделю
//     *
//     * @return массив типа {@link Weather}
//     */
//    public Weather[] getWeeklyWeather() {
//        Weather[] weather = new Weather[7];
//        Calendar calendar = GregorianCalendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 12);
//        calendar.set(Calendar.MINUTE, 0);
//        for (int i = 0; i < 7; i++) {
//            weather[i] = getWeather(currStation.getServiceType(), calendar);
//            calendar.add(Calendar.DAY_OF_YEAR, 1);
//        }
//        return weather;
//    }

    public boolean positionIsPresent(String name) {
        return positions.containsKey(name);
    }

    /**
     * Метод, вызывемый активити, для обновления текущей погоды от текущей погодной станции
     */
    public Weather getCurrentForecast() {
        // TODO: currPosition == null
//        if (!positionIsPresent(currPosition.getLocationName())) {
        if (currPosition == null || !positionIsPresent(currPosition.getLocationName())) {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nГород отсутствует в списке локаций.", Toast.LENGTH_SHORT).show();
            return null;
        }
        currStation.updateWeather(currPosition.getCityID(), currPosition.getCoordinate());
        return dbManager.loadWeather(currStation.serviceType, currPosition.getLocationName(), Calendar.getInstance().getTime());
    }

    /**
     * Метод, вызывемый активити, для обновления погоды на сутки
     */
    public Map<Calendar, Weather> getHourlyForecast() {
        if (!positionIsPresent(currPosition.getLocationName())) {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nГород отсутствует в списке локаций.", Toast.LENGTH_SHORT).show();
            return null;
        }
        currStation.updateHourlyWeather(currPosition.getCityID(), currPosition.getCoordinate());
        return null;
    }

    /**
     * Метод, вызывемый активити, для обновления погоды на неделю
     */
    public Map<Calendar, Weather> getDailyForecast() {
        // TODO: currPosition == null
//        if (!positionIsPresent(currPosition.getLocationName())) {
        if (currPosition == null || !positionIsPresent(currPosition.getLocationName())) {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nГород отсутствует в списке локаций.", Toast.LENGTH_SHORT).show();
            return null;
        }
        currStation.updateWeeklyWeather(currPosition.getCityID(), currPosition.getCoordinate());
        return null;    // Возвращать ближайшую погоду
    }

    /**
     * Пролучение локации из списка локаций
     *
     * @param name объект типа {@link String}, хранящий название населенного пункта
     * @return обьект типа {@link Position}
     */
    public Position getPosition(String name) {
        return positions.get(name);
    }

    /**
     * Пролучение локации из списка локаций
     *
     * @param coordinate объект типа {@link Coordinate}, указывающий на местоположение локации
     * @return обьект типа {@link Position}
     */
    private Position getPosition(Coordinate coordinate) {
        for (Position pos : positions.values()) {
            if (pos.getCoordinate().compareTo(coordinate) == 0) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Пролучение локации из списка локаций
     *
     * @param cityID идентификатор местоположения
     * @return обьект типа {@link Position}
     */
    private Position getPosition(int cityID) {
        for (Position pos : positions.values()) {
            if (pos.getCityID() == cityID) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Метод для обновления погодных данных. Вызывается погодным сервисом, когда он получает актуальные данные
     *
     * @param cityId      внутренний идентификатор города, передается в погодную станцию во время запроса погоды
     * @param serviceType идентификатор погодного сервиса
     * @param weather     обьект типа {@link Weather}, содержащий погодные характеристики
     */
    public void onResponseReceived(int cityId, WeatherStationFactory.ServiceType serviceType, Map<Calendar, Weather> weather) {
        HashMap.Entry<Calendar, Weather> firstEntry = (Map.Entry<Calendar, Weather>) weather.entrySet().iterator().next();
        Position position = getPosition(cityId);
        if (position != null) {
            dbManager.saveWeather(serviceType, position.getLocationName(), firstEntry.getKey(), firstEntry.getValue());
        }
        if (currPosition.getCityID() == cityId && currStation.getServiceType() == serviceType) {
            mActivity.updateInterface(firstEntry.getKey(), formatWeather(firstEntry.getValue()));
        }
    }

    public void onHourlyResponseReceived(int cityId, WeatherStationFactory.ServiceType serviceType, Map<Calendar, Weather> weather) {
        Position position = getPosition(cityId);
        if (position != null) {
            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                dbManager.saveWeather(serviceType, position.getLocationName(), entry.getKey(), entry.getValue());
            }
        }
        if (currPosition.getCityID() == cityId && currStation.getServiceType() == serviceType) {
            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                entry.setValue(formatWeather(entry.getValue()));                                    //Есди позиция и станция теккущие
            }                                                                                       //Преобразуем погодные данные в нужные метрики
            mActivity.updateHourForecast(weather);                                                  //Отправляем данные в интерфейс
        }
    }

    public void onDaylyResponseReceived(int cityId, WeatherStationFactory.ServiceType serviceType, Map<Calendar, Weather> weather) {
        Position position = getPosition(cityId);
        if (position != null) {
            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                dbManager.saveWeather(serviceType, position.getLocationName(), entry.getKey(), entry.getValue());
            }
        }
        if (currPosition.getCityID() == cityId && currStation.getServiceType() == serviceType) {
            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                entry.setValue(formatWeather(entry.getValue()));
            }
            mActivity.updateDayForecast(weather);
        }
    }

    //region Вспомогательные методы

    private Weather formatWeather(Weather weather) {
        weather.setTemperature(formatTemperature(weather.getTemperature()));
        weather.setPressure(formatPressure(weather.getPressure()));
        weather.setWind(weather.getWindDirection(), formatSpeed(weather.getWindPower()));
        return weather;
    }

    /**
     * Метод для преобразования температуры в заданную пользователем метрику
     *
     * @param temperature температура на входе (в Кельвинах)
     * @return температура в выбранной пользователем метрике
     */
    double formatTemperature(double temperature) {
        switch (settingsTemperatureMetrics) {
            case KELVIN:
                break;
            case CELSIUS:
                return kelvinToCelsius(temperature);
            case FAHRENHEIT:
                return kelvinToFahrenheit(temperature);
        }
        return temperature;
    }

    /**
     * Метод для преобразования скорости ветра в заданную пользователем метрику
     *
     * @param speed преобразуемая скорость
     * @return скорость в выбранной пользователем метрике
     */
    double formatSpeed(double speed) {
        switch (formatSpeedMetrics) {
            case METER_PER_SECOND:
                break;
            case FOOT_PER_SECOND:
                return meterInSecondToFootInSecond(speed);
            case KM_PER_HOURS:
                return meterInSecondToKmInHours(speed);
            case MILES_PER_HOURS:
                return meterInSecondToMilesInHour(speed);
        }
        return speed;
    }

    /**
     * Метод для преобразования давления в заданную пользователем метрику
     *
     * @param pressure преобразуемое давление
     * @return давление в выбранной пользователем метрике
     */
    double formatPressure(double pressure) {
        switch (formatPressureMetrics) {
            case HPA:
                break;
            case MM_HG:
                return kpaToMmHg(pressure);
        }
        return pressure;
    }

    /**
     * Метод для преобразования температуры из Кельвинов в Цельсии
     *
     * @param temperature температура в Кельвинах
     * @return температура в Цельсиях
     */
    public double kelvinToCelsius(double temperature) {
        double celsiusTemperature = temperature - KELVIN_CELSIUS_DELTA;
        return celsiusTemperature;
    }

    /**
     * Метод для преобразования температуры из Кельвина в Фаренгейт
     *
     * @param temperature температура в Кельвинах
     * @return температура в Фаренгейтах
     */
    public double kelvinToFahrenheit(double temperature) {
        double fahrenheitTemperature = (kelvinToCelsius(temperature) * 9 / 5) + 32;
        return fahrenheitTemperature;
    }

    /**
     * Метод для преобразования скорости ветра из метров в секунду в футы в секунду
     *
     * @param speed скорость ветра в метрах в секунду
     * @return скорость ветра в футах в секунду
     */
    public double meterInSecondToFootInSecond(double speed) {
        double footInSecond = speed * METER_TO_FOOT;
        return footInSecond;
    }

    /**
     * Метод для преобразования скорости ветра из метров в секунду в километры в час
     *
     * @param speed скорость ветра в метрах в секунду
     * @return скорость ветра в километрах в час
     */
    public double meterInSecondToKmInHours(double speed) {
        double kmInHours = speed * 3.6;
        return kmInHours;
    }

    /**
     * Метод для преобразования скорости ветра из метров в секунду в мили в час
     *
     * @param speed скорость ветра в метрах в секунду
     * @return скорость ветра в милях в час
     */
    public double meterInSecondToMilesInHour(double speed) {
        double milesInHours = meterInSecondToKmInHours(speed) * KM_TO_MILES;
        return milesInHours;
    }

    /**
     * Метод для преобразования давления из килопаскалей в мм.рт.ст.
     *
     * @param pressure давление в килопаскалях
     * @return давление в мм.рт.ст.
     */
    public double kpaToMmHg(double pressure) {
        double mmHg = pressure / KPA_TO_MM_HG;
        return mmHg;
    }
    //endregion
}