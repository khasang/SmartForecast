package com.khasang.forecast.position;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

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

    private Drawable[] iconsSet;
    IconicsDrawable iconNa = null;
    private boolean isSvgIconsUsed = false;
    private int currentWeatherIconColor;
    private int forecastWeatherIconColor;

    public synchronized void setReceiver(IWeatherReceiver receiver) {
        this.receiver = receiver;
    }

    public synchronized void setMessageProvider(IMessageProvider provider) {
        this.messageProvider = provider;
    }

    private static volatile PositionManager instance;

    private PositionManager() {
        lastResponseIsFailure = false;
        iconNa = new IconicsDrawable(MyApplication.getAppContext())
                .icon(WeatherIcons.Icon.wic_na)
                .sizeDp(80)
                .paddingDp(4);
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
            Collections.sort(favouritesPositions);
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

    public void setSpeedMetric(AppUtils.SpeedMetrics sm) {
        speedMetric = sm;
    }

    public AppUtils.SpeedMetrics getSpeedMetric() {
        return speedMetric;
    }

    public AppUtils.PressureMetrics getPressureMetric() {
        return pressureMetric;
    }

    public AppUtils.PressureMetrics changePressureMetric() {
        pressureMetric = pressureMetric.change();
        return pressureMetric;
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
            sendMessage(R.string.update_error_net_not_available, Snackbar.LENGTH_LONG);
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

    public void generateIconSet(Context context) {
        iconsSet = AppUtils.createIconsSet(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String iconsSet = sp.getString(context.getString(R.string.pref_icons_set_key), context.getString(R.string.pref_icons_set_default));
        currentWeatherIconColor = ContextCompat.getColor(context, R.color.current_weather_color);
        forecastWeatherIconColor = ContextCompat.getColor(context, R.color.text_primary);
        if (iconsSet.equals(context.getString(R.string.pref_icons_set_mike_color))) {
            isSvgIconsUsed = true;
            int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            String colorScheme = sp.getString(context.getString(R.string.pref_color_scheme_key), context.getString(R.string.pref_color_scheme_teal));
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            } else if (colorScheme.equals(context.getString(R.string.pref_color_scheme_brown))) {
                forecastWeatherIconColor = ContextCompat.getColor(context, R.color.primary_brown);
            } else if (colorScheme.equals(context.getString(R.string.pref_color_scheme_teal))) {
                forecastWeatherIconColor = ContextCompat.getColor(context, R.color.primary_teal);
            } else if (colorScheme.equals(context.getString(R.string.pref_color_scheme_indigo))) {
                forecastWeatherIconColor = ContextCompat.getColor(context, R.color.primary_indigo);
            } else if (colorScheme.equals(context.getString(R.string.pref_color_scheme_purple))) {
                forecastWeatherIconColor = ContextCompat.getColor(context, R.color.primary_purple);
            } else {
                forecastWeatherIconColor = ContextCompat.getColor(context, R.color.primary_green);
            }
        } else {
            isSvgIconsUsed = iconsSet.equals(context.getString(R.string.pref_icons_set_mike_bw));
        }
    }

    public Drawable getWeatherIcon(int iconNumber, boolean isCurrentWeatherIcon) {
        Drawable icon = null;
        try {
            if (iconsSet[iconNumber] != null) {
                icon = iconsSet[iconNumber];
                if (isSvgIconsUsed) {
                    if (isCurrentWeatherIcon) {
                        IconicsDrawable cIcon = ((IconicsDrawable) icon).clone();
                        cIcon.color(currentWeatherIconColor);
                        return cIcon;
                    } else {
                        icon = ((IconicsDrawable) icon)
                                .color(forecastWeatherIconColor);
                    }
                }
            } else if (isCurrentWeatherIcon) {
                IconicsDrawable cIcon = ((IconicsDrawable) iconsSet[AppUtils.ICON_INDEX_NA])
                        .clone();
                cIcon.color(currentWeatherIconColor);
                return cIcon;
            } else {
                icon = ((IconicsDrawable) iconsSet[AppUtils.ICON_INDEX_NA])
                        .color(forecastWeatherIconColor);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            if (isCurrentWeatherIcon) {
                icon = iconNa.clone();
                ((IconicsDrawable) icon).color(currentWeatherIconColor);
            } else {
                icon = iconNa.color(forecastWeatherIconColor);
            }
        }
        return icon;
    }
}