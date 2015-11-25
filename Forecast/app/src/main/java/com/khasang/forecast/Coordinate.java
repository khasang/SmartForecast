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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        return Double.compare(that.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result = 1;
        int prime = 31;
        long temp = Double.doubleToLongBits(latitude);
        result = result * prime + (int)(temp - (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = result * prime + (int)(temp - (temp >>> 32));
        return result;
    }
}

