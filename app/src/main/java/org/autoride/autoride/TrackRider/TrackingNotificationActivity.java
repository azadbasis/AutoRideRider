package org.autoride.autoride.TrackRider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.utils.Constants;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.autoride.autoride.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.autoride.constants.AppsConstants.FULL_NAME;
import static org.autoride.autoride.constants.AppsConstants.PROFILE_PHOTO;
import static org.autoride.autoride.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.SESSION_SHARED_PREFERENCES;

public class TrackingNotificationActivity extends AppCompatActivity implements OnMapReadyCallback, NetworkConnectionReciever.ConnectivityRecieverListener {

    private CircleImageView circleImageRequestTracker;
    private TextView textRequestSenderName, textRequestTitle, textRequestMessage,
            textRequestSenderId, textRequestSenderAddress, textRequestSenderPhone;
    private ImageView imageQrCodNotifier;
    private String title, message, riderFcmToken, trackerFcmToken, riderFullName, riderPhoto,
            riderFirstName, riderAddress, riderUserId, trackerUserId, riderPhone, trackerLatitude, trackerLongitude;

    private MapView mapView;
    private GoogleMap gmap;
    private Button buttonOk, buttonCancel;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    Boolean isConnected;
    public static final String NA = "NA";

    ViewGroup containerTrackingNotification;
    List<TrackingPeopleListItem> trackingPeopleListItems;
    TrackingPeopleListItem trackPeopleItem;
    Double lastLatitude,lastLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_notification);



        if (AutoRideRiderApps.isLocationEnabled()) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lastLatitude = location.getLatitude();
                        lastLongitude = location.getLongitude();
                    }
                }
            });

        }


        trackingPeopleListItems = new ArrayList<>();
        trackPeopleItem = new TrackingPeopleListItem();

        SharedPreferences sp = this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
        String rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        containerTrackingNotification = (ViewGroup) findViewById(R.id.ContainerTrackingNotification);
        buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (getIntent() != null) {
            String notificationStatus = getIntent().getStringExtra("intent_status");
            if (notificationStatus != null) {
                if (notificationStatus.equalsIgnoreCase(getIntent().getStringExtra("riderFirstName") + " " + "share location")) {
                    title = getIntent().getStringExtra("title");
                    message = getIntent().getStringExtra("message");
                    riderFcmToken = getIntent().getStringExtra("rider_token");
                    trackerFcmToken = getIntent().getStringExtra("tracker_token");
                    riderFullName = getIntent().getStringExtra("riderFullName");
                    riderPhoto = getIntent().getStringExtra("riderPhoto");
                    riderFirstName = getIntent().getStringExtra("riderFirstName");
                    riderPhone = getIntent().getStringExtra("riderPhone");
                    riderAddress = getIntent().getStringExtra("riderAddress");
                    riderUserId = getIntent().getStringExtra("riderUserId");
                    trackerUserId = getIntent().getStringExtra("trackerUserId");
                    trackerLatitude = getIntent().getStringExtra("trackerLatitude");
                    trackerLongitude = getIntent().getStringExtra("trackerLongitude");

                    circleImageRequestTracker = (CircleImageView) findViewById(R.id.circleImageRequestTracker);
                    textRequestSenderName = (TextView) findViewById(R.id.textRequestSenderName);
                    textRequestMessage = (TextView) findViewById(R.id.textRequestMessage);
                    textRequestSenderId = (TextView) findViewById(R.id.textRequestSenderId);
                    textRequestSenderAddress = (TextView) findViewById(R.id.textRequestSenderAddress);
                    textRequestSenderPhone = (TextView) findViewById(R.id.textRequestSenderPhone);
                    imageQrCodNotifier = (ImageView) findViewById(R.id.imageQrCodNotifier);

                    textRequestSenderName.setText(riderFullName);
                    textRequestSenderId.setText("ID: " + riderUserId);
                    textRequestSenderAddress.setText(riderAddress);
                    textRequestSenderPhone.setText(riderPhone);

                    textRequestMessage.setText(message + " " + riderFirstName);
                    loadTrackerImageUrl(riderPhoto);
                    generateQrCode(riderAddress, riderPhone);

                }
            }
        }
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getTrackingLocation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sp = TrackingNotificationActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        String profilePhoto = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);
                        String riderFullNames = sp.getString(FULL_NAME, DOUBLE_QUOTES);
                        Common c = new Common(Common.getRiderFCMService(), "rider_accept_tracking_request", "Your message Accept!", riderFcmToken, profilePhoto, riderFullNames);
                        c.invitationNotification();
                        finish();
                    }
                }, 2000);

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sp = TrackingNotificationActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        String profilePhoto = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);
                        String riderFullNames = sp.getString(FULL_NAME, DOUBLE_QUOTES);
                        Common c = new Common(Common.getRiderFCMService(), "rider_cancel_tracking_request", "Your invitation  cancel!", riderFcmToken, profilePhoto, riderFullNames);
                        c.invitationNotification();
                    }
                }, 1000);
            }
        });

    }

    private void loadTrackerImageUrl(String riderPhoto) {
        Picasso.with(this)
                .load(riderPhoto)//image
                .noFade()
                .into(circleImageRequestTracker, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startPostponedEnterTransition();
                        }
                    }

                    @Override
                    public void onError() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startPostponedEnterTransition();
                        }
                    }
                });
    }

    private void generateQrCode(String riderAddress, String riderPhone) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(riderFullName + "\n" + riderAddress + "\n " + riderPhone, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageQrCodNotifier.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//23.8401959,90.361566
        gmap = googleMap;
        gmap.setMinZoomPreference(12);

        double latitude = Double.valueOf(trackerLatitude);
        double longitude = Double.valueOf(trackerLongitude);
        LatLng ny = new LatLng(latitude, longitude);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));

        if (mapView != null) {
            gmap.addMarker(new MarkerOptions()
                    .position(ny).title("Your location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .draggable(false).visible(true));
        }

    }


    private void getTrackingLocation() {

        if (checkConnectivity()) {

            try {
                getAppUserLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showSnackBar();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public boolean checkConnectivity() {
        return NetworkConnectionReciever.isConnected();
        // showSnackBar(isConnected);
    }

    @Override
    public void OnNetworkChange(boolean inConnected) {
        this.isConnected = inConnected;
    }


    public void showSnackBar() {

        Snackbar.make(containerTrackingNotification, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();

    }

    public void getAppUserLocation() throws Exception {

        String url = Constants.REQUEST_STATUS_CHECK_URL + trackerUserId + "&trackerId=" + riderUserId+ "&lat=" + lastLatitude + "&lng=" + lastLongitude;;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                       // Toast.makeText(TrackingNotificationActivity.this, "response"+response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            if (statusCode.equalsIgnoreCase("200")) {
                                String status = jsonObject.getString("status");
                                if (status.equalsIgnoreCase("error")) {

                                    String errors = jsonObject.getString("errors");
                                    String message = jsonObject.getString("message");
                                    if (errors.equalsIgnoreCase("Already add this user..")) {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(TrackingNotificationActivity.this, RiderTrackingActivity.class));
                                                finish();

                                            }
                                        }, 2000);


                                        Snackbar.make(containerTrackingNotification, errors, Snackbar.LENGTH_SHORT).show();
                                    }
                                    // Snackbar.make()
                                }
                                if (status.equalsIgnoreCase("Success")) {

                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    if (success.equalsIgnoreCase("Successfully send notification...")) {
                                        //  trackingUserList();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(TrackingNotificationActivity.this, RiderTrackingActivity.class));
                                                finish();

                                            }
                                        }, 2000);

                                    }
                                }
                            } else {
                                Snackbar.make(containerTrackingNotification, "Slow Internet connection!", Snackbar.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if( error instanceof NetworkError) {
                        } else if( error instanceof ClientError) {
                            Log.d("ERROR", "error => " + error.toString());
                        } else if( error instanceof ServerError) {
                        } else if( error instanceof AuthFailureError) {
                        } else if( error instanceof ParseError) {
                        } else if( error instanceof NoConnectionError) {
                        } else if( error instanceof TimeoutError) {
                        }

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                SharedPreferences sp = TrackingNotificationActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
                String rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);

                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", accessToken);
                params.put("rememberToken", rememberToken);

                return params;
            }
        };

        AutoRideRiderApps.getInstance().addToRequestQueue(postRequest);
    }


}
