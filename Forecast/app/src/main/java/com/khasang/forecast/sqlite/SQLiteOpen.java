package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteOpen extends SQLiteOpenHelper {

    public SQLiteOpen(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public void checkTable(String tableName, String query, SQLiteDatabase db) {
        try {
            if (!isTableExists(tableName, db)) {
                db.execSQL(query);
                if (tableName.equals(SQLiteFields.TABLE_SETTINGS)) {
                    db.execSQL(SQLiteFields.QUERY_INSERT_SETTINGS, new String[]{"OPEN_WEATHER_MAP", "", "CELSIUS", "METER_PER_SECOND", "HPA"});
                }
            }
        } catch (Exception e) {
            System.out.println("createTables ERROR " + e);
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        checkTable(SQLiteFields.TABLE_TOWNS, SQLiteFields.QUERY_CREATE_TABLE_TOWNS, db);
        checkTable(SQLiteFields.TABLE_WEATHER, SQLiteFields.QUERY_CREATE_TABLE_WEATHER, db);
        checkTable(SQLiteFields.TABLE_SETTINGS, SQLiteFields.QUERY_CREATE_TABLE_SETTINGS, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {

            try {
                db.beginTransaction();

                db.execSQL(SQLiteFields.QUERY_DELETE_TABLE_TOWNS);
                db.execSQL(SQLiteFields.QUERY_DELETE_TABLE_WEATHER);
                db.execSQL(SQLiteFields.QUERY_DELETE_TABLE_SETTINGS);

                onCreate(db);

                db.setVersion(newVersion);
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }

    }
}
