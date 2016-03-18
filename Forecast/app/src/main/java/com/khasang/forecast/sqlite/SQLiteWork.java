package com.khasang.forecast.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.khasang.forecast.MyApplication;

/**
 * Класс для работы с запросами БД.
 *
 * @author maxim.kulikov
 */

public class SQLiteWork {

    private SQLiteDatabase sqlDatabaseRead;
    private SQLiteDatabase sqlDatabaseWrite;
    private SQLiteOpen dbWork;
    private final int CURRENT_DB_VERSION = 5;
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

    public void checkOpenDatabaseRead() {
        if (sqlDatabaseRead != null && sqlDatabaseRead.isOpen()) {
            sqlDatabaseRead.close();
        }
    }

    public void qExec(String query) {
        try {
            new SQLiteExecAsyncTask(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void qExExec(String query, Object[] bindArgs) {
        try {
            new SQLiteExExecAsyncTask(query, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void queryExec(String query) {
        sqlDatabaseWrite = dbWork.getWritableDatabase();
        try {
            sqlDatabaseWrite.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlDatabaseWrite.close();
        }
    }

    public synchronized void queryExExec(String query, Object[] bindArgs) {
        sqlDatabaseWrite = dbWork.getWritableDatabase();
        try {
            sqlDatabaseWrite.execSQL(query, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlDatabaseWrite.close();
        }
    }

    public synchronized Cursor queryOpen(String query, String[] bindArgs) {
        checkOpenDatabaseRead();
        sqlDatabaseRead = dbWork.getReadableDatabase();
        try {
            return sqlDatabaseRead.rawQuery(query, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
