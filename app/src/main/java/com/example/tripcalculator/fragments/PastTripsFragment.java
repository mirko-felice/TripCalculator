package com.example.tripcalculator.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.databinding.ListFragmentBinding;
import com.example.tripcalculator.ui.recyclerview.adapters.PastTripsAdapter;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PastTripsFragment extends Fragment {

    private TripViewModel tripViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListFragmentBinding binding = ListFragmentBinding.inflate(inflater);
        binding.itemsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        PastTripsAdapter adapter = new PastTripsAdapter(requireContext());
        tripViewModel.getEndedTrips().observe(getViewLifecycleOwner(), adapter::setTrips);
        binding.itemsList.setAdapter(adapter);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete){
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.alert)
                .setMessage(R.string.delete_past_trips)
                .setPositiveButton(R.string.yes, (dialog, which) -> tripViewModel.deleteAllEndedTrips())
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
