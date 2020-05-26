package com.example.tripcalculator.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class TripViewHolder extends RecyclerView.ViewHolder {

    private TextView trip_info;
    private MaterialCardView card;
    private MaterialButton start_trip_btn, plan_trip_btn;

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        card = itemView.findViewById(R.id.trip_card);
        start_trip_btn = itemView.findViewById(R.id.start_trip_btn);
        plan_trip_btn = itemView.findViewById(R.id.plan_trip_btn);
        trip_info = itemView.findViewById(R.id.trip_info);
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
}
