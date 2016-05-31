package com.khasang.forecast.api;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;

/**
 * Created by roman on 31.05.16.
 */
public class GoogleMapsGeocoding {
    private final static String TAG = GoogleMapsGeocoding.class.getSimpleName();
    private final static String PLACE_API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private final static String API_KEY = MyApplication.getAppContext().getString(R.string.google_maps_geocoding);

}
