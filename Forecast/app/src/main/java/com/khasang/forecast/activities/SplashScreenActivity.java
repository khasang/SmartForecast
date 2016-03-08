package com.khasang.forecast.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;


public class SplashScreenActivity
        extends AppCompatActivity
        implements Animation.AnimationListener {

    private final static String TAG = SplashScreenActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSIONS_REQUEST_LOCATION = 10;
    private volatile boolean coordinatesServicesChecked = false;
    private volatile boolean runtimePermissionChecked = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView splash_cloud = (ImageView) findViewById(R.id.splash_cloud);
        final ImageView splash_left_cloud = (ImageView) findViewById(R.id.splash_left_cloud);
        final ImageView splash_right_cloud = (ImageView) findViewById(R.id.splash_right_cloud);
        final ImageView splash_rainbow = (ImageView) findViewById(R.id.splash_rainbow);
        final ImageView splash_smile = (ImageView) findViewById(R.id.splash_smile);
        final ImageView splash_wink = (ImageView) findViewById(R.id.splash_wink);

        // Определение изображение для ImageView
        splash_cloud.setImageResource(R.drawable.splash_cloud);
        splash_left_cloud.setImageResource(R.drawable.splash_left_cloud);
        splash_right_cloud.setImageResource(R.drawable.splash_right_cloud);
        splash_rainbow.setImageResource(R.drawable.splash_rainbow);
        splash_smile.setImageResource(R.drawable.splash_smile);
        splash_wink.setImageResource(R.drawable.splash_wink);

        // Создание анимации
        Animation anim_splash_cloud = AnimationUtils.loadAnimation(this, R.anim.anim_splash_cloud);
        Animation anim_splash_left_cloud = AnimationUtils.loadAnimation(this, R.anim.anim_splash_left_cloud);
        Animation anim_splash_right_cloud = AnimationUtils.loadAnimation(this, R.anim.anim_splash_right_cloud);
        Animation anim_splash_rainbow = AnimationUtils.loadAnimation(this, R.anim.anim_splash_rainbow);
        Animation anim_splash_smile = AnimationUtils.loadAnimation(this, R.anim.anim_splash_smile);
        Animation anim_splash_wink = AnimationUtils.loadAnimation(this, R.anim.anim_splash_wink);

        splash_cloud.startAnimation(anim_splash_cloud);
        splash_left_cloud.startAnimation(anim_splash_left_cloud);
        splash_right_cloud.startAnimation(anim_splash_right_cloud);
        splash_rainbow.startAnimation(anim_splash_rainbow);
        splash_smile.startAnimation(anim_splash_smile);
        splash_wink.startAnimation(anim_splash_wink);
        anim_splash_wink.setAnimationListener(this);
    }

    private boolean checkProviders() {
        LocationManager locationManager = ((LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE));
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return (gps_enabled || network_enabled);
    }

    private void checkCoordinatesServices() {
        if (!checkProviders()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.location_manager);
            builder.setMessage(R.string.activate_geographical_service);
            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Launch settings, allowing user to make a change
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            });
            builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    coordinatesServicesChecked = true;
                    startWeatherActivity();
                }
            });
            builder.create().show();
        } else {
            coordinatesServicesChecked = true;
        }
    }

    private void checkPermissions() {
        // TODO добавить проверку на рантайм пермишны в АПИ > 23
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        SplashScreenActivity.this.finish();
                    }
                });
                dialog.show();
            } else {
                Log.d("LOG", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (checkPlayServices()) {
            checkCoordinatesServices();
            checkPermissions();
            startWeatherActivity();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    private void startWeatherActivity() {
        if (coordinatesServicesChecked && runtimePermissionChecked) {
            Intent intent = new Intent(SplashScreenActivity.this, WeatherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreenActivity.this)
                    .toBundle();
            ActivityCompat.startActivity(SplashScreenActivity.this, intent, bundle);
        }
    }
}
