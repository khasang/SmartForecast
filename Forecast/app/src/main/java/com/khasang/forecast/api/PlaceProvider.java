package com.khasang.forecast.api;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.utils.AppUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xsobolx on 12.12.2015.
 */
public class PlaceProvider {
    private final static String TAG = PlaceProvider.class.getSimpleName();
    private final static String PLACE_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
    private final static String API_KEY = MyApplication.getAppContext().getString(R.string.place_provider_key);
    private final static String API = "Google Maps Place API";

    public ArrayList<String> autocomplete(String input, int maxResult) {
        ArrayList<String> resultList;
        String status = "";
        try {
            String URL = PLACE_API_BASE_URL + "?key="
                    + API_KEY + "&input="
                    + URLEncoder.encode(input, "utf8")
                    + "&types=(cities)";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            status = jsonObject.getString("status");
            if (!status.equals("OK")) {
                String log = "GooglePlaceAPI: url <" + URL + ">; response status <" + status + ">";
                throw new JSONException(log);
            }
            JSONArray jsonArray = jsonObject.getJSONArray("predictions");
            int size = (maxResult <= jsonArray.length()) ? maxResult : jsonArray.length();
            resultList = new ArrayList<String>(jsonArray.length());
            for (int i = 0; i < size; i++) {
                resultList.add(jsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            resultList = null;
            if (Fabric.isInitialized()) {
                Crashlytics.logException(e);
                Answers.getInstance().logCustom(new CustomEvent(AppUtils.ApiCustomEvent)
                        .putCustomAttribute(API, status));
            }

        }
        return resultList;
    }
}
