package com.example.tripcalculator.ui;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;

import java.util.concurrent.Executors;

public class TripActionModeCallback implements ActionMode.Callback {

    private Trip trip;
    private Context context;

    public TripActionModeCallback(Trip trip, Context context) {
        this.trip = trip;
        this.context = context;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            /*for (Integer intItem : selectedItems) {
                items.remove(intItem);
            }*/
            deleteCurrentItem();
            mode.finish();
            return true;
        }
        return false;
    }

    private void deleteCurrentItem() {
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).tripDao().deleteTrip(trip));
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }
}
