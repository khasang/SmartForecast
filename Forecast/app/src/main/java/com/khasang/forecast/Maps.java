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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.khasang.forecast.activities.CityPickerActivity;
import com.khasang.forecast.exceptions.EmptyCurrentAddressException;
import com.khasang.forecast.interfaces.IMapDataReceiver;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.position.PositionManager;

/**
 * Класс предоставляет методы для работы с Google maps.
 *
 * @author maxim.kulikov
 */

public class Maps implements OnMapReadyCallback {

    private final String TAG = "mapLogs";
    private double currentLatitude;
    private double currentLongitude;
    private float defaultZoom;
    private IMapDataReceiver receiver;
    private boolean isMapInitialized;
    GoogleMap map;

    public Maps(CityPickerActivity activity, IMapDataReceiver receiver) {
        isMapInitialized = false;
        this.receiver = receiver;
        Coordinate coordinate = PositionManager.getInstance().getCurrentLocationCoordinates();
        try {
            currentLatitude = coordinate.getLatitude();
            currentLongitude = coordinate.getLongitude();
            defaultZoom = 8;
        } catch (NullPointerException e) {
            currentLongitude = 37.59;
            currentLatitude = 55.74;
            defaultZoom = 3;
        }
        ((SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public void closeDataReceiver() {
        receiver = null;
    }

    public void setMapSettings(GoogleMap map) {
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
        return map.getCameraPosition().target.latitude;
    }

    public double getCurrentLongitude() {
        return map.getCameraPosition().target.longitude;
    }

    public float getCurrentZoom() {
        return map.getCameraPosition().zoom;
    }

    public float getDefaultZoom() {
        return defaultZoom;
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
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(false));
    }

    public void setNewMarker(double latitude, double longitude, String text) {
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(text).draggable(false));
    }

    private void setMapClickListeners(final GoogleMap map) {
        // Клик на маркер
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    setCameraPosition(marker.getPosition().latitude, marker.getPosition().longitude, getCurrentZoom(), 0, 0);
                    marker.showInfoWindow();
                    receiver.setLocationNameFromMap(marker.getTitle());
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
                    String location = receiver.setLocationCoordinatesFromMap(currentLatitude, currentLongitude);
                    deleteAllMarkers();
                    setNewMarker(currentLatitude, currentLongitude, location);
                    Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);
                } catch (EmptyCurrentAddressException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        map = googleMap;
        if (!isMapInitialized) {
            setMapSettings(googleMap);
            setMapClickListeners(googleMap);
            isMapInitialized = true;
            setCameraPosition(currentLatitude, currentLongitude, defaultZoom, 0, 0);
        }
    }
}
