package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Precipitation {
    private Type type;

    public static enum Type {
        THUNDERSTORM(R.drawable.ic_thunderstorm),
        DRIZZLE(R.drawable.ic_drizzle),
        RAIN(R.drawable.ic_rain),
        SNOW(R.drawable.ic_snow),
        ATMOSPHERE(R.drawable.ic_fog),
        CLEAR(R.drawable.ic_sun),
        CLOUDS(R.drawable.ic_cloud),
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

    public Type stringToType(String type) {
        return Type.valueOf(type);
    }

}
