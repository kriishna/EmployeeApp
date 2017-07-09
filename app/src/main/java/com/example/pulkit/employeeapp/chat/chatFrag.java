package com.example.pulkit.employeeapp.chat;


import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;

import com.example.pulkit.employeeapp.MainViews.TaskHome;
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

public class chatFrag extends Fragment {


    RecyclerView task_list;
    DatabaseReference dbTask, db;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> TaskList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private String selectedFilePath;
    Context context;
    ProgressDialog pDialog;
    int i = 0;
    public static String emp_id;
    ChildEventListener ch;
    ValueEventListener vl;

    public chatFrag() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chatfrag, container, false);

        return rootView;
    }
}
