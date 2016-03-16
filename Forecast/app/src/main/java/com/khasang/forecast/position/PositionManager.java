package com.khasang.forecast.position;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.Toast;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.interfaces.IWeatherReceiver;
import com.khasang.forecast.location.CurrentLocationManager;
import com.khasang.forecast.exceptions.EmptyCurrentAddressException;
import com.khasang.forecast.location.LocationParser;
import com.khasang.forecast.exceptions.NoAvailableAddressesException;
import com.khasang.forecast.orm.SettingsManager;
import com.khasang.forecast.orm.TownsManager;
import com.khasang.forecast.orm.WeatherManager;
import com.khasang.forecast.orm.tables.TownsTable;
import com.khasang.forecast.stations.WeatherStation;
import com.khasang.forecast.stations.WeatherStationFactory;
import com.orm.SugarContext;

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
    private IWeatherReceiver weatherReceiver;
    private SettingsManager settingsManager;
    private WeatherManager weatherManager;
    private TownsManager townsManager;
    private boolean lastResponseIsFailure;
    private CurrentLocationManager locationManager;

    public void setWeatherReceiver(IWeatherReceiver weatherReceiver) {
        this.weatherReceiver = weatherReceiver;
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
                    instance.initManager();
                }
            }
        }
        return instance;
    }

    public synchronized void removeInstance() {
        SugarContext.terminate();
        instance = null;
    }

    public void initManager() {
        weatherReceiver = null;

        SugarContext.init(MyApplication.getAppContext());

        settingsManager = new SettingsManager();
        weatherManager = new WeatherManager();
        townsManager = new TownsManager();

        temperatureMetric = settingsManager.loadСurrTemperatureMetrics();
        speedMetric = settingsManager.loadСurrSpeedMetrics();
        pressureMetric = settingsManager.loadСurrPressureMetrics();

        initCurrentLocation();
        initPositions();
        initLocationManager();
        initStations();
        updateFavoritesList();
    }

    public void updateFavoritesList() {
        favouritesPositions = townsManager.loadFavoriteTownList();
        if (favouritesPositions != null) {
            Collections.sort(favouritesPositions);
        }
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
        townsManager.saveTownFavourite(state, cityName);
        return state;
    }

    public boolean isFavouriteCity(String cityName) {
        try {
            return favouritesPositions.contains(cityName);
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Пока заглушка, потом настрки сохранять при их смене в настройках
    public void saveSettings() {
        saveCurrStation();
        saveMetrics();
        saveCurrPosition();
    }

    public void saveMetrics() {
        settingsManager.saveCurrMetrics(temperatureMetric, speedMetric, pressureMetric);
    }

    public void saveCurrPosition() {
        try {
            settingsManager.saveCurrTown(currentLocation.getLocationName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCurrStation() {
        settingsManager.saveCurrStation(currStation);
    }

    /**
     * Метод инициализации списка сервисов для получения информации о погодных условиях.
     */
    private void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf
                .addOpenWeatherMap()
                .create();
        currStation = stations.get(settingsManager.loadСurrStation());
    }

    private void initCurrentLocation() {
        String locName = settingsManager.loadСurrTown();
        currentLocation = new Position();
        currentLocation.setLocationName("Smart Forecast");
        currentLocation.setCityID(0);
        currentLocation.setCoordinate(null);
        if (locName != null && !locName.isEmpty()) {
            currentLocation.setLocationName(locName);
        }
    }

    /**
     * Метод инициализации списка местоположений, вызывается из активити
     */
    private void initPositions() {
        List<TownsTable> pos = townsManager.loadTownList();
        initPositions(pos);
    }

    /**
     * Метод инициализации списка местоположений, которые добавлены в список городов
     *
     * @param favorites коллекция {@link List} типа {@link String}, содержащая названия городов
     */
    private void initPositions(List<TownsTable> favorites) {
        PositionFactory positionFactory = new PositionFactory();

        if (favorites.size() != 0) {
            for(int i = 0; i < favorites.size(); i++) {
                positionFactory.addFavouritePosition(
                        favorites.get(i).name,
                        new Coordinate(favorites.get(i).latitude, favorites.get(i).longitude)
                );
            }
        }
        positions = positionFactory.getPositions();
        //      String currPositionName = dbManager.loadСurrentTown();
        // TODO если сохранять будем координаты текущего местоположения то необходимо переделать
        //      if (!currPositionName.isEmpty() && positionIsPresent(currPositionName)) {
        //       activePosition = positions.get(currPositionName);
        //    }

        // Сейчас активная позиция всегда текущая локация
        activePosition = currentLocation;
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
            Toast.makeText(MyApplication.getAppContext(), R.string.city_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        PositionFactory positionFactory = new PositionFactory(positions);
        positionFactory.addFavouritePosition(name, coordinates, townsManager);
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
            townsManager.deleteTown(name);
        }
    }

    public void removePositions() {
        positions.clear();
        townsManager.deleteTowns();
    }

    /**
     * Метод, с помощью которого из списока "Избранных" выбираем другую локацию в качестве текущей
     *
     * @param name объект типа {@link String}, содержащий название города
     */
    public void setCurrentPosition(String name) {
        if (name.isEmpty()) {
            activePosition = currentLocation;
        } else if (positions.containsKey(name)) {
            activePosition = positions.get(name);
        } else {
            Toast.makeText(MyApplication.getAppContext(), "Не могу определить координаты запрашиваемого местоположения", Toast.LENGTH_SHORT).show();
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
    private Position getPosition(int cityID) {
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
        if (activePosition == null || !positionIsPresent(activePosition.getLocationName())) {
            Toast.makeText(MyApplication.getAppContext(), R.string.update_error_location_not_found, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MyApplication.getAppContext(), R.string.coordinates_error, Toast.LENGTH_SHORT).show();
        } else if (!isNetworkAvailable(MyApplication.getAppContext())) {
            if (!activePosition.getLocationName().isEmpty()) {
                updateWeatherFromDB();
            }
            Toast.makeText(MyApplication.getAppContext(), R.string.update_error_net_not_availble, Toast.LENGTH_SHORT).show();
        } else {
            LinkedList<WeatherStation.ResponseType> requestQueue = new LinkedList<>();
            requestQueue.add(WeatherStation.ResponseType.CURRENT);
            try {
                if (weatherReceiver.receiveHourlyWeatherFirst()) {
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
            }
        }
    }

    public void updateWeatherFromDB(WeatherStation.ResponseType responseType, String positionName) {
        try {
            switch (responseType) {
                case CURRENT:
                    weatherReceiver.updateInterface(responseType, getCurrentWeatherFromDB(currStation.getServiceType(), positionName));
                    break;
                case HOURLY:
                    weatherReceiver.updateInterface(responseType, getHourlyWeatherFromDB(currStation.getServiceType(), positionName));
                    break;
                case DAILY:
                    weatherReceiver.updateInterface(responseType, getDailyWeatherFromDB(currStation.getServiceType(), positionName));
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
                    weatherManager.deleteOldWeatherAllTowns(serviceType, entry.getKey());
                    // Для CURRENT погоды сохраняем дополнительно погоду с текущим локальным временем,
                    // иначе возможно расхождение с погодой из БД, например при смене метрик
                    // (ближайшая в БД может отличаться от "текущей" погоды со станции)
                    weatherManager.saveWeather(serviceType, position.getLocationName(), Calendar.getInstance(), entry.getValue());
                }
                weatherManager.saveWeather(serviceType, position.getLocationName(), entry.getKey(), entry.getValue());
            }

            for (Map.Entry<Calendar, Weather> entry : weather.entrySet()) {
                entry.setValue(AppUtils.formatWeather(entry.getValue(), temperatureMetric, speedMetric, pressureMetric));
            }
            try {
                weatherReceiver.updateInterface(rType, weather);
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
            Toast.makeText(MyApplication.getAppContext(), R.string.update_error_from + sType.toString(), Toast.LENGTH_SHORT).show();
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
        return weatherManager.loadWeather(sType, locationName, Calendar.getInstance(), temperatureMetric, speedMetric, pressureMetric);
    }

    private HashMap<Calendar, Weather> getHourlyWeatherFromDB(WeatherStationFactory.ServiceType sType, String locationName) {
        final int HOUR_PERIOD = 3;
        final int FORECASTS_COUNT = 8;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, HOUR_PERIOD);
        calendar.set(Calendar.MINUTE, 0);
        HashMap<Calendar, Weather> forecast = new HashMap<>();
        for (int i = 0; i < FORECASTS_COUNT; i++) {
            HashMap<Calendar, Weather> temp = weatherManager.loadWeather(sType, locationName, calendar, temperatureMetric, speedMetric, pressureMetric);
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
            HashMap<Calendar, Weather> temp = weatherManager.loadWeather(sType, locationName, calendar, temperatureMetric, speedMetric, pressureMetric);
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
        } catch (EmptyCurrentAddressException e) {
            currentLocation.setCoordinate(null);
            e.printStackTrace();
        }
    }

    public boolean isSomeLocationProviderAvailable () {
        return locationManager.checkProviders();
    }

    public void setUseGpsModule(boolean useGpsModule) {
        locationManager.giveGpsAccess(useGpsModule);
    }

    public void updateCurrentLocationCoordinates() {
        try {
            locationManager.updateCurrentLocationCoordinates();
        } catch (NullPointerException e) {
            // Возможно активити уже уничтожено
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
        Geocoder geocoder = new Geocoder(MyApplication.getAppContext());
        try {
            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
            currentLocation.setLocationName(new LocationParser(list).parseList().getAddressLine());
            currentLocation.setCoordinate(new Coordinate(location.getLatitude(), location.getLongitude()));
            return true;
        } catch (IOException e) {
            Toast.makeText(MyApplication.getAppContext(), R.string.error_service_not_available, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Toast.makeText(MyApplication.getAppContext(), R.string.invalid_lang_long_used, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (EmptyCurrentAddressException | NoAvailableAddressesException | NullPointerException e) {
            Toast.makeText(MyApplication.getAppContext(), R.string.no_address_found, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        currentLocation.setCoordinate(null);
        return false;
    }
}