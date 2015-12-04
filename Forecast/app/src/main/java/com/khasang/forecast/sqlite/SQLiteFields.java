package com.khasang.forecast.sqlite;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteFields {

    public static final String TABLE_TOWNS = "TOWNS";
    public static final String TABLE_WEATHER = "WEATHER";
    public static final String TABLE_SETTINGS = "SETTINGS";
    public static final String TABLE_STATIONS = "STATIONS";

    public static final String ID = "Id";

    public static final String TOWN = "Town";
    public static final String LATITUDE = "Latitude";
    public static final String LONGTITUDE = "Longtitude";

    public static final String TOWN_ID = "TownId";
    public static final String STATION_ID = "StationId";
    public static final String DATE = "Date";
    public static final String TEMPIRATURE = "Temperature";
    public static final String TEMPIRATURE_MIN = "TemperatureMin";
    public static final String TEMPIRATURE_MAX = "TemperatureMax";
    public static final String PRESSURE = "Pressure";
    public static final String HUMIDITY = "Humidity";
    public static final String WIND = "Wind";
    public static final String PRECIPITATION = "Precipitation";
    public static final String DESCRIPTION = "Description";
    public static final String WIND_DIRECTION = "WindDirection";
    public static final String WIND_SPEED = "WindSpeed";
    public static final String PRECIPITATION_TYPE = "PrecipitationType";

    public static final String CURRENT_TEMPIRATURE_METRICS = "CurrentTemperatureMetrics";
    public static final String CURRENT_SPEED_METRICS = "CurrentSpeedMetrics";
    public static final String CURRENT_PRESSURE_METRICS = "CurrentPressureMetrics";
    public static final String CURRENT_SERVICE_TYPE = "CurrentServiceType";

    public static final String STATION_TYPE = "StationType";

    public static final String QUERY_CREATE_TABLE_TOWNS  = "CREATE TABLE " + TABLE_TOWNS + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TOWN + " VARCHAR(30)," +
            LATITUDE + " VARCHAR(30)," +
            LONGTITUDE + " VARCHAR(30))";

    public static final String QUERY_CREATE_TABLE_WEATHER = "CREATE TABLE " + TABLE_WEATHER + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            STATION_ID + " VARCHAR(30)," +
            TOWN_ID + " VARCHAR(10)," +
            DATE + " DATETIME," +
            TEMPIRATURE + " VARCHAR(10)," +
            TEMPIRATURE_MAX + " VARCHAR(10)," +
            TEMPIRATURE_MIN + " VARCHAR(10)," +
            PRESSURE + " VARCHAR(20)," +
            HUMIDITY + " VARCHAR(20)," +
            WIND + " VARCHAR(20)," +
            PRECIPITATION + " VARCHAR(20)," +
            DESCRIPTION + " VARCHAR(30)," +
            WIND_DIRECTION + " VARCHAR(20)," +
            WIND_SPEED + " VARCHAR(20)," +
            PRECIPITATION_TYPE + " VARCHAR(20))";

    public static final String QUERY_CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CURRENT_SERVICE_TYPE + " VARCHAR(30)," +
            CURRENT_TEMPIRATURE_METRICS + " VARCHAR(30)," +
            CURRENT_SPEED_METRICS + " VARCHAR(30)," +
            CURRENT_PRESSURE_METRICS + " VARCHAR(30))";

    public static final String QUERY_CREATE_TABLE_STATIONS = "CREATE TABLE " + TABLE_STATIONS + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            STATION_TYPE + " VARCHAR(30))";

    public static final String QUERY_DELETE_TABLE_WEATHER = "DROP TABLE IF EXISTS " + TABLE_WEATHER;
    public static final String QUERY_DELETE_TABLE_TOWNS = "DROP TABLE IF EXISTS " + TABLE_TOWNS;
    public static final String QUERY_DELETE_TABLE_SETTINGS = "DROP TABLE IF EXISTS " + TABLE_SETTINGS;
    public static final String QUERY_DELETE_TABLE_STATIONS = "DROP TABLE IF EXISTS " + TABLE_STATIONS;

    public static final String QUERY_OBJECTS_COUNT = "SELECT COUNT(*) FROM SQLITE_MASTER WHERE TYPE = ? AND NAME = ?";
    public static final String QUERY_SELECT_TOWNS = "SELECT * FROM " + TABLE_TOWNS;

    public static final String QUERY_SELECT_WEATHER =
            "SELECT * FROM " + TABLE_WEATHER + " AS w " +
            " INNER JOIN " + TABLE_TOWNS + " AS t ON t." + ID + " = w." + TOWN_ID +
            " INNER JOIN " + TABLE_STATIONS + " AS s ON s." + ID + " = w." + STATION_ID +
            " WHERE s." + STATION_TYPE + " = ? and t." + TOWN + " = ? and w." + DATE + " = (SELECT MAX(" + DATE + ") " + TABLE_WEATHER + " WHERE " + DATE + " < ? )";

    public static final String QUERY_INSERT_TOWN = "INSERT INTO " + TABLE_TOWNS + " (" +
            TOWN + "," +
            LATITUDE + "," +
            LONGTITUDE + ") " +
            " VALUES ( ? , ? , ? )";

    public static final String QUERY_INSERT_WEATHER = "INSERT INTO " + TABLE_WEATHER + " (" +
            TOWN_ID + "," +
            STATION_ID + "," +
            DATE + "," +
            TEMPIRATURE + "," +
            TEMPIRATURE_MAX + "," +
            TEMPIRATURE_MIN + "," +
            PRESSURE + "," +
            HUMIDITY + "," +
            WIND + "," +
            PRECIPITATION + "," +
            DESCRIPTION + "," +
            WIND_DIRECTION + "," +
            WIND_SPEED + "," +
            PRECIPITATION_TYPE + ") " +
            " VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )";

    public static final String QUERY_INSERT_SETTINGS = "INSERT INTO " + TABLE_SETTINGS + " (" +
            CURRENT_SERVICE_TYPE + "," +
            CURRENT_TEMPIRATURE_METRICS + "," +
            CURRENT_SPEED_METRICS + "," +
            CURRENT_PRESSURE_METRICS + ") " +
            " VALUES ( ? , ? , ? , ? )";

    public static final String QUERY_INSERT_STATIONS = "INSERT INTO " + TABLE_STATIONS + " (" +
            STATION_TYPE + ") " +
            " VALUES ( ? )";


}
