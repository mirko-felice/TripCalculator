package com.example.tripcalculator.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.google.android.material.card.MaterialCardView;

public class PastTripLocationsViewHolder extends RecyclerView.ViewHolder {

    MaterialCardView locationCardView;

    public PastTripLocationsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.locationCardView = itemView.findViewById(R.id.ended_trip_location_cardview);
    }

    public void setLocationCardView(String title, String latitude, String longitude) {
        ((TextView)this.locationCardView.findViewById(R.id.ended_trip_location_title)).setText(title);
        ((TextView)this.locationCardView.findViewById(R.id.ended_trip_location_latitude)).setText(latitude);
        ((TextView)this.locationCardView.findViewById(R.id.ended_trip_location_longitude)).setText(longitude);
    }
}
