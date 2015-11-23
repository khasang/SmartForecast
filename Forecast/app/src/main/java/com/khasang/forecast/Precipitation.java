package com.khasang.forecast;

/**
 * Created by baradist on 24.11.2015.
 */
public class Precipitation {
    public static enum PrecipitationType {
        FAIR_WEATHER,
        RAINING,
        SNOWING,
        HAIL
        // TODO: продолжить
        ;
    }

    private PrecipitationType precipitation;
    private int probability;

    public Precipitation() {
    }

    public Precipitation(PrecipitationType precipitation, int probability) {
        this.precipitation = precipitation;
        this.probability = probability;
    }

    public PrecipitationType getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(PrecipitationType precipitation) {
        this.precipitation = precipitation;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }
}
