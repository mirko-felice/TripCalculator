package com.example.tripcalculator.ui.recyclerview.viewholders;

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

    public void setReminderListener(View.OnClickListener listener){
        itemView.findViewById(R.id.reminder_btn).setOnClickListener(listener);
    }

    public void setPreviousListener(View.OnClickListener listener){
        itemView.findViewById(R.id.previous_btn).setOnClickListener(listener);
    }

    public void setLocationNameListener(View.OnClickListener listener){
        itemView.findViewById(R.id.edit_location).setOnClickListener(listener);
    }

    public void setDividerVisibility(int visibility){
        itemView.findViewById(R.id.divider).setVisibility(visibility);
    }

    public void setDividerColor(int color){
        itemView.findViewById(R.id.divider).setBackgroundColor(color);
    }
}
