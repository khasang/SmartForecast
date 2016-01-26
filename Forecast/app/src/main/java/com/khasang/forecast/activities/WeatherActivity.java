package com.khasang.forecast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.Logger;
import com.khasang.forecast.R;
import com.khasang.forecast.fragments.DailyForecastFragment;
import com.khasang.forecast.fragments.HourlyForecastFragment;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.position.Weather;
import com.khasang.forecast.stations.WeatherStation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Данные которые необходимо отображать в WeatherActivity (для первого релиза):
 * город, температура, давление, влажность, ветер, временная метка.
 */

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final int CHOOSE_CITY = 1;
    private static final String TAG = WeatherActivity.class.getSimpleName();

    private TextView temperature;
    private TextView description;
    //    private TextView pressure;
    private TextView wind;
    private TextView humidity;
    private ImageView currWeather;
    private ImageView syncBtn;

    private Animation animationRotateCenter;
    private Animation animationGrow;
    //    private Animation animScale;
//    private Animation fallingDown;
//    private Animation fallingDown_plus1;
//    private Animation fallingDownAlpha;
//    private Animation fallingDownAlpha_plus1;
//    private Animation flip;
//    private Animation animTrans;
//    private Animation animTrans_plus1;
//    private Animation fallingUp;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private String temp_measure;
    private String press_measure;
    private HourlyForecastFragment hourlyForecastFragment;
    private DailyForecastFragment dailyForecastFragment;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_material);
        PositionManager.getInstance().configureManager(this);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            hourlyForecastFragment = new HourlyForecastFragment();
            dailyForecastFragment = new DailyForecastFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, hourlyForecastFragment)
                    .add(R.id.fragment_container, dailyForecastFragment)
                    .hide(dailyForecastFragment)
                    .commit();
        }
        initStartingMetrics();
        initFields();
        initFirstAppearance();
        setAnimationForWidgets();
        startAnimations();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PositionManager.getInstance().saveSettings();
    }

    private void initStartingMetrics() {
//        switch (PositionManager.getInstance().getTemperatureMetric()) {
//            case FAHRENHEIT:
//                temp_measure = getString(R.string.FAHRENHEIT);
//                break;
//            case KELVIN:
//                temp_measure = getString(R.string.KELVIN);
//                break;
//            case CELSIUS:
//            default:
//                temp_measure = getString(R.string.CELSIUS);
//        }
        switch (PositionManager.getInstance().getPressureMetric()) {
            case MM_HG:
                press_measure = getString(R.string.pressure_measure);
                break;
            case HPA:
            default:
                press_measure = getString(R.string.pressure_measure_hpa);
        }
    }

    private void switchDisplayMode() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (dailyForecastFragment.isHidden()) {
            ft.show(dailyForecastFragment)
                    .hide(hourlyForecastFragment)
                    .commit();
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_by_hour));
        } else {
            ft.show(hourlyForecastFragment)
                    .hide(dailyForecastFragment)
                    .commit();
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_by_day));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_weather, menu);
        final MenuItem item = menu.findItem(R.id.menu_item_refresh);
        item.setActionView(R.layout.iv_action_refresh);
        syncBtn = (ImageView) item.getActionView().findViewById(R.id.refreshButton);
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_change_location:
//                startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
                startCityPickerActivity();
                return true;
            case R.id.menu_item_refresh:
                startAnimation();
                onRefresh();
                return true;
        }
        return false;
    }

    private void startCityPickerActivity() {
        Intent intent = new Intent(this, CityPickerActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                    .toBundle();
        ActivityCompat.startActivityForResult(this, intent, CHOOSE_CITY, bundle);
    }

    private void initFields() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_material);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        currWeather = (ImageView) findViewById(R.id.iv_curr_weather);
        temperature = (TextView) findViewById(R.id.temperature);
        description = (TextView) findViewById(R.id.precipitation);
//        pressure = (TextView) findViewById(R.id.pressure);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);

        /** Слушатели нажатий объектов */
        fab.setOnClickListener(this);
        temperature.setOnClickListener(this);
//        pressure.setOnClickListener(this);

