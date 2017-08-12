package com.example.pulkit.employeeapp.Customer;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.custAdapter;
import com.example.pulkit.employeeapp.adapters.taskAdapter;
import com.example.pulkit.employeeapp.model.Customer;
import com.example.pulkit.employeeapp.model.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class custFrag extends Fragment implements custAdapter.CustomerAdapterListener{

    RecyclerView task_list;
    DatabaseReference dbTask, db,dbCust;
    LinearLayoutManager linearLayoutManager;
    private List<String> custList = new ArrayList<>();
    private ArrayList<Customer> Cust = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    ProgressDialog pDialog;
    int i = 0,j=0;
    public static String emp_id;
    ChildEventListener ch;
    ValueEventListener vl,custl;
    EmployeeSession session;

    public custFrag() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chatfrag, container, false);

        session = new EmployeeSession(getActivity());
        pDialog = new ProgressDialog(getContext());
        emp_id = session.getUsername();
        task_list = (RecyclerView) rootView.findViewById(R.id.recycler);

        dbTask = DBREF.child("Employee").child(emp_id).child("AssignedTask").getRef();

        mAdapter = new custAdapter(Cust, getActivity(), this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        task_list.setLayoutManager(linearLayoutManager);
        task_list.setItemAnimator(new DefaultItemAnimator());
        task_list.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        task_list.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, 2000);

        return rootView;
    }

    @Override
    public void onCustomerRowClicked(int position) {
        Intent intent = new Intent(getContext(),custTasks.class);
        //Task task = TaskList.get(position);
        Customer cust = Cust.get(position);
        intent.putExtra("customerId",cust.getId());
  //      intent.putExtra("task_id",task.getTaskId());
        startActivity(intent);
    }

    void LoadTasks() {
        db = DBREF.child("Task").child(list.get(i));
        vl = db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                if(!custList.contains(task.getCustomerId())) {
                    custList.add(task.getCustomerId());
                    LoadCustomers();
                    j++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void LoadCustomers() {
        dbCust = DBREF.child("Customer").child(custList.get(j));
        custl = dbCust.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Customer c = dataSnapshot.getValue(Customer.class);

                if(!Cust.contains(c)) {
                    Cust.add(c);
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
                        LoadTasks();
                        i++;
                        if (pDialog.isShowing())
                            pDialog.dismiss();
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
        if(ch!=null)
            dbTask.removeEventListener(ch);
        if(vl!=null)
            db.removeEventListener(vl);
        if(custl!=null)
            dbCust.removeEventListener(custl);
    }

    @Override
    public void onResume() {
        super.onResume();
        i = 0;
        j=0;
        list.clear();
        custList.clear();
        Cust.clear();
        mAdapter.notifyDataSetChanged();
        new net().execute();

    }



}
