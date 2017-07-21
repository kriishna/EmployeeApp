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

    public void create_oldusersession(final String username_get,String designation,String name)
    {
                editor.putString("designation",designation.toLowerCase());
                editor.putString(is_loggedin,"true");
                editor.putString(username,username_get);
                editor.putString("name",name);
                editor.commit();

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
    public String getName()
    {

        return pref.getString("name","");
    }

    public void clearoldusersession()
    {
        editor.clear();
        editor.commit();
    }
}
