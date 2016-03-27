package com.khasang.forecast;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.khasang.forecast.interfaces.IPermissionCallback;

/**
 * Created by roman on 09.03.16.
 */
public class PermissionChecker {

    public enum RuntimePermissions {
        PERMISSION_REQUEST_FINE_LOCATION {
            @Override
            public String toStringValue() {
                return android.Manifest.permission.ACCESS_FINE_LOCATION;
            }

            @Override
            public String showInformationMessage() {
                return MyApplication.getAppContext().getString(R.string.info_message_about_request_permission_fine_location);
            }
        };
        public final int VALUE;

        RuntimePermissions() {
            VALUE = this.ordinal();
        }

        public abstract String toStringValue();

        public abstract String showInformationMessage();
    }

    public boolean isPermissionGranted(Context context, final RuntimePermissions permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(context, permission.toStringValue()) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkForPermissions(Activity activity, final RuntimePermissions permission, final IPermissionCallback callback) {
        if (!isPermissionGranted(MyApplication.getAppContext(), permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.toStringValue())) {
                Toast toast = AppUtils.showInfoMessage(activity, permission.showInformationMessage());
                toast.getView().setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.background_toast));
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
                callback.permissionDenied(permission);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission.toStringValue()}, permission.ordinal());
            }
        } else {
            callback.permissionGranted(permission);
        }
    }
}
