package com.khasang.forecast;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by aleksandrlihovidov on 15.12.15.
 */
public class DrawUtils {
    private static Context appContext;

    private static DrawUtils ourInstance = new DrawUtils();
    private float widthDpx;
    private int widthPx;

    public static DrawUtils getInstance() {
        return ourInstance;
    }

    public void init(Context context) {
        appContext = context.getApplicationContext();
        Display display = ((WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        widthPx = metrics.widthPixels;
        widthDpx = widthPx / metrics.density;
    }

    private DrawUtils() {

    }

    public float getWidthDpx() {
        return widthDpx;
    }

    public int getWidthPx() {
        return widthPx;
    }
}
