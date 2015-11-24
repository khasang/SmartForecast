package com.khasang.forecast.location;

/**
 * Created by xsobolx on 24.11.2015.
 */
public class GPSLocation implements iLocation{
    private String mName;
    private int mCityID;
    private Coordinate mCoordinate;

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    private double mLatitude;
    private double mLongitude;

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public int getCityID() {
        return mCityID;
    }

    @Override
    public void setCityID(int cityID) {
        mCityID = cityID;
    }

    @Override
    public Coordinate getCoordinate() {
        return mCoordinate;
    }

    @Override
    public void setCoordinate(Coordinate coordinate) {
        mCoordinate = coordinate;
        coordinate.setLatitude(mLatitude);
        coordinate.setLongitude(mLongitude);
    }

    public static iLocationFactory factory = new iLocationFactory() {
        @Override
        public iLocation getLocation() {
            return new GPSLocation();
        }
    };

}
