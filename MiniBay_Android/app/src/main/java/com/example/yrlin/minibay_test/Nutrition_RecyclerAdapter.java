package com.example.yrlin.minibay_test;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yrlin on 2017/4/9.
 */

public class Nutrition_RecyclerAdapter extends RecyclerView.Adapter<Nutrition_RecyclerAdapter.ViewHolder> {
    private ArrayList<String> titles;
    private ArrayList<String> rate;
    private ArrayList<String> quota;
    int[] imageid = {R.drawable.apple, R.drawable.watermelon};


    public Nutrition_RecyclerAdapter(ArrayList<String> itemtitles, ArrayList<String> itemrates, ArrayList<String> itemquota) {
        super();
        this.titles = itemtitles;
        this.rate = itemrates;
        this.quota = itemquota;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public TextView itemRate;
        public TextView itemquota;
        public LinearLayout layouttemp;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView)itemView.findViewById(R.id.info_text1);
            itemRate = (TextView)itemView.findViewById(R.id.textRate);
            itemquota = (TextView)itemView.findViewById(R.id.quota_week);
            layouttemp = (LinearLayout)itemView.findViewById(R.id.linear_image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_cardview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTitle.setText(titles.get(position));
        holder.itemRate.setText(rate.get(position) + "%");
        holder.itemquota.setText(quota.get(position) + " miligrams for 7 days");

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
