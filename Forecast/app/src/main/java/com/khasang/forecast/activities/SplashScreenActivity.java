package com.khasang.forecast.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.PermissionChecker;
import com.khasang.forecast.R;
import com.khasang.forecast.interfaces.IPermissionCallback;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import static com.khasang.forecast.PermissionChecker.RuntimePermissions.PERMISSION_REQUEST_FINE_LOCATION;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class SplashScreenActivity
        extends AppCompatActivity
        implements IPermissionCallback {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private volatile boolean coordinatesServicesChecked = false;
    private volatile boolean runtimePermissionChecked = false;
    private volatile boolean gifIsFinishedPlaying = false;

    GifDrawable gifDrawable = null;
    Shimmer shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        GifImageView gifImageView = ((GifImageView) findViewById(R.id.gifImageView));
        try {
            gifDrawable = new GifDrawable(getResources(), R.raw.splash_screen);
            gifDrawable.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifImageView.setImageDrawable(gifDrawable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gifDrawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gifIsFinishedPlaying = true;
                startWeatherActivity();
            }
        }, gifDrawable.getDuration());
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmer = new Shimmer();
        shimmer.start(((ShimmerTextView) findViewById(R.id.welcomeText)));
        if (checkPlayServices()) {
            checkCoordinatesServices();
            checkPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gifDrawable.stop();
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
        PermissionChecker permissionChecker = new PermissionChecker();
        permissionChecker.checkForPermissions(this, PERMISSION_REQUEST_FINE_LOCATION, this);
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

    private void startWeatherActivity() {
        if (coordinatesServicesChecked && runtimePermissionChecked && gifIsFinishedPlaying) {
            Intent intent = new Intent(SplashScreenActivity.this, WeatherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreenActivity.this).toBundle();
            ActivityCompat.startActivity(SplashScreenActivity.this, intent, bundle);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_FINE_LOCATION.VALUE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Splash screen checkForPermissions granted");
            } else {
                Log.d("PERMISSION", "Splash screen checkForPermissions not granted");
            }
        }
        runtimePermissionChecked = true;
        startWeatherActivity();
    }


    @Override
    public void permissionGranted(PermissionChecker.RuntimePermissions permission) {
        runtimePermissionChecked = true;
        startWeatherActivity();
    }

    @Override
    public void permissionDenied(PermissionChecker.RuntimePermissions permission) {

    }
}
