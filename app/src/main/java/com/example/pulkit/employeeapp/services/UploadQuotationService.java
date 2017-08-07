package com.example.pulkit.employeeapp.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.Quotation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

/**
 * Created by SoumyaAgarwal on 7/3/2017.
 */
public class UploadQuotationService extends IntentService
{
    public static ArrayList<String> TaskIdList = new ArrayList<String>();
    public static Uri selectedFileUri;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    public UploadQuotationService() {
        super("Upload");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int icon = R.mipmap.ic_upload;
        mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        mBuilder.setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_upload))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getApplicationContext().getResources().getColor(R.color.white))
                .setContentText("Uploading quotations...");
        synchronized (this) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            TaskIdList = intent.getStringArrayListExtra("TaskIdList");
            selectedFileUri = Uri.parse(intent.getStringExtra("selectedFileUri"));
            saveQuotationtoFirebase(TaskIdList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void saveQuotationtoFirebase(final ArrayList<String> TaskIdList)
    {

        Toast.makeText(getBaseContext(),"Uploading Quotation in Background", Toast.LENGTH_SHORT).show();
        long timestamp = Calendar.getInstance().getTimeInMillis();
        timestamp = 9999999999999L-timestamp;

        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("Quotation").child(timestamp+"");
        riversRef.putFile(selectedFileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        for (int i = TaskIdList.size() - 1; i >= 0; i--)
                        {
                            Quotation quotation = new Quotation("No",taskSnapshot.getDownloadUrl().toString());
                            DatabaseReference dbQuotation = DBREF.child("Task").child(TaskIdList.get(i)).child("Quotation").getRef();
                            dbQuotation.setValue(quotation);
                        }
                        updateNotification("Succesfully Uploaded");

                        stopSelf();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        updateNotification("Upload failed");
                        stopSelf();
                    }
                });
    }

    private void updateNotification(String information) {
        notificationManager.cancel(0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        int icon = R.mipmap.ic_launcher;
        mBuilder.setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getApplicationContext().getResources().getColor(R.color.white))
                .setContentText(information)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(information));

        synchronized (this) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
        }
    }
}