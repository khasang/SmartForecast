package com.khasang.forecast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

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
import com.khasang.forecast.api.GoogleMapsGeocoding;
import com.khasang.forecast.exceptions.EmptyCurrentAddressException;
import com.khasang.forecast.interfaces.IMapDataReceiver;
import com.khasang.forecast.interfaces.IMessageProvider;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.position.Position;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.utils.AppUtils;

/**
 * Класс предоставляет методы для работы с Google maps.
 *
 * @author maxim.kulikov
 */

public class Maps implements OnMapReadyCallback {

    private double currentLatitude;
    private double currentLongitude;
    private float defaultZoom;
    private IMapDataReceiver receiver;
    private IMessageProvider messageProvider;
    GoogleMap map;

    public Maps(CityPickerActivity activity, IMapDataReceiver receiver, IMessageProvider messageProvider) {
        map = null;
        this.receiver = receiver;
        this.messageProvider = messageProvider;
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
        messageProvider = null;
    }

    public void setMapSettings(GoogleMap map) {
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                messageProvider.showToast(R.string.error_gps_permission);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return;
        }
        map.setMyLocationEnabled(true);
    }

    public void setCameraPosition(Coordinate coordinate, float zoom, float bearing, float tilt) {
        setCameraPosition(coordinate.getLatitude(), coordinate.getLongitude(), zoom, bearing, tilt);
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

    public void setNewMarker(Coordinate coordinate, String text) {
        setNewMarker(coordinate.getLatitude(), coordinate.getLongitude(), text);
    }

    public void setNewMarker(double latitude, double longitude) {
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(false));
    }

    public void setNewMarker(double latitude, double longitude, String text) {
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(text).draggable(false));
    }

    public void setNewMarker (String city) {
        if (!AppUtils.isNetworkAvailable(MyApplication.getAppContext())) {
            return;
        }
        CoordinateGetter coordinatedGetter = new CoordinateGetter();
        coordinatedGetter.execute(city);
    }

    private void setMapClickListeners(final GoogleMap map) {
        // Клик на маркер
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    setCameraPosition(marker.getPosition().latitude, marker.getPosition().longitude, getCurrentZoom(), 0, 0);
                    marker.showInfoWindow();
                    try {
                        receiver.setLocationNameFromMap(marker.getTitle());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
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
                } catch (EmptyCurrentAddressException | NullPointerException e) {
                    e.printStackTrace();
                    if (AppUtils.isNetworkAvailable(MyApplication.getAppContext())) {
                        LocationNameGetter locationNameGetter = new LocationNameGetter();
                        locationNameGetter.execute(latLng);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapSettings(googleMap);
        setMapClickListeners(googleMap);
        setCameraPosition(currentLatitude, currentLongitude, defaultZoom, 0, 0);
    }

    class CoordinateGetter extends AsyncTask<String, Void, Position> {


        @Override
        protected Position doInBackground(String... params) {
            GoogleMapsGeocoding googleMapsGeocoding = new GoogleMapsGeocoding();
            Coordinate coordinate = googleMapsGeocoding.requestCoordinatesSynch(params[0]);
            Position position = new Position();
            position.setLocationName(params[0]);
            position.setCoordinate(coordinate);
            return position;
        }

        @Override
        protected void onPostExecute(Position position) {
            super.onPostExecute(position);
            if (position.getCoordinate() != null) {
                deleteAllMarkers();
                setNewMarker(position.getCoordinate().getLatitude(), position.getCoordinate().getLongitude(),position.getLocationName());
                setCameraPosition(position.getCoordinate(),getDefaultZoom(),0,0);
            } else {
                messageProvider.showToast(R.string.invalid_lang_long_used);
            }
        }
    }

    class LocationNameGetter extends AsyncTask<LatLng, Void, Position> {


        @Override
        protected Position doInBackground(LatLng... params) {
            GoogleMapsGeocoding googleMapsGeocoding = new GoogleMapsGeocoding();
            String city = googleMapsGeocoding.requestLocationNameSynch(params[0]);
            Position position = new Position();
            position.setLocationName(city);
            Coordinate coordinate = new Coordinate(params[0].latitude, params[0].longitude);
            position.setCoordinate(coordinate);
            return position;
        }

        @Override
        protected void onPostExecute(Position position) {
            super.onPostExecute(position);
            receiver.setLocation(position.getLocationName());
            deleteAllMarkers();
            setNewMarker(position.getCoordinate().getLatitude(), position.getCoordinate().getLongitude());
            setCameraPosition(position.getCoordinate(),getDefaultZoom(),0,0);
        }
    }
}
