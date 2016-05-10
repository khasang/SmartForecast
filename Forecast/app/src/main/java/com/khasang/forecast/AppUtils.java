package com.khasang.forecast;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.models.DailyForecastList;
import com.khasang.forecast.models.DailyResponse;
import com.khasang.forecast.models.HourlyForecastList;
import com.khasang.forecast.models.OpenWeatherMapResponse;
import com.khasang.forecast.position.Precipitation;
import com.khasang.forecast.position.Weather;
import com.khasang.forecast.position.Wind;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.meteocons_typeface_library.Meteoconcs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Вспомогательный класс, служащий для преобразования получаемых данных.
 * <p>Реализованы методы
 * <ul>
 * <li>{@link #convertToWeather(OpenWeatherMapResponse)}</li>
 * <li>{@link #convertToHourlyWeather(OpenWeatherMapResponse)}</li>
 * <li>{@link #convertToDailyWeather(DailyResponse)}</li>
 * </ul>
 */

public class AppUtils {
    public static final double KELVIN_CELSIUS_DELTA = 273.15;
    public static final double HPA_TO_MM_HG = 1.33322;
    public static final double KM_TO_MILES = 0.62137;
    public static final double METER_TO_FOOT = 3.28083;

    //Icons set
    private static final int ICONS_COUNT = 84;

    public static final int ICON_INDEX_THUNDERSTORM_LIGHT_RAIN = 0;
    public static final int ICON_INDEX_THUNDERSTORM_RAIN = 1;
    public static final int ICON_INDEX_THUNDERSTORM_HEAVY_RAIN = 2;
    public static final int ICON_INDEX_LIGHT_THUNDERSTORM = 3;
    public static final int ICON_INDEX_THUNDERSTORM = 4;
    public static final int ICON_INDEX_HEAVY_THUNDERSTORM = 5;
    public static final int ICON_INDEX_RAGGED_THUNDERSTORM = 6;
    public static final int ICON_INDEX_THUNDERSTORM_LIGHT_DRIZZLE = 7;
    public static final int ICON_INDEX_THUNDERSTORM_DRIZZLE = 8;
    public static final int ICON_INDEX_THUNDERSTORM_HEAVY_DRIZZLE = 9;

    public static final int ICON_INDEX_LIGHT_INTENSITY_DRIZZLE = 10;
    public static final int ICON_INDEX_DRIZZLE = 11;
    public static final int ICON_INDEX_HEAVY_INTENSITY_DRIZZLE = 12;
    public static final int ICON_INDEX_LIGHT_INTENSITY_DRIZZLE_RAIN = 13;
    public static final int ICON_INDEX_DRIZZLE_RAIN = 14;
    public static final int ICON_INDEX_HEAVY_INTENSITY_DRIZZLE_RAIN = 15;
    public static final int ICON_INDEX_SHOWER_RAIN_AND_DRIZZLE = 16;
    public static final int ICON_INDEX_HEAVY_SHOWER_RAIN_AND_DRIZZLE = 17;
    public static final int ICON_INDEX_SHOWER_DRIZZLE = 18;

    public static final int ICON_INDEX_LIGHT_RAIN = 19;
    public static final int ICON_INDEX_MODERATE_RAIN = 20;
    public static final int ICON_INDEX_HEAVY_INTENSITY_RAIN = 21;
    public static final int ICON_INDEX_VERY_HEAVY_RAIN = 22;
    public static final int ICON_INDEX_EXTREME_RAIN = 23;
    public static final int ICON_INDEX_LIGHT_RAIN_NIGHT = 24;
    public static final int ICON_INDEX_MODERATE_RAIN_NIGHT = 25;
    public static final int ICON_INDEX_HEAVY_INTENSITY_RAIN_NIGHT = 26;
    public static final int ICON_INDEX_VERY_HEAVY_RAIN_NIGHT = 27;
    public static final int ICON_INDEX_EXTREME_RAIN_NIGHT = 28;
    public static final int ICON_INDEX_FREEZING_RAIN = 29;
    public static final int ICON_INDEX_LIGHT_INTENSITY_SHOWER_RAIN = 30;
    public static final int ICON_INDEX_SHOWER_RAIN = 31;
    public static final int ICON_INDEX_HEAVY_INTENSITY_SHOWER_RAIN = 32;
    public static final int ICON_INDEX_RAGGED_SHOWER_RAIN = 33;

    public static final int ICON_INDEX_LIGHT_SNOW = 34;
    public static final int ICON_INDEX_SNOW = 35;
    public static final int ICON_INDEX_HEAVY_SNOW = 36;
    public static final int ICON_INDEX_SLEET = 37;
    public static final int ICON_INDEX_SHOWER_SLEET = 38;
    public static final int ICON_INDEX_LIGHT_RAIN_AND_SNOW = 39;
    public static final int ICON_INDEX_RAIN_AND_SNOW = 40;
    public static final int ICON_INDEX_LIGHT_SHOWER_SNOW = 41;
    public static final int ICON_INDEX_SHOWER_SNOW = 42;
    public static final int ICON_INDEX_HEAVY_SHOWER_SNOW = 43;

    public static final int ICON_INDEX_MIST = 44;
    public static final int ICON_INDEX_SMOKE = 45;
    public static final int ICON_INDEX_HAZE = 46;
    public static final int ICON_INDEX_SAND_DUST_WHIRLS = 47;
    public static final int ICON_INDEX_FOG = 48;
    public static final int ICON_INDEX_SAND = 49;
    public static final int ICON_INDEX_DUST = 50;
    public static final int ICON_INDEX_VOLCANIC_ASH = 51;
    public static final int ICON_INDEX_SQUALLS = 52;
    public static final int ICON_INDEX_TORNADO = 53;

    public static final int ICON_INDEX_CLEAR_SKY_SUN = 54;
    public static final int ICON_INDEX_CLEAR_SKY_MOON = 55;

    public static final int ICON_INDEX_FEW_CLOUDS = 56;
    public static final int ICON_INDEX_SCATTERED_CLOUDS = 57;
    public static final int ICON_INDEX_BROKEN_CLOUDS = 58;
    public static final int ICON_INDEX_OVERCAST_CLOUDS = 59;
    public static final int ICON_INDEX_FEW_CLOUDS_NIGHT = 60;
    public static final int ICON_INDEX_SCATTERED_CLOUDS_NIGHT = 61;
    public static final int ICON_INDEX_BROKEN_CLOUDS_NIGHT = 62;
    public static final int ICON_INDEX_OVERCAST_CLOUDS_NIGHT = 63;

    public static final int ICON_INDEX_EXTREME_TORNADO = 64;
    public static final int ICON_INDEX_EXTREME_TROPICAL_STORM = 65;
    public static final int ICON_INDEX_EXTREME_HURRICANE = 66;
    public static final int ICON_INDEX_EXTREME_COLD = 67;
    public static final int ICON_INDEX_EXTREME_HOT = 68;
    public static final int ICON_INDEX_EXTREME_WINDY = 69;
    public static final int ICON_INDEX_EXTREME_HAIL = 70;

    public static final int ICON_INDEX_CALM = 71;
    public static final int ICON_INDEX_LIGHT_BREEZE = 72;
    public static final int ICON_INDEX_GENTLE_BREEZE = 73;
    public static final int ICON_INDEX_MODERATE_BREEZE = 74;
    public static final int ICON_INDEX_FRESH_BREEZE = 75;
    public static final int ICON_INDEX_STRONG_BREEZE = 76;
    public static final int ICON_INDEX_HIGH_WIND_NEAR_GALE = 77;
    public static final int ICON_INDEX_GALE = 78;
    public static final int ICON_INDEX_SEVERE_GALE = 79;
    public static final int ICON_INDEX_STORM = 80;
    public static final int ICON_INDEX_VIOLENT_STORM = 81;
    public static final int ICON_INDEX_HURRICANE = 82;

    public static final int ICON_INDEX_NA = 83;

    public enum TemperatureMetrics {
        KELVIN {
            public TemperatureMetrics change() {
                return CELSIUS;
            }

            public String toStringValue() {
                return MyApplication.getAppContext().getString(R.string.KELVIN);
            }
        },
        CELSIUS {
            public TemperatureMetrics change() {
                return FAHRENHEIT;
            }

            public String toStringValue() {
                return MyApplication.getAppContext().getString(R.string.CELSIUS);
            }

        },
        FAHRENHEIT {
            public TemperatureMetrics change() {
                return KELVIN;
            }

            public String toStringValue() {
                return MyApplication.getAppContext().getString(R.string.FAHRENHEIT);
            }
        };

        public abstract TemperatureMetrics change();

        public abstract String toStringValue();
    }

    public enum SpeedMetrics {
        METER_PER_SECOND {
            @Override
            public String toStringValue() {
                return MyApplication.getAppContext().getString(R.string.wind_measure_m_s);
            }
        },
        FOOT_PER_SECOND {
            @Override
            public String toStringValue() {
                return MyApplication.getAppContext().getString(R.string.wind_measure_fps);
            }
        },
        KM_PER_HOURS {
            @Override
            public String toStringValue() {
                return MyApplication.getAppContext().getString(R.string.wind_measure_km_h);
            }
        },
        MILES_PER_HOURS {
            @Override
            public String toStringValue() {
                return MyApplication.getAppContext().getString(R.string.wind_measure_mph);
            }
        };

        public abstract String toStringValue();
    }

    public enum PressureMetrics {
        HPA {
            public PressureMetrics change() {
                return MM_HG;
            }
        },
        MM_HG {
            public PressureMetrics change() {
                return HPA;
            }
        };

        public abstract PressureMetrics change();
    }

    public static void showSnackBar(Activity activity, View view, CharSequence string, int length) {
        if (view == null) {
            if (activity != null) {
                showInfoMessage(activity, string).show();
            } else {
                showInfoMessage(string).show();
            }
            return;
        }
        Snackbar snackbar = Snackbar.make(view, string, length);
        View snackbarView = snackbar.getView();
        //        Default background fill: #323232 100%
        //        snackbarView.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.primary_dark));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.snackbar_text));
        snackbar.show();
    }

    public static Toast showInfoMessage(Activity activity, CharSequence string) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.warning_toast, ((ViewGroup) activity.findViewById(R.id.toast_layout_root)));
        ((TextView) layout.findViewById(R.id.warningMessage)).setText(string);
        Toast toast = new Toast(MyApplication.getAppContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        return toast;
    }

    public static Toast showInfoMessage(CharSequence string) {
        return Toast.makeText(MyApplication.getAppContext(), string, Toast.LENGTH_SHORT);
    }


    /**
     * Метод для конвертирования ответа от API в коллекцию типа {@link Map}<{@link Calendar}, {@link Weather}>
     * для запроса текущего прогноза погоды.
     *
     * @param response объект типа {@link OpenWeatherMapResponse}, содержащий ответ от API.
     */
    public static Map<Calendar, Weather> convertToWeather(OpenWeatherMapResponse response) {
        Map<Calendar, Weather> map = new HashMap<>();
        Weather weather = new Weather();
        Calendar calendar = unixToCalendar(response.getDt());
        weather.setTemperature(response.getMain().getTemp());
        weather.setHumidity(response.getMain().getHumidity());
        weather.setPressure(response.getMain().getPressure());
        weather.setSunrise(response.getSys().getSunrise());
        weather.setSunset(response.getSys().getSunset());
        setPrecipitationType(response.getWeather().get(0).getId(), weather);
        double speed = response.getWind().getSpeed();
        double deg = response.getWind().getDeg();
        setWindDirectionAndSpeed(weather, speed, deg);
        String description = null;
        for (com.khasang.forecast.models.Weather descr : response.getWeather()) {
            description = descr.getDescription();
        }
        weather.setDescription(description);
        map.put(calendar, weather);
        return map;
    }

    /**
     * Метод для конвертирования ответа от API в коллекцию типа {@link Map}<{@link Calendar}, {@link Weather}>
     * для запроса почасового прогноза погоды.
     *
     * @param response объект типа {@link OpenWeatherMapResponse}, содержащий ответ от API.
     */
    public static Map<Calendar, Weather> convertToHourlyWeather(OpenWeatherMapResponse response) {
        Map<Calendar, Weather> map = new HashMap<>();
        for (HourlyForecastList forecast : response.getList()) {
            Calendar calendar = unixToCalendar(forecast.getDt());
            Weather weather = new Weather();
            weather.setTemperature(forecast.getMain().getTemp());
            weather.setHumidity(forecast.getMain().getHumidity());
            weather.setPressure(forecast.getMain().getPressure());
            double speed = forecast.getWind().getSpeed();
            double deg = forecast.getWind().getDeg();
            setWindDirectionAndSpeed(weather, speed, deg);
            setPrecipitationType(forecast.getWeather().get(0).getId(), weather);
            String description = null;
            for (com.khasang.forecast.models.Weather descr : forecast.getWeather()) {
                description = descr.getDescription();
            }
            weather.setDescription(description);
            map.put(calendar, weather);
        }
        return map;
    }

    /**
     * Метод для конвертирования ответа от API в коллекцию типа {@link Map}<{@link Calendar}, {@link Weather}>
     * для запроса прогноза погоды по дням.
     *
     * @param response объект типа {@link DailyResponse}, содержащий ответ от API.
     */
    public static Map<Calendar, Weather> convertToDailyWeather(DailyResponse response) {
        Map<Calendar, Weather> map = new HashMap<>();
        for (DailyForecastList forecast : response.getList()) {
            Calendar calendar = unixToCalendar(forecast.getDt());
            Weather weather = new Weather();
            weather.setTemperature(forecast.getTemp().getDay());
            weather.setHumidity(forecast.getHumidity());
            weather.setPressure(forecast.getPressure());
            double speed = forecast.getSpeed();
            double deg = forecast.getDeg();
            setWindDirectionAndSpeed(weather, speed, deg);
            setPrecipitationType(forecast.getWeather().get(0).getId(), weather);
            String description = null;
            for (com.khasang.forecast.models.Weather descr : forecast.getWeather()) {
                description = descr.getDescription();
            }
            weather.setDescription(description);
            map.put(calendar, weather);
        }
        return map;
    }

    /**
     * Метод преобразует градусы направления ветра в перечисление типа Wind.Direction,
     * а так же устанавливает свойства для класса {@link Weather}.
     *
     * @param weather объект типа {@link Weather}.
     * @param speed   скорость ветра в м/с.
     * @param deg     направление ветра в градусах.
     */
    private static void setWindDirectionAndSpeed(Weather weather, double speed, double deg) {
        if ((deg >= 0 && deg <= 22.5) || (deg > 337.5 && deg <= 360)) {
            weather.setWind(Wind.Direction.NORTH, speed);
        } else if (deg > 22.5 && deg <= 67.5) {
            weather.setWind(Wind.Direction.NORTHEAST, speed);
        } else if (deg > 67.5 && deg <= 112.5) {
            weather.setWind(Wind.Direction.EAST, speed);
        } else if (deg > 112.5 && deg <= 157.5) {
            weather.setWind(Wind.Direction.SOUTHEAST, speed);
        } else if (deg > 157.5 && deg <= 202.5) {
            weather.setWind(Wind.Direction.SOUTH, speed);
        } else if (deg > 202.5 && deg <= 247.5) {
            weather.setWind(Wind.Direction.SOUTHWEST, speed);
        } else if (deg > 247.5 && deg <= 292.5) {
            weather.setWind(Wind.Direction.WEST, speed);
        } else if (deg > 292.5 && deg <= 337.5) {
            weather.setWind(Wind.Direction.NORTHWEST, speed);
        }
    }

    /**
     * Метод, преобразующий UNIX-время в объект типа {@link Calendar}.
     *
     * @param unixTime UNIX-время.
     */
    private static Calendar unixToCalendar(long unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000L);
        return calendar;
    }

    public static Drawable[] createIconsSet(Context context) {
        Drawable[] iconsSet = new Drawable[ICONS_COUNT];
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String iconSetType = sp.getString(context.getString(R.string.pref_icons_set_key), context.getString(R.string.pref_icons_set_default));
        if (iconSetType.equals(context.getString(R.string.pref_icons_set_mike))) {
            iconsSet[ICON_INDEX_THUNDERSTORM] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_cloud_flash);
            iconsSet[ICON_INDEX_DRIZZLE] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_drizzle);
            iconsSet[ICON_INDEX_MODERATE_RAIN] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_rain);
            iconsSet[ICON_INDEX_SNOW] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_snow_heavy);
            iconsSet[ICON_INDEX_FOG] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_fog);
            iconsSet[ICON_INDEX_CLEAR_SKY_SUN] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_sun);
            iconsSet[ICON_INDEX_CLEAR_SKY_MOON] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_moon);
            iconsSet[ICON_INDEX_FEW_CLOUDS] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_cloud);
            iconsSet[ICON_INDEX_EXTREME_TORNADO] = new IconicsDrawable(context)
                    .icon(Meteoconcs.Icon.met_clouds_flash);
        } else if (iconSetType.equals(context.getString(R.string.pref_icons_set_owm))) {
            Drawable thunderstorm = ContextCompat.getDrawable(context, R.raw.ic_owm_thunderstorm);
            Drawable drizzle = ContextCompat.getDrawable(context, R.raw.ic_owm_drizzle);
            Drawable rain_day = ContextCompat.getDrawable(context, R.raw.ic_owm_day_light_rain);
            Drawable rain_night = ContextCompat.getDrawable(context, R.raw.ic_owm_night_light_rain);
            Drawable snow = ContextCompat.getDrawable(context, R.raw.ic_owm_snow);
            Drawable atmosphere = ContextCompat.getDrawable(context, R.raw.ic_owm_atmosphere);
            Drawable sun = ContextCompat.getDrawable(context, R.raw.ic_owm_day_clear);
            Drawable moon = ContextCompat.getDrawable(context, R.raw.ic_owm_night_clear);
            Drawable clouds_few_day = ContextCompat.getDrawable(context, R.raw.ic_owm_day_clouds);
            Drawable clouds_few_night = ContextCompat.getDrawable(context, R.raw.ic_owm_night_clouds);
            Drawable clouds_scattered = ContextCompat.getDrawable(context, R.raw.ic_owm_clouds);
            Drawable clouds_broken = ContextCompat.getDrawable(context, R.raw.ic_owm_overcast_clouds);
            Drawable extreme = ContextCompat.getDrawable(context, R.drawable.ic_extreme);

            for (int i = ICON_INDEX_THUNDERSTORM_LIGHT_RAIN; i <= ICON_INDEX_THUNDERSTORM_HEAVY_DRIZZLE; i++) {
                iconsSet[i] = thunderstorm;
            }
            for (int i = ICON_INDEX_LIGHT_INTENSITY_DRIZZLE; i <= ICON_INDEX_SHOWER_DRIZZLE; i++) {
                iconsSet[i] = drizzle;
            }
            for (int i = ICON_INDEX_LIGHT_RAIN; i <= ICON_INDEX_EXTREME_RAIN; i++) {
                iconsSet[i] = rain_day;
            }
            iconsSet[ICON_INDEX_FREEZING_RAIN] = snow;
            for (int i = ICON_INDEX_LIGHT_INTENSITY_SHOWER_RAIN; i <= ICON_INDEX_RAGGED_SHOWER_RAIN; i++) {
                iconsSet[i] = drizzle;
            }
            for (int i = ICON_INDEX_LIGHT_RAIN_NIGHT; i <= ICON_INDEX_EXTREME_RAIN_NIGHT; i++) {
                iconsSet[i] = rain_night;
            }
            for (int i = ICON_INDEX_LIGHT_SNOW; i <= ICON_INDEX_HEAVY_SHOWER_SNOW; i++) {
                iconsSet[i] = snow;
            }
            for (int i = ICON_INDEX_MIST; i <= ICON_INDEX_TORNADO; i++) {
                iconsSet[i] = atmosphere;
            }
            iconsSet[ICON_INDEX_CLEAR_SKY_SUN] = sun;
            iconsSet[ICON_INDEX_CLEAR_SKY_MOON] = moon;
            iconsSet[ICON_INDEX_FEW_CLOUDS] = clouds_few_day;
            iconsSet[ICON_INDEX_FEW_CLOUDS_NIGHT] = clouds_few_night;
            iconsSet[ICON_INDEX_SCATTERED_CLOUDS] = clouds_scattered;
            iconsSet[ICON_INDEX_SCATTERED_CLOUDS_NIGHT] = clouds_scattered;
            iconsSet[ICON_INDEX_BROKEN_CLOUDS] = clouds_broken;
            iconsSet[ICON_INDEX_BROKEN_CLOUDS_NIGHT] = clouds_broken;
            iconsSet[ICON_INDEX_OVERCAST_CLOUDS] = clouds_broken;
            iconsSet[ICON_INDEX_OVERCAST_CLOUDS_NIGHT] = clouds_broken;
            for (int i = ICON_INDEX_EXTREME_TORNADO; i <= ICON_INDEX_EXTREME_HAIL; i++) {
                iconsSet[i] = extreme;
            }
            for (int i = ICON_INDEX_CALM; i <= ICON_INDEX_HURRICANE; i++) {
                iconsSet[i] = extreme;
            }
        } else {
            Drawable thunderstorm = ContextCompat.getDrawable(context, R.drawable.ic_thunderstorm);
            Drawable drizzle = ContextCompat.getDrawable(context, R.drawable.ic_drizzle);
            Drawable rain = ContextCompat.getDrawable(context, R.drawable.ic_rain);
            Drawable snow = ContextCompat.getDrawable(context, R.drawable.ic_snow);
            Drawable atmosphere = ContextCompat.getDrawable(context, R.drawable.ic_fog);
            Drawable sun = ContextCompat.getDrawable(context, R.drawable.ic_sun);
            Drawable moon = ContextCompat.getDrawable(context, R.drawable.ic_moon);
            Drawable clouds = ContextCompat.getDrawable(context, R.drawable.ic_cloud);
            Drawable extreme = ContextCompat.getDrawable(context, R.drawable.ic_extreme);

            for (int i = ICON_INDEX_THUNDERSTORM_LIGHT_RAIN; i <= ICON_INDEX_THUNDERSTORM_HEAVY_DRIZZLE; i++) {
                iconsSet[i] = thunderstorm;
            }
            for (int i = ICON_INDEX_LIGHT_INTENSITY_DRIZZLE; i <= ICON_INDEX_SHOWER_DRIZZLE; i++) {
                iconsSet[i] = drizzle;
            }
            for (int i = ICON_INDEX_LIGHT_RAIN; i <= ICON_INDEX_RAGGED_SHOWER_RAIN; i++) {
                iconsSet[i] = rain;
            }
            for (int i = ICON_INDEX_LIGHT_SNOW; i <= ICON_INDEX_HEAVY_SHOWER_SNOW; i++) {
                iconsSet[i] = snow;
            }
            for (int i = ICON_INDEX_MIST; i <= ICON_INDEX_TORNADO; i++) {
                iconsSet[i] = atmosphere;
            }
            iconsSet[ICON_INDEX_CLEAR_SKY_SUN] = sun;
            iconsSet[ICON_INDEX_CLEAR_SKY_MOON] = moon;
            for (int i = ICON_INDEX_FEW_CLOUDS; i <= ICON_INDEX_OVERCAST_CLOUDS_NIGHT; i++) {
                iconsSet[i] = clouds;
            }
            for (int i = ICON_INDEX_EXTREME_TORNADO; i <= ICON_INDEX_EXTREME_HAIL; i++) {
                iconsSet[i] = extreme;
            }
            for (int i = ICON_INDEX_CALM; i <= ICON_INDEX_HURRICANE; i++) {
                iconsSet[i] = extreme;
            }
        }
        iconsSet[ICON_INDEX_NA] = new IconicsDrawable(context)
                .icon(Meteoconcs.Icon.met_na);
        return iconsSet;
    }

    /**
     * Метод преобразует полученный тип осадков в перечисление типа Precipitation.Type, а так же
     * устанавливает свойства для класса {@link Weather}.
     *
     * @param id      идентификатор типа осадков.
     * @param weather объект типа {@link Weather}.
     */
    private static void setPrecipitationType(int id, Weather weather) {
        if (id >= 200 && id < 300) {
            switch (id) {
                case 200:
                    weather.setPrecipitation(Precipitation.Type.THUNDERSTORM_LIGHT_RAIN);
                    break;
                case 201:
                    weather.setPrecipitation(Precipitation.Type.THUNDERSTORM_RAIN);
                    break;
                case 202:
                    weather.setPrecipitation(Precipitation.Type.THUNDERSTORM_HEAVY_RAIN);
                    break;
                case 210:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_THUNDERSTORM);
                    break;
                case 212:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_THUNDERSTORM);
                    break;
                case 221:
                    weather.setPrecipitation(Precipitation.Type.RAGGED_THUNDERSTORM);
                    break;
                case 230:
                    weather.setPrecipitation(Precipitation.Type.THUNDERSTORM_LIGHT_DRIZZLE);
                    break;
                case 231:
                    weather.setPrecipitation(Precipitation.Type.THUNDERSTORM_DRIZZLE);
                    break;
                case 232:
                    weather.setPrecipitation(Precipitation.Type.THUNDERSTORM_HEAVY_DRIZZLE);
                    break;
                default:
                    weather.setPrecipitation(Precipitation.Type.THUNDERSTORM); // 211
                    break;
            }
        } else if (id >= 300 && id < 400) {
            switch (id) {
                case 300:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_INTENSITY_DRIZZLE);
                    break;
                case 302:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_INTENSITY_DRIZZLE);
                    break;
                case 310:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_INTENSITY_DRIZZLE_RAIN);
                    break;
                case 311:
                    weather.setPrecipitation(Precipitation.Type.DRIZZLE_RAIN);
                    break;
                case 312:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_INTENSITY_DRIZZLE_RAIN);
                    break;
                case 313:
                    weather.setPrecipitation(Precipitation.Type.SHOWER_RAIN_AND_DRIZZLE);
                    break;
                case 314:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_SHOWER_RAIN_AND_DRIZZLE);
                    break;
                case 321:
                    weather.setPrecipitation(Precipitation.Type.SHOWER_DRIZZLE);
                    break;
                default:
                    weather.setPrecipitation(Precipitation.Type.DRIZZLE); // 301
                    break;
            }
        } else if (id >= 500 && id < 600) {
            switch (id) {
                case 500:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_RAIN);
                    break;
                case 501:
                    weather.setPrecipitation(Precipitation.Type.MODERATE_RAIN);
                    break;
                case 502:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_INTENSITY_RAIN);
                    break;
                case 503:
                    weather.setPrecipitation(Precipitation.Type.VERY_HEAVY_RAIN);
                    break;
                case 504:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_RAIN);
                    break;
                case 511:
                    weather.setPrecipitation(Precipitation.Type.FREEZING_RAIN);
                    break;
                case 520:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_INTENSITY_SHOWER_RAIN);
                    break;
                case 521:
                    weather.setPrecipitation(Precipitation.Type.SHOWER_RAIN);
                    break;
                case 522:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_INTENSITY_SHOWER_RAIN);
                    break;
                case 531:
                    weather.setPrecipitation(Precipitation.Type.RAGGED_SHOWER_RAIN);
                    break;
                default:
                    weather.setPrecipitation(Precipitation.Type.RAIN);  // DEFAULT == SHOWER_RAIN
                    break;
            }
        } else if (id >= 600 && id < 700) {
            switch (id) {
                case 600:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_SNOW);
                    break;
                case 602:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_SNOW);
                    break;
                case 611:
                    weather.setPrecipitation(Precipitation.Type.SLEET);
                    break;
                case 612:
                    weather.setPrecipitation(Precipitation.Type.SHOWER_SLEET);
                    break;
                case 615:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_RAIN_AND_SNOW);
                    break;
                case 616:
                    weather.setPrecipitation(Precipitation.Type.RAIN_AND_SNOW);
                    break;
                case 620:
                    weather.setPrecipitation(Precipitation.Type.LIGHT_SHOWER_SNOW);
                    break;
                case 621:
                    weather.setPrecipitation(Precipitation.Type.SHOWER_SNOW);
                    break;
                case 622:
                    weather.setPrecipitation(Precipitation.Type.HEAVY_SHOWER_SNOW);
                    break;
                default:
                    weather.setPrecipitation(Precipitation.Type.SNOW);  // 601
                    break;
            }
        } else if (id >= 700 && id < 800) {
            switch (id) {
                case 701:
                    weather.setPrecipitation(Precipitation.Type.MIST);
                    break;
                case 711:
                    weather.setPrecipitation(Precipitation.Type.SMOKE);
                    break;
                case 721:
                    weather.setPrecipitation(Precipitation.Type.HAZE);
                    break;
                case 731:
                    weather.setPrecipitation(Precipitation.Type.SAND_DUST_WHIRLS);
                    break;
                case 741:
                    weather.setPrecipitation(Precipitation.Type.FOG);
                    break;
                case 751:
                    weather.setPrecipitation(Precipitation.Type.SAND);
                    break;
                case 761:
                    weather.setPrecipitation(Precipitation.Type.DUST);
                    break;
                case 762:
                    weather.setPrecipitation(Precipitation.Type.VOLCANIC_ASH);
                    break;
                case 771:
                    weather.setPrecipitation(Precipitation.Type.SQUALLS);
                    break;
                case 781:
                    weather.setPrecipitation(Precipitation.Type.TORNADO);
                    break;
                default:
                    weather.setPrecipitation(Precipitation.Type.ATMOSPHERE);    // DEFAULT == FOG
                    break;
            }
        } else if (id == 800) {
            weather.setPrecipitation(Precipitation.Type.CLEAR_SKY);
        } else if (id > 800 && id < 805) {
            switch (id) {
                case 801:
                    weather.setPrecipitation(Precipitation.Type.FEW_CLOUDS);
                    break;
                case 802:
                    weather.setPrecipitation(Precipitation.Type.SCATTERED_CLOUDS);
                    break;
                case 803:
                    weather.setPrecipitation(Precipitation.Type.BROKEN_CLOUDS);
                    break;
                case 804:
                    weather.setPrecipitation(Precipitation.Type.OVERCAST_CLOUDS);
                    break;
                default:
                    weather.setPrecipitation(Precipitation.Type.SCATTERED_CLOUDS);  // DEFAULT == SCATTERED_CLOUDS
                    break;
            }
        } else if (id >= 900 && id < 910) {
            switch (id) {
                case 900:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_TORNADO);
                    break;
                case 901:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_TROPICAL_STORM);
                    break;
                case 902:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_HURRICANE);
                    break;
                case 903:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_COLD);
                    break;
                case 904:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_HOT);
                    break;
                case 905:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_WINDY);
                    break;
                case 906:
                    weather.setPrecipitation(Precipitation.Type.EXTREME_HAIL);
                    break;
                default:
                    weather.setPrecipitation(Precipitation.Type.EXTREME);   // DEFAULT == ICON_INDEX_EXTREME_TORNADO;
                    break;
            }
        } else if (id == 951) {
            weather.setPrecipitation(Precipitation.Type.CALM);
        } else if (id == 952) {
            weather.setPrecipitation(Precipitation.Type.LIGHT_BREEZE);
        } else if (id == 953) {
            weather.setPrecipitation(Precipitation.Type.GENTLE_BREEZE);
        } else if (id == 954) {
            weather.setPrecipitation(Precipitation.Type.MODERATE_BREEZE);
        } else if (id == 955) {
            weather.setPrecipitation(Precipitation.Type.FRESH_BREEZE);
        } else if (id == 956) {
            weather.setPrecipitation(Precipitation.Type.STRONG_BREEZE);
        } else if (id == 957) {
            weather.setPrecipitation(Precipitation.Type.HIGH_WIND_NEAR_GALE);
        } else if (id == 958) {
            weather.setPrecipitation(Precipitation.Type.GALE);
        } else if (id == 959) {
            weather.setPrecipitation(Precipitation.Type.SEVERE_GALE);
        } else if (id == 960) {
            weather.setPrecipitation(Precipitation.Type.STORM);
        } else if (id == 961) {
            weather.setPrecipitation(Precipitation.Type.VIOLENT_STORM);
        } else if (id == 962) {
            weather.setPrecipitation(Precipitation.Type.HURRICANE);
        } else {
            weather.setPrecipitation(Precipitation.Type.NO_DATA);
        }
    }

    /**
     * Метод для преобразования погодных характеристик в заданные пользователями метрики
     *
     * @param weather обьект класса {@link Weather}, в котором нужно привести погодные характеристики к заданным метрикам
     * @return обьект класса {@link Weather} с преобразованными погодными характеристиками
     */
    public static Weather formatWeather(Weather weather, AppUtils.TemperatureMetrics temperatureMetric, AppUtils.SpeedMetrics speedMetric, AppUtils.PressureMetrics pressureMetric) {
        weather.setTemperature(formatTemperature(weather.getTemperature(), temperatureMetric));
        weather.setPressure(formatPressure(weather.getPressure(), pressureMetric));
        weather.setWind(weather.getWindDirection(), formatSpeed(weather.getWindPower(), speedMetric));
        return weather;
    }

    /**
     * Метод для преобразования температуры в заданную пользователем метрику
     *
     * @param temperature       температура на входе (в Кельвинах)
     * @param temperatureMetric
     * @return температура в выбранной пользователем метрике
     */
    public static double formatTemperature(double temperature, AppUtils.TemperatureMetrics temperatureMetric) {
        switch (temperatureMetric) {
            case KELVIN:
                break;
            case CELSIUS:
                return kelvinToCelsius(temperature);
            case FAHRENHEIT:
                return kelvinToFahrenheit(temperature);
        }
        return temperature;
    }

    /**
     * Метод для преобразования скорости ветра в заданную пользователем метрику
     *
     * @param speed       преобразуемая скорость
     * @param speedMetric
     * @return скорость в выбранной пользователем метрике
     */
    public static double formatSpeed(double speed, AppUtils.SpeedMetrics speedMetric) {
        switch (speedMetric) {
            case METER_PER_SECOND:
                break;
            case FOOT_PER_SECOND:
                return meterInSecondToFootInSecond(speed);
            case KM_PER_HOURS:
                return meterInSecondToKmInHours(speed);
            case MILES_PER_HOURS:
                return meterInSecondToMilesInHour(speed);
        }
        return speed;
    }

    /**
     * Метод для преобразования давления в заданную пользователем метрику
     *
     * @param pressure       преобразуемое давление
     * @param pressureMetric
     * @return давление в выбранной пользователем метрике
     */
    public static double formatPressure(double pressure, AppUtils.PressureMetrics pressureMetric) {
        switch (pressureMetric) {
            case HPA:
                break;
            case MM_HG:
                return hpaToMmHg(pressure);
        }
        return pressure;
    }

    /**
     * Метод для преобразования температуры из Кельвинов в Цельсии
     *
     * @param temperature температура в Кельвинах
     * @return температура в Цельсиях
     */
    public static double kelvinToCelsius(double temperature) {
        double celsiusTemperature = temperature - KELVIN_CELSIUS_DELTA;
        return celsiusTemperature;
    }

    /**
     * Метод для преобразования температуры из Кельвина в Фаренгейт
     *
     * @param temperature температура в Кельвинах
     * @return температура в Фаренгейтах
     */
    public static double kelvinToFahrenheit(double temperature) {
        double fahrenheitTemperature = (kelvinToCelsius(temperature) * 9 / 5) + 32;
        return fahrenheitTemperature;
    }

    /**
     * Метод для преобразования скорости ветра из метров в секунду в футы в секунду
     *
     * @param speed скорость ветра в метрах в секунду
     * @return скорость ветра в футах в секунду
     */
    public static double meterInSecondToFootInSecond(double speed) {
        double footInSecond = speed * METER_TO_FOOT;
        return footInSecond;
    }

    /**
     * Метод для преобразования скорости ветра из метров в секунду в километры в час
     *
     * @param speed скорость ветра в метрах в секунду
     * @return скорость ветра в километрах в час
     */
    public static double meterInSecondToKmInHours(double speed) {
        double kmInHours = speed * 3.6;
        return kmInHours;
    }

    /**
     * Метод для преобразования скорости ветра из метров в секунду в мили в час
     *
     * @param speed скорость ветра в метрах в секунду
     * @return скорость ветра в милях в час
     */
    public static double meterInSecondToMilesInHour(double speed) {
        double milesInHours = meterInSecondToKmInHours(speed) * KM_TO_MILES;
        return milesInHours;
    }

    /**
     * Метод для преобразования давления из килопаскалей в мм.рт.ст.
     *
     * @param pressure давление в килопаскалях
     * @return давление в мм.рт.ст.
     */
    public static double hpaToMmHg(double pressure) {
        double mmHg = pressure / HPA_TO_MM_HG;
        return mmHg;
    }

    /**
     * Определение времени суток
     */
    public static boolean isDayFromString(String timeString) {
        timeString = timeString.substring(0, 2);
        try {
            int time = Integer.parseInt(timeString);
            if (time >= 21 || time < 6) return false;
            return true;
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    /**
     * Получение дня
     **/
    public static String getDayName(Context context, Calendar calendar) {
        int rightnow = Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        if (rightnow == today) {
            return context.getString(R.string.today);
        } else if (today == rightnow + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, d MMMM", Locale.getDefault());
            String myString = dayFormat.format(calendar.getTime());
            return myString.substring(0, 1).toUpperCase() + myString.substring(1);
        }
    }

    /**
     * Получение времени
     **/
    public static String getTime(Context context, Calendar calendar) {
        int rightnow = Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (rightnow == today) {
            return timeFormat.format(calendar.getTime());
        } else {
            return timeFormat.format(calendar.getTime())
                    + ", "
                    + context.getString(R.string.tomorrow).toLowerCase();
        }
    }
}
