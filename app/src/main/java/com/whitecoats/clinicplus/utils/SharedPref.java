package com.whitecoats.clinicplus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.whitecoats.clinicplus.constants.AppConstants;

public class SharedPref {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static SharedPref instance;

    public static SharedPref getPrefsHelper() {
        return instance;
    }

    public SharedPref(Context context) {
        instance = this;
        String prefsFile = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void delete(String key) {
        if (sharedPreferences!=null && sharedPreferences.contains(key)) {
            editor.remove(key).commit();
        }
    }

    public void savePref(String key, Object value) {
        if(editor!=null){ // Check for null in case editor not initialized
            delete(key);
            if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Enum) {
                editor.putString(key, value.toString());
            } else if (value != null) {
                throw new RuntimeException("Attempting to save non-primitive preference");
            }

            editor.commit();
        }

    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key, T defValue) {
        T returnValue = null;
        if(sharedPreferences!=null)
            returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean isPrefExists(String key) {
        return sharedPreferences.contains(key);
    }
}
