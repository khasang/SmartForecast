package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Precipitation {
    private Type type;
    //private int probability;

    /*public static enum Type {
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
    }*/

    public static enum Type {
        THUNDERSTORM(R.drawable.thunder),
        DRIZZLE(R.drawable.partlycloudy),
        RAIN(0),
        SNOW(R.drawable.snowy),
        ATMOSPHERE(0),
        CLEAR(R.drawable.sunny),
        CLOUDS(R.drawable.cloudy),
        EXTREME(0),
        ADDITIONAL(0);

        int iconResId;

        Type(int iconResId) {
            this.iconResId = iconResId;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    @Override
    public String toString() {
        return "Precipitation{" +
                "type=" + type +
                '}';
    }

    public Precipitation() {

    }

    public Precipitation(Type type) {
        this.type = type;
        //this.probability = probability;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = stringToType(type);
    }

    public Type stringToType(String direction) {
        switch (direction) {
            case "THUNDERSTORM": return Type.THUNDERSTORM;
            case "DRIZZLE": return Type.DRIZZLE;
            case "RAIN": return Type.RAIN;
            case "SNOW": return Type.SNOW;
            case "ATMOSPHERE": return Type.ATMOSPHERE;
            case "CLEAR": return Type.CLEAR;
            case "CLOUDS": return Type.CLOUDS;
            case "EXTREME": return Type.EXTREME;
            case "ADDITIONAL": return Type.ADDITIONAL;
        }
        return null;
    }


    /*public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }*/
}
