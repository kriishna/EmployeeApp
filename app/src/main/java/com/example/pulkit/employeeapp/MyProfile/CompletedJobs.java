package com.example.pulkit.employeeapp.MyProfile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.pulkit.employeeapp.helper.DividerItemDecoration;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.completedjobs_adapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class CompletedJobs extends AppCompatActivity {

    RecyclerView rec_completedjobs_list;
    ArrayList<String> completedjobs_list = new ArrayList<>();
    completedjobs_adapter completedjobs_adapter;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference dbEmployee;
    EmployeeSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_jobs);

        rec_completedjobs_list = (RecyclerView) findViewById(R.id.completedJobs_list);
        session = new EmployeeSession(getApplicationContext());

        completedjobs_adapter = new completedjobs_adapter(completedjobs_list, getApplicationContext());
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rec_completedjobs_list.setLayoutManager(linearLayoutManager);
        rec_completedjobs_list.setItemAnimator(new DefaultItemAnimator());
        rec_completedjobs_list.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rec_completedjobs_list.setAdapter(completedjobs_adapter);

        dbEmployee = DBREF.child("Employee").child(session.getUsername()).child("CompletedTask").getRef();

        LoadData();
    }

    void LoadData() {

        dbEmployee.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String taskid = dataSnapshot.getKey();
                    completedjobs_list.add(taskid);
                    completedjobs_adapter.notifyDataSetChanged();
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
    }
}