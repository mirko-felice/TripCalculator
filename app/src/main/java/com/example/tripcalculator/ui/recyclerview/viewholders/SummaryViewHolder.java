package com.example.tripcalculator.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;

public class SummaryViewHolder extends RecyclerView.ViewHolder {

    private View modifyReminder;
    private View showPhoto;
    private View addNote;
    private View showReminder;

    public SummaryViewHolder(@NonNull View itemView) {
        super(itemView);
        modifyReminder = itemView.findViewById(R.id.mod_reminder);
        showPhoto = itemView.findViewById(R.id.view_photo);
        addNote = itemView.findViewById(R.id.add_note);
        showReminder = itemView.findViewById(R.id.view_reminder);
    }

    public void setName(String name){
        ((TextView)itemView.findViewById(R.id.summary_name)).setText(name);
    }

    public void adjustVisibility(boolean isPassed) {
        if(isPassed){
            modifyReminder.setVisibility(View.GONE);
            showPhoto.setVisibility(View.VISIBLE);
            addNote.setVisibility(View.VISIBLE);
            showReminder.setVisibility(View.VISIBLE);
        } else {
            modifyReminder.setVisibility(View.VISIBLE);
            showPhoto.setVisibility(View.GONE);
            addNote.setVisibility(View.GONE);
            showReminder.setVisibility(View.GONE);
        }
    }

    public void setModReminderListener(View.OnClickListener listener){
        modifyReminder.setOnClickListener(listener);
    }

    public void setViewPhotoListener(View.OnClickListener listener){
        showPhoto.setOnClickListener(listener);
    }

    public void setAddNoteListener(View.OnClickListener listener){
        addNote.setOnClickListener(listener);
    }

    public void setViewReminderListener(View.OnClickListener listener){
        showReminder.setOnClickListener(listener);
    }
}
