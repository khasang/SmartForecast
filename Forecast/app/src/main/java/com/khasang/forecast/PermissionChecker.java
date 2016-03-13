package com.khasang.forecast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

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

    public boolean isPermissionGranted(Activity activity, final RuntimePermissions permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(activity, permission.toStringValue()) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkForPermissions(Activity activity, final RuntimePermissions permission, final IPermissionCallback callback) {
        if (!isPermissionGranted(activity, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.toStringValue())) {
                AlertDialog.Builder d = new AlertDialog.Builder(activity);
                d.setMessage(permission.showInformationMessage());
                d.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                }
                d.show();
                callback.permissionDenied(permission);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission.toStringValue()}, permission.ordinal());
            }
        } else {
            callback.permissionGranted(permission);
        }
    }
}
