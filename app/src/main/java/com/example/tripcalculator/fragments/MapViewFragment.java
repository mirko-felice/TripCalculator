package com.example.tripcalculator.fragments;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tripcalculator.database.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class MapViewFragment extends Fragment {

    private final static String TAG = "OSM_REQUEST";

    void createRequest(String place){
        String url = "https://nominatim.openstreetmap.org/search?q=" + place + "&format=json";
        LoaderFragment loaderFragment = new LoaderFragment(requireActivity().findViewById(android.R.id.content));
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
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
                afterResponse(locations);
                loaderFragment.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("SEARCH", error.toString()));

        jsonArrayRequest.setTag(TAG);
        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest);
        loaderFragment.show(requireActivity().getSupportFragmentManager(), "loader");
    }

    protected abstract void afterResponse(List<Location> locations);
}
