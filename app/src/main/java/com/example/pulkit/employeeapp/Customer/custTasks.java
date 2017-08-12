package com.example.pulkit.employeeapp.Customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
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
import android.widget.Toast;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.MainViews.taskFrag;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.taskAdapter;
import com.example.pulkit.employeeapp.helper.FilePath;
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
import java.util.Date;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class custTasks extends AppCompatActivity implements taskAdapter.TaskAdapterListener {

    RecyclerView recycler;
    taskAdapter mAdapter;
    String custId;

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
    int m = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_tasks);

        recycler = (RecyclerView) findViewById(R.id.recycler);

        custId = getIntent().getStringExtra("customerId");
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

                    if(!dataSnapshot.exists()){
                        if (pDialog.isShowing())
                            pDialog.dismiss();

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
                Return();
                break;

            case R.id.upload:
                UploadQuotation();
                break;
        }
        return true;
    }

    private void Return() {
        DatabaseReference db, databaseReference;

        for (int j = 0; j < TaskList.size(); j++) {
            db = DBREF.child("Employee").child(emp_id).child("AssignedTask").child(TaskList.get(j).getTaskId());
            db.setValue("complete");

            databaseReference = DBREF.child("Task").child(TaskList.get(j).getTaskId());
            databaseReference.child("AssignedTo").child(emp_id).child("datecompleted")
                    .setValue(new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(custTasks.this, "Task Returned", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        mAdapter.notifyDataSetChanged();
    }

}
