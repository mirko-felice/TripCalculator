package com.example.tripcalculator.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.TripViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TripRecyclerViewAdapter extends RecyclerView.Adapter<TripViewHolder> {

    private Context context;
    private List<Trip> trips = new ArrayList<>();

    public TripRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tripView = LayoutInflater.from(context).inflate(R.layout.trip_view, parent, false);
        return new TripViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        ((TextView) holder.itemView.findViewById(R.id.name)).setText(trip.Name);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void updateTrips(List<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }
}
