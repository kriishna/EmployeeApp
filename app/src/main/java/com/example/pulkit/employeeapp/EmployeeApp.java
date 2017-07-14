package com.example.pulkit.employeeapp;

import com.example.pulkit.employeeapp.CheckInternetConnectivity.NetWatcher;
import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.model.Employee;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EmployeeApp extends android.support.multidex.MultiDexApplication {
    private static EmployeeApp mInstance;
    public static DatabaseReference DBREF;
    private EmployeeSession session;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        Fresco.initialize(getApplicationContext());

        DBREF = FirebaseDatabase.getInstance().getReference().child("MeChat").getRef();
        session = new EmployeeSession(this);
        String userkey = session.getUsername();
        setOnlineStatus(userkey);
        Fresco.initialize(getApplicationContext());

    }

    public static synchronized EmployeeApp getInstance() {
        return mInstance;
    }

    public static void setOnlineStatus(String userkey)
    {
        if(!userkey.equals("")){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myConnectionsRef = DBREF.child("Users").child("Usersessions").child(userkey).child("online").getRef();

// stores the timestamp of my last disconnect (the last time I was seen online)
//            final DatabaseReference lastOnlineRef = database.getReference().child("Users").child("Usersessions").child(userkey).child("lastseen").getRef();

            final DatabaseReference connectedRef = database.getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        myConnectionsRef.setValue(Boolean.TRUE);
                        myConnectionsRef.onDisconnect().setValue(Boolean.FALSE);

                        // when I disconnect, update the last time I was seen online
//                        lastOnlineRef.onDisconnect().setValue(Calendar.getInstance().getTime()+"");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled at .info/connected");
                }
            });
        }

    }
    public void setConnectivityListener(NetWatcher.ConnectivityReceiverListener listener) {
        NetWatcher.connectivityReceiverListener = listener;
    }
}