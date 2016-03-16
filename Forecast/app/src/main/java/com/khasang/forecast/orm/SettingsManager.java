package com.khasang.forecast.orm;

import android.util.Log;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.orm.tables.SettingsTable;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.stations.WeatherStation;
import com.khasang.forecast.stations.WeatherStationFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim.kulikov on 16.03.2016.
 */

public class SettingsManager {

    private String mTag = "SugarTest";

    public SettingsManager() {
        setDefaultSettings();
    }

    //Запись дефолтных настроек
    public void setDefaultSettings() {
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable == null) {
                new SettingsTable("CELSIUS", "METER_PER_SECOND", "HPA", "OPEN_WEATHER_MAP").save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Сохранение текущей станции в настройках
    public void saveCurrStation(WeatherStation currentStation) {
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                settingsTable.currentServiceType = currentStation.getServiceType().name();
                settingsTable.save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Сохранение текущих систем измерений в настройках
    public void saveCurrMetrics(AppUtils.TemperatureMetrics temperatureMetrics, AppUtils.SpeedMetrics speedMetrics, AppUtils.PressureMetrics pressureMetrics) {
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                settingsTable.currentTemperatureMetrics = temperatureMetrics.name();
                settingsTable.currentSpeedMetrics = speedMetrics.name();
                settingsTable.currentPressureMetrics = pressureMetrics.name();
                settingsTable.save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Сохранение текущего города в настройках
    public void saveCurrTown(String currentTown) {
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                settingsTable.currentTown = currentTown;
                settingsTable.save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Сохранение координат в настройках
    public void saveCurrLatLng(Coordinate coordinate) {
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                settingsTable.currentLatitude = coordinate.getLatitude();
                settingsTable.currentLongtitude = coordinate.getLongitude();
                settingsTable.save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Загрузка CurrentTown.
    public String loadСurrTown() {
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                return settingsTable.currentTown;
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return "";
    }

    // Загрузка CurrentLatLng.
    public Coordinate loadСurrLatLng() {
        List<SettingsTable> list = new ArrayList<>();
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                return new Coordinate(settingsTable.currentLatitude, settingsTable.currentLongtitude);
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return null;
    }

    // Загрузка CurrentTemperatureMetrics.
    public AppUtils.TemperatureMetrics loadСurrTemperatureMetrics() {
        List<SettingsTable> list = new ArrayList<>();
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                return AppUtils.TemperatureMetrics.valueOf(settingsTable.currentTemperatureMetrics);
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        // Значение по умолчанию.
        return AppUtils.TemperatureMetrics.CELSIUS;
    }

    // Загрузка CurrentSpeedMetrics.
    public AppUtils.SpeedMetrics loadСurrSpeedMetrics() {
        List<SettingsTable> list = new ArrayList<>();
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                return AppUtils.SpeedMetrics.valueOf(settingsTable.currentSpeedMetrics);
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        // Значение по умолчанию.
        return AppUtils.SpeedMetrics.METER_PER_SECOND;
    }

    // Загрузка CurrentPressureMetrics.  {HPA, MM_HG}
    public AppUtils.PressureMetrics loadСurrPressureMetrics() {
        List<SettingsTable> list = new ArrayList<>();
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                return AppUtils.PressureMetrics.valueOf(settingsTable.currentPressureMetrics);
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        // Значение по умолчанию.
        return AppUtils.PressureMetrics.HPA;
    }

    // Загрузка CurrentStation.
    public WeatherStationFactory.ServiceType loadСurrStation() {
        List<SettingsTable> list = new ArrayList<>();
        try {
            SettingsTable settingsTable = SettingsTable.findById(SettingsTable.class, 1);
            if (settingsTable != null) {
                return WeatherStationFactory.ServiceType.valueOf(settingsTable.currentServiceType);
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        // Значение по умолчанию.
        return WeatherStationFactory.ServiceType.OPEN_WEATHER_MAP;
    }
}
