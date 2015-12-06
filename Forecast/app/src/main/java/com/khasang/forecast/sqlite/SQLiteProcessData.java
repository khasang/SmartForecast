package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.khasang.forecast.Coordinate;
import com.khasang.forecast.PositionManager;
import com.khasang.forecast.Precipitation;
import com.khasang.forecast.Weather;
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
        setDefaultValues();
    }

    void setDefaultValues() {
        saveTown("Москва", 55.75, 37.62);
        saveTown("Волгоград", 48.72, 44.5);
        saveTown("Buenos Aires", 45.03, 38.98);

        deleteSettins();

        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]
                {"OPEN_WEATHER_MAP", "Москва", dtFormat.format(Calendar.getInstance().getTime()), "0.0", "+1.0", "-1.0", "20.0",
                        "5", "зима", "NORTHEAST", "5.0", "SNOW"});

        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]
                {"OPEN_WEATHER_MAP", "Краснодар", dtFormat.format(Calendar.getInstance().getTime()), "0.0", "+1.0", "-1.0", "20.0",
                        "5", "зима", "NORTHEAST", "5.0", "SNOW"});

        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]
                {"OPEN_WEATHER_MAP", "Волгоград", dtFormat.format(Calendar.getInstance().getTime()), "0.0", "+1.0", "-1.0", "20.0",
                        "5", "зима", "NORTHEAST", "5.0", "SNOW"});
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

    // Сохранение насроек.
    public void saveSettings(String currentStation, String temperatureMetrics, String speedMetrics, String pressureMetrics) {
        deleteSettins();
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_SETTINGS, new String[]{currentStation, temperatureMetrics, speedMetrics, pressureMetrics});
    }

    // Загрузка TemperatureMetrics.
    public PositionManager.TemperatureMetrics loadTemperatureMetrics() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        if (dataset != null && dataset.getCount() != 0) {
            if (dataset.moveToFirst()) {
                return PositionManager.TemperatureMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TEMPIRATURE_METRICS)));
            }
        }
        // Значение по умолчанию.
        return PositionManager.TemperatureMetrics.CELSIUS;
    }

    // Загрузка SpeedMetrics. {METER_PER_SECOND, FOOT_PER_SECOND, KM_PER_HOURS, MILES_PER_HOURS}
    public PositionManager.SpeedMetrics loadSpeedMetrics() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        if (dataset != null && dataset.getCount() != 0) {
            if (dataset.moveToFirst()) {
                return PositionManager.SpeedMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_SPEED_METRICS)));
            }
        }
        // Значение по умолчанию.
        return PositionManager.SpeedMetrics.METER_PER_SECOND;
    }

    // Загрузка PressureMetrics.  {HPA, MM_HG}
    public PositionManager.PressureMetrics loadPressureMetrics() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        if (dataset != null && dataset.getCount() != 0) {
            if (dataset.moveToFirst()) {
                return PositionManager.PressureMetrics.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_PRESSURE_METRICS)));
            }
        }
        // Значение по умолчанию.
        return PositionManager.PressureMetrics.HPA;
    }

    // Загрузка Station.
    public WeatherStationFactory.ServiceType loadStation() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        if (dataset != null && dataset.getCount() != 0) {
            if (dataset.moveToFirst()) {
                return WeatherStationFactory.ServiceType.valueOf(dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_STATION)));
            }
        }
        // Значение по умолчанию.
        return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
    }

    // Очистка таблицы настроек.
    public void deleteSettins() {
        sqLite.queryExec(SQLiteFields.QUERY_DELETE_DATA_SETTINGS);
    }

    // Очистка таблицы погоды.
    public void deleteWeather() {
        sqLite.queryExec(SQLiteFields.QUERY_DELETE_DATA_WEATHER);
    }

    // Очистка таблицы от погоды, которая старше текущего дня.
    public void deleteOldWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Calendar date) {
        sqLite.queryExExec(SQLiteFields.QUERY_DELETE_OLD_DATA_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())});
    }

    // Очистка таблицы городов.
    public void deleteTowns() {
        sqLite.queryExec(SQLiteFields.QUERY_DELETE_DATA_TOWNS);
    }

    // Загрузка списка городов.
    public HashMap<String, Coordinate> loadTownList() {

        double townLat = 0;
        double townLong = 0;
        String townName = "";
        HashMap hashMap = new HashMap();

        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_TOWNS, null);
        if (dataset != null && dataset.getCount() != 0) {
            if (dataset.moveToFirst()) {
                do {
                    townName = dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN));
                    townLat = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.LATITUDE));
                    townLong = dataset.getDouble(dataset.getColumnIndex(SQLiteFields.LONGTITUDE));

                    Coordinate coordinate = new Coordinate(townLat, townLong);
                    hashMap.put(townName, coordinate);
                } while (dataset.moveToNext());

                return hashMap;
            }
        }
        return null;
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
        Wind WIND;
        Precipitation PRECIPITATION;
        Weather weather = null;

        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(date.getTime())});
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
        return weather;
    }
}
