package com.khasang.forecast;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

import io.fabric.sdk.android.Fabric;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class MyApplication extends Application {

    private static Context context;
    boolean debugMode = true;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!debugMode) {
            Fabric.with(this, new Crashlytics());
        }
        DrawUtils.getInstance().init(this);
        MyApplication.context = getApplicationContext();
        Stetho.initializeWithDefaults(this);

        //FirebaseCrash.report(new Exception("My first Android non-fatal error"));

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
