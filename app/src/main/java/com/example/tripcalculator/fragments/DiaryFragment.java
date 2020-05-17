package com.example.tripcalculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.databinding.DiaryLayoutBinding;
import com.example.tripcalculator.viewmodel.TripViewModel;

public class DiaryFragment extends Fragment {

    int tripId;

    public DiaryFragment(int tripId){
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DiaryLayoutBinding binding = DiaryLayoutBinding.inflate(inflater, container, false);
        TripViewModel tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        tripViewModel.getTripFromId(tripId).observe(getViewLifecycleOwner(), trip -> {
            //binding.diaryHeader.setHint(trip.Name);
            binding.diaryContent.setText(trip.Diary);
        });
        return binding.getRoot();
    }
}
