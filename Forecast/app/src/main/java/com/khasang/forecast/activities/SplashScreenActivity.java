package com.khasang.forecast.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.khasang.forecast.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity extends BaseActivity implements Animation.AnimationListener, AnimationListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @BindView(R.id.welcomeText) ShimmerTextView welcomeText;
    @BindView(R.id.gifImageView) GifImageView gifImageView;
    @BindView(R.id.root) View rootView;

    private GifDrawable gifDrawable;
    private Shimmer shimmer;
    private JumpingBeans jumpingBeans;
    private boolean showWelcomeString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        boolean isGooglePlayServicesInstalled = checkPlayServices();
        if (isGooglePlayServicesInstalled) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            defineNightMode(sp);

            boolean showWelcome = sp.getBoolean(getString(R.string.pref_welcome_key), true);
            if (!showWelcome) {
                startWeatherActivityWithoutTransition();
            } else {
                showWelcomeString = sp.getBoolean(getString(R.string.pref_welcome_text_key), true);
                if (showWelcomeString) {
                    setWelcomeString();
                }

                try {
                    gifDrawable = new GifDrawable(getResources(), R.raw.splash_screen);
                    gifDrawable.addAnimationListener(this);
                    gifImageView.setImageDrawable(gifDrawable);
                    gifDrawable.start();
                    gifImageView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    startWeatherActivityWithoutTransition();
                }
            }
        }
    }

    private void defineNightMode(SharedPreferences sp) {
        String nightMode = sp.getString(getString(R.string.pref_night_mode_key), null);
        String nightModeOff = getString(R.string.pref_night_mode_off);
        String nightModeOn = getString(R.string.pref_night_mode_on);
        String nightModeAuto = getString(R.string.pref_night_mode_auto);
        if (nightModeOff.equals(nightMode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (nightModeOn.equals(nightMode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (nightModeAuto.equals(nightMode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void setWelcomeString() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String text;
        if (hour >= 4 && hour <= 9) {
            text = getString(R.string.welcome_string_morning);
        } else if (hour <= 17) {
            text = getString(R.string.welcome_string_day);
        } else if (hour <= 22) {
            text = getString(R.string.welcome_string_evening);
        } else {
            text = getString(R.string.welcome_string_default);
        }
        welcomeText.setTextColor(ContextCompat.getColor(this, R.color.material_drawer_selected_text));
        welcomeText.setText(text);
        jumpingBeans = JumpingBeans
                .with(welcomeText)
                .appendJumpingDots()
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (showWelcomeString) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim
                            .welcome_string_disappear);
                    animation.setAnimationListener(SplashScreenActivity.this);
                    welcomeText.startAnimation(animation);
                    jumpingBeans.stopJumping();
                    shimmer.cancel();
                }
            }, gifDrawable.getDuration());
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_string_appear);
            welcomeText.startAnimation(animation);
            welcomeText.setVisibility(View.VISIBLE);
            shimmer = new Shimmer();
            shimmer.setStartDelay(200)
                    .start(welcomeText);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
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

    @SuppressWarnings("unchecked")
    private void startWeatherActivity() {
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
        ActivityCompat.startActivity(this, intent, bundle);
    }

    /**
     * Создал отдельный метод, потому что на API 21+ анимация запуска WeatherActivity не работает, падает ошибка
     * java.lang.NullPointerException: Attempt to invoke virtual method 'void android.view.ViewRootImpl.setPausedForTransition(boolean)' on a null object reference
     *
     * Видимо view не успевают инициализоваться
     */
    private void startWeatherActivityWithoutTransition() {
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(this, intent, null);
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
        startWeatherActivity();
    }
}
