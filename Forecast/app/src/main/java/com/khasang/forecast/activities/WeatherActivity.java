package com.khasang.forecast.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.LockableViewPager;
import com.khasang.forecast.Logger;
import com.khasang.forecast.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.Weather;
import com.khasang.forecast.WeatherStation;
import com.khasang.forecast.adapters.ForecastPageAdapter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Данные которые необходимо отображать в WeatherActivity (для первого релиза):
 * город, температура, давление, влажность, ветер, временная метка.
 */

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = this.getClass().getSimpleName();

    /**
     * ViewPager для отображения нижних вкладок прогноза: по часам и по дням
     */
    private TabLayout tabLayout;
    private LockableViewPager pager;

    private TextView city;
    private TextView temperature;
    private TextView description;
    private TextView pressure;
    private TextView wind;
    private TextView humidity;
    private TextView timeStamp;
    private ImageButton syncBtn;
    private ImageButton cityPickerBtn;

    private LinearLayout llMainInformation;

    private Animation animationRotateCenter;
    private Animation animScale;
    private Animation fallingDown;
    private Animation fallingDown_plus1;
    private Animation fallingDownAlpha;
    private Animation fallingDownAlpha_plus1;
    private Animation flip;
    private Animation animTrans;
    private Animation animTrans_plus1;
    private Animation fallingUp;

    private final int CHOOSE_CITY = 1;
    public Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String temp_measure;
    private String press_measure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        context = getApplicationContext();
        PositionManager.getInstance().initManager(this);

        initStartingMetrics();
        initFields();
        setAnimationForWidgets();
        startAnimation();

        initFirstAppearance();
    }

    private void initStartingMetrics() {
        switch (PositionManager.getInstance().getTemperatureMetric()) {
            case FAHRENHEIT:
                temp_measure = getString(R.string.FAHRENHEIT);
                break;
            case KELVIN:
                temp_measure = getString(R.string.KELVIN);
                break;
            case CELSIUS:
            default:
                temp_measure = getString(R.string.CELSIUS);
        }
        switch (PositionManager.getInstance().getPressureMetric()) {
            case MM_HG:
                press_measure = getString(R.string.pressure_measure);
                break;
            case HPA:
            default:
                press_measure = getString(R.string.pressure_measure_hpa);
        }
    }

    private void initFields() {
        city = (TextView) findViewById(R.id.city);
        cityPickerBtn = (ImageButton) findViewById(R.id.cityPickerBnt);
        temperature = (TextView) findViewById(R.id.temperature);
        description = (TextView) findViewById(R.id.precipitation);
        pressure = (TextView) findViewById(R.id.pressure);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        timeStamp = (TextView) findViewById(R.id.timeStamp);
        syncBtn = (ImageButton) findViewById(R.id.syncBtn);
        llMainInformation = (LinearLayout) findViewById(R.id.llMainInformation);

        /** Слушатели нажатий объектов */
        syncBtn.setOnClickListener(this);
        city.setOnClickListener(this);
        cityPickerBtn.setOnClickListener(this);
        temperature.setOnClickListener(this);
        pressure.setOnClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        pager = (LockableViewPager) findViewById(R.id.pager);
        ForecastPageAdapter adapter = new ForecastPageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.my_holo_alpha));
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_by_hour_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_by_date_24);
        pager.setSwipeable(false);
        pager.addOnPageChangeListener(adapter);
    }

    private void startAnimation() {
        syncBtn.startAnimation(animationRotateCenter);
        wind.startAnimation(animTrans);
        humidity.startAnimation(animTrans);
    }

    private void setAnimationForWidgets() {
        /** Анимация объектов */
        animationRotateCenter = AnimationUtils.loadAnimation(this, R.anim.rotate_center);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        fallingDown = AnimationUtils.loadAnimation(this, R.anim.falling_down);
        fallingDown_plus1 = AnimationUtils.loadAnimation(this, R.anim.falling_down_plus1);
        fallingDownAlpha = AnimationUtils.loadAnimation(this, R.anim.falling_down_alpha);
        fallingDownAlpha_plus1 = AnimationUtils.loadAnimation(this, R.anim.falling_down_alpha_plus1);
        flip = AnimationUtils.loadAnimation(this, R.anim.flip);
        animTrans = AnimationUtils.loadAnimation(this, R.anim.translate);
        animTrans_plus1 = AnimationUtils.loadAnimation(this, R.anim.translate_plus1);
        fallingUp = AnimationUtils.loadAnimation(this, R.anim.falling_up);

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void initFirstAppearance() {
        temperature.setText("--/--");
        if (PositionManager.getInstance().getPositions().size() == 0) {
            startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
        } else if (!PositionManager.getInstance().positionIsPresent(PositionManager.getInstance().getCurrentPositionName())) {
            Toast.makeText(this, R.string.msg_choose_city, Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
        } else {
            if (!PositionManager.getInstance().getCurrentPositionName().isEmpty()) {
                onRefresh();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PositionManager.getInstance().saveSettings();
    }

    /**
     * Обработчик нажатия объектов
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.syncBtn:
                onRefresh();
                break;
            case R.id.city:
                startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
                break;
            case R.id.cityPickerBnt:
                startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
                break;
            case R.id.temperature:
                switch (PositionManager.getInstance().changeTemperatureMetric()) {
                    case FAHRENHEIT:
                        temp_measure = getString(R.string.FAHRENHEIT);
                        break;
                    case KELVIN:
                        temp_measure = getString(R.string.KELVIN);
                        break;
                    case CELSIUS:
                    default:
                        temp_measure = getString(R.string.CELSIUS);
                }
                PositionManager.getInstance().updateWeather();
                break;
            case R.id.pressure:
                switch (PositionManager.getInstance().changePressureMetric()) {
                    case MM_HG:
                        press_measure = getString(R.string.pressure_measure);
                        break;
                    case HPA:
                    default:
                        press_measure = getString(R.string.pressure_measure_hpa);
                }
                PositionManager.getInstance().updateWeather();
                break;
        }
    }

    /**
     * Обновление интерфейса Activity при получении новых данных
     */
    public void updateInterface(WeatherStation.ResponseType responseType, Map<Calendar, Weather> forecast) {
        stopRefresh();

        if (forecast == null || forecast.size() == 0) {
            Logger.println(TAG, "Weather is null!");

            return;
        }
        switch (responseType) {
            case CURRENT:
                Logger.println(TAG, "Принят CURRENT прогноз");
                HashMap.Entry<Calendar, Weather> firstEntry = (Map.Entry<Calendar, Weather>) forecast.entrySet().iterator().next();
                updateCurrentWeather(firstEntry.getKey(), firstEntry.getValue());
                break;
            case HOURLY:
                Logger.println(TAG, "Принят HOURLY прогноз");
                ((ForecastPageAdapter) pager.getAdapter()).setHourForecast(forecast);
                break;
            case DAILY:
                Logger.println(TAG, "Принят DAILY прогноз");
                ((ForecastPageAdapter) pager.getAdapter()).setDayForecast(forecast);
                break;
            default:
                Logger.println(TAG, "Принят необрабатываемый прогноз");
        }
    }

    public void updateCurrentWeather(Calendar date, Weather wCurent) {

        if (wCurent == null) {
            Log.i(TAG, "Weather is null!");
            return;
        }

        /** Получаем текущее время */
        int hours = date.get(Calendar.HOUR_OF_DAY);
        int minutes = date.get(Calendar.MINUTE);

        city.setText(PositionManager.getInstance().getCurrentPositionName().split(",")[0]); // отображаем имя текущей локации
        //temperature.setText(String.format("%.0f°C", wCurent.getTemperature()));
        temperature.setText(String.format("%.0f%s", wCurent.getTemperature(), temp_measure));

        description.setText(String.format("%s", wCurent.getDescription()
                .substring(0, 1)
                .toUpperCase() + wCurent
                .getDescription()
                .substring(1)));

        pressure.setText(String.format("%s %.0f %s",
                getString(R.string.pressure),
                wCurent.getPressure(),
                press_measure));

        wind.setText(Html.fromHtml(String.format("%s %s %.0f%s",
                getString(R.string.wind),
                wCurent.getWindDirection().getDirectionString(),
                wCurent.getWindPower(),
                getString(R.string.wind_measure))));

        humidity.setText(String.format("%s %s%%",
                getString(R.string.humidity),
                wCurent.getHumidity()));

        timeStamp.setText(String.format("%s %d:%02d",
                getString(R.string.timeStamp),
                hours,
                minutes));

    }

    /**
     * Получаем город из CityPickActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_CITY) {
            if (resultCode == RESULT_OK) {
                String newCity = data.getStringExtra(CityPickerActivity.CITY_PICKER_TAG);
                city.setText(newCity.split(",")[0]);
                Logger.println(TAG, newCity);
                PositionManager.getInstance().setCurrentPosition(newCity);
                PositionManager.getInstance().saveCurrPosition();
                onRefresh();
                syncBtn.setVisibility(View.VISIBLE);
            } else {
                if (!PositionManager.getInstance().positionIsPresent(PositionManager.getInstance().getCurrentPositionName())) {
                    stopRefresh();
                    syncBtn.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Анимация обновления
     */
    @Override
    public void onRefresh() {
        if (!PositionManager.getInstance().positionIsPresent(PositionManager.getInstance().getCurrentPositionName())) {
            Logger.println(TAG, "There is nothing to refresh");
            Toast.makeText(WeatherActivity.this, R.string.msg_no_city, Toast.LENGTH_SHORT).show();
            stopRefresh();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.println(TAG, "Start animation");
                allAnimation();
                PositionManager.getInstance().updateWeather();
            }
        }, 1000);
        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * Проигрываение анимации всех объектов activity
     */
    private void allAnimation() {
        city.startAnimation(fallingDown);
        cityPickerBtn.startAnimation(fallingDown_plus1);
        temperature.startAnimation(flip);
        description.startAnimation(fallingDownAlpha);
        pressure.startAnimation(fallingDownAlpha_plus1);
        //llMainInformation.setAnimation(flip);
        wind.startAnimation(animTrans);
        humidity.startAnimation(animTrans_plus1);
        timeStamp.startAnimation(fallingUp);
        syncBtn.startAnimation(animationRotateCenter);
    }

    /**
     * Останавливаем анимацию
     */
    public void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        //syncBtn.clearAnimation();
    }

}

