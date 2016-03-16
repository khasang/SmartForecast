package com.khasang.forecast.orm;

import android.util.Log;

import com.khasang.forecast.orm.tables.TownsTable;
import com.khasang.forecast.orm.tables.WeatherTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by maxim.kulikov on 16.03.2016.
 */
public class TownsManager {

    private String mTag = "SugarTest";
    private SimpleDateFormat dtFormat;

    public TownsManager() {
        dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    // Сохранение города с координатами (перед сохранением списка нужно очистить старый)
    public void saveTown(String townName, double latitude, double longitude) {
        try {
            if (!checkTown(townName)) {
                new TownsTable(townName, latitude, longitude).save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    public long getTownId(String townName) {
        List<TownsTable> list = new ArrayList<>();
        try {
            list = TownsTable.find(TownsTable.class, "name = ?", new String[]{townName});
            return list.get(0).getId();
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return -1;
    }

    public boolean checkTown(String townName) {
        List<TownsTable> list = new ArrayList<>();
        try {
            list = TownsTable.find(TownsTable.class, "name = ?", new String[]{townName});
            return list.size() > 0;
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return false;
    }

    // Очистка таблицы городов и удаление погодных данных к ним.
    public void deleteTowns() {
        TownsTable.deleteAll(TownsTable.class);
        WeatherTable.deleteAll(WeatherTable.class);
    }

    // Удаление города и погодных данных к нему.
    public void deleteTownAllData(String townName) {
        deleteTown(townName);
        deleteTownWeather(townName);
    }

    public void deleteTownWeather(String townName) {
        List<WeatherTable> list = new ArrayList<>();
        try {
            list = WeatherTable.find(WeatherTable.class, "town = ?", new String[]{townName});
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).delete();
                }
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Удаление города и погодных данных к нему.
    public void deleteTown(String townName) {
        List<TownsTable> list = new ArrayList<>();
        try {
            list = TownsTable.find(TownsTable.class, "name = ?", new String[]{townName});
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).delete();
                }
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    public void updateTownSunTime(Calendar sunRise, Calendar sunSet, String townName) {
        try {
            TownsTable townTable = TownsTable.findById(TownsTable.class, getTownId(townName));
            if (townTable != null) {
                townTable.sunrise = dtFormat.format(sunRise.getTime());
                townTable.sunset = dtFormat.format(sunSet.getTime());
                townTable.save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Получение времени рассвета к городу.
    public Calendar loadTownSunSet(String townName) {
        Calendar weatherDate = null;
        try {
            TownsTable townTable = TownsTable.findById(TownsTable.class, getTownId(townName));
            if (townTable != null) {
                weatherDate = Calendar.getInstance();
                weatherDate.setTime(dtFormat.parse(townTable.sunset));
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return weatherDate;
    }

    // Получение времени заката к городу.
    public Calendar loadTownSunRise(String townName) {
        Calendar weatherDate = null;
        try {
            TownsTable townTable = TownsTable.findById(TownsTable.class, getTownId(townName));
            if (townTable != null) {
                weatherDate = Calendar.getInstance();
                weatherDate.setTime(dtFormat.parse(townTable.sunrise));
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return weatherDate;
    }

    // Добавление города в избранное.
    public void saveTownFavourite(boolean isFavourite, String townName) {
        try {
            TownsTable townTable = TownsTable.findById(TownsTable.class, getTownId(townName));
            if (townTable != null) {
                townTable.favorite = isFavourite;
                townTable.save();
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
    }

    // Наличие города в избранном.
    public boolean getTownFavourite(String townName) {
        try {
            TownsTable townTable = TownsTable.findById(TownsTable.class, getTownId(townName));
            if (townTable != null) {
                return townTable.favorite;
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return false;
    }

    // Загрузка списка городов.
    public List<TownsTable> loadTownList() {
        List<TownsTable> list = null;
        try {
            list = TownsTable.listAll(TownsTable.class);
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return list;
    }

    // Загрузка списка избранных городов.
    public ArrayList<String> loadFavoriteTownList() {
        List<TownsTable> list;
        ArrayList<String> listNames = new ArrayList<>();
        try {
            list = TownsTable.find(TownsTable.class, "favorite = ?", new String[]{Boolean.toString(true)});
            if (list != null) {
                for(int i = 0; i < list.size(); i++) {
                    listNames.add(list.get(i).name);
                }
            }
        } catch (Exception e) {
            Log.i(mTag, e.getMessage());
        }
        return listNames;
    }

}
