package com.example.pulkit.employeeapp;

import com.example.pulkit.employeeapp.CheckInternetConnectivity.NetWatcher;
import com.example.pulkit.employeeapp.model.Employee;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;


public class EmployeeApp extends android.support.multidex.MultiDexApplication {
    private static EmployeeApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

    }

    public static synchronized EmployeeApp getInstance() {
        return mInstance;
    }

    /*
    public void setConnectivityListener(NetWatcher.ConnectivityReceiverListener listener) {
        NetWatcher.connectivityReceiverListener = listener;
    }
    */
}