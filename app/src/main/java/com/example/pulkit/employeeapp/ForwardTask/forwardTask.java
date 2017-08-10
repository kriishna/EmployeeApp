package com.example.pulkit.employeeapp.ForwardTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.RecAdapter_emp;
import com.example.pulkit.employeeapp.listener.ClickListener;
import com.example.pulkit.employeeapp.listener.RecyclerTouchListener;
import com.example.pulkit.employeeapp.model.Employee;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class forwardTask extends AppCompatActivity {
    RecyclerView recview;
    RecAdapter_emp adapter;
    List<Employee> list = new ArrayList<Employee>();
    Employee emp;
    ProgressDialog pDialog;
    String task_id,emp_id;
    DatabaseReference db,databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_task);
 //       new net().execute();
        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");
        emp_id = TaskHome.emp_id;


        db = DBREF.child("Employee").child(emp_id).child("AssignedTask").child(task_id);
        db.removeValue();

        databaseReference = DBREF.child("Task").child(task_id);
        databaseReference.child("AssignedTo").child(emp_id).child("datecompleted").setValue(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(forwardTask.this, TaskDetail.class);
        intent.putExtra("task_id",task_id);
        startActivity(intent);
        finish();

    }
}
