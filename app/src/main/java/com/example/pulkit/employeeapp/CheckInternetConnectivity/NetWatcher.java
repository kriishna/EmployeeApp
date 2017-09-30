package com.example.pulkit.employeeapp.CheckInternetConnectivity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.pulkit.employeeapp.EmployeeApp;
import com.example.pulkit.employeeapp.Notification.NotificationActivity;
import com.example.pulkit.employeeapp.services.LocServ;
import com.example.pulkit.employeeapp.services.MyFirebaseMessagingService;

import static android.provider.LiveFolders.INTENT;

public class NetWatcher extends BroadcastReceiver {

    public NetWatcher() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        if (arg1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
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

       /* if (arg1.getAction().equals("seen_notification")) {
            String content = arg1.getStringExtra("empname") + " has seen the Job";
            String receiverId = arg1.getStringExtra("senderuid");
            String senderId = arg1.getStringExtra("mykey");
            String id = arg1.getStringExtra("id");
            EmployeeApp.sendNotif(senderId,receiverId,"seen",content," ");
            NotificationManager notificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.cancel(Integer.parseInt(id));
            Toast.makeText(context,"Informing Coordinator",Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(context, NotificationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }*/
    }
}