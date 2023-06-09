package com.example.tripcalculator.database;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class StringConverter {

    private static final String SEPARATOR = ",";

    private StringConverter(){}

    @TypeConverter
    public static List<String> fromString(String string) {
        return string.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(string.split(SEPARATOR)));
    }

    @TypeConverter
    public static String fromStringArray(List<String> array) {
        return TextUtils.join(SEPARATOR, array);
    }
}
