package com.khasang.forecast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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

        RuntimePermissions(){
            VALUE = this.ordinal();
        }

        public abstract String toStringValue();

        public abstract String showInformationMessage();
    }

    public void checkForPermissions(Activity activity, final RuntimePermissions permission, final IPermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Do something for API 23 and above versions
            if (ActivityCompat.checkSelfPermission(activity, permission.toStringValue()) != PackageManager.PERMISSION_GRANTED) {  //check permission
                Log.d("PERMISSION", "checkForPermissions not granted");
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.toStringValue())) {
                    //  если он отклонил, объясняем зачем нужно
                    Log.d("PERMISSION", "checkForPermissions not granted – explanation ");

                    AlertDialog.Builder d = new AlertDialog.Builder(activity);
                    d.setMessage(permission.showInformationMessage());
                    d.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callback.permissionGranted(permission);
                        }
                    });
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.dismiss();
                            callback.permissionGranted(permission);
                        }
                    });
                    d.show();
                    callback.permissionDenied(permission);
                } else {
                    // запросить пермишн
                    Log.d("PERMISSION", "checkForPermissions not granted – did not ask ");
                    ActivityCompat.requestPermissions(activity, new String[]{permission.toStringValue()}, permission.ordinal());
                }

            } else {
                //пермишн одобрен
                Log.d("PERMISSION", "checkForPermissions granted");
                callback.permissionGranted(permission);
            }
        } else {
            // do something for phones running an SDK before
            callback.permissionGranted(permission);
        }
    }
}
