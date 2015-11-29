package com.khasang.forecast.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.khasang.forecast.R;

/**
 * Created by CopyPasteStd on 29.11.15.
 *
 * Activity для выбора местоположения
 */
public class CityPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);
    }
}
