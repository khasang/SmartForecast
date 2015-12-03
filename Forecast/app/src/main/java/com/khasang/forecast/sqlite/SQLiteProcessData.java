package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteProcessData {

    public SQLiteWork sqLite;

    public SQLiteProcessData(Context context) {
        this.sqLite = new SQLiteWork(context, "Forecast.db");
    }

    public void saveTown(String town, String latitude, String longtitude) {
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_TOWN, new String[]{town, latitude, longtitude});
    }

    public void saveWeather(String townId, String date, String temperature, String temperatureMax, String temperatureMin) {
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_WEATHER, new String[]{townId, date, temperature, temperatureMax, temperatureMin});
    }

    public void saveSettings(String temperatureMetrics, String speedMetrics, String pressureMetrics, String currentServiceType) {
        sqLite.queryExExec(SQLiteFields.QUERY_INSERT_SETTINGS, new String[]{temperatureMetrics, speedMetrics, pressureMetrics, currentServiceType});
    }

    public void loadTownList() {
        String str1 = "";
        String str2 = "";
        String str3 = "";
        Cursor dataset = sqLite.queryOpen(SQLiteFields.QUERY_SELECT_TOWNS, null);

        if (dataset != null && dataset.getCount() != 0) {
            if (dataset.moveToFirst()) {
                do {

                    str1 = dataset.getString(dataset.getColumnIndex(SQLiteFields.TOWN));
                    str2 = dataset.getString(dataset.getColumnIndex(SQLiteFields.LATITUDE));
                    str3 = dataset.getString(dataset.getColumnIndex(SQLiteFields.LONGTITUDE));

                } while (dataset.moveToNext());
            }
        }
    }
}
