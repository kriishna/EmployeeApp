package com.example.pulkit.employeeapp.MainViews;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.pulkit.employeeapp.ForwardTask.forwardTask;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.bigimage_adapter;
import com.example.pulkit.employeeapp.adapters.measurement_adapter;
import com.example.pulkit.employeeapp.adapters.taskdetailDescImageAdapter;
import com.example.pulkit.employeeapp.chat.ChatActivity;
import com.example.pulkit.employeeapp.measurement.MeasureList;
import com.example.pulkit.employeeapp.model.CompletedBy;
import com.example.pulkit.employeeapp.model.Quotation;
import com.example.pulkit.employeeapp.model.Task;
import com.example.pulkit.employeeapp.model.measurement;
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
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.sendNotif;

public class TaskDetail extends AppCompatActivity implements taskdetailDescImageAdapter.ImageAdapterListener,bigimage_adapter.bigimage_adapterListener{

    DatabaseReference dbRef, dbTask, dbCompleted, dbAssigned, dbMeasurement, dbDescImages;
    ImageButton download;
    public static String task_id, emp_id, desig;
    private Task task;
    private String customername,mykey;
    EditText startDate, endDate, quantity, description, coordinators_message;
    private static final int PICK_FILE_REQUEST = 1;
    RecyclerView rec_measurement,rec_DescImages;
    FloatingActionButton forward;
    ArrayList<measurement> measurementList = new ArrayList<>();
    measurement_adapter adapter_measurement;
    TextView appByCustomer, uploadStatus;
    DatabaseReference dbQuotation;
    ProgressDialog progressDialog;
    LinearLayout ll;
    TextView text, measure_and_hideme;
    Button measure;
    ScrollView scroll;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    taskdetailDescImageAdapter adapter_taskimages;
    bigimage_adapter adapter;
    private AlertDialog viewSelectedImages ;
    ArrayList<String> DescImages = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    EmployeeSession session;
    String dbTablekey,id;
    private AlertDialog confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        dbRef = DBREF;
        progressDialog = new ProgressDialog(this);
        download = (ImageButton) findViewById(R.id.download);
        uploadStatus = (TextView) findViewById(R.id.uploadStatus);
        appByCustomer = (TextView) findViewById(R.id.appByCustomer);

        scroll = (ScrollView) findViewById(R.id.scroll);
        measure = (Button) findViewById(R.id.measure);
        forward = (FloatingActionButton) findViewById(R.id.forward);
        coordinators_message = (EditText) findViewById(R.id.coordinators_message);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity = (EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        rec_measurement = (RecyclerView) findViewById(R.id.rec_measurement);
        rec_DescImages = (RecyclerView)findViewById(R.id.rec_DescImages);
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

        rec_DescImages.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rec_DescImages.setItemAnimator(new DefaultItemAnimator());
        rec_DescImages.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        adapter_taskimages = new taskdetailDescImageAdapter(DescImages, getApplicationContext(),this);
        rec_DescImages.setAdapter(adapter_taskimages);

        dbDescImages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
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

                Button okcompleted = (Button)confirmation.findViewById(R.id.okcompleted);
                Button okcanceled = (Button)confirmation.findViewById(R.id.okcanceled);

                okcompleted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference databaseReference;

                        databaseReference = DBREF.child("Task").child(task_id);
                        databaseReference.child("AssignedTo").child(emp_id).getRef();

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CompletedBy completedBy = dataSnapshot.getValue(CompletedBy.class);
                                completedBy.setDatecompleted(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                                //completed job
                                DatabaseReference dbAssigned = DBREF.child("Task").child(task_id).child("CompletedBy").child(session.getUsername());
                                dbAssigned.setValue(completedBy);

                                databaseReference.removeValue();
                                final DatabaseReference[] dbEmployee = {DBREF.child("Employee").child(session.getUsername()).child("AssignedTask").child(task_id).getRef()};
                                dbEmployee[0].addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        dbEmployee[0].removeValue(); //for employee

                                        dbEmployee[0] = DBREF.child("Employee").child(session.getUsername()).child("CompletedTask").child(task_id);
                                        dbEmployee[0].setValue("completed"); //for employee

                                        String contentforme = "I have completed "+task.getName();
                                        sendNotif(mykey,mykey,"completedJob",contentforme,task_id);
                                        String contentforother= "Employee "+session.getName()+" completed his job of "+task.getName();
                                        //TODO hardcoded coordinator username
                                        sendNotif(mykey,"pulkit","completedJob",contentforother,task_id);
                                        confirmation.dismiss();
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
                    }
                });

                okcanceled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmation.dismiss();
                    }
                });
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    checkPermission();
                } else {
                    launchLibrary();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskdetail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                //TODO Phone call
                break;
            case R.id.item2:
                checkChatref(mykey, id);
                break;
        }
        return true;
    }

    private void checkChatref(final String mykey, final String otheruserkey) {
        DatabaseReference dbChat = DBREF.child("Chats").child(mykey+otheruserkey).getRef();
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("query1" + mykey+otheruserkey);
                System.out.println("datasnap 1" + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    System.out.println("datasnap exists1" + dataSnapshot.toString());
                    dbTablekey = mykey+otheruserkey;
                    goToChatActivity();

                }
                else
                {
                    checkChatref2(mykey,otheruserkey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkChatref2(final String mykey, final String otheruserkey) {
        final DatabaseReference dbChat = DBREF.child("Chats").child(otheruserkey+mykey).getRef();
        dbTablekey = otheruserkey+mykey;
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    System.out.println("query1" + otheruserkey+mykey);
                    goToChatActivity();


                }
                else
                {

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

    private void goToChatActivity()
    {
        Intent in = new Intent(this, ChatActivity.class);
        in.putExtra("dbTableKey",dbTablekey);
        in.putExtra("otheruserkey",id);
        startActivity(in);
    }

    private void launchLibrary()
    {
        dbQuotation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showpd("Downloading");
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
                                    // Successfully downloaded data to local file
                                    // ...
                                    hidepd();
                                    Toast.makeText(TaskDetail.this, "Successfully downloaded", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                            String s = exception.toString();
                            hidepd();
                            Toast.makeText(TaskDetail.this, "Download Failed", Toast.LENGTH_SHORT).show();
                        }


                    });
                } else {
                    Toast.makeText(TaskDetail.this, "No quotation uploaded yet!!", Toast.LENGTH_SHORT).show();
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
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

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
                }
                else
                {
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
                String note = dataSnapshot.getValue(String.class);
                coordinators_message.setText(note);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                String selectedFilePath = "";
                Uri selectedFileUri = data.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //                selectedFilePath = FilePath.getPath(this,selectedFileUri);
                }

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    StorageReference riversRef = mStorageRef.child("Quotation").child(task_id);

                    showpd("Uploading");
                    riversRef.putFile(selectedFileUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Quotation quotation = new Quotation("No");
                                    dbQuotation.setValue(quotation);
                                    Toast.makeText(TaskDetail.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                    hidepd();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(TaskDetail.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                                    hidepd();
                                }
                            });
                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void showpd(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    void hidepd() {
        progressDialog.dismiss();
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

        RecyclerView bigimage = (RecyclerView)viewSelectedImages.findViewById(R.id.bigimage);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        bigimage.setLayoutManager(linearLayoutManager);
        bigimage.setItemAnimator(new DefaultItemAnimator());
        bigimage.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

        adapter = new bigimage_adapter(DescImages, this,this);
        bigimage.setAdapter(adapter);

        bigimage.scrollToPosition(position);
    }

    @Override
    public void ondownloadButtonClicked(int position) {
        // TODO :download task image code here
    }
}
