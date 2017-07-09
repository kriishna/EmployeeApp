package com.example.pulkit.employeeapp.EmployeeLogin;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pulkit.employeeapp.model.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeSession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode=0;
    String prefname="SESSION";
    private String is_loggedin = "is_loggedin";
    private String username = "username",designation;
    private DatabaseReference dbEmp = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").getRef();

    public EmployeeSession(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(prefname,mode);
        editor = pref.edit();
    }

    public void create_oldusersession(final String username_get)
    {
        DatabaseReference dbRef = dbEmp.child(username_get).getRef();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Employee employee = dataSnapshot.getValue(Employee.class);
                editor.putString("designation",employee.getDesignation().toLowerCase());
                editor.putString(is_loggedin,"true");
                editor.putString(username,employee.getUsername());
                editor.putString(designation,employee.getDesignation());
                editor.commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }

    public String isolduser()
    {
        return pref.getString(is_loggedin,"");
    }

    public String getUsername()
    {

        return pref.getString(username,"");
    }

    public String getDesig()
    {

        return pref.getString(designation,"");
    }

    public void clearoldusersession()
    {
        editor.clear();
        editor.commit();
    }
}
