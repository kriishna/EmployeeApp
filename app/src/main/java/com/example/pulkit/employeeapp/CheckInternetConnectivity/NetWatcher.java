package com.example.pulkit.employeeapp.CheckInternetConnectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.pulkit.employeeapp.EmployeeApp;
import com.example.pulkit.employeeapp.services.LocServ;
import com.example.pulkit.employeeapp.services.MyFirebaseMessagingService;

public class NetWatcher extends BroadcastReceiver {

    public NetWatcher() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        if (arg1.getAction() == "android.intent.action.BOOT_COMPLETED") {
            Intent serviceIntent = new Intent(context, MyFirebaseMessagingService.class);
            context.startService(serviceIntent);
        }
        if (arg1.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            final LocationManager manager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            Intent in = new Intent(context, LocServ.class);
            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                context.startService(in);
            } else {
                context.stopService(in);
            }
        }

    }
}