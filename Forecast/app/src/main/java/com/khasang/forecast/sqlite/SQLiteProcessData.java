package com.khasang.forecast.sqlite;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.position.Position;
import com.khasang.forecast.position.Precipitation;
import com.khasang.forecast.position.Weather;
import com.khasang.forecast.stations.WeatherStation;
import com.khasang.forecast.stations.WeatherStationFactory;
import com.khasang.forecast.position.Wind;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Класс-обертка, предоставляет методы для записи/загрузки/удаления информации в БД.
 *
 * @author maxim.kulikov
 */

public class SQLiteProcessData {

    public SimpleDateFormat dtFormat;

    public SQLiteProcessData() {
        dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void sqliteClose() {
        SQLiteWork.getInstance().removeInstance();
    }

    // Сохранение города с координатами (перед сохранением списка нужно очистить старый)
    public void saveTown(String town, double latitude, double longitude) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_INSERT_TOWN_v4, new String[]{town, Double.toString(latitude), Double.toString(longitude)});
    }

    // Сохранение погоды, удаление старой погоды.
    public void saveWeather(WeatherStationFactory.ServiceType serviceType, String townName, Calendar date, Weather weather) {

        deleteDoubleWeather(serviceType, townName, date);

        SQLiteWork.getInstance().qExec(
                SQLiteFields.QUERY_INSERT_WEATHER,
                new String[]{serviceType.name(), townName, dtFormat.format(date.getTime()), Double.toString(weather.getTemperature()), Double.toString(weather.getTemp_max()),
                        Double.toString(weather.getTemp_min()), Double.toString(weather.getPressure()),
                        Integer.toString(weather.getHumidity()), weather.getDescription(), weather.getWindDirection().name(),
                        Double.toString(weather.getWindPower()), weather.getPrecipitation().name()
                }
        );
    }

    // Обновление координат города
    public void updatePositionCoordinates (Position position) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_TOWN_POSITION,
                new String[]{
                        Double.toString(position.getCoordinate().getLatitude()),
                        Double.toString(position.getCoordinate().getLongitude()),
                        position.getLocationName()
                });
    }

    // Обновление временно
    public void updateCityTimeZone (Position position) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_TOWN_TIME_ZONE,
                new String[]{
                        String.valueOf(position.getTimeZone()),
                        position.getLocationName()
                });
    }

    // Сохранение настроек
    public void saveSettings(WeatherStation currentStation) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_CURRSTATION_SETTING, new String[]{currentStation.getServiceType().name()});
    }

    public void saveSettings(AppUtils.TemperatureMetrics temperatureMetrics,
                             AppUtils.SpeedMetrics speedMetrics, AppUtils.PressureMetrics pressureMetrics) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_METRICS_SETTINGS, new String[]{temperatureMetrics.name(), speedMetrics.name(), pressureMetrics.name()});
    }

    public void saveLastCurrentLocationName(String currLocation) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_CURRCITY_SETTING, new String[]{currLocation});
    }

    public void saveLastPositionCoordinates(Coordinate coordinate) {
        saveLastPositionCoordinates(coordinate.getLatitude(), coordinate.getLongitude());
    }

    // Сохранение координат в настройках
    public void saveLastPositionCoordinates(double latitude, double longitude) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_CURRLATLNG_SETTING, new String[]{Double.toString(latitude), Double.toString(longitude)});
    }

    // Загрузка CurrentTown.
    public String loadСurrentTown() {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            return recList.get(0).get(SQLiteFields.CURRENT_TOWN);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    // Загрузка последних сохраненных координат из настроек.
    // bug: parseDouble уходит в Exception при возвращении пустого значения "" latitude|longitude из БД
    public Coordinate loadLastPositionCoordinates() {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        double latitude;
        double longitude;
        try {
            String stringLatitude = recList.get(0).get(SQLiteFields.CURRENT_LATITUDE);
            if(stringLatitude.equals("")){
                latitude = 0;
            } else {
                latitude = Double.parseDouble(stringLatitude);
            }

            String stringLongitude = recList.get(0).get(SQLiteFields.CURRENT_LONGITUDE);
            if(stringLongitude.equals("")){
                longitude = 0;
            } else {
                longitude = Double.parseDouble(stringLongitude);
            }

            return new Coordinate(latitude, longitude);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Загрузка TemperatureMetrics.
    public AppUtils.TemperatureMetrics loadTemperatureMetrics() {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            return AppUtils.TemperatureMetrics.valueOf(recList.get(0).get(SQLiteFields.CURRENT_TEMPIRATURE_METRICS));
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return AppUtils.TemperatureMetrics.CELSIUS;
    }

    // Загрузка SpeedMetrics.
    public AppUtils.SpeedMetrics loadSpeedMetrics() {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            return AppUtils.SpeedMetrics.valueOf(recList.get(0).get(SQLiteFields.CURRENT_SPEED_METRICS));
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return AppUtils.SpeedMetrics.METER_PER_SECOND;
    }

    // Загрузка PressureMetrics.  {HPA, MM_HG}
    public AppUtils.PressureMetrics loadPressureMetrics() {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            return AppUtils.PressureMetrics.valueOf(recList.get(0).get(SQLiteFields.CURRENT_PRESSURE_METRICS));
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return AppUtils.PressureMetrics.HPA;
    }

    // Загрузка Station.
    public WeatherStationFactory.ServiceType loadStation() {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            return WeatherStationFactory.ServiceType.valueOf(recList.get(0).get(SQLiteFields.CURRENT_STATION));
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
    }

    // Очистка таблицы от данных, старше определенной даты.
    public void deleteOldWeather(WeatherStationFactory.ServiceType serviceType, String townName, Calendar date) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_OLD_DATA_WEATHER, new String[]{serviceType.name(), townName, dtFormat.format(date.getTime())});
    }

    // Очистка таблицы от данных, старше определенной даты.
    public void deleteOldWeatherAllTowns(WeatherStationFactory.ServiceType serviceType, Calendar date) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_OLD_DATA_WEATHER_ALL_TOWNS, new String[]{serviceType.name(), dtFormat.format(date.getTime())});
    }

    // Очистка таблицы от старых данных, чтобы не было дублей.
    public void deleteDoubleWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Calendar date) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_DOUBLE_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())});
    }

    // Очистка таблицы городов и удаление погодных данных к ним.
    public void deleteTowns() {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_DATA_TOWNS);
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_DATA_WEATHER);
    }

    // Удаление города и погодных данных к нему.
    public void deleteTown(String townName) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_DATA_TOWN, new String[]{townName});
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_DATA_TOWN_WEATHER, new String[]{townName});
    }

    // Запись времени рассвета и заката.
    public void updateTownSunTime(Calendar sunRise, Calendar sunSet, String townName) {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_TOWN_SUNTIME, new String[]{dtFormat.format(sunRise.getTime()), dtFormat.format(sunSet.getTime()), townName});
    }

    // Получение времени рассвета к городу.
    public Calendar loadTownSunSet(String townName) {
        Calendar weatherDate = null;
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            weatherDate = Calendar.getInstance();
            weatherDate.setTime(dtFormat.parse(recList.get(0).get(SQLiteFields.SUNSET)));
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return weatherDate;
    }

    // Получение времени заката к городу.
    public Calendar loadTownSunRise(String townName) {
        Calendar weatherDate = null;
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            weatherDate = Calendar.getInstance();
            weatherDate.setTime(dtFormat.parse(recList.get(0).get(SQLiteFields.SUNRISE)));
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return weatherDate;
    }

    // Добавление города в избранное.
    public void saveTownFavourite(boolean isFavourite, String townName) {
        String favourite = "0";
        if (isFavourite) {
            favourite = "1";
        }
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_UPDATE_TOWN_FAVORITE, new String[]{favourite, townName});
    }

    // Наличие города в избранном.
    public boolean getTownFavourite(String townName) {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_DATA_TOWN, new String[]{townName});
        try {
            if (recList.get(0).get(SQLiteFields.FAVORITE).equals("1")) {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return false;
    }

    // получение временной зоны города.
    public String getTownTimeZone(String townName) {
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_DATA_TOWN, new String[]{townName});
        try {
            return recList.get(0).get(SQLiteFields.TIME_ZONE);
        } catch (Exception e){
            e.printStackTrace();
        }
        // Значение по умолчанию.
        return "";
    }

    // Загрузка списка городов.
    public HashMap<String, Coordinate> loadTownList() {
        HashMap<String, Coordinate> hashMap = new HashMap<>();
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_TOWNS, null);
        try {
            for (int i = 0; i < recList.size(); i++) {
                String townName = recList.get(i).get(SQLiteFields.TOWN);
                double townLat = Double.parseDouble(recList.get(i).get(SQLiteFields.LATITUDE));
                double townLong = Double.parseDouble(recList.get(i).get(SQLiteFields.LONGITUDE));

                Coordinate coordinate = new Coordinate(townLat, townLong);
                hashMap.put(townName, coordinate);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return hashMap;
    }

    // Загрузка списка городов
    public ArrayList<Position> loadTownListFull() {
        ArrayList<Position> list = new ArrayList<Position>();
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_TOWNS, null);
        try {
            for (int i = 0; i < recList.size(); i++) {
                String townName = recList.get(i).get(SQLiteFields.TOWN);
                String timeZone = recList.get(i).get(SQLiteFields.TIME_ZONE);
                double townLat = Double.parseDouble(recList.get(i).get(SQLiteFields.LATITUDE));
                double townLong = Double.parseDouble(recList.get(i).get(SQLiteFields.LONGITUDE));

                Position position = new Position();
                position.setLocationName(townName);
                position.setCoordinate(new Coordinate(townLat, townLong));
                position.setTimeZone(timeZone);

                list.add(position);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    // Загрузка списка избранных городов.
    public ArrayList<String> loadFavoriteTownList() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_FAVORITE_TOWN, new String[]{"1"});
        try {
            for (int i = 0; i < recList.size(); i++) {
                list.add(recList.get(i).get(SQLiteFields.TOWN));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    // Загрузка погоды.
    public HashMap<Calendar, Weather> loadWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Calendar date, AppUtils.TemperatureMetrics tm, AppUtils.SpeedMetrics sm, AppUtils.PressureMetrics pm) {
        Weather weather = null;
        HashMap<Calendar, Weather> hashMap = null;
        Calendar weatherDate = null;

        ArrayList<HashMap<String, String>> recList = SQLiteWork.getInstance().queryOpen(
                SQLiteFields.QUERY_SELECT_WEATHER,
                new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())}
        );

        try {
            for (int i = 0; i < recList.size(); i++) {
                String wDate = recList.get(i).get(SQLiteFields.DATE);
                weatherDate = Calendar.getInstance();
                weatherDate.setTime(dtFormat.parse(wDate));

                double tempirature = Double.parseDouble(recList.get(i).get(SQLiteFields.TEMPIRATURE));
                double tempirature_max = Double.parseDouble(recList.get(i).get(SQLiteFields.TEMPIRATURE_MAX));
                double tempirature_min = Double.parseDouble(recList.get(i).get(SQLiteFields.TEMPIRATURE_MIN));
                double pressure = Double.parseDouble(recList.get(i).get(SQLiteFields.PRESSURE));
                int humidity = Integer.parseInt(recList.get(i).get(SQLiteFields.HUMIDITY));
                String description = recList.get(i).get(SQLiteFields.DESCRIPTION);

                String wind_direction = recList.get(i).get(SQLiteFields.WIND_DIRECTION);
                double wind_speed = Double.parseDouble(recList.get(i).get(SQLiteFields.WIND_SPEED));
                Wind wind = new Wind();
                wind.setDirection(wind_direction);
                wind.setSpeed(wind_speed);

                String precipitation_type = recList.get(i).get(SQLiteFields.PRECIPITATION_TYPE);
                Precipitation precipitation = new Precipitation();
                precipitation.setType(precipitation_type);

                weather = new Weather(tempirature, tempirature_min, tempirature_max, pressure, humidity, wind, precipitation, description);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (weather != null) {
            hashMap = new HashMap<>();
            hashMap.put(weatherDate, AppUtils.formatWeather(weather, tm, sm, pm));
        }
        return hashMap;
    }
}
