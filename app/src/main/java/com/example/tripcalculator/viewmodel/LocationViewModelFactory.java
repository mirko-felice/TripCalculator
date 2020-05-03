package com.example.tripcalculator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;

public class LocationViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private int tripId;
    private Application application;

    public LocationViewModelFactory(@NonNull Application application, int tripId) {
        super(application);
        this.application = application;
        this.tripId = tripId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(Application.class, int.class).newInstance(application, tripId);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            return super.create(modelClass);
        }
    }
}
