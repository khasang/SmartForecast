package com.khasang.forecast.location;

import android.content.Context;
import android.util.Log;

/**
 * Created by novoselov on 24.11.2015.
 */
public class LocationFactory {
    private Location mLocation;
    private Coordinate mCurrentCoordinate;

    private static final String TAG = LocationFactory.class.getSimpleName();

    public LocationFactory() {

    }


    public Location createLocation() {
        return createLocation(Location.LOCATION_TYPE.GPS_LOCATION, new Coordinate());
    }

    public Location getLocation(double latitude, double longitude){
        mCurrentCoordinate = LocationManager.getGPSCoordinate(latitude, longitude);
        mLocation = new Location();
        mLocation.setCoordinate(mCurrentCoordinate);
        Log.i(TAG, "Location created, latitude: " + latitude + ", longitude: " + longitude);
        return mLocation;
    }

    public Location createLocation(Location.LOCATION_TYPE lType, Coordinate coordinate) {
        mLocation = new Location();
        mLocation.setLocationType(lType);
        switch (lType) {
            case GPS_LOCATION:
                mLocation.setCoordinate(coordinate);

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

