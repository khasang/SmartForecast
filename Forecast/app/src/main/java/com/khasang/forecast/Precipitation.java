package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Precipitation {
    public static enum Type {
        FAIR_WEATHER,
        RAINING,
        SNOWING,
        HAIL
        // TODO: продолжить
        ;
    }

    private Type type;
    private int probability;

    public Precipitation(Type type, int probability) {
        this.type = type;
        this.probability = probability;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }
}
