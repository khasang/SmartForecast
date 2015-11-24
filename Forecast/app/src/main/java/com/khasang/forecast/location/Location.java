package com.khasang.forecast.location;

/**
 * Created by xsobolx on 24.11.2015.
 */
public class Location {
    private String mName;
    private int mCityID;
    private Coordinate mCoordinate;

    public enum LOCATION_TYPE{
        GPS_LOCATION,
        STATIC_LOCATION
    }

    private LOCATION_TYPE mLocationType;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getCityID() {
        return mCityID;
    }

    public void setCityID(int cityID) {
        mCityID = cityID;
    }

    public Coordinate getCoordinate() {
        return mCoordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        mCoordinate = coordinate;
    }

    public LOCATION_TYPE getLocationType() {
        return mLocationType;
    }

    public void setLocationType(LOCATION_TYPE locationType) {
        mLocationType = locationType;
    }
}
