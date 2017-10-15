package com.example.pulkit.employeeapp.Customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.MainViews.TaskDetail;
import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.MainViews.taskFrag;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.adapters.ViewImageAdapter;
import com.example.pulkit.employeeapp.adapters.taskAdapter;
import com.example.pulkit.employeeapp.helper.CompressMe;
import com.example.pulkit.employeeapp.helper.FilePath;
import com.example.pulkit.employeeapp.helper.MarshmallowPermissions;
import com.example.pulkit.employeeapp.listener.ClickListener;
import com.example.pulkit.employeeapp.listener.RecyclerTouchListener;
import com.example.pulkit.employeeapp.model.CompletedBy;
import com.example.pulkit.employeeapp.model.CompletedJob;
import com.example.pulkit.employeeapp.model.CustomerAccount;
import com.example.pulkit.employeeapp.model.Task;
import com.example.pulkit.employeeapp.services.UploadQuotationService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.sendNotif;
import static com.example.pulkit.employeeapp.EmployeeApp.sendNotifToAllCoordinators;
import static com.example.pulkit.employeeapp.EmployeeApp.simpleDateFormat;

public class custTasks extends AppCompatActivity implements taskAdapter.TaskAdapterListener {

    private AlertDialog viewSelectedImages1;
    private ArrayList<String> docPaths = new ArrayList<>(), photoPaths = new ArrayList<>();
    CompressMe compressMe;
    String item;
    ViewImageAdapter madapter;

    private static final int REQUEST_CODE = 111;
    RecyclerView recycler;
    taskAdapter mAdapter;
    String customerName;
    public static String custId;
    private static final int PICK_FILE_REQUEST = 1;
    DatabaseReference dbTask, db;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> TaskList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    ProgressDialog pDialog;
    int i = 0;
    public static String emp_id;
    ChildEventListener ch;
    ValueEventListener vl;
    EmployeeSession session;
    private AlertDialog confirmation;
    int m = 0;
    private ActionModeCallback actionModeCallback;

