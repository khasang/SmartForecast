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
import android.view.Gravity;
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
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

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


    private OnFilterChangedListener onFilterChangedListener;

    public void setOnFilterChangedListener(OnFilterChangedListener onFilterChangedListener) {
        this.onFilterChangedListener = onFilterChangedListener;
    }

    private Drawer result = null;
    private boolean opened = false;
    List<String> cityList;

    private PrimaryDrawerItem favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_material);
        PositionManager.getInstance().initManager(this);
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

    /** Инициализирует Navigation Drawer
     * @version alpha
     * */
    private void initNavigationDrawer() {
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(true)
                .addProfiles(

                        //new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(R.drawable.ic_location_city))
                        new ProfileDrawerItem()
                                .withName("ГОРОД")
                                .withEmail("СТРАНА")
                                .withIcon(getResources().getDrawable(R.drawable.ic_location_city))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //TODO Delete
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(R.string.app_name).withSelectable(false);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withName(R.string.error_empty_location_name);
        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(2);
        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad);
        final SecondaryDrawerItem moscow = new SecondaryDrawerItem().withName("Москва");
        final SecondaryDrawerItem milan = new SecondaryDrawerItem().withName("Милан");
        final SecondaryDrawerItem new_york = new SecondaryDrawerItem().withName("Нью Йорк");
        new SecondaryDrawerItem().withName("Collapsable").withIcon(GoogleMaterial.Icon.gmd_play_for_work).withIdentifier(19).withSelectable(false);


        getFavaritesList();
        //Cписок городов
/*        cityList = new ArrayList<>();
        Set<String> cities = PositionManager.getInstance().getPositions();
        for (String city : cities) {
            cityList.add(city);
            Collections.sort(cityList);
        }*/


        DividerDrawerItem divider = new DividerDrawerItem();
        favorites = new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(MaterialDesignIconic.Icon.gmi_star).withBadge(String.valueOf(cityList.size())).withIdentifier(1);
        PrimaryDrawerItem cityList = new PrimaryDrawerItem().withName(R.string.drawer_item_city_list).withIcon(CommunityMaterial.Icon.cmd_city).withIdentifier(2);
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3);
        SecondaryDrawerItem feedBack = new SecondaryDrawerItem().withName(R.string.drawer_item_feedback).withIcon(GoogleMaterial.Icon.gmd_feedback).withIdentifier(4);




        /*new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(item1,item2)
                .build();*/

        //new Drawer()
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(-1)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        favorites,
                        cityList,
                        divider,
                        settings,
                        feedBack
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View v, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case 1:
                                if (opened) {
                                    for(int i = WeatherActivity.this.cityList.size()-1; i >= 0; i--) {
                                        result.removeItems(1000+i);
                                    }
                                } else {
                                    int curPos = result.getPosition(drawerItem);
                                    if (!WeatherActivity.this.cityList.isEmpty()) {
                                        for(int i = WeatherActivity.this.cityList.size()-1; i >= 0; i--) {
                                            result.addItemsAtPosition(
                                                    curPos,
                                                    new SecondaryDrawerItem().withLevel(2).withName(WeatherActivity.this.cityList.get(i)).withIdentifier(1000+i)
                                            );
                                        }
                                    } else {
                                        Toast.makeText(WeatherActivity.this, "cityList is empty ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                opened = !opened;
                                break;
                            case 2:
                                startCityPickerActivity();
                                result.closeDrawer();
                                break;
                            case 3:
                                result.updateItem(favorites);
                                Toast.makeText(WeatherActivity.this, "Intent for settings ", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Intent feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/1HK_s5Fuzacf0qeB8t2bvHwbo7sJQB_DMesYA6opU_zY/viewform"));
                                startActivity(feedbackIntent);
                                result.closeDrawer();
                                break;
                        }
                        return true;
                    }
                })
                .build();

    }

    public interface OnFilterChangedListener {
        public void onFilterChanged(int filter);
    }

    private void getFavaritesList() {
        //Cписок городов
        cityList = new ArrayList<>();
        Set<String> cities = PositionManager.getInstance().getPositions();
        for (String city : cities) {
            cityList.add(city);
            Collections.sort(cityList);
        }
        //result.updateItem(favorites);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFavaritesList();
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

