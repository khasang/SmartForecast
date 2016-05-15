package com.khasang.forecast.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import com.khasang.forecast.R;
import com.khasang.forecast.fragments.TeamFragment;

/**
 * Активити "О приложении"
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String colorScheme = sp.getString(getString(R.string.pref_color_scheme_key), getString(R.string.pref_color_scheme_teal));
        if (colorScheme.equals(getString(R.string.pref_color_scheme_brown))) {
            setTheme(R.style.AppTheme_About_Brown);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_teal))) {
            setTheme(R.style.AppTheme_About_Teal);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_indigo))) {
            setTheme(R.style.AppTheme_About_Indigo);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_purple))) {
            setTheme(R.style.AppTheme_About_Purple);
        } else {
            setTheme(R.style.AppTheme_About_Green);
        }
        setContentView(R.layout.activity_about);
        setupToolbar();

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.about_fragment_container, new TeamFragment())
            .commit();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
