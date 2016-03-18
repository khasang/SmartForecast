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

    private ArrayList<HashMap<String, String>> townList = null;
    private HashMap<String, String> settingsMap;
    private int dbOldVersion = 0;

    public SQLiteOpen(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteFields.QUERY_CREATE_TABLE_TOWNS);
        db.execSQL(SQLiteFields.QUERY_CREATE_TABLE_WEATHER);
        db.execSQL(SQLiteFields.QUERY_CREATE_TABLE_SETTINGS);

        db.execSQL(SQLiteFields.QUERY_INSERT_SETTINGS, new String[]{"OPEN_WEATHER_MAP", "", "CELSIUS", "METER_PER_SECOND", "HPA", "", ""});
    }

    public void setTownList(SQLiteDatabase db) throws Exception {
        HashMap<String, String> map;
        try {
            if (townList != null) {
                for (int i = 0; i < townList.size(); i++) {
                    map = townList.get(i);
                    switch (dbOldVersion) {
                        case 3:
                            db.execSQL(SQLiteFields.QUERY_INSERT_TOWN_v4, new String[]{
                                    map.get(SQLiteFields.TOWN),
                                    map.get(SQLiteFields.LATITUDE),
                                    map.get(SQLiteFields.LONGITUDE)
                            });
                            break;

                        default:
                            db.execSQL(SQLiteFields.QUERY_INSERT_TOWN_v5, new String[]{
                                    map.get(SQLiteFields.TOWN),
                                    map.get(SQLiteFields.LATITUDE),
                                    map.get(SQLiteFields.LONGITUDE),
                                    map.get(SQLiteFields.FAVORITE)
                            });
                            break;
                    }
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
                        switch (dbOldVersion) {
                            case 3:
                                map.put(SQLiteFields.TOWN, dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN)));
                                map.put(SQLiteFields.LATITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.LATITUDE)));
                                map.put(SQLiteFields.LONGITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.LONGITUDE)));
                                break;

                            default:
                                map.put(SQLiteFields.TOWN, dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN)));
                                map.put(SQLiteFields.LATITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.LATITUDE)));
                                map.put(SQLiteFields.LONGITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.LONGITUDE)));
                                map.put(SQLiteFields.FAVORITE, dataset.getString(dataset.getColumnIndex(SQLiteFields.FAVORITE)));
                                break;
                        }
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
                switch (dbOldVersion) {
                    case 3:
                    case 4:
                        db.execSQL(SQLiteFields.QUERY_UPDATE_SETTINGS_v4, new String[]{
                                settingsMap.get(SQLiteFields.CURRENT_STATION),
                                settingsMap.get(SQLiteFields.CURRENT_TOWN),
                                settingsMap.get(SQLiteFields.CURRENT_TEMPIRATURE_METRICS),
                                settingsMap.get(SQLiteFields.CURRENT_SPEED_METRICS),
                                settingsMap.get(SQLiteFields.CURRENT_PRESSURE_METRICS)
                        });
                        break;

                    default:
                        db.execSQL(SQLiteFields.QUERY_UPDATE_SETTINGS_v5, new String[]{
                                settingsMap.get(SQLiteFields.CURRENT_STATION),
                                settingsMap.get(SQLiteFields.CURRENT_TOWN),
                                settingsMap.get(SQLiteFields.CURRENT_TEMPIRATURE_METRICS),
                                settingsMap.get(SQLiteFields.CURRENT_SPEED_METRICS),
                                settingsMap.get(SQLiteFields.CURRENT_PRESSURE_METRICS),
                                settingsMap.get(SQLiteFields.CURRENT_LATITUDE),
                                settingsMap.get(SQLiteFields.CURRENT_LONGITUDE)
                        });
                }
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
                    switch (dbOldVersion) {
                        case 3:
                        case 4:
                            settingsMap.put(SQLiteFields.CURRENT_STATION, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_STATION)));
                            settingsMap.put(SQLiteFields.CURRENT_TOWN, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TOWN)));
                            settingsMap.put(SQLiteFields.CURRENT_TEMPIRATURE_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TEMPIRATURE_METRICS)));
                            settingsMap.put(SQLiteFields.CURRENT_SPEED_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_SPEED_METRICS)));
                            settingsMap.put(SQLiteFields.CURRENT_PRESSURE_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_PRESSURE_METRICS)));
                            break;

                        default:
                            settingsMap.put(SQLiteFields.CURRENT_STATION, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_STATION)));
                            settingsMap.put(SQLiteFields.CURRENT_TOWN, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TOWN)));
                            settingsMap.put(SQLiteFields.CURRENT_TEMPIRATURE_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_TEMPIRATURE_METRICS)));
                            settingsMap.put(SQLiteFields.CURRENT_SPEED_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_SPEED_METRICS)));
                            settingsMap.put(SQLiteFields.CURRENT_PRESSURE_METRICS, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_PRESSURE_METRICS)));
                            settingsMap.put(SQLiteFields.CURRENT_LATITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_LATITUDE)));
                            settingsMap.put(SQLiteFields.CURRENT_LONGITUDE, dataset.getString(dataset.getColumnIndex(SQLiteFields.CURRENT_LONGITUDE)));
                            break;
                    }
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
            dbOldVersion = oldVersion;

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
