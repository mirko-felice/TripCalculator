package com.example.tripcalculator.ui.recyclerview.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.ui.ItemTouchHelperAdapter;
import com.example.tripcalculator.ui.recyclerview.viewholders.LocationViewHolder;
import com.example.tripcalculator.utility.DialogHelper;
import com.example.tripcalculator.utility.Utilities;
import com.example.tripcalculator.viewmodel.LocationViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.internal.Util;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> implements ItemTouchHelperAdapter {

    private static final String DIALOG_TITLE = "Sei sicuro di voler eliminare la destinazione?";
    private List<Location> locations = new ArrayList<>();
    private AppCompatActivity activity;
    private AlertDialog alertDialog;
    private Location lastLocationDismiss;
    private int lastLocationDismissPosition;

    public LocationAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.alertDialog = new AlertDialog.Builder(activity.getApplicationContext(), R.style.Theme_MaterialComponents_Light_Dialog)
                .setTitle(DIALOG_TITLE)
                .setPositiveButton("SI", (dialog, which) -> deleteItem())
                .setNegativeButton("NO", (dialog, which) -> notifyDataSetChanged())
                .setOnDismissListener(dialog -> notifyDataSetChanged())
                .create();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View locationView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.location_view, parent, false);
        return new LocationViewHolder(locationView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.setName(location.DisplayName);
        holder.itemView.findViewById(R.id.reminder_btn).setOnClickListener(v -> DialogHelper.showReminderDialog(location, activity));
        holder.itemView.findViewById(R.id.previous_btn).setOnClickListener(v -> DialogHelper.showSetPreviousDialog(location, activity));
        if (position > 0) {
            holder.itemView.findViewById(R.id.divider).setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void updateLocations(List<Location> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        lastLocationDismiss = locations.get(position);
        lastLocationDismissPosition = position;
        this.alertDialog.show();
    }

    @Override
    public void moveLocation(int from, int to) {
        if (from < to){
            for (int i = from; i < to; i++){
                Utilities.swapLocations(activity, locations.get(i), locations.get(i + 1));
                Collections.swap(locations, i, i+ 1);
            }
        } else {
            for (int i = from; i > to; i--){
                Utilities.swapLocations(activity, locations.get(i), locations.get(i - 1));
                Collections.swap(locations, i , i- 1);
            }
        }
    }


    private void deleteItem(){
        LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
        locationViewModel.deleteLocation(lastLocationDismiss);
        for(Location location: locations){
            if (location.Order > lastLocationDismissPosition){
                location.Order--;
                locationViewModel.updateLocation(location);
            }
            if (location.PreviousId != null && location.PreviousId == lastLocationDismiss.Id){
                location.PreviousId = null;
            }
        }
        notifyItemRemoved(lastLocationDismissPosition);
    }
}
