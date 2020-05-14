package com.example.tripcalculator.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.PastTripActivity;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.fragments.PastTripLocationsFragment;
import com.example.tripcalculator.ui.PastTripsViewHolder;

import java.util.ArrayList;
import java.util.List;

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
        holder.setData(trip.Name, trip.StartDate.toString(), trip.EndDate.toString());
        holder.itemView.findViewById(R.id.past_trips_card).setOnClickListener(v -> {
            Intent intent = new Intent(inflater.getContext(), PastTripActivity.class);
            intent.putExtra("TripId", trip.TripId);
            inflater.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }
}
