package com.example.tripcalculator.ui.recyclerview.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.ui.recyclerview.viewholders.PastTripsViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PastTripsAdapter extends RecyclerView.Adapter<PastTripsViewHolder> {

    private List<Trip> trips = new ArrayList<>();
    private LayoutInflater inflater;

    public PastTripsAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public void setTrips(List<Trip> trips){
        this.trips = trips;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PastTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.past_trip_view, parent, false);
        return new PastTripsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastTripsViewHolder holder, int position) {
        Trip trip = trips.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy - HH:mm", PreferenceManager.getDefaultSharedPreferences(inflater.getContext()).getString("language", "0").equals("0") ? Locale.ITALIAN : Locale.ENGLISH);
        assert trip.StartDate != null;
        assert trip.EndDate != null;
        holder.setData(trip.Name, dateFormat.format(trip.StartDate), dateFormat.format(trip.EndDate), v -> {
            Intent intent = new Intent(inflater.getContext(), TripActivity.class);
            intent.putExtra("TripId", trip.TripId);
            inflater.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }
}
