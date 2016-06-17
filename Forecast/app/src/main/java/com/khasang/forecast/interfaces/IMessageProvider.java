package com.khasang.forecast.interfaces;

import android.support.annotation.StringRes;

/**
 * Created by novoselov on 16.03.2016.
 */
public interface IMessageProvider {

    void showMessageToUser (@StringRes int stringId, int length);

    void showMessageToUser (CharSequence string, int length);

    void showToast (@StringRes int stringId);

    void showToast (CharSequence string);
}
