package com.khasang.forecast;

import com.khasang.forecast.models.DailyForecastList;
import com.khasang.forecast.models.DailyResponse;
import com.khasang.forecast.models.HourlyForecastList;
import com.khasang.forecast.models.OpenWeatherMapResponse;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AppUtils {

    public static Weather convertToWeather(OpenWeatherMapResponse response) {
        Weather weather = new Weather();
        weather.setTemperature(response.getMain().getTemp());
        weather.setHumidity(response.getMain().getHumidity());
        weather.setPressure(response.getMain().getPressure());
        double speed = response.getWind().getSpeed();
        double deg = response.getWind().getDeg();
        setWindDirectionAndSpeed(weather, speed, deg);
        String description = null;
        for (com.khasang.forecast.models.Weather descr : response.getWeather()) {
            description = descr.getDescription();
        }
        weather.setDescription(description);
        return weather;
    }

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
            String description = null;
            for (com.khasang.forecast.models.Weather descr : forecast.getWeather()) {
                description = descr.getDescription();
            }
            weather.setDescription(description);
            map.put(calendar, weather);
        }
        return map;
    }

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
            String description = null;
            for (com.khasang.forecast.models.Weather descr : forecast.getWeather()) {
                description = descr.getDescription();
            }
            weather.setDescription(description);
            map.put(calendar, weather);
        }
        return map;
    }

    private static void setWindDirectionAndSpeed(Weather weather, double speed, double deg) {
        if (deg >= 0 && deg <= 22.5) {
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
        } else if (deg > 337.5 && deg <= 360) {
            weather.setWind(Wind.Direction.NORTH, speed);
        }
    }

    private static Calendar unixToCalendar(long unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000L);
        return calendar;
    }

}
