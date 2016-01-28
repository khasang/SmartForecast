package com.khasang.forecast.position;

/**
 * Created by roman on 29.01.16.
 */
public interface IPosition {
    void setLocationName(String name);
    String getLocationName();
    int getCityID();
    void setCityID(int cityID);
    Coordinate getCoordinate();
    void setCoordinate(Coordinate coordinate);
}
