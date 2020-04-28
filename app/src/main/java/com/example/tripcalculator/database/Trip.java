package com.example.tripcalculator.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Trip {

    @PrimaryKey(autoGenerate = true)
    public int TripId;

    @NonNull
    public String Name;

    @Nullable
    public String Diary;

    public boolean IsActive;

    public boolean IsEnded;

    @Nullable
    public Date StartDate;

    @Nullable
    public Date EndDate;
}
