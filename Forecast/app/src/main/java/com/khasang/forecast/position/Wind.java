package com.khasang.forecast.position;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;

/**
 * Created by Veda on 24.11.15.
 */

public class Wind {
    private Direction direction;
    private double speed;

    public static enum Direction {
        NORTH(MyApplication.getAppContext().getString(R.string.N)),
        NORTHEAST(MyApplication.getAppContext().getString(R.string.NE)),
        EAST(MyApplication.getAppContext().getString(R.string.E)),
        SOUTHEAST(MyApplication.getAppContext().getString(R.string.SE)),
        SOUTH(MyApplication.getAppContext().getString(R.string.S)),
        SOUTHWEST(MyApplication.getAppContext().getString(R.string.SW)),
        WEST(MyApplication.getAppContext().getString(R.string.W)),
        NORTHWEST(MyApplication.getAppContext().getString(R.string.NW));

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
