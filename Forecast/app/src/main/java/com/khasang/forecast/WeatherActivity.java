package com.khasang.forecast;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.khasang.forecast.adapter.DailyForecastPageAdapter;

public class WeatherActivity extends FragmentActivity {

    /** ViewPager для отображения нижних вкладок прогноза: по часам и по дням */
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        pager = (ViewPager) findViewById(R.id.pager);


        DailyForecastPageAdapter adapter = new DailyForecastPageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
    }
}
