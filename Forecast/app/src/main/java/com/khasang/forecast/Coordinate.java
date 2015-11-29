package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Coordinate {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean compareTo(Coordinate another) {
        if (latitude == another.getLatitude() && longitude == another.getLongitude()) {
            return true;
        }
        return false;
    }
}

