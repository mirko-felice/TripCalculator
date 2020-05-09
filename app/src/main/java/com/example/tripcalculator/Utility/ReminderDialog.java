package com.example.tripcalculator.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ReminderDialog {

    public static MaterialAlertDialogBuilder createReminderDialog(Location location, Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(R.id.reminder_layout);
        return builder;
    }

    public static MaterialAlertDialogBuilder createSetPreviousDialog(Location locationToExclude, Context context){
        List<Location> items = AppDatabase.getInstance(context).locationDao().getPossiblePreviousLocations(locationToExclude.TripId).getValue();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setSingleChoiceItems((android.widget.ListAdapter) new MyListAdapter(), -1, (dialog, which) -> {});
        return null;
    }

    static class MyListAdapter extends ListAdapter<Location, MyListAdapter.ListViewHolder>{

        private MyListAdapter() {
            super(Location.DIFF_CALLBACK);
        }

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_view, parent, false);
            return new ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        }

        class ListViewHolder extends RecyclerView.ViewHolder{

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
