package com.khasang.forecast;

import android.widget.Switch;

/**
 * Created by Veda on 24.11.15.
 */

public class Precipitation {
    private Type type;
    private int probability;

    public static enum Type {
        CLEAR_SKY, FEW_CLOUDS, SCATTERED_CLOUDS, BROKEN_CLOUDS,
        SHOWER_RAIN, RAIN, TRUNDERSTORM, SNOW, MIST;

        @Override
        public String toString() {
            switch(this) {
                case CLEAR_SKY: return "Ясно";
                case FEW_CLOUDS: return "Небольшая облачность";
                case SCATTERED_CLOUDS: return "Облачно";
                case BROKEN_CLOUDS: return "Облачно с прояснениями";
                case SHOWER_RAIN: return "Ливень";
                case RAIN: return "Дождь";
                case TRUNDERSTORM: return "Гроза";
                case SNOW: return "Снег";
                case MIST: return "Туман";
                default: return "Type";
            }
        }
    }

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
