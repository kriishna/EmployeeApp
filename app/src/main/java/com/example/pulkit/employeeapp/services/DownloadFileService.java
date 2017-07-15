package com.example.pulkit.employeeapp.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.pulkit.employeeapp.model.Quotation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;

public class DownloadFileService extends Service {
    private static String LOG_TAG = "UploadFileService";
    private IBinder mBinder = new MyBinder();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        Toast.makeText(getApplicationContext(),"Service Started",Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }


    public class MyBinder extends Binder {
        public DownloadFileService getService() {
            return DownloadFileService.this;
        }
    }

    public void downloadFile(final DatabaseReference dbQuotation, final String task_id) {
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    File localFile = null;
                    localFile = new File(Environment.getExternalStorageDirectory(), "Management/Quotation");
                    // Create direcorty if not exists
                    if (!localFile.exists()) {
                        localFile.mkdirs();
                    }

                    File myDownloadedFile = new File(localFile, task_id + "Quotation.pdf");
                    StorageReference storageReference = mStorageRef.child("Quotation").child(task_id);
                    storageReference.getFile(myDownloadedFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }


                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double fprogress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            long bytes = taskSnapshot.getBytesTransferred();

                            String progress = String.format("%.2f", fprogress);
                            int constant = 1000;
                            if(bytes%constant == 0)
                            {
                                android.support.v4.app.NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(android.R.drawable.stat_sys_download)
                                                .setContentTitle("Downloading " + task_id + "Quotation.pdf")
                                                .setContentText(" " + progress + "% completed" );

                                NotificationManager mNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                mNotificationManager.notify(100, mBuilder.build());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
