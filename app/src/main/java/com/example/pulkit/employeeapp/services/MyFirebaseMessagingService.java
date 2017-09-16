package com.example.pulkit.employeeapp.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.pulkit.employeeapp.BroadcastReceivers.AlarmReceiver;
import com.example.pulkit.employeeapp.CheckInternetConnectivity.NetWatcher;
import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.Notification.NotificationActivity;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.chat.ChatActivity;
import com.example.pulkit.employeeapp.model.Employee;
import com.example.pulkit.employeeapp.model.NameAndStatus;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.sendNotif;
import static com.example.pulkit.employeeapp.MainViews.TaskDetail.task_id;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Context context =this;
    private ArrayList<String> chatnotifList = new ArrayList<>();
    private static final String TAG1 = "MyFireMesgService";
    private EmployeeSession session;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //TODO get the type of notification handle separately for chats, quotation and normal assigned tasks
        session = new EmployeeSession(context);
        String type = remoteMessage.getData().get("type");
        if (type.equals("chat") || type == null) {
            String msg = remoteMessage.getData().get("body");
            String senderuid = remoteMessage.getData().get("senderuid");
            String chatref = remoteMessage.getData().get("chatref");
            String msgid = remoteMessage.getData().get("msgid");
            if (msg != null && chatref != null && msgid != null)
                sendChatNotification(msg, chatref, msgid, senderuid);

        }
        else if(type.contains("repeatedReminder"))
        {
            String body = remoteMessage.getData().get("body");
            String senderuid = remoteMessage.getData().get("senderuid");
            String taskId = remoteMessage.getData().get("taskId");
            String id = remoteMessage.getData().get("msgid");
            String words[] = type.split(" ");
            String repeatAfter= words[1];
            if (body != null && taskId != null && senderuid != null)
                sendRepeatedNotification(body, senderuid, taskId, id,repeatAfter);
        }
        else if(type.equals("assignJob"))
        {
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
            String body = remoteMessage.getData().get("body");
            String senderuid = remoteMessage.getData().get("senderuid");
            String taskId = remoteMessage.getData().get("taskId");
            String id = remoteMessage.getData().get("msgid");

            Intent intent = new Intent(this, NetWatcher.class);
            intent.setAction("seen_notification");
            intent.putExtra("empname",session.getName());
            intent.putExtra("senderuid",senderuid);
            intent.putExtra("mykey",session.getUsername());
            contentView.setTextViewText(R.id.title, body);
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                    intent, 0);

            contentView.setOnClickPendingIntent(R.id.seen,pendingSwitchIntent);
            if (body != null  && senderuid != null)
                sendGeneralNotification2(body, senderuid, taskId, id, contentView);
        }
        else {
            String body = remoteMessage.getData().get("body");
            String senderuid = remoteMessage.getData().get("senderuid");
            String taskId = remoteMessage.getData().get("taskId");
            String id = remoteMessage.getData().get("msgid");
            if (body != null && taskId != null && senderuid != null)
                sendGeneralNotification(body, senderuid, taskId, id);
        }
    }

    private void sendRepeatedNotification(final String body, String senderuid, final String taskId, final String id, final String repeatAfter) {
        DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(senderuid).getRef();
        dbOnlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    alarmIntent.putExtra("body",body);
                    alarmIntent.putExtra("title","New Notification from " + nameAndStatus.getName());
                    alarmIntent.putExtra("notifId",id);
                    alarmIntent.putExtra("task_id",taskId);
                    String notifid = id.substring(8);
                    Integer pendingIntentId = Integer.parseInt(notifid);
                    alarmIntent.putExtra("pendingIntentId",pendingIntentId);
                    final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Integer repeat = Integer.parseInt(repeatAfter);
                    final AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    final int interval = 1000 * 60 * repeat;
                    manager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                            interval, pendingIntent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendGeneralNotification(final String body, String senderuid, String taskId, final String id) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(senderuid).getRef();
        dbOnlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New Notification from " + nameAndStatus.getName())
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String notifid = id.substring(8);
                    notificationManager.notify(Integer.parseInt(notifid) /* ID of notification */, notificationBuilder.build());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendGeneralNotification2(final String body, String senderuid, String taskId, final String id, final RemoteViews contentView) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(senderuid).getRef();
        dbOnlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setCustomBigContentView(contentView)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String notifid = id.substring(8);
                    notificationManager.notify(Integer.parseInt(notifid) /* ID of notification */, notificationBuilder.build());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendChatNotification(final String msg, String chatref, final String msgid, final String senderuid) throws NullPointerException {
        final DatabaseReference dbr = DBREF.child("Chats").child(chatref).child("ChatMessages").child(msgid).child("status");
        Intent intent = new Intent(this, ChatActivity.class);

        intent.putExtra("otheruserkey", senderuid);
        intent.putExtra("dbTableKey", chatref);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        System.out.println(dbr + " setting value to 2 for " + chatref);
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!dataSnapshot.getValue().toString().matches("3"))
                        dbr.setValue("2");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (isAppIsInForeground(this) == false&& !chatnotifList.contains(msgid)) {
            DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(senderuid).getRef();
            dbOnlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("New Message from " + nameAndStatus.getName())
                                .setContentText(msg)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(senderuid.hashCode() /* ID of notification */, notificationBuilder.build());
                        chatnotifList.add(msgid);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private boolean isAppIsInForeground(Context context) {
        boolean isInForeground = false;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                isInForeground = true;
            }

        }
        return true;
    }
}
