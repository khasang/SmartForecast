package com.khasang.forecast.api;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.maps.model.LatLng;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.interfaces.ICoordinateReceiver;
import com.khasang.forecast.interfaces.ILocationNameReceiver;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.utils.AppUtils;
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
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by roman on 31.05.16.
 */
public class GoogleMapsGeocoding {
    private final static String TAG = GoogleMapsGeocoding.class.getSimpleName();
    private final static String PLACE_API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private final static String API_KEY = MyApplication.getAppContext().getString(R.string.google_maps_geocoding);
    private final static String API = "Google Maps Geocoding API";

    public void requestCoordinates(final String input, final ICoordinateReceiver receiver, final boolean updateTimezone) {
        if (!AppUtils.isNetworkAvailable(MyApplication.getAppContext())) {
            return;
        }
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
//                    AppUtils.showInfoMessage(MyApplication.getAppContext().getString(R.string.invalid_lang_long_used)).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    String status = "";
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        status = jsonObject.getString("status");
                        if (!status.equals("OK")) {
                            String log = "GoogleMapsGeocoding Async: url <" + URL + ">; response status: <" + status + ">";
                            throw new JSONException(log);
                        }
                        JSONObject jsonGeometry = new JSONObject(jsonArray.getJSONObject(0).getString("geometry"));
                        JSONObject jsonLocation = new JSONObject(jsonGeometry.getString("location"));
                        Coordinate coordinate = new Coordinate();
                        coordinate.setLatitude(Double.parseDouble(jsonLocation.getString("lat")));
                        coordinate.setLongitude(Double.parseDouble(jsonLocation.getString("lng")));
                        receiver.updatePositionCoordinate(input, coordinate);
                        if (updateTimezone) {
                            GoogleMapsTimezone googleMapsTimezone = new GoogleMapsTimezone();
                            googleMapsTimezone.requestCoordinates(input);
                        }
                    } catch (JSONException e) {
//                        AppUtils.showInfoMessage(MyApplication.getAppContext().getString(R.string.invalid_lang_long_used)).show();
                        if (Fabric.isInitialized()) {
                            Crashlytics.logException(e);
                            Answers.getInstance().logCustom(new CustomEvent(AppUtils.ApiCustomEvent)
                                    .putCustomAttribute(API, status));
                        }
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
//            AppUtils.showInfoMessage(MyApplication.getAppContext().getString(R.string.invalid_lang_long_used)).show();
        }
    }

    public void requestLocationName(final double latitude, final double longitude, final ILocationNameReceiver receiver) {
        if (!AppUtils.isNetworkAvailable(MyApplication.getAppContext())) {
            return;
        }
        try {
            final String systemLanguage = Locale.getDefault().getLanguage();
            final String locationCoordinate = String.valueOf(latitude) + "," + String.valueOf(longitude);
            final String resultType = URLEncoder.encode("administrative_area_level_2|administrative_area_level_1|country", "utf8");
            final String URL = PLACE_API_BASE_URL
                    + "?key=" + API_KEY
                    + "&language=" + systemLanguage
                    + "&result_type=" + resultType
                    + "&latlng=" + URLEncoder.encode(locationCoordinate, "utf8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
//                    AppUtils.showInfoMessage(MyApplication.getAppContext().getString(R.string.no_address_found)).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    String status = "";
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        status = jsonObject.getString("status");
                        if (!status.equals("OK")) {
                            String log = "GoogleMapsReverseGeocoding Async: url <" + URL + ">; response status: <" + status + ">";
                            throw new JSONException(log);
                        }
                        receiver.updateLocation(jsonArray.getJSONObject(0).getString("formatted_address"), new Coordinate(latitude, longitude));
                    } catch (JSONException e) {
//                        AppUtils.showInfoMessage(MyApplication.getAppContext().getString(R.string.no_address_found)).show();
                        if (Fabric.isInitialized()) {
                            Crashlytics.logException(e);
                            Answers.getInstance().logCustom(new CustomEvent(AppUtils.ApiCustomEvent)
                                    .putCustomAttribute(API, status));
                        }
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Coordinate requestCoordinatesSynch(final String input) {
        Coordinate coordinate = null;
        String status = "";
        try {
            final String URL = PLACE_API_BASE_URL + "?key="
                    + API_KEY + "&address="
                    + URLEncoder.encode(input, "utf8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            status = jsonObject.getString("status");
            if (!status.equals("OK")) {
                String log = "GoogleMapsGeocoding: url <" + URL + ">; response status <" + status + ">";
                throw new JSONException(log);
            }
            JSONObject jsonGeometry = new JSONObject(jsonArray.getJSONObject(0).getString("geometry"));
            JSONObject jsonLocation = new JSONObject(jsonGeometry.getString("location"));
            coordinate = new Coordinate();
            coordinate.setLatitude(Double.parseDouble(jsonLocation.getString("lat")));
            coordinate.setLongitude(Double.parseDouble(jsonLocation.getString("lng")));
        } catch (JSONException e) {
            if (Fabric.isInitialized()) {
                Crashlytics.logException(e);
                Answers.getInstance().logCustom(new CustomEvent(AppUtils.ApiCustomEvent)
                        .putCustomAttribute(API, status));
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return coordinate;
    }

    public String requestLocationNameSynch(final LatLng coordinates) {
        String status = "";
        try {
            final String systemLanguage = Locale.getDefault().getLanguage();
            final String locationCoordinate = String.valueOf(coordinates.latitude) + "," + String.valueOf(coordinates.longitude);
            final String resultType = URLEncoder.encode("administrative_area_level_2|administrative_area_level_1|country", "utf8");
            final String URL = PLACE_API_BASE_URL
                    + "?key=" + API_KEY
                    + "&language=" + systemLanguage
                    + "&result_type=" + resultType
                    + "&latlng=" + URLEncoder.encode(locationCoordinate, "utf8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            status = jsonObject.getString("status");
            if (!status.equals("OK")) {
                String log = "GoogleMapsReverseGeocoding: url <" + URL + ">; response status <" + status + ">";
                throw new JSONException(log);
            }
            return jsonArray.getJSONObject(0).getString("formatted_address");
        } catch (JSONException e) {
            if (Fabric.isInitialized()) {
                Crashlytics.logException(e);
                Answers.getInstance().logCustom(new CustomEvent(AppUtils.ApiCustomEvent)
                        .putCustomAttribute(API, status));
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
