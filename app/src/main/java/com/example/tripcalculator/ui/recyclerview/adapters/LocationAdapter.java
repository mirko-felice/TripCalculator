package com.example.tripcalculator.ui.recyclerview.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.ui.recyclerview.viewholders.LocationViewHolder;
import com.example.tripcalculator.utility.DialogHelper;
import com.example.tripcalculator.utility.Utilities;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> implements ItemTouchHelperAdapter {

    private List<Location> locations = new ArrayList<>();
    private AppCompatActivity activity;
    private MaterialAlertDialogBuilder alertDialogBuilder;
    private Location lastLocationDismiss;
    private int lastLocationDismissPosition;

    public LocationAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.alertDialogBuilder = new MaterialAlertDialogBuilder(activity, R.style.Theme_MaterialComponents_DayNight_Dialog)
                .setTitle(R.string.delete_dialog_title)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteItem())
                .setNegativeButton(R.string.no, (dialog, which) -> notifyDataSetChanged())
                .setOnDismissListener(dialog -> notifyDataSetChanged());
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View locationView = LayoutInflater.from(activity).inflate(R.layout.location_view, parent, false);
        return new LocationViewHolder(locationView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.setName(location.DisplayName);
        holder.setReminderListener(v -> DialogHelper.showSetReminderDialog(location, activity, position));
        holder.setPreviousListener(v -> DialogHelper.showSetPreviousDialog(location, activity, this));
        holder.setDividerColor(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? Color.WHITE : Color.BLACK);
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
    public void onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        lastLocationDismiss = locations.get(position);
        lastLocationDismissPosition = position;
        this.alertDialogBuilder.show();
    }

    @Override
    public void moveLocation(int from, int to) {
        if(locations.size() > 1 && from > -1) {
            boolean canMove = true;
            Location movedLocation = locations.get(from);
            if (from < to) {
                for (int i = from + 1; i <= to; i++) {
                    if (locations.get(i).PreviousId != null && locations.get(i).PreviousId == movedLocation.Id) {
                        canMove = false;
                        break;
                    }
                }
                for (int i = from; canMove && i < to; i++) {
                    Utilities.swapLocations(activity, locations.get(i), locations.get(i + 1));
                    Collections.swap(locations, i, i + 1);
                }
            } else {
                if (movedLocation.PreviousId != null) {
                    for (int i = from - 1; i >= to; i--) {
                        if (locations.get(i).Id == movedLocation.PreviousId) {
                            canMove = false;
                            break;
                        }
                    }
                }
                for (int i = from; canMove && i > to; i--) {
                    Utilities.swapLocations(activity, locations.get(i), locations.get(i - 1));
                    Collections.swap(locations, i, i - 1);
                }
            }
            if (!canMove)
                notifyDataSetChanged();
        }
    }


    private void deleteItem(){
        LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
        locationViewModel.deleteLocation(lastLocationDismiss);
        locations.remove(lastLocationDismiss);
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
