package com.example.pulkit.employeeapp.Quotation;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.example.pulkit.employeeapp.helper.DividerItemDecoration;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.QuotationBatch;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class QuotationGroups extends Fragment implements QAdapter.QAdapterListener {

    RecyclerView recycler;
    DatabaseReference dbTask;
    LinearLayoutManager linearLayoutManager;
    private List<QuotationBatch> list = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    ProgressDialog pDialog;
    int i = 0;
    public String emp_id;
    ChildEventListener ch;

    public QuotationGroups() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quotationfrag, container, false);

        recycler = (RecyclerView) rootView.findViewById(R.id.recycler);

        emp_id = TaskHome.emp_id;

        dbTask = DBREF.child("Employee").child(emp_id).child("AssignedTask").getRef();

        mAdapter = new QAdapter(list, getActivity(), (QAdapter.QAdapterListener) this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recycler.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, 2000);

        return rootView;
    }

    @Override
    public void onTaskRowClicked(int position) {
        Intent intent = new Intent(getContext(), QuotaionTasks.class);
        QuotationBatch batch = list.get(position);
        intent.putExtra("id", batch.getId());
        intent.putExtra("note", batch.getNote());
        intent.putExtra("end", batch.getEndDate());
        intent.putExtra("start", batch.getStartDate());
        startActivity(intent);

    }

    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            ch = dbTask.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    QuotationBatch batch = dataSnapshot.getValue(QuotationBatch.class);

                    list.add(batch);
                    mAdapter.notifyDataSetChanged();

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
        if (!dbTask.equals(null))
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

