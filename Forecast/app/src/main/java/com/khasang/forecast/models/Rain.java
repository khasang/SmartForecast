package com.khasang.forecast.models;

import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("3h")
    private double rainVolume;

    @Override
    public String toString() {
        return "Rain{" +
                "rainVolume=" + rainVolume +
                '}';
    }

    public double getRainVolume() {
        return rainVolume;
    }
}
