package com.khasang.forecast.models;

import java.util.ArrayList;
import java.util.List;

public class DailyResponse {

    private City city;
    private String cod;
    private Double message;
    private Integer cnt;
    private List<DailyForecastList> list = new ArrayList<>();

    public City getCity() {
        return city;
    }

    public String getCod() {
        return cod;
    }

    public Double getMessage() {
        return message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public List<DailyForecastList> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "DailyResponse{" +
                "city=" + city +
                ", cod='" + cod + '\'' +
                ", message=" + message +
                ", cnt=" + cnt +
                ", list=" + list +
                '}';
    }
}
