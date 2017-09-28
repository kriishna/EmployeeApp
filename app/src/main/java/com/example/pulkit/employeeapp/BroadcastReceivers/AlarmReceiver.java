package com.example.pulkit.employeeapp.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.Notification.NotificationActivity;
import com.example.pulkit.employeeapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class AlarmReceiver extends BroadcastReceiver {
EmployeeSession employeeSession;
    @Override
    public void onReceive(final Context context, Intent intent) {
        final String body = intent.getStringExtra("body");
        final String title = intent.getStringExtra("title");
        final String task_id = intent.getStringExtra("task_id");

        employeeSession = new EmployeeSession(context);

        final String notifId = intent.getStringExtra("notifId");
        Intent intent1  = new Intent(context,AlarmReceiver.class);
        final Integer pendingIntentId = intent.getIntExtra("pendingIntentId",0);
        final PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(context, pendingIntentId,intent1,PendingIntent.FLAG_CANCEL_CURRENT);

        final DatabaseReference dbAssignedTask = DBREF.child("Employee").child(employeeSession.getUsername()).child("AssignedTask").child(task_id).getRef();
        dbAssignedTask.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    createNotification(context,title,body,notifId);

                }
                else
                {
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(alarmIntent);
                    Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show();
                    dbAssignedTask.removeEventListener(this);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // For our recurring task, we'll just display a message
    }
    public void createNotification(Context context, String title, String msgText, String notifId)
    {
        PendingIntent notificIntent=PendingIntent.getActivity(context,0,new Intent(context,NotificationActivity.class),0);
        NotificationCompat.Builder mBuilder= (NotificationCompat.Builder) new NotificationCompat.Builder(context).setLargeIcon(BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title).setTicker("Reminder").setContentText(msgText);
        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String notifid = notifId.substring(8);
        notificationManager.notify(Integer.parseInt(notifid) /* ID of notification */, mBuilder.build());
    }

}
