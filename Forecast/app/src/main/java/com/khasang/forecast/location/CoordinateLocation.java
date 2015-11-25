package com.khasang.forecast.location;

import com.khasang.forecast.Observer;
import com.khasang.forecast.Weather;
import com.khasang.forecast.WeatherStationFactory;

import java.util.Date;

/**
 * Created by xsobolx on 24.11.2015.
 */
public class CoordinateLocation
        implements iLocation,
        Observer{
    private double[] mLatLng;

    CoordinateLocation(){
        mLatLng = new double[2];
    }

    @Override
    public double[] getLatLng() {
        return mLatLng;
    }

    @Override
    public void setLatLng(double lat, double lon) {
        mLatLng[0] = lat;
        mLatLng[1] = lon;
    }

    public static iLocationFactory factory = new iLocationFactory() {
        @Override
        public iLocation getLocation() {
            return new CoordinateLocation();
        }
    };

    @Override
    public void setWeather(WeatherStationFactory.WEATHER_STATION ws, Date date, Weather weather) {

    }
}
