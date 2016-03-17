package com.khasang.forecast.interfaces;

/**
 * Created by novoselov on 16.03.2016.
 */
public interface IMessageProvider {
    void showMessageToUser (CharSequence string, int length);

    void showMessageToUser (int stringId, int length);

    void showToast (int stringId);

    void showToast (CharSequence string);
}
