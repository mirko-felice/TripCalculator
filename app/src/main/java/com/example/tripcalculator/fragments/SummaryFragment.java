package com.example.tripcalculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.databinding.SummaryFragmentBinding;
import com.example.tripcalculator.ui.adapters.SummaryRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;

public class SummaryFragment extends Fragment {

    private LocationViewModel locationViewModel;
    private SummaryFragmentBinding binding;

    public SummaryFragment(LocationViewModel locationViewModel) {
        this.locationViewModel = locationViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SummaryFragmentBinding.inflate(inflater, container, false);

        binding.summary.setLayoutManager(new LinearLayoutManager(getContext()));
        SummaryRecyclerViewAdapter adapter = new SummaryRecyclerViewAdapter(getContext());
        binding.summary.setAdapter(adapter);
        locationViewModel.getLocations().observe(getViewLifecycleOwner(), adapter::updateLocations);
        return binding.getRoot();
    }
}
