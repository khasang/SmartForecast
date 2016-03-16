package com.khasang.forecast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
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
                showInfoMessage(activity, string);
            } else {
                showInfoMessage(string);
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

    public static void showInfoMessage(Activity activity, CharSequence string) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.warning_toast, ((ViewGroup) activity.findViewById(R.id.toast_layout_root)));
        ((TextView) layout.findViewById(R.id.warningMessage)).setText(string);
        Toast toast = new Toast(MyApplication.getAppContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showInfoMessage(CharSequence string) {
        Toast.makeText(MyApplication.getAppContext(), string, Toast.LENGTH_SHORT).show();
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

    /**
     * Метод преобразует полученный тип осадков в перечисление типа Precipitation.Type, а так же
     * устанавливает свойства для класса {@link Weather}.
     *
     * @param type    строка, приходящая как тип осадков.
     * @param weather объект типа {@link Weather}.
     */
    private static void setPrecipitationType(String type, Weather weather) {
        switch (type) {
            case "Thunderstorm":
                weather.setPrecipitation(Precipitation.Type.THUNDERSTORM);
                break;
            case "Drizzle":
                weather.setPrecipitation(Precipitation.Type.DRIZZLE);
                break;
            case "Rain":
                weather.setPrecipitation(Precipitation.Type.RAIN);
                break;
            case "Snow":
                weather.setPrecipitation(Precipitation.Type.SNOW);
                break;
            case "Atmosphere":
                weather.setPrecipitation(Precipitation.Type.ATMOSPHERE);
                break;
            case "Clear":
                weather.setPrecipitation(Precipitation.Type.CLEAR);
                break;
            case "Clouds":
                weather.setPrecipitation(Precipitation.Type.CLOUDS);
                break;
            case "Extreme":
                weather.setPrecipitation(Precipitation.Type.EXTREME);
                break;
            case "Additional":
                weather.setPrecipitation(Precipitation.Type.ADDITIONAL);
                break;
        }
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
            weather.setPrecipitation(Precipitation.Type.THUNDERSTORM);
        } else if (id >= 300 && id < 400) {
            weather.setPrecipitation(Precipitation.Type.DRIZZLE);
        } else if (id >= 500 && id < 600) {
            weather.setPrecipitation(Precipitation.Type.RAIN);
        } else if (id >= 600 && id < 700) {
            weather.setPrecipitation(Precipitation.Type.SNOW);
        } else if (id >= 700 && id < 800) {
            weather.setPrecipitation(Precipitation.Type.ATMOSPHERE);
        } else if (id == 800) {
            weather.setPrecipitation(Precipitation.Type.CLEAR);
        } else if (id > 800 && id < 805) {
            weather.setPrecipitation(Precipitation.Type.CLOUDS);
        } else if (id >= 900 && id < 910) {
            weather.setPrecipitation(Precipitation.Type.EXTREME);
        } else if (id >= 951 && id < 963) {
            weather.setPrecipitation(Precipitation.Type.ADDITIONAL);
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
