package com.example.tripcalculator.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity = Trip.class, childColumns = "TripId", parentColumns = "TripId", onDelete = ForeignKey.CASCADE), indices = @Index(value = {"TripId", "Order"}, unique = true))
public class Location {

    @PrimaryKey(autoGenerate = true)
    public long Id;

    public int Order;

    public int TripId;

    @Nullable
    public String Note;

    @Nullable
    public List<String> ImgNames;

    public boolean IsPassed;

    @Nullable
    public Long PreviousId;

    @Nullable
    public String Reminder;

    public double Latitude;

    public double Longitude;

    @NonNull
    public String DisplayName = "";
}
