package org.autoride.autoride.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenUtility {

    public ScreenUtility() {
        // float density = activity.getResources().getDisplayMetrics().density;
    }

    public static int getDeviceHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels / 2;
    }

    public static int getDeviceWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels / 2;
    }
}