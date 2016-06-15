package com.khasang.forecast;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.khasang.forecast.utils.DrawUtils;
import com.khasang.forecast.utils.LocaleUtils;

import io.fabric.sdk.android.Fabric;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class MyApplication extends Application {

    private static Context context;
    boolean debugMode = true;

    public static Context getAppContext() {
        return MyApplication.context;
    }

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = sharedPreferences.getString(getString(R.string.pref_language_key), null);
        LocaleUtils.updateResources(this, languageCode);
    }
}
