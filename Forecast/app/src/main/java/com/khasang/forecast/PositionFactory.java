package com.khasang.forecast;
import android.content.Context;

import com.khasang.forecast.sqlite.SQLiteProcessData;
import java.util.HashMap;


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
