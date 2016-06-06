package com.khasang.forecast;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

import io.fabric.sdk.android.Fabric;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        /** Проверяет, если Debug mode, то не отправляет отчеты об ошибках */
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        DrawUtils.getInstance().init(this);
        MyApplication.context = getApplicationContext();
        Stetho.initializeWithDefaults(this);

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
