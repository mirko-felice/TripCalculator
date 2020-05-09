package com.example.tripcalculator.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

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

    @NonNull
    public boolean IsPassed;

    @Nullable
    public Long PreviousId;

    @Nullable
    public String Reminder;

    @NonNull
    public double Latitude;

    @NonNull
    public double Longitude;

    @NonNull
    public String DisplayName;

    public static DiffUtil.ItemCallback<Location> DIFF_CALLBACK = new DiffUtil.ItemCallback<Location>() {
        @Override
        public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Double.compare(location.Latitude, Latitude) == 0 &&
                Double.compare(location.Longitude, Longitude) == 0 &&
                DisplayName.equals(location.DisplayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Latitude, Longitude, DisplayName);
    }
}
