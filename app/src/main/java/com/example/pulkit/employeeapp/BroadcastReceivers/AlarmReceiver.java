package com.example.pulkit.employeeapp.BroadcastReceivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.Notification.NotificationActivity;
import com.example.pulkit.employeeapp.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String body = intent.getStringExtra("body");
        final String title = intent.getStringExtra("title");
        String notifId = intent.getStringExtra("notifId");
        createNotification(context,title,body,notifId);
        // For our recurring task, we'll just display a message
    }
    public void createNotification(Context context, String title, String msgText, String notifId)
    {
        PendingIntent notificIntent=PendingIntent.getActivity(context,0,new Intent(context,NotificationActivity.class),0);
        NotificationCompat.Builder mBuilder= (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_call_me)
                .setContentTitle(title).setTicker("Reminder").setContentText(msgText);

        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String notifid = notifId.substring(8);
        notificationManager.notify(Integer.parseInt(notifid) /* ID of notification */, mBuilder.build());
    }

}
