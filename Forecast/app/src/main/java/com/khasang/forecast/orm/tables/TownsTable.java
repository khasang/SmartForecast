package com.khasang.forecast.orm.tables;

import com.orm.SugarRecord;

/**
 * Created by maxim.kulikov on 15.03.2016.
 */

public class TownsTable extends SugarRecord {
    
    public String name = "";
    public double latitude = 0;
    public double longitude = 0;
    public String sunrise = "";
    public String sunset = "";
    public boolean favorite = false;

    public TownsTable(){
    }

    public TownsTable(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TownsTable(String name, double latitude, double longitude, boolean favorite) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.favorite = favorite;
    }

    public TownsTable(String name, double latitude, double longitude, String sunrise, String sunset, boolean favorite) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.favorite = favorite;
    }
}
