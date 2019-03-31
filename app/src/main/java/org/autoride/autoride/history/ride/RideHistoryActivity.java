package org.autoride.autoride.history.ride;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.maps.android.ui.IconGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.configs.AppsSingleton;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.history.helpers.OnItemClickListener;
import org.autoride.autoride.history.helpers.RideHistoryInfo;
import org.autoride.autoride.history.helpers.RideHistoryRecyclerViewAdapter;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.api.CallApi;
import org.autoride.autoride.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.autoride.networks.managers.api.RequestedUrlBuilder;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.notifications.helpers.GoogleAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RideHistoryActivity extends AppCompatActivity implements AppsConstants, RiderApiUrl {

    private static final String TAG = "RideHistory";
    private RecyclerView rvRideHistory;
    private RideHistoryRecyclerViewAdapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;
    private List<RideHistoryInfo> rideHistoryInfoList;
    private ViewFlipper vfRideHistory;
    private SupportMapFragment riderDetailsMapsFragment;
    private GoogleMap riderDetailsMaps;
    private static final float MAP_ZOOM = 16.0f;
    private TextView tvNoRideMsg, tvDRideDate, tvDAmount, tvDVehicle, tvDPaymentType, tvDRideStatus, tvDRidePickup, tvDRideDest,
            tvDRideDName, tvRideBaseFare, tvRideDistanceFare, tvRideTimeFare, tvRideSubTotal, tvRideFareRounding,
            tvRideFareTotal, tvReceiptPaymentType, tvReceiptPaymentTotal, tvRideReceiptTitle;
    private CircleImageView civRideDriverPhoto;
    private ProgressDialog pDialog;
    private LinearLayout llHistoryRoot;
    private View.OnClickListener snackBarDismissListener;
    private String accessToken, rememberToken, riderId, rideStatus;
    private OkHttpClient okHttpClient;
    private GoogleAPI mService;
    private List<LatLng> polyLineList;
    private Polyline blackPolyline;
    private Marker pickupMarker, destinationMarker;
    private LatLng ridePickup, rideDest;
    private Button btnRideDetailsHelp, btnRideDetailsReceipt;
    private LinearLayout llRideDetailsHelp, llRideDetailsReceipt;
    private View viewRideDetailsHelp, viewRideDetailsReceipt;
    protected Animation slideRightIn, slideRightOut;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Xerox Serif Wide.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        setUiComponent();
    }

    private void setUiComponent() {

        pDialog = new ProgressDialog(this);
        Common.startWaitingDialog(pDialog);
        mService = Common.getGoogleAPI();

        okHttpClient = AppsSingleton.getInstance().getOkHttpClient();
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            okHttpClient = builder.build();
            AppsSingleton.getInstance().setOkHttpClient(okHttpClient);
        }

        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        btnRideDetailsHelp = (Button) findViewById(R.id.btn_ride_details_help);
        btnRideDetailsReceipt = (Button) findViewById(R.id.btn_ride_details_receipt);
        llRideDetailsHelp = (LinearLayout) findViewById(R.id.ll_ride_details_help);
        llRideDetailsReceipt = (LinearLayout) findViewById(R.id.ll_ride_details_receipt);
        viewRideDetailsHelp = findViewById(R.id.view_ride_details_help);
        viewRideDetailsReceipt = findViewById(R.id.view_ride_details_receipt);

        llHistoryRoot = (LinearLayout) findViewById(R.id.ll_history_root);
        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        };

        tvNoRideMsg = (TextView) findViewById(R.id.tv_no_ride_msg);
        tvDRideDate = (TextView) findViewById(R.id.tv_details_ride_date);
        tvDAmount = (TextView) findViewById(R.id.tv_details_ride_amount);
        tvDVehicle = (TextView) findViewById(R.id.tv_details_ride_vehicle_desc);
        tvDPaymentType = (TextView) findViewById(R.id.tv_details_ride_payment_type);
        tvDRideStatus = (TextView) findViewById(R.id.tv_details_ride_status);
        tvDRideDName = (TextView) findViewById(R.id.tv_ride_driver_name);
        //receipt
        tvRideReceiptTitle = (TextView) findViewById(R.id.tv_ride_receipt_title);
        tvRideBaseFare = (TextView) findViewById(R.id.tv_ride_base_fare);
        tvRideDistanceFare = (TextView) findViewById(R.id.tv_ride_distance_fare);
        tvRideTimeFare = (TextView) findViewById(R.id.tv_ride_time_fare);
        tvRideSubTotal = (TextView) findViewById(R.id.tv_ride_sub_total);
        tvRideFareRounding = (TextView) findViewById(R.id.tv_ride_fare_rounding);
        tvRideFareTotal = (TextView) findViewById(R.id.tv_ride_fare_total);
        tvReceiptPaymentType = (TextView) findViewById(R.id.tv_receipt_payment_type);
        tvReceiptPaymentTotal = (TextView) findViewById(R.id.tv_receipt_payment_total);

        tvDRidePickup = (TextView) findViewById(R.id.tv_details_ride_pickup);
        tvDRideDest = (TextView) findViewById(R.id.tv_details_ride_dest);

        civRideDriverPhoto = (CircleImageView) findViewById(R.id.civ_ride_driver_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ride_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideHistoryActivity.this, RiderMainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                RideHistoryActivity.this.finish();
            }
        });
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_ride_history);
        collapsingToolbar.setTitle("Your Ride");

        vfRideHistory = (ViewFlipper) findViewById(R.id.view_flipper_ride_history);
        vfRideHistory.setDisplayedChild(0);

        rideHistoryInfoList = new ArrayList<>();
        rvRideHistory = (RecyclerView) findViewById(R.id.recycler_view_ride_history);

        SharedPreferences sp = getBaseContext().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sp != null) {
            Log.i(TAG, "checkForSession " + sp.getAll());
            accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
            rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);
            riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        }

        performGetRideHistory();
        setRideHistoryDetailsUi();
    }

    private void setRideHistoryDetailsUi() {
        rvAdapter = new RideHistoryRecyclerViewAdapter(this, rideHistoryInfoList, new OnItemClickListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onClick(View view, int position) {

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ride_history_details);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vfRideHistory.setInAnimation(slideRightIn);
                        vfRideHistory.setOutAnimation(slideRightOut);
                        vfRideHistory.showPrevious();
                    }
                });
                CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ctl_ride_history_details);
                collapsingToolbar.setTitle("Ride Details");

                rideStatus = rideHistoryInfoList.get(position).getRideStatus();  // "riding"; //
                ridePickup = rideHistoryInfoList.get(position).getPickupLatLng();
                rideDest = rideHistoryInfoList.get(position).getDropLatLng();

                riderDetailsMapsFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ride_details_maps_fragment);
                riderDetailsMapsFragment.getMapAsync(new OnMapReadyCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        riderDetailsMaps = googleMap;
                        riderDetailsMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        riderDetailsMaps.setMyLocationEnabled(false);
                        riderDetailsMaps.getUiSettings().setZoomControlsEnabled(false);
                        riderDetailsMaps.getUiSettings().setAllGesturesEnabled(false);

                        riderDetailsMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(ridePickup, MAP_ZOOM));
                        if (ridePickup != null && rideDest != null) {
                            detailsRoute(ridePickup, rideDest, rideStatus);
                        }
                    }
                });

                tvDRideDate.setText(rideHistoryInfoList.get(position).getRideDate());
                tvDAmount.setText(rideHistoryInfoList.get(position).getAmount());
                tvDVehicle.setText(rideHistoryInfoList.get(position).getVehicleDesc());
                tvDPaymentType.setText(rideHistoryInfoList.get(position).getPaymentType());

                if (rideStatus.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {
                    tvDRideStatus.setVisibility(View.GONE);
                } else {
                    tvDRideStatus.setText(rideStatus);
                }
                tvDRideDName.setText(RIDE_TEXT + rideHistoryInfoList.get(position).getRideDriverName());
                Glide.with(getBaseContext())
                        .load(rideHistoryInfoList.get(position).getRideDriverPhoto())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .fitCenter()
                                .error(R.drawable.ic_profile_photo_default)
                                .fallback(R.drawable.ic_profile_photo_default))
                        .into(civRideDriverPhoto);
                //receipt
                tvRideReceiptTitle.setText(RECEIPT_TITLE + rideHistoryInfoList.get(position).getVehicleType() + RECEIPT_TITLE2);

                Double bFare = rideHistoryInfoList.get(position).getBaseFare();
                Double dFare = rideHistoryInfoList.get(position).getDistanceFare();
                Double tFare = rideHistoryInfoList.get(position).getTimeFare();
                Double subTotal = bFare + dFare + tFare;
                Double round = roundDown(subTotal);
                Double rounding = (round - subTotal);

                String fBFare = TAKA + String.format("%.2f", bFare);
                String fDFare = TAKA + String.format("%.2f", dFare);
                String fTFare = TAKA + String.format("%.2f", tFare);
                String fSTotal = TAKA + String.format("%.2f", subTotal);
                String fRound = TAKA + String.format("%.2f", round);
                String fRounding = TAKA + String.format("%.2f", rounding);

                tvRideBaseFare.setText(fBFare);
                tvRideDistanceFare.setText(fDFare);
                tvRideTimeFare.setText(fTFare);
                tvRideSubTotal.setText(fSTotal);
                tvRideFareRounding.setText(fRounding);
                tvRideFareTotal.setText(fRound);
                tvReceiptPaymentTotal.setText(fRound);
                String pType = rideHistoryInfoList.get(position).getPaymentType();
                tvReceiptPaymentType.setText(pType);

                JSONObject qrCodeContents = new JSONObject();
                try {
                    qrCodeContents.put("Base Fare", fBFare);
                    qrCodeContents.put("Distance Fare", fDFare);
                    qrCodeContents.put("Time Fare", fTFare);
                    qrCodeContents.put("Fare Subtotal", fSTotal);
                    qrCodeContents.put("Fare Rounding", fRounding);
                    qrCodeContents.put("Fare Total", fRound);
                    qrCodeContents.put("Payment " + pType, fRound);
                    ((ImageView) findViewById(R.id.iv_ride_receipt_qr_code)).setImageBitmap(qrCodeMaker(qrCodeContents.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                vfRideHistory = (ViewFlipper) findViewById(R.id.view_flipper_ride_history);
                vfRideHistory.setDisplayedChild(1);

                btnRideDetailsHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (llRideDetailsHelp.getVisibility() != View.VISIBLE) {
                            btnRideDetailsHelp.setTextColor(Color.parseColor("#545961"));
                            llRideDetailsHelp.setVisibility(View.VISIBLE);
                        }
                        if (viewRideDetailsHelp.getVisibility() != View.VISIBLE) {
                            viewRideDetailsHelp.setVisibility(View.VISIBLE);
                        }
                        if (llRideDetailsReceipt.getVisibility() == View.VISIBLE) {
                            btnRideDetailsReceipt.setTextColor(Color.parseColor("#B0B0B7"));
                            llRideDetailsReceipt.setVisibility(View.GONE);
                        }
                        if (viewRideDetailsReceipt.getVisibility() == View.VISIBLE) {
                            viewRideDetailsReceipt.setVisibility(View.GONE);
                        }
                    }
                });

                btnRideDetailsReceipt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (llRideDetailsReceipt.getVisibility() != View.VISIBLE) {
                            btnRideDetailsReceipt.setTextColor(Color.parseColor("#545961"));
                            llRideDetailsReceipt.setVisibility(View.VISIBLE);
                        }
                        if (viewRideDetailsReceipt.getVisibility() != View.VISIBLE) {
                            viewRideDetailsReceipt.setVisibility(View.VISIBLE);
                        }
                        if (llRideDetailsHelp.getVisibility() == View.VISIBLE) {
                            btnRideDetailsHelp.setTextColor(Color.parseColor("#B0B0B7"));
                            llRideDetailsHelp.setVisibility(View.GONE);
                        }
                        if (viewRideDetailsHelp.getVisibility() == View.VISIBLE) {
                            viewRideDetailsHelp.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        rvLayoutManager = new LinearLayoutManager(this);
        rvRideHistory.setLayoutManager(rvLayoutManager);
        rvRideHistory.setHasFixedSize(true);
        rvRideHistory.setNestedScrollingEnabled(false);
        rvRideHistory.setAdapter(rvAdapter);
    }

    private Double roundDown(double number) {
        double result = number / 10;
        result = Math.floor(result);
        result *= 10;
        return result;
    }

    private Bitmap qrCodeMaker(String contents) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(contents, BarcodeFormat.QR_CODE, 400, 400);
        return new BarcodeEncoder().createBitmap(matrix);
    }

    private void performGetRideHistory() {
        try {
            new GetRideHistory().execute(RIDE_HISTORY_URL).get();
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            Common.snackBarAlert(getString(R.string.slow_internet_connection), llHistoryRoot, snackBarDismissListener);
            e.printStackTrace();
        }
    }

    private class GetRideHistory extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedGETUrl(url[0], getBodyJSON()),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
            } catch (Exception e) {
                Log.i(TAG, ERROR_RESPONSE + e.toString());
                e.printStackTrace();
            }
            Log.i(TAG, HTTP_RESPONSE + response);
            return setRideHistoryInfo(response);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private String setRideHistoryInfo(String response) {
        JSONObject responseObj = null;
        try {
            if (response != null) {
                responseObj = new JSONObject(response);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(WEB_RESPONSE_CODE_200))) {
                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS) && responseObj.optBoolean(IS_DATA)) {
                            if (responseObj.has(RIDE_HISTORY)) {
                                JSONArray historyArray = responseObj.optJSONArray(RIDE_HISTORY);
                                if (historyArray != null) {
                                    for (int i = 0; i < historyArray.length(); i++) {

                                        RideHistoryInfo historyInfo = new RideHistoryInfo();
                                        JSONObject arrayObj = historyArray.getJSONObject(i);

                                        historyInfo.setRideDate(arrayObj.getString(RIDE_DATE_TIME));
                                        historyInfo.setAmount(String.format("%.2f", arrayObj.getDouble(RIDE_AMOUNT)));
                                        historyInfo.setPaymentType(PAYMENT_TYPE);
                                        historyInfo.setRideStatus(arrayObj.getString(RIDE_STATUS));
                                        historyInfo.setRideDriverPhoto(arrayObj.getString(PROFILE_PHOTO));
                                        historyInfo.setRideDriverName(arrayObj.getString(FIRST_NAME));

                                        if (arrayObj.has(VEHICLE)) {
                                            JSONObject vObj = arrayObj.optJSONObject(VEHICLE);
                                            if (vObj != null) {
                                                historyInfo.setVehicleType(vObj.getString(VEHICLE_TYPE));
                                                historyInfo.setVehicleDesc(vObj.getString(VEHICLE_BRAND) + " " + vObj.getString(VEHICLE_MODEL));
                                            }
                                        }

                                        if (arrayObj.has(PICKUP_LOCATION)) {
                                            JSONObject pickObj = arrayObj.optJSONObject(PICKUP_LOCATION);
                                            if (pickObj != null) {
                                                historyInfo.setPickupLatLng(new LatLng(pickObj.getDouble(LAT), pickObj.getDouble(LNG)));
                                            }
                                        }

                                        if (arrayObj.has(DESTINATION)) {
                                            JSONObject destObj = arrayObj.optJSONObject(DESTINATION);
                                            if (destObj != null) {
                                                historyInfo.setDropLatLng(new LatLng(destObj.getDouble(LAT), destObj.getDouble(LNG)));
                                            }
                                        }

                                        if (arrayObj.has(RIDE_RECEIPT)) {
                                            JSONObject receiptObj = arrayObj.optJSONObject(RIDE_RECEIPT);
                                            if (receiptObj != null) {
                                                historyInfo.setBaseFare(Double.valueOf(receiptObj.getString(BASE_FARE).equals("null") ? "0.0" : receiptObj.getString(BASE_FARE)));
                                                historyInfo.setDistanceFare(receiptObj.getDouble(DISTANCE_FARE));
                                                historyInfo.setTimeFare(receiptObj.getDouble(TIME_FARE));
                                            }
                                        }
                                        rideHistoryInfoList.add(historyInfo);
                                    }
                                }
                                Common.stopWaitingDialog(pDialog);
                            }
                        } else if (!responseObj.optBoolean(IS_DATA)) {
                            Common.stopWaitingDialog(pDialog);
                            tvNoRideMsg.setVisibility(View.VISIBLE);
                        } else if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            Common.stopWaitingDialog(pDialog);
                            tvNoRideMsg.setText("Something Went Wrong.");
                            tvNoRideMsg.setVisibility(View.VISIBLE);
                        }
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                        logOutHere();
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                        logOutHere();
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                        logOutHere();
                    } else if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                        logOutHere();
                    }
                } else {
                    Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    logOutHere();
                }
            } else {
                Common.stopWaitingDialog(pDialog);
                tvNoRideMsg.setText(R.string.slow_internet_connection);
                tvNoRideMsg.setVisibility(View.VISIBLE);
                // Common.snackBarAlert(getString(R.string.slow_internet_connection), llHistoryRoot, snackBarDismissListener);
            }
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            tvNoRideMsg.setText(R.string.slow_internet_connection);
            tvNoRideMsg.setVisibility(View.VISIBLE);

            // Common.snackBarAlert(getString(R.string.slow_internet_connection), llHistoryRoot, snackBarDismissListener);
            e.printStackTrace();
        }
        return response;
    }

    // body and header json
    private JSONObject getBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(USER_ID, riderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getHeaderJSON() {
        JSONObject postHeader = new JSONObject();
        try {
            postHeader.put(ACCESS_TOKENS, accessToken);
            postHeader.put(REMEMBER_TOKEN, rememberToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postHeader;
    }

    private void logOutHere() {
        AutoRideRiderApps.logout();
        Intent intent = new Intent(RideHistoryActivity.this, RiderWelcomeActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RideHistoryActivity.this.finish();
    }

    private void detailsRoute(final LatLng pickup, LatLng destination, final String rStatus) {
        try {

            if (pickupMarker != null) {
                pickupMarker.remove();
            }
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
            if (blackPolyline != null) {
                blackPolyline.remove();
            }

            mService.getPath(Common.directionsApi(pickup, destination, this)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyLine = poly.getString("points");
                            polyLineList = Common.decodePoly(polyLine);
                        }

                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        String startPoint = legsObject.getString("start_address");
                        String endPoint = legsObject.getString("end_address");

                        if (tvDRidePickup != null) {
                            tvDRidePickup.setText(startPoint);
                        }

                        if (tvDRideDest != null) {
                            tvDRideDest.setText(endPoint);
                        }

                        if (rStatus.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (LatLng latLng : polyLineList) {
                                builder.include(latLng);
                            }

                            LatLngBounds bounds = builder.build();
                            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 130);
                            riderDetailsMaps.moveCamera(mCameraUpdate);

                            PolylineOptions blackPolylineOptions = new PolylineOptions();
                            blackPolylineOptions.color(Color.parseColor("#FFA000"));
                            blackPolylineOptions.width(8);
                            blackPolylineOptions.startCap(new SquareCap());
                            blackPolylineOptions.endCap(new SquareCap());
                            blackPolylineOptions.jointType(JointType.ROUND);
                            blackPolylineOptions.addAll(polyLineList);
                            blackPolyline = riderDetailsMaps.addPolyline(blackPolylineOptions);

                            IconGenerator destIconGen = new IconGenerator(RideHistoryActivity.this);
                            int destShapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                            Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rider_destination_marker, null);
                            destIconGen.setBackground(shapeDrawable);
                            View destView = new View(RideHistoryActivity.this);
                            destView.setLayoutParams(new ViewGroup.LayoutParams(destShapeSize, destShapeSize));
                            destIconGen.setContentView(destView);
                            Bitmap destBitmap = destIconGen.makeIcon();
                            Bitmap destBitmapResized = Bitmap.createScaledBitmap(destBitmap, 25, 25, false);
                            destinationMarker = riderDetailsMaps.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(destBitmapResized)));
                        }

                        IconGenerator pickupIconGen = new IconGenerator(RideHistoryActivity.this);
                        int pickupShapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                        Drawable shapeDrawablePickup = ResourcesCompat.getDrawable(getResources(), R.drawable.rider_pickup_marker, null);
                        pickupIconGen.setBackground(shapeDrawablePickup);
                        View pickupView = new View(RideHistoryActivity.this);
                        pickupView.setLayoutParams(new ViewGroup.LayoutParams(pickupShapeSize, pickupShapeSize));
                        pickupIconGen.setContentView(pickupView);
                        Bitmap pickupBitmap = pickupIconGen.makeIcon();
                        Bitmap pickupBitmapResized = Bitmap.createScaledBitmap(pickupBitmap, 28, 28, false);
                        pickupMarker = riderDetailsMaps.addMarker(new MarkerOptions().position(pickup).icon(BitmapDescriptorFactory.fromBitmap(pickupBitmapResized)));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "Throwable " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        int index = vfRideHistory.getDisplayedChild();
        if (index == 1) {
            vfRideHistory.setInAnimation(slideRightIn);
            vfRideHistory.setOutAnimation(slideRightOut);
            vfRideHistory.showPrevious();
        } else {
            Intent intent = new Intent(RideHistoryActivity.this, RiderMainActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RideHistoryActivity.this.finish();
        }
    }
}