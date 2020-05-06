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
import com.example.tripcalculator.databinding.ActivitySearchBinding;
import com.example.tripcalculator.databinding.SearchResultFragmentBinding;
import com.example.tripcalculator.ui.adapters.SearchResultAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment {

    private final static String TAG = "OSM_REQUEST";
    private RequestQueue requestQueue;
    SearchResultFragmentBinding binding;
    SearchResultAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchResultFragmentBinding.inflate(inflater, container, false);
        requestQueue = Volley.newRequestQueue(getContext());

        adapter = new SearchResultAdapter(getContext(), (SearchActivity)getActivity());

        binding.searchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.searchResultList.setAdapter(adapter);
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

    private void createRequest(String place){
        String url = "https://nominatim.openstreetmap.org/search?q=" + place + "&format=json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Location> locations = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject singleAddress = response.getJSONObject(i);
                        Location location = new Location();
                        location.DisplayName = singleAddress.get("display_name").toString();
                        location.Latitude = Double.parseDouble(singleAddress.get("lat").toString());
                        location.Longitude = Double.parseDouble(singleAddress.get("lon").toString());
                        locations.add(location);
                    }
                    adapter.setLocations(locations);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEARCH", error.toString());
            }
        });

        jsonArrayRequest.setTag(TAG);
        requestQueue.add(jsonArrayRequest);
    }

    public Location getLocation(int position){
        return adapter.getLocation(position);
    }
}
