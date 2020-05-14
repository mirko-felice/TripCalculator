package com.example.tripcalculator.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.ui.PastTripLocationsViewHolder;

import java.util.List;

public class PastTripLocationsAdapter extends RecyclerView.Adapter<PastTripLocationsViewHolder> {

    List<Location> locations;
    LayoutInflater inflater;

    public PastTripLocationsAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public void setLocations(List<Location> locations){
        this.locations = locations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PastTripLocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.past_trip_location_layout, parent, false);
        return new PastTripLocationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastTripLocationsViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.setLocationCardView(location.DisplayName, String.valueOf(location.Latitude), String.valueOf(location.Longitude));
        ((ImageButton)holder.itemView.findViewById(R.id.popup_menu_btn)).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(inflater.getContext(), v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.showNotes:
                            //TODO Mostra note
                            return true;
                        case R.id.showReminders:
                            //TODO Mostra promemoria
                            return true;
                        case R.id.showImages:
                            //TODO MostraImmagini
                            return true;
                    }
                    return false;
                }
            });
            MenuInflater menuInflater = popup.getMenuInflater();
            menuInflater.inflate(R.menu.past_trip_menu, popup.getMenu());
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
