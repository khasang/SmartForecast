package com.khasang.forecast.stations;

import com.khasang.forecast.models.DailyResponse;
import com.khasang.forecast.models.OpenWeatherMapResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Этот интерфейс задает конечные точки для запроса к API. Методы и параметры запроса указываются
 * через аннотации.
 * <p/>
 * <p><b>Методы:</b>
 * <ul>
 * <li>{@link #getCurrent(double, double)}</li>
 * <li>{@link #getHourly(double, double, int)}</li>
 * <li>{@link #getDaily(double, double, int)}</li>
 * </ul>
 */

public interface OpenWeatherMapService {

    /**
     * Получить текущий прогноз погоды по заданным географическим координатам.
     *
     * @param latitude  географическая широта.
     * @param longitude географическая долгота.
     */
    @GET("/data/2.5/weather")
    Call<OpenWeatherMapResponse> getCurrent(@Query("lat") double latitude, @Query("lon") double longitude);

    /**
     * Получить прогноз погоды с заданным количеством 3-х часов интервалов по географическим
     * координатам.
     *
     * @param latitude   географическая широта.
     * @param longitude  географическая долгота.
     * @param timePeriod количество 3-х часовых интервалов.
     */
    @GET("/data/2.5/forecast")
    Call<OpenWeatherMapResponse> getHourly(@Query("lat") double latitude, @Query("lon") double longitude,
                                           @Query("cnt") int timePeriod);

    /**
     * Получить прогноз погоды с заданным количеством дней по географическим координатам.
     *
     * @param latitude   географическая широта.
     * @param longitude  географическая долгота.
     * @param daysPeriod количество дней.
     */
    @GET("/data/2.5/forecast/daily")
    Call<DailyResponse> getDaily(@Query("lat") double latitude, @Query("lon") double longitude,
                                 @Query("cnt") int daysPeriod);
}
