package com.khasang.forecast.location;

import android.util.Log;

/**
 * Created by novoselov on 24.11.2015.
 */
public class LocationFactory{

    public static iLocation getLocation(iLocationFactory factory) {
        iLocation location = factory.getLocation();
        return location;
    }


//    private static final String TAG = LocationFactory.class.getSimpleName();
//
//    public LocationFactory() {
//
//    }
//
//
//    public GPSLocation createLocation() {
//        return new iLocation();
//    }
//
//    public GPSLocation getLocation(double latitude, double longitude){
//        mCurrentCoordinate = LocationManager.getGPSCoordinate(latitude, longitude);
//        mGPSLocation = new GPSLocation();
//        mGPSLocation.setCoordinate(mCurrentCoordinate);
//        Log.i(TAG, "Location created, latitude: " + latitude + ", longitude: " + longitude);
//        return mGPSLocation;
//    }
//
//    public GPSLocation createLocation(GPSLocation.LOCATION_TYPE lType, Coordinate coordinate) {
//        mGPSLocation = new GPSLocation();
//        mGPSLocation.setLocationType(lType);
//        switch (lType) {
//            case GPS_LOCATION:
//                mGPSLocation.setCoordinate(coordinate);
//
//                break;
//            case STATIC_LOCATION:
//                mGPSLocation.setCoordinate(coordinate);
//                break;
//            default:
//                break;
//        }
//        return mGPSLocation;
//    }

}

