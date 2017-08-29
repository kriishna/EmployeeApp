package com.example.pulkit.employeeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pulkit.employeeapp.Customer.custTasks;
import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class taskAdapter extends RecyclerView.Adapter<taskAdapter.MyViewHolder> {
    ArrayList<Task> list = new ArrayList<>();
    private Context context;
    String desig = TaskHome.desig;
    private TaskAdapterListener listener;


    public taskAdapter(ArrayList<Task> list, Context context, TaskAdapterListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskname, customername, timestamp, icon_text;
        ImageView imgProfile;
        public LinearLayout messageContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            taskname = (TextView) itemView.findViewById(R.id.tv_taskname);
            customername = (TextView) itemView.findViewById(R.id.tv_customerName);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            icon_text = (TextView) itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final taskAdapter.MyViewHolder holder, int position) {
        Task task = list.get(position);
        String iconText = task.getName().toUpperCase();

        holder.icon_text.setText(iconText.charAt(0) + "");
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(task.getColor());
   //     holder.timestamp.setText(task.getExpEndDate());
        holder.taskname.setText(iconText);


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

        DBREF.child("Task").child(task.getTaskId()).child("AssignedTo").child(custTasks.emp_id).child("datecompleted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String temp = dataSnapshot.getValue(String.class);
                    holder.timestamp.setText(temp);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        applyClickEvents(holder, position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface TaskAdapterListener {
        void onTaskRowClicked(int position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTaskRowClicked(position);
            }
        });

    }

}
