package com.khasang.forecast.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by uoles on 19.01.2016.
 */

public class SQLiteOpenAsyncTask extends AsyncTask<Void, Void, Void> {

    private final SQLiteOpen db;
    private final String query;
    private String[] bindArgs;
    private Cursor cursor;
    private SQLiteDatabase sqlDatabase;

    public SQLiteOpenAsyncTask(SQLiteOpen db, String query, String[] bindArgs, Cursor cursor) {
        this.db = db;
        this.query = query;
        this.bindArgs = bindArgs;
        this.cursor = cursor;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        sqlDatabase = db.getReadableDatabase();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            cursor = sqlDatabase.rawQuery(query, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
    }
}
