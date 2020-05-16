package com.example.tripcalculator.fragments;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.activities.ModifyTripActivity;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.NextTripsFragmentBinding;
import com.example.tripcalculator.ui.adapters.TripRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.TripViewModel;

import java.util.Date;

public class NextTripsFragment extends Fragment {

    private NextTripsFragmentBinding binding;
    private TripRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NextTripsFragmentBinding.inflate(inflater, container, false);

        TripViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(TripViewModel.class);
        binding.addTrip.setOnClickListener(v -> {
            Trip trip = new Trip();
            trip.TripId = 0;
            trip.Name = "Nuovo Viaggio";
            trip.IsActive = false;
            trip.IsEnded = false;
            trip.InsertDate = new Date();
            viewModel.insertTrip(trip);
            Intent intent = new Intent(getContext(), ModifyTripActivity.class);
            startActivity(intent);
        });
        adapter = new TripRecyclerViewAdapter(requireActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        LiveData<Trip> activeTrip = viewModel.getActiveTrip();
        activeTrip.observe(getViewLifecycleOwner(), trip -> {
            if(trip != null){
                Intent intent = new Intent(getContext(), TripActivity.class);
                intent.putExtra("TripId", trip.TripId);
                startActivity(intent);
                activeTrip.removeObservers(getViewLifecycleOwner());
            }
        });
        viewModel.getTrips().observe(getViewLifecycleOwner(), trips -> {
            adapter.updateTrips(trips);
        });

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_menu, menu);
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public TripRecyclerViewAdapter getAdapter() {
        return adapter;
    }
}
