package com.example.tripcalculator.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {

    private Button location;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);
        location = itemView.findViewById(R.id.search_result);
    }

    public void setLabel(String s, View.OnClickListener listener){
        location.setText(s);
        location.setOnClickListener(listener);
    }
}
