package com.khasang.forecast;

import com.khasang.forecast.models.DailyResponse;
import com.khasang.forecast.models.OpenWeatherMapResponse;

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
    Call<OpenWeatherMapResponse> getCurrent(@Query("lat") double latitude, @Query("lon") double longitude);

    //3 hour forecast data by geographic coordinates.
    @GET("/data/2.5/forecast")
    Call<OpenWeatherMapResponse> getHourly(@Query("lat") double latitude, @Query("lon") double longitude,
                                   @Query("cnt") int timePeriod);

    //Weekly forecast data by geographic coordinates.
    @GET("/data/2.5/forecast/daily")
    Call<DailyResponse> getDaily(@Query("lat") double latitude, @Query("lon") double longitude,
                                          @Query("cnt") int daysPeriod);
}
