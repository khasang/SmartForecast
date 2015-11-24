package com.khasang.forecast.location;

import android.util.Log;

/**
 * Created by novoselov on 24.11.2015.
 */
public class LocationFactory {
    private Location mLocation;
    private double mLatitude;
    private double mLongitude;

    private static final String TAG = LocationFactory.class.getSimpleName();

    public LocationFactory() {

    }

    public LocationFactory(double latitude, double longitude){
        mLatitude = latitude;
        mLongitude = longitude;
    }


    public Location createLocation() {
        return createLocation(Location.LOCATION_TYPE.GPS_LOCATION, new Coordinate());
    }

    public Location createLocation(Location.LOCATION_TYPE lType, Coordinate coordinate) {
        mLocation = new Location();
        mLocation.setLocationType(lType);
        switch (lType) {
            case GPS_LOCATION:
                coordinate.setLatitude(mLatitude);
                coordinate.setLongitude(mLongitude);
                mLocation.setCoordinate(coordinate);
                Log.i(TAG, "Location created, latitude: " + mLatitude + ", longitude: " + mLongitude);
                break;
            case STATIC_LOCATION:
                mLocation.setCoordinate(coordinate);
                break;
            default:
                break;
        }
        return mLocation;
    }

}

