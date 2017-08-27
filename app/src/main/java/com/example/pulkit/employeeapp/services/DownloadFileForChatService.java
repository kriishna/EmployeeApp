package com.example.pulkit.employeeapp.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.IDN;

import static com.example.pulkit.employeeapp.EmployeeApp.AppName;
import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class DownloadFileForChatService extends IntentService {

    String type, url, dbTableKey, Id;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    public DownloadFileForChatService() {
        super("Upload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            type = intent.getStringExtra("type");
            url = intent.getStringExtra("url");
            dbTableKey = intent.getStringExtra("dbTableKey");
            Id = intent.getStringExtra("Id");
            downloadFile(url, type, dbTableKey);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void downloadFile(final String url, final String type, final String dbTableKey)
    {
        final DatabaseReference[] dbChat = {DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef()};
        StorageReference str = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        switch (type) {
            case "photo":
                File rootPath = new File(Environment.getExternalStorageDirectory(), AppName+"/Images");
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                String uriSting = System.currentTimeMillis() + ".jpg";

                final File localFile = new File(rootPath, uriSting);
                final String localuri = (rootPath.getAbsolutePath() + "/" + uriSting);
                str.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                        dbChat[0] = DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef();
                        dbChat[0].child(Id).child("othersenderlocal_storage").setValue(localuri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    }
                });
                break;
            case "doc":
                rootPath = new File(Environment.getExternalStorageDirectory(), AppName+"/Docs");
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                uriSting = System.currentTimeMillis() + ".jpg";

                final File localdocFile = new File(rootPath, uriSting);
                final String localdocuri = (rootPath.getAbsolutePath() + "/" + uriSting);
                str.getFile(localdocFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        dbChat[0] = DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef();
                        dbChat[0].child(Id).child("othersenderlocal_storage").setValue(localdocuri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
//                        mAdapter.dismissProgressBar(holder);
                        Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    }
                });
                break;
        }
    }
}
