package com.khasang.forecast;

import java.util.Date;

/**
 * Created by Veda on 24.11.15.
 */
public interface Observable {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObserver(Observer observer, Date date, Weather weather);
}
