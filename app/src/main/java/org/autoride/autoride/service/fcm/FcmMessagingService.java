package org.autoride.autoride.service.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.SMSAuth.BroadcastReceiver.SmsReceiver;
import org.autoride.autoride.TrackRider.RiderTrackingActivity;
import org.autoride.autoride.TrackRider.TrackingAcceptActivity;
import org.autoride.autoride.TrackRider.TrackingCancelActivity;
import org.autoride.autoride.TrackRider.TrackingNotificationActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.notifications.DriverArrivedActivity;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.notifications.helpers.FCMService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FcmMessaging";
    private FCMService fcmService;
    private Handler handler;
    public static int driverRequestStatus = 0;

    @Override
    public void onMessageReceived(final RemoteMessage rm) {

        if (rm.getData() != null) {

            Map<String, String> data = rm.getData();
            String title = data.get("title");

            if (title.equalsIgnoreCase(data.get("riderFirstName") + " " + "share location")) {

                final String titles = data.get("title");
                final String message = data.get("message");
                final String riderFcmToken = data.get("rider_token");
                final String trackerFcmToken = data.get("tracker_token");
                final String riderFullName = data.get("riderFullName");
                final String riderPhoto = data.get("riderPhoto");
                final String riderFirstName = data.get("riderFirstName");
                final String riderPhone = data.get("riderPhone");
                final String riderAddress = data.get("riderAddress");

                final String riderUserId = data.get("riderUserId");
                final String trackerUserId = data.get("trackerUserId");
                final String trackerLatitude = data.get("trackerLatitude");
                final String trackerLongitude = data.get("trackerLongitude");

                trackingNotificationsBuilder(titles, message, riderFcmToken, trackerFcmToken, riderFullName, riderPhoto, riderFirstName, riderPhone, riderAddress, riderUserId, trackerUserId, trackerLatitude, trackerLongitude);


            } else if (title.equalsIgnoreCase("rider_accept_tracking_request")) {

                final String titles = data.get("title");
                final String message = data.get("message");
                final String riderFcmToken = data.get("rider_token");
                final String trackerFcmToken = data.get("tracker_token");
                final String riderFullName = data.get("riderFullName");
                final String riderPhoto = data.get("riderPhoto");
                final String riderFirstName = data.get("riderFirstName");
                final String riderPhone = data.get("riderPhone");
                final String riderAddress = data.get("riderAddress");

                final String riderUserId = data.get("riderUserId");
                final String trackerUserId = data.get("trackerUserId");
                final String trackerLatitude = data.get("trackerLatitude");
                final String trackerLongitude = data.get("trackerLongitude");

                trackingNotificationsBuilder(titles, message, riderFullName, riderPhoto);


            } else if (title.equalsIgnoreCase("rider_cancel_tracking_request")) {

                final String titles = data.get("title");
                final String message = data.get("message");
                final String riderFcmToken = data.get("rider_token");
                final String trackerFcmToken = data.get("tracker_token");
                final String riderFullName = data.get("riderFullName");
                final String riderPhoto = data.get("riderPhoto");
                final String riderFirstName = data.get("riderFirstName");
                final String riderPhone = data.get("riderPhone");
                final String riderAddress = data.get("riderAddress");

                final String riderUserId = data.get("riderUserId");
                final String trackerUserId = data.get("trackerUserId");
                final String trackerLatitude = data.get("trackerLatitude");
                final String trackerLongitude = data.get("trackerLongitude");

                trackingNotificationsBuilder(titles, message, riderPhoto);
            } else if (title.equalsIgnoreCase("driver_request_cancel")) {

                driverRequestStatus = 1;

                fcmService = Common.getFCMService();
                handler = new Handler(Looper.getMainLooper());

                final String riderToken = data.get("rider_fcm_token");
                final String vehicleType = data.get("vehicle_type");
                final String riderId = data.get("rider_id");
                final String riderDestPlace = data.get("rider_destination_place");
                final String pickupLocation = data.get("pickup_location");
                final String destination = data.get("dest_location");
                final String confirmFare = data.get("confirm_fare");
                final String destKm = data.get("dest_km");
                final String destMin = data.get("dest_min");
                final String riderRating = data.get("rider_rating");

                final LatLng rPickupLocation = new Gson().fromJson(pickupLocation, LatLng.class);
                final LatLng rDestination = new Gson().fromJson(destination, LatLng.class);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Common common = new Common(FcmMessagingService.this, fcmService, "rider_ride_request", riderToken,
                                vehicleType, riderId, riderDestPlace, confirmFare, riderRating, Double.valueOf(destKm), Double.valueOf(destMin), rPickupLocation, rDestination);
                        common.notificationToSingleNearestDriver();
                    }
                });
            } else if (title.equalsIgnoreCase("driver_request_accept")) {

                driverRequestStatus = 2;

                handler = new Handler(Looper.getMainLooper());

                final Map<String, String> dataDesc = data;
                String pickupLocation = dataDesc.get("pickup_location");
                String destination = dataDesc.get("dest_location");
                String driverLocation = dataDesc.get("driver_location");
                final LatLng rPickupLocation = new Gson().fromJson(pickupLocation, LatLng.class);
                final LatLng rDestination = new Gson().fromJson(destination, LatLng.class);
                final LatLng dLocation = new Gson().fromJson(driverLocation, LatLng.class);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FcmMessagingService.this, RiderMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("notification_status", "driver_request_accept");
                        intent.putExtra("driver_id", dataDesc.get("driver_id"));
                        intent.putExtra("vehicle_type", dataDesc.get("vehicle_type"));
                        intent.putExtra("driver_photo", dataDesc.get("driver_photo"));
                        intent.putExtra("vehicle_photo", dataDesc.get("vehicle_photo"));
                        intent.putExtra("driver_name", dataDesc.get("driver_name"));
                        intent.putExtra("v_brand", dataDesc.get("v_brand"));
                        intent.putExtra("driver_rating", dataDesc.get("driver_rating"));
                        intent.putExtra("driver_phone", dataDesc.get("driver_phone"));
                        intent.putExtra("v_desc", dataDesc.get("v_desc"));
                        intent.putExtra("rider_destination_place", dataDesc.get("rider_destination_place"));
                        intent.putExtra("pickup_lat", rPickupLocation.latitude);
                        intent.putExtra("pickup_lng", rPickupLocation.longitude);
                        intent.putExtra("dest_lat", rDestination.latitude);
                        intent.putExtra("dest_lng", rDestination.longitude);
                        intent.putExtra("arriving_time", dataDesc.get("arriving_time"));
                        intent.putExtra("driver_lat", dLocation.latitude);
                        intent.putExtra("driver_lng", dLocation.longitude);
                        intent.putExtra("confirm_fare", dataDesc.get("confirm_fare"));
                        startActivity(intent);
                    }
                });
            } else if (title.equalsIgnoreCase("driver_arrived")) {

                handler = new Handler(Looper.getMainLooper());

                final String message = data.get("message");
                final String vehicleType = data.get("vehicle_type");
                final String driverLatLng = data.get("driver_lat_lng");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        arrivedNotificationsBuilder("Auto Ride Driver Arrived", vehicleType, message, driverLatLng);
                    }
                });
            } else if (title.equalsIgnoreCase("driver_trip_completed")) {

                handler = new Handler(Looper.getMainLooper());

                final String message = data.get("message");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FcmMessagingService.this, RiderMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra("notification_status", "driver_trip_completed");
                        intent.putExtra("notifications_msg", message);

                        startActivity(intent);
                    }
                });
            } else if (title.equalsIgnoreCase("driver_trip_canceled")) {

                handler = new Handler(Looper.getMainLooper());

                final String message = data.get("message");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FcmMessagingService.this, RiderMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra("notification_status", "driver_trip_canceled");
                        intent.putExtra("notifications_msg", message);

                        startActivity(intent);
                    }
                });
            } else if (title.equalsIgnoreCase("driver_start_trip")) {

                handler = new Handler(Looper.getMainLooper());

                final Map<String, String> dataDesc = data;
                String pickupLocation = dataDesc.get("pickup_location");
                String destination = dataDesc.get("dest_location");
                String driverLocation = dataDesc.get("driver_location");
                final LatLng rPickupLocation = new Gson().fromJson(pickupLocation, LatLng.class);
                final LatLng rDestination = new Gson().fromJson(destination, LatLng.class);
                final LatLng dLocation = new Gson().fromJson(driverLocation, LatLng.class);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FcmMessagingService.this, RiderMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("notification_status", "driver_start_trip");
                        intent.putExtra("driver_id", dataDesc.get("driver_id"));
                        intent.putExtra("vehicle_type", dataDesc.get("vehicle_type"));
                        intent.putExtra("driver_photo", dataDesc.get("driver_photo"));
                        intent.putExtra("vehicle_photo", dataDesc.get("vehicle_photo"));
                        intent.putExtra("driver_name", dataDesc.get("driver_name"));
                        intent.putExtra("v_brand", dataDesc.get("v_brand"));
                        intent.putExtra("driver_rating", dataDesc.get("driver_rating"));
                        intent.putExtra("driver_phone", dataDesc.get("driver_phone"));
                        intent.putExtra("v_desc", dataDesc.get("v_desc"));
                        intent.putExtra("rider_destination_place", dataDesc.get("rider_destination_place"));
                        intent.putExtra("pickup_lat", rPickupLocation.latitude);
                        intent.putExtra("pickup_lng", rPickupLocation.longitude);
                        intent.putExtra("dest_lat", rDestination.latitude);
                        intent.putExtra("dest_lng", rDestination.longitude);
                        intent.putExtra("arriving_time", dataDesc.get("arriving_time"));
                        intent.putExtra("driver_lat", dLocation.latitude);
                        intent.putExtra("driver_lng", dLocation.longitude);
                        intent.putExtra("confirm_fare", dataDesc.get("confirm_fare"));
                        startActivity(intent);
                    }
                });
            } else if (title.equalsIgnoreCase("driver_request_problem")) {

                driverRequestStatus = 1;

                fcmService = Common.getFCMService();
                handler = new Handler(Looper.getMainLooper());

                final String riderToken = data.get("rider_fcm_token");
                final String vehicleType = data.get("vehicle_type");
                final String riderId = data.get("rider_id");
                final String riderDestPlace = data.get("rider_destination_place");
                final String pickupLocation = data.get("pickup_location");
                final String destination = data.get("dest_location");
                final String confirmFare = data.get("confirm_fare");
                final String destKm = data.get("dest_km");
                final String destMin = data.get("dest_min");
                final String riderRating = data.get("rider_rating");

                final LatLng rPickupLocation = new Gson().fromJson(pickupLocation, LatLng.class);
                final LatLng rDestination = new Gson().fromJson(destination, LatLng.class);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Common common = new Common(FcmMessagingService.this, fcmService, "driver_request_problem", riderToken,
                                vehicleType, riderId, riderDestPlace, confirmFare, riderRating, Double.valueOf(destKm), Double.valueOf(destMin), rPickupLocation, rDestination);
                        common.notificationToSingleNearestDriver();
                    }
                });
            }
        }
    }

    private void arrivedNotificationsBuilder(String title, String vehicleType, String mBody, String driverLatLng) {
        try {

            LatLng driverLocation = new Gson().fromJson(driverLatLng, LatLng.class);
            Intent intent = new Intent(this, DriverArrivedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("vehicle_type", vehicleType);
            intent.putExtra("driver_lat", driverLocation.latitude);
            intent.putExtra("driver_lng", driverLocation.longitude);

            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            builder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_auto_ride))
                    .setSmallIcon(R.drawable.ic_directions_bus_black_24dp)
                    .setContentTitle(title)
                    .setContentText(mBody)
                    .setContentIntent(contentIntent);

            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(5000);

            NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    NotificationCompat.Builder mBuilder;

    private void trackingNotificationsBuilder(String title, String mBody, String riderFcmToken, String trackerFcmToken,
                                              String riderFullName, String riderPhoto, String riderFirstName,
                                              String riderPhone, String riderAddress, String riderUserId,
                                              String trackerUserId, String trackerLatitude, String trackerLongitude) {
        try {

            Intent intent = new Intent(this, TrackingNotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("intent_status", riderFirstName + " " + "share location");
            intent.putExtra("title", title);
            intent.putExtra("message", mBody);
            intent.putExtra("rider_token", riderFcmToken);
            intent.putExtra("tracker_token", trackerFcmToken);
            intent.putExtra("riderFullName", riderFullName);
            intent.putExtra("riderPhoto", riderPhoto);
            intent.putExtra("riderFirstName", riderFirstName);
            intent.putExtra("riderPhone", riderPhone);
            intent.putExtra("riderAddress", riderAddress);
            intent.putExtra("riderUserId", riderUserId);
            intent.putExtra("trackerUserId", trackerUserId);
            intent.putExtra("trackerLatitude", trackerLatitude);
            intent.putExtra("trackerLongitude", trackerLongitude);
            startActivity(intent);

         /*   PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mBuilder = new NotificationCompat.Builder(getBaseContext());
            mBuilder.setAutoCancel(true);
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            mBuilder.setWhen(System.currentTimeMillis());
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.baseline_room_black_18dp));
           // mBuilder.setSmallIcon(R.drawable.baseline_room_black_18dp);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.ic_stat_name);
                mBuilder.setColor(getResources().getColor(android.R.color.white));
            } else {
                mBuilder.setSmallIcon(R.drawable.ic_stat_name);
            }
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(mBody);
            mBuilder.setContentIntent(contentIntent);*/
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(5000);

//            Notification notification = builder.build();
//            notification.sound =Uri.parse("android.resource://org.autoride.autoride/" + R.raw.notification_tone);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

/*
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(5000);

            NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, mBuilder.build());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trackingNotificationsBuilder(String title, String mBody,
                                              String riderFullName, String riderPhoto) {
        try {

            Intent intent = new Intent(this, TrackingAcceptActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("intent_status", "rider_accept_tracking_request");
            intent.putExtra("title", title);
            intent.putExtra("message", mBody);
            intent.putExtra("riderFullName", riderFullName);
            intent.putExtra("riderPhoto", riderPhoto);
            startActivity(intent);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(5000);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trackingNotificationsBuilder(String title, String mBody,
                                              String riderPhoto) {
        try {

            Intent intent = new Intent(this, TrackingCancelActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("intent_status", "rider_cancel_tracking_request");
            intent.putExtra("title", title);
            intent.putExtra("message", mBody);
            intent.putExtra("riderPhoto", riderPhoto);
            startActivity(intent);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(5000);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Drawable getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Drawable d = new BitmapDrawable(getResources(), myBitmap);
            return d;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public void setNotificationImage() {

         /*   Glide.with(getBaseContext())
                    .load(riderPhoto)
                    .apply(new RequestOptions().optionalCircleCrop()

                    )
                    .into(new SimpleTarget<Drawable>() {

                        @SuppressLint("NewApi")
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 150, 110, false);

                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapResized);
                            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                            roundedBitmapDrawable.setCircular(true);
                            mBuilder.setLargeIcon(roundedBitmapDrawable.getBitmap());
                            //CroppedDrawable cd = new CroppedDrawable(bitmapResized);
//                            toggle.setHomeAsUpIndicator(cd);
                        }
                    });*/
    }
}