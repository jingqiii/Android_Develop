package com.example.yangjingqi.assignment3;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yangjingqi on 7/18/17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView timeView;
    public TextView contentView;

    public MyViewHolder(View view) {
        super(view);
        titleView = (TextView) view.findViewById(R.id.title);
        timeView = (TextView) view.findViewById(R.id.time);
        contentView = (TextView) view.findViewById(R.id.content);
    }

}
