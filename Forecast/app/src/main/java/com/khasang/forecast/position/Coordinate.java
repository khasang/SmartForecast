package com.khasang.forecast.position;

/**
 * Created by Veda on 24.11.15.
 */

public class Coordinate implements Comparable<Coordinate> {
    private double latitude;
    private double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinate() {

    }

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
    public int compareTo(Coordinate another) {
        if (another == null) {
            return 1;
        }
        if (latitude == another.getLatitude() && longitude == another.getLongitude()) {
            return 0;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "Coordinate{latitude=" + latitude + ", longitude=" + longitude + "}";
    }

    public String convertToTimezoneUrlParameterString() {
        return String.valueOf(latitude) + "," + String.valueOf(longitude);
    }
}

