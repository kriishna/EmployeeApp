package com.example.pulkit.employeeapp.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pulkit on 20/7/17.
 */

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Service", Toast.LENGTH_LONG).show();
        Log.e("gsedf","gvhgcjvj");
        Intent myIntent = new Intent(intent);
        myIntent.setComponent(new ComponentName(context, MyFirebaseMessagingService.class));
        context.startService(myIntent);
    }
}
