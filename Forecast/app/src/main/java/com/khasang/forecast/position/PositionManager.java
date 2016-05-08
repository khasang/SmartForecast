package com.khasang.forecast.position;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.PermissionChecker;
import com.khasang.forecast.R;
import com.khasang.forecast.exceptions.AccessFineLocationNotGrantedException;
import com.khasang.forecast.exceptions.GpsIsDisabledException;
import com.khasang.forecast.exceptions.NoAvailableLocationServiceException;
import com.khasang.forecast.interfaces.IMessageProvider;
import com.khasang.forecast.interfaces.IWeatherReceiver;
import com.khasang.forecast.location.CurrentLocationManager;
import com.khasang.forecast.exceptions.EmptyCurrentAddressException;
import com.khasang.forecast.location.LocationParser;
import com.khasang.forecast.exceptions.NoAvailableAddressesException;
import com.khasang.forecast.sqlite.SQLiteProcessData;
import com.khasang.forecast.stations.WeatherStation;
import com.khasang.forecast.stations.WeatherStationFactory;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.meteocons_typeface_library.Meteoconcs;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionManager {

    AppUtils.TemperatureMetrics temperatureMetric;
    AppUtils.SpeedMetrics speedMetric;
    AppUtils.PressureMetrics pressureMetric;
    private WeatherStation currStation;
    private volatile Position activePosition;            // Здесь лежит "активная" позиция (которая на данный момент активна на экране)
    private HashMap<WeatherStationFactory.ServiceType, WeatherStation> stations;
    private volatile Position currentLocation; // Здесь лежит текущая по местоположению локация (там где находится пользователь)
    private volatile HashMap<String, Position> positions;
    List<String> favouritesPositions;
    private volatile IWeatherReceiver receiver;
    private volatile IMessageProvider messageProvider;
    private SQLiteProcessData dbManager;
    private boolean lastResponseIsFailure;
    private CurrentLocationManager locationManager;

    //Main icons set
    private static final int MAIN_ICONS_COUNT = 10;

    //Extended icons set
    private static final int EXTENDED_ICONS_COUNT = 10;

    public static final int ICON_INDEX_THUNDERSTORM_LIGHT_RAIN = 0;
    public static final int ICON_INDEX_THUNDERSTORM_RAIN = 1;
    public static final int ICON_INDEX_THUNDERSTORM_HEAVY_RAIN = 2;
    public static final int ICON_INDEX_LIGHT_THUNDERSTORM = 3;
    public static final int ICON_INDEX_THUNDERSTORM = 4;
    public static final int ICON_INDEX_HEAVY_THUNDERSTORM = 5;
    public static final int ICON_INDEX_RAGGED_THUNDERSTORM = 6;
    public static final int ICON_INDEX_THUNDERSTORM_LIGHT_DRIZZLE = 7;
    public static final int ICON_INDEX_THUNDERSTORM_DRIZZLE = 8;
    public static final int ICON_INDEX_THUNDERSTORM_HEAVY_DRIZZLE = 9;

    public static final int ICON_INDEX_LIGHT_INTENSITY_DRIZZLE = 10;
    public static final int ICON_INDEX_DRIZZLE = 11;
    public static final int ICON_INDEX_HEAVY_INTENSITY_DRIZZLE = 12;
    public static final int ICON_INDEX_LIGHT_INTENSITY_DRIZZLE_RAIN = 13;
    public static final int ICON_INDEX_DRIZZLE_RAIN = 14;
    public static final int ICON_INDEX_HEAVY_INTENSITY_DRIZZLE_RAIN = 15;
    public static final int ICON_INDEX_SHOWER_RAIN_AND_DRIZZLE = 16;
    public static final int ICON_INDEX_HEAVY_SHOWER_RAIN_AND_DRIZZLE = 17;
    public static final int ICON_INDEX_SHOWER_DRIZZLE = 18;

    public static final int ICON_INDEX_LIGHT_RAIN = 19;
    public static final int ICON_INDEX_MODERATE_RAIN = 20;
    public static final int ICON_INDEX_HEAVY_INTENSITY_RAIN = 21;
    public static final int ICON_INDEX_VERY_HEAVY_RAIN = 22;
    public static final int ICON_INDEX_EXTREME_RAIN = 23;
    public static final int ICON_INDEX_FREEZING_RAIN = 24;
    public static final int ICON_INDEX_LIGHT_INTENSITY_SHOWER_RAIN = 25;
    public static final int ICON_INDEX_SHOWER_RAIN = 26;
    public static final int ICON_INDEX_HEAVY_INTENSITY_SHOWER_RAIN = 27;
    public static final int ICON_INDEX_RAGGED_SHOWER_RAIN = 28;

    public static final int ICON_INDEX_LIGHT_SNOW = 29;
    public static final int ICON_INDEX_SNOW = 30;
    public static final int ICON_INDEX_HEAVY_SNOW = 31;
    public static final int ICON_INDEX_SLEET = 32;
    public static final int ICON_INDEX_SHOWER_SLEET = 33;
    public static final int ICON_INDEX_LIGHT_RAIN_AND_SNOW = 34;
    public static final int ICON_INDEX_RAIN_AND_SNOW = 35;
    public static final int ICON_INDEX_LIGHT_SHOWER_SNOW = 36;
    public static final int ICON_INDEX_SHOWER_SNOW = 37;
    public static final int ICON_INDEX_HEAVY_SHOWER_SNOW = 38;

    public static final int ICON_INDEX_MIST = 39;
    public static final int ICON_INDEX_SMOKE = 40;
    public static final int ICON_INDEX_HAZE = 41;
    public static final int ICON_INDEX_SAND_DUST_WHIRLS = 42;
    public static final int ICON_INDEX_FOG = 43;
    public static final int ICON_INDEX_SAND = 44;
    public static final int ICON_INDEX_DUST = 45;
    public static final int ICON_INDEX_VOLCANIC_ASH = 46;
    public static final int ICON_INDEX_SQUALLS = 47;
    public static final int ICON_INDEX_TORNADO = 48;

    public static final int ICON_INDEX_CLEAR_SKY_SUN = 49;
    public static final int ICON_INDEX_CLEAR_SKY_MOON = 50;

    public static final int ICON_INDEX_FEW_CLOUDS = 51;
    public static final int ICON_INDEX_SCATTERED_CLOUDS = 52;
    public static final int ICON_INDEX_BROKEN_CLOUDS = 53;
    public static final int ICON_INDEX_OVERCAST_CLOUDS = 54;

    public static final int ICON_INDEX_EXTREME_TORNADO = 55;
    public static final int ICON_INDEX_EXTREME_TROPICAL_STORM = 56;
    public static final int ICON_INDEX_EXTREME_HURRICANE = 57;
    public static final int ICON_INDEX_EXTREME_COLD = 58;
    public static final int ICON_INDEX_EXTREME_HOT = 59;
    public static final int ICON_INDEX_EXTREME_WINDY = 60;
    public static final int ICON_INDEX_EXTREME_HAIL = 61;

    public static final int ICON_INDEX_CALM = 62;
    public static final int ICON_INDEX_LIGHT_BREEZE = 63;
    public static final int ICON_INDEX_GENTLE_BREEZE = 64;
    public static final int ICON_INDEX_MODERATE_BREEZE = 65;
    public static final int ICON_INDEX_FRESH_BREEZE = 66;
    public static final int ICON_INDEX_STRONG_BREEZE = 67;
    public static final int ICON_INDEX_HIGH_WIND_NEAR_GALE = 67;
    public static final int ICON_INDEX_GALE = 68;
    public static final int ICON_INDEX_SEVERE_GALE = 69;
    public static final int ICON_INDEX_STORM = 70;
    public static final int ICON_INDEX_VIOLENT_STORM = 71;
    public static final int ICON_INDEX_HURRICANE = 72;

    public static final int ICON_INDEX_NA = 73;

    private Drawable[] iconsSet;

    public void createIconsSet (Context context) {
        iconsSet = new Drawable[MAIN_ICONS_COUNT];
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String iconSet = sp.getString(context.getString(R.string.pref_icons_set_key), context.getString(R.string.pref_icons_set_default));
        if (iconSet.equals(context.getString(R.string.pref_icons_set_mike))) {
            iconsSet[ICON_INDEX_THUNDERSTORM] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_cloud_flash);
            iconsSet[ICON_INDEX_DRIZZLE] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_drizzle);
            iconsSet[ICON_INDEX_MODERATE_RAIN] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_rain);
            iconsSet[ICON_INDEX_SNOW] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_snow_heavy);
            iconsSet[ICON_INDEX_FOG] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_fog);
            iconsSet[ICON_INDEX_CLEAR_SKY_SUN] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_sun);
            iconsSet[ICON_INDEX_CLEAR_SKY_MOON] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_moon);
            iconsSet[ICON_INDEX_FEW_CLOUDS] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_cloud);
            iconsSet[ICON_INDEX_EXTREME_TORNADO] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_clouds_flash);
        } else {
            iconsSet[ICON_INDEX_THUNDERSTORM] = ContextCompat.getDrawable(context, R.drawable.ic_thunderstorm);
            iconsSet[ICON_INDEX_DRIZZLE] = ContextCompat.getDrawable(context, R.drawable.ic_drizzle);
            iconsSet[ICON_INDEX_MODERATE_RAIN] = ContextCompat.getDrawable(context, R.drawable.ic_rain);
            iconsSet[ICON_INDEX_SNOW] = ContextCompat.getDrawable(context, R.drawable.ic_snow);
            iconsSet[ICON_INDEX_FOG] = ContextCompat.getDrawable(context, R.drawable.ic_fog);
            iconsSet[ICON_INDEX_CLEAR_SKY_SUN] = ContextCompat.getDrawable(context, R.drawable.ic_sun);
            iconsSet[ICON_INDEX_CLEAR_SKY_MOON] = ContextCompat.getDrawable(context, R.drawable.ic_moon);
            iconsSet[ICON_INDEX_FEW_CLOUDS] = ContextCompat.getDrawable(context, R.drawable.ic_cloud);
            iconsSet[ICON_INDEX_EXTREME_TORNADO] = ContextCompat.getDrawable(context, R.drawable.ic_extreme);
        }
        iconsSet[ICON_INDEX_NA] = new IconicsDrawable(context)
                .icon(Meteoconcs.Icon.met_na);
    }

    public Drawable getWeatherIcon (int iconNumber)
    {
        if (iconsSet[iconNumber] != null) {
            return iconsSet[iconNumber];
        } else {
            return iconsSet[ICON_INDEX_NA];
        }
    }

    public synchronized void setReceiver(IWeatherReceiver receiver) {
        this.receiver = receiver;
    }

    public synchronized void setMessageProvider(IMessageProvider provider) {
        this.messageProvider = provider;
    }

    private static volatile PositionManager instance;

    private PositionManager() {
        lastResponseIsFailure = false;
    }

    public static PositionManager getInstance() {
        if (instance == null) {
            synchronized (PositionManager.class) {
                if (instance == null) {
                    instance = new PositionManager();
                    instance.setReceiver(null);
                    instance.initManager();
                }
            }
        }
        return instance;
    }

    public static PositionManager getInstance(IMessageProvider provider, IWeatherReceiver receiver) {
        if (instance == null) {
            synchronized (PositionManager.class) {
                if (instance == null) {
                    instance = new PositionManager();
                    instance.setMessageProvider(provider);
                    instance.setReceiver(receiver);
                    instance.initManager();
                }
            }
        }
        return instance;
    }

    public synchronized void removeInstance() {
        instance = null;
    }

    public void initManager() {
        dbManager = new SQLiteProcessData();
        temperatureMetric = dbManager.loadTemperatureMetrics();
        speedMetric = dbManager.loadSpeedMetrics();
        pressureMetric = dbManager.loadPressureMetrics();
        initCurrentLocation();
        initPositions();
        initLocationManager();
        initStations();
        updateFavoritesList();
    }

    public void updateFavoritesList() {
        favouritesPositions = dbManager.loadFavoriteTownList();
        Collections.sort(favouritesPositions);
    }

    public List<String> getFavouritesList() {
        return favouritesPositions;
    }

    public boolean flipFavCity(String cityName) {
        boolean state;
        if (isFavouriteCity(cityName)) {
            state = false;
            favouritesPositions.remove(cityName);
        } else {
            state = true;
            favouritesPositions.add(cityName);
        }
        dbManager.saveTownFavourite(state, cityName);
        return state;
    }

    public void removeFavoriteCity(String city) {
        if (favouritesPositions != null) {
            favouritesPositions.remove(city);
        }
    }

    public boolean isFavouriteCity(String cityName) {
        try {
            return favouritesPositions.contains(cityName);
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clearFavorites() {
        if (favouritesPositions != null) {
            favouritesPositions.clear();
        }
    }

    // Пока заглушка, потом настрки сохранять при их смене в настройках
    public void saveSettings() {
        saveCurrStation();
        saveMetrics();
        saveCurrPosition();
    }

    public void saveMetrics() {
        dbManager.saveSettings(temperatureMetric, speedMetric, pressureMetric);
    }

    public void saveCurrPosition() {
        try {
            dbManager.saveLastCurrentLocationName(currentLocation.getLocationName());
            dbManager.saveLastPositionCoordinates(currentLocation.getCoordinate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCurrStation() {
        dbManager.saveSettings(currStation);
    }

    /**
     * Метод инициализации списка сервисов для получения информации о погодных условиях.
     */
    private void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf
                .addOpenWeatherMap()
                .create();
        currStation = stations.get(dbManager.loadStation());
    }

    private void initCurrentLocation() {
        currentLocation = new Position();
        currentLocation.setLocationName("Smart Forecast");
        currentLocation.setCityID(0);
        currentLocation.setCoordinate(null);
    }

    /**
     * Метод инициализации списка местоположений, вызывается из активити
     */
    private void initPositions() {
        HashMap<String, Coordinate> pos = dbManager.loadTownList();
        initPositions(pos);
    }

    /**
     * Метод инициализации списка местоположений, которые добавлены в список городов
     *
     * @param favorites коллекция {@link List} типа {@link String}, содержащая названия городов
     */
    private void initPositions(HashMap<String, Coordinate> favorites) {
        PositionFactory positionFactory = new PositionFactory();

        if (favorites.size() != 0) {
            for (HashMap.Entry<String, Coordinate> entry : favorites.entrySet()) {
                positionFactory.addFavouritePosition(entry.getKey(), entry.getValue());
            }
        }
        positions = positionFactory.getPositions();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        boolean saveCurrentLocation = sp.getString(MyApplication.getAppContext().getString(R.string.pref_location_key), MyApplication.getAppContext().getString(R.string.pref_location_current)).equals(MyApplication.getAppContext().getString(R.string.pref_location_current));
        String lastActivePositionName = sp.getString(MyApplication.getAppContext().getString(R.string.shared_last_active_position_name), "");
        currentLocation.setLocationName(dbManager.loadСurrentTown());
        currentLocation.setCoordinate(dbManager.loadLastPositionCoordinates());
        if (saveCurrentLocation) {
            activePosition = currentLocation;
            PermissionChecker permissionChecker = new PermissionChecker();
            boolean isLocationPermissionGranted = permissionChecker.isPermissionGranted(MyApplication.getAppContext(), PermissionChecker.RuntimePermissions.PERMISSION_REQUEST_FINE_LOCATION);
            if (!isLocationPermissionGranted) {
                if (!lastActivePositionName.isEmpty() && positionInListPresent(lastActivePositionName)) {
                    activePosition = getPosition(lastActivePositionName);
                }
            }
        } else {
            if (!lastActivePositionName.isEmpty() && positionInListPresent(lastActivePositionName)) {
                activePosition = getPosition(lastActivePositionName);
            } else {
                activePosition = currentLocation;
            }
        }
    }

    /**
     * Метод, с помощью которого добавляем новую локацию в список "Избранных"
     * Вызывается когда пользователь добавляет новый город в список.
     *
     * @param name        объект типа {@link String}, содержащий название города
     * @param coordinates геграфические координаты местоположения
     */
    public void addPosition(String name, Coordinate coordinates) {
        if (positionInListPresent(name)) {
            sendMessage(R.string.city_exist, Snackbar.LENGTH_LONG);
            return;
        }
        PositionFactory positionFactory = new PositionFactory(positions);
        positionFactory.addFavouritePosition(name, coordinates, dbManager);
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
        if (activePosition == null) {
            return "";
        }
        return activePosition.getLocationName();
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
        if (isFavouriteCity(name)) {
            removeFavoriteCity(name);
        }
    }

    public void removePositions() {
        positions.clear();
        clearFavorites();
        dbManager.deleteTowns();
    }

    /**
     * Метод, с помощью которого из списка городов выбираем другую локацию в качестве текущей
     *
     * @param name объект типа {@link String}, содержащий название города
     */
    public void setCurrentPosition(String name) {
        if (name.isEmpty()) {
            activePosition = currentLocation;
        } else if (positions.containsKey(name)) {
            activePosition = positions.get(name);
        } else {
            sendMessage(R.string.error_get_coordinates, Snackbar.LENGTH_LONG);
        }
    }

    public boolean positionInListPresent(String name) {
        try {
            return positions.containsKey(name);
        } catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean positionIsPresent(String name) {
        if (currentLocation.getLocationName().equals(name)) {
            return true;
        } else {
            try {
                return positions.containsKey(name);
            } catch (ClassCastException | NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }
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
     * Пролучение локации из списка локаций по внутреннему идентификатору локации
     *
     * @param cityID идентификатор местоположения
     * @return обьект типа {@link Position}
     */
    public Position getPosition(int cityID) {
        if (cityID == 0) {
            return currentLocation;
        }
        for (Position pos : positions.values()) {
            if (pos.getCityID() == cityID) {
                return pos;
            }
        }
        return null;
    }

    public AppUtils.PressureMetrics getPressureMetric() {
        return pressureMetric;
    }

    public AppUtils.PressureMetrics changePressureMetric() {
        pressureMetric = pressureMetric.change();
        return pressureMetric;
    }

    public void setSpeedMetric(AppUtils.SpeedMetrics sm) {
        speedMetric = sm;
    }

    public AppUtils.SpeedMetrics getSpeedMetric() {
        return speedMetric;
    }

    public AppUtils.TemperatureMetrics getTemperatureMetric() {
        return temperatureMetric;
    }

    public void setTemperatureMetric(AppUtils.TemperatureMetrics tm) {
        temperatureMetric = tm;
    }

    public AppUtils.TemperatureMetrics changeTemperatureMetric() {
        temperatureMetric = temperatureMetric.change();
        return temperatureMetric;
    }

    /**
     * Метод, вызывемый активити, для обновления текущей погоды от текущей погодной станции
     */
    public void updateWeather() {
        lastResponseIsFailure = false;
        if (activePosition == null || !positionIsPresent(activePosition.getLocationName())) {
            sendMessage(R.string.update_error_location_not_found, Snackbar.LENGTH_LONG);
            return;
        }
        if (activePosition == currentLocation) {
            updateCurrentLocationCoordinates();
            if (currentLocation.getCoordinate() == null) {
                updateWeatherFromDB();
                return;
            }
        }
        sendRequest();
    }

    /**
     * Метод, отправляет запрос на обновление погоды
     */
    public void sendRequest() {
        if (activePosition.getCoordinate() == null) {
            if (!activePosition.getLocationName().isEmpty()) {
                updateWeatherFromDB();
            }
            sendMessage(R.string.coordinates_error, Snackbar.LENGTH_LONG);
        } else if (!isNetworkAvailable(MyApplication.getAppContext())) {
            if (!activePosition.getLocationName().isEmpty()) {
                updateWeatherFromDB();
            }
            sendMessage(R.string.update_error_net_not_availble, Snackbar.LENGTH_LONG);
        } else {
            LinkedList<WeatherStation.ResponseType> requestQueue = new LinkedList<>();
            requestQueue.add(WeatherStation.ResponseType.CURRENT);
            try {
                if (receiver.receiveHourlyWeatherFirst()) {
                    requestQueue.addLast(WeatherStation.ResponseType.HOURLY);
                    requestQueue.addLast(WeatherStation.ResponseType.DAILY);
                } else {
                    requestQueue.addLast(WeatherStation.ResponseType.DAILY);
                    requestQueue.addLast(WeatherStation.ResponseType.HOURLY);
                }
                currStation.updateWeather(requestQueue, activePosition.getCityID(), activePosition.getCoordinate());
            } catch (NullPointerException e) {
                // Отсроченный запрос активити уже уничтожено
                e.printStackTrace();
                updateWeatherFromDB();
            }
        }
    }

    public void updateWeatherFromDB(WeatherStation.ResponseType responseType, String positionName) {
        try {
            switch (responseType) {
                case CURRENT:
                    receiver.updateInterface(responseType, getCurrentWeatherFromDB(currStation.getServiceType(), positionName));
                    break;
                case HOURLY:
                    receiver.updateInterface(responseType, getHourlyWeatherFromDB(currStation.getServiceType(), positionName));
                    break;
                case DAILY:
                    receiver.updateInterface(responseType, getDailyWeatherFromDB(currStation.getServiceType(), positionName));
                    break;
            }
        } catch (NullPointerException e) {
            // Отсроченный запрос активити уже уничтожено
            e.printStackTrace();
        }
    }

    public void updateWeatherFromDB(WeatherStation.ResponseType responseType, Position position) {
        try {
            updateWeatherFromDB(responseType, position.getLocationName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void updateWeatherFromDB() {
        try {
            updateWeatherFromDB(activePosition.getLocationName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void updateWeatherFromDB(String locationName) {
        updateWeatherFromDB(WeatherStation.ResponseType.CURRENT, locationName);
        updateWeatherFromDB(WeatherStation.ResponseType.HOURLY, locationName);
        updateWeatherFromDB(WeatherStation.ResponseType.DAILY, locationName);
    }

    /**
     * Метод для обновления погодных данных. Вызывается погодным сервисом, когда приходят актуальные данные
     *
     * @param requestList коллекция типа {@link LinkedList}, содержащая элементы {@link com.khasang.forecast.stations.WeatherStation.ResponseType}, хранит очередность запросов (текущий прогноз, прогноз на день или неделю)
     * @param cityId      внутренний идентификатор города, передается в погодную станцию во время запроса погоды
     * @param serviceType идентификатор погодного сервиса
     * @param weather     обьект типа {@link Weather}, содержащий погодные характеристики
     */
    public void onResponseReceived(LinkedList<WeatherStation.ResponseType> requestList, int cityId, WeatherStationFactory.ServiceType serviceType, Map<Calendar, Weather> weather) {
        lastResponseIsFailure = false;
        WeatherStation.ResponseType rType = requestList.pollFirst();
        if (rType == null) {
            return;
        }
        Position position = getPosition(cityId);
        if (activePosition.getCityID() == cityId && currStation.getServiceType() == serviceType && position != null) {
            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                if (rType == WeatherStation.ResponseType.CURRENT) {
                    dbManager.deleteOldWeatherAllTowns(serviceType, entry.getKey());
                    // Для CURRENT погоды сохраняем дополнительно погоду с текущим локальным временем,
                    // иначе возможно расхождение с погодой из БД, например при смене метрик
                    // (ближайшая в БД может отличаться от "текущей" погоды со станции)
                    dbManager.saveWeather(serviceType, position.getLocationName(), Calendar.getInstance(), entry.getValue());
                }
                dbManager.saveWeather(serviceType, position.getLocationName(), entry.getKey(), entry.getValue());
            }

            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                entry.setValue(AppUtils.formatWeather(entry.getValue(), temperatureMetric, speedMetric, pressureMetric));
            }
            try {
                receiver.updateInterface(rType, weather);
            } catch (NullPointerException e) {
                // Ответ пришел, когда активити уже уничтожено
                e.printStackTrace();
            }

            rType = requestList.peekFirst();
            if (rType == WeatherStation.ResponseType.HOURLY) {
                currStation.updateHourlyWeather(requestList, cityId, position.getCoordinate());
            } else if (rType == WeatherStation.ResponseType.DAILY) {
                currStation.updateWeeklyWeather(requestList, cityId, position.getCoordinate());
            }
        }
    }

    public void onFailureResponse(LinkedList<WeatherStation.ResponseType> requestList, int cityID, WeatherStationFactory.ServiceType sType) {
        if (!lastResponseIsFailure) {
            try {
                messageProvider.showToast(MyApplication.getAppContext().getString(R.string.update_error_from) + sType.toString());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            lastResponseIsFailure = true;
        }
        WeatherStation.ResponseType rType = requestList.pollFirst();
        if (rType == null) {
            return;
        }
        Position position = getPosition(cityID);
        if (activePosition.getCityID() == cityID && currStation.getServiceType() == sType && position != null) {
            updateWeatherFromDB(rType, position);
            rType = requestList.peekFirst();
            if (rType == WeatherStation.ResponseType.HOURLY) {
                currStation.updateHourlyWeather(requestList, cityID, position.getCoordinate());
            } else if (rType == WeatherStation.ResponseType.DAILY) {
                currStation.updateWeeklyWeather(requestList, cityID, position.getCoordinate());
            }
        }
    }

    private HashMap<Calendar, Weather> getCurrentWeatherFromDB(WeatherStationFactory.ServiceType sType, String locationName) {
        return dbManager.loadWeather(sType, locationName, Calendar.getInstance(), temperatureMetric, speedMetric, pressureMetric);
    }

    private HashMap<Calendar, Weather> getHourlyWeatherFromDB(WeatherStationFactory.ServiceType sType, String locationName) {
        final int HOUR_PERIOD = 3;
        final int FORECASTS_COUNT = 8;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
        calendar.set(Calendar.MINUTE, 0);
        HashMap<Calendar, Weather> forecast = new HashMap<>();
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


    private HashMap<Calendar, Weather> getDailyWeatherFromDB(WeatherStationFactory.ServiceType sType, String locationName) {
        final int DAY_PERIOD = 1;
        final int FORECASTS_COUNT = 7;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        HashMap<Calendar, Weather> forecast = new HashMap<>();
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

    public void initLocationManager() {
        locationManager = new CurrentLocationManager();
        locationManager.giveGpsAccess(true);
        try {
            updateCurrentLocation(locationManager.getLastLocation());
        } catch (AccessFineLocationNotGrantedException | EmptyCurrentAddressException e) {
            e.printStackTrace();
        }
    }

    public boolean isSomeLocationProviderAvailable() {
        return locationManager.checkProviders();
    }

    public void setUseGpsModule(boolean useGpsModule) {
        locationManager.giveGpsAccess(useGpsModule);
    }

    public void updateCurrentLocationCoordinates() {
        try {
            locationManager.updateCurrLocCoordinates();
        } catch (NoAvailableLocationServiceException e) {
            sendMessage(R.string.error_location_services_are_not_active, Snackbar.LENGTH_LONG);
            e.printStackTrace();
        } catch (GpsIsDisabledException e) {
            sendMessage(R.string.error_gps_disabled, Snackbar.LENGTH_LONG);
            e.printStackTrace();
        } catch (AccessFineLocationNotGrantedException e) {
            sendMessage(R.string.error_gps_permission, Snackbar.LENGTH_LONG);
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Coordinate getCurrentLocationCoordinates() {
        return currentLocation.getCoordinate();
    }

    public String getCurrentLocationName() {
        return currentLocation.getLocationName();
    }

    public void setCurrentLocationCoordinates(Location location) {
        if (updateCurrentLocation(location) && activePosition == currentLocation) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendRequest();
                }
            }, 3000);
        }
    }

    private boolean updateCurrentLocation(Location location) {
        if (location == null) {
            sendMessage(R.string.error_service_not_available, Snackbar.LENGTH_LONG);
            return false;
        }
        Geocoder geocoder = new Geocoder(MyApplication.getAppContext());
        try {
            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
            currentLocation.setLocationName(new LocationParser(list).parseList().getAddressLine());
            currentLocation.setCoordinate(new Coordinate(location.getLatitude(), location.getLongitude()));
            return true;
        } catch (IOException e) {
            sendMessage(R.string.error_service_not_available, Snackbar.LENGTH_LONG);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            sendMessage(R.string.invalid_lang_long_used, Snackbar.LENGTH_LONG);
            e.printStackTrace();
        } catch (EmptyCurrentAddressException | NoAvailableAddressesException | NullPointerException e) {
            sendMessage(R.string.no_address_found, Snackbar.LENGTH_LONG);
            e.printStackTrace();
        }
        return false;
    }

    private synchronized void sendMessage(CharSequence string, int length) {
        try {
            messageProvider.showMessageToUser(string, length);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private synchronized void sendMessage(int stringId, int length) {
        try {
            messageProvider.showMessageToUser(stringId, length);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}