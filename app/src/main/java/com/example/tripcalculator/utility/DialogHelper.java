package com.example.tripcalculator.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.fragments.SummaryFragment;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class DialogHelper {

    public static void showSetReminderDialog(Location location, FragmentActivity activity){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        View view = View.inflate(activity, R.layout.reminder_view, null);
        ((TextInputEditText)view.findViewById(R.id.reminder)).setText(location.Reminder);
        builder.setTitle(R.string.reminder)
                .setView(view)
                .setPositiveButton(R.string.add_label, (dialog, which) -> setReminder(activity, view, location))
                .setNegativeButton(R.string.cancel, (dialog, which) -> Toast.makeText(activity, R.string.reminder_cancel_message, Toast.LENGTH_SHORT).show())
                .show();
    }

    public static void showSetPreviousDialog(Location locationToExclude, FragmentActivity activity){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setView(R.layout.previous_layout);
        LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
        LiveData<List<Location>> liveData = locationViewModel.getPossiblePreviousLocations(locationToExclude.TripId, locationToExclude.Id);
        liveData.observe(activity, locations -> {
            builder.setAdapter(new PreviousLocationAdapter(activity, locations), (dialog, which) -> {
                locationToExclude.PreviousId = locations.get(which).Id;
                locationViewModel.updateLocation(locationToExclude);
                Toast.makeText(activity, R.string.previous_message, Toast.LENGTH_SHORT).show();
            });
            builder.show();
            liveData.removeObservers(activity);
        });
    }

    public static void showAddNote(Location location, FragmentActivity activity){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        View view = View.inflate(activity, R.layout.note_view, null);
        ((TextInputEditText)view.findViewById(R.id.note)).setText(location.Note);
        builder.setTitle(R.string.note)
                .setView(view)
                .setPositiveButton(R.string.insert, (dialog, which) -> setNote(activity, view, location))
                .setNegativeButton(R.string.cancel, (dialog, which) -> Toast.makeText(activity, R.string.note_cancel_message, Toast.LENGTH_SHORT).show())
                .show();
    }

    public static void showImages(Location location, SummaryFragment fragment){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.requireContext());
        View view = View.inflate(fragment.requireContext(), R.layout.photo_view, null);
        builder.setView(view);
        GridView gridView = view.findViewById(R.id.grid);
        new ViewModelProvider(fragment.requireActivity()).get(LocationViewModel.class).getLocationFromId(location.Id).observe(fragment.getViewLifecycleOwner(), l -> {
            ImageAdapter adapter = new ImageAdapter(fragment.requireContext(), l.ImgNames);
            gridView.setAdapter(adapter);
        });
        LiveData<Trip> tripLiveData = new ViewModelProvider(fragment.requireActivity()).get(TripViewModel.class).getTripFromId(location.TripId);
        tripLiveData.observe(fragment.requireActivity(), trip -> {
            if (!trip.IsActive){
                view.findViewById(R.id.add_photo_btn).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.add_photo_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.add_photo_btn).setOnClickListener(v -> fragment.takePhoto(location));
            }
            tripLiveData.removeObservers(fragment.requireActivity());
        });
        builder.show();
    }

    public static void showReminder(Location location, SummaryFragment fragment){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.requireContext());
        builder.setTitle(R.string.show_reminder)
                .setMessage(location.Note != null && location.Note.length() > 0 ? location.Note : fragment.getString(R.string.no_reminder_message))
                .setNeutralButton(R.string.close, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showLocationName(Location location, FragmentActivity activity){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        View view = View.inflate(activity, R.layout.edit_location_name, null);
        ((TextInputEditText)view.findViewById(R.id.location_name_edit_text)).setText(location.DisplayName);
        builder.setTitle(R.string.edit_location_name)
                .setView(view)
                .setPositiveButton(R.string.save, (dialog, which) -> setLocationName(activity, view, location))
                .setNegativeButton(R.string.cancel, (dialog, which) -> Toast.makeText(activity, R.string.modify_location_name_canceled, Toast.LENGTH_SHORT).show())
                .show();
    }

    private static void setLocationName(FragmentActivity activity, View viewHolder, Location location){
        TextInputEditText editText = viewHolder.findViewById(R.id.location_name_edit_text);
        location.DisplayName = editText.getText() != null ? editText.getText().toString() : location.FullName;
        LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
        locationViewModel.updateLocation(location);
    }

    private static void setReminder(FragmentActivity activity, View viewHolder, Location location) {
        TextInputEditText editText = viewHolder.findViewById(R.id.reminder);
        location.Reminder = editText.getText() != null ? editText.getText().toString(): "";
        Toast.makeText(activity.getApplicationContext(), R.string.reminder_save_message, Toast.LENGTH_SHORT).show();
        LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
        locationViewModel.updateLocation(location);
    }

    private static void setNote(FragmentActivity activity, View viewHolder, Location location) {
        TextInputEditText editText = viewHolder.findViewById(R.id.note);
        location.Note = editText.getText() != null ? editText.getText().toString(): "";
        Toast.makeText(activity.getApplicationContext(), R.string.note_message, Toast.LENGTH_SHORT).show();
        LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
        locationViewModel.updateLocation(location);
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
                try (InputStream inputStream =  getContext().getContentResolver().openInputStream(Uri.parse(getItem(position)))){
                    Bitmap bitmap =  BitmapFactory.decodeStream(inputStream);
                    ((ImageView) convertView).setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.e("ImageAdapter", e.toString());
                }
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