//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
//        swipeRefreshLayout.setOnRefreshListener(this);
//
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
        setSupportActionBar(toolbar);
    }

    private void startAnimation() {
        syncBtn.startAnimation(animationRotateCenter);
//        wind.startAnimation(animTrans);
//        humidity.startAnimation(animTrans);
    }

    private void setAnimationForWidgets() {
        /** Анимация объектов */
        animationRotateCenter = AnimationUtils.loadAnimation(this, R.anim.rotate_center);
        animationGrow = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
//        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
//        fallingDown = AnimationUtils.loadAnimation(this, R.anim.falling_down);
//        fallingDown_plus1 = AnimationUtils.loadAnimation(this, R.anim.falling_down_plus1);
//        fallingDownAlpha = AnimationUtils.loadAnimation(this, R.anim.falling_down_alpha);
//        fallingDownAlpha_plus1 = AnimationUtils.loadAnimation(this, R.anim.falling_down_alpha_plus1);
//        flip = AnimationUtils.loadAnimation(this, R.anim.flip);
//        animTrans = AnimationUtils.loadAnimation(this, R.anim.translate);
//        animTrans_plus1 = AnimationUtils.loadAnimation(this, R.anim.translate_plus1);
//        fallingUp = AnimationUtils.loadAnimation(this, R.anim.falling_up);
//
//        getSupportFragmentManager().beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
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
                HashMap.Entry<Calendar, Weather> firstEntry = forecast.entrySet().iterator().next();
                updateCurrentWeather(firstEntry.getKey(), firstEntry.getValue());
                break;
            case HOURLY:
                Logger.println(TAG, "Принят HOURLY прогноз");
                hourlyForecastFragment.setDatasAndAnimate(forecast);
                break;
            case DAILY:
                Logger.println(TAG, "Принят DAILY прогноз");
                dailyForecastFragment.setDatasAndAnimate(forecast);
                break;
            default:
                Logger.println(TAG, "Принят необрабатываемый прогноз");
        }
    }    /**
     * Обработчик нажатия объектов
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                switchDisplayMode();
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

    public void updateCurrentWeather(Calendar date, Weather wCurent) {

        if (wCurent == null) {
            Log.i(TAG, "Weather is null!");
            return;
        }

        /** Получаем текущее время */
        int hours = date.get(Calendar.HOUR_OF_DAY);
        int minutes = date.get(Calendar.MINUTE);

        //temperature.setText(String.format("%.0f°C", wCurent.getTemperature()));
        toolbar.setTitle(PositionManager.getInstance().getCurrentPositionName().split(",")[0]);
        temperature.setText(String.format("%.0f%s", wCurent.getTemperature(),
                PositionManager.getInstance().getTemperatureMetric().toStringValue()));
        description.setText(String.format("%s", wCurent.getDescription()
                .substring(0, 1)
                .toUpperCase() + wCurent
                .getDescription()
                .substring(1)));
        int iconId = wCurent.getPrecipitation().getIconResId(
                AppUtils.isDayFromString(
                        String.format(Locale.getDefault(), "%tR", date)));
        currWeather.setImageResource(iconId == 0 ? R.mipmap.ic_launcher : iconId);
//
//        pressure.setText(String.format("%s %.0f %s",
//                getString(R.string.pressure),
//                wCurent.getPressure(),
//                getString(R.string.pressure_measure)));
//
        wind.setText(Html.fromHtml(String.format("%s %.0f%s",
                wCurent.getWindDirection().getDirectionString(),
                wCurent.getWindPower(),
                getString(R.string.wind_measure))));

        humidity.setText(String.format("%s%%",
                wCurent.getHumidity()));
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
//                city.setText(newCity.split(",")[0]);
                toolbar.setTitle(newCity.split(",")[0]);
                Logger.println(TAG, newCity);
                PositionManager.getInstance().setCurrentPosition(newCity);
                PositionManager.getInstance().saveCurrPosition();
                onRefresh();
//                syncBtn.setVisibility(View.VISIBLE);
            } else {
                if (!PositionManager.getInstance().positionIsPresent(PositionManager.getInstance().getCurrentPositionName())) {
                    stopRefresh();
//                    syncBtn.setVisibility(View.GONE);
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
//            stopRefresh();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.println(TAG, "Start animation");
                PositionManager.getInstance().updateWeather();
            }
        }, 1000);
//        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * Проигрываение анимации всех объектов activity
     */
    private void startAnimations() {
        fab.startAnimation(animationGrow);
//        city.startAnimation(fallingDown);
//        cityPickerBtn.startAnimation(fallingDown_plus1);
//        temperature.startAnimation(flip);
//        description.startAnimation(fallingDownAlpha);
//        pressure.startAnimation(fallingDownAlpha_plus1);
//        //llMainInformation.setAnimation(flip);
//        wind.startAnimation(animTrans);
//        humidity.startAnimation(animTrans_plus1);
//        timeStamp.startAnimation(fallingUp);
//        syncBtn.startAnimation(animationRotateCenter);
    }

    /**
     * Останавливаем анимацию
     */
    public void stopRefresh() {
//        swipeRefreshLayout.setRefreshing(false);
//        syncBtn.clearAnimation();
    }


}

