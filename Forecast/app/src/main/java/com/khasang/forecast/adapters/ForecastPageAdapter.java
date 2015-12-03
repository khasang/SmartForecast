package com.khasang.forecast.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.khasang.forecast.fragments.DayForecastFragment;
import com.khasang.forecast.fragments.HourForecastFragment;

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
            case 0:
                return DayForecastFragment.newInstance(0, "Page # 1");
            case 1:
                return HourForecastFragment.newInstance(1, "Page # 1");
            default:
                Log.i("Adapter", "NULL");
                return null;
        }
    }

    // Returns the page title for the top indicator
   /* @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }*/
}
