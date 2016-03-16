package com.khasang.forecast.orm;

import android.util.Log;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.orm.tables.WeatherTable;
import com.khasang.forecast.position.Precipitation;
import com.khasang.forecast.position.Weather;
import com.khasang.forecast.position.Wind;
import com.khasang.forecast.stations.WeatherStationFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maxim.kulikov on 16.03.2016.
 */
public class WeatherManager {

    private String mTag = "SugarTest";
    private SimpleDateFormat dtFormat;

    public WeatherManager() {
        dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    // Очистка таблицы от старых данных, чтобы не было дублей.
    public void deleteDoubleWeather(WeatherStationFactory.ServiceType stationName, String townName, Calendar date) {
        List<WeatherTable> list = new ArrayList<>();
        try {
            list = WeatherTable.find(WeatherTable.class, "stationName = ? and town = ? and date = ?", new String[]{stationName.name(), townName, dtFormat.format(date.getTime())});
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).delete();
                }
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Сохранение погоды, удаление старой погоды.
    public void saveWeather(WeatherStationFactory.ServiceType serviceType, String townName, Calendar date, Weather weather) {
        deleteDoubleWeather(serviceType, townName, date);

        new WeatherTable(
                serviceType.name(),
                townName,
                dtFormat.format(date.getTime()),
                weather.getTemperature(),
                weather.getTemp_min(),
                weather.getTemp_max(),
                weather.getPressure(),
                weather.getHumidity(),
                weather.getDescription(),
                weather.getWindDirection().name(),
                weather.getWindPower(),
                weather.getPrecipitation().name()
        ).save();
    }

    // Очистка таблицы от данных, старше определенной даты.
    public void deleteOldWeather(WeatherStationFactory.ServiceType stationName, String townName, Calendar date) {
        List<WeatherTable> list = new ArrayList<>();
        try {
            list = WeatherTable.find(WeatherTable.class, "stationName = ? and town = ? and date < (SELECT MAX(date) FROM WeatherTable WHERE date < ? )",
                    new String[]{stationName.name(), townName, dtFormat.format(date.getTime())});

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).delete();
                }
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Очистка таблицы от данных, старше определенной даты, для всех городов.
    public void deleteOldWeatherAllTowns(WeatherStationFactory.ServiceType stationName, Calendar date) {
        List<WeatherTable> list = new ArrayList<>();
        try {
            list = WeatherTable.find(WeatherTable.class, "stationName = ? and date < (SELECT MAX(date) FROM WeatherTable WHERE date < ? )",
                    new String[]{stationName.name(), dtFormat.format(date.getTime())});

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).delete();
                }
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    public void deleteTownWeather(String townName) {
        List<WeatherTable> list = new ArrayList<>();
        try {
            list = WeatherTable.find(WeatherTable.class, "town = ?", new String[]{townName});
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).delete();
                }
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Загрузка погоды.
    public HashMap<Calendar, Weather> loadWeather(WeatherStationFactory.ServiceType stationName, String townName, Calendar date, AppUtils.TemperatureMetrics tm, AppUtils.SpeedMetrics sm, AppUtils.PressureMetrics pm) {
        Wind wind;
        Precipitation precipitation;
        Weather weather = null;
        HashMap<Calendar, Weather> hashMap = null;
        Calendar weatherDate = null;
        List<WeatherTable> list;

        try {
            list = WeatherTable.find(
                    WeatherTable.class,
                    " stationName = ? and town = ? " +
                            " ORDER BY ABS( CAST(strftime('%s', DATE) AS int) - CAST(strftime('%s', ?) AS int) ) ASC " +
                            " LIMIT 1",
                    new String[]{stationName.name(), townName, dtFormat.format(date.getTime())}
            );

            if (list != null) {
                weatherDate = Calendar.getInstance();
                weatherDate.setTime(dtFormat.parse(list.get(0).date));

                wind = new Wind();
                wind.setDirection(list.get(0).windDirection);
                wind.setSpeed(list.get(0).windSpeed);

                precipitation = new Precipitation();
                precipitation.setType(list.get(0).precipitationType);

                weather = new Weather(
                        list.get(0).temperature,
                        list.get(0).temperatureMin,
                        list.get(0).temperatureMax,
                        list.get(0).pressure,
                        list.get(0).humidity,
                        wind,
                        precipitation,
                        list.get(0).description
                );
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }

        if (weather != null) {
            hashMap = new HashMap<>();
            hashMap.put(weatherDate, AppUtils.formatWeather(weather, tm, sm, pm));
        }
        return hashMap;
    }
}
