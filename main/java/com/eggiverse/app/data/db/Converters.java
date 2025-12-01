package com.eggiverse.app.data.db;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

public class Converters {

    @TypeConverter
    public static String fromStringSet(Set<String> set) {
        if (set == null) {
            return null;
        }
        return new JSONArray(set).toString();
    }

    @TypeConverter
    public static Set<String> toSet(String value) {
        Set<String> set = new HashSet<>();
        if (value == null) {
            return set;
        }
        try {
            JSONArray array = new JSONArray(value);
            for (int i = 0; i < array.length(); i++) {
                set.add(array.getString(i));
            }
        } catch (JSONException e) {
            // Handle exception or log error
        }
        return set;
    }
}
