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
import com.example.tripcalculator.ui.recyclerview.viewholders.LocationViewHolder;
import com.example.tripcalculator.utility.DialogHelper;
import com.example.tripcalculator.utility.Utilities;
import com.example.tripcalculator.viewmodel.LocationViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> implements ItemTouchHelperAdapter {

    private List<Location> locations = new ArrayList<>();
    private AppCompatActivity activity;
    private AlertDialog alertDialog;
    private Location lastLocationDismiss;
    private int lastLocationDismissPosition;

    public LocationAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.alertDialog = new AlertDialog.Builder(activity, R.style.Theme_MaterialComponents_DayNight_Dialog)
                .setTitle(R.string.delete_dialog_title)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteItem())
                .setNegativeButton(R.string.No, (dialog, which) -> notifyDataSetChanged())
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
        holder.setReminderListener(v -> DialogHelper.showSetReminderDialog(location, activity));
        holder.setPreviousListener(v -> DialogHelper.showSetPreviousDialog(location, activity));
        if (position > 0) {
            holder.setDividerVisibility(View.GONE);
        } else {
            holder.setDividerVisibility(View.VISIBLE);
        }
        holder.setLocationNameListener(v -> DialogHelper.showLocationName(location, activity));
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
        if(locations.size() > 1) {
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Utilities.swapLocations(activity, locations.get(i), locations.get(i + 1));
                    Collections.swap(locations, i, i + 1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Utilities.swapLocations(activity, locations.get(i), locations.get(i - 1));
                    Collections.swap(locations, i, i - 1);
                }
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
