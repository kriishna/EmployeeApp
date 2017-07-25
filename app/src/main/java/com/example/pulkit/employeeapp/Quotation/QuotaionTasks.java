package com.example.pulkit.employeeapp.Quotation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.MainViews.taskFrag;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.taskAdapter;
import com.example.pulkit.employeeapp.model.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static java.security.AccessController.getContext;

public class QuotaionTasks extends AppCompatActivity implements taskAdapter.TaskAdapterListener {

    String start, end, note, id;
    EditText start_edit, end_edit, note_edit;
    RecyclerView recycler;
    DatabaseReference dbTask, db;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Task> TaskList = new ArrayList<>();
    private taskAdapter mAdapter;
    ProgressDialog pDialog;
    ChildEventListener ch;
    ValueEventListener vl;
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotaion_tasks);

        start = getIntent().getStringExtra("start");
        id = getIntent().getStringExtra("id");
        end = getIntent().getStringExtra("end");
        note = getIntent().getStringExtra("note");

        start_edit = (EditText) findViewById(R.id.start_edit);
        end_edit = (EditText) findViewById(R.id.end_edit);
        note_edit = (EditText) findViewById(R.id.note_edit);

        start_edit.setText(start);
        end_edit.setText(end);
        note_edit.setText(note);

        recycler = (RecyclerView) findViewById(R.id.recycler);

        dbTask = DBREF.child("Quotation").child(id).child("tasks").getRef();

        mAdapter = new taskAdapter(TaskList, QuotaionTasks.this, this);
        linearLayoutManager = new LinearLayoutManager(QuotaionTasks.this);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new DividerItemDecoration(QuotaionTasks.this, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(mAdapter);


        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, 2000);

    }

    @Override
    public void onTaskRowClicked(int position) {
        Intent intent = new Intent(this, TaskDetail.class);
        intent.putExtra("task_id", list.get(position));
        startActivity(intent);
    }


    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(QuotaionTasks.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();

        }


        void LoadData() {
            db = DBREF.child("Task").child(list.get(i));


            vl = db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Task task = dataSnapshot.getValue(Task.class);
                    TaskList.add(task);
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        @Override
        protected Void doInBackground(Void... params) {

            ch = dbTask.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                    list.add(dataSnapshot.getKey());
                    LoadData();
                    i++;

                    if (pDialog.isShowing())
                        pDialog.dismiss();


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

        db.removeEventListener(vl);
        dbTask.removeEventListener(ch);
    }

    @Override
    public void onResume() {
        super.onResume();


        i = 0;
        list.clear();
        mAdapter.notifyDataSetChanged();
        new net().execute();

    }
}
