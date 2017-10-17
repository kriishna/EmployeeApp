package com.example.pulkit.employeeapp.MainViews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.pulkit.employeeapp.EmployeeLogin.empLogin;
import com.example.pulkit.employeeapp.adapters.EmployeeTask_Adapter;
import com.example.pulkit.employeeapp.helper.DividerItemDecoration;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.taskAdapter;
import com.example.pulkit.employeeapp.model.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class taskFrag extends Fragment implements EmployeeTask_Adapter.EmployeeTask_AdapterListener {

    RecyclerView task_list;
    DatabaseReference dbTask;
    LinearLayoutManager linearLayoutManager;
    List<String> listoftasks = new ArrayList<>();
    private EmployeeTask_Adapter mAdapter;
    ProgressDialog pDialog;
    int i = 0;
    public static String emp_id;
    ValueEventListener vl;
    EmployeeSession session;
    EmployeeTask_Adapter.EmployeeTask_AdapterListener listener;

    public taskFrag() {
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
        listener = this;
        dbTask = DBREF.child("Employee").child(emp_id).child("AssignedTask").getRef();
        mAdapter = new EmployeeTask_Adapter(listoftasks, getActivity(), emp_id, listener);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        task_list.setLayoutManager(linearLayoutManager);
        task_list.setItemAnimator(new DefaultItemAnimator());
        task_list.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        new net().execute();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, 2000);

        return rootView;
    }

    @Override
    public void onRowClick(int position, EmployeeTask_Adapter.MyViewHolder holder) {

        Intent intent = new Intent(getContext(), TaskDetail.class);
        if ( listoftasks.size()>0 && !listoftasks.get(position).equals("")) {
            intent.putExtra("task_id", listoftasks.get(position));
            startActivity(intent);
        }
        else{
            startActivity(new Intent(getContext(),empLogin.class));
        }
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

            vl = dbTask.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listoftasks.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            if (childSnapshot.getValue().toString().equals("pending"))
                                listoftasks.add(childSnapshot.getKey());
                        }
                        mAdapter = new EmployeeTask_Adapter(listoftasks, getActivity(), emp_id, listener);
                        task_list.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }

                    if (pDialog.isShowing())
                        pDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }


}
