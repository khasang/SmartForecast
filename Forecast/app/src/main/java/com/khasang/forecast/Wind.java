package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Wind {
    private Direction direction;
    private double speed;
/*
    public static enum WIND_POWER {
        CALM, LIGHT_AIR, LIGHT_BREEZE, GENTLE_BREEZE, MODERATE_BREEZE,
        FRESH_BREEZE, STRONG_BREEZE, MODERATE_GALE, FRESH_GALE, STRONG_GALE, WHOLE_GALE, STORM, HURRICANE
    }
*/
    public static enum Direction {NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST}

    public Wind() {

    }

    public Wind(Direction direction, double speed) {
        this.direction = direction;
        this.speed = speed;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) {
        this.direction = stringToDirection(direction);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Direction stringToDirection(String direction) {
        return Direction.valueOf(direction);
    }

}
