package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.khasang.forecast.Coordinate;
import com.khasang.forecast.Position;
import com.khasang.forecast.PositionManager;
import com.khasang.forecast.Precipitation;
import com.khasang.forecast.Weather;
import com.khasang.forecast.WeatherStation;
import com.khasang.forecast.WeatherStationFactory;
import com.khasang.forecast.Wind;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteProcessData {

    public SQLiteWork sqLite;
    public SimpleDateFormat dtFormat;

    public SQLiteProcessData(Context context) {
        this.sqLite = new SQLiteWork(context, "Forecast.db");
        dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    public void closeDatabase() {
        sqLite.closeDatabase();
    }

    // Сохранение города с координатами (перед сохранением списка нужно очистить старый)
    public void saveTown(String town, double latitude, double longitude) {
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_TOWN, new String[]{town, Double.toString(latitude), Double.toString(longitude)});
    }

    // Сохранение погоды, удаление старой погоды.
    public void saveWeather(WeatherStationFactory.ServiceType serviceType, String townName, Calendar date, Weather weather) {

        deleteOldWeather(serviceType, townName, date);

        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]
            {serviceType.name(), townName, dtFormat.format(date.getTime()), Double.toString(weather.getTemperature()), Double.toString(weather.getTemp_max()),
                    Double.toString(weather.getTemp_min()), Double.toString(weather.getPressure()),
                    Integer.toString(weather.getHumidity()), weather.getDescription(), weather.getWindDirection().name(),
                    Double.toString(weather.getWindPower()), weather.getPrecipitation().name()});
    }

    // Сохранение настроек
    public void saveSettings(WeatherStation currentStation) {
        sqLite.queryExExec(SQLiteFields.QUERY_UPDATE_CURRSTATION_SETTING, new String[]{currentStation.getServiceType().name()});
    }

    public void saveSettings(Position currPosition) {
        sqLite.queryExExec(SQLiteFields.QUERY_UPDATE_CURRCITY_SETTING, new String[]{currPosition.getLocationName()});
    }

    public void saveSettings(PositionManager.TemperatureMetrics temperatureMetrics,
                             PositionManager.SpeedMetrics speedMetrics, PositionManager.PressureMetrics pressureMetrics) {
        sqLite.queryExExec(SQLiteFields.QUERY_UPDATE_METRICS_SETTINGS, new String[]{temperatureMetrics.name(), speedMetrics.name(), pressureMetrics.name()});
    }

    // Загрузка CurrentTown.
    public String loadСurrentTown() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TOWN));
                }
            }
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
        return "";
    }

    // Загрузка TemperatureMetrics.
    public PositionManager.TemperatureMetrics loadTemperatureMetrics() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return PositionManager.TemperatureMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TEMPIRATURE_METRICS)));
                }
            }
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return PositionManager.TemperatureMetrics.CELSIUS;
    }

    // Загрузка SpeedMetrics.
    public PositionManager.SpeedMetrics loadSpeedMetrics() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return PositionManager.SpeedMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_SPEED_METRICS)));
                }
            }
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return PositionManager.SpeedMetrics.METER_PER_SECOND;
    }

    // Загрузка PressureMetrics.  {HPA, MM_HG}
    public PositionManager.PressureMetrics loadPressureMetrics() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return PositionManager.PressureMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_PRESSURE_METRICS)));
                }
            }
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return PositionManager.PressureMetrics.HPA;
    }

    // Загрузка Station.
    public WeatherStationFactory.ServiceType loadStation() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    return WeatherStationFactory.ServiceType.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_STATION)));
                }
            }
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
        // Значение по умолчанию.
        return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
    }

    // Очистка таблицы от погоды, которая старше текущего дня.
    public void deleteOldWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Calendar date) {
        sqLite.queryExExec(SQLiteFields.QUERY_DELETE_OLD_DATA_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())});
    }

    // Очистка таблицы городов.
    public void deleteTowns() {
        sqLite.queryExec(SQLiteFields.QUERY_DELETE_DATA_TOWNS);
    }

    public void deleteTown(String name) {
        sqLite.queryExExec(SQLiteFields.QUERY_DELETE_DATA_TOWN, new String[]{name});
    }

    // Загрузка списка городов.
    public HashMap<String, Coordinate> loadTownList() {

        double townLat = 0;
        double townLong = 0;
        String townName = "";
        HashMap hashMap = new HashMap();

        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_TOWNS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    do {
                        townName = dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN));
                        townLat = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.LATITUDE));
                        townLong = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.LONGTITUDE));

                        Coordinate coordinate = new Coordinate(townLat, townLong);
                        hashMap.put(townName, coordinate);
                    } while (dataset.moveToNext());
                }
            }
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
        return hashMap;
    }

    // Загрузка погоды.
    public Weather loadWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Calendar date) {

        double TEMPIRATURE = 0;
        double TEMPIRATURE_MAX = 0;
        double TEMPIRATURE_MIN = 0;
        double PRESSURE = 0;
        double WIND_SPEED = 0;
        String DESCRIPTION = "";
        String WIND_DIRECTION = "";
        String PRECIPITATION_TYPE = "";
        int HUMIDITY = 0;
        Wind WIND = null;
        Precipitation PRECIPITATION = null;
        Weather weather = null;

        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())});
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    TEMPIRATURE = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.TEMPIRATURE));
                    TEMPIRATURE_MAX = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.TEMPIRATURE_MAX));
                    TEMPIRATURE_MIN = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.TEMPIRATURE_MIN));
                    PRESSURE = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.PRESSURE));
                    HUMIDITY = dataset.getInt(dataset.getColumnIndex(SQLiteFields.HUMIDITY));
                    DESCRIPTION = dataset.getString(dataset.getColumnIndex(SQLiteFields.DESCRIPTION));

                    WIND_DIRECTION = dataset.getString(dataset.getColumnIndex(SQLiteFields.WIND_DIRECTION));
                    WIND_SPEED = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.WIND_SPEED));
                    WIND = new Wind();
                    WIND.setDirection(WIND_DIRECTION);
                    WIND.setSpeed(WIND_SPEED);

                    PRECIPITATION_TYPE = dataset.getString(dataset.getColumnIndex(SQLiteFields.PRECIPITATION_TYPE));
                    PRECIPITATION = new Precipitation();
                    PRECIPITATION.setType(PRECIPITATION_TYPE);

                    weather = new Weather(TEMPIRATURE, TEMPIRATURE_MIN, TEMPIRATURE_MAX, PRESSURE, HUMIDITY, WIND, PRECIPITATION, DESCRIPTION);
                }
            }
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
        return weather;
    }
}
