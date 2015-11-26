package com.khasang.forecast;

import java.util.ArrayList;

/**
 * Created by Роман on 26.11.2015.
 */

public class PositionFactory {
    private ArrayList<Position> positions;

    public PositionFactory() {
        positions = new ArrayList<Position>();
    }

    public PositionFactory addCurrentPosition() {
        Position p = new Position();
        // Получить название города
        // и
        // координаты
        // positions.add(p);
        return this;
    }

    public PositionFactory addFavouritePosition(String name) {
        Position p = new Position();
        p.setLocationName(name);
        // Через геокодер получить и занести координаты
        positions.add(p);
        return this;
    }

    public ArrayList<Position> create() {
        return positions;
    }
}
