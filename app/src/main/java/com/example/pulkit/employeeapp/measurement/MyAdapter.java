package com.example.pulkit.employeeapp.measurement;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.model.measurement;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final Context context;
    private List<measurement> listData;
    private int position;


    public MyAdapter(List<measurement> listData, Context context) {

        this.listData = listData;
        this.context = context;

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final measurement m = listData.get(position);
        holder.width.setText(m.getWidth());
        holder.height.setText(m.getHeight());
        holder.unit.setText(m.getUnit());

        if (!m.getFleximage().equals(""))
            Picasso.with(context).load(m.getFleximage()).into(holder.fleximage);

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView width;
        public TextView height;
        public TextView unit;
        public ImageView fleximage;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);

            width = (TextView) itemView.findViewById(R.id.width);
            height = (TextView) itemView.findViewById(R.id.height);
            unit = (TextView) itemView.findViewById(R.id.unit);
            fleximage = (ImageView) itemView.findViewById(R.id.fleximage);
            view = itemView.findViewById(R.id.container);

        }


    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.view.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

}
