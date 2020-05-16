package com.example.tripcalculator.ui.adapters;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.utility.DialogHelper;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.fragments.SummaryFragment;
import com.example.tripcalculator.ui.SummaryViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SummaryRecyclerViewAdapter extends RecyclerView.Adapter<SummaryViewHolder> {

    private List<Location> locations = new ArrayList<>();
    private SummaryFragment fragment;

    public SummaryRecyclerViewAdapter(SummaryFragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View locationView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.summary_view, parent, false);
        return new SummaryViewHolder(locationView);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.adjustVisibility(location.IsPassed);
        holder.setName(location.DisplayName);
        holder.itemView.setOnClickListener(v -> {
            DialogHelper.showImages(location, fragment.requireContext());
        });
        holder.itemView.findViewById(R.id.view_reminder).setOnClickListener(v -> {
            //TODO visualizza promemoria
        });
        holder.itemView.findViewById(R.id.mod_reminder).setOnClickListener(v -> {
            //TODO modifica promemoria
            DialogHelper.showReminderDialog(location, fragment.requireActivity());
        });
        holder.itemView.findViewById(R.id.add_note).setOnClickListener(v -> {
            //TODO aggiungi Nota
            DialogHelper.showAddNote(location, fragment.requireActivity());
        });
        if(fragment.requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            holder.itemView.findViewById(R.id.add_photo).setOnClickListener(v -> {
                //TODO Aggiungi photo
                fragment.takePhoto(location);
            });
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

}
