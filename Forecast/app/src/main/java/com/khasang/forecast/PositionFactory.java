package com.khasang.forecast;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionFactory {
    private final static String TAG = PositionFactory.class.getSimpleName();

    private HashMap<String, Position> mPositions;
    private Context mContext;

    public PositionFactory(Context context) {
        mPositions = new HashMap<String, Position>();
        mContext = context;
    }

    public void addCurrentPosition() {
        Position p = new Position();
        // Получить название города
        // и
        // координаты
        // positions.add(p);
    }

    public PositionFactory addFavouritePosition(String name) {
        Position p = new Position();
        p.setLocationName(name);
        // Через геокодер получить и занести координаты
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(name, 3);
            if (addresses.size() == 0){
                Log.i(TAG, "Coordinates not found");
                return null;
            }
            Address currentAddress = addresses.get(0);
            Coordinate coordinate = new Coordinate();
            coordinate.setLatitude(currentAddress.getLatitude());
            coordinate.setLongitude(currentAddress.getLongitude());
            p.setCoordinate(coordinate);
            Log.i(TAG, "Coordinate of " + name + " lat: " + currentAddress.getLatitude() + ", lon: " + currentAddress.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPositions.put(name, p);
        return this;
    }

    public Map<String, Position> getPositions() {
        return mPositions;
    }
}
