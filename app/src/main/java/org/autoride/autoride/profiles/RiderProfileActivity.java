package org.autoride.autoride.profiles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.R;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.listeners.ParserListener;
import org.autoride.autoride.constants.AppsConstants;

import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.utils.Operation;
import org.autoride.autoride.utils.ScreenUtility;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiderProfileActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        RiderApiUrl, AppsConstants,
        OnStreetViewPanoramaReadyCallback, OnMapReadyCallback {

    private static final String TAG = "RiderProfile";
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private LinearLayout mTitleContainer, llRiderPromoCode, llRiderCommission;
    private CoordinatorLayout clRiderProfile;
    private View.OnClickListener snackBarDismissListener;
    private TextView tvRTitleName, tvRProfileName, tvRiderRole, tvRiderMainBal, tvRiderUsableBal, tvRiderCommission,
            tvRPhone, tvREmail, tvRAccountNumber, tvRPromoCode, tvRAddress, tvRiderUsableDiscount, tvRTotalCommission, tvNumberOfRide, tvRiderRating;
    private CircleImageView civRiderProfilePhoto;
    private String accessToken, rememberToken, riderId;

    private GoogleMap map;
    private LatLng gps, tappedPointGPS, longPressGPS;
    private StreetViewPanoramaFragment fragmentStreetViewPanorama;

    private StreetViewPanorama myStreetViewPanorama;
    private SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
    private Marker currentMarker, tapedMarker, longPressedMarker;
    private Toolbar toolbarRiderProfile;
    private ProgressDialog pDialog;

    //fontStyle
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
        setContentView(R.layout.activity_rider_profile_t);

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    String sLat = Double.toString(lat);
                    String sLng = Double.toString(lng);
                    Operation.saveString("SLAT", sLat);
                    Operation.saveString("SLNG", sLng);
                    gps = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        });

        setUiComponent();

        SupportMapFragment mapFragmentRiderProfile = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_rider_profile);
        mapFragmentRiderProfile.getMapAsync(this);


        streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        streetViewPanoramaFragment.getView().setVisibility(View.GONE);
    }

    private void setUiComponent() {

        pDialog = new ProgressDialog(RiderProfileActivity.this);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setClassName(RiderProfileActivity.this, "org.autoride.autoride.profiles.RiderStreetViewActivity");
                startActivity(intent);
            }
        });
        clRiderProfile = (CoordinatorLayout) findViewById(R.id.cl_rider_profile);
        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        };
        toolbarRiderProfile = (Toolbar) findViewById(R.id.toolbar_main);

        llRiderCommission = (LinearLayout) findViewById(R.id.ll_rider_commission);
        llRiderPromoCode = (LinearLayout) findViewById(R.id.ll_rider_promo_code);
        mTitleContainer = (LinearLayout) findViewById(R.id.linear_layout_title);
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_profile_main);

        civRiderProfilePhoto = (CircleImageView) findViewById(R.id.civ_rider_profile_photo);

        tvNumberOfRide = (TextView) findViewById(R.id.tv_number_of_ride);
        tvRiderRating = (TextView) findViewById(R.id.tv_rider_rating);

        tvRTitleName = (TextView) findViewById(R.id.tv_rider_profile_tab_name);
        tvRProfileName = (TextView) findViewById(R.id.tv_rider_profile_name);
        tvRiderRole = (TextView) findViewById(R.id.tv_rider_role);

        tvRiderMainBal = (TextView) findViewById(R.id.tv_rider_main_balance);
        tvRiderUsableBal = (TextView) findViewById(R.id.tv_rider_usable_balance);
        tvRiderCommission = (TextView) findViewById(R.id.tv_rider_commission);

        tvRPhone = (TextView) findViewById(R.id.tv_rider_profile_phone);
        tvREmail = (TextView) findViewById(R.id.tv_rider_profile_email);
        tvRAccountNumber = (TextView) findViewById(R.id.tv_rider_account_number);
        tvRPromoCode = (TextView) findViewById(R.id.tv_rider_promo_code);
        tvRAddress = (TextView) findViewById(R.id.tv_rider_profile_address);

        tvRiderUsableDiscount = (TextView) findViewById(R.id.tv_rider_usable_discount);

        tvRTotalCommission = (TextView) findViewById(R.id.tv_rider_total_commission);
        mAppBarLayout.addOnOffsetChangedListener(this);
        changeLayoutFeature();

        startAlphaAnimation(tvRTitleName, 0, View.INVISIBLE);

        SharedPreferences sp = getBaseContext().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sp != null) {
            Log.i(TAG, "checkForSession " + sp.getAll());
            accessToken = sp.getString(ACCESS_TOKEN, "");
            rememberToken = sp.getString(REMEMBER_TOKEN, "");
            riderId = sp.getString(RIDER_ID, "");
        }
        getRiderProfile();
    }

    private void changeLayoutFeature() {
        toolbarRiderProfile.inflateMenu(R.menu.menu_main);
        toolbarRiderProfile.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case android.R.id.home:
                        onGoBack();

                    case R.id.menu_profile_streetview:

                        if (findViewById(R.id.content_profile).getVisibility() == View.VISIBLE) {
                            item.setIcon(ContextCompat.getDrawable(RiderProfileActivity.this, R.drawable.ic_user));
                            item.setTitle("street");
                            findViewById(R.id.content_profile).setVisibility(View.GONE);
                            streetViewPanoramaFragment.getView().setVisibility(View.VISIBLE);
                            return true;
                        } else {
                            item.setIcon(ContextCompat.getDrawable(RiderProfileActivity.this, R.drawable.ic_city_street_view));
                            item.setTitle("profile");
                            streetViewPanoramaFragment.getView().setVisibility(View.GONE);
                            findViewById(R.id.content_profile).setVisibility(View.VISIBLE);
                            return true;
                        }

                }
                //return super.onOptionsItemSelected(item);
                return false;
            }
        });
    }

    // get set profile info
    private void getRiderProfile() {
        Common.startWaitingDialog(pDialog);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            performGetRiderProfile();
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    private void performGetRiderProfile() {
        ManagerData.taskManager(GET, PROFILE_URL, getBodyJSON(), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                if (riderInfo != null) {
                    setRiderProfileInfo(riderInfo);
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                if (riderInfo != null) {
                    Common.stopWaitingDialog(pDialog);
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(clRiderProfile, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderProfileActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
                    } else {
                        Snackbar.make(clRiderProfile, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissListener).show();
                    }
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setRiderProfileInfo(RiderInfo riderInfo) {

        //String riderRole = "executiveUser";
        String riderRole = riderInfo.getRole();
        if (riderRole.equalsIgnoreCase("executiveUser")) {
            llRiderPromoCode.setVisibility(View.VISIBLE);

            llRiderCommission.setVisibility(View.VISIBLE);
            tvRiderCommission.setText(String.format("%.2f", Double.valueOf(riderInfo.getCommission())));

        }

        tvRTitleName.setText(riderInfo.getFullName().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getFullName());
        tvRProfileName.setText(riderInfo.getFullName().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getFullName());
        tvRiderRole.setText(riderRole.equals(NULLS) ? DOUBLE_QUOTES : riderRole);

        tvRiderMainBal.setText(String.format("%.2f", Double.valueOf(riderInfo.getMainBalance())));
        tvRiderUsableBal.setText(String.format("%.2f", Double.valueOf(riderInfo.getUsableBalance())));
        tvRiderUsableDiscount.setText(riderInfo.getUsableDiscount() + " %");

        tvNumberOfRide.setText("0");
        tvRiderRating.setText("0.0");

        tvRPhone.setText(riderInfo.getPhone().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getPhone());
        tvREmail.setText(riderInfo.getEmail().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getEmail());
        tvRAccountNumber.setText(String.format("%s%s%s", ACCOUNT_NO_START, riderInfo.getRiderAccountNo().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAccountNo(), ACCOUNT_NO_END));
        tvRPromoCode.setText(String.format("%s%s%s", PROMOTION_CODE_START, riderInfo.getPromotionCode().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getPromotionCode(), PROMOTION_CODE_END));
        tvRAddress.setText(String.format("%s%s%s%s%s%s%s", riderInfo.getRiderAddress().getHouse().equals(NULLS) ? DOUBLE_QUOTES : HOUSES + riderInfo.getRiderAddress().getHouse(),
                riderInfo.getRiderAddress().getRoad().equals(NULLS) ? DOUBLE_QUOTES : ROADS + riderInfo.getRiderAddress().getRoad() + "\n",
                riderInfo.getRiderAddress().getUnit().equals(NULLS) ? DOUBLE_QUOTES : UNITS + riderInfo.getRiderAddress().getUnit(),
                riderInfo.getRiderAddress().getZipCode().equals(NULLS) ? DOUBLE_QUOTES : ZIP_CODES + riderInfo.getRiderAddress().getZipCode() + "\n",
                riderInfo.getRiderAddress().getFax().equals(NULLS) ? DOUBLE_QUOTES : FAXES + riderInfo.getRiderAddress().getFax() + "\n",
                riderInfo.getRiderAddress().getCity().equals(NULLS) ? DOUBLE_QUOTES : CITIES + riderInfo.getRiderAddress().getCity(),
                riderInfo.getRiderAddress().getCountry().equals(NULLS) ? DOUBLE_QUOTES : COUNTRIES + riderInfo.getRiderAddress().getCountry()));

        Glide.with(getApplicationContext())
                .load(riderInfo.getProfilePhoto())
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop()
                        .fitCenter()
                        .error(R.drawable.ic_profile_photo_default)
                        .fallback(R.drawable.ic_profile_photo_default))
                .into(civRiderProfilePhoto);

        Common.stopWaitingDialog(pDialog);
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(tvRTitleName, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(tvRTitleName, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onBackPressed() {
        onGoBack();
    }

    private void onGoBack() {
        Intent intent = new Intent(RiderProfileActivity.this, RiderMainActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderProfileActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onGetStreetViewPanorama(View view) {

       /* if (mapFragmentRiderProfile.isVisible()) {
            mapFragmentRiderProfile.getView().setVisibility(View.GONE);
            fragmentStreetViewPanorama.getView().setVisibility(View.VISIBLE);
        } else {
            mapFragmentRiderProfile.getView().setVisibility(View.VISIBLE);
            fragmentStreetViewPanorama.getView().setVisibility(View.GONE);
            setUpMap();
        }*/
    }

    private void setUpMap() {
        map.setPadding(0, 0, 0, 30 + ScreenUtility.getDeviceHeight(this) / 2);
        map.setOnMyLocationChangeListener(myLocationChangeListener);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (map != null) {

                if (currentMarker != null) {
                    currentMarker.remove();
                }
                gps = new LatLng(location.getLatitude(), location.getLongitude());
                double lat = gps.latitude;
                double lng = gps.longitude;
                String myAdress[] = getGPSAddress(lat, lng);
                currentMarker = map.addMarker(new MarkerOptions()
                        .position(gps)
                        .title(myAdress[1])
                        .snippet(myAdress[0])
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                        .infoWindowAnchor(0.5f, 0.5f)
                        .draggable(true));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 14));
            }
        }
    };

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        myStreetViewPanorama = streetViewPanorama;
        setStreetView(myStreetViewPanorama, gps);

    }

    private void setStreetView(StreetViewPanorama myStreetViewPanorama, LatLng gps) {
        myStreetViewPanorama.setPosition(gps);
        myStreetViewPanorama.setUserNavigationEnabled(true);
        myStreetViewPanorama.setPanningGesturesEnabled(true);
        myStreetViewPanorama.setZoomGesturesEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        tappedPointGPS = latLng;
        double lat = tappedPointGPS.latitude;
        double lng = tappedPointGPS.longitude;
        String myAdress[] = getGPSAddress(lat, lng);
        if (map != null) {
            if (tapedMarker != null) {
                tapedMarker.remove();
            }

            tapedMarker = map.addMarker(new MarkerOptions()
                    .position(tappedPointGPS)
                    .title(myAdress[1])
                    .snippet(myAdress[0])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(tappedPointGPS, 14));
        }
        setStreetView(myStreetViewPanorama, tappedPointGPS);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        longPressGPS = latLng;

        double lat = longPressGPS.latitude;
        double lng = longPressGPS.longitude;
        String myAdress[] = getGPSAddress(lat, lng);


        if (longPressedMarker != null) {
            longPressedMarker.remove();
        }


        longPressedMarker = map.addMarker(new MarkerOptions().position(longPressGPS)
                .title(myAdress[1])
                .snippet(myAdress[0])
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(longPressGPS, 14));
        setStreetView(myStreetViewPanorama, longPressGPS);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setUpMap();
        //setSelectedStyle();
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 14));

    }

    public String city, state, country, postalCode, knownName, address;

    private String[] getGPSAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String myAdress[] = new String[6];
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            myAdress[0] = address;
            myAdress[1] = city;
            myAdress[2] = state;
            myAdress[3] = country;
            myAdress[4] = postalCode;
            myAdress[5] = knownName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myAdress;

    }

    private final Random mRandom = new Random();

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(tapedMarker)) {
            // This causes the marker at Perth to bounce into position when it is clicked.
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.max(
                            1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + 2 * t);

                    if (t > 0.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        } else if (marker.equals(longPressedMarker)) {
            // This causes the marker at Adelaide to change color and alpha.
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360));
            marker.setAlpha(mRandom.nextFloat());
        }
/*
        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);
        Toast.makeText(this, marker.getTitle() + " z-index set to " + zIndex,
                Toast.LENGTH_SHORT).show();

        mLastSelectedMarker = marker;*/
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void setSelectedStyle() {
        MapStyleOptions style;
        // style= MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
        style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_grayscale);
        map.setMapStyle(style);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        // remove the following flag for version < API 19
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    private void snackBarNoInternet() {
        Snackbar.make(clRiderProfile, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
    }

    private void snackBarSlowInternet() {
        Snackbar.make(clRiderProfile, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
    }

    private void logOutHere() {
        AutoRideRiderApps.logout();
        Intent intent = new Intent(RiderProfileActivity.this, RiderWelcomeActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderProfileActivity.this.finish();
    }
}