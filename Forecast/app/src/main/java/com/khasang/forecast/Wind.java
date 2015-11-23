package com.khasang.forecast;

/**
 * Created by baradist on 24.11.2015.
 */
public class Wind {
    public static enum WindDirection {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        NORTH_WEST,
        NORTH_EAST,
        SOUTH_WEST,
        SOUTH_EAST
        // ...
        ;
    }
    public static enum WindPower {
        // Шкала Бофорта
        CALM,           //  0 штиль
        LIGHT_AIR,      //  1 тихий
        LIGHT_BREEZE,   //  2 легкий
        GENTLE_BREEZE,  //  3 слабый
        MODERATE_BREEZE,//  4 умеренный
        FRESH_BREEZE,   //  5 свежий
        STRONG_BREEZE,  //  6 сильный
        HIGHT_WIND,     //  7 крепкий
        GALE,           //  8 очень крепкий
        STRONG_GALE,    //  9 шторм
        STORM,          // 10 сильный шторм
        VIOLENT_STORM,  // 11 жестокий шторм
        HURRICANE_FORCE,// 12 ураган
        ;
    }

    private WindDirection direction;
    private WindPower power;

    public Wind() {
    }

    public Wind(WindDirection direction, WindPower power) {
        this.direction = direction;
        this.power = power;
    }

    public WindPower getPower() {
        return power;
    }

    public void setPower(WindPower power) {
        this.power = power;
    }

    public WindDirection getDirection() {
        return direction;
    }

    public void setDirection(WindDirection direction) {
        this.direction = direction;
    }
}
