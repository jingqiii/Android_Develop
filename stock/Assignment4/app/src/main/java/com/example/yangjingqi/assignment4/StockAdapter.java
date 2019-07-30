package com.example.yangjingqi.assignment4;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yangjingqi on 7/24/17.
 */

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    public StockAdapter (List<Stock> stList, MainActivity ma) {
        this.stockList = stList;
        mainAct = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);


        if (stock.getChange().startsWith("+") ){
            String change = stock.getChange().trim().replace("+", "\u25B2");
            String perc = stock.getPercentage().trim().replace("+", "");

            holder.symbol.setText(stock.getSymbol());
            holder.company.setText(stock.getCompany());
            holder.change.setText(change);
            holder.price.setText(stock.getPrice());
            holder.percentage.setText(" ( "+perc+"% )");

            holder.symbol.setTextColor(Color.parseColor("#00CF5B"));
            holder.company.setTextColor(Color.parseColor("#00CF5B"));
            holder.change.setTextColor(Color.parseColor("#00CF5B"));
            holder.price.setTextColor(Color.parseColor("#00CF5B"));
            holder.percentage.setTextColor(Color.parseColor("#00CF5B"));

        }else{
            String change = stock.getChange().trim().replace("-", "\u25BC");
            String perc = stock.getPercentage().trim().replace("-", "");

            holder.symbol.setText(stock.getSymbol());
            holder.company.setText(stock.getCompany());
            holder.change.setText(change);
            holder.price.setText(stock.getPrice());
            holder.percentage.setText(" ( "+perc+"% )");

            holder.symbol.setTextColor(Color.parseColor("#E30000"));
            holder.company.setTextColor(Color.parseColor("#E30000"));
            holder.price.setTextColor(Color.parseColor("#E30000"));
            holder.change.setTextColor(Color.parseColor("#E30000"));
            holder.percentage.setTextColor(Color.parseColor("#E30000"));
        }
        Log.d(TAG, "onBindViewHolder: mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}