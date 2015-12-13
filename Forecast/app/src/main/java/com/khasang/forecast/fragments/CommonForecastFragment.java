package com.khasang.forecast.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.R;
import com.khasang.forecast.Weather;
import com.khasang.forecast.adapters.CustomAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import it.gmariotti.recyclerview.adapter.AlphaAnimatorAdapter;

/**
 * Created by aleksandrlihovidov on 05.12.15.
 * Родительский класс для фрагментов
 * DayForecastFragment && HourForecastFragment
 */
public abstract class CommonForecastFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    protected Map<Calendar, Weather> forecasts;

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected CustomAdapter adapter;
    protected ArrayList<String> sDate;
    protected ArrayList<Weather> weathers;

    public void setDatas(Map<Calendar, Weather> forecasts) {
        this.forecasts = forecasts;
        sDate.clear();
        weathers.clear();
        updateForecasts();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sDate = new ArrayList<>();
        weathers = new ArrayList<>();
    }

    protected abstract void updateForecasts();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view_frag, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);


        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CustomAdapter(sDate, weathers);

        AlphaAnimatorAdapter animatorAdapter = new AlphaAnimatorAdapter(adapter, recyclerView);

        recyclerView.setAdapter(animatorAdapter);
        //recyclerView.setAdapter(adapter);


        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "OnResume");
        AlphaAnimatorAdapter animatorAdapter = new AlphaAnimatorAdapter(adapter, recyclerView);
        recyclerView.setAdapter(animatorAdapter);
    }

}
