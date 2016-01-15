package com.khasang.forecast.activities;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.khasang.forecast.R;
import com.khasang.forecast.adapters.CitySpinnerAdapter;
import com.khasang.forecast.fragments.DayForecastFragment;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivityMd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_material);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_material);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        CitySpinnerAdapter spinnerAdapter = new CitySpinnerAdapter();
        spinnerAdapter.addItem("Москва");
        spinnerAdapter.addItem("Санкт-Петербург");
        spinnerAdapter.addItem("Сочи");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            DayForecastFragment firstFragment = new DayForecastFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }
}
