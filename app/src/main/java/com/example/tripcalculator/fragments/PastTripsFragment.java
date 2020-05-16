package com.example.tripcalculator.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.databinding.ListFragmentBinding;
import com.example.tripcalculator.ui.adapters.PastTripsAdapter;
import com.example.tripcalculator.viewmodel.TripViewModel;

public class PastTripsFragment extends Fragment {

    private ListFragmentBinding binding;
    private PastTripsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ListFragmentBinding.inflate(inflater);
        Context context = getContext();
        binding.itemsList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PastTripsAdapter(context);
        TripViewModel tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        tripViewModel.getEndedTrips().observe(getViewLifecycleOwner(), trips -> {
            adapter.setTrips(trips);
        });
        binding.itemsList.setAdapter(adapter);
        return binding.getRoot();
    }
}
