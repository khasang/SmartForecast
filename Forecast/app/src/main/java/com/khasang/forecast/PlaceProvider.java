package com.khasang.forecast;


import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

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
import java.util.ArrayList;

/**
 * Created by xsobolx on 12.12.2015.
 */
public class PlaceProvider {
    private final static String TAG = PlaceProvider.class.getSimpleName();
    private final static String PLACE_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
    private final static String API_KEY = "AIzaSyBSW2y4MeIl9qkmTXVWy27VTLieAytOWYw";

    public static ArrayList autocomplete(String input){
        final ArrayList resultList = null;
        try {
            String URL = PLACE_API_BASE_URL + "?key="
                    + API_KEY + "&input=" + URLEncoder.encode(input, "utf8");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.d(TAG, "Request to Google Place API failure");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            resultList.add(jsonArray.getJSONObject(i).getString("description"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return resultList;
    }

}
