package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

/**
 * Класс для работы с запросами БД.
 *
 * @author maxim.kulikov
 */

public class SQLiteWork {

    private SQLiteDatabase sqlDatabaseRead;
    private SQLiteDatabase sqlDatabaseWrite;
    private SQLiteOpen dbWork;
    private SQLiteExecAsyncTask execAsyncTask;
    private SQLiteExExecAsyncTask exExecAsyncTask;
    private int newVersion = 4;

    private static class ManagerHolder {
        private final static SQLiteWork instance = new SQLiteWork();
    }

    public static SQLiteWork getInstance() {
        return ManagerHolder.instance;
    }

    public void init(Context context, String dbName) {
        // инициализация класса обёртки
        dbWork = new SQLiteOpen(context, dbName, newVersion);
    }

    public void checkOpenDatabaseRead() {
        if (sqlDatabaseRead != null && sqlDatabaseRead.isOpen()) {
            sqlDatabaseRead.close();
        }
    }

    public void qExec(String query) {
        try {
            execAsyncTask = new SQLiteExecAsyncTask(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void qExExec(String query, Object[] bindArgs) {
        try {
            exExecAsyncTask = new SQLiteExExecAsyncTask(query, bindArgs);
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
