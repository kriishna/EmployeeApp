package com.example.pulkit.employeeapp.EmployeeLogin;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pulkit.employeeapp.model.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class EmployeeSession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode = 0;
    String prefname = "SESSION";
    private String is_loggedin = "is_loggedin";
    private String username = "username", designation="designation";
    private DatabaseReference dbEmp = DBREF.child("Employee").getRef();

    public EmployeeSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(prefname, mode);
        editor = pref.edit();
    }

    public void create_oldusersession(final String username_get, String _designation, String name, String contact, String address) {

   //     this.designation = designation.toLowerCase();
        editor.putString(designation, _designation);
        editor.putBoolean(is_loggedin, true);
        editor.putString(username, username_get);
        editor.putString("name", name);
        editor.putString("contact", contact);
        editor.putString("address", address);
        editor.commit();

    }

    public void edit_oldusersession(String name, String contact, String address) {
        editor.putBoolean(is_loggedin, true);
        editor.putString("name", name);
        editor.putString("contact", contact);
        editor.putString("address", address);
        editor.commit();
    }

    public Boolean isolduser() {
        return pref.getBoolean(is_loggedin, false);
    }

    public String getUsername() {

        return pref.getString(username, "");
    }

    public String getDesig() {

        return pref.getString(designation, "");
    }

    public String getName() {

        return pref.getString("name", "");
    }

    public String getContact() {
        return pref.getString("contact", "");
    }

    public String getAddress() {
        return pref.getString("address", "");
    }

    public void clearoldusersession() {
        editor.clear();
        editor.commit();
    }
    public void set_ShortCutInstalled()
    {
        editor.putBoolean("shortCutInstalled",true);
        editor.commit();
    }
    public Boolean get_ShortCutInstalled()
    {
        return pref.getBoolean("shortCutInstalled",false);
    }

}
