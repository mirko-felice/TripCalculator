package com.example.tripcalculator.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.SearchActivity;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.ListFragmentBinding;
import com.example.tripcalculator.ui.recyclerview.adapters.SearchAdapter;

import java.util.List;

public class SearchResultFragment extends MapViewFragment {

    private SearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListFragmentBinding binding = ListFragmentBinding.inflate(inflater, container, false);

        adapter = new SearchAdapter(requireContext(), (SearchActivity) requireActivity());

        binding.itemsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemsList.setBackgroundColor(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? Color.BLACK : Color.WHITE);
        binding.itemsList.setAdapter(adapter);
        return binding.getRoot();
    }

    public void executeQueue(String query){
        adapter.clearSearchResult();
        createRequest(query);
    }

    @Override
    protected void afterResponse(List<Location> locations) {
        adapter.setLocations(locations);
    }
}
