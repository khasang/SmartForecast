package com.khasang.forecast.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.InviteEvent;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.khasang.forecast.R;
import com.khasang.forecast.activities.etc.NavigationDrawer;
import com.khasang.forecast.behaviors.FabOnTopBehavior;
import com.khasang.forecast.chart.WeatherChart;
import com.khasang.forecast.fragments.DailyForecastFragment;
import com.khasang.forecast.fragments.HourlyForecastFragment;
import com.khasang.forecast.interfaces.IMessageProvider;
import com.khasang.forecast.interfaces.IWeatherReceiver;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.position.Weather;
import com.khasang.forecast.stations.WeatherStation;
import com.khasang.forecast.utils.AppUtils;
import com.khasang.forecast.utils.Logger;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import icepick.State;
import io.fabric.sdk.android.Fabric;

import static android.support.design.R.id.snackbar_text;

/**
 * Данные которые необходимо отображать в WeatherActivity (для первого релиза):
 * город, температура, давление, влажность, ветер, временная метка.
 */
public class WeatherActivity extends BaseActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, IWeatherReceiver,
        IMessageProvider, NavigationDrawer.OnNavigationItemClickListener, GoogleApiClient
                .OnConnectionFailedListener {

    public static final String ACTIVE_CITY_TAG = "ACTIVE_CITY";
    public static final String CHECK_PERMISSION_TAG = "CHECK_PERMISSION";
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 87;
    public static final int PERMISSION_REQUEST_SETTINGS_CODE = 88;
    private static final String TAG = WeatherActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;
    private static final int CHOOSE_CITY = 1;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.temperature_text)
    TextView temperatureView;
    @BindView(R.id.precipitation)
    TextView precipitationView;
    @BindView(R.id.wind_text)
    TextView windView;
    @BindView(R.id.humidity_text)
    TextView humidityView;
    @BindView(R.id.pressure_text)
    TextView pressureView;
    @BindView(R.id.current_weather_icon)
    ImageView currentWeatherView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.chart)
    WeatherChart chart;
    @BindView(R.id.chart_layout)
    FrameLayout chartLayout;

    @State
    String activeCity;

    private ImageView syncBtn;
    private Animation animationRotateCenter;
    private Animation animationGrow;
    private HourlyForecastFragment hourlyForecastFragment;
    private DailyForecastFragment dailyForecastFragment;
    private NavigationDrawer navigationDrawer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        int drawerHeaderArrayIndex = 0;
        switch (themeResId) {
            case R.style.AppTheme_Brown:
                drawerHeaderArrayIndex = 1;
                break;
            case R.style.AppTheme_Teal:
                drawerHeaderArrayIndex = 2;
                break;
            case R.style.AppTheme_Indigo:
                drawerHeaderArrayIndex = 3;
                break;
            case R.style.AppTheme_Purple:
                drawerHeaderArrayIndex = 4;
                break;
        }

        if (getIntent().getBooleanExtra(CHECK_PERMISSION_TAG, true)) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                checkLocationPermission();
            }
        }

        PositionManager.getInstance(this, this);
        PositionManager.getInstance().generateIconSet(this);
        setActivePosition();
        init();
        initNavigationDrawer(drawerHeaderArrayIndex);
        setAnimationForWidgets();
        startAnimations();
        initAppInvite();

        hourlyForecastFragment = new HourlyForecastFragment();
        dailyForecastFragment = new DailyForecastFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, hourlyForecastFragment)
                .add(R.id.fragment_container, dailyForecastFragment)
                .hide(dailyForecastFragment)
                .commit();
        onRefresh();
    }

    private void initAppInvite() {
        // Create an auto-managed GoogleApiClient with access to App Invites.
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        }
                );
    }

    private void setActivePosition() {
        String intentActiveCity = getIntent().getStringExtra(ACTIVE_CITY_TAG);
        if (intentActiveCity != null) {
            activeCity = intentActiveCity;
        }
        if (activeCity != null) {
            PositionManager.getInstance().setActivePosition(activeCity);
            setCityInToolbar(activeCity);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
        showSnackbar(R.string.google_play_services_error, Snackbar.LENGTH_SHORT);
    }

    private void init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressbar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.accent),
                    PorterDuff.Mode.SRC_ATOP);
        }

        /** Слушатели нажатий объектов */
        IconicsDrawable iconCalendar = new IconicsDrawable(this)
                .color(ContextCompat.getColor(this, R.color.current_weather_color))
                .icon(Octicons.Icon.oct_calendar);
        fab.setImageDrawable(iconCalendar);
        fab.setOnClickListener(this);

        /** Behavior для FAB */
        CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        int maxChartHeight = (int) getResources().getDimension(R.dimen.chart_height);
        fabLayoutParams.setBehavior(new FabOnTopBehavior(chartLayout, maxChartHeight));

