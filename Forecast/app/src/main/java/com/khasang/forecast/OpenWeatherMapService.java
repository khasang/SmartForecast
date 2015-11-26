package com.khasang.forecast;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * This interface defines API endpoints for requests. URL and request methods are specified in the
 * annotation.
 */

public interface OpenWeatherMapService {

    //Current weather data by geographic coordinates.
    @GET("/data/2.5/weather")
    Call<OpenWeatherMapResponse> getCurrent(@Query("appid") String appid, @Query("lat") double latitude,
                             @Query("lon") double longitude);

    //3 hour forecast data by geographic coordinates.
    @GET("/data/2.5/forecast")
    Call<List<Weather>> getHourly(@Query("appid") String appid, @Query("lat") double latitude,
                                  @Query("lon") double longitude, @Query("cnt") int timePeriod);

    //Weekly forecast data by geographic coordinates.
    @GET("/data/2.5/forecast/daily")
    Call<List<Weather>> getWeekly(@Query("appid") String appid, @Query("lat") double latitude,
                                  @Query("lon") double longitude, @Query("cnt") int daysPeriod);
}
