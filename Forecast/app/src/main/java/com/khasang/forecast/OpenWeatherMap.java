package com.khasang.forecast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;



public class OpenWeatherMap {

    //API URL
    private static final String API_BASE_URL = "api.openweathermap.org/data/2.5";

    //API key
    private static final String APP_ID = "Add APP ID HERE PLZ";

    private Coordinate mCoordinate;

    //Creating the Retrofit instance.
    Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();

    //Creating a service from interface with defined endpoints.
    OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);

    //Get current weather data asynchronously.
    public void getCurrent() {
        Call<Weather> call = service.getCurrent(APP_ID, mCoordinate.getLat(), mCoordinate.getLon());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //TODO handle execution failure.
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //TODO handle execution success.
            }
        });
    }

    //Get 24-hour forecast asynchronously.
    public void getHourly() {
        Call<List<Weather>> call = service.getHourly(APP_ID, mCoordinate.getLat(),
                                    mCoordinate.getLon(), 8);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //TODO handle execution failure.
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //TODO handle execution success.
            }
        });
    }

    //Get 7 day forecast asynchronously.
    public void getWeekly() {
        Call<List<Weather>> call = service.getWeekly(APP_ID, mCoordinate.getLat(),
                mCoordinate.getLon(), 7);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //TODO handle execution failure.
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //TODO handle execution success.
            }
        });
    }
}
