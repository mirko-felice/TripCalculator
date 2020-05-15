package com.example.tripcalculator.ui.adapters;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
            Context wrapper = new ContextThemeWrapper(inflater.getContext(), R.style.AlertDialogDarkTheme);
            PopupMenu popup = new PopupMenu(wrapper, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext(), R.style.AlertDialogDarkTheme);
                    builder.setNeutralButton(R.string.close, (dialog, which) -> {
                        dialog.dismiss();
                    });
                    String message;
                    switch (item.getItemId()){
                        case R.id.showNotes:
                            //TODO Mostra note
                            builder.setTitle("Note");
                            if (location.Note == null){
                                message = "Non sono presenti note per questa località";
                            } else {
                                message = location.Note;
                            }
                            builder.setMessage(message);
                            builder.show();
                            return true;
                        case R.id.showReminders:
                            //TODO Mostra promemoria
                            builder.setTitle("Promemoria");
                            if (location.Reminder == null){
                                message = "Non è presente alcun promemoria per questa località";
                            } else {
                                message = location.Reminder;
                            }
                            builder.setMessage(message);
                            builder.show();
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
