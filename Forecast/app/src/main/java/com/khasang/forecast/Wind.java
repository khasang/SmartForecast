package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */

public class Wind {
    private Direction direction;
    private double speed;

    public static enum Direction {
        NORTH("N"),
        NORTHEAST("NE"),
        EAST("E"),
        SOUTHEAST("SE"),
        SOUTH("S"),
        SOUTHWEST("SW"),
        WEST("W"),
        NORTHWEST("NW");

        String directionString;

        Direction(String directionString) {
            this.directionString = directionString;
        }

        public String getDirectionString() {
            return directionString;
        }
    }

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
