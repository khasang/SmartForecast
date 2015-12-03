package com.khasang.forecast.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by maxim.kulikov on 02.12.15.
 */

public class SQLiteOpen extends SQLiteOpenHelper {

    private static final int version = 40;

    public SQLiteOpen(Context context, String name) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
