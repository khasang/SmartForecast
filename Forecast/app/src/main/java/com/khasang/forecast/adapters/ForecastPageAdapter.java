package com.khasang.forecast.adapters;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.khasang.forecast.Weather;
import com.khasang.forecast.fragments.CommonForecastFragment;
import com.khasang.forecast.fragments.DayForecastFragment;
import com.khasang.forecast.fragments.HourForecastFragment;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by CopyPasteStd on 24.11.15.
 * Адаптер для отображения погоды по часам и по дням
 */
public class ForecastPageAdapter extends FragmentPagerAdapter
    implements ViewPager.OnPageChangeListener {

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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.i("TAG", "onPageScrolled");
    }

    @Override
    public void onPageSelected(int position) {
//        Log.i("TAG", "onPageSelected");
        View view = fragments[position].getView();
        view.setAlpha(0.2f);
        view.setScaleX(0.3f);
        view.setScaleY(0.3f);
        ViewCompat.animate(view)
                .alpha(1.0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start();

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.i("TAG", "onPageScrollStateChanged");
    }
}
