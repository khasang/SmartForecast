package com.khasang.forecast.models;

import com.google.gson.annotations.SerializedName;

public class Snow {

    @SerializedName("3h")
    private double snowVolume;

    public double getSnowVolume() {
        return snowVolume;
    }

    @Override
    public String toString() {
        return "Snow{" +
                "snowVolume=" + snowVolume +
                '}';
    }
}
