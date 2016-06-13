package com.khasang.forecast.api;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.khasang.forecast.AppUtils;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.position.Position;
import com.khasang.forecast.position.PositionManager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.fabric.sdk.android.Fabric;

/**
 * Created by roman on 31.05.16.
 */
public class GoogleMapsTimezone {
    private final static String TAG = GoogleMapsTimezone.class.getSimpleName();
    private final static String PLACE_API_BASE_URL = "https://maps.googleapis.com/maps/api/timezone/json";
    private final static String API_KEY = MyApplication.getAppContext().getString(R.string.google_maps_timezone);
    private final static String API = "Google Maps Timezone API";

    public void requestCoordinates(final String cityName) {
        final Position position = PositionManager.getInstance().getPosition(cityName);
        if ((position == null) || (position.getCoordinate() == null) || (position.getCoordinate().getLatitude() == 0 && position.getCoordinate().getLongitude() == 0)) {
            return;
        }
        requestCoordinates(position);
    }

    public void requestCoordinates(final Position position) {
        try {
            final String URL = PLACE_API_BASE_URL + "?key="
                    + API_KEY + "&timestamp=0&location="
                    + URLEncoder.encode(position.getCoordinate().convertToTimezoneUrlParameterString(), "utf8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.d(TAG, "Request to Google Maps Timezone API failure");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    String status = "";
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        status = jsonObject.getString("status");
                        if (!status.equals("OK")) {
                            String log = "GoogleMapsTimezone: url <" + URL + ">; response status <" + status + ">";
                            throw new JSONException(log);
                        }
                        int timeZoneId = jsonObject.getInt("rawOffset");
                        Log.d("TimeZone", "TimeZone: " + timeZoneId);
                        PositionManager.getInstance().updatePositionTimeZone(position, timeZoneId);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        if (Fabric.isInitialized()) {
                            Crashlytics.logException(e);
                            Answers.getInstance().logCustom(new CustomEvent(AppUtils.ApiCustomEvent)
                                    .putCustomAttribute(API, status));
                        }
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
