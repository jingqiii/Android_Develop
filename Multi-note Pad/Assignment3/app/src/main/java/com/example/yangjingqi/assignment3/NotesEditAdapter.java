package com.example.yangjingqi.assignment3;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yangjingqi on 7/18/17.
 */

public class NotesEditAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private static final String TAG = "NotesEditAdapter";
    private List<NotesEdit> notesList;
    private MainActivity mainAct;

    public NotesEditAdapter(List<NotesEdit> nList, MainActivity ma) {
        this.notesList = nList;
        mainAct = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotesEdit nt = notesList.get(position);

        holder.titleView.setText(nt.getNotes_title());
        holder.timeView.setText(nt.getTime());
        holder.contentView.setText(nt.getContent());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
