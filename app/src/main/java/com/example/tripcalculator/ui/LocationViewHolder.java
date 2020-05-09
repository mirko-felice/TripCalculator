package com.example.tripcalculator.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    public LocationViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setName(String name) {
        ((TextView)itemView.findViewById(R.id.location_name)).setText(name);
    }
}
