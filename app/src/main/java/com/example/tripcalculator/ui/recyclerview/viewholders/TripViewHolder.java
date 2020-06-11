package com.example.tripcalculator.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.google.android.material.card.MaterialCardView;

public class TripViewHolder extends RecyclerView.ViewHolder {

    private TextView tripInfo;
    private TextView activeTrip;
    private TextView otherTrips;
    private MaterialCardView card;
    private Button startTripBtn;
    private Button planTripBtn;

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        card = itemView.findViewById(R.id.trip_card);
        startTripBtn = itemView.findViewById(R.id.start_trip_btn);
        planTripBtn = itemView.findViewById(R.id.plan_trip_btn);
        tripInfo = itemView.findViewById(R.id.trip_info);
        activeTrip = itemView.findViewById(R.id.active_trip);
        otherTrips = itemView.findViewById(R.id.other_trips);
    }

    public void setName(String text){
        ((TextView) itemView.findViewById(R.id.name)).setText(text);
    }

    public MaterialCardView getCard() {
        return card;
    }

    public void setStartEnabled(boolean value){
        startTripBtn.setEnabled(value);
    }

    public void setPlanEnabled(boolean value){
        planTripBtn.setEnabled(value);
    }

    public void setStartText(String text){
        startTripBtn.setText(text);
    }

    public void setPlanText(String text){
        planTripBtn.setText(text);
    }

    public void setStartListener(View.OnClickListener l){
        startTripBtn.setOnClickListener(l);
    }

    public void setPlanListener(View.OnClickListener l){
        planTripBtn.setOnClickListener(l);
    }

    public void setTripInfo(String text){
        ((TextView) itemView.findViewById(R.id.trip_info)).setText(text);
    }

    public void setTripInfoVisibility(int value){
        tripInfo.setVisibility(value);
    }

    public void setActiveTripVisibility(int value){
        activeTrip.setVisibility(value);
    }

    public void setOtherTripsVisibility(int value){
        otherTrips.setVisibility(value);
    }

    public void setStartVisibility(int value) {
        startTripBtn.setVisibility(value);
    }

    public void setPlanVisibility(int value) {
        planTripBtn.setVisibility(value);
    }
}
