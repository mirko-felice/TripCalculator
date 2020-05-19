package com.example.tripcalculator.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;

public class TripViewHolder extends RecyclerView.ViewHolder {

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setName(String text){
        ((TextView) itemView.findViewById(R.id.name)).setText(text);
    }
}
