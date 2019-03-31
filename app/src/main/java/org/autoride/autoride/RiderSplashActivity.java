package org.autoride.autoride;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.api.CallApi;
import org.autoride.autoride.networks.managers.api.RequestedUrlBuilder;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RiderSplashActivity extends BaseAppCompatNoDrawerActivity implements RiderApiUrl {

    private static final String TAG = "RiderSplash";
    private static boolean permissionGranted = false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String[] permissionsRequired = new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS};
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rider_splash);
        getSupportActionBar().hide();
        logoAnimator();
        setUiComponent();
    }

    private void setUiComponent() {
        setProgressScreenVisibility(true, true);
        client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
    }

    private void logoAnimator() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.reset();
        FrameLayout layout = findViewById(R.id.activity_splash);
        layout.clearAnimation();
        layout.startAnimation(animation);
        animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        animation.reset();
        ImageView ivSplashIcon = (ImageView) findViewById(R.id.iv_splash_icon);
        ivSplashIcon.clearAnimation();
        ivSplashIcon.startAnimation(animation);
    }

    private Thread splashThread = new Thread() {
        @Override
        public void run() {
            try {
                int waited = 0;
                while (waited < 2000) {
                    sleep(100);
                    waited += 100;
                }
                navigate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void navigate() {
        if (AutoRideRiderApps.isNetworkAvailable()) {
            if (AutoRideRiderApps.isLocationEnabled()) {
                if (AutoRideRiderApps.checkSession()) {
                    mainActivity();
                } else {
                    AutoRideRiderApps.logout();
                    Intent intent = new Intent(RiderSplashActivity.this, RiderWelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    RiderSplashActivity.this.finish();
                }
            } else {
                snackBarNoGps();
            }
        } else {
            snackBarNoInternet();
        }
    }

    private void mainActivity() {
        Intent intent = new Intent(RiderSplashActivity.this, RiderMainActivity.class);
        intent.putExtra(RiderMainActivity.EXTRA_REVEAL_X, 10);
        intent.putExtra(RiderMainActivity.EXTRA_REVEAL_Y, 10);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        RiderSplashActivity.this.finish();
    }

    private boolean checkPermission() {

        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);

        return result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED &&
                result4 == PackageManager.PERMISSION_GRANTED &&
                result5 == PackageManager.PERMISSION_GRANTED &&
                result6 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION,
                        CAMERA,
                        READ_PHONE_STATE,
                        WRITE_EXTERNAL_STORAGE,
                        READ_CONTACTS,
                        SEND_SMS},

                PERMISSION_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean readContactsAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean sendSMSAccepted = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted &&
                            cameraAccepted &&
                            readPhoneAccepted &&
                            writeExternalAccepted &&
                            readContactsAccepted &&
                            sendSMSAccepted
                            ) {
                        permissionGranted = true;
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION,
                                                            CAMERA,
                                                            READ_PHONE_STATE,
                                                            WRITE_EXTERNAL_STORAGE,
                                                            READ_CONTACTS,
                                                            SEND_SMS}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS,SEND_SMS}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS,SEND_SMS}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS,SEND_SMS}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }

                            if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS,SEND_SMS}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            if (shouldShowRequestPermissionRationale(SEND_SMS)) {
                                showMessageOKCancel("You need to allow all access permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS,SEND_SMS}, PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(RiderSplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AutoRideRiderApps.isNetworkAvailable()) {
            try {
                String appsV = getAppsVersion();
                String newV = getUpdateVersion();
                if (appsV != null && newV != null) {
                    if (!appsV.equalsIgnoreCase(newV)) {
                        setProgressScreenVisibility(false, false);
                        showUpdateDialog(newV);
                    } else {
                        setProgressScreenVisibility(false, false);
                        if (checkPermission()) {
                            permissionGranted = true;
                        } else if (!checkPermission()) {
                            requestPermission();
                        }
                        if (permissionGranted) {
                            splashThread.start();
                            getUserLastLocation();
                        }
                    }
                } else {
                    setProgressScreenVisibility(false, false);
                    Log.i(TAG, "vCheck " + appsV + " newV " + newV);
                    snackBarSlowInternet();
                }
            } catch (Exception e) {
                setProgressScreenVisibility(false, false);
                e.printStackTrace();
                snackBarSlowInternet();
            }
        } else {
            setProgressScreenVisibility(false, false);
            snackBarNoInternet();
        }
    }

    private void showUpdateDialog(String nVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RiderSplashActivity.this);
        builder.setTitle(getString(R.string.update_available));
        builder.setMessage(getString(R.string.update_message) + " " + nVersion);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_update_available, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.autoride.autoride")));
                dialog.cancel();
            }
        });
        builder.show();
    }

    private String getAppsVersion() {
        String appsVersion;
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            appsVersion = packageInfo.versionName;
        } catch (Exception e) {
            appsVersion = null;
            Log.i(TAG, "apps_version " + e.toString());
            e.printStackTrace();
        }
        return appsVersion;
    }

    private String getUpdateVersion() throws ExecutionException, InterruptedException {
        return new GetNewVersion().execute(RIDER_VERSION_URL).get();
    }

    private class GetNewVersion extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url[0])
                );
            } catch (Exception e) {
                Log.i(TAG, ERROR_RESPONSE + e.toString());
                e.printStackTrace();
            }
            Log.i(TAG, HTTP_RESPONSE + response);
            return versionParser(response);
        }
    }

    private String versionParser(String response) {
        String versionName = null;
        JSONObject jsonObj = null;
        try {
            if (response != null) {
                jsonObj = new JSONObject(response);
                if (jsonObj.has(WEB_RESPONSE_STATUS_CODE)) {
                    if (jsonObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(WEB_RESPONSE_CODE_200))) {

                        if (jsonObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                            if (jsonObj.has(WEB_RESPONSE_DATA)) {
                                JSONObject dataObj = jsonObj.optJSONObject(WEB_RESPONSE_DATA);
                                if (dataObj != null) {
                                    if (dataObj.has(VERSION_NAME)) {
                                        versionName = dataObj.getString(VERSION_NAME);
                                    }
                                }
                            }
                        }

                        if (jsonObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            versionName = null;
                        }
                    }

                    if (jsonObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        versionName = null;
                    }

                    if (jsonObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        versionName = null;
                    }

                    if (jsonObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        versionName = null;
                    }

                    if (jsonObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        versionName = null;
                    }
                }
            } else {
                versionName = null;
            }
        } catch (Exception e) {
            versionName = null;
            e.printStackTrace();
        }
        return versionName;
    }

    private void snackBarNoGps() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.no_gps_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                }).setActionTextColor(getResources().getColor(R.color.holo_red_light));
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
        params.gravity = Gravity.TOP;
        sbView.setLayoutParams(params);

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void snackBarNoInternet() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                }).setActionTextColor(getResources().getColor(R.color.holo_red_light));
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
        params.gravity = Gravity.TOP;
        sbView.setLayoutParams(params);

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void snackBarSlowInternet() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).setActionTextColor(getResources().getColor(R.color.holo_red_light));
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
        params.gravity = Gravity.TOP;
        sbView.setLayoutParams(params);

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        //
    }
}