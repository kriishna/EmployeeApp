package com.example.pulkit.employeeapp.MainViews;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.bigimage_adapter;
import com.example.pulkit.employeeapp.adapters.measurement_adapter;
import com.example.pulkit.employeeapp.adapters.taskdetailDescImageAdapter;
import com.example.pulkit.employeeapp.chat.ChatActivity;
import com.example.pulkit.employeeapp.helper.MarshmallowPermissions;
import com.example.pulkit.employeeapp.measurement.MeasureList;
import com.example.pulkit.employeeapp.model.CompletedBy;
import com.example.pulkit.employeeapp.model.CompletedJob;
import com.example.pulkit.employeeapp.model.Quotation;
import com.example.pulkit.employeeapp.model.Task;
import com.example.pulkit.employeeapp.model.measurement;
import com.example.pulkit.employeeapp.services.DownloadFileService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.sendNotif;

public class TaskDetail extends AppCompatActivity implements taskdetailDescImageAdapter.ImageAdapterListener, bigimage_adapter.bigimage_adapterListener {

    DatabaseReference dbRef, dbTask, dbCompleted, dbAssigned, dbMeasurement, dbDescImages;
    ImageButton download;
    public static String task_id;
    public String emp_id,desig;
    private Task task;
    private String customername, mykey;
    EditText startDate, endDate, quantity, description, coordinators_message;
    RecyclerView rec_measurement, rec_DescImages;
    Button forward;
    ArrayList<measurement> measurementList = new ArrayList<>();
    measurement_adapter adapter_measurement;
    TextView appByCustomer, uploadStatus;
    DatabaseReference dbQuotation;
    ProgressDialog progressDialog;
    LinearLayout ll;
    TextView text, measure_and_hideme;
    Button measure;
    ScrollView scroll;
    taskdetailDescImageAdapter adapter_taskimages;
    bigimage_adapter adapter;
    private AlertDialog viewSelectedImages, confirmation;
    ArrayList<String> DescImages = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    EmployeeSession session;
    String dbTablekey, id;
    String num;
    private MarshmallowPermissions marshmallowPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        dbRef = DBREF;
        marshmallowPermissions = new MarshmallowPermissions(this);
        progressDialog = new ProgressDialog(this);
        download = (ImageButton) findViewById(R.id.download);
        uploadStatus = (TextView) findViewById(R.id.uploadStatus);
        appByCustomer = (TextView) findViewById(R.id.appByCustomer);

