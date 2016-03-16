package com.khasang.forecast.interfaces;

import com.khasang.forecast.Maps;

/**
 * Created by novoselov on 16.03.2016.
 */
public interface IMapDataReceiver {
    public void setLocationNameFromMap (String name);
    public void setLocationCoordinatesFromMap(Maps maps, double latitude, double longitude);
}
