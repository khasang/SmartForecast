package com.khasang.forecast.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.khasang.forecast.R;
import com.khasang.forecast.location.LocationProvider;


public class SplashScreenActivity
        extends AppCompatActivity
        implements LocationProvider.LocationCallback {

    private final static String TAG = SplashScreenActivity.class.getSimpleName();


    private double mCurrentLatitude;
    private double mCurrentLongitude;

    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView mImageViewFilling = (ImageView) findViewById(R.id.imageview_animation_list_filling);
        ((AnimationDrawable) mImageViewFilling.getBackground()).start();


        //TODO DELETE
        mImageViewFilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageViewFilling.setActivated(!mImageViewFilling.isActivated());
            }
        });

        /** New Handler запускает  Splash-Screen Activity
         * и закрывает его после нескольких секунд ожидания.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

/*        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
        finish();*/

    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();
    }
}
