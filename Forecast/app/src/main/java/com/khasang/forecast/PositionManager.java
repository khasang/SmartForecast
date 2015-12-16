package com.khasang.forecast;

import android.content.Context;
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
    public enum TemperatureMetrics {
        KELVIN {
            public TemperatureMetrics change() {return CELSIUS;}
        },
        CELSIUS{
            public TemperatureMetrics change() {return FAHRENHEIT;}
        },
        FAHRENHEIT{
            public TemperatureMetrics change() {return KELVIN;}
        };
        public abstract TemperatureMetrics change();
    }
    public enum SpeedMetrics {METER_PER_SECOND, FOOT_PER_SECOND, KM_PER_HOURS, MILES_PER_HOURS}
    public enum PressureMetrics {HPA, MM_HG}


    TemperatureMetrics temperatureMetric;
    SpeedMetrics speedMetric;
    PressureMetrics pressureMetric;
    private WeatherStation currStation;
    private Position currPosition;
    private HashMap<WeatherStationFactory.ServiceType, WeatherStation> stations;
    private volatile HashMap<String, Position> positions;
    private WeatherActivity mActivity;
    private SQLiteProcessData dbManager;
    private boolean lastResponseIsFailure;

    private static class ManagerHolder {
        private final static PositionManager instance = new PositionManager();
    }

    private PositionManager() {
        lastResponseIsFailure = false;
    }

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
            Toast.makeText(mActivity, R.string.city_exist, Toast.LENGTH_SHORT).show();
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
        dbManager.deleteTowns();
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

    public TemperatureMetrics changeTemperatureMetric () {
        temperatureMetric = temperatureMetric.change();
        return temperatureMetric;
    }

    /**
     * Метод, вызывемый активити, для обновления текущей погоды от текущей погодной станции
     */
    public void updateWeather () {
        if (currPosition == null || !positionIsPresent(currPosition.getLocationName())) {
            Toast.makeText(mActivity, R.string.update_error_location_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNetworkAvailable(mActivity)) {
            currStation.updateWeather(currPosition.getCityID(), currPosition.getCoordinate());
            currStation.updateHourlyWeather(currPosition.getCityID(), currPosition.getCoordinate());
            currStation.updateWeeklyWeather(currPosition.getCityID(), currPosition.getCoordinate());
        } else {
            mActivity.updateInterface(WeatherStation.ResponseType.CURRENT, getCurrentWeatherFromDB(currStation.getServiceType(), currPosition.getLocationName(), Calendar.getInstance()));
            mActivity.updateInterface(WeatherStation.ResponseType.HOURLY, getHourlyWeatherFromDB(currStation.getServiceType(), currPosition.getLocationName(), Calendar.getInstance()));
            mActivity.updateInterface(WeatherStation.ResponseType.DAILY, getDailyWeatherFromDB(currStation.getServiceType(), currPosition.getLocationName(), Calendar.getInstance()));
            Toast.makeText(mActivity, R.string.update_error_net_not_availble, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Метод для обновления погодных данных. Вызывается погодным сервисом, когда он получает актуальные данные
     *
     * @param rType       переменая типа {@link com.khasang.forecast.WeatherStation.ResponseType}, характеризующая тип ответа (текущий прогноз, прогноз на день или неделю)
     * @param cityId      внутренний идентификатор города, передается в погодную станцию во время запроса погоды
     * @param serviceType идентификатор погодного сервиса
     * @param weather     обьект типа {@link Weather}, содержащий погодные характеристики
     */
    public void onResponseReceived(WeatherStation.ResponseType rType, int cityId, WeatherStationFactory.ServiceType serviceType, Map<Calendar, Weather> weather) {
        lastResponseIsFailure = false;
        Position position = getPosition(cityId);
        if (position != null) {
            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                if (rType == WeatherStation.ResponseType.CURRENT){
                    dbManager.deleteOldWeather(serviceType, position.getLocationName(), entry.getKey());
                }
                dbManager.saveWeather(serviceType, position.getLocationName(), entry.getKey(), entry.getValue());
            }
        }
        if (currPosition.getCityID() == cityId && currStation.getServiceType() == serviceType) {
            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                entry.setValue(AppUtils.formatWeather(entry.getValue(), temperatureMetric, speedMetric, pressureMetric));
            }
            mActivity.updateInterface(rType, weather);
        }
    }

    public void onFailureResponse(int cityID, String weatherStationName, WeatherStationFactory.ServiceType sType) {
        if (!lastResponseIsFailure) {
            Toast.makeText(mActivity, mActivity.getString(R.string.update_error_from) + weatherStationName, Toast.LENGTH_SHORT).show();
            lastResponseIsFailure = true;
            //  Вернуть это в интерфейс ближайшую погоду
            mActivity.updateInterface(WeatherStation.ResponseType.CURRENT, getCurrentWeatherFromDB(sType, getPosition(cityID).getLocationName(), Calendar.getInstance()));
            mActivity.updateInterface(WeatherStation.ResponseType.HOURLY, getHourlyWeatherFromDB(sType, getPosition(cityID).getLocationName(), Calendar.getInstance()));
            mActivity.updateInterface(WeatherStation.ResponseType.DAILY, getDailyWeatherFromDB(sType, getPosition(cityID).getLocationName(), Calendar.getInstance()));
        }
    }

    private HashMap<Calendar, Weather> getCurrentWeatherFromDB(WeatherStationFactory.ServiceType sType, String locationName, Calendar date) {
        return dbManager.loadWeather(sType, locationName, date, temperatureMetric, speedMetric, pressureMetric);
    }

    private HashMap<Calendar, Weather> getHourlyWeatherFromDB(WeatherStationFactory.ServiceType sType, String locationName, Calendar date) {
        final int HOUR_PERIOD = 3;
        final int FORECASTS_COUNT = 8;
        Calendar calendar = date;
        calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
        calendar.set(Calendar.MINUTE, 0);
        HashMap <Calendar, Weather> forecast = new HashMap<>();
        for (int i = 0; i < FORECASTS_COUNT; i++) {
            HashMap<Calendar, Weather> temp = dbManager.loadWeather(sType, locationName, calendar, temperatureMetric, speedMetric, pressureMetric);
            if (temp == null || temp.size() == 0) {
                return null;
            }
            forecast.putAll(temp);
            calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
        }
        return forecast;
    }


    private HashMap<Calendar, Weather> getDailyWeatherFromDB(WeatherStationFactory.ServiceType sType, String locationName, Calendar date) {
        final int DAY_PERIOD = 1;
        final int FORECASTS_COUNT = 7;
        Calendar calendar = date;
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        HashMap <Calendar, Weather> forecast = new HashMap<>();
        for (int i = 0; i < FORECASTS_COUNT; i++) {
            HashMap<Calendar, Weather> temp = dbManager.loadWeather(sType, locationName, calendar, temperatureMetric, speedMetric, pressureMetric);
            if (temp == null || temp.size() == 0) {
                return null;
            }
            forecast.putAll(temp);
            calendar.add(Calendar.DAY_OF_YEAR, DAY_PERIOD);
        }
        return forecast;
    }
}