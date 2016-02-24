package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс наследник SQLiteOpenHelper для создания/изменения БД.
 *
 * @author maxim.kulikov
 */

public class SQLiteOpen extends SQLiteOpenHelper {

    ArrayList<HashMap<String, String>> townList = null;
    HashMap<String, String> settingsMap;

    public SQLiteOpen(Context context, String name, int version) {
        super(context, name, null, version);
    }
/*
    public void checkTable(String tableName, String query, SQLiteDatabase db) {
        try {
            if (!isTableExists(tableName, db)) {
                db.execSQL(query);
                if (tableName.equals(SQLiteFields.TABLE_SETTINGS)) {
                    db.execSQL(SQLiteFields.QUERY_INSERT_SETTINGS, new String[]{"OPEN_WEATHER_MAP", "", "CELSIUS", "METER_PER_SECOND", "HPA"});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isTableExists(String tableName, SQLiteDatabase db) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }

        int count = 0;
        Cursor cursor = db.rawQuery(SQLiteFields.QUERY_OBJECTS_COUNT, new String[]{"table", tableName});
        try {
            if (!cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }
*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteFields.QUERY_CREATE_TABLE_TOWNS);
        db.execSQL(SQLiteFields.QUERY_CREATE_TABLE_WEATHER);
        db.execSQL(SQLiteFields.QUERY_CREATE_TABLE_SETTINGS);

        db.execSQL(SQLiteFields.QUERY_INSERT_SETTINGS, new String[]{"OPEN_WEATHER_MAP", "", "CELSIUS", "METER_PER_SECOND", "HPA"});
    }

    public void setTownList(SQLiteDatabase db) throws Exception {
        HashMap<String, String> map;
        try {
            if (townList != null) {
                for (int i = 0; i < townList.size(); i++) {
                    map = townList.get(i);

                    db.execSQL(SQLiteFields.QUERY_INSERT_TOWN_1, new String[]{
                            map.get(SQLiteFields.TOWN),
                            map.get(SQLiteFields.LATITUDE),
                            map.get(SQLiteFields.LONGTITUDE)
                    });

                    /*  TODO Для третьего релиза!
                    db.execSQL(SQLiteFields.QUERY_INSERT_FULL_TOWN, new String[]{
                            map.get(SQLiteFields.TOWN),
                            map.get(SQLiteFields.LATITUDE),
                            map.get(SQLiteFields.LONGTITUDE),
                            map.get(SQLiteFields.FAVORITE)
                    });
                    */
                }
            }
        } catch (Exception e) {
            throw new Exception("setTownList error: " + e.getMessage());
        }
    }

    public void getTownList(SQLiteDatabase db) throws Exception {
        HashMap<String, String> map;
        Cursor dataset = db.rawQuery(SQLiteFields.QUERY_SELECT_TOWNS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    townList = new ArrayList<>();
                    do {
                        map = new HashMap<>();
                        map.put(SQLiteFields.TOWN, dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN)));
                        map.put(SQLiteFields.LATITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.LATITUDE)));
                        map.put(SQLiteFields.LONGTITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.LONGTITUDE)));
                        // TODO Для третьего релиза!
                        //map.put(SQLiteFields.FAVORITE, dataset.getString(dataset.getColumnIndex(SQLiteFields.FAVORITE)));
                        townList.add(map);
                    } while (dataset.moveToNext());
                }
            }
        } catch (Exception e) {
            throw new Exception("getTownList error: " + e.getMessage());
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
    }

    public void setSettings(SQLiteDatabase db) throws Exception {
        try {
            if (settingsMap != null) {
                db.execSQL(SQLiteFields.QUERY_UPDATE_SETTINGS, new String[]{
                        settingsMap.get(SQLiteFields.CURRENT_STATION),
                        settingsMap.get(SQLiteFields.CURRENT_TOWN),
                        settingsMap.get(SQLiteFields.CURRENT_TEMPIRATURE_METRICS),
                        settingsMap.get(SQLiteFields.CURRENT_SPEED_METRICS),
                        settingsMap.get(SQLiteFields.CURRENT_PRESSURE_METRICS),
                });
            }
        } catch (Exception e) {
            throw new Exception("setSettings error: " + e.getMessage());
        }
    }

    public void getSettings(SQLiteDatabase db) throws Exception {
        Cursor dataset = db.rawQuery(SQLiteFields.QUERY_SELECT_SETTINGS, null);
        try {
            if (dataset != null && dataset.getCount() != 0) {
                if (dataset.moveToFirst()) {
                    settingsMap = new HashMap<>();
                    settingsMap.put(SQLiteFields.CURRENT_STATION, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_STATION)));
                    settingsMap.put(SQLiteFields.CURRENT_TOWN, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TOWN)));
                    settingsMap.put(SQLiteFields.CURRENT_TEMPIRATURE_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TEMPIRATURE_METRICS)));
                    settingsMap.put(SQLiteFields.CURRENT_SPEED_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_SPEED_METRICS)));
                    settingsMap.put(SQLiteFields.CURRENT_PRESSURE_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_PRESSURE_METRICS)));
                }
            }
        } catch (Exception e) {
            throw new Exception("getSettings error: " + e.getMessage());
        } finally {
            if (dataset != null) {
                dataset.close();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Выгрузка в память списка городов
            getTownList(db);
            // Выгрузка в память настроек
            getSettings(db);
            // Пересоздание таблиц
            recreate(db);
            // Запись списка городов в новые таблицы
            setTownList(db);
            // Запись настроек в новые таблицы
            setSettings(db);
        } catch (Exception e) {
            e.printStackTrace();
            // При ошибке пересоздаем таблицы без сохранения данных
            recreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreate(db);
    }

    private void recreate(SQLiteDatabase db) {
        db.execSQL(SQLiteFields.QUERY_DELETE_TABLE_TOWNS);
        db.execSQL(SQLiteFields.QUERY_DELETE_TABLE_WEATHER);
        db.execSQL(SQLiteFields.QUERY_DELETE_TABLE_SETTINGS);

        onCreate(db);
    }
}
