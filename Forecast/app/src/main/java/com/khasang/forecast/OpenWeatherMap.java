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
 * Этот класс скачивает и парсит данные с API в фоновом потоке, после чего отправляет их
 * на UI поток через методы onResponse или onFailure.
 */

public class OpenWeatherMap extends WeatherStation {

    /**
     * Тэг для дебаггинга.
     */
    private static final String TAG = OpenWeatherMap.class.getSimpleName();

    /**
     * API URL.
     */
    private static final String API_BASE_URL = "http://api.openweathermap.org";

    /**
     * API ключ.
     */
    private static final String APP_ID = "96dd81a807540894eb4c96c05f17ed01";

    /**
     * Выбранные пользователем языковые настройки устройства для того, чтобы получить
     * локализованный ответ от API.
     */
    public static final String SYSTEM_LANGUAGE = Locale.getDefault().getLanguage();

    /**
     * Количество 3-х часовых интервалов для запроса к API.
     */
    private static final int TIME_PERIOD = 8;

    /**
     * Количество дней для запроса к API.
     */
    private static final int DAYS_PERIOD = 7;

    /**
     * Нам необходимо вручную создать экземпляр объекта HttpLoggingInterceptor и OkHttpClient,
     * потому что Retrofit версии 2.0.0-beta2 использует их старые версии, в которых еще нет
     * некоторых нужных нам возможностей. Например, получения полного тела запроса.
     */
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    OkHttpClient client = new OkHttpClient();

    /**
     * Создаем экземпляр объекта Retrofit, подключая конвертер GSON и созданный нами OkHttpClient.
     */
    Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build();

    /**
     * Создаем сервис из интерфейса {@link OpenWeatherMapService} с заданными конечными точками
     * для запроса.
     */
    OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);

    /**
     * Метод, который добавляет постоянно-используемые параметры к нашему запросу, а так же
     * устанавливает уровень логирования.
     */
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
        client.interceptors().add(logging);
    }

    /**
     * Метод для асинхронного получения текущего прогноза погоды.
     *
     * @param coordinate объект типа {@link Coordinate}, содержащий географические координаты
     *                   для запроса.
     * @param manager    //TODO
     */
    @Override
    public void updateWeather(final Coordinate coordinate, final PositionManager manager) {
        addInterceptors();
        Call<OpenWeatherMapResponse> call = service.getCurrent(coordinate.getLatitude(),
                                                                coordinate.getLongitude());
        call.enqueue(new Callback<OpenWeatherMapResponse>() {
            @Override
            public void onResponse(Response<OpenWeatherMapResponse> response, Retrofit retrofit) {
                manager.onResponseReceived(coordinate, AppUtils.convertToWeather(response.body()));
                /*for (Map.Entry<Calendar, Weather> m : AppUtils.convertToWeather(response.body()).entrySet()) {
                    Log.d(TAG, "onResponse: " + m.getValue().toString());
                }*/
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
                Log.e(TAG, "updateWeather, onFailure: ", t);
            }
        });
    }

    /**
     * Метод для асинхронного получения прогноза погоды с заданым количеством 3-х часовых
     * интервалов.
     *
     * @param coordinate объект типа {@link Coordinate}, содержащий географические координаты
     *                   для запроса.
     * @param manager    //TODO
     */
    @Override
    public void updateHourlyWeather(final Coordinate coordinate, final PositionManager manager) {
        addInterceptors();
        Call<OpenWeatherMapResponse> call = service.getHourly(coordinate.getLatitude(),
                                                                coordinate.getLongitude(),
                                                                TIME_PERIOD);
        call.enqueue(new Callback<OpenWeatherMapResponse>() {
            @Override
            public void onResponse(Response<OpenWeatherMapResponse> response, Retrofit retrofit) {
                manager.onResponseReceived(coordinate,
                                            AppUtils.convertToHourlyWeather(response.body()));
                /*for (Map.Entry<Calendar, Weather> m : AppUtils.convertToHourlyWeather(response.body()).entrySet()) {
                    Log.d(TAG, "onResponse: " + m.getValue().toString());
                }*/
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle execution failure.
                Log.e(TAG, "updateHourlyWeather, onFailure: ", t);
            }
        });
    }

    /**
     * Метод для асинхронного получения прогноза погоды с заданным количеством дней.
     *
     * @param coordinate объект типа {@link Coordinate}, содержащий географические координаты
     *                   для запроса.
     * @param manager    //TODO
     */
    @Override
    public void updateWeeklyWeather(final Coordinate coordinate, final PositionManager manager) {
        addInterceptors();
        Call<DailyResponse> call = service.getDaily(coordinate.getLatitude(),
                                                    coordinate.getLongitude(),
                                                    DAYS_PERIOD);
        call.enqueue(new Callback<DailyResponse>() {
            @Override
            public void onResponse(Response<DailyResponse> response, Retrofit retrofit) {
                manager.onResponseReceived(coordinate,
                                            AppUtils.convertToDailyWeather(response.body()));
                /*for (Map.Entry<Calendar, Weather> m : AppUtils.convertToDailyWeather(response.body()).entrySet()) {
                    Log.d(TAG, "onResponse: " + m.getValue().toString());
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
