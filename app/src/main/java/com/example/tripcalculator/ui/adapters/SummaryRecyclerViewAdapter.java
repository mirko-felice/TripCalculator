package com.example.tripcalculator.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.ui.SummaryViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SummaryRecyclerViewAdapter extends RecyclerView.Adapter<SummaryViewHolder> {

    private List<Location> locations = new ArrayList<>();
    private Context context;

    public SummaryRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View locationView = LayoutInflater.from(context).inflate(R.layout.location_view, parent, false);
        return new SummaryViewHolder(locationView);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.adjustVisibility(location.IsPassed);
        holder.setName(location.DisplayName);
        holder.itemView.findViewById(R.id.view_reminder).setOnClickListener(v -> {
            //TODO visualizza promemoria
        });
        holder.itemView.findViewById(R.id.mod_reminder).setOnClickListener(v -> {
            //TODO modifica promemoria
        });
        holder.itemView.findViewById(R.id.add_note).setOnClickListener(v -> {
            //TODO aggiungi Nota
        });
        holder.itemView.findViewById(R.id.add_photo).setOnClickListener(v -> {
            //TODO Aggiungi photo
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void updateLocations(List<Location> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

}
