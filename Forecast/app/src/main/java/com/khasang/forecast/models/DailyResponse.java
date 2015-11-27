package com.khasang.forecast.models;

import java.util.ArrayList;
import java.util.List;

public class DailyResponse {

    public City city;
    public String cod;
    public Double message;
    public Integer cnt;
    public List<WeeklyForecastList> list = new ArrayList<WeeklyForecastList>();

}
