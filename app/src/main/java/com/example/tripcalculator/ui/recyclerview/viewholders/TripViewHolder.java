package com.example.tripcalculator.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.google.android.material.card.MaterialCardView;

public class TripViewHolder extends RecyclerView.ViewHolder {

    private TextView trip_info, active_trip, other_trips;
    private MaterialCardView card;
    private Button start_trip_btn, plan_trip_btn;

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        card = itemView.findViewById(R.id.trip_card);
        start_trip_btn = itemView.findViewById(R.id.start_trip_btn);
        plan_trip_btn = itemView.findViewById(R.id.plan_trip_btn);
        trip_info = itemView.findViewById(R.id.trip_info);
        active_trip = itemView.findViewById(R.id.active_trip);
        other_trips = itemView.findViewById(R.id.other_trips);
    }

    public void setName(String text){
        ((TextView) itemView.findViewById(R.id.name)).setText(text);
    }

    public MaterialCardView getCard() {
        return card;
    }

    public void setStartEnabled(boolean value){
        start_trip_btn.setEnabled(value);
    }

    public void setPlanEnabled(boolean value){
        plan_trip_btn.setEnabled(value);
    }

    public void setStartText(String text){
        start_trip_btn.setText(text);
    }

    public void setPlanText(String text){
        plan_trip_btn.setText(text);
    }

    public void setStartListener(View.OnClickListener l){
        start_trip_btn.setOnClickListener(l);
    }

    public void setPlanListener(View.OnClickListener l){
        plan_trip_btn.setOnClickListener(l);
    }

    public void setTripInfo(String text){
        ((TextView) itemView.findViewById(R.id.trip_info)).setText(text);
    }

    public void setTripInfoVisibility(int value){
        trip_info.setVisibility(value);
    }

    public void setActiveTripVisibility(int value){
        active_trip.setVisibility(value);
    }

    public void setOtherTripsVisibility(int value){
        other_trips.setVisibility(value);
    }
}
