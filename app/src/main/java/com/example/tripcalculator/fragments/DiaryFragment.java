package com.example.tripcalculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.DiaryLayoutBinding;
import com.example.tripcalculator.viewmodel.TripViewModel;

import java.util.Objects;

public class DiaryFragment extends Fragment {

    private int tripId;
    private Trip trip;
    private TripViewModel tripViewModel;
    private DiaryLayoutBinding binding;

    public DiaryFragment(int tripId){
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DiaryLayoutBinding.inflate(inflater, container, false);
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        LiveData<Trip> tripLiveData = tripViewModel.getTripFromId(tripId);
        tripLiveData.observe(getViewLifecycleOwner(), trip -> {
            this.trip = trip;
            if(trip.IsEnded){
                binding.diaryContent.setEnabled(false);
                binding.diaryContent.setHint("");
                binding.diaryContent.setText(trip.Diary == null || trip.Diary.isEmpty() ? getString(R.string.no_diary_label) : trip.Diary);
            } else {
                binding.diaryContent.setHint(getString(R.string.diary_hint));
                binding.diaryContent.setText(trip.Diary);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trip.Diary = Objects.requireNonNull(binding.diaryContent.getText()).toString();
        tripViewModel.updateTrip(trip);
    }
}
