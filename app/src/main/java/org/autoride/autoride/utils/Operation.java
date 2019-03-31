package org.autoride.autoride.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import org.autoride.autoride.applications.AutoRideRiderApps;

/**
 * Created by goldenreign on 4/26/2018.
 */

public class Operation {
    public static void saveString(String keyValue, String getValue) {

        SharedPreferences sharedPreferences = AutoRideRiderApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyValue, getValue);
        editor.apply();

    }

    public static void saveSwitchStatus(String keyValue, boolean getValue){
        SharedPreferences sharedPreferences = AutoRideRiderApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(keyValue, getValue);
        editor.apply();
    }
    public static boolean getSwitchStatus(String keyValue,boolean defaultValue){
        SharedPreferences sharedPreferences = AutoRideRiderApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(keyValue, defaultValue);
    }

    public static String getString(String keyValue, String defaultValue) {

        SharedPreferences sharedPreferences = AutoRideRiderApps.getInstance().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        return sharedPreferences.getString(keyValue, defaultValue);
    }

    public static void saveSMS(Context ctx, String key, String value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static SharedPreferences sharedPreferences;

    public static String getSMS(Context ctx, String key) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void IntSaveToSharedPreference(Context ctx, String key, int value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static int getIntegerSharedPreference(Context ctx, String key, int defaultValue) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }
}
