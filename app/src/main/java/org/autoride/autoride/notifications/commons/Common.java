package org.autoride.autoride.notifications.commons;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.listeners.ParserListenerDriver;
import org.autoride.autoride.model.DataMessage;
import org.autoride.autoride.model.DriverInfo;
import org.autoride.autoride.model.FCMResponse;
import org.autoride.autoride.model.FCMToken;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.notifications.helpers.FCMClient;
import org.autoride.autoride.notifications.helpers.FCMService;
import org.autoride.autoride.notifications.helpers.GoogleAPI;
import org.autoride.autoride.notifications.helpers.RetrofitClient;
import org.autoride.autoride.notifications.helpers.rider.RiderFCMService;
import org.autoride.autoride.service.fcm.FcmMessagingService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common implements AppsConstants, RiderApiUrl {

    private static final String TAG = "Common";
    private Context context;
    private FCMService fcmService;
    private RiderFCMService riderFCMService;
    private String title, riderPhoto, riderFullName, riderToken, trackerToken, vehicleType, riderId,
            riderDestPlace, confirmFare, riderRating, message, trackerUserId, riderUserId,
            riderFirstName, riderPhone, riderAddress, trackerLongitude, trackerLatitude, lastDriverToken;
    private double km, min;
    private LatLng pickupLocation, destLocation;
    private static final String baseUrl = "https://maps.googleapis.com";
    private static final String fcmUrl = "https://fcm.googleapis.com/";
    private Handler handler;

    public Common() {

    }

    public Common(RiderFCMService riderFCMService, String title, String message, String riderToken, String trackerToken,
                  String riderPhoto, String riderFullName, String riderFirstName, String riderPhone,
                  String riderAddress, String riderUserId, String trackerUserId, String trackerLatitude, String trackerLongitude) {
        this.riderFCMService = riderFCMService;
        this.title = title;
        this.message = message;
        this.riderToken = riderToken;
        this.trackerToken = trackerToken;
        this.riderPhoto = riderPhoto;
        this.riderFullName = riderFullName;
        this.riderFirstName = riderFirstName;
        this.riderPhone = riderPhone;
        this.riderAddress = riderAddress;
        this.riderUserId = riderUserId;
        this.trackerUserId = trackerUserId;
        this.trackerLatitude = trackerLatitude;
        this.trackerLongitude = trackerLongitude;
    }



    public Common(RiderFCMService riderFCMService, String title, String message, String trackerToken,
                  String riderPhoto, String riderFullName) {
        this.riderFCMService = riderFCMService;
        this.title = title;
        this.message = message;
        this.trackerToken = trackerToken;
        this.riderPhoto = riderPhoto;
        this.riderFullName = riderFullName;

    }

    public Common(Context context, FCMService fcmService, String title, String riderToken, String vehicleType, String riderId,
                  String riderDestPlace, String confirmFare, String riderRating, double km, double min, LatLng pickupLocation,
                  LatLng destLocation) {
        this.context = context;
        this.fcmService = fcmService;
        this.title = title;
        this.riderToken = riderToken;
        this.vehicleType = vehicleType;
        this.riderId = riderId;
        this.riderDestPlace = riderDestPlace;
        this.confirmFare = confirmFare;
        this.riderRating = riderRating;
        this.km = km;
        this.min = min;
        this.pickupLocation = pickupLocation;
        this.destLocation = destLocation;
    }





    public static GoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseUrl).create(GoogleAPI.class);
    }

    public static FCMService getFCMService() {
        return FCMClient.getClient(fcmUrl).create(FCMService.class);
    }

    public static RiderFCMService getRiderFCMService() {
        return FCMClient.getClient(fcmUrl).create(RiderFCMService.class);
    }

    public void notificationToSingleNearestDriver() {
        ManagerData.driverTaskManager(GET, REQUESTED_DRIVER_URL, singleNearestDriverJSON(), null, new ParserListenerDriver() {
            @Override
            public void onLoadCompleted(String response) {
                if (response != null) {

                    if (FcmMessagingService.driverRequestStatus == 1) {
                        FcmMessagingService.driverRequestStatus = 0;
                    }

                    singleNearestDriverParser(response);
                } else {
                    FcmMessagingService.driverRequestStatus = 2;
                    backMessage();
                }
            }

            @Override
            public void onLoadFailed(String response) {
                FcmMessagingService.driverRequestStatus = 2;
                backMessage();
            }
        });
    }

    private JSONObject singleNearestDriverJSON() {
        JSONObject postData = new JSONObject();
        try {
            postData.put(LAT, pickupLocation.latitude);
            postData.put(LNG, pickupLocation.longitude);
            postData.put(VEHICLE, vehicleType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private void singleNearestDriverParser(String response) {
        DriverInfo driverInfo = new DriverInfo();
        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(response);
            if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(200))) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {

                        if (responseObj.has(WEB_RESPONSE_DRIVER)) {
                            JSONObject driverObj = responseObj.optJSONObject(WEB_RESPONSE_DRIVER);
                            if (driverObj != null) {

                                if (driverObj.has(DRIVER_ID)) {
                                    driverInfo.setDriverId(driverObj.getString(DRIVER_ID));
                                }

                                if (driverObj.has(VEHICLE_TYPE)) {
                                    driverInfo.setVehicleType(driverObj.getString(VEHICLE_TYPE));
                                }

                                if (driverObj.has(DRIVER_FIRE_BASE_TOKEN)) {
                                    driverInfo.setDriverFireBaseToken(driverObj.getString(DRIVER_FIRE_BASE_TOKEN));
                                    FcmMessagingService.driverRequestStatus = 0;
                                }

                                JSONArray locArr = driverObj.getJSONArray(DRIVER_LOCATION);
                                for (int j = 0; j < locArr.length(); j++) {
                                    driverInfo.setDriverLat((Double) locArr.get(1));
                                    driverInfo.setDriverLng((Double) locArr.get(0));
                                }

                                SharedPreferences preferences = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(TRIP_DRIVER_FIRE_BASE_TOKEN, driverInfo.getDriverFireBaseToken());
                                editor.commit();

                                sendNotificationToSingleDriver(driverInfo.getDriverFireBaseToken(), driverInfo.getVehicleType());
                            }
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        FcmMessagingService.driverRequestStatus = 2;
                        Toast.makeText(context.getApplicationContext(), UNABLE_FOUND_DRIVER, Toast.LENGTH_LONG).show();
                        goBack(context);
                    }
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                    FcmMessagingService.driverRequestStatus = 2;
                    backMessage();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                    FcmMessagingService.driverRequestStatus = 2;
                    backMessage();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                    FcmMessagingService.driverRequestStatus = 2;
                    backMessage();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                    FcmMessagingService.driverRequestStatus = 2;
                    backMessage();
                }
            } else {
                FcmMessagingService.driverRequestStatus = 2;
                backMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FcmMessagingService.driverRequestStatus = 2;
            backMessage();
        }
    }

    private void sendNotificationToSingleDriver(final String driverToken, String vType) {

        final FCMToken dFcmToken = new FCMToken(driverToken);
        String riderPickupLocation = new Gson().toJson(new LatLng(pickupLocation.latitude, pickupLocation.longitude));
        String riderDestLocation = new Gson().toJson(new LatLng(destLocation.latitude, destLocation.longitude));

        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("rider_fcm_token", riderToken);
        content.put("vehicle_type", vType);
        content.put("rider_id", riderId);
        content.put("rider_destination_place", riderDestPlace);
        content.put("confirm_fare", confirmFare);
        content.put("rider_pickup_location", riderPickupLocation);
        content.put("rider_destination", riderDestLocation);
        content.put("dest_km", String.valueOf(km));
        content.put("dest_min", String.valueOf(min));
        content.put("rider_rating", riderRating);

        DataMessage dataMessage = new DataMessage(dFcmToken.getToken(), content);
        fcmService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                if (response.body().getSuccess() == 1) {

                    Log.i(TAG, "RequestDriver success " + response.message());

                    if (FcmMessagingService.driverRequestStatus == 1) {
                        FcmMessagingService.driverRequestStatus = 0;
                    }

                    lastDriverToken = dFcmToken.getToken();
                    handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(notificationTask, 30000);

                    //  Toast.makeText(context.getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "RequestDriver error " + response.message());
                    FcmMessagingService.driverRequestStatus = 2;
                    backMessage();
                    // stopNotificationTask();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.i(TAG, "RequestDriver error" + t.getMessage());
                FcmMessagingService.driverRequestStatus = 2;
                backMessage();
                // stopNotificationTask();
            }
        });
    }

    public static void startWaitingDialog(ProgressDialog pDialog) {
        pDialog.setMessage("Please waiting...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void stopWaitingDialog(ProgressDialog pDialog) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void notificationToDriver(FCMService fcmServices, String dToken, String title, String msg) {

        FCMToken fcmFCMToken = new FCMToken(dToken);
        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("message", msg);

        DataMessage dataMessage = new DataMessage(fcmFCMToken.getToken(), content);
        fcmServices.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                Log.i(TAG, "success " + response.body().getResults().get(0).getMessage_id());
                if (response.body().getSuccess() == 1) {
                    Log.i(TAG, "Cancel Notification success " + response.message());
                    // Toast.makeText(DriverTracking.this, "Notify Success", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Cancel Notification Failed " + response.message());
                    // Toast.makeText(DriverTracking.this, "Notify Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.i(TAG, "Cancel Notification Error " + t.getMessage());
                // Toast.makeText(DriverTracking.this, "Notify Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendNotificationSingleRiderToTrack() {

        FCMToken trackerFcmToken = new FCMToken(trackerToken);
        Map<String, String> content = new HashMap<>();


        SharedPreferences sp = AutoRideRiderApps.getInstance().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        AutoRideRiderApps.getInstance().setRiderFireBaseToken(riderToken);

        content.put("title", "Invite");
        content.put("message", " Invite Request");
        content.put("rider_fcm_token", AutoRideRiderApps.getInstance().getRiderFireBaseToken());
        content.put("tracker_fcm_token", AutoRideRiderApps.getInstance().getTrackerFireBaseToken());
        content.put("tracker_user_id", AutoRideRiderApps.getInstance().getTrackerUserId());
        content.put("rider_user_id", AutoRideRiderApps.getInstance().getRiderUserId());

        DataMessage dataMessage = new DataMessage(trackerFcmToken.getToken(), content);
        fcmService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                Log.i(TAG, "success " + response.body().getResults().get(0).getMessage_id());
                if (response.body().getSuccess() == 1) {
                    Log.i(TAG, "rider invite request success! " + response.message());

                } else {
                    Log.i(TAG, "rider invitation fail! " + response.message());
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.i(TAG, "RequestDriver error" + t.getMessage());
            }
        });
    }


    public void invitationNotification() {

        FCMToken fcmFCMToken = new FCMToken(trackerToken);
        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("message", message);
        content.put("rider_token", riderToken);
        content.put("tracker_token", trackerToken);
        content.put("riderFullName", riderFullName);
        content.put("riderFirstName", riderFirstName);
        content.put("riderAddress", riderAddress);
        content.put("riderPhone", riderPhone);
        content.put("riderPhoto", riderPhoto);
        content.put("riderUserId", riderUserId);
        content.put("trackerUserId", trackerUserId);
        content.put("trackerLatitude", trackerLatitude);
        content.put("trackerLongitude", trackerLongitude);


        DataMessage dataMessage = new DataMessage(fcmFCMToken.getToken(), content);
        riderFCMService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                Log.i(TAG, "success " + response.body().getResults().get(0).getMessage_id());
                if (response.body().getSuccess() == 1) {
                    Log.i(TAG, "Notify success " + response.message());
                } else {
                    Log.i(TAG, "Notify Failed " + response.message());
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.i(TAG, "Notify Error " + t.getMessage());
            }
        });
    }

    private void backMessage() {
        Toast.makeText(context.getApplicationContext(), "Some thing went wrong, Please try again", Toast.LENGTH_LONG).show();
        goBack(context);
    }

    private void goBack(Context c) {
        c.startActivity(new Intent(c, RiderMainActivity.class));
    }

    private Runnable notificationTask = new Runnable() {
        @Override
        public void run() {
            if (FcmMessagingService.driverRequestStatus == 0) {
                notificationToDriver(fcmService, lastDriverToken, "rider_trip_canceled", "The rider has canceled ride");
                notificationToSingleNearestDriver();
            } else if (FcmMessagingService.driverRequestStatus == 1) {

            } else {
                stopNotificationTask();
            }
        }
    };

    private void stopNotificationTask() {
        if (notificationTask != null) {
            handler.removeCallbacks(notificationTask);
        }
    }

    public static void snackBarNoGps(final Activity root, View view) {
        Snackbar snackbar = Snackbar.make(view, R.string.no_gps_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.btn_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        root.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                }).setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        // sbView.setBackgroundColor(root.getResources().getColor(R.color.colorPrimary));

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public static void snackBarNoInternet(final Activity root, View view) {
        Snackbar snackbar = Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.btn_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        root.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                }).setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        // sbView.setBackgroundColor(root.getResources().getColor(R.color.colorPrimary));

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public static void snackBarAlert(String message, View view, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, listener).setActionTextColor(Color.RED);
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public static String directionsApi(LatLng startPoint, LatLng endPoint, Context context) {
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "mode=driving&" +
                "transit_routing_preference=less_driving&" +
                "origin=" + startPoint.latitude + "," + startPoint.longitude + "&" +
                "destination=" + endPoint.latitude + "," + endPoint.longitude + "&" +
                "key=" + context.getResources().getString(R.string.google_direction_api_key);
    }

    public static List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}