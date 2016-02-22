package com.khasang.forecast.sqlite;

import android.os.AsyncTask;

/**
 * Класс для выполнения метода SQLiteWork.getInstance().queryExec в отдельном AsyncTask потоке.
 *
 * @author maxim.kulikov
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
