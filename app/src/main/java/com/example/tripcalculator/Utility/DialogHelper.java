package com.example.tripcalculator.Utility;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class DialogHelper {

    public static void showReminderDialog(Location location, Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_view, null, false);
        builder.setTitle("Promemoria")
                .setView(view)
                .setPositiveButton("Aggiungi", (dialog, which) -> setReminder(view, location))
                .setNegativeButton("Cancella", (dialog, which) -> Toast.makeText(context, "Non aggiunto", Toast.LENGTH_SHORT).show())
                .setOnDismissListener(dialog -> Toast.makeText(context, "Non aggiunto", Toast.LENGTH_SHORT).show())
                .show();
    }

    public static void showSetPreviousDialog(Location locationToExclude, Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(R.layout.previous_layout);
        LiveData<List<Location>> liveData = AppDatabase.getInstance(context).locationDao().getPossiblePreviousLocations(locationToExclude.TripId, locationToExclude.Id);
        liveData.observe((AppCompatActivity)context, locations -> {
            builder.setAdapter(new MyListAdapter(context, locations), (dialog, which) -> {
                locationToExclude.PreviousId = locations.get(which).Id;
                DatabaseQueryHelper.update(locationToExclude, context);
                Toast.makeText(context, "La destinazione precedente è stata impostata", Toast.LENGTH_SHORT).show();
            });
            builder.show();
            liveData.removeObservers((AppCompatActivity)context);
        });
    }

    private static void setReminder(View view, Location location) {
        TextInputEditText editText = view.findViewById(R.id.reminder);
        location.Reminder = editText.getText() != null ? editText.getText().toString(): "Errore";
        Toast.makeText(view.getContext(), "Il promemoria è stato salvato.", Toast.LENGTH_SHORT).show();
        DatabaseQueryHelper.update(location, view.getContext());
    }

    private static class MyListAdapter extends ArrayAdapter<Location> {

        private MyListAdapter(@NonNull Context context, List<Location> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.previous_layout, parent, false);
            }
            Location location = getItem(position);
            ((TextView)convertView.findViewById(R.id.previous_name)).setText(location != null ? location.DisplayName : "Errore");
            return convertView;
        }
    }
}
