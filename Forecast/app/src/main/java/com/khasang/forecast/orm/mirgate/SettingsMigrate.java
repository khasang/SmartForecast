package com.khasang.forecast.orm.mirgate;

import android.content.Context;
import android.content.SharedPreferences;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.orm.SettingsManager;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.stations.WeatherStationFactory;

/**
 * Created by maxim.kulikov on 22.03.2016.
 */

public class SettingsMigrate {

    private static final String APP_PREFERENCES = "ForecastSettings";
    private static final String CURRENT_TEMPIRATURE_METRICS = "CurrentTemperatureMetrics";
    private static final String CURRENT_SPEED_METRICS = "CurrentSpeedMetrics";
    private static final String CURRENT_PRESSURE_METRICS = "CurrentPressureMetrics";
    private static final String CURRENT_STATION = "CurrentServiceType";
    private static final String CURRENT_TOWN = "Town";
    private static final String CURRENT_LATITUDE = "Latitude";
    private static final String CURRENT_LONGTITUDE = "Longitude";

    private SharedPreferences mSettings;

    public SettingsMigrate() {
        mSettings = MyApplication.getAppContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public AppUtils.TemperatureMetrics getCurrentTemperatureMetrics() {
        if (mSettings.contains(CURRENT_TEMPIRATURE_METRICS)) {
            return AppUtils.TemperatureMetrics.valueOf(mSettings.getString(CURRENT_TEMPIRATURE_METRICS, "CELSIUS"));
        }
        return AppUtils.TemperatureMetrics.CELSIUS;
    }

    public void setCurrentTemperatureMetrics(String currentTemperatureMetrics) {
        try {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(CURRENT_TEMPIRATURE_METRICS, currentTemperatureMetrics);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppUtils.SpeedMetrics getCurrentSpeedMetrics() {
        if (mSettings.contains(CURRENT_SPEED_METRICS)) {
            return AppUtils.SpeedMetrics.valueOf(mSettings.getString(CURRENT_SPEED_METRICS, "METER_PER_SECOND"));
        }
        return AppUtils.SpeedMetrics.METER_PER_SECOND;
    }

    public void setCurrentSpeedMetrics(String currentSpeedMetrics) {
        try {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(CURRENT_SPEED_METRICS, currentSpeedMetrics);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppUtils.PressureMetrics getCurrentPressureMetrics() {
        if (mSettings.contains(CURRENT_PRESSURE_METRICS)) {
            return AppUtils.PressureMetrics.valueOf(mSettings.getString(CURRENT_PRESSURE_METRICS, "HPA"));
        }
        return AppUtils.PressureMetrics.HPA;
    }

    public void setCurrentPressureMetrics(String currentPressureMetrics) {
        try {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(CURRENT_PRESSURE_METRICS, currentPressureMetrics);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WeatherStationFactory.ServiceType getCurrentServiceType() {
        if (mSettings.contains(CURRENT_STATION)) {
            return WeatherStationFactory.ServiceType.valueOf(mSettings.getString(CURRENT_STATION, "OPEN_WEATHER_MAP"));
        }
        return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
    }

    public void setCurrentServiceType(String currentServiceType) {
        try {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(CURRENT_STATION, currentServiceType);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentTown() {
        if (mSettings.contains(CURRENT_TOWN)) {
            return mSettings.getString(CURRENT_TOWN, "");
        }
        return "";
    }

    public void setCurrentTown(String currentTown) {
        try {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(CURRENT_TOWN, currentTown);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Coordinate getCurrentCoordinate() {
        if (mSettings.contains(CURRENT_LATITUDE) && mSettings.contains(CURRENT_LONGTITUDE)) {
            return new Coordinate(
                    Double.parseDouble(mSettings.getString(CURRENT_LATITUDE, "0")),
                    Double.parseDouble(mSettings.getString(CURRENT_LONGTITUDE, "0"))
            );
        } else {
            return new Coordinate(0,0);
        }
    }

    public void setCurrentCoordinate(double latitude, double longitude) {
        try {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(CURRENT_LATITUDE, Double.toString(latitude));
            editor.putString(CURRENT_LONGTITUDE, Double.toString(longitude));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSettingsInOrm() {
        try {
            SettingsManager settingsManager = new SettingsManager();
            settingsManager.saveCurrStation(getCurrentServiceType());
            settingsManager.saveCurrTown(getCurrentTown());
            settingsManager.saveCurrLatLng(getCurrentCoordinate());
            settingsManager.saveCurrMetrics(
                    getCurrentTemperatureMetrics(),
                    getCurrentSpeedMetrics(),
                    getCurrentPressureMetrics()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
