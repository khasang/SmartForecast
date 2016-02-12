package com.khasang.forecast.position;

import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.position.Position;
import com.khasang.forecast.sqlite.SQLiteProcessData;
import java.util.HashMap;


/**
 * Created by Роман on 26.11.2015.
 */

public class PositionFactory {
    private static int cityIdentificationCounter;

    static {
        cityIdentificationCounter = 1;
    }

    private HashMap<String, Position> mPositions;

    public PositionFactory() {
        mPositions = new HashMap<String, Position>();
    }

    public PositionFactory(HashMap<String, Position> positions) {
        mPositions = positions;
    }

    public void addFavouritePosition(String name, Coordinate coordinates, SQLiteProcessData dbm) {
        Position p = new Position();
        p.setLocationName(name);
        p.setCityID(cityIdentificationCounter++);
        p.setCoordinate(coordinates);
        mPositions.put(name, p);
        try{
            dbm.saveTown(name, coordinates.getLatitude(), coordinates.getLongitude());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
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
