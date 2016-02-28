package com.khasang.forecast;

import android.app.Application;
import android.content.Context;
import com.facebook.stetho.Stetho;
import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class MyApplication extends Application {

    private static Context context;
    boolean debugMode = false;

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;


    @Override
    public void onCreate() {
        super.onCreate();
        if (!debugMode) {
            Fabric.with(this, new Crashlytics());
        }
        DrawUtils.getInstance().init(this);
        MyApplication.context = getApplicationContext();
        Stetho.initializeWithDefaults(this);

        refWatcher = LeakCanary.install(this);

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
