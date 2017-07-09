package com.example.pulkit.employeeapp.Notification;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.pulkit.employeeapp.R;

public class Notification extends AppCompatActivity {

    Button normal,persistent,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        normal = (Button) findViewById(R.id.button2);
        persistent = (Button) findViewById(R.id.button3);
        cancel = (Button) findViewById(R.id.button4);


        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler mHandler = new Handler();
                Context appContext = Notification.this.getApplicationContext();
       //         mHandler.post(new DisplayNotification(appContext,"0",0));

            }
        });

        persistent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler mHandler = new Handler();
                Context appContext = Notification.this.getApplicationContext();
         //       mHandler.post(new DisplayNotification(appContext,"1",0));

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler mHandler = new Handler();
                Context appContext = Notification.this.getApplicationContext();
         //       mHandler.post(new DisplayNotification(appContext,0));

            }
        });
    }
}
