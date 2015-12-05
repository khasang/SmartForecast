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

    public SQLiteWork(Context context, String dbName) {
        // инициализация класса обёртки
        dbWork = new SQLiteOpen(context, dbName);
        sqlDatabase = dbWork.getWritableDatabase();
        // удаление таблиц, пока не устаканится их структура
        // tablesDelete();
        tablesCreate();
    }

    public void checkTable(String tableName, String query) {
        try {
            if (!isTableExists(tableName)) {
                queryExec(query);
            }
        } catch (Exception e) {
            System.out.println("createTables ERROR " + e);
        }
    }

    public boolean isTableExists(String tableName) {
        if (tableName == null || sqlDatabase == null || !sqlDatabase.isOpen()) {
            return false;
        }
        Cursor cursor = sqlDatabase.rawQuery(SQLiteFields.QUERY_OBJECTS_COUNT, new String[] {"table", tableName});
        if (!cursor.moveToFirst()) {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public void queryExec(String query) {
        try {
            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
            sqlDatabase = dbWork.getWritableDatabase();
            sqlDatabase.execSQL(query);
        } catch (Exception e) {
            System.out.println("queryExec ERROR " + e);
        }
    }

    public void queryExExec(String sql, Object[] bindArgs) {
        try {
            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
            sqlDatabase = dbWork.getWritableDatabase();
            sqlDatabase.execSQL(sql, bindArgs);
        } catch (Exception e) {
            System.out.println("queryExExec ERROR " + e);
        }
    }

    public Cursor queryOpen(String sql, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            if(sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
            sqlDatabase = dbWork.getWritableDatabase();
            cursor = sqlDatabase.rawQuery(sql, selectionArgs);
        } catch(Exception e) {
            System.out.println("queryOpen ERROR " + e);
        }
        return cursor;
    }

    public void tablesCreate() {
        // создание таблиц
        checkTable(SQLiteFields.TABLE_TOWNS, SQLiteFields.QUERY_CREATE_TABLE_TOWNS);
        checkTable(SQLiteFields.TABLE_WEATHER, SQLiteFields.QUERY_CREATE_TABLE_WEATHER);
        checkTable(SQLiteFields.TABLE_SETTINGS, SQLiteFields.QUERY_CREATE_TABLE_SETTINGS);
    }

    public void tablesDelete() {
        // удаление таблиц
        sqlDatabase.execSQL(SQLiteFields.QUERY_DELETE_TABLE_TOWNS);
        sqlDatabase.execSQL(SQLiteFields.QUERY_DELETE_TABLE_WEATHER);
        sqlDatabase.execSQL(SQLiteFields.QUERY_DELETE_TABLE_SETTINGS);
    }
}
