package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteWork {

    public SQLiteDatabase sqlDatabase;
    public SQLiteOpen dbWork;
    private int newVersion = 1;

    public SQLiteWork(Context context, String dbName) {
        // инициализация класса обёртки
        dbWork = new SQLiteOpen(context, dbName, newVersion);
        sqlDatabase = dbWork.getWritableDatabase();
        dbWork.onUpgrade(sqlDatabase, sqlDatabase.getVersion(), newVersion);
    }

    public void checkOpenDatabase() {
        if (sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
    }

    public void closeDatabase() {
        checkOpenDatabase();
    }

    public void queryExec(String query) {
        try {
            checkOpenDatabase();
            sqlDatabase = dbWork.getWritableDatabase();
            sqlDatabase.execSQL(query);
        } catch (Exception e) {
            System.out.println("queryExec ERROR " + e);
        }
    }

    public void queryExExec(String sql, Object[] bindArgs) {
        try {
            checkOpenDatabase();
            sqlDatabase = dbWork.getWritableDatabase();
            sqlDatabase.execSQL(sql, bindArgs);
        } catch (Exception e) {
            System.out.println("queryExExec ERROR " + e);
        }
    }

    public Cursor queryOpen(String sql, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            checkOpenDatabase();
            sqlDatabase = dbWork.getWritableDatabase();
            cursor = sqlDatabase.rawQuery(sql, selectionArgs);
        } catch(Exception e) {
            System.out.println("queryOpen ERROR " + e);
        }
        return cursor;
    }
}
