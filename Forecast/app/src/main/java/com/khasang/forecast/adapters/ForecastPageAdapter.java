package com.khasang.forecast.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.khasang.forecast.Weather;
import com.khasang.forecast.fragments.CommonForecastFragment;
import com.khasang.forecast.fragments.DayForecastFragment;
import com.khasang.forecast.fragments.HourForecastFragment;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by qwsa on 24.11.15.
 */
public class ForecastPageAdapter extends FragmentPagerAdapter {
    private CommonForecastFragment[] fragments;

    public ForecastPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        fragments = new CommonForecastFragment[2];
        fragments[0] = new HourForecastFragment();
        fragments[1] = new DayForecastFragment();
    }

    public void setHourForecast(Map<Calendar, Weather> forecasts) {
        fragments[0].setDatas(forecasts);
    }

    public void setDayForecast(Map<Calendar, Weather> forecasts) {
        fragments[1].setDatas(forecasts);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return fragments.length;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }
}
