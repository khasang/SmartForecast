package com.khasang.forecast.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;


public class SplashScreenActivity
        extends AppCompatActivity
        implements Animation.AnimationListener {

    private final static String TAG = SplashScreenActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSIONS_REQUEST_LOCATION = 10;
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView splash_cloud = (ImageView) findViewById(R.id.splash_cloud);
        final ImageView splash_left_cloud = (ImageView) findViewById(R.id.splash_left_cloud);
        ImageView splash_right_cloud = (ImageView) findViewById(R.id.splash_right_cloud);
        ImageView splash_rainbow = (ImageView) findViewById(R.id.splash_rainbow);
        ImageView splash_smile = (ImageView) findViewById(R.id.splash_smile);
        ImageView splash_wink = (ImageView) findViewById(R.id.splash_wink);

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

        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        }

        // Запуск анимации
        PositionManager.getInstance().initManager();
        splash_cloud.startAnimation(anim_splash_cloud);
        splash_left_cloud.startAnimation(anim_splash_left_cloud);
        splash_right_cloud.startAnimation(anim_splash_right_cloud);
        splash_rainbow.startAnimation(anim_splash_rainbow);
        splash_smile.startAnimation(anim_splash_smile);
        splash_wink.startAnimation(anim_splash_wink);
        anim_splash_wink.setAnimationListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
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
            Intent intent = new Intent(SplashScreenActivity.this, WeatherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreenActivity.this)
                    .toBundle();
            ActivityCompat.startActivity(SplashScreenActivity.this, intent, bundle);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
