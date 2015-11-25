package com.khasang.forecast.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.R;
import com.khasang.forecast.fragment.DayForecastFragment;

/**
 * Created by qwsa on 24.11.15.
 */
public class DailyForecastPageAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;

    public DailyForecastPageAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;

    }

    @Override
    public Fragment getItem(int position) {
        DayForecastFragment f = new DayForecastFragment();
        return f;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
