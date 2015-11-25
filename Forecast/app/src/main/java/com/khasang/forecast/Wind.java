package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */
public class Wind {

    public static enum WIND_POWER { CALM, LIGHT_AIR, LIGHT_BREEZE, GENTLE_BREEZE, MODERATE_BREEZE,
        FRESH_BREEZE, STRONG_BREEZE, MODERATE_GALE, FRESH_GALE, STRONG_GALE, WHOLE_GALE, STORM, HURRICANE }

    public static enum WIND_DIRECTION { NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST }

    private WIND_DIRECTION direction;
    private WIND_POWER power;

    public Wind(WIND_DIRECTION direction, WIND_POWER power) {
        this.direction = direction;
        this.power = power;
    }

    public WIND_DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(WIND_DIRECTION direction) {
        this.direction = direction;
    }

    public WIND_POWER getPower() {
        return power;
    }

    public void setPower(WIND_POWER power) {
        this.power = power;
    }
}
