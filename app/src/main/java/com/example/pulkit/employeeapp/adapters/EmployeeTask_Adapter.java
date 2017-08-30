package com.example.pulkit.employeeapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pulkit.employeeapp.Customer.custTasks;
import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.helper.FlipAnimator;
import com.example.pulkit.employeeapp.model.CompletedBy;
import com.example.pulkit.employeeapp.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.simpleDateFormat;


public class EmployeeTask_Adapter extends RecyclerView.Adapter<EmployeeTask_Adapter.MyViewHolder>{
    List<String> list = new ArrayList<>();
    private Context context;
    String empId;
    String desig = TaskHome.desig;
    private EmployeeTask_AdapterListener listener;


    public EmployeeTask_Adapter(List<String> list, Context context, String empId, EmployeeTask_AdapterListener listener) {
        this.list = list;
        this.context = context;
        this.empId = empId;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final EmployeeTask_Adapter.MyViewHolder holder, final int position) {

        DBREF.child("Task").child(list.get(position)).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    String iconText = task.getName().toUpperCase();

                    holder.icon_text.setText(iconText.charAt(0) + "");
                    holder.imgProfile.setImageResource(R.drawable.bg_circle);
                    holder.imgProfile.setColorFilter(task.getColor());
//                    holder.timestamp.setText(task.getExpEndDate());
                    holder.taskname.setText(iconText);
                    DBREF.child("Task").child(task.getTaskId()).child("AssignedTo").child(empId).child("datecompleted").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String temp = dataSnapshot.getValue(String.class);
                                holder.timestamp.setText(temp);
                                applyBackgroundColor(holder,temp);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    if(empId.equals("accounts")&&!task.getCustomerId().equals(custTasks.custId))
                        holder.base_container.setVisibility(View.GONE);
                    else
                        holder.base_container.setVisibility(View.VISIBLE);

                    if(desig.toLowerCase().equals("accounts")){
                        DBREF.child("Task").child(task.getTaskId()).child("Quotation").child("url").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                    holder.customername.setText("Quotation Uploaded : YES");
                                else
                                    holder.customername.setText("Quotation Uploaded : NO");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        DatabaseReference dbCustomerName = DBREF.child("Customer").child(task.getCustomerId()).getRef();
                        dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String customername = dataSnapshot.child("name").getValue(String.class);
                                holder.customername.setText(customername);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    applyClickEvents(holder, position);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void applyBackgroundColor(EmployeeTask_Adapter.MyViewHolder holder, String deadline) {

        try {
            String curdate = simpleDateFormat.format(Calendar.getInstance().getTime());
            Date curDate = simpleDateFormat.parse(curdate);
            if (deadline != null) {
                Date aDate = simpleDateFormat.parse(deadline);

                if (curDate.compareTo(aDate) > -1) {
                    holder.base_container.setBackgroundResource(R.color.colorAccent);
                } else {
                    holder.base_container.setBackgroundColor(Color.WHITE);
                }
            } else
                holder.base_container.setBackgroundColor(Color.WHITE);


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskname, customername, timestamp, icon_text;
        ImageView imgProfile;
        RelativeLayout base_container;
        public LinearLayout messageContainer;
        RelativeLayout iconBack, iconFront, iconContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            taskname = (TextView) itemView.findViewById(R.id.tv_taskname);
            customername = (TextView) itemView.findViewById(R.id.tv_customerName);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            icon_text = (TextView) itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
            base_container = (RelativeLayout) itemView.findViewById(R.id.base_container);
            iconBack = (RelativeLayout) itemView.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) itemView.findViewById(R.id.icon_front);
            iconContainer = (RelativeLayout) itemView.findViewById(R.id.icon_container);

        }



    }

    public interface EmployeeTask_AdapterListener {
        void onRowClick(int position, MyViewHolder holder);

    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {
        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRowClick(position, holder);
            }
        });
    }


}
