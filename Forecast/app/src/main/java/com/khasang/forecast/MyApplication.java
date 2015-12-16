package com.khasang.forecast;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DrawUtils.getInstance().init(this);
        MyApplication.context = getApplicationContext();
        Stetho.initializeWithDefaults(this);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
