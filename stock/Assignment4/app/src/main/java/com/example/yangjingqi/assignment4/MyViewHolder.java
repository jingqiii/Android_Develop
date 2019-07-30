package com.example.yangjingqi.assignment4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yangjingqi on 7/24/17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    public TextView company;
    public TextView price;
    public TextView change;
    public TextView percentage;

    public MyViewHolder(View view) {
        super(view);
        symbol = (TextView) view.findViewById(R.id.symbol);
        company = (TextView) view.findViewById(R.id.company);
        price = (TextView) view.findViewById(R.id.price);
        change = (TextView) view.findViewById(R.id.change);
        percentage = (TextView) view.findViewById(R.id.percentage);
    }
}