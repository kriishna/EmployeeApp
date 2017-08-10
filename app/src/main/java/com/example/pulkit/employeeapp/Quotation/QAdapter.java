package com.example.pulkit.employeeapp.Quotation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.QuotationBatch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;

public class QAdapter extends RecyclerView.Adapter<QAdapter.MyViewHolder> {
    List<QuotationBatch> list = new ArrayList<>();
    private Context context;
    private QAdapterListener listener;


    public QAdapter(List<QuotationBatch> list, Context context, QAdapterListener listener) {
        this.list = list;
        this.listener = listener;
        this.context=context;
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
    public void onBindViewHolder(final QAdapter.MyViewHolder holder, int position) {
        QuotationBatch batch = list.get(position);
        String iconText = batch.getCustName();

        holder.icon_text.setText((iconText.charAt(0) + "").toUpperCase());
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(batch.getColor());
        holder.timestamp.setText(batch.getStartDate());
        holder.taskname.setText(batch.getId().substring(5));
        holder.customername.setText(iconText);

        /*
        DatabaseReference dbCustomerName = DBREF.child("Customer").child(batch.getId()).getRef();
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

        */
        applyClickEvents(holder, position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface QAdapterListener {
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
