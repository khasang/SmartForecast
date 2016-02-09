package com.khasang.forecast.location;

import android.location.Address;

import com.khasang.forecast.location.exceptions.EmptyCurrentAddressException;
import com.khasang.forecast.location.exceptions.NoAvailableAddressesException;

import java.util.List;

/**
 * Created by roman on 03.02.16.
 */
public class LocationParser { // кидать ошибку если нехватает элементов
    List<Address> list;
    String country;
    String region;
    String subRegion;
    String city;

    public LocationParser() {
        this(null);
    }

    public LocationParser(List<Address> list) {
        this.list = list;
        country = null;
        region = null;
        subRegion = null;
        city = null;
    }

    public LocationParser setAddressesList(List<Address> list) {
        this.list = list;
        return this;
    }

    public LocationParser parseList() throws NoAvailableAddressesException {
        if (list == null || list.size() == 0) {
            throw new NoAvailableAddressesException();
        } else {
            for (Address address : list) {
                if (country == null) {
                    country = address.getCountryName();
                }
                if (region == null) {
                    region = address.getAdminArea();
                }
                if (subRegion == null) {
                    subRegion = address.getSubAdminArea();
                }
                if (city == null) {
                    city = address.getLocality();
                }
            }
        }
        return this;
    }

    public String getAddressLine() throws EmptyCurrentAddressException {
        StringBuilder sb = new StringBuilder(20);
        if (city == null) {
            city = "";
        }
        if (subRegion == null) {
            subRegion = "";
        }
        if (region == null) {
            region = "";
        }
        if (country == null) {
            country = "";
        }

        if (city.length() > 0) {
            sb.append(city);
        } else {
            sb.append(subRegion);
        }
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(region);
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(country);
        if (sb.length() == 0) {
            throw new EmptyCurrentAddressException();
        }
        return sb.toString();
    }
}
