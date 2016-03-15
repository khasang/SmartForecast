package com.khasang.forecast.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.io.IOException;
import java.util.Calendar;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity
        extends AppCompatActivity
        implements Animation.AnimationListener, AnimationListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GifDrawable gifDrawable = null;
    private Shimmer shimmer;
    private ShimmerTextView welcomeText;
    private JumpingBeans jumpingBeans;
    boolean isGooglePlayServicesInstalled = false;
    boolean welcomeStringIsOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        welcomeStringIsOff = sp.getString(getString(R.string.pref_welcome_key), getString(R.string.pref_welcome_default)).equals(getString(R.string.pref_welcome_off));
        welcomeStringIsOff = !sp.getBoolean(getString(R.string.pref_welcome_key_switch), true);
        if (!welcomeStringIsOff) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            String text = getString(R.string.welcome_def_str);
            if (hour < 4) {
            } else if (hour >= 4 && hour <= 9) {
                text = getString(R.string.welcome_string_morning);
            } else if (hour <= 17) {
                text = getString(R.string.welcome_string_day);
            } else if (hour <= 22) {
                text = getString(R.string.welcome_string_evening);
            }
            welcomeText = ((ShimmerTextView) findViewById(R.id.welcomeText));
            welcomeText.setText(text);
            jumpingBeans = JumpingBeans
                    .with(welcomeText)
                    .appendJumpingDots()
                    .build();
        }
        GifImageView gifImageView = ((GifImageView) findViewById(R.id.gifImageView));
        try {
            gifDrawable = new GifDrawable(getResources(), R.raw.splash_screen);
            gifDrawable.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifDrawable.addAnimationListener(this);
        gifImageView.setImageDrawable(gifDrawable);
        gifDrawable.start();
        gifImageView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!welcomeStringIsOff) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.welcome_string_disappear);
                    animation.setAnimationListener(SplashScreenActivity.this);
                    welcomeText.startAnimation(animation);
                    jumpingBeans.stopJumping();
                    shimmer.cancel();
                }
            }, gifDrawable.getDuration() - 500);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_string_appear);
            welcomeText.startAnimation(animation);
            welcomeText.setVisibility(View.VISIBLE);
            shimmer = new Shimmer();
            shimmer.setStartDelay(500)
                    .start(welcomeText);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPlayServices()) {
            isGooglePlayServicesInstalled = true;
            PositionManager.getInstance();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        Intent intent = new Intent(SplashScreenActivity.this, WeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreenActivity.this).toBundle();
        ActivityCompat.startActivity(SplashScreenActivity.this, intent, bundle);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        welcomeText.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationCompleted() {
        gifDrawable.stop();
        if (isGooglePlayServicesInstalled) {
            startWeatherActivity();
        }
    }
}
