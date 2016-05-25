package com.khasang.forecast.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.khasang.forecast.R;
import com.khasang.forecast.fragments.ChangelogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Активити с Changelog
 */
public class ChangelogActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String colorScheme = sp.getString(getString(R.string.pref_color_scheme_key), getString(R.string.pref_color_scheme_teal));
        if (colorScheme.equals(getString(R.string.pref_color_scheme_brown))) {
            setTheme(R.style.AppTheme_Changelog_Brown);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_teal))) {
            setTheme(R.style.AppTheme_Changelog_Teal);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_indigo))) {
            setTheme(R.style.AppTheme_Changelog_Indigo);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_purple))) {
            setTheme(R.style.AppTheme_Changelog_Purple);
        } else {
            setTheme(R.style.AppTheme_Changelog_Green);
        }
        setContentView(R.layout.activity_one_fragment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new ChangelogFragment())
            .commit();
    }
}
