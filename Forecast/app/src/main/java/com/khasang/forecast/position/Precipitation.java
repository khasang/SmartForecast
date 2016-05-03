package com.khasang.forecast.position;

import com.khasang.forecast.R;

/**
 * Created by Veda on 24.11.15.
 */

public class Precipitation {
    private Type type;

    public static enum Type {
        THUNDERSTORM {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_THUNDERSTORM;
            }
        },
        DRIZZLE {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_DRIZZLE;
            }
        },
        RAIN {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_RAIN;
            }
        },
        SNOW {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_SNOW;
            }
        },
        ATMOSPHERE {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_ATMOSPHERE;
            }
        },
        CLEAR {
            public int getIconNumber(boolean isDay) {
                if (isDay) {
                    return PositionManager.ICON_INDEX_SUN;
                } else {
                    return PositionManager.ICON_INDEX_MOON;
                }
            }
        },
        CLOUDS {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_CLOUDS;
            }
        },
        EXTREME {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_EXTREME;
            }
        },
        ADDITIONAL {
            public int getIconNumber(boolean isDay) {
                return PositionManager.ICON_INDEX_ADDITIONAL;
            }
        };

        public abstract int getIconNumber(boolean isDay);
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
