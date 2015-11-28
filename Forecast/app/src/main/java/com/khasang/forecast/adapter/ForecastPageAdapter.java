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
public class ForecastPageAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    public ForecastPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /*private FragmentManager fm;

    public DailyForecastPageAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }*/

  /*  @Override
    public Fragment getItem(int position) {
        DayForecastFragment f = new DayForecastFragment();
        return f;
    }*/

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return DayForecastFragment.newInstance(0, "Page # 1");
            /*case 1: // Fragment # 0 - This will show FirstFragment different title
                return FirstFragment.newInstance(1, "Page # 2");
            case 2: // Fragment # 1 - This will show SecondFragment
                return SecondFragment.newInstance(2, "Page # 3");*/
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
   /* @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }*/
}
