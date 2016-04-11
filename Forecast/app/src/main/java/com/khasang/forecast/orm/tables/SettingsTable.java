package com.khasang.forecast.orm.tables;

import com.orm.SugarRecord;

/**
 * Created by maxim.kulikov on 16.03.2016.
 */

public class SettingsTable extends SugarRecord {

    public String currentTemperatureMetrics;
    public String currentSpeedMetrics;
    public String currentPressureMetrics;
    public String currentServiceType;
    public String currentTown;
    public double currentLatitude;
    public double currentLongtitude;

    public SettingsTable() {
    }

    public SettingsTable(String currentTemperatureMetrics, String currentSpeedMetrics, String currentPressureMetrics,
                         String currentServiceType, String currentTown, double curentLatitude, double curentLongtitude) {

        this.currentTemperatureMetrics = currentTemperatureMetrics;
        this.currentSpeedMetrics = currentSpeedMetrics;
        this.currentPressureMetrics = currentPressureMetrics;
        this.currentServiceType = currentServiceType;
        this.currentTown = currentTown;
        this.currentLatitude = curentLatitude;
        this.currentLongtitude = curentLongtitude;
    }

    public SettingsTable(String currentTemperatureMetrics, String currentSpeedMetrics, String currentPressureMetrics, String currentServiceType) {
        this.currentTemperatureMetrics = currentTemperatureMetrics;
        this.currentSpeedMetrics = currentSpeedMetrics;
        this.currentPressureMetrics = currentPressureMetrics;
        this.currentServiceType = currentServiceType;
    }
}
