package com.example.evalee.smsreceiver.activitys.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by EvaLee on 2016/9/6.
 * 20160907 added by Eva_Lee for android M permission issue
 */
public class PermissionActivity extends Activity {
    private static final String TAG = "PermissionActivity_lsn";
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    Context mContext;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.SEND_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = PermissionActivity.this;
        Log.i(TAG,"mContext = "+mContext);
        //checkSDKVersion only need to check the Permission at Android M
        Log.i(TAG,"checkSDKVersion() = "+checkSDKVersion());
        Log.i(TAG,"hasAllPermissions(PERMISSIONS) = "+hasAllPermissions(PERMISSIONS));
        if (checkSDKVersion() && !hasAllPermissions(PERMISSIONS))
            checkPermissionGranted();
    }

    public void checkPermissionGranted() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();

        for(int i = 0;i<PERMISSIONS.length;i++){
            if(!addPermission(permissionsList,PERMISSIONS[i])){
                permissionsNeeded.add(PERMISSIONS[i]);
            }
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Activity) mContext).requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ((Activity) mContext).requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

    }

    @TargetApi(23)
    private boolean addPermission(List<String> permissionsList, String permission) {

        try {
            if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!((Activity) mContext).shouldShowRequestPermissionRationale(permission))
                    return false;
            }
            return true;
        } catch (Exception e) {
        }
        return false;

    }

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                for (int j = 0;j<grantResults.length; j++){
                    if(grantResults[j]== PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(mContext, "All Permissions Granted is granted", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // Permission Denied
                        Toast.makeText(mContext, "Some Permission is Denied", Toast.LENGTH_SHORT)
                                .show();
                        openSetting();
                    }
                }
            }
        }
    }

    private boolean checkSDKVersion() {
        if (Build.VERSION.SDK_INT >= 23) {
            return true;
        } else {
            return false;
        }
    }

    //20161115
    public boolean hasAllPermissions(String... permissions){
        for (String permission : permissions) {
            if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void openSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish();
    }
}
