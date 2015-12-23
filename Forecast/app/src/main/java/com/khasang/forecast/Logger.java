package com.khasang.forecast;

import android.util.Log;

/**
 * Created by aleksandrlihovidov on 23.12.15.
 */
public class Logger {
    private static boolean isDebugMode = true;

    public static void println(String tag, String msg) {
        if (isDebugMode) {
            Log.i(tag, msg);
        }
    }
}
