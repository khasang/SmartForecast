package com.khasang.forecast.sqlite;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.khasang.forecast.AppUtils;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.position.Position;
import com.khasang.forecast.position.Precipitation;
import com.khasang.forecast.position.Weather;
import com.khasang.forecast.stations.WeatherStation;
import com.khasang.forecast.stations.WeatherStationFactory;
import com.khasang.forecast.position.Wind;

import java.text.ParseException;
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
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_INSERT_TOWN_1, new String[]{town, Double.toString(latitude), Double.toString(longitude)});
    }

    // Сохранение погоды, удаление старой погоды.
    public void saveWeather(WeatherStationFactory.ServiceType serviceType, String townName, Calendar date, Weather weather) {

        deleteDoubleWeather(serviceType, townName, date);

        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]
                {serviceType.name(), townName, dtFormat.format(date.getTime()), Double.toString(weather.getTemperature()), Double.toString(weather.getTemp_max()),
                        Double.toString(weather.getTemp_min()), Double.toString(weather.getPressure()),
                        Integer.toString(weather.getHumidity()), weather.getDescription(), weather.getWindDirection().name(),
                        Double.toString(weather.getWindPower()), weather.getPrecipitation().name()});
    }

    // Сохранение настроек
    public void saveSettings(WeatherStation currentStation) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_UPDATE_CURRSTATION_SETTING, new String[]{currentStation.getServiceType().name()});
    }

    public void saveSettings(AppUtils.TemperatureMetrics temperatureMetrics,
                             AppUtils.SpeedMetrics speedMetrics, AppUtils.PressureMetrics pressureMetrics) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_UPDATE_METRICS_SETTINGS, new String[]{temperatureMetrics.name(), speedMetrics.name(), pressureMetrics.name()});
    }

    public void saveLastCurrentLocationName(String currLocation) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_UPDATE_CURRCITY_SETTING, new String[]{currLocation});
    }

    // Сохранение координат в настройках
    public void saveLastCurrentLatLng(double latitude, double longitude) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_UPDATE_CURRLATLNG_SETTING, new String[]{Double.toString(latitude), Double.toString(longitude)});
    }

    // Загрузка CurrentTown.
    public String loadСurrentTown() {
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TOWN));
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        return "";
    }

    // Загрузка последних сохраненных координат из настроек.
    public LatLng loadCurrentLatLng() {
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    double latitude = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.CURRENT_LATITUDE));
                    double longitude = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.CURRENT_LONGITUDE));

                    return new LatLng(latitude, longitude);
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        return null;
    }

    // Загрузка TemperatureMetrics.
    public AppUtils.TemperatureMetrics loadTemperatureMetrics() {
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return AppUtils.TemperatureMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TEMPIRATURE_METRICS)));
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return AppUtils.TemperatureMetrics.CELSIUS;
    }

    // Загрузка SpeedMetrics.
    public AppUtils.SpeedMetrics loadSpeedMetrics() {
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return AppUtils.SpeedMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_SPEED_METRICS)));
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return AppUtils.SpeedMetrics.METER_PER_SECOND;
    }

    // Загрузка PressureMetrics.  {HPA, MM_HG}
    public AppUtils.PressureMetrics loadPressureMetrics() {
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return AppUtils.PressureMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_PRESSURE_METRICS)));
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return AppUtils.PressureMetrics.HPA;
    }

    // Загрузка Station.
    public WeatherStationFactory.ServiceType loadStation() {
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return WeatherStationFactory.ServiceType.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_STATION)));
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
    }

    // Очистка таблицы от данных, старше определенной даты.
    public void deleteOldWeather(WeatherStationFactory.ServiceType serviceType, String townName, Calendar date) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_DELETE_OLD_DATA_WEATHER, new String[]{serviceType.name(), townName, dtFormat.format(date.getTime())});
    }

    // Очистка таблицы от данных, старше определенной даты.
    public void deleteOldWeatherAllTowns(WeatherStationFactory.ServiceType serviceType, Calendar date) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_DELETE_OLD_DATA_WEATHER_ALL_TOWNS, new String[]{serviceType.name(), dtFormat.format(date.getTime())});
    }

    // Очистка таблицы от старых данных, чтобы не было дублей.
    public void deleteDoubleWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Calendar date) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_DELETE_DOUBLE_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())});
    }

    // Очистка таблицы городов и удаление погодных данных к ним.
    public void deleteTowns() {
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_DATA_TOWNS);
        SQLiteWork.getInstance().qExec(SQLiteFields.QUERY_DELETE_DATA_WEATHER);
    }

    // Удаление города и погодных данных к нему.
    public void deleteTown(String townName) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_DELETE_DATA_TOWN, new String[]{townName});
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_DELETE_DATA_TOWN_WEATHER, new String[]{townName});
    }

    // Запись времени рассвета и заката.
    public void updateTownSunTime(Calendar sunRise, Calendar sunSet, String townName) {
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_UPDATE_TOWN_SUNTIME, new String[]{dtFormat.format(sunRise.getTime()), dtFormat.format(sunSet.getTime()), townName});
    }

    // Получение времени рассвета к городу.
    public Calendar loadTownSunSet(String townName) {
        String wDate;
        Calendar weatherDate = null;

        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_DATA_TOWN, new String[]{townName});
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    wDate = dataset.getString(dataset.getColumnIndex(SQLiteFields.SUNSET));
                    weatherDate = Calendar.getInstance();
                    weatherDate.setTime(dtFormat.parse(wDate));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        return weatherDate;
    }

    // Получение времени заката к городу.
    public Calendar loadTownSunRise(String townName) {
        String wDate;
        Calendar weatherDate = null;

        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_DATA_TOWN, new String[]{townName});
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    wDate = dataset.getString(dataset.getColumnIndex(SQLiteFields.SUNRISE));
                    weatherDate = Calendar.getInstance();
                    weatherDate.setTime(dtFormat.parse(wDate));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        return weatherDate;
    }

    // Добавление города в избранное.
    public void saveTownFavourite(boolean isFavourite, String townName) {
        String favourite = "0";
        if(isFavourite) {
            favourite = "1";
        }
        SQLiteWork.getInstance().qExExec(SQLiteFields.QUERY_UPDATE_TOWN_FAVORITE, new String[]{favourite, townName});
    }

    // Наличие города в избранном.
    public boolean getTownFavourite(String townName) {
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_DATA_TOWN, new String[]{townName});
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    do {
                        if(dataset.getString(dataset.getColumnIndex(SQLiteFields.FAVORITE)) == "1") {
                            return true;
                        }
                    } while (dataset.moveToNext());
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        return false;
    }

    // Загрузка списка городов.
    public HashMap<String, Coordinate> loadTownList() {
        double townLat = 0;
        double townLong = 0;
        String townName = "";
        HashMap hashMap = new HashMap();
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_TOWNS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    do {
                        townName = dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN));
                        townLat = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.LATITUDE));
                        townLong = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.LONGITUDE));

                        Coordinate coordinate = new Coordinate(townLat, townLong);
                        hashMap.put(townName, coordinate);
                    } while (dataset.moveToNext());
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        return hashMap;
    }

    // Загрузка списка избранных городов.
    public ArrayList<String> loadFavoriteTownList() {
        ArrayList<String> list = new ArrayList<>();
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_FAVORITE_TOWN, new String[]{"1"});
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    do {
                        list.add(dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN)));
                    } while (dataset.moveToNext());
                }
            }
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }
        return list;
    }

    // Загрузка погоды.
    public HashMap<Calendar, Weather> loadWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Calendar date, AppUtils.TemperatureMetrics tm, AppUtils.SpeedMetrics sm, AppUtils.PressureMetrics pm) {
        double tempirature = 0;
        double tempirature_max = 0;
        double tempirature_min = 0;
        double pressure = 0;
        double wind_speed = 0;
        String description = "";
        String wind_direction = "";
        String precipitation_type = "";
        String wDate;
        int humidity = 0;
        Wind wind = null;
        Precipitation precipitation = null;
        Weather weather = null;
        HashMap hashMap = null;
        Calendar weatherDate = null;
        Cursor dataset = SQLiteWork.getInstance().queryOpen(SQLiteFields.QUERY_SELECT_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())});
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    wDate = dataset.getString(dataset.getColumnIndex(SQLiteFields.DATE));
                    weatherDate = Calendar.getInstance();
                    weatherDate.setTime(dtFormat.parse(wDate));

                    tempirature = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.TEMPIRATURE));
                    tempirature_max = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.TEMPIRATURE_MAX));
                    tempirature_min = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.TEMPIRATURE_MIN));
                    pressure = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.PRESSURE));
                    humidity = dataset.getInt(dataset.getColumnIndex(SQLiteFields.HUMIDITY));
                    description = dataset.getString(dataset.getColumnIndex(SQLiteFields.DESCRIPTION));

                    wind_direction = dataset.getString(dataset.getColumnIndex(SQLiteFields.WIND_DIRECTION));
                    wind_speed = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.WIND_SPEED));
                    wind = new Wind();
                    wind.setDirection(wind_direction);
                    wind.setSpeed(wind_speed);

                    precipitation_type = dataset.getString(dataset.getColumnIndex(SQLiteFields.PRECIPITATION_TYPE));
                    precipitation = new Precipitation();
                    precipitation.setType(precipitation_type);

                    weather = new Weather(tempirature, tempirature_min, tempirature_max, pressure, humidity, wind, precipitation, description);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            SQLiteWork.getInstance().checkOpenDatabaseRead();
            if (dataset != null) {
                dataset.close();
            }
        }

        if (weather != null) {
            hashMap = new HashMap();
            hashMap.put(weatherDate, AppUtils.formatWeather(weather, tm, sm, pm));
        }
        return hashMap;
    }
}
