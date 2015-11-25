package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */
public class Coordinate implements Comparable<Coordinate>{

    private double MIN_DISTANCE = 1;
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
    public int compareTo(Coordinate another) {
        if ((Math.abs(latitude - another.latitude) < MIN_DISTANCE) &&
                (Math.abs(longitude - another.longitude) < MIN_DISTANCE)) {
            return 0;
        }
        return 1;
    }
}

