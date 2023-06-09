package com.example.tripcalculator.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.ModifyTripActivity;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.NextTripsFragmentBinding;
import com.example.tripcalculator.ui.recyclerview.adapters.TripAdapter;
import com.example.tripcalculator.viewmodel.TripViewModel;

import java.util.Date;

public class NextTripsFragment extends Fragment {

    private static final String TRIP_ID = "TripId";
    private TripAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NextTripsFragmentBinding binding = NextTripsFragmentBinding.inflate(inflater, container, false);

        TripViewModel viewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        binding.addTrip.setOnClickListener(v -> {
            Trip trip = new Trip();
            trip.TripId = 0;
            trip.Name = getString(R.string.new_trip);
            trip.IsActive = false;
            trip.IsEnded = false;
            trip.InsertDate = new Date();
            viewModel.insertTrip(trip);
            Intent intent = new Intent(getContext(), ModifyTripActivity.class);
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setVisibility(View.GONE);

        LiveData<Trip> activeTrip = viewModel.getActiveTrip();
        activeTrip.observe(getViewLifecycleOwner(), trip -> {
            activeTrip.removeObservers(getViewLifecycleOwner());
            if(trip != null){
                Intent intent = new Intent(getContext(), TripActivity.class);
                intent.putExtra(TRIP_ID, trip.TripId);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (sharedPreferences.contains(TRIP_ID)) {
            int tripId = sharedPreferences.getInt(TRIP_ID, -1);
            sharedPreferences.edit().remove(TRIP_ID).apply();
            TripViewModel tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
            LiveData<Trip> liveData = tripViewModel.getTripFromId(tripId);
            liveData.observe(getViewLifecycleOwner(), trip -> {
                trip.IsPlanned = false;
                tripViewModel.updateTrip(trip);
                liveData.removeObservers(getViewLifecycleOwner());
            });
        }

        viewModel.getAllTrips().observe(getViewLifecycleOwner(), trips -> {
            boolean isTripActive = false;
            boolean isTripPlanned = false;
            for(Trip trip: trips){
                if (trip.IsActive && !isTripActive) {
                    isTripActive = true;
                }
                if(trip.IsPlanned && !isTripPlanned){
                    isTripPlanned = true;
                }
            }

            adapter = new TripAdapter(requireActivity(), isTripActive, isTripPlanned);
            binding.recyclerView.setAdapter(adapter);
            binding.recyclerView.setVisibility(View.VISIBLE);
            adapter.updateTrips(trips);
        });

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home) {
            adapter.deselectAllCards();
            return true;
        }
        return false;
    }
}
