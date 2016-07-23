package com.khasang.forecast.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.khasang.forecast.R;

import butterknife.ButterKnife;
import icepick.Icepick;

public class BaseActivity extends AppCompatActivity {

    protected int themeResId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String colorScheme = sp.getString(getString(R.string.pref_color_scheme_key), getString(R.string.pref_color_scheme_teal));
        if (colorScheme.equals(getString(R.string.pref_color_scheme_brown))) {
            themeResId = R.style.AppTheme_Brown;
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_teal))) {
            themeResId = R.style.AppTheme_Teal;
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_indigo))) {
            themeResId = R.style.AppTheme_Indigo;
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_purple))) {
            themeResId = R.style.AppTheme_Purple;
        } else {
            themeResId = R.style.AppTheme_Green;
        }
        setTheme(themeResId);

        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
    }

    protected void leaveActivity() {
        ActivityCompat.finishAfterTransition(this);
    }
}
