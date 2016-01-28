package com.khasang.forecast.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.khasang.forecast.R;
import com.khasang.forecast.location.LocationProvider;


public class SplashScreenActivity
        extends AppCompatActivity
        implements LocationProvider.LocationCallback,
        Animation.AnimationListener {

    private final static String TAG = SplashScreenActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private double mCurrentLatitude;
    private double mCurrentLongitude;

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

        // Запуск анимации
        splash_cloud.startAnimation(anim_splash_cloud);
        splash_left_cloud.startAnimation(anim_splash_left_cloud);
        splash_right_cloud.startAnimation(anim_splash_right_cloud);
        splash_rainbow.startAnimation(anim_splash_rainbow);
        splash_smile.startAnimation(anim_splash_smile);
        splash_wink.startAnimation(anim_splash_wink);
        anim_splash_wink.setAnimationListener(this);
        //TODO FOR TEST
        //splash_left_cloud.setVisibility(View.GONE);
        //splash_right_cloud.setVisibility(View.GONE);
        //splash_rainbow.setVisibility(View.GONE);
        //splash_smile.setVisibility(View.GONE);

        //TODO FOR TEST
       /* final ImageView mImageViewFilling = (ImageView) findViewById(R.id.imageview_animation_list_face);
        mImageViewFilling.animate().setStartDelay(1000);
        ((AnimationDrawable) mImageViewFilling.getBackground()).start();*/

        //TODO DELETE
     /*   mImageViewFilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageViewFilling.setActivated(!mImageViewFilling.isActivated());
            }
        });*/

        /** New Handler запускает  Splash-Screen Activity
         * и закрывает его после нескольких секунд ожидания.*/
//        if (checkPlayServices()){
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(SplashScreenActivity.this, WeatherActivity.class);
////                    startActivity(intent);
//                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreenActivity.this)
//                            .toBundle();
//                    ActivityCompat.startActivity(SplashScreenActivity.this, intent, bundle);
//                    finish();
//                }
//            }, SPLASH_DISPLAY_LENGTH);
//        }
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
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();
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
            ActivityCompat.finishAfterTransition(SplashScreenActivity.this);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
