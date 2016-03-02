package com.khasang.forecast.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.Logger;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.activities.etc.NavigationDrawer;
import com.khasang.forecast.fragments.DailyForecastFragment;
import com.khasang.forecast.fragments.HourlyForecastFragment;
import com.khasang.forecast.position.Position;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.position.Weather;
import com.khasang.forecast.stations.WeatherStation;
import com.squareup.leakcanary.RefWatcher;

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
    private TextView wind;
    private TextView humidity;
    private ImageView currWeather;
    private ImageView syncBtn;

    private Animation animationRotateCenter;
    private Animation animationGrow;

    private HourlyForecastFragment hourlyForecastFragment;
    private DailyForecastFragment dailyForecastFragment;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private ProgressBar progressbar;
    private SharedPreferences sp;

    //private NavigationDrawer drawer = new NavigationDrawer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_material);
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
        //PositionManager.getInstance().initManager();
        initFields();
        setAnimationForWidgets();
        startAnimations();
        //initNavigationDrawer();
        initFirstAppearance();

        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(PositionManager.getInstance());
    }

/*    private void initNavigationDrawer() {
        drawer.init(this, toolbar);
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(drawer);
    }*/

    @Override
    public void onBackPressed() {
        //if (drawer.isDrawerOpened()) {
        //} else {
            super.onBackPressed();
        //}
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PositionManager.getInstance().configureActivityFeedback(this);
        PositionManager.getInstance().updateWeatherFromDB();
        Logger.println(TAG, "OnResume");
        //drawer.updateBadges();
        PositionManager.getInstance().setUseGpsModule(sp.getBoolean(getString(R.string.pref_gps_key), true));
        if (sp.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric)).equals(getString(R.string.pref_units_metric))) {
            PositionManager.getInstance().setTemperatureMetric(AppUtils.TemperatureMetrics.CELSIUS);
            PositionManager.getInstance().setSpeedMetric(AppUtils.SpeedMetrics.METER_PER_SECOND);
        } else if (sp.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric)).equals(getString(R.string.pref_units_imperial))) {
            PositionManager.getInstance().setTemperatureMetric(AppUtils.TemperatureMetrics.FAHRENHEIT);
            PositionManager.getInstance().setSpeedMetric(AppUtils.SpeedMetrics.MILES_PER_HOURS);
        }
        onRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PositionManager.getInstance().configureActivityFeedback(null);
        //drawer.closeSubItems();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PositionManager.getInstance().saveSettings();
        PositionManager.getInstance().removeInstance();
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

    public boolean isHourlyForecastActive() {
        return dailyForecastFragment.isHidden();
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
            case R.id.menu_item_refresh:
                startAnimation();
                onRefresh();
                return true;
        }
        return false;
    }

    public void startCityPickerActivity() {
        Intent intent = new Intent(this, CityPickerActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                .toBundle();
        ActivityCompat.startActivityForResult(this, intent, CHOOSE_CITY, bundle);
    }

    public void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                .toBundle();
        ActivityCompat.startActivity(this, intent, bundle);
    }

    private void initFields() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_material);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        currWeather = (ImageView) findViewById(R.id.iv_curr_weather);
        temperature = (TextView) findViewById(R.id.temperature);
        description = (TextView) findViewById(R.id.precipitation);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressbar.setIndeterminate(true);

        /** Слушатели нажатий объектов */
        fab.setOnClickListener(this);
        temperature.setOnClickListener(this);
        setSupportActionBar(toolbar);
    }

    public void showProgress(boolean loading) {
        progressbar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void startAnimation() {
        syncBtn.startAnimation(animationRotateCenter);
    }

    private void setAnimationForWidgets() {
        /** Анимация объектов */
        animationRotateCenter = AnimationUtils.loadAnimation(this, R.anim.rotate_center);
        animationGrow = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
    }

    private void initFirstAppearance() {
        temperature.setText("--/--");
/*      if (PositionManager.getInstance().getPositions().size() == 0) {
            startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
        } else if (!PositionManager.getInstance().positionIsPresent(PositionManager.getInstance().getCurrentPositionName())) {
            Toast.makeText(this, R.string.msg_choose_city, Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(this, CityPickerActivity.class), CHOOSE_CITY);
        } else { }        */
    }

    /**
     * Обновление интерфейса Activity при получении новых данных
     */

    public void updateInterface(WeatherStation.ResponseType responseType, Map<Calendar, Weather> forecast) {
        stopRefresh();
        toolbar.setTitle(PositionManager.getInstance().getCurrentPositionName().split(",")[0]);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgress(false);
            }
        }, 1250);
    }


    /**
     * Обработчик нажатия объектов
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                switchDisplayMode();
                break;
            case R.id.temperature:
                PositionManager.getInstance().changeTemperatureMetric();
                PositionManager.getInstance().updateWeatherFromDB();
                break;
            case R.id.pressure:
                PositionManager.getInstance().changePressureMetric();
                PositionManager.getInstance().updateWeatherFromDB();
                break;
        }
    }

    public void updateCurrentWeather(Calendar date, Weather wCurent) {

        if (wCurent == null) {
            Log.i(TAG, "Weather is null!");
            return;
        }

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

        wind.setText(Html.fromHtml(String.format("%s %.0f%s",
                wCurent.getWindDirection().getDirectionString(),
                wCurent.getWindPower(),
                PositionManager.getInstance().getSpeedMetric().toStringValue())));

        humidity.setText(String.format("%s%%",
                wCurent.getHumidity()));
    }

    /**
     * Изменяет отображаемый город WeatherActivity
     */
    public void changeDisplayedCity(String newCity) {
        PositionManager.getInstance().setCurrentPosition(newCity);
        onRefresh();
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
                toolbar.setTitle(newCity.split(",")[0]);
                Logger.println(TAG, newCity);
                PositionManager.getInstance().setCurrentPosition(newCity);
            } else {
                if (!PositionManager.getInstance().positionIsPresent(PositionManager.getInstance().getCurrentPositionName())) {
                    stopRefresh();
                    showProgress(false);
                }
            }
        }
    }

    /**
     * Анимация обновления
     */
    @Override
    public void onRefresh() {
        showProgress(true);
        if (!PositionManager.getInstance().positionIsPresent(PositionManager.getInstance().getCurrentPositionName())) {
            Logger.println(TAG, "There is nothing to refresh");
            Toast.makeText(WeatherActivity.this, R.string.msg_no_city, Toast.LENGTH_SHORT).show();
            showProgress(false);
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.println(TAG, "Start animation");
                PositionManager.getInstance().updateWeather();
            }
        }, 500);
    }

    /**
     * Проигрываение анимации всех объектов activity
     */
    private void startAnimations() {
        fab.startAnimation(animationGrow);
    }

    //TODO DELETE

    /**
     * Останавливаем анимацию
     */
    public void stopRefresh() {
    }


}

