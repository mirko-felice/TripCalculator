package com.example.tripcalculator.ui.recyclerview.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TripItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final ItemTouchHelperAdapter adapter;
    private int dragFrom = -1;

    public TripItemTouchHelper(int dragDirs, int swipeDirs, ItemTouchHelperAdapter adapter) {
        super(dragDirs, swipeDirs);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (dragFrom == -1)
            dragFrom = viewHolder.getAdapterPosition();
        return adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        adapter.moveLocation(dragFrom, viewHolder.getAdapterPosition());
        dragFrom = -1;
    }
}
