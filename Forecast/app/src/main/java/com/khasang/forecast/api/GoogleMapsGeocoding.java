package com.khasang.forecast.api;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.position.PositionManager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.fabric.sdk.android.Fabric;

/**
 * Created by roman on 31.05.16.
 */
public class GoogleMapsGeocoding {
    private final static String TAG = GoogleMapsGeocoding.class.getSimpleName();
    private final static String PLACE_API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private final static String API_KEY = MyApplication.getAppContext().getString(R.string.google_maps_geocoding);

    public void requestCoordinates(final String input) {
        try {
            final String URL = PLACE_API_BASE_URL + "?key="
                    + API_KEY + "&address="
                    + URLEncoder.encode(input, "utf8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.d(TAG, "Request to Google Maps Geocoding API failure");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        String status = jsonObject.getString("status");
                        if (!status.equals("OK")) {
                            String log = "GoogleMapsGeocoding: url <" + URL + ">  response status <" + status + ">";
                            throw new JSONException(log);
                        }
                        JSONObject jsonGeometry = new JSONObject(jsonArray.getJSONObject(0).getString("geometry"));
                        JSONObject jsonLocation = new JSONObject(jsonGeometry.getString("location"));
                        Coordinate coordinate = new Coordinate();
                        coordinate.setLatitude(Double.parseDouble(jsonLocation.getString("lat")));
                        coordinate.setLongitude(Double.parseDouble(jsonLocation.getString("lng")));
                        PositionManager.getInstance().updatePositionCoordinates(input, coordinate);

                        GoogleMapsTimezone googleMapsTimezone = new GoogleMapsTimezone();
                        googleMapsTimezone.requestCoordinates(input);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        if (Fabric.isInitialized()) {
                            Crashlytics.logException(e);
                        }
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
