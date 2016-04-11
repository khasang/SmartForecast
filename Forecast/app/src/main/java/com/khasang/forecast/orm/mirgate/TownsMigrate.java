package com.khasang.forecast.orm.mirgate;

import android.content.Context;
import android.content.SharedPreferences;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.orm.TownsManager;
import com.khasang.forecast.orm.tables.SettingsTable;
import com.khasang.forecast.orm.tables.TownsTable;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.sqlite.SQLiteFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by maxim.kulikov on 22.03.2016.
 */

public class TownsMigrate {

    private static final String APP_PREFERENCES = "ForecastTown";
    private static final String ID = "Id";
    private static final String COUNT = "Count";
    private static final String TOWN = "Town";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String SUNRISE = "Sunrise";
    private static final String SUNSET = "Sunset";
    private static final String FAVORITE = "Favorite";

    public TownsMigrate() {

    }

    public void setTownsListInSharedPreferences(ArrayList<HashMap<String, String>> townsList) {
        try {
            SharedPreferences mTown;
            SharedPreferences.Editor editor = null;
            for(int i = 0; i < townsList.size(); i++) {

                mTown = MyApplication.getAppContext().getSharedPreferences(APP_PREFERENCES + Integer.toString(i), Context.MODE_PRIVATE);
                editor = mTown.edit();

                HashMap<String, String> map = townsList.get(i);
                editor.putString(ID, Integer.toString(i));
                editor.putString(COUNT, Integer.toString(townsList.size()));
                editor.putString(TOWN, map.get(SQLiteFields.TOWN));
                editor.putString(LATITUDE, map.get(SQLiteFields.LATITUDE));
                editor.putString(LONGITUDE, map.get(SQLiteFields.LONGITUDE));
                editor.putString(SUNRISE, map.get(SQLiteFields.SUNRISE));
                editor.putString(SUNSET, map.get(SQLiteFields.SUNSET));
                editor.putString(FAVORITE, map.get(SQLiteFields.FAVORITE));
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTownsListInOrm() {
        try {
            TownsManager townsManager = new TownsManager();
            SharedPreferences mTown = MyApplication.getAppContext().getSharedPreferences(APP_PREFERENCES + Integer.toString(0), Context.MODE_PRIVATE);

            if (mTown.contains(COUNT)) {
                int count = Integer.parseInt(mTown.getString(COUNT, "0"));
                for (int i = 0; i < count; i++) {
                    mTown = MyApplication.getAppContext().getSharedPreferences(APP_PREFERENCES + Integer.toString(0), Context.MODE_PRIVATE);
                    townsManager.saveTown(
                            getValue(TOWN, mTown),
                            Double.parseDouble(getValue(LATITUDE, mTown)),
                            Double.parseDouble(getValue(LONGITUDE, mTown)),
                            getValue(SUNRISE, mTown),
                            getValue(SUNSET, mTown),
                            Boolean.valueOf(getValue(FAVORITE, mTown))
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getValue(String fieldName, SharedPreferences mTown) {
        try {
            if (mTown.contains(fieldName)) {
                return mTown.getString(fieldName, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
