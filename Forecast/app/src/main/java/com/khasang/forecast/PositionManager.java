package com.khasang.forecast;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionManager {
    private WeatherStation currStation;
    private Position currPosition;
    private ArrayList<WeatherStation> stations;
    private Map<String, Position> mPositions;
    private Context mContext;

    public PositionManager(Context context) {
        mContext = context;
    }

    public void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf.addOpenWeatherMap().create();
    }

    public void initPositions(List<String> favorites) throws NullPointerException{
        PositionFactory positionFactory = new PositionFactory(mContext);
        positionFactory = positionFactory.addCurrentPosition();
        for (String pos : favorites) {
            positionFactory = positionFactory.addFavouritePosition(pos);
        }
        mPositions = positionFactory.getPositions();
    }

    public Position getFavoritePosition(String name){
        return mPositions.get(name);
    }
}
