package com.example.tripcalculator.ui.recyclerview.adapters;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void moveLocation(int from, int to);
}
