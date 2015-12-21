package com.khasang.forecast;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;


import com.khasang.forecast.sqlite.SQLiteProcessData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionFactory {
    private final static String TAG = "MyTAG";
    private static int cityIdentificationCounter;

    static {
        cityIdentificationCounter = 0;
    }

    private HashMap<String, Position> mPositions;
    private Context mContext;

    public PositionFactory(Context context) {
        mPositions = new HashMap<String, Position>();
        mContext = context;
    }

    public PositionFactory(Context context, HashMap<String, Position> positions) {
        mPositions = positions;
        mContext = context;
    }

    public void addCurrentPosition() {
        Position p = new Position();
        // Получить название города
        // и
        // координаты
        // positions.add(p);
    }

    public void addFavouritePosition(String name, SQLiteProcessData dbm) {
        Position p = new Position();
        p.setLocationName(name);
        p.setCityID(cityIdentificationCounter++);
        // Через геокодер получить и занести координаты
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(name, 3);
            if (addresses.size() == 0){
                Log.i(TAG, "Coordinates not found");
                Toast.makeText(mContext, String.format(mContext.getString(R.string.coordinates_not_found), name), Toast.LENGTH_SHORT).show();
                return;
            }
            Address currentAddress = addresses.get(0);
            Coordinate coordinate = new Coordinate();
            coordinate.setLatitude(currentAddress.getLatitude());
            coordinate.setLongitude(currentAddress.getLongitude());
            p.setCoordinate(coordinate);
            Log.i(TAG, "Coordinate of " + name + " lat: " + currentAddress.getLatitude() + ", lon: " + currentAddress.getLongitude());
            mPositions.put(name, p);
            dbm.saveTown(name, coordinate.getLatitude(), coordinate.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFavouritePosition(String name, Coordinate coordinates, SQLiteProcessData dbm) {
        Position p = new Position();
        p.setLocationName(name);
        p.setCityID(cityIdentificationCounter++);
        p.setCoordinate(coordinates);
        mPositions.put(name, p);
        dbm.saveTown(name, coordinates.getLatitude(), coordinates.getLongitude());
    }

    public void addFavouritePosition(String name, Coordinate coordinates) {
        Position position = new Position();
        position.setLocationName(name);
        position.setCityID(cityIdentificationCounter++);
        position.setCoordinate(coordinates);
        mPositions.put(name, position);
    }

    public HashMap<String, Position> getPositions() {
        return mPositions;
    }
}
