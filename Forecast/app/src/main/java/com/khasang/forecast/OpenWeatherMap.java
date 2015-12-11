package com.khasang.forecast;

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

    /** Тэг для дебаггинга. */
    private static final String TAG = OpenWeatherMap.class.getSimpleName();

    /** API URL. */
    private static final String API_BASE_URL = "http://api.openweathermap.org";

    /** API ключ. */
    private static final String APP_ID = "96dd81a807540894eb4c96c05f17ed01";

    /**
     * Выбранные пользователем языковые настройки устройства для того, чтобы получить
     * локализованный ответ от API.
     */
    private String systemLanguage = Locale.getDefault().getLanguage();

    /** Количество 3-х часовых интервалов для запроса к API. */
    private static final int TIME_PERIOD = 8;

    /** Количество дней для запроса к API. */
    private static final int DAYS_PERIOD = 7;

    /**
     * Нам необходимо вручную создать экземпляр объекта HttpLoggingInterceptor и OkHttpClient,
     * потому что Retrofit версии 2.0.0-beta2 использует их старые версии, в которых еще нет
     * некоторых нужных нам возможностей. Например, получения полного тела запроса.
     */
    private HttpLoggingInterceptor logging;
    private OkHttpClient client;

    /**
     * Создаем сервис из интерфейса {@link OpenWeatherMapService} с заданными конечными точками
     * для запроса.
     */
    private OpenWeatherMapService service;

    /**
     * Метод, который добавляет постоянно-используемые параметры к нашему запросу, а так же
     * устанавливает уровень логирования.
     */
    private void addInterceptors() {
        logging.setLevel(Level.BASIC);
        client.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.httpUrl().newBuilder()
                        .addQueryParameter("lang", systemLanguage)
                        .addQueryParameter("appid", APP_ID)
                        .build();
                request = request.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        });
        client.interceptors().add(logging);
    }

    /** Конструктор. */
    public OpenWeatherMap() {
        logging = new HttpLoggingInterceptor();
        client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        service = retrofit.create(OpenWeatherMapService.class);
        addInterceptors();
    }

    /**
     * Метод для асинхронного получения текущего прогноза погоды.
     * @param cityID внутренний идентификатор города.
     * @param coordinate объект типа {@link Coordinate}, содержащий географические координаты
     *                   для запроса.
     */
    @Override
    public void updateWeather(final int cityID, final Coordinate coordinate) {
        Call<OpenWeatherMapResponse> call = service.getCurrent(coordinate.getLatitude(),
                coordinate.getLongitude());
        call.enqueue(new Callback<OpenWeatherMapResponse>() {
            @Override
            public void onResponse(Response<OpenWeatherMapResponse> response, Retrofit retrofit) {
                PositionManager.getInstance().onResponseReceived(
                        ResponseType.CURRENT,
                        cityID,
                        serviceType,
                        AppUtils.convertToWeather(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
                PositionManager.getInstance().onFailureResponse(cityID, getWeatherStationName(), getServiceType());
                //Log.e(TAG, "updateWeather, onFailure: ", t);
            }
        });
    }

    /**
     * Метод для асинхронного получения прогноза погоды с заданым количеством 3-х часовых
     * интервалов.
     * @param cityID внутренний идентификатор города.
     * @param coordinate объект типа {@link Coordinate}, содержащий географические координаты
     *                   для запроса.
     */
    @Override
    public void updateHourlyWeather(final int cityID, final Coordinate coordinate) {
        Call<OpenWeatherMapResponse> call = service.getHourly(coordinate.getLatitude(),
                coordinate.getLongitude(),
                TIME_PERIOD);
        call.enqueue(new Callback<OpenWeatherMapResponse>() {
            @Override
            public void onResponse(Response<OpenWeatherMapResponse> response, Retrofit retrofit) {
                PositionManager.getInstance().onResponseReceived(
                        ResponseType.HOURLY,
                        cityID,
                        serviceType,
                        AppUtils.convertToHourlyWeather(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
                PositionManager.getInstance().onFailureResponse(cityID, getWeatherStationName(), getServiceType());
                //Log.e(TAG, "updateHourlyWeather, onFailure: ", t);
            }
        });
    }

    /**
     * Метод для асинхронного получения прогноза погоды с заданным количеством дней.
     * @param cityID внутренний идентификатор города.
     * @param coordinate объект типа {@link Coordinate}, содержащий географические координаты
     *                   для запроса.
     */
    @Override
    public void updateWeeklyWeather(final int cityID, final Coordinate coordinate) {
        Call<DailyResponse> call = service.getDaily(coordinate.getLatitude(),
                coordinate.getLongitude(),
                DAYS_PERIOD);
        call.enqueue(new Callback<DailyResponse>() {
            @Override
            public void onResponse(Response<DailyResponse> response, Retrofit retrofit) {
                PositionManager.getInstance().onResponseReceived(
                        ResponseType.DAILY,
                        cityID,
                        serviceType,
                        AppUtils.convertToDailyWeather(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
                PositionManager.getInstance().onFailureResponse(cityID, getWeatherStationName(), getServiceType());
                //Log.e(TAG, "updateWeeklyWeather, onFailure: ", t);
            }
        });
    }
}
