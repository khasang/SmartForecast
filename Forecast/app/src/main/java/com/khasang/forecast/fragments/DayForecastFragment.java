package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.Weather;
import com.khasang.forecast.adapters.CustomAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Created by qwsa on 24.11.15.
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
