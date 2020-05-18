package com.example.tripcalculator.database;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

class StringConverter {

    private static final String SEPARATOR = ",";

    @TypeConverter
    public static List<String> fromString(String string) {
        return string == null ? null : Arrays.asList(string.split(SEPARATOR));
    }

    @TypeConverter
    public static String fromStringArray(List<String> array) {
        return array == null ? null : TextUtils.join(SEPARATOR, array);
    }
}
