package com.example.pulkit.employeeapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.pulkit.employeeapp.EmployeeApp.DBREF;
import static com.example.pulkit.employeeapp.EmployeeApp.simpleDateFormat;

public class taskAdapter extends RecyclerView.Adapter<taskAdapter.MyViewHolder> {
    ArrayList<Task> list = new ArrayList<>();
    private Context context;
    String desig = TaskHome.desig;
    private TaskAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;



    public taskAdapter(ArrayList<Task> list, Context context, TaskAdapterListener listener) {
        this.list = list;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        this.context = context;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView taskname, customername, timestamp, icon_text;
        ImageView imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout ll_overall,iconContainer,iconBack,iconFront;

        public MyViewHolder(View itemView) {
            super(itemView);
            taskname = (TextView) itemView.findViewById(R.id.tv_taskname);
            customername = (TextView) itemView.findViewById(R.id.tv_customerName);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            icon_text = (TextView) itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
            ll_overall = (RelativeLayout)itemView.findViewById(R.id.base_container);
            iconBack = (RelativeLayout) itemView.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) itemView.findViewById(R.id.icon_front);
            iconContainer = (RelativeLayout) itemView.findViewById(R.id.icon_container);



        }
        @Override
        public boolean onLongClick(View v) {
            listener.onRowLongClicked(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
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


        if(desig.toLowerCase().equals("quotation")){
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
                    applyBackgroundColor(holder,temp);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        applyClickEvents(holder, position);
        applyIconAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface TaskAdapterListener {
        void onTaskRowClicked(int position);
        void onRowLongClicked(int position);
        void onIconClicked(int position);

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTaskRowClicked(position);
            }
        });
        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });


    }
    private void applyBackgroundColor(taskAdapter.MyViewHolder holder, String deadline) {

        try {
            String curdate = simpleDateFormat.format(Calendar.getInstance().getTime());
            Date curDate = simpleDateFormat.parse(curdate);
            if(deadline!=null) {
                Date aDate = simpleDateFormat.parse(deadline);

                if (curDate.compareTo(aDate) > -1) {
                    holder.ll_overall.setBackgroundResource(R.color.colorAccent);
                } else {
                    holder.ll_overall.setBackgroundColor(Color.WHITE);
                }
            }

            else
                holder.ll_overall.setBackgroundColor(Color.WHITE);


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void applyIconAnimation(taskAdapter.MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(context, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(context, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
    public int getSelectedItemCount() {
        return selectedItems.size();
    }


}
