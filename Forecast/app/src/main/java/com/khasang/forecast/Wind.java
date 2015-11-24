package com.khasang.forecast;

/**
 * Created by Veda on 24.11.15.
 */
public class Wind {

    public static enum Power {
        // Шкала Бофорта
        CALM,           //  0 штиль
        LIGHT_AIR,      //  1 тихий
        LIGHT_BREEZE,   //  2 легкий
        GENTLE_BREEZE,  //  3 слабый
        MODERATE_BREEZE,//  4 умеренный
        FRESH_BREEZE,   //  5 свежий
        STRONG_BREEZE,  //  6 сильный
        HIGH_WIND,     //   7 крепкий
        GALE,           //  8 очень крепкий
        STRONG_GALE,    //  9 шторм
        STORM,          // 10 сильный шторм
        VIOLENT_STORM,  // 11 жестокий шторм
        HURRICANE_FORCE,// 12 ураган
        ;
    }

    public static enum Direction { NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST }

    private Direction direction;
    private Power power;

    public Wind(Direction direction, Power power) {
        this.direction = direction;
        this.power = power;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        this.power = power;
    }
}
