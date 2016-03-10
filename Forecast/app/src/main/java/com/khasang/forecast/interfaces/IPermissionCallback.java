package com.khasang.forecast.interfaces;

import com.khasang.forecast.PermissionChecker;

/**
 * Created by roman on 09.03.16.
 */
public interface IPermissionCallback {
    void permissionGranted(PermissionChecker.RuntimePermissions permission);

    void permissionDenied(PermissionChecker.RuntimePermissions permission);
}
