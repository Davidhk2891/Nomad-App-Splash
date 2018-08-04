package com.nomadapp.splash.model.localdatastorage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by David on 7/30/2018 for Splash.
 */
public class StoragePermission {
    public static boolean isStoragePermissionGranted(Context context,Activity act) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission. READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission","Permission is granted, press again");
                return true;
            } else {

                Log.i("permission","Permission is revoked");
                ActivityCompat.requestPermissions(act,
                        new String[]{android.Manifest.permission. READ_EXTERNAL_STORAGE},
                        1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.i("permission","Permission is granted, press again");
            return true;
        }
    }
    public static boolean isWrittingToStoragePermissionGranted(Context context, Activity act) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission. WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission","Permission is granted");
                return true;
            } else {

                Log.i("permission","Permission is revoked");
                ActivityCompat.requestPermissions(act,
                        new String[]{Manifest.permission. WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.i("permission","Permission is granted");
            return true;
        }
    }
}
