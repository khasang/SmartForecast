package com.khasang.forecast;

/**
 * Created by novoselov on 24.11.2015.
 */
public class LocationManager {
    //получение координат через GPS
    public static Coordinate getGPSCoordinate () {
        return new Coordinate();
    }

    // По названию города через геокодер возвращает георгафические координаты
    public static Coordinate getLocationCoordinate (String location) {
        return new Coordinate();
    }
}
