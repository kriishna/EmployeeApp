package com.example.pulkit.employeeapp.Notification;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.example.pulkit.employeeapp.MainActivity;
import com.example.pulkit.employeeapp.R;

public class DisplayNotification implements Runnable {
    Context mContext;
    NotificationManager mNotificationManager;
    int NOTIFICATION_ID = 0;
    String TYPE = "0";
    String text="fsd",title="fzsd";

    // there are 2 types of constructors

    public DisplayNotification(Context mContext, String TYPE, String title,String text) {
        this.mContext = mContext;
        this.TYPE = TYPE;
        this.NOTIFICATION_ID=NOTIFICATION_ID;
        this.text = text;
        this.title = title;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public DisplayNotification(Context mContext, int NOTIFICATION_ID) {
        this.mContext = mContext;
        this.NOTIFICATION_ID=NOTIFICATION_ID;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(NOTIFICATION_ID);
    }


    //type = 0 normal notification
    //type = 1 persistent notification
    //notification id to remove notification

    @Override
    public void run() {
        if(TYPE.equals("0") || TYPE.equals("1"))
            makeNotification(mContext);
    }

    private void makeNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.edit_icon)
                ;
        Notification n;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            n = builder.build();
        } else {
            n = builder.getNotification();
        }

        if(TYPE.equals("1"))
            n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT | Notification.PRIORITY_HIGH;


        mNotificationManager.notify(NOTIFICATION_ID, n);

    }

}