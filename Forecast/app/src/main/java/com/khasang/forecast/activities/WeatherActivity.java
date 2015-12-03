package com.khasang.forecast.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.OpenWeatherMap;
import com.khasang.forecast.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.Weather;
import com.khasang.forecast.adapters.ForecastPageAdapter;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Данные которые необходимо отображать в WeatherActivity (для первого релиза):
 * город, температура, давление, влажность, ветер, временная метка.
 */

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * ViewPager для отображения нижних вкладок прогноза: по часам и по дням
     */
    private ViewPager pager;

    String TAG = this.getClass().getSimpleName();

    private TextView city;
    private TextView temperature;
    private TextView precipitation;
    private TextView pressure;
    private TextView wind;
    private TextView humidity;
    private TextView timeStamp;
    private ImageButton syncBtn;
    private ImageButton cityPickerBtn;

    private Animation animationRotateCenter;

//    int hours;
//    int minutes;

    private final int CHOOSE_CITY = 1;
    // Для тестирования
/*
    private String current_city = "London";
    private int current_temperature = 1;
    //private Precipitation current_precipitation;
    private String current_precipitation = "Солнечно";
    private int current_pressure = 40;
    private int current_wind = 25;
    private int current_humidity = 12;
    private String current_timeStamp = "10:15";
*/

    // Для заглушки в PositionManager
    /*private String current_city = "Berlin";
    private int current_temperature = 10;
    private String current_precipitation = "Солнечно";
    private double current_pressure = 35.0;
    private double current_wind = 11;
    private int current_humidity = 90;
    private String current_timeStamp = "10:15";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        PositionManager.getInstance().initManager(this);

        city = (TextView) findViewById(R.id.city);
        temperature = (TextView) findViewById(R.id.temperature);
        precipitation = (TextView) findViewById(R.id.precipitation);
        pressure = (TextView) findViewById(R.id.pressure);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        timeStamp = (TextView) findViewById(R.id.timeStamp);
        syncBtn = (ImageButton) findViewById(R.id.syncBtn);
        cityPickerBtn = (ImageButton) findViewById(R.id.cityPickerBnt);

        /** Анимация кнопки */
        animationRotateCenter = AnimationUtils.loadAnimation(
                this, R.anim.rotate_center);

        syncBtn.setOnClickListener(this);
        cityPickerBtn.setOnClickListener(this);

        /**
         * Код для фрагментов
         */
        pager = (ViewPager) findViewById(R.id.pager);
        ForecastPageAdapter adapter = new ForecastPageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        PositionManager.getInstance().updateCurrent();
    }

    /**
     * Обработчик нажатия кнопки
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.syncBtn:
                //manager.updateCurrent();
                //manager.updateHourly();
                syncBtn.startAnimation(animationRotateCenter);
//                manager.addPosition(current_city);            Это итак делается в менеджере
//               manager.setCurrentPosition(current_city);      Это делается в менеджере
                PositionManager.getInstance().updateCurrent();

                //updateInterface(manager.updateCurrent());
                //updateHourForecast(manager.updateHourly());
                break;
            case R.id.cityPickerBnt:
                //startActivity(new Intent(this, CityPickerActivity.class));
                startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
                break;
        }
        //onActivityResult()
    }

    /**
     * Обновление интерфейса Activity
     */
    public void updateInterface(Calendar date, Weather wCurent) {

        //TODO нужно перепроверить
        if (wCurent == null) {
            Log.i(TAG, "Weather is null!");
            return;
        }
        /** Получаем текущее время
         * TODO minutes в формате 13:04, сейчас выводит 13:4
         * TODO UPDATE Check fixes
         * */
        int hours = date.get(Calendar.HOUR_OF_DAY);
        int minutes = date.get(Calendar.MINUTE);

        //updateCurrent(Weather w);
        city.setText(PositionManager.getInstance().getCurrentPositionName()); // отображаем имя текущей локации
        temperature.setText(String.format("%.0f°C", wCurent.getTemperature()));

        //precipitation.setText(String.format("%s %s", w.getPrecipitation(), w.getPrecipitationProbability()));
        precipitation.setText(String.format("%s", wCurent.getPrecipitation()));

        pressure.setText(String.format("%s %.0f %s",
                getString(R.string.pressure),
                wCurent.getPressure(),
                getString(R.string.pressure_measure)));

        wind.setText(Html.fromHtml(String.format("%s %s %.0f%s",
                getString(R.string.wind),
                wCurent.getWindDirection(),
                wCurent.getWindPower(),
                getString(R.string.wind_measure))));

        humidity.setText(String.format("%s %s%%",
                getString(R.string.humidity),
                wCurent.getHumidity()));

      /*  timeStamp.setText(String.format("%s %d:%02d",
                getString(R.string.timeStamp),
                hours,
                minutes)); */

        timeStamp.setText(String.format("%s %d:%02d",
                getString(R.string.timeStamp),
                hours,
                minutes));
    }

    //TODO Реализовать метод получения прогноза по часам
    public void updateHourForecast(Weather wHour) {

    }

    //TODO Реализовать метод получения прогноза по дням
    public void updateDayForecast(Weather wDay) {

    }


    /**
     * Получаем город из CityPickActivity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int size = PositionManager.getInstance().getPositions().size();
        if (size == 0) {
            city.setText("--/--");
            temperature.setText("--/--");
            precipitation.setText("--/--");
            pressure.setText("--/--");
            wind.setText("--/--");
            humidity.setText("--/--");
            timeStamp.setText("--/--");
        }
        else if (requestCode == CHOOSE_CITY) {
            if (resultCode == RESULT_OK) {
                String newCity = data.getStringExtra(CityPickerActivity.CITY_PICKER_TAG);
                city.setText(newCity);
                Log.d(TAG, newCity);

                if (!PositionManager.getInstance().positionIsPresent(newCity)) {
                    PositionManager.getInstance().addPosition(newCity);
                }
                PositionManager.getInstance().setCurrentPosition(newCity);
                PositionManager.getInstance().updateCurrent();
            } else {
                //TODO Временная заглушка
                //city.setText(""); // стираем текст
            }
        }
    }
}
