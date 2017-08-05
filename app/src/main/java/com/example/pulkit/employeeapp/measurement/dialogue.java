package com.example.pulkit.employeeapp.measurement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.measurement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class dialogue extends AppCompatActivity {

    EditText width, height, unit;
    String fleximage = "", temp_width, temp_height, temp_unit, id = "";
    private static final int CAMERA_REQUEST = 1888;
    DatabaseReference dbRef, db;
    StorageReference storageReference, sf;
    Uri tempUri = Uri.parse("");
    ProgressDialog pd;
    ImageView img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);

        dbRef = DBREF;
        storageReference = FirebaseStorage.getInstance().getReference().child("MeasurementImages");
        img = (ImageView) findViewById(R.id.imageView);
        width = (EditText) findViewById(R.id.width);
        height = (EditText) findViewById(R.id.height);
        unit = (EditText) findViewById(R.id.unit);

        pd = new ProgressDialog(dialogue.this);
        pd.setMessage("Uploading....");


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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
       //     img.setBackground(new ColorDrawable(Color.WHITE));
            img.setBackground(new BitmapDrawable(photo));

            tempUri = getImageUri(getApplicationContext(), photo);


        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String temp;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        temp = cursor.getString(idx);
        cursor.close();
        return temp;
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
                db = dbRef.child("Task").child(TaskDetail.task_id).child("measurement").push();
                id = db.getKey();
            }

            db = dbRef.child("Task").child(TaskDetail.task_id).child("measurement").child(id);

            if(!tempUri.toString().equals("")) {
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

            }
            else{
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
