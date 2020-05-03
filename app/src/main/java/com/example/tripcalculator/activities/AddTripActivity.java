package com.example.tripcalculator.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Intent;
import android.os.Bundle;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.ActivityAddTripBinding;
import com.example.tripcalculator.viewmodel.TripViewModel;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class AddTripActivity extends AppCompatActivity {

    private ActivityAddTripBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.add.setOnClickListener(v -> {
            Trip trip = new Trip();
            trip.TripId = 0;
            trip.Name = binding.editName.getText().toString();
            trip.IsActive = false;
            trip.IsEnded = false;
            Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(this).tripDao().insertTrip(trip));
            Intent intent = new Intent(this, AddLocationActivity.class);
            //TODO al posto di zero passare l'id dell'ultimo trip inserito
            intent.putExtra("TripId", 0);
            startActivity(intent);
            finish();
        });
    }
}
