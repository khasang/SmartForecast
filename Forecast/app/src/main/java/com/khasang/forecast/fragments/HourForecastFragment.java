package com.khasang.forecast.fragments;

import com.khasang.forecast.position.Weather;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Created by CopyPasteStd on 01.12.15.
 * Фрагмент для отображения погоды по часам
 */

public class HourForecastFragment extends CommonForecastFragment {

    @Override
    protected void updateForecasts() {
        for (Map.Entry<Calendar, Weather> entry : forecasts.entrySet()) {
            Calendar calendar = entry.getKey();
            String sTime = String.format(Locale.getDefault(), "%tR", calendar);
            sDate.add(sTime);
            weathers.add(entry.getValue());
        }
    }

}