//        temperatureView.setOnClickListener(this);

        setSupportActionBar(toolbar);
    }

    private void initNavigationDrawer(int drawerHeaderArrayIndex) {
        navigationDrawer = new NavigationDrawer(this, toolbar, drawerHeaderArrayIndex);
        navigationDrawer.setNavigationItemClickListener(this);
    }

    @Override
    public void onNavigationItemClicked(int identifier) {
        switch (identifier) {
            case NavigationDrawer.NAVIGATION_CURRENT_PLACE:
                changeDisplayedCity("");
                break;
            case NavigationDrawer.NAVIGATION_CITY_LIST:
                launchCityPickerActivity();
                break;
            case NavigationDrawer.NAVIGATION_FAVORITES:
                break;
            case NavigationDrawer.NAVIGATION_SETTINGS:
                launchSettingsActivity();
                break;
            case NavigationDrawer.NAVIGATION_ABOUT:
                launchAboutActivity();
                break;
            case NavigationDrawer.NAVIGATION_INVITE:
                onInviteClicked();
                break;
            case NavigationDrawer.NAVIGATION_APP_NAME:
                break;
            default:
                String newCity = PositionManager.getInstance()
                        .getFavouritesList()
                        .get(identifier - NavigationDrawer.SUB_ITEMS_BASE_INDEX);
                changeDisplayedCity(newCity);
        }
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void setAnimationForWidgets() {
        /** Анимация объектов */
        animationRotateCenter = AnimationUtils.loadAnimation(this, R.anim.rotate_center);
        animationGrow = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
    }

    /**
     * Проигрываение анимации всех объектов activity
     */
    private void startAnimations() {
        fab.startAnimation(animationGrow);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PositionManager positionManager = PositionManager.getInstance();
                positionManager.setReceiver(this);
                positionManager.setMessageProvider(this);
                positionManager.initLocationManager();
                positionManager.updateWeatherFromDB();
                positionManager.updateWeather();
                navigationDrawer.updateBadges(true);
            } else {
                navigationDrawer.updateBadges(false);
                showProgress(false);
            }
        }
    }

    /**
     * Получаем город из CityPickerActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        PositionManager.getInstance().setMessageProvider(this);
        PositionManager.getInstance().setReceiver(this);
        switch (requestCode) {
            case PERMISSION_REQUEST_SETTINGS_CODE:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    navigationDrawer.updateBadges(true);
                } else {
                    navigationDrawer.updateBadges(false);
                    showPermissionSnack();
                }
                break;
            case CHOOSE_CITY:
                PositionManager pm = PositionManager.getInstance();
                if (resultCode == RESULT_OK) {
                    String newCity = data.getStringExtra(CityPickerActivity.CITY_PICKER_TAG);
                    Logger.println(TAG, newCity);
                    setCityInToolbar(newCity);
                    pm.setActivePosition(newCity);
                    onRefresh();
                } else if (!pm.positionIsPresent(pm.getActivePositionCity())) {
                    showProgress(false);
                }
                break;
            case REQUEST_INVITE:
                if (resultCode == RESULT_OK) {
                    // Get the invitation IDs of all sent messages
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    int invitesCount = 0;
                    for (String id : ids) {
                        Log.d(TAG, "onActivityResult: sent invitation " + id);
                        invitesCount++;
                    }
                    if (Fabric.isInitialized()) {
                        Answers.getInstance().logInvite(new InviteEvent().putMethod("App Invites")
                                .putCustomAttribute("Invite friends", "Send to " + String.valueOf(invitesCount) + " " +
                                        "users"));
                    }

                    // Сохранение общего количества отправленных приглашений
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                    int totalInvites = sp.getInt(getString(R.string.total_app_invites), 0);
                    totalInvites += invitesCount;
                    sp.edit().putInt(getString(R.string.total_app_invites), totalInvites).apply();
                } else {
                    // Sending failed or it was canceled, show failure message to the user
                    if (Fabric.isInitialized()) {
                        Answers.getInstance().logInvite(new InviteEvent().putMethod("App Invites")
                                .putCustomAttribute("Invite friends", "Canceled"));
                    }
                }
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void launchCityPickerActivity() {
        Intent intent = new Intent(this, CityPickerActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
        ActivityCompat.startActivityForResult(this, intent, CHOOSE_CITY, bundle);
    }

    @SuppressWarnings("unchecked")
    public void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent, bundle);
    }

    @SuppressWarnings("unchecked")
    public void launchAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent, bundle);
    }

    /**
     * Изменяет отображаемый город WeatherActivity
     */

    public void changeDisplayedCity(String newCity) {
        PositionManager.getInstance().setMessageProvider(this);
        PositionManager.getInstance().setReceiver(this);
        PositionManager.getInstance().setActivePosition(newCity);
        PositionManager.getInstance().updateWeatherFromDB();
        onRefresh();
    }

    @Override
    public void onBackPressed() {
        if (!navigationDrawer.closeDrawer()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PositionManager.getInstance().setReceiver(this);
        PositionManager.getInstance().setMessageProvider(this);
        boolean isLocationPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        navigationDrawer.updateBadges(isLocationPermissionGranted);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Получаем GPS флаг из памяти
        PositionManager.getInstance().setUseGpsModule(sp.getBoolean(getString(R.string.pref_gps_key), true));

        // Получаем единицы измерения температуры из памяти
        String temperature = sp.getString(getString(R.string.pref_temperature_key), getString(R.string
                .pref_temperature_celsius));
        if (temperature.equals(getString(R.string.pref_temperature_kelvin))) {
            PositionManager.getInstance().setTemperatureMetric(AppUtils.TemperatureMetrics.KELVIN);
        } else if (temperature.equals(getString(R.string.pref_temperature_fahrenheit))) {
            PositionManager.getInstance().setTemperatureMetric(AppUtils.TemperatureMetrics.FAHRENHEIT);
        } else {
            PositionManager.getInstance().setTemperatureMetric(AppUtils.TemperatureMetrics.CELSIUS);
        }

        // Получаем единицы измерения скорости ветра из памяти
        String speed = sp.getString(getString(R.string.pref_speed_key), getString(R.string.pref_speed_meter_sec));
        if (speed.equals(getString(R.string.pref_speed_foot_sec))) {
            PositionManager.getInstance().setSpeedMetric(AppUtils.SpeedMetrics.FOOT_PER_SECOND);
        } else if (speed.equals(getString(R.string.pref_speed_km_hour))) {
            PositionManager.getInstance().setSpeedMetric(AppUtils.SpeedMetrics.KM_PER_HOURS);
        } else if (speed.equals(getString(R.string.pref_speed_mile_hour))) {
            PositionManager.getInstance().setSpeedMetric(AppUtils.SpeedMetrics.MILES_PER_HOURS);
        } else {
            PositionManager.getInstance().setSpeedMetric(AppUtils.SpeedMetrics.METER_PER_SECOND);
        }

        // Получаем единицы измерения давления из памяти
        String pressure = sp.getString(getString(R.string.pref_pressure_key), getString(R.string.pref_pressure_hpa));
        if (pressure.equals(getString(R.string.pref_pressure_mm_hg))) {
            PositionManager.getInstance().setPressureMetric(AppUtils.PressureMetrics.MM_HG);
        } else {
            PositionManager.getInstance().setPressureMetric(AppUtils.PressureMetrics.HPA);
        }
        PositionManager.getInstance().updateWeatherFromDB();
    }

    void checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
        showPermissionSnack();
    }

    void showPermissionSnack() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.weather_act_enable_permission_txt,
                Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(this, R.color.accent))
                .setAction(R.string.weather_act_accept_txt, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_SETTINGS_CODE);
                    }
                });
        TextView tv = (TextView) snackbar.getView().findViewById(snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String currentPositionName = PositionManager.getInstance().getActivePositionCity();
        sp.edit().putString(getString(R.string.last_active_position_name), currentPositionName).apply();

        PositionManager.getInstance().setMessageProvider(null);
        PositionManager.getInstance().setReceiver(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PositionManager.getInstance().saveSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PositionManager.getInstance().removeInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_weather, menu);
        final MenuItem item = menu.findItem(R.id.menu_item_refresh);
        item.setActionView(R.layout.iv_action_refresh);

        IconicsDrawable icon = new IconicsDrawable(this)
                .paddingDp(3)
                .icon(GoogleMaterial.Icon.gmd_refresh)
                .color(ContextCompat.getColor(this, R.color.current_weather_color));

        syncBtn = (ImageView) item.getActionView().findViewById(R.id.refresh_button);
        syncBtn.setImageDrawable(icon);
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
                syncBtn.startAnimation(animationRotateCenter);
                onRefresh();
                return true;
        }
        return false;
    }

    /**
     * Обновление интерфейса Activity при получении новых данных
     */
    @Override
    public void updateInterface(WeatherStation.ResponseType responseType,
                                Map<Calendar, Weather> forecast) {
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
                hourlyForecastFragment.setTimeZone(PositionManager.getInstance().getActivePosition().getTimeZone());
                hourlyForecastFragment.setDatasAndAnimate(forecast);
                if (hourlyForecastFragment.isVisible()) {
                    updateWeatherChart(true);
                }
                break;
            case DAILY:
                Logger.println(TAG, "Принят DAILY прогноз");
                dailyForecastFragment.setDatasAndAnimate(forecast);
                if (dailyForecastFragment.isVisible()) {
                    updateWeatherChart(true);
                }
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

    @SuppressLint("DefaultLocale")
    public void updateCurrentWeather(Calendar date, Weather weather) {
        PositionManager pm = PositionManager.getInstance();
        setCityInToolbar(pm.getActivePositionCity());

        if (weather == null) {
            Log.i(TAG, "Weather is null!");
            return;
        }

        double temperature = weather.getTemperature();
        String temperatureMetrics = pm.getTemperatureMetric().toStringValue();
        temperatureView.setText(String.format("%.0f%s", temperature, temperatureMetrics));

        String precipitation1 = weather.getDescription().substring(0, 1).toUpperCase();
        String precipitation2 = weather.getDescription().substring(1);
        precipitationView.setText(String.format("%s", precipitation1 + precipitation2));

        boolean isDay = AppUtils.isDayFromString(String.format(Locale.getDefault(), "%tR", date));
        Drawable weatherIcon = pm.getWeatherIcon(weather.getPrecipitation().getIconIndex(isDay), true);
        currentWeatherView.setImageDrawable(weatherIcon);

        String directionString = weather.getWindDirection().getDirectionString();
        double windPower = weather.getWindPower();
        String speedMetrics = PositionManager.getInstance().getSpeedMetric().toStringValue();
        windView.setText(Html.fromHtml(String.format("%s %.0f%s", directionString, windPower, speedMetrics)));

        humidityView.setText(String.format("%s%%", weather.getHumidity()));

        int pressure = weather.getIntPressure();
        String pressureMetrics = pm.getPressureMetric().toStringValue();
        pressureView.setText(String.format("%s %s", pressure, pressureMetrics));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                switchDisplayMode();
                break;
            case R.id.temperature_text:
                PositionManager.getInstance().changeTemperatureMetric();
                PositionManager.getInstance().updateWeatherFromDB();
                break;
        }
    }

    private void switchDisplayMode() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        IconicsDrawable icon = new IconicsDrawable(this)
                .color(ContextCompat.getColor(this, R.color.current_weather_color));
        if (dailyForecastFragment.isHidden()) {
            ft.show(dailyForecastFragment).hide(hourlyForecastFragment).commit();
            icon.icon(Octicons.Icon.oct_clock);
            updateWeatherChart(false);
        } else {
            ft.show(hourlyForecastFragment).hide(dailyForecastFragment).commit();
            icon.icon(Octicons.Icon.oct_calendar);
            updateWeatherChart(true);
        }
        fab.setImageDrawable(icon);
    }

    /**
     * Анимация обновления
     */
    @Override
    public void onRefresh() {
        showProgress(true);
        if (!PositionManager.getInstance()
                .positionIsPresent(PositionManager.getInstance().getActivePositionCity())) {
            Logger.println(TAG, "There is nothing to refresh");
            showSnackbar(R.string.msg_no_city, Snackbar.LENGTH_LONG);
            showProgress(false);
            return;
        }
        PositionManager.getInstance().updateWeather();
    }

    public void showProgress(boolean loading) {
        progressbar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean receiveHourlyWeatherFirst() {
        return dailyForecastFragment.isHidden();
    }

    @Override
    public void showSnackbar(int stringId, int length) {
        showSnackbar(getString(stringId), length);
    }

    @Override
    public void showSnackbar(CharSequence string, int length) {
        AppUtils.showSnackBar(this, coordinatorLayout, string, length);
    }

    @Override
    public void showToast(int stringId) {
        AppUtils.showInfoMessage(this, getString(stringId)).show();
    }

    @Override
    public void showToast(CharSequence string) {
        Toast toast = AppUtils.showInfoMessage(this, string);
        toast.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.background_toast));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void updateWeatherChart(final boolean isHourFragmentVisible) {
        Map<Calendar, Weather> forecast;
        if (isHourFragmentVisible) {
            forecast = hourlyForecastFragment.getForecasts();
        } else {
            forecast = dailyForecastFragment.getForecasts();
        }
        chart.updateForecast(forecast, isHourFragmentVisible);
    }

    private void setCityInToolbar(String city) {
        activeCity = city;
        if (city != null) {
            toolbar.setTitle(city.split(",")[0]);
        }
    }
}