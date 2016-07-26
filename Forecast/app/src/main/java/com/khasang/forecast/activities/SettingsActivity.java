package com.khasang.forecast.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.utils.AppUtils;
import com.khasang.forecast.utils.LocaleUtils;

import java.util.Locale;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {

    private static boolean configurationChanged;

    @BindView(R.id.toolbar) Toolbar toolbar;

    private static void setConfigurationChanged(boolean changed) {
        configurationChanged = changed;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_container, new GeneralPreferenceFragment())
                .commit();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveActivity();
            }
        });
        getSupportActionBar().setTitle(R.string.title_activity_settings);
    }

    @Override
    public void onBackPressed() {
        leaveActivity();
    }

    @Override
    protected void leaveActivity() {
        if (configurationChanged) {
            configurationChanged = false;

            Intent intent = new Intent(this, WeatherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(WeatherActivity.ACTIVE_CITY_TAG, PositionManager.getInstance().getActivePositionCity());
            startActivity(intent);
        }
        super.leaveActivity();
    }

    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_general);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_temperature_key));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_speed_key));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_pressure_key));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.pref_location_key));

            Preference preference = findPreference(getString(R.string.pref_language_key));
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;

                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string
                        .pref_language_key), null));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                } else {
                    String languageCode = Locale.getDefault().getLanguage();
                    if (getString(R.string.pref_language_deutsch).equals(languageCode)) {
                        preference.setSummary(getString(R.string.pref_language_deutsch_label));
                        listPreference.setValue(getString(R.string.pref_language_deutsch));
                    } else if (getString(R.string.pref_language_russian).equals(languageCode)) {
                        preference.setSummary(getString(R.string.pref_language_russian_label));
                        listPreference.setValue(getString(R.string.pref_language_russian));
                    } else if (getString(R.string.pref_language_ukrainian).equals(languageCode)) {
                        preference.setSummary(getString(R.string.pref_language_ukrainian_label));
                        listPreference.setValue(getString(R.string.pref_language_ukrainian));
                    } else {
                        preference.setSummary(getString(R.string.pref_language_english_label));
                        listPreference.setValue(getString(R.string.pref_language_english));
                    }
                }
            }

            preference = findPreference(getString(R.string.pref_color_scheme_key));
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string
                        .pref_color_scheme_key), getString(R.string.pref_color_scheme_teal)));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }

            preference = findPreference(getString(R.string.pref_night_mode_key));
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string
                        .pref_night_mode_key), getString(R.string.pref_night_mode_off)));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }

            preference = findPreference(getString(R.string.pref_icons_set_key));
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string
                        .pref_icons_set_key), getString(R.string.pref_icons_set_default)));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_welcome_key))) {
                boolean welcome = sharedPreferences.getBoolean(getString(R.string.pref_welcome_key), true);
                sharedPreferences.edit().putBoolean(getString(R.string.pref_welcome_text_key), welcome).apply();

                SwitchPreference welcomeTextSwitchPreference = (SwitchPreference) findPreference(getString(R
                        .string.pref_welcome_text_key));
                welcomeTextSwitchPreference.setChecked(welcome);
                return;
            }

            if (key.equals(getString(R.string.pref_gps_key))) {
                if (sharedPreferences.getBoolean(getString(R.string.pref_gps_key), true)) {
                    Toast toast = AppUtils.showInfoMessage(getActivity(), getString(R.string.warning_message_gps));
                    toast.getView().setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color
                            .background_toast));
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                }
                return;
            }

            Preference preference = findPreference(key);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
                if (key.equals(getString(R.string.pref_language_key))) {
                    String languageCode = listPreference.getEntryValues()[prefIndex].toString();
                    LocaleUtils.updateResources(getContext(), languageCode);

                    setConfigurationChanged(true);
                    getActivity().recreate();
                } else if (key.equals(getString(R.string.pref_color_scheme_key))) {
                    SettingsActivity.setConfigurationChanged(true);
                    getActivity().recreate();
                } else if (key.equals(getString(R.string.pref_night_mode_key))) {
                    String stringValue = sharedPreferences.getString(getString(R.string.pref_night_mode_key),
                            getString(R.string.pref_night_mode_off));
                    if (stringValue.equals(getString(R.string.pref_night_mode_off))) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else if (stringValue.equals(getString(R.string.pref_night_mode_on))) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else if (stringValue.equals(getString(R.string.pref_night_mode_auto))) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                    } else {
                        // неподдерживаемая функциональность
                    }
                    SettingsActivity.setConfigurationChanged(true);
                    getActivity().recreate();
                } else if (key.equals(getString(R.string.pref_icons_set_key))) {
                    PositionManager.getInstance().generateIconSet(getActivity());
                    SettingsActivity.setConfigurationChanged(true);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}