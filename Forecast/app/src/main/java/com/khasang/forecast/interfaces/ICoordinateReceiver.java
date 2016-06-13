package com.khasang.forecast.interfaces;

import com.khasang.forecast.position.Coordinate;

/**
 * Created by roman on 12.06.16.
 */
public interface ICoordinateReceiver {
    void updatePositionCoordinate (String city, Coordinate coordinate);
}
