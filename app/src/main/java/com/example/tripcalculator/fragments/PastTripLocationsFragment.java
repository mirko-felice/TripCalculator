package com.example.tripcalculator.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.databinding.ListFragmentBinding;
import com.example.tripcalculator.ui.adapters.PastTripLocationsAdapter;

public class PastTripLocationsFragment extends Fragment {

    private ListFragmentBinding binding;
    private PastTripLocationsAdapter adapter;
    private int tripId;

    public PastTripLocationsFragment(int tripId){
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ListFragmentBinding.inflate(inflater);
        Context context = getContext();
        binding.itemsList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PastTripLocationsAdapter(context);
        AppDatabase.getInstance(context).locationDao().getLocationsFromTrip(tripId).observe(getViewLifecycleOwner(), locations -> {
            adapter.setLocations(locations);
            binding.itemsList.setAdapter(adapter);
        });
        return binding.getRoot();
    }
}
