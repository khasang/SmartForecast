package com.khasang.forecast;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * This interface defines API endpoints for requests. URL and request methods are specified in the
 * annotation.
 */

public interface OpenWeatherMapService {

    //Current weather data by geographic coordinates.
    @GET("/weather")
    Call<Weather> getCurrent(@Query("appid") String appid, @Query("lat") double latitude,
                             @Query("lon") double longitude);

    //3 hour forecast data by geographic coordinates.
    @GET("/forecast")
    Call<List<Weather>> getHourly(@Query("appid") String appid, @Query("lat") double latitude,
                                  @Query("lon") double longitude, @Query("cnt") int numberOfIntervals);

    //Weekly forecast data by geographic coordinates.
    @GET("/forecast/daily")
    Call<List<Weather>> getWeekly(@Query("appid") String appid, @Query("lat") double latitude,
                                  @Query("lon") double longitude, @Query("cnt") int numberOfDays);
}
