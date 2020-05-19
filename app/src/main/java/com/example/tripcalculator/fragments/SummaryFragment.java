package com.example.tripcalculator.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SummaryFragment extends Fragment {

    private static final int REQUEST_PHOTO_CODE = 1;
    private final int tripId;
    private LocationViewModel locationViewModel;
    private SummaryFragmentBinding binding;
    private String currentPhotoPath;
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
        locationViewModel.getLocationsFromTrip(tripId).observe(getViewLifecycleOwner(), adapter::updateLocations);
        TripViewModel tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        binding.endTripBtn.setOnClickListener(v -> {
            LiveData<Trip> tripLiveData = tripViewModel.getTripFromId(tripId);
            tripLiveData.observe(getViewLifecycleOwner(), trip -> {
                trip.IsEnded = true;
                trip.IsActive = false;
                trip.EndDate = new Date();
                tripViewModel.updateTrip(trip);
                requireActivity().finish();
                tripLiveData.removeObservers(getViewLifecycleOwner());
            });
        });
        return binding.getRoot();
    }

    public void takePhoto(Location location) {
        lastLocation = location;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(requireActivity().getPackageManager()) != null){
            File photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.example.tripcalculator.fileprovider",
                        photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, REQUEST_PHOTO_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PHOTO_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap thumbnail = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    thumbnail = requireActivity().getContentResolver().loadThumbnail(photoURI, new Size(200,200), null);
                }
                lastLocation.ImgNames.add(photoURI.toString());
                locationViewModel.updateLocation(lastLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            return null;
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
