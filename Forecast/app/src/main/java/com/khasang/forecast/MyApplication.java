package com.khasang.forecast;

import android.app.Application;
import android.content.Context;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class MyApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        DrawUtils.getInstance().init(this);
    }

    public static Context getAppContext() {
        return appContext;
    }
}
