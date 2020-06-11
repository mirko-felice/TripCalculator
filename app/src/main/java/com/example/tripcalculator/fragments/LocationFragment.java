package com.example.tripcalculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;

public class LocationFragment extends Fragment {

    private Location location;
    private final MapFragment mapFragment;

    public LocationFragment(Location location, MapFragment mapFragment) {
        this.location = location;
        this.mapFragment = mapFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_fragment, container, false);
        ((TextView)view.findViewById(R.id.location_name)).setText(location.DisplayName);
        view.findViewById(R.id.location_name).setOnClickListener(v -> mapFragment.focusOn(location));
        return view;
    }
}
