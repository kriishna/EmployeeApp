package com.example.pulkit.employeeapp.measurement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.pulkit.employeeapp.EmployeeApp;
import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.helper.CompressMe;
import com.example.pulkit.employeeapp.model.measurement;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.sendNotif;


public class dialogue extends AppCompatActivity {

    private ArrayList<String> photoPaths = new ArrayList<>();
    EditText width, height, unit, tag, amount;
    String fleximage = "", temp_width="", temp_height="", temp_unit="", temp_amount="", id = "", temp_tag = "";
    private static final int REQUEST_CODE = 51;
    DatabaseReference dbRef, db;
    StorageReference storageReference, sf;
    Uri tempUri = Uri.parse("");
    ProgressDialog pd;
    ImageView img;
    String item;
    EmployeeSession employeeSession;
    CompressMe compressMe;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);

        dbRef = DBREF;
        storageReference = FirebaseStorage.getInstance().getReference().child("MeasurementImages");
        img = (ImageView) findViewById(R.id.imageView);
        width = (EditText) findViewById(R.id.width);
        height = (EditText) findViewById(R.id.height);
        unit = (EditText) findViewById(R.id.unit);
        tag = (EditText) findViewById(R.id.tag);
        amount = (EditText) findViewById(R.id.amount);

        pd = new ProgressDialog(dialogue.this);
        pd.setMessage("Uploading....");

        compressMe = new CompressMe(this);
        employeeSession = new EmployeeSession(this);

        if (getIntent().hasExtra("width")) {
            temp_width = getIntent().getStringExtra("width");
            temp_height = getIntent().getStringExtra("height");
            temp_unit = getIntent().getStringExtra("unit");
            fleximage = getIntent().getStringExtra("fleximage");
            temp_tag = getIntent().getStringExtra("tag");
            temp_amount = getIntent().getStringExtra("amount");
            id = getIntent().getStringExtra("id");

            width.setText(temp_width);
            height.setText(temp_height);
            unit.setText(temp_unit);
            tag.setText(temp_tag);
            amount.setText(temp_amount);
            if (!fleximage.equals(""))
                Picasso.with(dialogue.this).load(fleximage).into(img);
        }


        ImageButton photoButton = (ImageButton) findViewById(R.id.capture);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FilePickerBuilder.getInstance().setMaxCount(1)
                        .setActivityTheme(R.style.AppTheme)
                        .pickPhoto(dialogue.this);    }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            photoPaths =new ArrayList<>();
            photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            assert photoPaths != null;

            System.out.println(String.format("Totally %d images selected:", photoPaths.size()));
            if (photoPaths.size() > 0) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        item = photoPaths.get(0);
                        String l = compressMe.compressImage(item, dialogue.this);
                        img.setImageURI(Uri.parse(l));
                        tempUri = Uri.fromFile(new File(l));
                    }
                }, 500);
            }

        }


    }


    public void confirm(View view) {

        // get the text from the EditText
        temp_width = width.getText().toString();
        temp_height = height.getText().toString();
        temp_unit = unit.getText().toString();
        temp_tag = tag.getText().toString();
        temp_amount = amount.getText().toString();

       /* if (temp_width.equals("") || temp_height.equals("") || temp_unit.equals("") ||temp_amount.equals(""))
            Toast.makeText(this, "Enter complete details...", Toast.LENGTH_SHORT).show();
        else*/
            new net().execute();


    }

    public void cancel(View v) {
        finish();
    }

    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (id.equals("")) {
                db = dbRef.child("Task").child(TaskDetail.task_id).child("Measurement").push();
                id = db.getKey();
            }

            db = dbRef.child("Task").child(TaskDetail.task_id).child("Measurement").child(id);

            if(!tempUri.toString().equals("")) {
                sf = storageReference.child(TaskDetail.task_id).child(id + ".jpeg");

                UploadTask uploadTask = sf.putFile(tempUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(dialogue.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        fleximage = taskSnapshot.getDownloadUrl().toString();
                        measurement temp = new measurement(temp_tag, temp_width, temp_height, fleximage, temp_unit, id,temp_amount);
                        db.setValue(temp);
                        DBREF.child("Task").child(TaskDetail.task_id).child("measurementApproved").setValue(Boolean.FALSE);
                        sendNotif(TaskHome.emp_id,TaskDetail.customer_id,"measurementChanged","Measurement for your task "+TaskDetail.taskname+" has been changed. Please approve it.",TaskDetail.task_id);
                        pd.dismiss();


                        // put the String to pass back into an Intent and close this activity


                        Intent intent = new Intent();
                        intent.putExtra("width", temp_width);
                        intent.putExtra("height", temp_height);
                        intent.putExtra("unit", temp_unit);
                        intent.putExtra("fleximage", fleximage);
                        intent.putExtra("tag", temp_tag);
                        intent.putExtra("amount", temp_amount);
                        intent.putExtra("id", id);

                        setResult(RESULT_OK, intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(dialogue.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Intent intent = new Intent();
                intent.putExtra("width", temp_width);
                intent.putExtra("height", temp_height);
                intent.putExtra("unit", temp_unit);
                intent.putExtra("fleximage", fleximage);
                intent.putExtra("amount", temp_amount);
                intent.putExtra("tag", temp_tag);
                intent.putExtra("id", id);

                setResult(RESULT_OK, intent);

                DBREF.child("Task").child(TaskDetail.task_id).child("measurementApproved").setValue(Boolean.FALSE);
                EmployeeApp.sendNotif(employeeSession.getUsername(), TaskDetail.customerId, "measurementChanged", "Measurement for your task " + TaskDetail.taskName + " has been changed. Please approve it.", TaskDetail.task_id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(dialogue.this, "Informing the Customer of Changes", Toast.LENGTH_SHORT).show();
                    }
                });

                finish();

            }


            return null;
        }


    }
}
