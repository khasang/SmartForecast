package com.khasang.forecast.position;

import com.khasang.forecast.AppUtils;

/**
 * Created by Veda on 24.11.15.
 */

public class Precipitation {
    private Type type;

    public static enum Type {
        THUNDERSTORM_LIGHT_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_THUNDERSTORM_LIGHT_RAIN;
            }
        },
        THUNDERSTORM_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_THUNDERSTORM_RAIN;
            }
        },
        THUNDERSTORM_HEAVY_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_THUNDERSTORM_HEAVY_RAIN;
            }
        },
        LIGHT_THUNDERSTORM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_THUNDERSTORM;
            }
        },
        THUNDERSTORM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_THUNDERSTORM;
            }
        },
        HEAVY_THUNDERSTORM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HEAVY_THUNDERSTORM;
            }
        },
        RAGGED_THUNDERSTORM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_RAGGED_THUNDERSTORM;
            }
        },
        THUNDERSTORM_LIGHT_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_THUNDERSTORM_LIGHT_DRIZZLE;
            }
        },
        THUNDERSTORM_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_THUNDERSTORM_DRIZZLE;
            }
        },
        THUNDERSTORM_HEAVY_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_THUNDERSTORM_HEAVY_DRIZZLE;
            }
        },

        LIGHT_INTENSITY_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_INTENSITY_DRIZZLE;
            }
        },
        DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_DRIZZLE;
            }
        },
        HEAVY_INTENSITY_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HEAVY_INTENSITY_DRIZZLE;
            }
        },
        LIGHT_INTENSITY_DRIZZLE_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_INTENSITY_DRIZZLE_RAIN;
            }
        },
        DRIZZLE_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_DRIZZLE_RAIN;
            }
        },
        HEAVY_INTENSITY_DRIZZLE_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HEAVY_INTENSITY_DRIZZLE_RAIN;
            }
        },
        SHOWER_RAIN_AND_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SHOWER_RAIN_AND_DRIZZLE;
            }
        },
        HEAVY_SHOWER_RAIN_AND_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HEAVY_SHOWER_RAIN_AND_DRIZZLE;
            }
        },
        SHOWER_DRIZZLE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SHOWER_DRIZZLE;
            }
        },

        LIGHT_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_LIGHT_RAIN;
                }
                return AppUtils.ICON_INDEX_LIGHT_RAIN_NIGHT;
            }
        },
        MODERATE_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_MODERATE_RAIN;
                }
                return AppUtils.ICON_INDEX_MODERATE_RAIN_NIGHT;
            }
        },
        HEAVY_INTENSITY_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_HEAVY_INTENSITY_RAIN;
                }
                return AppUtils.ICON_INDEX_HEAVY_INTENSITY_RAIN_NIGHT;
            }
        },
        VERY_HEAVY_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_VERY_HEAVY_RAIN;
                }
                return AppUtils.ICON_INDEX_VERY_HEAVY_RAIN_NIGHT;
            }
        },
        EXTREME_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_EXTREME_RAIN;
                }
                return AppUtils.ICON_INDEX_EXTREME_RAIN_NIGHT;
            }
        },
        FREEZING_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_FREEZING_RAIN;
            }
        },
        LIGHT_INTENSITY_SHOWER_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_INTENSITY_SHOWER_RAIN;
            }
        },
        SHOWER_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SHOWER_RAIN;
            }
        },
        HEAVY_INTENSITY_SHOWER_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HEAVY_INTENSITY_SHOWER_RAIN;
            }
        },
        RAGGED_SHOWER_RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_RAGGED_SHOWER_RAIN;
            }
        },
        RAIN {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SHOWER_RAIN;
            }
        },

        LIGHT_SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_SNOW;
            }
        },
        SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SNOW;
            }
        },
        HEAVY_SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HEAVY_SNOW;
            }
        },
        SLEET {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SLEET;
            }
        },
        SHOWER_SLEET {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SHOWER_SLEET;
            }
        },
        LIGHT_RAIN_AND_SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_RAIN_AND_SNOW;
            }
        },
        RAIN_AND_SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_RAIN_AND_SNOW;
            }
        },
        LIGHT_SHOWER_SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_SHOWER_SNOW;
            }
        },
        SHOWER_SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SHOWER_SNOW;
            }
        },
        HEAVY_SHOWER_SNOW {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HEAVY_SHOWER_SNOW;
            }
        },

        ATMOSPHERE {
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_FOG;
            }
        },
        MIST {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_MIST;
            }
        },
        SMOKE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SMOKE;
            }
        },
        HAZE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HAZE;
            }
        },
        SAND_DUST_WHIRLS {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SAND_DUST_WHIRLS;
            }
        },
        FOG {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_FOG;
            }
        },
        SAND {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SAND;
            }
        },
        DUST {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_DUST;
            }
        },
        VOLCANIC_ASH {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_VOLCANIC_ASH;
            }
        },
        SQUALLS {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SQUALLS;
            }
        },
        TORNADO {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_TORNADO;
            }
        },

        CLEAR_SKY {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_CLEAR_SKY_SUN;
                }
                return AppUtils.ICON_INDEX_CLEAR_SKY_MOON;
            }
        },

        FEW_CLOUDS {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_FEW_CLOUDS;
                }
                return AppUtils.ICON_INDEX_FEW_CLOUDS_NIGHT;
            }
        },
        SCATTERED_CLOUDS {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_SCATTERED_CLOUDS;
                }
                return AppUtils.ICON_INDEX_SCATTERED_CLOUDS_NIGHT;
            }
        },
        BROKEN_CLOUDS {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_BROKEN_CLOUDS;
                }
                return AppUtils.ICON_INDEX_BROKEN_CLOUDS_NIGHT;
            }
        },
        OVERCAST_CLOUDS {
            @Override
            public int getIconIndex(boolean isDay) {
                if (isDay) {
                    return AppUtils.ICON_INDEX_OVERCAST_CLOUDS;
                }
                return AppUtils.ICON_INDEX_OVERCAST_CLOUDS_NIGHT;
            }
        },

        EXTREME_TORNADO {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_TORNADO;
            }
        },
        EXTREME_TROPICAL_STORM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_TROPICAL_STORM;
            }
        },
        EXTREME_HURRICANE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_HURRICANE;
            }
        },
        EXTREME_COLD {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_COLD;
            }
        },
        EXTREME_HOT {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_HOT;
            }
        },
        EXTREME_WINDY {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_WINDY;
            }
        },
        EXTREME_HAIL {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_HAIL;
            }
        },
        EXTREME {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_EXTREME_TORNADO;
            }
        },

        CALM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_CALM;
            }
        },
        LIGHT_BREEZE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_LIGHT_BREEZE;
            }
        },
        GENTLE_BREEZE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_GENTLE_BREEZE;
            }
        },
        MODERATE_BREEZE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_MODERATE_BREEZE;
            }
        },
        FRESH_BREEZE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_FRESH_BREEZE;
            }
        },
        STRONG_BREEZE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_STRONG_BREEZE;
            }
        },
        HIGH_WIND_NEAR_GALE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HIGH_WIND_NEAR_GALE;
            }
        },
        GALE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_GALE;
            }
        },
        SEVERE_GALE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_SEVERE_GALE;
            }
        },
        STORM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_STORM;
            }
        },
        VIOLENT_STORM {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_VIOLENT_STORM;
            }
        },
        HURRICANE {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_HURRICANE;
            }
        },

        NO_DATA {
            @Override
            public int getIconIndex(boolean isDay) {
                return AppUtils.ICON_INDEX_NA;
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
