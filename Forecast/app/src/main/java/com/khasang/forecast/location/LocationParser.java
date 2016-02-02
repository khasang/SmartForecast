package com.khasang.forecast.location;

import android.location.Address;
import android.location.Location;
import android.util.Log;

/**
 * Created by roman on 03.02.16.
 */
public class LocationParser { // кидать ошибку если нехватает элементов
    Address address;
    boolean hasResult;
    public LocationParser(Address address) {
        this.address = address;
        hasResult = false;
    }



    private void parseAddress () {

    }

    private String getCountry () {

    }


    private String buildCurrentLocationName(Address address) {
        StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < 3; ++i) {
            String temp = null;
            if (i == 0) {
                temp = address.getLocality();
                if (temp == null) {
                    temp = address.getSubAdminArea();
                }
            } else if (i == 1) {
                temp = address.getAdminArea();
            } else if (i == 2) {
                temp = address.getCountryName();
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            if (!(temp == null)) {
                Log.d("LOCATION", temp);
                sb.append(temp);
            }
        }
        return sb.toString();
    }
}
