package com.khasang.forecast.models;

import com.khasang.forecast.models.Clouds;
import com.khasang.forecast.models.Forecast;
import com.khasang.forecast.models.Rain;
import com.khasang.forecast.models.Sys;
import com.khasang.forecast.models.Weather;
import com.khasang.forecast.models.Wind;

import java.util.Arrays;

/**
 * Model class to show how weather representation should look like.
 */

public class OpenWeatherMapResponse {
    private Weather[] weather;
    private Forecast main;
    private Wind wind;
    private Clouds clouds;
    private Rain rain;
    private long dt;
    private Sys sys;
    private String id;
    private String name;

    @Override
    public String toString() {
        return "OpenWeatherMapResponse{" +
                "weather=" + Arrays.toString(weather) +
                ", main=" + main +
                ", wind=" + wind +
                ", clouds=" + clouds +
                ", rain=" + rain +
                ", dt=" + dt +
                ", sys=" + sys +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
