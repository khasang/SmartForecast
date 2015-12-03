package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.R;

/**
 * Created by CopyPasteStd on 01.12.15.
 */

public class HourForecastFragment extends Fragment {

   /* private DayForecast dayForecast;

    public DayForecastFragment() {}

    public void setForecast(DayForecast dayForecast) {

        this.dayForecast = dayForecast;

    }*/

    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static DayForecastFragment newInstance(int page, String title) {
        DayForecastFragment fragmentFirst = new DayForecastFragment();
       /* Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args)*/;
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //page = getArguments().getInt("someInt", 0);
        //title = getArguments().getString("someTitle");
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dayforecast_fragment, container, false);

        return v;
    }

}
