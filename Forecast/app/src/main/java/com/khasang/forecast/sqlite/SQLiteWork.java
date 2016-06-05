package com.khasang.forecast.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.khasang.forecast.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс для работы с запросами БД.
 *
 * @author maxim.kulikov
 */

public class SQLiteWork {

    private SQLiteOpen dbWork;
    private final int CURRENT_DB_VERSION = 6;
    private static volatile SQLiteWork instance;

    private SQLiteWork() {
    }

    public static SQLiteWork getInstance() {
        if (instance == null) {
            synchronized (SQLiteWork.class) {
                if (instance == null) {
                    instance = new SQLiteWork();
                    instance.init();
                }
            }
        }
        return instance;
    }

    public void init() {
        dbWork = new SQLiteOpen(MyApplication.getAppContext(), "Forecast.db", CURRENT_DB_VERSION);
    }

    public synchronized void removeInstance() {
        instance = null;
    }

    public void qExec(String query) {
        try {
            new SQLiteExecAsyncTask(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void qExec(String query, Object[] bindArgs) {
        try {
            new SQLiteExecAsyncTask(query, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void queryExec(String query, Object[] bindArgs) {
        SQLiteDatabase sqlDatabaseWrite = dbWork.getWritableDatabase();
        try {
            if (bindArgs != null) {
                sqlDatabaseWrite.execSQL(query, bindArgs);
            } else {
                sqlDatabaseWrite.execSQL(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlDatabaseWrite.close();
        }
    }

    public synchronized ArrayList<HashMap<String, String>> queryOpen(String query, String[] bindArgs) {
        ArrayList<HashMap<String, String>> recList = new ArrayList<>();
        SQLiteDatabase sqlDatabaseRead = dbWork.getReadableDatabase();
        try {
            Cursor cursor = sqlDatabaseRead.rawQuery(query, bindArgs);
            try {
                if (cursor != null && cursor.getCount() != 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            HashMap<String, String> newRec = new HashMap<>();
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                newRec.put(cursor.getColumnName(i), cursor.getString(i));
                            }
                            recList.add(newRec);
                        } while (cursor.moveToNext());
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlDatabaseRead.close();
        }
        return recList;
    }
}
