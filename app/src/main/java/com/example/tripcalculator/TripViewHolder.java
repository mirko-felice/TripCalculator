package com.example.tripcalculator;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;

public class TripViewHolder extends RecyclerView.ViewHolder {

    //tutte le view di un singolo trip
    private TextView name;

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
    }

    public void setText(String s){
        name.setText(s);
    }
}
