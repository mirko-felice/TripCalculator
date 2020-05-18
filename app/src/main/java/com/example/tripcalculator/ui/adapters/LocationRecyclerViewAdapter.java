package com.example.tripcalculator.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.utility.DialogHelper;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.ui.LocationViewHolder;
import com.example.tripcalculator.viewmodel.LocationViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationViewHolder> implements ItemTouchHelperAdapter {

    private List<Location> locations = new ArrayList<>();
    private Context context;
    private AlertDialog alertDialog;
    private Location lastLocationDismiss;
    private int lastLocationDismissPosition;

    public LocationRecyclerViewAdapter(Context context) {
        this.context = context;
        this.alertDialog = new AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog)
                .setTitle("Sei sicuro di voler eliminare la destinazione?")
                .setPositiveButton("SI", (dialog, which) -> deleteItem())
                .setNegativeButton("NO", (dialog, which) -> notifyDataSetChanged())
                .setOnDismissListener(dialog -> notifyDataSetChanged())
                .create();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View locationView = LayoutInflater.from(context).inflate(R.layout.location_view, parent, false);
        return new LocationViewHolder(locationView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.setName(location.DisplayName);
        holder.itemView.findViewById(R.id.reminder_btn).setOnClickListener(v -> {
            DialogHelper.showReminderDialog(location, (FragmentActivity) context);
        });
        holder.itemView.findViewById(R.id.previous_btn).setOnClickListener(v -> {
            DialogHelper.showSetPreviousDialog(location, (FragmentActivity) context);
        });
        if (position > 0){
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
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(locations, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(locations, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        lastLocationDismiss = locations.get(position);
        lastLocationDismissPosition = position;
        this.alertDialog.show();
    }
    private void deleteItem(){
        LocationViewModel locationViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LocationViewModel.class);
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
