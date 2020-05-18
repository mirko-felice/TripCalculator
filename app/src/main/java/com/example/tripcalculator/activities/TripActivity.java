package com.example.tripcalculator.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.ActivityTripBinding;
import com.example.tripcalculator.fragments.MapFragment;
import com.example.tripcalculator.fragments.MyFragment;
import com.example.tripcalculator.ui.adapters.LocationViewPagerAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;

import java.util.ArrayList;
import java.util.List;

public class TripActivity extends BaseActivity {

    private int tripId = -1;
    private ActivityTripBinding binding;
    private FragmentManager fragmentManager;
    private MapFragment mapFragment;
    private MyFragment myFragment;
    private boolean isNetworkConnected = false;

    private List<Location> path;
    private TextView actualLocationTextView;
    private int actualLocationIndex;

    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        registerNetworkCallback();
        binding = ActivityTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.fragment_layout, mapFragment);
        fragmentTransaction.commit();

        ViewPager2 viewPager = binding.locationViewPager;
        if (intent.hasExtra("TripId")){
            tripId = intent.getIntExtra("TripId", -1);

            locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
            locationViewModel.getLocationsFromTrip(tripId).observe(this, locations -> {
                for (Location location : locations){
                    path.add(location);
                }
                if (path.size() == 0){
                    finish();
                    return;
                }

                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                myFragment = new MyFragment(tripId);
                fragmentTransaction1.add(R.id.activity_active_trip_layout, myFragment);
                fragmentTransaction1.hide(myFragment);
                fragmentTransaction1.commit();

                LocationViewPagerAdapter adapter = new LocationViewPagerAdapter(this, locations);
                viewPager.setAdapter(adapter);
                /*actualLocationTextView = binding.actualLocation;
                actualLocationIndex = 0;
                while (actualLocationIndex<path.size() && path.get(actualLocationIndex).IsPassed)  actualLocationIndex++;
                if (actualLocationIndex == path.size()) {
                    actualLocationTextView.setText("Viaggio terminato");
                } else {
                    actualLocationTextView.setText(path.get(actualLocationIndex).DisplayName);
                }*/

                //setAnimations();

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
            if(myFragment.isVisible()){
                item.setIcon(R.drawable.ic_details);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(myFragment);
                fragmentTransaction.commit();
            } else {
                item.setIcon(R.drawable.ic_map);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.show(myFragment);
                fragmentTransaction.commit();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public void setLocationAsPassed(Location passedLocation){
        passedLocation.IsPassed = true;
        locationViewModel.updateLocation(passedLocation);
    }

    /*private void setAnimations(){
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
    }*/

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
