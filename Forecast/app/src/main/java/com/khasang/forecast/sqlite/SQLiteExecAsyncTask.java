package com.khasang.forecast.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by uoles on 19.01.2016.
 */

public class SQLiteExecAsyncTask extends AsyncTask<Void, Void, Void> {

    private final String query;

    public SQLiteExecAsyncTask(String query) {
        this.query = query;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            SQLiteWork.getInstance().queryExec(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
