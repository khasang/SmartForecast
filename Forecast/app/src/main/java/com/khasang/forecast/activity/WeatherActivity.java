package com.khasang.forecast.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.R;
import com.khasang.forecast.adapter.DailyForecastPageAdapter;

import java.awt.font.TextAttribute;

/**
 * Данные которые необходимо отображать в WeatherActivity (для первого релиза):
 * город, температура, давление, влажность, ветер, временная метка.
 */

public class WeatherActivity extends FragmentActivity implements View.OnClickListener{
    /**
     * ViewPager для отображения нижних вкладок прогноза: по часам и по дням
     */
    private ViewPager pager;

    private TextView city;
    private TextView temperature;
    private TextView presure;
    private TextView wind;
    private TextView humidity;
    private TextView timeStamp;
    private ImageButton syncBtn;

    // Для тестирования
    private String current_city = "Berlin";
    private int current_temperature = 20;
    private int current_presure = 40;
    private int current_wind = 25;
    private int current_humidity = 12;
    private String current_timeStamp = "10:15";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        city = (TextView) findViewById(R.id.city);
        temperature = (TextView) findViewById(R.id.temperature);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        timeStamp = (TextView) findViewById(R.id.timeStamp);
        syncBtn = (ImageButton) findViewById(R.id.syncBtn);

        syncBtn.setOnClickListener(this);


        /**
         * Код для фрагментов
         */
        pager = (ViewPager) findViewById(R.id.pager);
        DailyForecastPageAdapter adapter = new DailyForecastPageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Let's sync", Toast.LENGTH_SHORT).show();
        city.setText(String.valueOf(current_city));
        temperature.setText(String.valueOf(current_temperature));
        wind.setText(getString(R.string.wind) + " " + String.valueOf(current_wind) + " " + getString(R.string.wind_measure));
        humidity.setText(String.valueOf(current_humidity));
        timeStamp.setText(String.valueOf(current_timeStamp));

    }
}
