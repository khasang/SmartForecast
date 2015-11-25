package com.khasang.forecast.location;

import com.khasang.forecast.Observer;
import com.khasang.forecast.Weather;
import com.khasang.forecast.WeatherStationFactory;

import java.util.Date;

/**
 * Created by xsobolx on 25.11.2015.
 */
public class CityNameLocation
        implements iLocation,
        Observer{
    private double[] mLatLng;
    private String mCityName;
    private int mCityID;

    CityNameLocation(){
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

    public void setLatLng(double[] latLng) {
        mLatLng = latLng;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public int getCityID() {
        return mCityID;
    }

    public void setCityID(int cityID) {
        mCityID = cityID;
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
