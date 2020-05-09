package com.example.tripcalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.ActivityActiveTripBinding;
import com.example.tripcalculator.fragments.MapFragment;

import java.util.List;

public class ActiveTripActivity extends AppCompatActivity {

    private int tripId = -1;
    private ActivityActiveTripBinding binding;
    private FragmentManager fragmentManager;
    private MapFragment mapFragment;

    private List<Location> path;
    private TextView actualLocationTextView;
    private int actualLocationIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("TripId")){
            tripId = intent.getIntExtra("TripId", -1);
            path = AppDatabase.getInstance(this).locationDao().getLocationsFromTrip(tripId).getValue();
            binding = ActivityActiveTripBinding.inflate(getLayoutInflater());

            actualLocationTextView = binding.actualLocation;
            while (actualLocationIndex<path.size() && path.get(actualLocationIndex).IsPassed)  actualLocationIndex++;
            actualLocationTextView.setText(path.get(actualLocationIndex).DisplayName);

            setAnimations();

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = new MapFragment();
            mapFragment.setPath(path);
            mapFragment.showActualRoad();
            fragmentTransaction.add(R.id.fragment_layout, mapFragment);
            fragmentTransaction.commit();
        }
    }

    private void setAnimations(){
        Animation leftAnimation = AnimationUtils.loadAnimation(this, R.anim.left_slide);
        Animation rightAnimation = AnimationUtils.loadAnimation(this, R.anim.right_slide);

        leftAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                actualLocationIndex = (actualLocationIndex + path.size() - 1) % path.size();
                actualLocationTextView.setText(path.get(actualLocationIndex).DisplayName);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        rightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                actualLocationIndex = actualLocationIndex + 1 % path.size();
                actualLocationTextView.setText(path.get(actualLocationIndex).DisplayName);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        binding.leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftAnimation.start();
            }
        });
        binding.rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightAnimation.start();
            }
        });
    }
}
