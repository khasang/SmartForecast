package com.khasang.forecast;

/**
 * Model class to show how weather representation should look like.
 */

public class OpenWeatherMapResponse {
    //City ID
    private String id;
    private Wind wind;
    //City name
    private String name;
    private Weather main;

    public Weather getMain() {
        return main;
    }
}
