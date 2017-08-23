package com.example.pulkit.employeeapp.Customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.MainViews.taskFrag;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.taskAdapter;
import com.example.pulkit.employeeapp.helper.FilePath;
import com.example.pulkit.employeeapp.model.CompletedBy;
import com.example.pulkit.employeeapp.model.CompletedJob;
import com.example.pulkit.employeeapp.model.Task;
import com.example.pulkit.employeeapp.services.UploadQuotationService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.sendNotif;

public class custTasks extends AppCompatActivity implements taskAdapter.TaskAdapterListener {

    RecyclerView recycler;
    taskAdapter mAdapter;
    String custId,customerName;

    private static final int PICK_FILE_REQUEST = 1;
    DatabaseReference dbTask, db;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> TaskList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    ProgressDialog pDialog;
    int i = 0;
    public static String emp_id;
    ChildEventListener ch;
    ValueEventListener vl;
    EmployeeSession session;
    Button upload;
    private AlertDialog confirmation;
    int m = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_tasks);

        recycler = (RecyclerView) findViewById(R.id.recycler);

        custId = getIntent().getStringExtra("customerId");
        customerName = getIntent().getStringExtra("customerName");

        session = new EmployeeSession(this);
        pDialog = new ProgressDialog(this);
        emp_id = session.getUsername();

        dbTask = DBREF.child("Employee").child(emp_id).child("AssignedTask").getRef();

        mAdapter = new taskAdapter(TaskList, this, this);
        linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, 2500);

    }

    private void UploadQuotation() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    selectedFilePath = FilePath.getPath(this, selectedFileUri);
                }

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    //            mAdapter.resetAnimationIndex();
                    //            List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
                    ArrayList<String> taskid_list = new ArrayList<>();

                    for (int i = 0; i < TaskList.size(); i++) {
                        final Task task = TaskList.get(i);
                        taskid_list.add(task.getTaskId());
                    }

                    Intent serviceIntent = new Intent(this, UploadQuotationService.class);
                    serviceIntent.putExtra("TaskIdList", taskid_list);
                    serviceIntent.putExtra("selectedFileUri", selectedFileUri.toString());
                    serviceIntent.putExtra("customerId",custId);
                    serviceIntent.putExtra("customerName",customerName);

                    this.startService(serviceIntent);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    @Override
    public void onTaskRowClicked(int position) {
        Intent intent = new Intent(this, TaskDetail.class);
        Task task = TaskList.get(position);
        intent.putExtra("customerId", task.getCustomerId());
        intent.putExtra("task_id", task.getTaskId());
        startActivity(intent);
    }

    void LoadData() {
        db = DBREF.child("Task").child(list.get(i));

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                if (task.getCustomerId().equals(custId) && !TaskList.contains(task)) {
                    TaskList.add(task);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            ch = dbTask.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue().toString().equals("pending")) {
                        list.add(dataSnapshot.getKey());
                        LoadData();
                        i++;
                        if (pDialog.isShowing())
                            pDialog.dismiss();

                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (ch != null)
            dbTask.removeEventListener(ch);
        if (vl != null)
            db.removeEventListener(vl);
    }

    @Override
    public void onResume() {
        super.onResume();
        i = 0;
        list.clear();
        TaskList.clear();
        mAdapter.notifyDataSetChanged();
        new net().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_quotation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.forward:
                confirmation = new AlertDialog.Builder(this)
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
                        Calendar c = Calendar.getInstance();
                        final String curdate =  new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                        confirmation.dismiss();

                        for(final Task task:TaskList)
                        {
                        final DatabaseReference db, databaseReference;

                        DBREF.child("Employee").child(emp_id).child("CompletedTask").child(task.getTaskId()).setValue("complete");
                        db = DBREF.child("Employee").child(emp_id).child("AssignedTask").child(task.getTaskId());
                        db.removeValue();

                        databaseReference = DBREF.child("Task").child(task.getTaskId()).child("AssignedTo").child(emp_id);

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
                                    completedJob.setDatecompleted(curdate);
                                    completedJob.setEmpployeeNote(employeesnote);
                                    completedJob.setEmpName(session.getName());
                                    completedJob.setEmpDesignation(session.getDesig());
                                    databaseReference.removeValue();

                                    DBREF.child("Task").child(task.getTaskId()).child("CompletedBy").child(emp_id).setValue(completedJob);
                                    String contentforme = "You completed " + task.getName();
                                    sendNotif(emp_id, emp_id, "completedJob", contentforme, task.getTaskId());
                                    String contentforother = "Employee " + session.getName() + " completed " + task.getName();
                                    sendNotif(emp_id, completedJob.getAssignedByUsername(), "completedJob", contentforother, task.getTaskId());
                                    Toast.makeText(getApplicationContext(), contentforme, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    Intent intent = new Intent(getApplicationContext(), TaskHome.class);
                        startActivity(intent);
                        finish();
                    }
                });


                break;

            case R.id.upload:
                if(TaskList.size()<1)
                UploadQuotation();
                break;
        }
        return true;
    }

}
