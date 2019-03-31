package org.autoride.autoride.applications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.autoride.autoride.TrackRider.Model.TrackMeItem;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.configs.AppsSingleton;
import org.autoride.autoride.utils.RobotoCondensedTextStyleExtractor;
import org.autoride.autoride.utils.TypefaceManager;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AutoRideRiderApps extends Application implements AppsConstants {

    public static final int SERVER_CONNECTION_AVAILABLE = 0;
    public static final int NETWORK_NOT_AVAILABLE = 1;
    public static final int AUTH_TOKEN_NOT_AVAILABLE = 2;
    private static final String TAG = "AutoRideRiderApps";
    private static AutoRideRiderApps instance;
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private Resources r;
    private float px;
    private int width;
    private int height;
    // private FileOp fop = new FileOp(this);

    public AutoRideRiderApps() {

        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // setup handler for uncaught exception
        Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>EXCEPTION IN MAIN HANDLER>>>>>>>>>>>>>>>>>>>>>");
                final Writer result = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(result);
                ex.printStackTrace(printWriter);
                printWriter.close();

                ex.printStackTrace();
                Toast.makeText(getApplicationContext(), "LaTaxi Has Crashed Due To An Error."
                        + "\nApplication Will Restart Now."
                        + "\nSorry For Any Inconvinience Caused", Toast.LENGTH_SHORT).show();

                // restart(getApplicationContext(), 2000);
                //	check(getApplicationContext(), 1);
                // fop = new FileOp(getApplicationContext());
                // fop.writeDebug(result.toString());

                checkSession();
                // checkForToken();

                //	android.os.Process.killProcess(android.os.Process.myPid());
                defaultHandler.uncaughtException(thread, ex);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);


    }


    public static AutoRideRiderApps getInstance() {
        if (instance == null)
            instance = new AutoRideRiderApps();
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        // MultiDex.install(this);
    }


    private static AutoRideRiderApps myInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    @Override
    public void onCreate() {

        super.onCreate();

        instance = this;

        // FacebookSdk.sdkInitialize(this.getApplicationContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        checkSession();
        // checkForToken();

        TypefaceManager.addTextStyleExtractor(RobotoCondensedTextStyleExtractor.getInstance());
        setDefaultFont();

        r = getResources();
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
        width = r.getDisplayMetrics().widthPixels;
        height = r.getDisplayMetrics().heightPixels;
    }

    private void setDefaultFont() {
        try {

            final Typeface bold = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
            final Typeface italic = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Italic.ttf");
            final Typeface boldItalic = Typeface.createFromAsset(getAssets(), "RobotoCondensed-BoldItalic.ttf");
            final Typeface regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

            final Typeface normal = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
            final Typeface monospace = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Light.ttf");
            final Typeface serif = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
            final Typeface sansSerif = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
            final Typeface sans = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

            Field DEFAULT = Typeface.class.getDeclaredField("DEFAULT");
            DEFAULT.setAccessible(true);
            DEFAULT.set(null, regular);

            Field DEFAULT_BOLD = Typeface.class.getDeclaredField("DEFAULT_BOLD");
            DEFAULT_BOLD.setAccessible(true);
            DEFAULT_BOLD.set(null, bold);

            Field SERIF = Typeface.class.getDeclaredField("SERIF");
            SERIF.setAccessible(true);
            SERIF.set(null, serif);

            Field SANS_SERIF = Typeface.class.getDeclaredField("SANS_SERIF");
            SANS_SERIF.setAccessible(true);
            SANS_SERIF.set(null, sansSerif);

            Field SANS = Typeface.class.getDeclaredField("SANS");
            SANS.setAccessible(true);
            SANS.set(null, sans);

            Field NORMAL = Typeface.class.getDeclaredField("NORMAL");
            NORMAL.setAccessible(true);
            NORMAL.set(null, normal);

            Field MONOSPACE = Typeface.class.getDeclaredField("MONOSPACE");
            MONOSPACE.setAccessible(true);
            MONOSPACE.set(null, monospace);

            Field sDefaults = Typeface.class.getDeclaredField("sDefaults");
            sDefaults.setAccessible(true);
            sDefaults.set(null, new Typeface[]{
                    regular, bold, italic, boldItalic, monospace, serif, sansSerif, normal, sans
            });

        } catch (Throwable e) {
            //cannot crash app if there is a failure with overriding the default font!
            System.out.println(e);
        }
    }

    public static boolean isNetworkAvailable() {
        Context context = getInstance().getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationEnabled() {
        Context context = getInstance().getApplicationContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void saveToSession(RiderInfo riderInfo) {

        Context context = getInstance().getApplicationContext();
        setConfigs(riderInfo);

        SharedPreferences preferences = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.putString(ACCESS_TOKEN, riderInfo.getAccessToken());
        editor.putString(REMEMBER_TOKEN, riderInfo.getRememberToken());
        editor.putString(RIDER_ID, riderInfo.getRiderId());
        editor.putString(RIDER_RATING, riderInfo.getRiderRating());
        editor.putString(PHONE, riderInfo.getPhone());
        editor.putString(FIRST_NAME, riderInfo.getFirstName());
        editor.putString(LAST_NAME, riderInfo.getLastName());
        editor.putString(FULL_NAME, riderInfo.getFullName());
        editor.putString(PROFILE_PHOTO, riderInfo.getProfilePhoto());
        editor.putString(PROMOTION_CODE, riderInfo.getPromotionCode());
        editor.putString(ROLE, riderInfo.getRole());
        editor.putString(RIDER_SEARCH_VEHICLE_TYPE, riderInfo.getRiderSearchVehicle());
        editor.putString(LAT, riderInfo.getLastLatitude());
        editor.putString(LNG, riderInfo.getLastLongitude());
        editor.commit();
    }

    private static void setConfigs(RiderInfo riderInfo) {

        AppsSingleton.getInstance().setAccessToken(riderInfo.getAccessToken());
        AppsSingleton.getInstance().setRememberToken(riderInfo.getRememberToken());
        AppsSingleton.getInstance().setRiderId(riderInfo.getRiderId());
        AppsSingleton.getInstance().setRiderRating(riderInfo.getRiderRating());
        AppsSingleton.getInstance().setPhone(riderInfo.getPhone());
        AppsSingleton.getInstance().setFirstName(riderInfo.getFirstName());
        AppsSingleton.getInstance().setLastName(riderInfo.getLastName());
        AppsSingleton.getInstance().setFullName(riderInfo.getFullName());
        AppsSingleton.getInstance().setPhone(riderInfo.getProfilePhoto());
        AppsSingleton.getInstance().setPromotionCode(riderInfo.getPromotionCode());
        AppsSingleton.getInstance().setRole(riderInfo.getRole());
        AppsSingleton.getInstance().setRiderSearchVehicle(riderInfo.getRiderSearchVehicle());
        AppsSingleton.getInstance().setLastLatitude(riderInfo.getLastLatitude());
        AppsSingleton.getInstance().setLastLongitude(riderInfo.getLastLongitude());

        if (AppsSingleton.getInstance().getLastLatitude() == null) {
            AppsSingleton.getInstance().setLastLatitude(DOUBLE_QUOTES);
            AppsSingleton.getInstance().setLastLongitude(DOUBLE_QUOTES);
        }
    }

    public static boolean checkSession() {

        Context context = getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Log.i(TAG, "SESSION " + sp.getAll());

        String aToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
        String rToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        String riderRating = sp.getString(RIDER_RATING, DOUBLE_QUOTES);
        String phone = sp.getString(PHONE, DOUBLE_QUOTES);
        String fName = sp.getString(FIRST_NAME, DOUBLE_QUOTES);
        String lName = sp.getString(LAST_NAME, DOUBLE_QUOTES);
        String fullName = sp.getString(FULL_NAME, DOUBLE_QUOTES);
        String photo = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);
        String pCode = sp.getString(PROMOTION_CODE, DOUBLE_QUOTES);
        String role = sp.getString(ROLE, DOUBLE_QUOTES);
        String searchVehicle = sp.getString(RIDER_SEARCH_VEHICLE_TYPE, DOUBLE_QUOTES);
        String lat = sp.getString(LAT, DOUBLE_QUOTES);
        String lng = sp.getString(LNG, DOUBLE_QUOTES);

        if (!aToken.equals(DOUBLE_QUOTES) && !rToken.equals(DOUBLE_QUOTES)) {
            AppsSingleton.getInstance().setAccessToken(aToken);
            AppsSingleton.getInstance().setRememberToken(rToken);
            AppsSingleton.getInstance().setRiderId(riderId);
            AppsSingleton.getInstance().setRiderRating(riderRating);
            AppsSingleton.getInstance().setPhone(phone);
            AppsSingleton.getInstance().setFirstName(fName);
            AppsSingleton.getInstance().setLastName(lName);
            AppsSingleton.getInstance().setFullName(fullName);
            AppsSingleton.getInstance().setProfilePhoto(photo);
            AppsSingleton.getInstance().setPromotionCode(pCode);
            AppsSingleton.getInstance().setRole(role);
            AppsSingleton.getInstance().setRiderSearchVehicle(searchVehicle);
            AppsSingleton.getInstance().setLastLatitude(lat);
            AppsSingleton.getInstance().setLastLongitude(lng);
            if (AppsSingleton.getInstance().getLastLatitude() == null) {
                AppsSingleton.getInstance().setLastLatitude(DOUBLE_QUOTES);
                AppsSingleton.getInstance().setLastLongitude(DOUBLE_QUOTES);
            }
            return true;
        } else {
            if (AppsSingleton.getInstance().getLastLatitude() == null) {
                AppsSingleton.getInstance().setLastLatitude(DOUBLE_QUOTES);
                AppsSingleton.getInstance().setLastLongitude(DOUBLE_QUOTES);
            }
            return false;
        }
    }

    public static void logout() {
        Context context = getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }


    public float getPx() {
        return px;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setPx(float px) {
        this.px = px;
    }

    public static String getImagePath(String path) {
        // return !path.startsWith("http") ? AppConstants.BASE_URL + path : path;
        return null;
    }

    public static void restart(Context context, int delay) {
        if (delay == 0) {
            delay = 1;
        }
        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(2);
    }

    public static void check(Context context, int delay) {
        if (delay == 0) {
            delay = 1;
        }
        System.exit(0);
    }

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getUserDateFromUnix(String time) {

        if (time.equalsIgnoreCase("-62169984000") || time.equalsIgnoreCase("false") || time.equalsIgnoreCase("true"))
            return "";
        try {
            Calendar calTemp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calTemp.setTimeInMillis(Long.valueOf(time) * 1000);
            calTemp.setTimeZone(Calendar.getInstance().getTimeZone());
            time = new SimpleDateFormat("MMM dd, yyyy", Locale.US)
                    .format(new Date(calTemp.getTimeInMillis()));
            return time;
        } catch (Exception e) {
            //	e.printStackTrace();
            return time;
        }
    }

    public static String getUserTimeFromUnix(String date) {

        if (date.equalsIgnoreCase("-62169984000") || date.equalsIgnoreCase("false") || date.equalsIgnoreCase("true"))
            return "";
        try {
            Calendar calTemp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calTemp.setTimeInMillis(Long.valueOf(date) * 1000);
            calTemp.setTimeZone(Calendar.getInstance().getTimeZone());
            date = new SimpleDateFormat("hh:mma", Locale.US)
                    .format(new Date(calTemp.getTimeInMillis()));
            return date;
        } catch (Exception e) {
            //	e.printStackTrace();
            return date;
        }
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceID(Context context) {
        String DEVICEID = "";
        String IMEI = "";

        try {
            TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = mngr.getDeviceId();

            System.out.println("IMEI : " + IMEI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            DEVICEID = Build.SERIAL + IMEI;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DEVICE ID : " + DEVICEID);
        return DEVICEID;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequest(Object tag) {
        getRequestQueue().cancelAll(tag);
    }

    public static Context getAppContext() {
        return AutoRideRiderApps.mContext;
    }

    public void setConnectivityReciever(NetworkConnectionReciever.ConnectivityRecieverListener listener) {
        NetworkConnectionReciever.connectivityRecieverListener = listener;
    }


    public static boolean checkForToken() {

//        Context context = getInstance().getApplicationContext();
//        SharedPreferences prfs = context.getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
//
//        Log.i(TAG, "checkForToken: SESSION : " + prfs.getAll());
//
//        String aToken = prfs.getString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, "");
//        String rToken = prfs.getString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, "");
//        String userID = prfs.getString(AppConstants.PREFERENCE_KEY_SESSION_USERID, "");
//        String phone = prfs.getString(AppConstants.PREFERENCE_KEY_SESSION_PHONE, "");
//        String vStatus = prfs.getString(AppConstants.PREFERENCE_KEY_SESSION_VSTATUS, "");
//        String name = prfs.getString(AppConstants.PREFERENCE_KEY_SESSION_DRIVER_FULL_NAME, "");
//        String role = prfs.getString(AppConstants.PREFERENCE_KEY_USER_ROLE, "");
//        String lat = prfs.getString(AppConstants.PREFERENCE_KEY_USER_LAT, "");
//        String lng = prfs.getString(AppConstants.PREFERENCE_KEY_USER_LNG, "");
//        boolean isPhoneVerified = prfs.getBoolean(AppConstants.PREFERENCE_KEY_SESSION_IS_PHONE_VERIFIED, false);
//
//        if (!"".equals(aToken)) {
//
//            Config.getInstance().setAccessToken(aToken);
//            Config.getInstance().setRememberToken(rToken);
//            Config.getInstance().setUserID(userID);
//            Config.getInstance().setPhone(phone);
//            Config.getInstance().setVerificationStatus(vStatus);
//            Config.getInstance().setName(name);
//            Config.getInstance().setRole(role);
//            Config.getInstance().setCurrentLatitude(lat);
//            Config.getInstance().setCurrentLongitude(lng);
//            Config.getInstance().setPhoneVerified(isPhoneVerified);
//
//            if (Config.getInstance().getCurrentLatitude() == null) {
//                Config.getInstance().setCurrentLatitude("");
//                Config.getInstance().setCurrentLongitude("");
//            }
//            return true;
//        } else {
//            if (Config.getInstance().getCurrentLatitude() == null) {
//                Config.getInstance().setCurrentLatitude("");
//                Config.getInstance().setCurrentLongitude("");
//            }
//            return false;
//        }
        return false;
    }

    public static void saveToken() {
//        Context context = getInstance().getApplicationContext();
//
//        System.out.println("SAVE STARTED");
//        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//        //editor.putString(AppConstants.PREFERENCE_KEY_SESSION_TOKEN, Config.getInstance().getAuthToken());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_USERID, Config.getInstance().getUserID());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_EMAIL, Config.getInstance().getEmail());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_FIRSTNAME, Config.getInstance().getFirstName());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_LASTNAME, Config.getInstance().getLastName());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_DRIVER_FULL_NAME, Config.getInstance().getName());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_PHONE, Config.getInstance().getPhone());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_GENDER, Config.getInstance().getGender());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_DOB, Config.getInstance().getDOB());
////        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_GCM_ID, Config.getInstance().getGCMID());
////        editor.putBoolean(AppConstants.PREFERENCE_KEY_SESSION_IS_FIRST_TIME, Config.getInstance().isFirstTime());
//        editor.putBoolean(AppConstants.PREFERENCE_KEY_SESSION_IS_PHONE_VERIFIED, Config.getInstance().isPhoneVerified());
//        editor.commit();
//        FileOp fileOp = new FileOp(context);
//        fileOp.writeHash();
//        System.out.println("SAVE COMPLETE");
    }

    public static void saveToken(String authBean) {

//        Context context = getInstance().getApplicationContext();
//
//        setConfig(authBean);
//
//        System.out.println("SAVE STARTED");
//
//        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_ACCESS_TOKEN, authBean.getAccessToken());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_REMEMBER_TOKEN, authBean.getRememberToken());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_USERID, authBean.getUserID());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_PHONE, authBean.getUserPhone());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_VSTATUS, authBean.getVerificationStatus());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_DIMG, authBean.getProfilePhoto());
//        editor.putString(AppConstants.PREFERENCE_KEY_SESSION_DRIVER_FULL_NAME, authBean.getUserFullName());
//        editor.putString(AppConstants.PREFERENCE_KEY_USER_ROLE, authBean.getUserRole());
//        editor.putString(AppConstants.PREFERENCE_KEY_USER_LAT, authBean.getLastLatitude());
//        editor.putString(AppConstants.PREFERENCE_KEY_USER_LNG, authBean.getLastLongitude());
//        editor.putBoolean(AppConstants.PREFERENCE_KEY_SESSION_IS_PHONE_VERIFIED, authBean.isPhoneVerified());
//        editor.commit();
//
//        FileOp fileOp = new FileOp(context);
//        fileOp.writeHash();
//        System.out.println("SAVE COMPLETE");
    }

    private static void setConfig(String authBean) {

//        Config.getInstance().setAccessToken(authBean.getAccessToken());
//        Config.getInstance().setRememberToken(authBean.getRememberToken());
//        Config.getInstance().setUserID(authBean.getUserID());
//        Config.getInstance().setPhone(authBean.getUserPhone());
//        Config.getInstance().setPhone(authBean.getVerificationStatus());
//        Config.getInstance().setPhone(authBean.getProfilePhoto());
//        Config.getInstance().setName(authBean.getUserFullName());
//        Config.getInstance().setRole(authBean.getUserRole());
//        Config.getInstance().setCurrentLatitude(authBean.getLastLatitude());
//        Config.getInstance().setCurrentLongitude(authBean.getLastLongitude());
//        Config.getInstance().setPhoneVerified(authBean.isPhoneVerified());
//
//        if (Config.getInstance().getCurrentLatitude() == null) {
//            Config.getInstance().setCurrentLatitude("");
//            Config.getInstance().setCurrentLongitude("");
//        }
    }

    public static void clearApplicationData() {
//        Context context = getInstance().getApplicationContext();
//        File cache = context.getFilesDir();
//        File appDir = new File(cache.getParent());
//        if (appDir.exists()) {
//            String[] children = appDir.list();
//            for (String s : children) {
//                //		if (!s.equals("lib")) {
//                (new File(appDir, s)).delete();
//                //		}
//            }
//        }
    }



    //INVITE USER REQUEST
    private String appInstallStatus;
    private String trackerFireBaseToken;
    private String riderFireBaseToken;
    private String TrackerUserId;
    private String RiderUserId;
    private LatLng riderPickupLatLng;
    private String globalStatus;
    private String globalLocationStatus;
    private String imageUrlTracker;
    private String isBindStatus;
    private String success;
    private String error;
    private String trackerName;
    private String trackerLat;
    private String trackerLng;

    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

    public String getTrackerLat() {
        return trackerLat;
    }

    public void setTrackerLat(String trackerLat) {
        this.trackerLat = trackerLat;
    }

    public String getTrackerLng() {
        return trackerLng;
    }

    public void setTrackerLng(String trackerLng) {
        this.trackerLng = trackerLng;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getIsBindStatus() {
        return isBindStatus;
    }

    public void setIsBindStatus(String isBindStatus) {
        this.isBindStatus = isBindStatus;
    }

    public String getImageUrlTracker() {
        return imageUrlTracker;
    }

    public void setImageUrlTracker(String imageUrlTracker) {
        this.imageUrlTracker = imageUrlTracker;
    }

    public String getGlobalLocationStatus() {
        return globalLocationStatus;
    }

    public void setGlobalLocationStatus(String globalLocationStatus) {
        this.globalLocationStatus = globalLocationStatus;
    }

    public String getGlobalStatus() {
        return globalStatus;
    }

    public void setGlobalStatus(String globalStatus) {
        this.globalStatus = globalStatus;
    }

    public LatLng getRiderPickupLatLng() {
        return riderPickupLatLng;
    }

    public void setRiderPickupLatLng(LatLng riderPickupLatLng) {
        this.riderPickupLatLng = riderPickupLatLng;
    }

    public String getRiderUserId() {
        return RiderUserId;
    }

    public void setRiderUserId(String riderUserId) {
        RiderUserId = riderUserId;
    }

    public String getRiderFireBaseToken() {
        return riderFireBaseToken;
    }

    public void setRiderFireBaseToken(String riderFireBaseToken) {
        this.riderFireBaseToken = riderFireBaseToken;
    }

    public String getAppInstallStatus() {
        return appInstallStatus;
    }

    public void setAppInstallStatus(String appInstallStatus) {
        this.appInstallStatus = appInstallStatus;
    }

    public String getTrackerFireBaseToken() {
        return trackerFireBaseToken;
    }

    public void setTrackerFireBaseToken(String trackerFireBaseToken) {
        this.trackerFireBaseToken = trackerFireBaseToken;
    }

    public String getTrackerUserId() {
        return TrackerUserId;
    }

    public void setTrackerUserId(String trackerUserId) {
        TrackerUserId = trackerUserId;
    }


}