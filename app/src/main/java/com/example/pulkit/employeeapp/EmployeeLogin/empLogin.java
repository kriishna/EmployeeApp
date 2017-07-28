package com.example.pulkit.employeeapp.EmployeeLogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.pulkit.employeeapp.EmployeeApp;
import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class empLogin extends AppCompatActivity {

    EditText username, password;
    Button button;
    String Username , Password;
    DatabaseReference database;
    EmployeeSession session;
    TextInputLayout input_email, input_password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_login);

        sharedPreferences = getSharedPreferences("myFCMToken",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(FirebaseInstanceId.getInstance().getToken()!=null)
        {
            editor.putString("myFCMToken",FirebaseInstanceId.getInstance().getToken());
            editor.commit();
        }

        session = new EmployeeSession(getApplicationContext());


      /*  if(session.isolduser().equals(true))*/{
            Intent intent = new Intent(empLogin.this, TaskHome.class);
            intent.putExtra("emp_id",session.getUsername());
            intent.putExtra("desig",session.getDesig());
            startActivity(intent);
            finish();
        }

        username = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText3);
        button = (Button) findViewById(R.id.login);
        input_email = (TextInputLayout)findViewById(R.id.input_emaillogin);
        input_password = (TextInputLayout)findViewById(R.id.input_passwordlogin);
        database = DBREF.child("Employee").getRef();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = username.getText().toString().trim();
                Password = password.getText().toString().trim();

                if (TextUtils.isEmpty(Username)) {
                    input_email.setError("Enter Email");
                    if (input_email.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }

                if (TextUtils.isEmpty(Password)) {
                    input_password.setError("Enter Password");
                    if (input_password.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                    }
                }

                if(!TextUtils.isEmpty(Username) && !TextUtils.isEmpty(Password)){
                    login();
                }
                else
                    Toast.makeText(getBaseContext(),"Enter Complete Details", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void login() {
        DatabaseReference db = DBREF.child("Employee").child(Username).getRef();

            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {

                        Employee employee = dataSnapshot.getValue(Employee.class);

                        if (!employee.getPassword().equals(Password)) {
                            Toast.makeText(getBaseContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            session.create_oldusersession(Username,employee.getDesignation(),employee.getName(),employee.getPhone_num(),employee.getAddress());
                            EmployeeApp.setOnlineStatus(Username);
                            String myFCMToken;
                            if(FirebaseInstanceId.getInstance().getToken()==null)
                                myFCMToken =sharedPreferences.getString("myFCMToken","");

                            else
                                myFCMToken = FirebaseInstanceId.getInstance().getToken();

                            if(!myFCMToken.equals("")) {
                                DBREF.child("Fcmtokens").child(Username).child("token").setValue(myFCMToken);
                                Intent intent = new Intent(empLogin.this, TaskHome.class);
                                intent.putExtra("emp_id",Username);
                                intent.putExtra("desig",employee.getDesignation());
                                startActivity(intent);
                                finish();

                            }
                                else
                                Toast.makeText(empLogin.this,"You will need to clear the app data or reinstall the app to make it work properly",Toast.LENGTH_LONG).show();

                            }
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Student Not Registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}
