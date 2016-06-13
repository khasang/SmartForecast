package com.khasang.forecast.interfaces;

import com.khasang.forecast.position.Coordinate;

/**
 * Created by roman on 13.06.16.
 */
public interface ILocationNameReceiver {
    void updateLocation(String city, Coordinate coordinate);
}
