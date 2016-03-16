package com.khasang.forecast.position;

/**
 *
 */

public class Weather {

    private double temperature;

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    private double temp_min;
    private double temp_max;
    private double pressure;
    private int humidity;
    private Wind wind;
    private Precipitation precipitation;
    private String description;
    private long sunrise;
    private long sunset;

    public Weather () {

    }

    public Weather(double temperature, double pressure, int humidity,
                   Wind wind, Precipitation precipitation) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind = wind;
        this.precipitation = precipitation;
    }

    public Weather(double temperature, double temp_min, double temp_max,
                   double pressure, int humidity, Wind wind, Precipitation precipitation,
                   String description) {
        this(temperature, pressure, humidity, wind, precipitation);
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.description = description;
    }

    /**
     * Получение температуры из обьекта
     * @return Метод возвращает температуру
     */
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * Получение давления из обьекта
     * @return Метод возвращает давление
     */
    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    /**
     * Получение влажность из обьекта
     * @return Метод возвращает влажность
     */
    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setWind(Wind.Direction wd, double wp) {
        if (this.wind == null) {
            this.wind = new Wind(wd, wp);
        } else {
            wind.setDirection(wd);
            wind.setSpeed(wp);
        }
    }

    /**
     * Получение из обьекта направления ветра
     * @return Метод возвращает значение {@link Wind.Direction}
     */
    public Wind.Direction getWindDirection() {
        return wind.getDirection();
    }

    /**
     * Получение из обьекта скорости ветра
     * @return Метод возвращает скорость ветра
     */
    public double getWindPower() {
        return wind.getSpeed();
    }

    public void setPrecipitation(Precipitation.Type type) {
        if (this.precipitation == null) {
            this.precipitation = new Precipitation(type);
        } else {
            this.precipitation.setType(type);
            //this.precipitation.setProbability(probability);
        }
    }

    /**
     * Получение из обьекта типа погоды
     * @return Метод возвращает значение {@link Precipitation.Type}
     */
    public Precipitation.Type getPrecipitation() {
        return precipitation.getType();
    }

    /**
     * Получение из вероятности установления погоды
     * @return Метод возвращает вероятности установления погоды
     */
    /*public int getPrecipitationProbability() {
        return precipitation.getProbability();
    }*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temperature=" + temperature +
                ", temp_min=" + temp_min +
                ", temp_max=" + temp_max +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", wind=" + wind +
                ", precipitation=" + precipitation +
                ", description='" + description + '\'' +
                '}';
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }
}
