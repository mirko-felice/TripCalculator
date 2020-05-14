package com.example.tripcalculator.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            builder.setAdapter(new PreviousLocationAdapter(context, locations), (dialog, which) -> {
                locationToExclude.PreviousId = locations.get(which).Id;
                DatabaseQueryHelper.update(locationToExclude, context);
                Toast.makeText(context, "La destinazione precedente è stata impostata", Toast.LENGTH_SHORT).show();
            });
            builder.show();
            liveData.removeObservers((AppCompatActivity)context);
        });
    }

    public static void showAddNote(Location location, Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.note_view, null, false);
        builder.setTitle("Note")
                .setView(view)
                .setPositiveButton("Inserisci", (dialog, which) -> setNote(view, location))
                .setNegativeButton("Cancella", (dialog, which) -> Toast.makeText(context, "Nota non aggiunta", Toast.LENGTH_SHORT).show())
                .setOnDismissListener(dialog -> Toast.makeText(context, "Nota non aggiunta", Toast.LENGTH_SHORT).show())
                .show();
    }

    public static void showImages(Location location, Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.photo_view, null, false);
        builder.setView(view);
        GridView gridView = view.findViewById(R.id.grid);
        ImageAdapter adapter;
        if (location.ImgNames == null)
            adapter = new ImageAdapter(context, new ArrayList<>());
        else
            adapter = new ImageAdapter(context, location.ImgNames);
        gridView.setAdapter(adapter);
        builder.show();
    }

    private static void setReminder(View view, Location location) {
        TextInputEditText editText = view.findViewById(R.id.reminder);
        location.Reminder = editText.getText() != null ? editText.getText().toString(): "Errore";
        Toast.makeText(view.getContext(), "Il promemoria è stato salvato.", Toast.LENGTH_SHORT).show();
        DatabaseQueryHelper.update(location, view.getContext());
    }

    private static void setNote(View view, Location location) {
        TextInputEditText editText = view.findViewById(R.id.note);
        location.Note = editText.getText() != null ? editText.getText().toString(): "Errore";
        Toast.makeText(view.getContext(), "La nota è stata salvata.", Toast.LENGTH_SHORT).show();
        DatabaseQueryHelper.update(location, view.getContext());
    }

    private static class PreviousLocationAdapter extends ArrayAdapter<Location> {

        private PreviousLocationAdapter(@NonNull Context context, List<Location> items) {
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

    private static class ImageAdapter extends ArrayAdapter<String>{

        private ImageAdapter(@NonNull Context context, @NonNull List<String> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = new ImageView(getContext());
                convertView.setLayoutParams(new GridView.LayoutParams(300,300));
                Bitmap bitmap = null;
                try (InputStream inputStream =  getContext().getContentResolver().openInputStream(Uri.parse(getItem(position)))){
                    bitmap =  BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ((ImageView) convertView).setImageBitmap(bitmap);
                convertView.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String path = Uri.parse(getItem(position)).getLastPathSegment();
                    File[] files = Objects.requireNonNull(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)).listFiles();
                    assert files != null;
                    for(File f: files){
                        if(f.getName().equals(path)){
                            Uri uri = FileProvider.getUriForFile(getContext(), "com.example.tripcalculator.fileprovider", f);
                            intent.setDataAndType(uri, "image/jpg");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            break;
                        }
                    }
                    getContext().startActivity(intent);
                });
            }
            return convertView;
        }
    }
}
