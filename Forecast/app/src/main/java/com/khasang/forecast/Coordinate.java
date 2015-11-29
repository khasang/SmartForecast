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
    public int hashCode() {
        final int temp = 31;
        int result = 1;
        result = (int) (temp * result + latitude);
        result = (int) (temp * result + longitude);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        Coordinate coordinate = (Coordinate) o;
        if (latitude !=  coordinate.latitude)
            return false;
        if (longitude != coordinate.longitude)
            return false;
        return true;
    }
}

