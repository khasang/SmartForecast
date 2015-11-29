package com.khasang.forecast.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.OpenWeatherMap;
import com.khasang.forecast.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.adapters.ForecastPageAdapter;

import java.util.ArrayList;

/**
 * Данные которые необходимо отображать в WeatherActivity (для первого релиза):
 * город, температура, давление, влажность, ветер, временная метка.
 */

public class WeatherActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = WeatherActivity.class.getSimpleName();

    /**
     * ViewPager для отображения нижних вкладок прогноза: по часам и по дням
     */
    private ViewPager pager;

    private TextView city;
    private TextView temperature;
    private TextView precipitation;
    private TextView pressure;
    private TextView wind;
    private TextView humidity;
    private TextView timeStamp;
    private ImageButton syncBtn;

    private PositionManager manager;

    // Для тестирования
    private String current_city = "Moscow";
    private int current_temperature = 1;
    //private Precipitation current_precipitation;
    private String current_precipitation = "Солнечно";
    private int current_pressure = 40;
    private int current_wind = 25;
    private int current_humidity = 12;
    private String current_timeStamp = "10:15";
    private OpenWeatherMap owm;
    private String positionName;
    private ArrayList<String> positions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

//        manager = new PositionManager(this);
        owm = new OpenWeatherMap();


        positions = new ArrayList<>();

        city = (TextView) findViewById(R.id.city);
        temperature = (TextView) findViewById(R.id.temperature);
        precipitation = (TextView) findViewById(R.id.precipitation);
        pressure = (TextView) findViewById(R.id.pressure);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        timeStamp = (TextView) findViewById(R.id.timeStamp);
        syncBtn = (ImageButton) findViewById(R.id.syncBtn);

        syncBtn.setOnClickListener(this);


        /**
         * Код для фрагментов
         */
        pager = (ViewPager) findViewById(R.id.pager);
        ForecastPageAdapter adapter = new ForecastPageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
    }

    /**
     * Обработчик нажатия кнопки
     */
    @Override
    public void onClick(View view){
        if (manager.getCurrPosition() == null) {
            showChooseCityDialog();
        }
        updateInterface(current_city, current_temperature, current_precipitation,
                current_pressure, current_wind, current_humidity, current_timeStamp);
//            manager.updateCurrent();


    }

    /**
     * Обновление интерфейса Activity
     */
    public void updateInterface(String current_city, int current_temperature,
                                String current_precipitation, int current_presure,
                                int current_wind, int current_humidity, String current_timeStamp) {
        city.setText(String.valueOf(current_city));
        temperature.setText(String.valueOf(current_temperature) + "°C");
        precipitation.setText(String.valueOf(current_precipitation));
//        pressure.setText(getString(R.string.pressure) + " " + String.valueOf(current_presure) + getString(R.string.pressure_measure));
        pressure.setText(String.format("%s %d %s",
                getString(R.string.pressure),
                current_presure,
                getString(R.string.pressure_measure)));
        wind.setText(getString(R.string.wind) + " " + String.valueOf(current_wind) + getString(R.string.wind_measure));
        humidity.setText(getString(R.string.humidity) + " " + String.valueOf(current_humidity) + "%");
        timeStamp.setText(getString(R.string.timeStamp) + " " + String.valueOf(current_timeStamp));
    }

    private void showChooseCityDialog() {
        final View view = getLayoutInflater().inflate(R.layout.choose_city_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText chooseCity = (EditText) view.findViewById(R.id.editTextCityName);
        builder.setTitle("Введите название города")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        positionName = chooseCity.getText().toString();
                        positions.add(positionName);
                        manager.initPositions(positions);
                        manager.setCurrPosition(positionName);
                        try {
                            owm.updateWeather(manager.getPosition(positionName).getCoordinate(), manager);
                            Log.i(TAG, "Coordinates: " + manager.getPosition(positionName).getCoordinate().getLatitude() + ", " + manager.getPosition(positionName).getCoordinate().getLongitude());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(WeatherActivity.this, "Вы ввели некорректный адрес, повторите попытку", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