        scroll = (ScrollView) findViewById(R.id.scroll);
        measure = (Button) findViewById(R.id.measure);
        forward = (Button) findViewById(R.id.forward);
        coordinators_message = (EditText) findViewById(R.id.coordinators_message);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity = (EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        rec_measurement = (RecyclerView) findViewById(R.id.rec_measurement);
        rec_DescImages = (RecyclerView) findViewById(R.id.rec_DescImages);
        measure_and_hideme = (TextView) findViewById(R.id.measure_and_hideme);
        text = (TextView) findViewById(R.id.textView6);
        ll = (LinearLayout) findViewById(R.id.quotation_container);

        session = new EmployeeSession(getApplicationContext());

        mykey = session.getUsername();
        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");
        id = intent.getStringExtra("customerId");
        emp_id = TaskHome.emp_id;
        desig = TaskHome.desig;

        dbTask = dbRef.child("Task").child(task_id);
        dbQuotation = dbTask.child("Quotation").getRef();
        dbCompleted = dbTask.child("CompletedBy").getRef();
        dbAssigned = dbTask.child("AssignedTo").getRef();
        dbMeasurement = dbTask.child("measurement").getRef();
        dbDescImages = dbTask.child("DescImages").getRef();

        rec_measurement.setLayoutManager(new LinearLayoutManager(this));
        rec_measurement.setItemAnimator(new DefaultItemAnimator());
        rec_measurement.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_measurement = new measurement_adapter(measurementList, this);
        rec_measurement.setAdapter(adapter_measurement);

        rec_DescImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rec_DescImages.setItemAnimator(new DefaultItemAnimator());
        rec_DescImages.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        adapter_taskimages = new taskdetailDescImageAdapter(DescImages, getApplicationContext(), this);
        rec_DescImages.setAdapter(adapter_taskimages);

        dbDescImages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    rec_DescImages.setVisibility(View.VISIBLE);
                    String item = dataSnapshot.getValue(String.class);
                    DescImages.add(item);
                    adapter_taskimages.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String item = dataSnapshot.getKey();
                DescImages.remove(item);
                adapter_taskimages.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskDetail.this, MeasureList.class));
            }
        });

        dbTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                task = dataSnapshot.getValue(Task.class);
                setValue(task);
                getSupportActionBar().setTitle(task.getName());
                DatabaseReference dbCustomerName = DBREF.child("Customer").child(task.getCustomerId()).getRef();
                dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        customername = dataSnapshot.child("name").getValue(String.class);
                        getSupportActionBar().setSubtitle(customername);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmation = new AlertDialog.Builder(TaskDetail.this)
                        .setView(R.layout.confirmation_layout).create();
                confirmation.show();

                final EditText employeeNote = (EditText) confirmation.findViewById(R.id.employeeNote);
                Button okcompleted = (Button) confirmation.findViewById(R.id.okcompleted);
                Button okcanceled = (Button) confirmation.findViewById(R.id.okcanceled);

                okcanceled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmation.dismiss();
                    }
                });

                okcompleted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String employeesnote = employeeNote.getText().toString().trim();

                        final DatabaseReference db, databaseReference;

                        DBREF.child("Employee").child(emp_id).child("CompletedTask").child(task_id).setValue("done");
                        db = DBREF.child("Employee").child(emp_id).child("AssignedTask").child(task_id);
                        db.removeValue();

                        databaseReference = DBREF.child("Task").child(task_id).child("AssignedTo").child(emp_id);

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    CompletedBy completedBy = dataSnapshot.getValue(CompletedBy.class);
                                    CompletedJob completedJob = new CompletedJob();
                                    completedJob.setEmpId(completedBy.getEmpId());
                                    completedJob.setAssignedByName(completedBy.getAssignedByName());
                                    completedJob.setAssignedByUsername(completedBy.getAssignedByUsername());
                                    completedJob.setCoordinatorNote(completedBy.getNote());
                                    completedJob.setDateassigned(completedBy.getDateassigned());
                                    completedJob.setDatecompleted(completedBy.getDatecompleted());
                                    completedJob.setEmpployeeNote(employeesnote);

                                    databaseReference.removeValue();
                                    DBREF.child("Task").child(task_id).child("CompletedBy").child(emp_id).setValue(completedJob);

                                    String contentforme = "You completed " + task.getName();
                                    sendNotif(mykey, mykey, "completedJob", contentforme, task_id);
                                    String contentforother = "Employee " + session.getName() + " completed " + task.getName();
                                    sendNotif(mykey, completedJob.getAssignedByUsername(), "completedJob", contentforother, task_id);
                                    Toast.makeText(TaskDetail.this, contentforme, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        confirmation.dismiss();
                    }
                });
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!marshmallowPermissions.checkPermissionForCamera()) {
                    marshmallowPermissions.requestPermissionForExternalStorage();
                    if (!marshmallowPermissions.checkPermissionForExternalStorage())
                        showToast("Cannot Download because external storage permission not granted");
                    else
                        launchLibrary();
                } else {

                    launchLibrary();
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskdetail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").child(id).child("phone_num").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        num = dataSnapshot.getValue(String.class);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + num));
                        startActivity(callIntent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case R.id.item2:
                checkChatref(mykey, id);
                break;
        }
        return true;
    }

    private void checkChatref(final String mykey, final String otheruserkey) {
        DatabaseReference dbChat = DBREF.child("Chats").child(mykey + otheruserkey).getRef();
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("query1" + mykey + otheruserkey);
                System.out.println("datasnap 1" + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    System.out.println("datasnap exists1" + dataSnapshot.toString());
                    dbTablekey = mykey + otheruserkey;
                    goToChatActivity();

                } else {
                    checkChatref2(mykey, otheruserkey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkChatref2(final String mykey, final String otheruserkey) {
        final DatabaseReference dbChat = DBREF.child("Chats").child(otheruserkey + mykey).getRef();
        dbTablekey = otheruserkey + mykey;
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("query1" + otheruserkey + mykey);
                    goToChatActivity();
                } else {
                    DBREF.child("Users").child("Userchats").child(mykey).child(otheruserkey).setValue(dbTablekey);
                    DBREF.child("Users").child("Userchats").child(otheruserkey).child(mykey).setValue(dbTablekey);
                    goToChatActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToChatActivity() {
        Intent in = new Intent(this, ChatActivity.class);
        in.putExtra("dbTableKey", dbTablekey);
        in.putExtra("otheruserkey", id);
        startActivity(in);
    }

    private void launchLibrary() {
        final String[] url = new String[1];
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    url[0] = quotation.getUrl();
                    Intent serviceIntent = new Intent(getApplicationContext(), DownloadFileService.class);
                    serviceIntent.putExtra("TaskId", task_id);
                    serviceIntent.putExtra("url", url[0]);
                    startService(serviceIntent);
                } else {
                    Toast.makeText(TaskDetail.this, "No Quotation Uploaded Yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(TaskDetail.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(TaskDetail.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(TaskDetail.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            launchLibrary();
        }

    }

    private void prepareListData() {
        dbMeasurement.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    measure_and_hideme.setVisibility(View.GONE);
                    measurement item = dataSnapshot.getValue(measurement.class);
                    measurementList.add(item);
                    adapter_measurement.notifyDataSetChanged();
                } else {
                    measure_and_hideme.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                measurement item = dataSnapshot.getValue(measurement.class);
                measurementList.remove(item);
                adapter_measurement.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setValue(Task task) {
        startDate.setText(task.getStartDate());
        endDate.setText(task.getExpEndDate());
        quantity.setText(task.getQty());
        description.setText(task.getDesc());

        dbAssigned.child(session.getUsername()).child("note").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String note = dataSnapshot.getValue(String.class);
                    if (!note.equals(""))
                        coordinators_message.setText(note);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    appByCustomer.setVisibility(View.VISIBLE);
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    appByCustomer.setText(" " + quotation.getApprovedByCust());
                    uploadStatus.setText(" Yes");
                } else {
                    appByCustomer.setVisibility(View.GONE);
                    uploadStatus.setText(" No");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchLibrary();
                } else {
                    checkPermission();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        measurementList.clear();
        prepareListData();
    }

    @Override
    public void onImageClicked(int position) {
        viewSelectedImages = new AlertDialog.Builder(TaskDetail.this)
                .setView(R.layout.view_image_on_click).create();
        viewSelectedImages.show();

        RecyclerView bigimage = (RecyclerView) viewSelectedImages.findViewById(R.id.bigimage);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        bigimage.setLayoutManager(linearLayoutManager);
        bigimage.setItemAnimator(new DefaultItemAnimator());
        bigimage.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

        adapter = new bigimage_adapter(DescImages, this, this);
        bigimage.setAdapter(adapter);

        bigimage.scrollToPosition(position);
    }

    @Override
    public void ondownloadButtonClicked(final int position, final bigimage_adapter.MyViewHolder holder) {
        if (!marshmallowPermissions.checkPermissionForExternalStorage()) {
            marshmallowPermissions.requestPermissionForExternalStorage();
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.download_taskdetail_image.setVisibility(View.GONE);
            String url = DescImages.get(position);
            StorageReference str = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            File rootPath = new File(Environment.getExternalStorageDirectory(), "MeChat/TaskDetailImages");

            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }
            String uriSting = System.currentTimeMillis() + ".jpg";

            final File localFile = new File(rootPath, uriSting);

            str.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                    holder.download_taskdetail_image.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.GONE);
                    Toast.makeText(TaskDetail.this, "Image " + position + 1 + " Downloaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    holder.download_taskdetail_image.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.GONE);
                    Toast.makeText(TaskDetail.this, "Failed to download image " + position + 1, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}