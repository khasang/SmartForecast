package com.khasang.forecast.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.khasang.forecast.AppUtils;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;

public class SettingsActivity extends AppCompatActivity {

    private static boolean themeChanged;

    static {
        themeChanged = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(AppUtils.getCurrentTheme(this));
        setContentView(R.layout.activity_settings);
        setupToolbar();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_container, new GeneralPreferenceFragment())
                .commit();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                leaveActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        leaveActivity();
    }

    public static void setThemeChanged(boolean changed) {
        themeChanged = changed;
    }

    private void leaveActivity() {
        if (themeChanged) {
            themeChanged = false;

            Intent intent = new Intent(this, WeatherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(WeatherActivity.ACTIVE_CITY_TAG, PositionManager.getInstance().getCurrentPositionName());
            startActivity(intent);
        }
        ActivityCompat.finishAfterTransition(this);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_general);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_units_key));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_location_key));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_welcome_key));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_speed_key));

            Preference preference = findPreference(getString(R.string.pref_night_mode_key));
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string.pref_night_mode_key), getString(R.string.pref_night_mode_off)));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }

            preference = findPreference(getString(R.string.pref_color_scheme_key));
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string.pref_color_scheme_key), getString(R.string.pref_color_scheme_teal)));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }

            preference = findPreference(getString(R.string.pref_icons_set_key));
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string.pref_icons_set_key), getString(R.string.pref_icons_set_default)));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_gps_key)) && sharedPreferences.getBoolean(getString(R.string.pref_gps_key), true)) {
                Toast toast = AppUtils.showInfoMessage(getActivity(), getString(R.string.warning_message_gps));
                toast.getView().setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.background_toast));
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            Preference preference = findPreference(key);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
                if (key.equals(getString(R.string.pref_night_mode_key))) {
                    String stringValue = sharedPreferences.getString(getString(R.string.pref_night_mode_key), getString(R.string.pref_night_mode_off));
                    if (stringValue.equals(getString(R.string.pref_night_mode_off))) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else if (stringValue.equals(getString(R.string.pref_night_mode_on))) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else if (stringValue.equals(getString(R.string.pref_night_mode_auto))) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                    } else {
                        // неподдерживаемая функциональность
                    }
                    SettingsActivity.setThemeChanged(true);
                    getActivity().recreate();
                } else if (key.equals(getString(R.string.pref_color_scheme_key))) {
                    SettingsActivity.setThemeChanged(true);
                    getActivity().recreate();
                }  else if (key.equals(getString(R.string.pref_icons_set_key))) {
                    PositionManager.getInstance().generateIconSet(getActivity());
                    SettingsActivity.setThemeChanged(true);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
