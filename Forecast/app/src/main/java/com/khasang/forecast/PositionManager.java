package com.khasang.forecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.khasang.forecast.activities.WeatherActivity;
import com.khasang.forecast.sqlite.SQLiteProcessData;

import java.util.Calendar;
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

    private SharedPreferences preferences;
    private String[] positionsKey;

    public enum TemperatureMetrics {KELVIN, CELSIUS, FAHRENHEIT};
    public enum SpeedMetrics {METER_PER_SECOND, FOOT_PER_SECOND, KM_PER_HOURS, MILES_PER_HOURS};
    public enum PressureMetrics {HPA, MM_HG};

    TemperatureMetrics temperatureMetric;
    SpeedMetrics speedMetric;
    PressureMetrics pressureMetric;
    private WeatherStation currStation;
    private Position currPosition;
    private HashMap<WeatherStationFactory.ServiceType, WeatherStation> stations;
    private volatile HashMap<String, Position> positions;
    private WeatherActivity mActivity;
    private SQLiteProcessData dbManager;

    private static class ManagerHolder {
        private final static PositionManager instance = new PositionManager();
    }

    private PositionManager() { }

    public static PositionManager getInstance () {
        return ManagerHolder.instance;
    }

    public void initManager(WeatherActivity activity) {
        this.mActivity = activity;

        dbManager = new SQLiteProcessData(mActivity.context);
        temperatureMetric = dbManager.loadTemperatureMetrics();
        speedMetric = dbManager.loadSpeedMetrics();
        pressureMetric = dbManager.loadPressureMetrics();

        initStations();
        initPositions();
    }

    // Пока заглушка, потом настрки сохранять при их смене в настройках
    public void saveSettings() {
        saveCurrStation();
        saveMetrics();
    }

    public void saveMetrics () {
        dbManager.saveSettings(temperatureMetric, speedMetric, pressureMetric);
    }

    public void saveCurrPosition () {
        dbManager.saveSettings(currPosition);
    }

    public void saveCurrStation () {
        dbManager.saveSettings(currStation);
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
        positionFactory.addCurrentPosition();

        if (favorites.size() != 0) {
            for (HashMap.Entry<String, Coordinate> entry : favorites.entrySet()) {
                positionFactory.addFavouritePosition(entry.getKey(), entry.getValue());
            }
        }
        positions = positionFactory.getPositions();
        String currPositionName = dbManager.loadСurrentTown();
        if (!currPositionName.isEmpty() && positionIsPresent(currPositionName)) {
            currPosition = positions.get(currPositionName);
        }
    }

    /**
     * Метод, с помощью которого добавляем новую локацию в список "Избранных"
     * Вызывается когда пользователь добавляет новый город в список.
     *
     * @param name объект типа {@link String}, содержащий название города
     */
    public void addPosition(String name) {
        if (positionIsPresent(name)) {
            Toast.makeText(mActivity, "Город уже присутствует в списке", Toast.LENGTH_SHORT).show();
            return;
        }
        PositionFactory positionFactory = new PositionFactory(mActivity, positions);
        positionFactory.addFavouritePosition(name, dbManager);
        positions = positionFactory.getPositions();
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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
        if (currPosition == null) {
            return "";
        }
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
            dbManager.deleteTown(name);
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

    public boolean positionIsPresent(String name) {
        return positions.containsKey(name);
    }

    /**
     * Метод, вызывемый активити, для обновления текущей погоды от текущей погодной станции
     * @return weather  обьект типа {@link Weather}, содержащий погодные характеристики на ближайшую дату
     */
    public Weather getCurrentForecast() {
        // TODO: currPosition == null
//        if (!positionIsPresent(currPosition.getLocationName())) {
        if (currPosition == null || !positionIsPresent(currPosition.getLocationName())) {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nГород отсутствует в списке локаций.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isNetworkAvailable(mActivity)) {
            currStation.updateWeather(currPosition.getCityID(), currPosition.getCoordinate());
        } else {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nСеть недоступна.", Toast.LENGTH_SHORT).show();
        }
        return dbManager.loadWeather(currStation.serviceType, currPosition.getLocationName(), Calendar.getInstance());
    }

    /**
     * Метод, вызывемый активити, для обновления погоды на сутки
     *
     * @return контейнер, содержит погоду на ближайшие часы, типа {@link Map} содержащий обьекты класса {@link Weather}, передаваемые в качестве значения контейнера. Ключем контейнера является дата прогноза (объект класса {@link Calendar}).
     */
    public Map<Calendar, Weather> getHourlyForecast() {
        if (!positionIsPresent(currPosition.getLocationName())) {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nГород отсутствует в списке локаций.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isNetworkAvailable(mActivity)) {
            currStation.updateHourlyWeather(currPosition.getCityID(), currPosition.getCoordinate());
        } else {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nСеть недоступна.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * Метод, вызывемый активити, для обновления погоды на неделю
     *
     * @return контейнер, содержит погоду на ближайшие даты, типа {@link Map} содержащий обьекты класса {@link Weather}, передаваемые в качестве значения контейнера. Ключем контейнера является дата прогноза (объект класса {@link Calendar}).
     */
    public Map<Calendar, Weather> getDailyForecast() {
        // TODO: currPosition == null
//        if (!positionIsPresent(currPosition.getLocationName())) {
        if (currPosition == null || !positionIsPresent(currPosition.getLocationName())) {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nГород отсутствует в списке локаций.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (isNetworkAvailable(mActivity)) {
            currStation.updateWeeklyWeather(currPosition.getCityID(), currPosition.getCoordinate());
        } else {
            Toast.makeText(mActivity, "Ошибка обновления погоды.\nСеть недоступна.", Toast.LENGTH_SHORT).show();
        }
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

    /**
     * Метод, в который приходит ответ от станции на запрос погоды на сутки
     *
     * @param cityId внутренний идентификатор города, однозначно указывающая на локацию в списке
     * @param serviceType станция, от которой пришел прогноз погоды
     * @param weather контейнер типа {@link Map} содержащий обьекты класса {@link Weather}, передаваемые в качестве значения контейнера. Ключем контейнера является дата полученного запроса (объект класса {@link Calendar})
     */
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

    /**
     * Метод, в который приходит ответ от станции на недельный запрос погоды
     *
     * @param cityId внутренний идентификатор города, однозначно указывающая на локацию в списке
     * @param serviceType станция, от которой пришел прогноз погоды
     * @param weather контейнер типа {@link Map} содержащий обьекты класса {@link Weather}, передаваемые в качестве значения контейнера. Ключем контейнера является дата полученного запроса (объект класса {@link Calendar})
     */
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
    /**
     * Метод для преобразования погодных характеристик в заданные пользователями метрики
     *
     * @param weather обьект класса {@link Weather}, в котором нужно привести погодные характеристики к заданным метрикам
     * @return обьект класса {@link Weather} с преобразованными погодными характеристиками
     */
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
        switch (temperatureMetric) {
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
        switch (speedMetric) {
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
        switch (pressureMetric) {
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