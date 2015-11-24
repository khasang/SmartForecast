package com.khasang.forecast.location;

/**
 * Created by xsobolx on 24.11.2015.
 */
public interface iLocation {
    String getName();
    void setName(String name);

    int getCityID();
    void setCityID(int ID);

    Coordinate getCoordinate();
    void setCoordinate(Coordinate coordinate);

}
