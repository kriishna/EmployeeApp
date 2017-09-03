package com.example.pulkit.employeeapp.measurement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pulkit.employeeapp.Customer.custTasks;
import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.ViewImageAdapter;
import com.example.pulkit.employeeapp.adapters.bigimage_adapter;
import com.example.pulkit.employeeapp.helper.CompressMe;
import com.example.pulkit.employeeapp.model.measurement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class dialogue extends AppCompatActivity {

    private ArrayList<String> photoPaths = new ArrayList<>();
    EditText width, height, unit,tag;
    String tagString="", fleximage = "", temp_width, temp_height, temp_unit, id = "";
    private static final int REQUEST_CODE = 51;
    DatabaseReference dbRef, db;
    StorageReference storageReference, sf;
    Uri tempUri = Uri.parse("");
    ProgressDialog pd;
    CompressMe compressMe;
    ImageView img;
    String item;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);
        tag = (EditText)findViewById(R.id.tag);
        dbRef = DBREF;
        storageReference = FirebaseStorage.getInstance().getReference().child("MeasurementImages");
        img = (ImageView) findViewById(R.id.imageView);
        width = (EditText) findViewById(R.id.width);
        height = (EditText) findViewById(R.id.height);
        unit = (EditText) findViewById(R.id.unit);

        pd = new ProgressDialog(dialogue.this);
        pd.setMessage("Uploading....");

        compressMe = new CompressMe(this);

        if (getIntent().hasExtra("width")) {
            temp_width = getIntent().getStringExtra("width");
            temp_height = getIntent().getStringExtra("height");
            temp_unit = getIntent().getStringExtra("unit");
            fleximage = getIntent().getStringExtra("fleximage");
            id = getIntent().getStringExtra("id");

            width.setText(temp_width);
            height.setText(temp_height);
            unit.setText(temp_unit);
            if (!fleximage.equals(""))
                Picasso.with(dialogue.this).load(fleximage).into(img);
        }

        ImageButton photoButton = (ImageButton) findViewById(R.id.capture);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FilePickerBuilder.getInstance().setMaxCount(10)
                        .setActivityTheme(R.style.AppTheme)
                        .pickPhoto(dialogue.this);
                    }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            photoPaths=new ArrayList<>();
            photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

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

        if (temp_width.equals("") || temp_height.equals("") || temp_unit.equals(""))
            Toast.makeText(this, "Enter complete details...", Toast.LENGTH_SHORT).show();
        else
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

            if (!tempUri.toString().equals("")) {
                sf = storageReference.child(TaskDetail.task_id).child(id + ".jpeg");

                UploadTask uploadTask = sf.putFile(tempUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(dialogue.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        fleximage = taskSnapshot.getDownloadUrl().toString();
                        measurement temp = new measurement("", temp_width, temp_height, fleximage, temp_unit, id);
                        db.setValue(temp);
                        pd.dismiss();


                        // put the String to pass back into an Intent and close this activity

                        Intent intent = new Intent();
                        intent.putExtra("width", temp_width);
                        intent.putExtra("height", temp_height);
                        intent.putExtra("unit", temp_unit);
                        intent.putExtra("fleximage", fleximage);
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
                intent.putExtra("id", id);

                setResult(RESULT_OK, intent);
                finish();

            }


            return null;
        }


    }
}
