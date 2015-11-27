package com.khasang.forecast;

import android.util.Log;

import com.khasang.forecast.models.DailyResponse;
import com.khasang.forecast.models.OpenWeatherMapResponse;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.okhttp.logging.HttpLoggingInterceptor.Level;

import java.io.IOException;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * This class downloads and parses API data on a background thread and deliver the results
 * to the UI thread via the onResponse or onFailure method. *
 */

public class OpenWeatherMap  extends WeatherStation {

    //TAG for debugging
    private static final String TAG = OpenWeatherMap.class.getSimpleName();

    //API URL.
    private static final String API_BASE_URL = "http://api.openweathermap.org";

    //API key.
    private static final String APP_ID = "96dd81a807540894eb4c96c05f17ed01";

    //Current language
    public static final String SYSTEM_LANGUAGE = Locale.getDefault().getLanguage();

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
    Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build();

    //Creating a service from interface with defined endpoints.
    OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);

    private void addInterceptors() {
        logging.setLevel(Level.BODY);
        client.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.httpUrl().newBuilder()
                        .addQueryParameter("lang", SYSTEM_LANGUAGE)
                        .addQueryParameter("appid", APP_ID)
                        .build();
                request = request.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        });
        //client.interceptors().add(logging);
    }

    //Get current weather data asynchronously.
    @Override
    public void updateWeather(Coordinate coordinate, PositionManager manager) {
        addInterceptors();

        Call<OpenWeatherMapResponse> call = service.getCurrent(55.7796551, 37.7125017);

        call.enqueue(new Callback<OpenWeatherMapResponse>() {
            @Override
            public void onResponse(Response<OpenWeatherMapResponse> response, Retrofit retrofit) {
                //TODO handle execution success.
                /*Log.d(TAG, "updateWeather, onResponse: " + response.body().toString());
                Weather weather = AppUtils.convertToWeather(response.body());
                Log.d(TAG, "updateWeather: " + weather.toString());*/


            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
                Log.e(TAG, "updateWeather, onFailure: ", t);
            }
        });
    }

    //Get 24-hour forecast asynchronously.
    @Override
    public void updateHourlyWeather(Coordinate coordinate, PositionManager manager) {
        addInterceptors();
        Call<OpenWeatherMapResponse> call = service.getHourly(55.7796551, 37.7125017, TIME_PERIOD);
        call.enqueue(new Callback<OpenWeatherMapResponse>() {
            @Override
            public void onResponse(Response<OpenWeatherMapResponse> response, Retrofit retrofit) {
                //TODO handle execution success.
                /*Log.d(TAG, "updateHourlyWeather, onResponse: " + response.body().toString());
                Map<Calendar, Weather> map = AppUtils.convertToHourlyWeather(response.body());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                for (Map.Entry<Calendar, Weather> m : map.entrySet()) {
                    Log.d(TAG, "updateHourlyWeather: " + dateFormat.format(m.getKey().getTime()) + " Weather: " + m.getValue());
                }*/

            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
                Log.e(TAG, "updateHourlyWeather, onFailure: ", t);
            }
        });
    }

    //Get 7 day forecast asynchronously.
    @Override
    public void updateWeeklyWeather(Coordinate coordinate, PositionManager manager) {
        addInterceptors();
        Call<DailyResponse> call = service.getDaily(55.7796551, 37.7125017, DAYS_PERIOD);
        call.enqueue(new Callback<DailyResponse>() {
            @Override
            public void onResponse(Response<DailyResponse> response, Retrofit retrofit) {
                //TODO handle execution success.
                /*Log.d(TAG, "updateWeeklyWeather, onResponse: " + response.body().toString());
                Map<Calendar, Weather> map = AppUtils.convertToDailyWeather(response.body());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                for (Map.Entry<Calendar, Weather> m : map.entrySet()) {
                    Log.d(TAG, "updateWeeklyWeather: " + dateFormat.format(m.getKey().getTime()) + " Weather: " + m.getValue());
                }*/
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
                Log.e(TAG, "updateWeeklyWeather, onFailure: ", t);
            }
        });
    }
}
