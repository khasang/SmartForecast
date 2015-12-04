package com.khasang.forecast;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public void addCurrentPosition(Set<WeatherStationFactory.ServiceType> serviceType) {
        Position p = new Position();
        // Получить название города
        // и
        // координаты
        // positions.add(p);
    }

    public void addFavouritePosition(String name, Set<WeatherStationFactory.ServiceType> serviceTypes) {
        Position p = new Position();
        p.setLocationName(name);
        p.setCityID(cityIdentificationCounter++);
        for (WeatherStationFactory.ServiceType stationType : serviceTypes) {
            p.addWeatherStation(stationType);
        }
        // Через геокодер получить и занести координаты
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(name, 3);
            if (addresses.size() == 0){
                Log.i(TAG, "Coordinates not found");
                Toast.makeText(mContext, "Координаты местоположения " + name + " не обнаружены.\nЛокация не добавлена", Toast.LENGTH_SHORT).show();
                return;
            }
            Address currentAddress = addresses.get(0);
            Coordinate coordinate = new Coordinate();
            coordinate.setLatitude(currentAddress.getLatitude());
            coordinate.setLongitude(currentAddress.getLongitude());
            p.setCoordinate(coordinate);
            Log.i(TAG, "Coordinate of " + name + " lat: " + currentAddress.getLatitude() + ", lon: " + currentAddress.getLongitude());
            mPositions.put(name, p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Position> getPositions() {
        return mPositions;
    }
}
