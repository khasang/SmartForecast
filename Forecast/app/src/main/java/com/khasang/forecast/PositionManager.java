package com.khasang.forecast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionManager {
    private WeatherStation currStation;
    private Position currPosition;
    private ArrayList<WeatherStation> stations;
    private ArrayList<Position> positions;

    public PositionManager() {

    }

    public void initStations() {
        WeatherStationFactory wsf = new WeatherStationFactory();
        stations = wsf.addOpenWeatherMap().create();
    }

    public void initPositions(List<String> favorites) {
        PositionFactory positionFactory = new PositionFactory();
        positionFactory = positionFactory.addCurrentPosition();
        for (String pos : favorites) {
            positionFactory = positionFactory.addFavouritePosition(pos);
        }
        positions = positionFactory.create();
    }
}
