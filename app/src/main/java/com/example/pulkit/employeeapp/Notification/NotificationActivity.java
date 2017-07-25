package com.example.pulkit.employeeapp.Notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.notification_adapter;
import com.example.pulkit.employeeapp.model.Notif;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static java.security.AccessController.getContext;

public class NotificationActivity extends AppCompatActivity implements notification_adapter.NotificationAdapterListener{

    RecyclerView recview;
    notification_adapter adapter;
    List<Notif> list = new ArrayList<>();
    Notif notif  = new Notif();
    String Username;
    EmployeeSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification2);

        session = new EmployeeSession(getApplicationContext());
        Username = session.getUsername();

        recview = (RecyclerView) findViewById(R.id.notification_list);
        recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        adapter = new notification_adapter(list, getApplicationContext(),this);
        recview.setAdapter(adapter);

        preparelist();
    }

    private void preparelist()
    {
        final DatabaseReference db = DBREF.child("Notification").child(Username).getRef();
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists()) {
                    notif = dataSnapshot.getValue(Notif.class);
                    list.add(notif);
                    adapter.notifyDataSetChanged();
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

    @Override
    public void onNotificationRowClicked(int position) {

        Intent intent = new Intent(getApplicationContext(),TaskDetail.class);
        Notif notif = list.get(position);
        intent.putExtra("task_id", notif.getTaskId());
        startActivity(intent);
    }
}
