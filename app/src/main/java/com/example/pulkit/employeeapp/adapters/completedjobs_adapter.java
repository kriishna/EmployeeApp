package com.example.pulkit.employeeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.CompletedJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SoumyaAgarwal on 8/8/2017.
 */

public class completedjobs_adapter extends RecyclerView.Adapter<completedjobs_adapter.MyViewHolder> {
    List<CompletedJob> list = new ArrayList<>();
    private Context context;

    public completedjobs_adapter(List<CompletedJob> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completedjobs_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        CompletedJob completedJob = list.get(position);
        holder.coordinatorname.setText(completedJob.getAssignedByName());
        holder.coordinatornote.setText(completedJob.getCoordinatorNote());
        holder.yournote.setText(completedJob.getEmpployeeNote());
        holder.dateassigned.setText(completedJob.getDateassigned());
        holder.datecompleted.setText(completedJob.getDatecompleted());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView coordinatorname, dateassigned, datecompleted, coordinatornote, yournote;

        public MyViewHolder(View itemView) {
            super(itemView);

            coordinatorname = (TextView) itemView.findViewById(R.id.name);
            dateassigned = (TextView) itemView.findViewById(R.id.dateassigned);
            datecompleted = (TextView) itemView.findViewById(R.id.datecompleted);
            coordinatornote = (TextView) itemView.findViewById(R.id.coordinatornote);
            yournote = (TextView) itemView.findViewById(R.id.yournote);
        }
    }
}