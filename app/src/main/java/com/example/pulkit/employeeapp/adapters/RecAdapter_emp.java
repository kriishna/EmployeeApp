package com.example.pulkit.employeeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.Employee;

import java.util.List;

public class RecAdapter_emp extends RecyclerView.Adapter<RecAdapter_emp.RecHolder>{


    public List<Employee> list;
    Context context;

    public RecAdapter_emp(List<Employee> list, Context c){
        this.list = list;
        this.context = c;
    }

    @Override
    public RecHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_row,parent,false);
        return new RecHolder(view);
    }

    @Override
    public void onBindViewHolder(RecHolder holder, int position) {

        Employee item = list.get(position);
        holder.name.setText(item.getName());
        holder.desig.setText(item.getDesignation());
        String iconText = item.getName().toUpperCase();
        holder.icon_text.setText(iconText.charAt(0)+"");
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(item.getColor());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setItem(Employee item , int p){
        this.list.set(p,item);
    }

    // holder class
    public class RecHolder extends RecyclerView.ViewHolder{

        TextView name,desig,tasks,icon_text;
        ImageView imgProfile;

        public RecHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            desig = (TextView) itemView.findViewById(R.id.desig);
            icon_text =(TextView)itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView)itemView.findViewById(R.id.icon_profile);

        }
    }


}
