package com.example.tripcalculator.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.activities.AddTripActivity;
import com.example.tripcalculator.databinding.NextTripsFragmentBinding;
import com.example.tripcalculator.adapters.TripRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.TripViewModel;

public class NextTripsFragment extends Fragment {

    private NextTripsFragmentBinding binding;
    private TripViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NextTripsFragmentBinding.inflate(getLayoutInflater(), container, false);
        binding.addTrip.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTripActivity.class);
            startActivity(intent);
        });
        TripRecyclerViewAdapter adapter = new TripRecyclerViewAdapter(getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(TripViewModel.class);
        viewModel.getTrips().observe(this, trips -> {
            adapter.updateTrips(trips);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
