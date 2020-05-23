package com.example.tripcalculator.ui.recyclerview.adapters;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void moveLocation(int from, int to);
}
