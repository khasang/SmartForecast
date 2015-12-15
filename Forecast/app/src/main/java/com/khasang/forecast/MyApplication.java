package com.khasang.forecast;

import android.app.Application;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DrawUtils.getInstance().init(this);
    }
}
