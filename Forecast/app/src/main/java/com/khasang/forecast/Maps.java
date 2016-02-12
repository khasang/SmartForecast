package com.khasang.forecast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.khasang.forecast.activities.CityPickerActivity;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.position.PositionManager;

/**
 * Created by uoles on 03.02.2016.
 */

public class Maps {

    private com.google.android.gms.maps.GoogleMap map;
    private final String TAG = "mapLogs";
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private float currentZoom = 0;
    private CityPickerActivity activity;
    private FragmentManager myFM;

    public Maps(CityPickerActivity activity) {
        this.activity = activity;
        if (setMap()) {
            setMapSettings();
            setMapClickListeners();
            Coordinate coordinate = PositionManager.getInstance().getCurrentLocationCoordinates();
            try {
                currentLatitude = coordinate.getLatitude();
                currentLongitude = coordinate.getLongitude();
                currentZoom = 8;
            } catch (NullPointerException e) {
                currentLongitude = 37.59;
                currentLatitude = 55.74;
                currentZoom = 3;
            }
            setCameraPosition(currentLatitude, currentLongitude, 8, 0, 0);
        }
    }

    public boolean setMap() {
        myFM = activity.getSupportFragmentManager();
        map = ((SupportMapFragment) myFM.findFragmentById(R.id.map)).getMap();
        return map != null;
    }

    public void setMapSettings() {
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(MyApplication.getAppContext(), MyApplication.getAppContext().getString(R.string.error_gps_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        map.setMyLocationEnabled(true);
    }

    public void setCameraPosition(double latitude, double longitude, float zoom, float bearing, float tilt) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .bearing(bearing)
                .tilt(tilt)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    public void setNewLatLng(double latitude, double longitude) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        map.animateCamera(cameraUpdate);
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public float getCurrentZoom() {
        return currentZoom;
    }

    public void setNewLatLngZoom(double latitude, double longitude, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom);
        map.animateCamera(cameraUpdate);
    }

    public void setNewZoom(float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(zoom);
        map.animateCamera(cameraUpdate);
    }

    public void deleteAllMarkers() {
        map.clear();
    }

    public void setNewMarker(double latitude, double longitude) {
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(true));
    }

    private void setMapClickListeners() {
        // Клик на маркер
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    setCameraPosition(marker.getPosition().latitude, marker.getPosition().longitude, 8, 0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        // Клик по карте
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    currentLatitude = latLng.latitude;
                    currentLongitude = latLng.longitude;
                    deleteAllMarkers();
                    setNewMarker(currentLatitude, currentLongitude);
                    activity.setLocationAddress(currentLatitude, currentLongitude);
                    Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Долгое нажатие на карту
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);
            }
        });

        // Смера камеры
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d(TAG, "onCameraChange: lat " + camera.target.latitude + ", lng " + camera.target.longitude + ", zoom " + camera.zoom);
            }
        });
    }
}
