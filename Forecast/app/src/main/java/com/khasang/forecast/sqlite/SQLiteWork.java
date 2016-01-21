package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteWork {

    private SQLiteDatabase sqlDatabase;
    private SQLiteOpen dbWork;
    private SQLiteExecAsyncTask execAsyncTask;
    private SQLiteExExecAsyncTask exExecAsyncTask;
    private int newVersion = 3;

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

    public void checkOpenDatabase() {
        if (sqlDatabase != null && sqlDatabase.isOpen()) {
            sqlDatabase.close();
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
        try {
            checkOpenDatabase();
            sqlDatabase = dbWork.getWritableDatabase();
            sqlDatabase.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void queryExExec(String query, Object[] bindArgs) {
        try {
            checkOpenDatabase();
            sqlDatabase = dbWork.getWritableDatabase();
            sqlDatabase.execSQL(query, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Cursor queryOpen(String query, String[] bindArgs) {
        Cursor cursor = null;
        try {
            checkOpenDatabase();
            sqlDatabase = dbWork.getReadableDatabase();
            cursor = sqlDatabase.rawQuery(query, bindArgs);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }
}
