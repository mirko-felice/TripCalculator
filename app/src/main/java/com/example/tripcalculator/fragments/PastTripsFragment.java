package com.example.tripcalculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.databinding.ListFragmentBinding;
import com.example.tripcalculator.ui.PastTripsAdapter;
import com.example.tripcalculator.viewmodel.TripViewModel;

public class PastTripsFragment extends Fragment {

    private PastTripsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListFragmentBinding binding = ListFragmentBinding.inflate(inflater);
        binding.itemsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PastTripsAdapter(requireContext());
        TripViewModel tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        tripViewModel.getEndedTrips().observe(getViewLifecycleOwner(), trips -> adapter.setTrips(trips));
        binding.itemsList.setAdapter(adapter);
        return binding.getRoot();
    }
}
