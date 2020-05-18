package com.example.tripcalculator.fragments;

import android.app.SearchManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tripcalculator.activities.SearchActivity;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.ListFragmentBinding;
import com.example.tripcalculator.ui.adapters.SearchResultAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends MapViewFragment {

    private final static String TAG = "OSM_REQUEST";
    private RequestQueue requestQueue;
    ListFragmentBinding binding;
    SearchResultAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ListFragmentBinding.inflate(inflater, container, false);

        adapter = new SearchResultAdapter(getContext(), (SearchActivity)getActivity());

        binding.itemsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.itemsList.setAdapter(adapter);
        return binding.getRoot();
    }

    public void executeQueue(String query){
        adapter.clearSearchResult();
        createRequest(query);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    protected void afterResponse(List<Location> locations) {
        adapter.setLocations(locations);
    }

    public Location getLocation(int position){
        return adapter.getLocation(position);
    }
}
