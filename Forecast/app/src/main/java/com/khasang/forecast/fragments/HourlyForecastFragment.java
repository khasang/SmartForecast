package com.khasang.forecast.fragments;

import com.khasang.forecast.utils.AppUtils;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.position.Weather;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by CopyPasteStd on 01.12.15.
 * Фрагмент для отображения погоды по часам
 */
public class HourlyForecastFragment extends CommonForecastFragment {

    private int timeZone;

    @Override
    protected void updateForecasts() {
        for (Map.Entry<Calendar, Weather> entry : forecasts.entrySet()) {
            Calendar calendar = entry.getKey();
            String sTime = AppUtils.getTime(MyApplication.getAppContext(), calendar, timeZone);
            sDate.add(sTime);
            weathers.add(entry.getValue());
        }
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }
}
