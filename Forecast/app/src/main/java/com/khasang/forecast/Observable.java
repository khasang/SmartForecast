package com.khasang.forecast;

import java.util.Date;

/**
 * Created by novoselov on 24.11.2015.
 */
public interface Observable {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObserver (Observer observer, Date date, Weather weather);
}
