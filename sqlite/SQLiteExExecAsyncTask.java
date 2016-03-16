package com.khasang.forecast.sqlite;

import android.os.AsyncTask;

/**
 * Класс для выполнения метода SQLiteWork.getInstance().queryExExec в отдельном AsyncTask потоке.
 *
 * @author maxim.kulikov
 */

public class SQLiteExExecAsyncTask extends AsyncTask<Void, Void, Void> {

    private final String query;
    private Object[] bindArgs;

    public SQLiteExExecAsyncTask(String query, Object[] bindArgs) {
        this.query = query;
        this.bindArgs = bindArgs;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            SQLiteWork.getInstance().queryExExec(query, bindArgs);
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
