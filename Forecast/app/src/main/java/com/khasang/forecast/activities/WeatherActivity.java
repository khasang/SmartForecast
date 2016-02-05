package com.khasang.forecast.activities;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.ProgressBar;
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
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


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

    private String temp_measure;
    private String press_measure;
    private HourlyForecastFragment hourlyForecastFragment;
    private DailyForecastFragment dailyForecastFragment;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private ProgressBar progressbar;

    private Drawer result = null;
    private boolean opened = false;
    private List<String> favCityList;
    private final int subItemIndex = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_material);
        PositionManager.getInstance().configureManager(this);
        PositionManager.getInstance().updateCurrentLocationCoordinates();
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
        initNavigationDrawer();
    }

    /**
     * Инициализация Navigation Drawer
     *
     * @version beta
     */
    private void initNavigationDrawer() {

        getFavaritesList();

        /** Инициализация элементов меню */
        final DividerDrawerItem divider = new DividerDrawerItem();
        final PrimaryDrawerItem currentPlace = new PrimaryDrawerItem().withName(R.string.drawer_item_current_place).withIcon(Ionicons.Icon.ion_navigate).withIdentifier(0);
        final PrimaryDrawerItem cityList = new PrimaryDrawerItem().withName(R.string.drawer_item_city_list).withIcon(CommunityMaterial.Icon.cmd_city).withIdentifier(1);
        final PrimaryDrawerItem favorites = new PrimaryDrawerItem().withName(R.string.drawer_item_favorites).withIcon(MaterialDesignIconic.Icon.gmi_star).withBadge(String.valueOf(this.favCityList.size())).withIdentifier(2);
        final SecondaryDrawerItem settings = new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3);
        final SecondaryDrawerItem feedBack = new SecondaryDrawerItem().withName(R.string.drawer_item_feedback).withIcon(GoogleMaterial.Icon.gmd_feedback).withIdentifier(4);

        /** Создание Navigation Drawer */
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(-1)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        currentPlace,
                        cityList,
                        favorites,
                        divider,
                        settings,
                        feedBack
                )
                .withGenerateMiniDrawer(true)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        result.updateBadge(2, new StringHolder(String.valueOf(favCityList.size())));
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        result.updateBadge(2, new StringHolder(String.valueOf("")));

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View v, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case 0:
                                changeDisplayedCity("");
                                result.closeDrawer();
                                //TODO add unselect item
                                break;
                            case 1:
                                startCityPickerActivity();
                                result.closeDrawer();
                                //TODO add unselect item
                                break;
                            case 2:
                                if (opened) {
                                    for (int i = WeatherActivity.this.favCityList.size() - 1; i >= 0; i--) {
                                        result.removeItems(subItemIndex + i);
                                    }
                                } else {
                                    int curPos = result.getPosition(drawerItem);
                                    if (!WeatherActivity.this.favCityList.isEmpty()) {
                                        for (int i = WeatherActivity.this.favCityList.size() - 1; i >= 0; i--) {
                                            String city = WeatherActivity.this.favCityList.get(i).split(",")[0];
                                            result.addItemsAtPosition(
                                                    curPos,
                                                    new SecondaryDrawerItem().withLevel(2).withName(city).withIdentifier(subItemIndex + i)
                                            );
                                        }
                                    } else {
                                        Logger.println(TAG, "favCityList is empty");
                                    }
                                }
                                opened = !opened;
                                break;
                            case 3:
//                                Toast.makeText(WeatherActivity.this, "Intent for settings ", Toast.LENGTH_SHORT).show();
                                startSettingsActivity();
                                result.closeDrawer();
                                break;
                            case 4:
                                //TODO add unselect item
                                // FIXME: 31.01.16
                                Intent feedbackIntent = null;
                                switch (Locale.getDefault().getLanguage()) {
                                    case "ru":
                                        feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_form_ru)));
                                        break;
                                    default:
                                        feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_form_en)));
                                        break;
                                }
                                startActivity(feedbackIntent);
                                result.closeDrawer();
                                break;
                            default:
                                String newCity = WeatherActivity.this.favCityList.get(drawerItem.getIdentifier() - subItemIndex);
                                changeDisplayedCity(newCity);
                                result.closeDrawer();
                                break;
                        }
                        return true;
                    }
                })
                .build();
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Запрос на список избранныъ городов из PositionManager
     */
    private void getFavaritesList() {
        favCityList = new ArrayList<>();
        Set<String> cities = PositionManager.getInstance().getPositions();
        for (String city : cities) {
            this.favCityList.add(city);
        }
        Collections.sort(this.favCityList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFavaritesList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = WeatherActivity.this.favCityList.size() - 1; i >= 0; i--) {
            result.removeItems(subItemIndex + i);
        }
        if (opened) opened = !opened;
    }


    @Override
    protected void onStop() {
        super.onStop();
        PositionManager.getInstance().saveSettings();
    }

    private void initStartingMetrics() {
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

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                .toBundle();
        ActivityCompat.startActivity(this, intent, bundle);
    }

    private void initFields() {
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
     * Изменяет отображаемый город WeatherActivity
     */
    public void changeDisplayedCity(String newCity) {
        PositionManager.getInstance().setCurrentPosition(newCity);
//  TODO закомментировал так как текущий пока "текущее местоположение"
//        PositionManager.getInstance().saveCurrPosition();
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
                changeDisplayedCity(newCity);
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
        }, 1000);
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

