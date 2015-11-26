package com.khasang.forecast;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.squareup.okhttp.logging.HttpLoggingInterceptor.Level;

/**
 *This class downloads and parses API data on a background thread and deliver the results
 * to the UI thread via the onResponse or onFailure method. *
 */

public class OpenWeatherMap extends WeatherStation {

    //TAG for debugging
    private static final String TAG = OpenWeatherMap.class.getSimpleName();

    //API URL.
    private static final String API_BASE_URL = "http://api.openweathermap.org";

    //API key.
    private static final String APP_ID = "96dd81a807540894eb4c96c05f17ed01";

    //24 hours in 3-hour interval.
    private static final int TIME_PERIOD = 8;

    //1 week in days interval.
    private static final int DAYS_PERIOD = 7;

    /**
     * We need to manually define logging interceptor because Retrofit 2.0.0-beta2 still uses
     * OkHttp 2.5.0, which doesn't have it.
     */
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    OkHttpClient client = new OkHttpClient();

    //Creating the Retrofit instance.
    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build();

    //Creating a service from interface with defined endpoints.
    OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);

    //Get current weather data asynchronously.
    @Override
    public void updateWeather(ILocation loc) {
        logging.setLevel(Level.BODY);
        client.interceptors().add(logging);

        Call<OpenWeatherMapResponse> call = service.getCurrent(APP_ID, 55.7796551, 37.7125017
                );

        call.enqueue(new Callback<OpenWeatherMapResponse>() {
            @Override
            public void onResponse(Response<OpenWeatherMapResponse> response, Retrofit retrofit) {
                //TODO handle execution success.
                Log.d(TAG, "onResponse: " + response.body().getMain().getTemperature());
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
            }
        });
    }

    //Get 24-hour forecast asynchronously.
    @Override
    public void updateHourlyWeather(ILocation loc) {
        Call<List<Weather>> call = service.getHourly(APP_ID, loc.getPosition().getLatitude(),
                                    loc.getPosition().getLongitude(), TIME_PERIOD);
        call.enqueue(new Callback<List<Weather>>() {
            @Override
            public void onResponse(Response<List<Weather>> response, Retrofit retrofit) {
                //TODO handle execution success.

            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
            }
        });
    }

    //Get 7 day forecast asynchronously.
    @Override
    void updateWeeklyWeather(ILocation loc) {
        Call<List<Weather>> call = service.getWeekly(APP_ID, loc.getPosition().getLatitude(),
                                    loc.getPosition().getLongitude(), DAYS_PERIOD);
        call.enqueue(new Callback<List<Weather>>() {
            @Override
            public void onResponse(Response<List<Weather>> response, Retrofit retrofit) {
                //TODO handle execution success.
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
            }
        });
    }
}
