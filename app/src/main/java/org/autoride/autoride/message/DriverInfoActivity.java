package org.autoride.autoride.message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.listeners.ParserListenerDriver;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.notifications.helpers.FCMService;
import org.autoride.autoride.notifications.helpers.GoogleAPI;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DriverInfoActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener,
        AppsConstants, RiderApiUrl {

    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private TextView tvRiderRideCancelClose, tvArrivingNowTime, tvDriverFullName, tvVehicleBrand, tvDriverRating, tvVehicleDesc,
            tvRiderDestination, tvArrivalTime, tvPaymentType, tvPaymentAmount, tvDriverName, tvDriverPhone, tvRiderChangeDestination, tvTripStatus;
    private CircleImageView civAcceptedDriverPhoto;
    private ImageView ivAcceptedDriverVehicle;
    private LinearLayout llDriverInfoContainer, llDriverContact;
    private String driverPhone, riderId, updateDest;
    private ProgressDialog pDialog;
    private FCMService fcmService;
    private SharedPreferences sp;
    private CollapsingToolbarLayout collapsingToolbar;
    private String TAG = "DriverInfo";
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private LatLng updateDestLatLng;
    private DestChangeModal destChangeModal;
    private RequestCancelModal requestCancelModal;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Xerox Serif Wide.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);

        setUiComponent();

        if (getIntent() != null) {
          /*  String intentStatus = getIntent().getStringExtra("intent_status");
            if (intentStatus != null) {
                if (intentStatus.equalsIgnoreCase("request_accept")) {*/

            String dName = getIntent().getStringExtra("driver_name");
            String fare = getIntent().getStringExtra("amount");
            String tripStatus = getIntent().getStringExtra("trip_status");

            driverPhone = getIntent().getStringExtra("driver_phone");
            tvArrivingNowTime.setText(getIntent().getStringExtra("arriving_time"));
            tvDriverFullName.setText(dName);
            tvVehicleBrand.setText(getIntent().getStringExtra("v_brand"));
            tvDriverRating.setText(getIntent().getStringExtra("driver_rating"));
            tvVehicleDesc.setText(getIntent().getStringExtra("v_desc"));
            tvRiderDestination.setText(getIntent().getStringExtra("rider_destination_place"));
            tvArrivalTime.setText("11:11 am arrival");
            tvPaymentType.setText("Cash");
            tvPaymentAmount.setText(CURRENCY + fare);
            tvTripStatus.setText(tripStatus);
            tvRiderRideCancelClose.setText(getIntent().getStringExtra("cancel_close_status"));

            collapsingToolbar.setTitle(tripStatus);

            tvDriverName.setText(dName);
            tvDriverPhone.setText(driverPhone);

            Glide.with(getBaseContext())
                    .load(getIntent().getStringExtra("driver_photo"))
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()
                            .fitCenter()
                            .error(R.drawable.ic_profile_photo_default)
                            .fallback(R.drawable.ic_profile_photo_default))
                    .into(civAcceptedDriverPhoto);

            Glide.with(getBaseContext())
                    .load(getIntent().getStringExtra("vehicle_photo"))
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()
                            .fitCenter()
                            .error(R.drawable.ic_profile_photo_default)
                            .fallback(R.drawable.ic_profile_photo_default))
                    .into(ivAcceptedDriverVehicle);
        }
           /* }
        }*/
    }

    private void setUiComponent() {

        fcmService = Common.getFCMService();
        pDialog = new ProgressDialog(DriverInfoActivity.this);
        sp = getBaseContext().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_in_driver_info);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_driver_info);

        llDriverInfoContainer = (LinearLayout) findViewById(R.id.ll_driver_info_container);
        llDriverContact = (LinearLayout) findViewById(R.id.ll_driver_contact);

        civAcceptedDriverPhoto = (CircleImageView) findViewById(R.id.civ_accepted_driver_photo2);
        ivAcceptedDriverVehicle = (ImageView) findViewById(R.id.iv_accepted_driver_vehicle);
        tvRiderRideCancelClose = (TextView) findViewById(R.id.tv_rider_ride_cancel_close);
        tvArrivingNowTime = (TextView) findViewById(R.id.tv_arriving_now_time);
        tvDriverFullName = (TextView) findViewById(R.id.tv_driver_full_name2);
        tvVehicleBrand = (TextView) findViewById(R.id.tv_vehicle_brand2);
        tvDriverRating = (TextView) findViewById(R.id.tv_driver_rating2);
        tvVehicleDesc = (TextView) findViewById(R.id.tv_vehicle_desc2);
        tvRiderDestination = (TextView) findViewById(R.id.tv_rider_destination);
        tvArrivalTime = (TextView) findViewById(R.id.tv_arrival_time);
        tvPaymentType = (TextView) findViewById(R.id.tv_payment_type);
        tvPaymentAmount = (TextView) findViewById(R.id.tv_payment_amount);
        tvTripStatus = (TextView) findViewById(R.id.tv_trip_status);

        tvDriverName = (TextView) findViewById(R.id.tv_driver_name);
        tvDriverPhone = (TextView) findViewById(R.id.tv_driver_phone);
        tvRiderChangeDestination = (TextView) findViewById(R.id.tv_rider_change_destination);
        tvRiderChangeDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationChanger();
            }
        });

        SharedPreferences sp = getBaseContext().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sp != null) {
            riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        }
        /* AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar_driver_info);
        appbar.addOnOffsetChangedListener(this); */
    }

    private void destinationChanger() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(DriverInfoActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                updateDest = place.getName().toString();
                updateDestLatLng = place.getLatLng();

                destChangeModal = new DestChangeModal();
                destChangeModal.setCancelable(false);
                destChangeModal.show(getSupportFragmentManager(), TAG);

                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        /* if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }

        int currentScrollPercentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;
                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        } */
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, DriverInfoActivity.class));
    }

   /* public void onContactToDriver(View view) {
        llDriverInfoContainer.setVisibility(View.GONE);
        llDriverContact.setVisibility(View.VISIBLE);
    }*/

 /*   public void onRiderRideCancelClose(View view) {
        if (tvRiderRideCancelClose.getText().toString().equalsIgnoreCase("Cancel")) {

            requestCancelModal = new RequestCancelModal();
            requestCancelModal.setCancelable(false);
            requestCancelModal.show(getSupportFragmentManager(), TAG);

            // tripCancelDialog();
        } else if (tvRiderRideCancelClose.getText().toString().equalsIgnoreCase("Close")) {
            moveTaskToBack(true);
        }
    }*/

    /*public void onRideToDriverSms(View view) {
        smsToPhone(driverPhone);
    }

    private void smsToPhone(String pNo) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + pNo)));
    }

    public void onRiderToDriverPhoneCall(View view) {
        callToPhone(driverPhone);
    }

    private void callToPhone(String pNo) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pNo)));
    }*/

  /*  private void tripCancelDialog() {

        final Dialog dialog = new Dialog(DriverInfoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ride_cancel_dialog);

        TextView tvNo = dialog.findViewById(R.id.tv_ride_cancel_no);
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView tvYes = dialog.findViewById(R.id.tv_ride_cancel_yes);
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.startWaitingDialog(pDialog);
                dialog.dismiss();
                performRiderAcceptedRequestCancel();
            }
        });
        dialog.show();
    }*/

    /*public void onRequestCancelYes(View view) {
        performRiderAcceptedRequestCancel();
    }*/

    private void performRiderAcceptedRequestCancel() {

        String driverToken = sp.getString(TRIP_DRIVER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        Common common = new Common();
        common.notificationToDriver(fcmService, driverToken, "rider_trip_canceled", "The rider has canceled ride");

        if (requestCancelModal != null) {
            requestCancelModal.dismiss();
        }

        ManagerData.driverTaskManager(POST, RIDE_CANCEL, getBodyJSON("reason", "rider cancel ride"), null, new ParserListenerDriver() {
            @Override
            public void onLoadCompleted(String driverInfo) {
                Common.stopWaitingDialog(pDialog);
                startActivity(new Intent(DriverInfoActivity.this, RiderMainActivity.class));
                finish();
            }

            @Override
            public void onLoadFailed(String driverInfo) {
                Common.stopWaitingDialog(pDialog);
                startActivity(new Intent(DriverInfoActivity.this, RiderMainActivity.class));
                finish();
            }
        });
    }

    // body json
    private JSONObject getBodyJSON(String key, String value) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("driverId", riderId);
            if (key != null && value != null) {
                postBody.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

   /* public void onRequestCancelNo(View view) {
        if (requestCancelModal != null) {
            requestCancelModal.dismiss();
        }
    }*/

    private GoogleAPI mService;

 /*   public void onDestChangeOk(View view) {

        mService = Common.getGoogleAPI();
        //  Common.startWaitingDialog(pDialog);

        RiderMainActivity main = new RiderMainActivity();
        main.setDestinationRoute(updateDestLatLng, updateDestLatLng, mService, this);

       *//* String driverToken = sp.getString(TRIP_DRIVER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        Common common = new Common();
        common.notificationToDriver(fcmService, driverToken, "rider_destination_update", "The rider has change destination");*//*

        tvRiderDestination.setText(updateDest);

        if (destChangeModal != null) {
            destChangeModal.dismiss();
        }
    }*/

   /* public void onDestChangeCancel(View view) {
        if (destChangeModal != null) {
            destChangeModal.dismiss();
        }
    }*/

    public static class RequestCancelModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            //super.setupDialog(dialog, style);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_request_cancel, null);
            dialog.setContentView(view);
        }
    }

    public static class DestChangeModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            //super.setupDialog(dialog, style);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dest_edit_alert, null);
            dialog.setContentView(view);
        }
    }
}