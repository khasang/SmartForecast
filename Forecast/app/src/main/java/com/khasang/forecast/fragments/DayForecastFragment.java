package com.khasang.forecast.fragments;

import com.khasang.forecast.position.Weather;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Created by CopyPasteStd on 24.11.15.
 * Фрагмент для отображения погоды по дням
 */
public class DayForecastFragment extends CommonForecastFragment {

    @Override
    protected void updateForecasts() {
        if (forecasts == null) {
            return; // TODO: пока просто выходим
        }
        // TODO: пока заполняю списки просто в цикле
        for (Map.Entry<Calendar, Weather> entry : forecasts.entrySet()) {
            Calendar calendar = entry.getKey();
            String dayOfWeek = String.format(Locale.getDefault(), "%ta", calendar);
            sDate.add(dayOfWeek);

            weathers.add(entry.getValue());
        }
    }

}
