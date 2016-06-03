package com.khasang.forecast.interfaces;

import com.khasang.forecast.exceptions.EmptyCurrentAddressException;

/**
 * Created by novoselov on 16.03.2016.
 */
public interface IMapDataReceiver {
    void setLocationNameFromMap (String name);
    String setLocationCoordinatesFromMap(double latitude, double longitude) throws EmptyCurrentAddressException;
}
