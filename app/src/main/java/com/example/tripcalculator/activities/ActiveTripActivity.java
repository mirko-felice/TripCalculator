package com.example.tripcalculator.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.ActivityActiveTripBinding;
import com.example.tripcalculator.fragments.MapFragment;
import com.example.tripcalculator.fragments.SummaryFragment;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.LocationViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ActiveTripActivity extends AppCompatActivity {

    private int tripId = -1;
    private ActivityActiveTripBinding binding;
    private FragmentManager fragmentManager;
    private MapFragment mapFragment;
    private SummaryFragment summaryFragment;
    private boolean isNetworkConnected = false;

    private List<Location> path;
    private TextView actualLocationTextView;
    private int actualLocationIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        registerNetworkCallback();
        binding = ActivityActiveTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.fragment_layout, mapFragment);
        fragmentTransaction.commit();

        if (intent.hasExtra("TripId")){
            tripId = intent.getIntExtra("TripId", -1);
            AppDatabase.getInstance(this.getApplicationContext()).locationDao().getLocationsFromTrip(tripId).observe(this, locations -> {
                for (Location location : locations){
                    path.add(location);
                }
                if (path.size() == 0){
                    finish();
                    return;
                }

                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                summaryFragment = new SummaryFragment(new LocationViewModelFactory(getApplication(), tripId).create(LocationViewModel.class));
                fragmentTransaction1.add(R.id.activity_active_trip_layout, summaryFragment);
                fragmentTransaction1.hide(summaryFragment);
                fragmentTransaction1.commit();

                actualLocationTextView = binding.actualLocation;
                actualLocationIndex = 0;
                while (actualLocationIndex<path.size() && path.get(actualLocationIndex).IsPassed)  actualLocationIndex++;
                actualLocationTextView.setText(path.get(actualLocationIndex).DisplayName);

                setAnimations();

                mapFragment.setPath(path);
                mapFragment.showActualRoad();
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.active_trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.details){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(summaryFragment);
            fragmentTransaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
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

    private void registerNetworkCallback(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService((Context.CONNECTIVITY_SERVICE));

        if (connectivityManager != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isNetworkConnected = false;
        }
    };
}
