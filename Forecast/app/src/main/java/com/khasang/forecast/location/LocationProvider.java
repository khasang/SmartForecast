package com.khasang.forecast.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by xsobolx on 24.11.2015.
 */
public class LocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    public static final String TAG = LocationProvider.class.getSimpleName();

    private LocationCallback mLocationCallback;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;

    public interface LocationCallback{
        void handleNewLocation(Location location);
    }

    public LocationProvider(Context context, LocationCallback callback){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationCallback = callback;

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // реализовать чтобы можно было менять в настройках
                .setInterval(10 * 1000)
                .setFastestInterval(1000);

        mContext = context;

    }

    public void connect(){
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected");

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null){
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this);
        } else {
            mLocationCallback.handleNewLocation(mLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.toString());
    }
}
