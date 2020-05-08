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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.ui.TripItemTouchHelper;
import com.example.tripcalculator.databinding.NextTripsFragmentBinding;
import com.example.tripcalculator.ui.adapters.TripRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.TripViewModel;

import java.util.Date;
import java.util.concurrent.Executors;

public class NextTripsFragment extends Fragment {

    private NextTripsFragmentBinding binding;
    private TripViewModel viewModel;
    private TripRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NextTripsFragmentBinding.inflate(getLayoutInflater(), container, false);
        binding.addTrip.setOnClickListener(v -> {
            Trip trip = new Trip();
            trip.TripId = 0;
            trip.Name = "Nuovo Viaggio";
            trip.IsActive = false;
            trip.IsEnded = false;
            trip.InsertDate = new Date();
            Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(getContext()).tripDao().insertTrip(trip));
            Intent intent = new Intent(getContext(), TripActivity.class);
            startActivity(intent);
        });
        adapter = new TripRecyclerViewAdapter(requireActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TripItemTouchHelper(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT, adapter));
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(TripViewModel.class);
        viewModel.getActiveTrip().observe(getViewLifecycleOwner(), trip -> {
            if(trip != null){
                /*Intent intent = new Intent(TripActivity.class);
                intent.putExtra("TripId", trip.TripId);
                startActivity(intent);*/
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
        if (item.getItemId() == R.id.delete) {
            //requireActivity().startActionMode(new TripActionModeCallback(, getContext()));
            return true;
        }
        return false;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
