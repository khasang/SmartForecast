package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Wind {
    private WIND_DIRECTION direction;
    private double speed;
/*
    public static enum WIND_POWER {
        CALM, LIGHT_AIR, LIGHT_BREEZE, GENTLE_BREEZE, MODERATE_BREEZE,
        FRESH_BREEZE, STRONG_BREEZE, MODERATE_GALE, FRESH_GALE, STRONG_GALE, WHOLE_GALE, STORM, HURRICANE
    }
*/
    public static enum WIND_DIRECTION {NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST}

    public Wind(WIND_DIRECTION direction, double speed) {
        this.direction = direction;
        this.speed = speed;
    }

    public WIND_DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(WIND_DIRECTION direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
