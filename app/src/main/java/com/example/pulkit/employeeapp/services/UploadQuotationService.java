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
import com.example.pulkit.employeeapp.model.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

/**
 * Created by SoumyaAgarwal on 7/3/2017.
 */

public class UploadQuotationService extends IntentService
{

    public static ArrayList<Task> TaskList = new ArrayList<Task>();
    public static Uri selectedFileUri;
    public static List<Integer> selectedItemPositions = new ArrayList<>();
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

            TaskList = (ArrayList<Task>) intent.getSerializableExtra("TaskList");
            selectedFileUri = Uri.parse(intent.getStringExtra("selectedFileUri"));
            selectedItemPositions = intent.getIntegerArrayListExtra("selectedItemPositions");
            saveQuotationtoFirebase(TaskList,selectedItemPositions);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void saveQuotationtoFirebase(ArrayList<Task> TaskList, List<Integer> selectedItemPositions)
    {

        Toast.makeText(getBaseContext(),"Uploading Quotation in Background", Toast.LENGTH_SHORT).show();

        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            selectedItemPositions.get(i);

            final Task task = TaskList.get(selectedItemPositions.get(i));

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("Quotation").child(task.getTaskId());

            riversRef.putFile(selectedFileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Quotation quotation = new Quotation("No");
                            DatabaseReference dbQuotation = DBREF.child("Task").child(task.getTaskId()).child("Quotation").getRef();
                            dbQuotation.setValue(quotation);
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