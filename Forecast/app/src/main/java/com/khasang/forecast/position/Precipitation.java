package com.khasang.forecast.position;

/**
 * Created by Veda on 24.11.15.
 */

public class Precipitation {
    private Type type;

    public static enum Type {
        THUNDERSTORM {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_THUNDERSTORM;
            }
        },
        DRIZZLE {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_DRIZZLE;
            }
        },
        RAIN {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_MODERATE_RAIN;
            }
        },
        SNOW {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_SNOW;
            }
        },
        ATMOSPHERE {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_FOG;
            }
        },
        CLEAR {
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return PositionManager.ICON_INDEX_CLEAR_SKY_SUN;
                } else {
                    return PositionManager.ICON_INDEX_CLEAR_SKY_MOON;
                }
            }
        },
        CLOUDS {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_FEW_CLOUDS;
            }
        },
        EXTREME {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_EXTREME_TORNADO;
            }
        },
        ADDITIONAL {
            public int getIconIndex(boolean isDay) {
                return PositionManager.ICON_INDEX_CALM;
            }
        };

        public abstract int getIconIndex(boolean isDay);
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
