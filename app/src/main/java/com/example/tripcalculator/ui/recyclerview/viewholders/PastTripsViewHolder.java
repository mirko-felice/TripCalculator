package com.example.tripcalculator.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.google.android.material.card.MaterialCardView;

public class PastTripsViewHolder extends RecyclerView.ViewHolder {

    private MaterialCardView trip;
    public PastTripsViewHolder(@NonNull View itemView) {
        super(itemView);
        trip = itemView.findViewById(R.id.past_trips_card);
    }

    public void setData(String title, String startDate, String endDate){
        ((TextView)trip.findViewById(R.id.past_trip_title)).setText(title);
        ((TextView)trip.findViewById(R.id.past_trip_start_date)).setText(startDate);
        ((TextView)trip.findViewById(R.id.past_trip_end_date)).setText(endDate);
    }
}
