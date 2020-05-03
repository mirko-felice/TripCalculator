package com.example.tripcalculator.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;

public class TripViewHolder extends RecyclerView.ViewHolder {

    private TextView name;

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
    }

    public void setName(String text){
        name.setText(text);
    }
}
