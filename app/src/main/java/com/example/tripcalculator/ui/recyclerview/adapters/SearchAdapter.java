package com.example.tripcalculator.ui.recyclerview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.SearchActivity;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.ui.recyclerview.viewholders.SearchViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    private List<Location> result = new ArrayList<>();
    private LayoutInflater inflater;
    private SearchActivity activity;

    public SearchAdapter(Context context, SearchActivity activity){
        this.activity = activity;
        this.inflater = LayoutInflater.from(context);
    }

    public void setLocations(List<Location> locations){
        this.result = locations;
        activity.setSearchResult(locations);
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
        holder.setLabel(location.DisplayName, v -> activity.focusOn(result.get(position)));
    }

    @Override
    public int getItemCount() {
        return result.size();
    }
}
