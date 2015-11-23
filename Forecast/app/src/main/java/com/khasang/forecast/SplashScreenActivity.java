package com.khasang.forecast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /** Заглушка для запуска WeatherActivity */
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
    }
}
