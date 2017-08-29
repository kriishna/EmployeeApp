package com.example.pulkit.employeeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pulkit.employeeapp.MainViews.TaskHome;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.Customer;
import com.example.pulkit.employeeapp.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class newcustAdapter extends RecyclerView.Adapter<newcustAdapter.MyViewHolder> {
    ArrayList<String> list = new ArrayList<>();
    private Context context;
    private CustomerAdapterListener listener;
    String emp_id;


    public newcustAdapter(ArrayList<String> list, Context context, String emp_id, CustomerAdapterListener listener) {
        this.list = list;
        this.context = context;
        this.emp_id = emp_id;
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
    public void onBindViewHolder(final newcustAdapter.MyViewHolder holder, final int position) {

        DBREF.child("Customer").child(list.get(position))
                .getRef().addListenerForSingleValueEvent(new ValueEventListener() {

                                                             @Override
                                                             public void onDataChange(DataSnapshot dataSnapshot) {

                                                                 if (dataSnapshot.exists()) {
                                                                     Customer cust = dataSnapshot.getValue(Customer.class);
                                                                     String iconText = cust.getName().toUpperCase();

                                                                     holder.icon_text.setText(iconText.charAt(0) + "");
                                                                     holder.imgProfile.setImageResource(R.drawable.bg_circle);
                                                                     holder.imgProfile.setColorFilter(cust.getColor());
                                                                     holder.taskname.setText(iconText);
                                                                     holder.customername.setText("");

                                                                     applyClickEvents(holder, position);
                                                                 }
                                                             }

                                                             @Override
                                                             public void onCancelled(DatabaseError databaseError) {

                                                             }
                                                         }
        );


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface CustomerAdapterListener {
        void onCustomerRowClicked(int position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCustomerRowClicked(position);
            }
        });

    }

}
