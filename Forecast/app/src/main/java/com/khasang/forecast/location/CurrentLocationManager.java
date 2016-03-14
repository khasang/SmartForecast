package com.khasang.forecast.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.exceptions.EmptyCurrentAddressException;
import com.khasang.forecast.position.PositionManager;

import java.util.List;

/**
 * Created by roman on 30.01.16.
 */
public class CurrentLocationManager {
    private LocationManager locationManager;
    private boolean isGpsAccessGranted;
    private boolean gps_enabled;
    private boolean network_enabled;

    public CurrentLocationManager() {
        locationManager = (LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;
    }

    /**
     * Метод дает возможность использовать или не использовать GPS для получения координат
     *
     * @param access примитив типа boolean, однозначно определяющий использовать или не использовать модуль GPS для опередения координат
     */
    public void giveGpsAccess(boolean access) {
        isGpsAccessGranted = access;
    }

    /**
     * Метод для получения последнего известного местоположения (вызывается при старте программы)
     *
     * @return объект типа {@link Location}, указывающий на последнее обнаруженное каким либо провайдером местоположение
     */
    public Location getLastLocation() throws EmptyCurrentAddressException {
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            giveMessageAboutPermission();
            return null;
        }
        Location recentLocation = null;
        List<String> matchingProviders = locationManager.getAllProviders();
        long bestTime = 0;
        for (String provider : matchingProviders) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                long time = location.getTime();
                if (time > bestTime) {
                    recentLocation = location;
                    bestTime = time;
                }
            }
        }
        if (recentLocation == null) {
            throw new EmptyCurrentAddressException();
        }
        return recentLocation;
    }

    /**
     * Метод для выбора лучшего из возможных на данный момент провайдеров определения местоположения
     *
     * @return объект типа {@link String}, определяющий провайдера для определения местоположения
     */
    private String getTheBestProvider() {
        if (!isGpsAccessGranted) {
            return LocationManager.NETWORK_PROVIDER;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        String providerName = locationManager.getBestProvider(criteria, true);
        if (providerName != null) {
            return providerName;
        } else if (gps_enabled) {
            return LocationManager.GPS_PROVIDER;
        } else {
            return LocationManager.NETWORK_PROVIDER;
        }
    }

    public boolean checkProviders() {
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGpsAccessGranted) {
            return network_enabled;
        }
        return (gps_enabled || network_enabled);
    }

    public void updateCurrentLocationCoordinates() {
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            giveMessageAboutPermission();
            return;
        }
        if (!checkProviders()) {
            if (!isGpsAccessGranted) {
                Toast.makeText(MyApplication.getAppContext(), R.string.error_gps_disabled, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MyApplication.getAppContext(), R.string.error_location_services_are_not_active, Toast.LENGTH_LONG).show();
            }
            return;
        }
        locationManager.removeUpdates(locationListener);
        locationManager.requestSingleUpdate(getTheBestProvider(), locationListener, null);
    }

    private void coordinatesUpdated(Location location) {
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            giveMessageAboutPermission();
            return;
        }
        locationManager.removeUpdates(locationListener);
        PositionManager.getInstance().setCurrentLocationCoordinates(location);
    }

    private void giveMessageAboutPermission() {
        Toast.makeText(MyApplication.getAppContext(), MyApplication.getAppContext().getString(R.string.error_gps_permission), Toast.LENGTH_SHORT).show();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            coordinatesUpdated(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