    private ActionMode actionMode;
    private MarshmallowPermissions marshMallowPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_tasks);
        actionModeCallback =new ActionModeCallback();
        recycler = (RecyclerView) findViewById(R.id.recycler);
        marshMallowPermission = new MarshmallowPermissions(this);
        compressMe = new CompressMe(this);

        custId = getIntent().getStringExtra("customerId");
        //       customerName = getIntent().getStringExtra("customerName");

        // to get customer name
        DBREF.child("Customer").child(custId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customerName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        session = new EmployeeSession(this);
        pDialog = new ProgressDialog(this);
        emp_id = session.getUsername();

        dbTask = DBREF.child("Employee").child(emp_id).child("AssignedTask").getRef();

        mAdapter = new taskAdapter(TaskList, this, this);
        linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, 2500);

    }

    private void UploadQuotation() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(custTasks.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.options_foruploadquotation, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(custTasks.this);
        alertDialogBuilderUserInput.setView(mView);

        LinearLayout uploadPhoto = (LinearLayout) mView.findViewById(R.id.uploadPhoto);
        LinearLayout uploadDoc = (LinearLayout) mView.findViewById(R.id.uploadDoc);


        alertDialogBuilderUserInput.setCancelable(true);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePickerBuilder.getInstance().setMaxCount(10)
                        .setActivityTheme(R.style.AppTheme)
                        .pickPhoto(custTasks.this);
                alertDialogAndroid.dismiss();
            }
        });
        uploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePickerBuilder.getInstance().setMaxCount(1)
                        .setActivityTheme(R.style.AppTheme)
                        .pickFile(custTasks.this);
                alertDialogAndroid.dismiss();
            }
        });

        Toast.makeText(custTasks.this,"Uploading...",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

                    if (photoPaths.size() > 0) {
                        viewSelectedImages1 = new AlertDialog.Builder(custTasks.this)
                                .setView(R.layout.activity_view_selected_image).create();
                        viewSelectedImages1.show();

                        final ImageView ImageViewlarge = (ImageView) viewSelectedImages1.findViewById(R.id.ImageViewlarge);
                        ImageButton cancel = (ImageButton) viewSelectedImages1.findViewById(R.id.cancel);
                        ImageButton canceldone = (ImageButton) viewSelectedImages1.findViewById(R.id.canceldone);
                        ImageButton okdone = (ImageButton) viewSelectedImages1.findViewById(R.id.okdone);
                        RecyclerView rv = (RecyclerView) viewSelectedImages1.findViewById(R.id.viewImages);

                        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        rv.setLayoutManager(linearLayoutManager);
                        rv.setItemAnimator(new DefaultItemAnimator());
                        rv.addItemDecoration(new com.example.pulkit.employeeapp.helper.DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

                        madapter = new ViewImageAdapter(photoPaths, this);
                        rv.setAdapter(madapter);


                        madapter.notifyDataSetChanged();

                        item = photoPaths.get(0);
                        ImageViewlarge.setImageURI(Uri.parse(item));

                        rv.performClick();

                        rv.addOnItemTouchListener(new RecyclerTouchListener(this, rv, new ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                madapter.selectedPosition = position;
                                madapter.notifyDataSetChanged();
                                item = photoPaths.get(position);
                                ImageViewlarge.setImageURI(Uri.parse(item));
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));

                        cancel.setVisibility(View.GONE);

                        canceldone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                photoPaths.clear();
                                viewSelectedImages1.dismiss();
                            }
                        });

                        okdone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String l = compressMe.compressImage(photoPaths.get(0), custTasks.this);
                                uploadFile(l, "photo");
                                viewSelectedImages1.dismiss();

                            }
                        });

                    }
                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    for (String result : docPaths) {
                        uploadFile(result, "doc");
                    }
                }
                break;

        }
    }
    private void uploadFile(String filePath, String type) {

        if (filePath != null && !filePath.equals("")) {
            ArrayList<String> taskid_list = new ArrayList<>();
            List<Integer> selectedItemPositions = mAdapter.getSelectedItems();

            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                selectedItemPositions.get(i);
                final Task task = TaskList.get(selectedItemPositions.get(i));
                taskid_list.add(task.getTaskId());
            }

            mAdapter.clearSelections();
            mAdapter.resetAnimationIndex();
            actionMode.finish();
            String temp = Uri.fromFile(new File(filePath)).toString();

            Intent serviceIntent = new Intent(this, UploadQuotationService.class);
            serviceIntent.putExtra("TaskIdList", taskid_list);
            serviceIntent.putExtra("customerId", custId);
            serviceIntent.putExtra("selectedFileUri", temp);
            serviceIntent.putExtra("customerName",customerName);

            this.startService(serviceIntent);
        } else {
            Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onTaskRowClicked(int position) {
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {

            Intent intent = new Intent(this, TaskDetail.class);
            Task task = TaskList.get(position);
            intent.putExtra("customerId", task.getCustomerId());
            intent.putExtra("task_id", task.getTaskId());
            startActivity(intent);
        }
    }

    void LoadData() {
        db = DBREF.child("Task").child(list.get(i));

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                if (task.getCustomerId().equals(custId) && !TaskList.contains(task)) {
                    TaskList.add(task);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

            ch = dbTask.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue().toString().equals("pending")) {
                        list.add(dataSnapshot.getKey());
                        LoadData();
                        i++;
                        if (pDialog.isShowing())
                            pDialog.dismiss();

                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Intent i = new Intent(custTasks.this, custTasks.class);
                    i.putExtra("customerId", custId);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Intent i = new Intent(custTasks.this, custTasks.class);
                    i.putExtra("customerId", custId);
                    startActivity(i);
                    finish();
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
        if (ch != null)
            dbTask.removeEventListener(ch);
        if (vl != null)
            db.removeEventListener(vl);
    }

    @Override
    public void onResume() {
        super.onResume();
        i = 0;
        list.clear();
        TaskList.clear();
        mAdapter.notifyDataSetChanged();
        new net().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_quotation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_account:
                AlertDialog customerAccountDialog = new AlertDialog.Builder(this)
                        .setView(R.layout.account_info_layout)
                        .create();
                customerAccountDialog.show();
                final Button edit, submit;
                final EditText total, advance, balance;
                final LinearLayout balanceLayout;
                total = (EditText) customerAccountDialog.findViewById(R.id.total);
                advance = (EditText) customerAccountDialog.findViewById(R.id.advance);
                balance = (EditText) customerAccountDialog.findViewById(R.id.balance);
                edit = (Button) customerAccountDialog.findViewById(R.id.edit);
                submit = (Button) customerAccountDialog.findViewById(R.id.submit);
                balanceLayout = (LinearLayout) customerAccountDialog.findViewById(R.id.balanceLayout);
                final DatabaseReference dbaccountinfo = DBREF.child("Customer").child(custId).child("Account").getRef();
                ValueEventListener dbaccountlistener = dbaccountinfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            CustomerAccount customerAccount = dataSnapshot.getValue(CustomerAccount.class);
                            total.setText(customerAccount.getTotal() + "");
                            advance.setText(customerAccount.getAdvance() + "");
                            balance.setText((customerAccount.getTotal() - customerAccount.getAdvance()) + "");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        total.setEnabled(true);
                        advance.setEnabled(true);
                        balanceLayout.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.GONE);
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CustomerAccount customerAccount = new CustomerAccount();
                        String totalString =total.getText().toString().trim();
                        String advanceTotal = advance.getText().toString().trim();
                        if(totalString!=null&&advanceTotal!=null&&!totalString.equals("")&&!advanceTotal.equals("")) {
                            Integer total_amount = Integer.parseInt(totalString);
                            customerAccount.setTotal(total_amount);
                            Integer advance_amount = Integer.parseInt(advanceTotal);
                            customerAccount.setAdvance(advance_amount);
                            if(total_amount<advance_amount){
                                Toast.makeText(getApplicationContext(),"Invalid amount entered",Toast.LENGTH_SHORT).show();
                            }
                            else{
                            dbaccountinfo.setValue(customerAccount);
                            total.setEnabled(false);
                            advance.setEnabled(false);
                            balanceLayout.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.GONE);
                            edit.setVisibility(View.VISIBLE);
                            sendNotif(emp_id, emp_id, "accountReset", "You modified the account details of " + customerName+".", custId);
                            sendNotif(emp_id, custId, "accountReset", "Your advance deposited is Rs." + advance_amount + " and balance left is Rs." + (total_amount - advance_amount), custId);
                            sendNotifToAllCoordinators(emp_id, "accountReset",session.getName()+" modified account details of "+ customerName + ". Advance deposited is Rs." + advance_amount + " and balance left is Rs." + (total_amount - advance_amount), custId);
                        }}
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid amount entered",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        }
        return true;
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            //actionMode = null;
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.uploadquotation:
                    if (!marshMallowPermission.checkPermissionForExternalStorage())
                        marshMallowPermission.requestPermissionForExternalStorage();
                    else {
                        UploadQuotation();
                        }
                    return true;

                case R.id.Forwardtocoordinator:
                    final ArrayList<Task> taskid_list = new ArrayList<>();
                    List<Integer> selectedItemPositions = mAdapter.getSelectedItems();

                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        selectedItemPositions.get(i);
                        final Task task = TaskList.get(selectedItemPositions.get(i));
                        taskid_list.add(task);
                    }

                    mAdapter.clearSelections();
                    mAdapter.resetAnimationIndex();
                    actionMode.finish();

                    confirmation = new AlertDialog.Builder(custTasks.this)
                            .setView(R.layout.confirmation_layout).create();
                    confirmation.show();
                    final EditText employeeNote = (EditText) confirmation.findViewById(R.id.employeeNote);
                    Button okcompleted = (Button) confirmation.findViewById(R.id.okcompleted);
                    Button okcanceled = (Button) confirmation.findViewById(R.id.okcanceled);

                    okcanceled.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmation.dismiss();
                        }
                    });

                    okcompleted.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String employeesnote = employeeNote.getText().toString().trim();
                            Calendar c = Calendar.getInstance();
                            final String curdate = simpleDateFormat.format(c.getTime());
                            confirmation.dismiss();

                            for (final Task task : taskid_list) {
                                final DatabaseReference db, databaseReference;

                                DBREF.child("Employee").child(emp_id).child("CompletedTask").child(task.getTaskId()).setValue("complete");
                                db = DBREF.child("Employee").child(emp_id).child("AssignedTask").child(task.getTaskId());
                                db.removeValue();

                                databaseReference = DBREF.child("Task").child(task.getTaskId()).child("AssignedTo").child(emp_id);

                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            CompletedBy completedBy = dataSnapshot.getValue(CompletedBy.class);
                                            CompletedJob completedJob = new CompletedJob();
                                            completedJob.setEmpId(completedBy.getEmpId());
                                            completedJob.setAssignedByName(completedBy.getAssignedByName());
                                            completedJob.setAssignedByUsername(completedBy.getAssignedByUsername());
                                            completedJob.setCoordinatorNote(completedBy.getNote());
                                            completedJob.setDateassigned(completedBy.getDateassigned());
                                            completedJob.setDatecompleted(curdate);
                                            completedJob.setEmpployeeNote(employeesnote);
                                            completedJob.setEmpName(session.getName());
                                            completedJob.setEmpDesignation(session.getDesig());
                                            databaseReference.removeValue();

                                            DBREF.child("Task").child(task.getTaskId()).child("CompletedBy").child(emp_id).setValue(completedJob);
                                            String contentforme = "You completed " + task.getName();
                                            sendNotif(emp_id, emp_id, "completedJob", contentforme, task.getTaskId());
                                            String contentforother = "Employee " + session.getName() + " completed " + task.getName();
                                            sendNotif(emp_id, completedJob.getAssignedByUsername(), "completedJob", contentforother, task.getTaskId());
                                            Toast.makeText(getApplicationContext(), contentforme, Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            if(taskid_list.size()==TaskList.size()) {
                                Intent intent = new Intent(getApplicationContext(), TaskHome.class);
                                startActivity(intent);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                            }
                            else
                            {
                                Intent i = new Intent(custTasks.this, custTasks.class);
                                i.putExtra("customerId", custId);
                                startActivity(i);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                            }
                        }
                    });

                return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }


}
