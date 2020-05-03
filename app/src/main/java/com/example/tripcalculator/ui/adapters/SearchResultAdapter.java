package com.example.tripcalculator.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.ui.SearchViewHolder;
import com.example.tripcalculator.structures.Location;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    private List<Location> result = new ArrayList<>();
    LayoutInflater inflater;

    public SearchResultAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
    }


    public void addLocation(double latitude, double longitude, String displayName){
        this.result.add(new Location(latitude, longitude, displayName));
        notifyDataSetChanged();
    }

    public void clearSearchResult(){
        this.result.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_listview, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Location location = result.get(position);
        ((TextView) holder.itemView.findViewById(R.id.result_location)).setText(location.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return result.size();
    }
}
