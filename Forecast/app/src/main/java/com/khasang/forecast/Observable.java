package com.khasang.forecast;

/**
 * Created by novoselov on 24.11.2015.
 */
public interface Observable {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObserver ();
}
