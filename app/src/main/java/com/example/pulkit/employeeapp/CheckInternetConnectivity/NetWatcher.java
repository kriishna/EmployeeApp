package com.example.pulkit.employeeapp.CheckInternetConnectivity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.example.pulkit.employeeapp.EmployeeApp;
import com.example.pulkit.employeeapp.Notification.DisplayNotification;
import com.example.pulkit.employeeapp.Notification.Notification;
import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NetWatcher extends BroadcastReceiver {
    DatabaseReference db,per,non;
    NotificationManager mNotificationManager;
    String text,title,task_id,type;
    Handler mHandler = new Handler();

    @Override
    public void onReceive(final Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child("abcd").child("notifications");


        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();



        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
/*
                String sum = dataSnapshot.toString();
                text = dataSnapshot.child("text").getValue().toString();
                title = dataSnapshot.child("title").getValue().toString();
                type = dataSnapshot.child("type").getValue().toString();
                mHandler.post(new DisplayNotification(context,type,title,text));

*/
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


}