package com.example.tripcalculator.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.SummaryFragmentBinding;
import com.example.tripcalculator.ui.recyclerview.adapters.SummaryAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.TripViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SummaryFragment extends Fragment {

    private static final int REQUEST_PHOTO_CODE = 1;
    private final int tripId;
    private LocationViewModel locationViewModel;
    private SummaryFragmentBinding binding;
    private Uri photoURI;
    private Location lastLocation;

    public SummaryFragment(int tripId) {
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SummaryFragmentBinding.inflate(inflater, container, false);
        binding.summary.setLayoutManager(new LinearLayoutManager(getContext()));
        SummaryAdapter adapter = new SummaryAdapter(this);
        binding.summary.setAdapter(adapter);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        TripViewModel tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        LiveData<Trip> tripLiveData = tripViewModel.getTripFromId(tripId);
        tripLiveData.observe(getViewLifecycleOwner(), trip -> {
            locationViewModel.getLocationsFromTrip(tripId).observe(getViewLifecycleOwner(), locations -> {
                if(locations.get(locations.size() - 1).IsPassed)
                    binding.endTripBtn.setVisibility(View.VISIBLE);
                adapter.updateLocations(locations);
            });
            if(!trip.IsEnded){
                binding.endTripBtn.setOnClickListener(v -> {
                    trip.IsEnded = true;
                    trip.IsActive = false;
                    trip.EndDate = new Date();
                    tripViewModel.updateTrip(trip);
                    requireActivity().finish();
                    tripLiveData.removeObservers(getViewLifecycleOwner());
                });
            } else
                binding.endTripBtn.setVisibility(View.GONE);
        });
        return binding.getRoot();
    }

    public void takePhoto(Location location) {
        lastLocation = location;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(requireActivity().getPackageManager()) != null){
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.example.tripcalculator.fileprovider",
                        photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, REQUEST_PHOTO_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PHOTO_CODE && resultCode == RESULT_OK) {
            lastLocation.ImgNames.add(photoURI.toString());
            locationViewModel.updateLocation(lastLocation);
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            return null;
        }
        return image;
    }
}
