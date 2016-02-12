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
    private double currentLatitude;
    private double currentLongitude;
    private float currentZoom;
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
            setCameraPosition(currentLatitude, currentLongitude, currentZoom, 0, 0);
        }
    }

    public boolean setMap() {
        myFM = activity.getSupportFragmentManager();

//        Fragment frag = myFM.findFragmentById(R.id.map);
//        if (frag != null) {
//            myFM.beginTransaction().remove(myFM.findFragmentById(R.id.map)).commit();
//        }
//        map = ((SupportMapFragment) frag).getMap();

//        myFM.beginTransaction().remove(myFM.findFragmentById(R.id.map)).commit();

//        Fragment frag = myFM.findFragmentById(R.id.map);
//        if (frag == null) {
//            myFM.beginTransaction().add(R.id.frgmConteiner, myFM.findFragmentById(R.id.map), "Maps").commit();
//        }
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

    public void setCameraPosition(double latitude, double longtitude, float zoom, float bearing, float tilt) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longtitude))
                .zoom(zoom)
                .bearing(bearing)
                .tilt(tilt)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    public void setNewLatLng(double latitude, double longtitude) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(latitude, longtitude));
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

    public void setNewLatLngZoom(double latitude, double longtitude, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtitude), zoom);
        map.animateCamera(cameraUpdate);
    }

    public void setNewZoom(float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(zoom);
        map.animateCamera(cameraUpdate);
    }

    public void setMarker(double latitude, double longtitude) {
        map.clear();
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).draggable(true));
    }

    private void setMapClickListeners() {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                currentLatitude = latLng.latitude;
                currentLongitude = latLng.longitude;
                setMarker(currentLatitude, currentLongitude);
                activity.setLocationAddress(currentLatitude, currentLongitude);

//                final View view = activity.getLayoutInflater().inflate(R.layout.dialog_pick_location, null);
//                final DelayedAutoCompleteTextView chooseCity = (DelayedAutoCompleteTextView) view.findViewById(R.id.editTextCityName);
//                chooseCity.setText(activity.ConvertPointToLocation(currentLatitude, currentLongtitde));

                Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d(TAG, "onCameraChange: lat " + camera.target.latitude + ", lng " + camera.target.longitude + ", zoom " + camera.zoom);
            }
        });
    }
}
