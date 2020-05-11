package com.example.tripcalculator.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;

public class SummaryViewHolder extends RecyclerView.ViewHolder {

    public SummaryViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setName(String name){
        ((TextView)itemView.findViewById(R.id.summary_name)).setText(name);
    }

    public void adjustVisibility(boolean isPassed) {
        if(isPassed){
            itemView.findViewById(R.id.mod_reminder).setVisibility(View.GONE);
            itemView.findViewById(R.id.add_photo).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.add_note).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.view_reminder).setVisibility(View.VISIBLE);
        } else{
            itemView.findViewById(R.id.mod_reminder).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.add_photo).setVisibility(View.GONE);
            itemView.findViewById(R.id.add_note).setVisibility(View.GONE);
            itemView.findViewById(R.id.view_reminder).setVisibility(View.GONE);
        }
    }
}
