package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.khasang.forecast.Coordinate;
import com.khasang.forecast.PositionManager;
import com.khasang.forecast.Precipitation;
import com.khasang.forecast.Weather;
import com.khasang.forecast.WeatherStation;
import com.khasang.forecast.WeatherStationFactory;
import com.khasang.forecast.Wind;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteProcessData {

    public SQLiteWork sqLite;


    public SQLiteProcessData(Context context) {
        this.sqLite = new SQLiteWork(context, "Forecast.db");
        setDefaultValues();
    }

    void setDefaultValues() {
        saveTown("Москва", 55.75, 37.62);
        saveTown("Волгоград", 48.72, 44.5);
        saveTown("Краснодар", 45.03, 38.98);

        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]
                {"OPEN_WEATHER_MAP", "Краснодар", "2015-12-04 00:00:00", "0.0", "+1.0", "-1.0", "20.0",
                "5", "зима", "NORTHEAST", "5.0", "SNOW"});
    }

    // Сохранение города с координатами.
    public void saveTown(String town, double latitude, double longitude) {
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_TOWN, new String[]{town, Double.toString(latitude), Double.toString(longitude)});
    }

    // Сохранение погоды.
    public void saveWeather(WeatherStationFactory.ServiceType serviceType, String townName, Calendar date, Weather weather) {
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]
            {serviceType.name(), townName, date.toString(), Double.toString(weather.getTemperature()), Double.toString(weather.getTemp_max()),
                    Double.toString(weather.getTemp_min()), Double.toString(weather.getPressure()),
                    Integer.toString(weather.getHumidity()), weather.getDescription(), weather.getWindDirection().name(),
                    Double.toString(weather.getWindPower()), weather.getPrecipitation().name()});
    }

    // Сохранение насроек.
    public void saveSettings(String currentStation, String temperatureMetrics, String speedMetrics, String pressureMetrics) {
        deleteOldSettins();
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_SETTINGS, new String[]{currentStation, temperatureMetrics, speedMetrics, pressureMetrics});
    }

    // Загрузка TemperatureMetrics.
    public PositionManager.TemperatureMetrics loadTemperatureMetrics() {
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        if (dataset != null && dataset.getCount() != 0) {
            if (dataset.moveToFirst()) {
                switch (dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TEMPIRATURE_METRICS))) {
                    case "KELVIN": return PositionManager.TemperatureMetrics.KELVIN;
                    case "CELSIUS": return PositionManager.TemperatureMetrics.CELSIUS;
                    case "FAHRENHEIT": return PositionManager.TemperatureMetrics.FAHRENHEIT;
                }
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
                switch (dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_SPEED_METRICS))) {
                    case "METER_PER_SECOND": return PositionManager.SpeedMetrics.METER_PER_SECOND;
                    case "FOOT_PER_SECOND": return PositionManager.SpeedMetrics.FOOT_PER_SECOND;
                    case "KM_PER_HOURS": return PositionManager.SpeedMetrics.KM_PER_HOURS;
                    case "MILES_PER_HOURS": return PositionManager.SpeedMetrics.MILES_PER_HOURS;
                }
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
                switch (dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_PRESSURE_METRICS))) {
                    case "HPA": return PositionManager.PressureMetrics.HPA;
                    case "MM_HG": return PositionManager.PressureMetrics.MM_HG;
                }
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
                switch (dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_STATION))) {
                    case "OPEN_WEATHER_MAP": return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
                }
            }
        }
        // Значение по умолчанию.
        return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
    }

    // Очистка таблицы настроек.
    public void deleteOldSettins() {
        sqLite.queryExExec(SQLiteFields.QUERY_DELETE_DATA_SETTINGS, null);
    }

    // Очистка таблицы погоды.
    public void deleteOldWeather() {
        sqLite.queryExExec(SQLiteFields.QUERY_DELETE_DATA_WEATHER, null);
    }

    // Очистка таблицы городов.
    public void deleteOldTowns() {
        sqLite.queryExExec(SQLiteFields.QUERY_DELETE_DATA_TOWNS, null);
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
    public Weather loadWeather(WeatherStationFactory.ServiceType serviceType, String cityName, Date data) {

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

        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_WEATHER, new String[]{serviceType.name(), cityName, dtFormat.format(data)});

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
