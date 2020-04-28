package com.example.tripcalculator.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Trip.class, childColumns = "TripId", parentColumns = "TripId", onDelete = ForeignKey.CASCADE), indices = @Index(value = {"TripId", "Order"}, unique = true))
public class Location {

    @PrimaryKey(autoGenerate = true)
    public long Id;

    @NonNull
    public int Order;

    @NonNull
    public int TripId;

    @Nullable
    public String Note;

    @Nullable
    public String ImgNames;

    @Nullable
    public String IsPassed;

    @Nullable
    public Long PreviousId;

    @Nullable
    public String Reminder;
}
