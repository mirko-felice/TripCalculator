package com.example.tripcalculator.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.ui.TripActionModeCallback;
import com.example.tripcalculator.ui.TripViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class TripRecyclerViewAdapter extends RecyclerView.Adapter<TripViewHolder> implements ItemTouchHelperAdapter {

    private FragmentActivity activity;
    private List<Trip> trips = new ArrayList<>();
    private AlertDialog alertDialog;
    private Trip lastTripDismiss;
    private int lastTripDismissPosition;

    public TripRecyclerViewAdapter(FragmentActivity activity) {
        this.activity = activity;
        this.alertDialog = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_DayNight_Dialog)
                .setTitle("Sei sicuro di voler eliminare il viaggio?")
                .setPositiveButton("SI", (dialog, which) -> deleteItem())
                .setNegativeButton("NO", (dialog, which) -> notifyDataSetChanged())
                .create();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tripView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.trip_view, parent, false);
        return new TripViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.setName(trip.Name);
        holder.itemView.setOnLongClickListener(v -> {
            activity.startActionMode(new TripActionModeCallback(trip, activity.getApplicationContext()));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void updateTrips(List<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(trips, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(trips, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        lastTripDismiss = trips.get(position);
        lastTripDismissPosition = position;
        this.alertDialog.show();
    }

    private void deleteItem(){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(activity).tripDao().deleteTrip(lastTripDismiss));
        notifyItemRemoved(lastTripDismissPosition);
    }

}
