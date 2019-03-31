package org.autoride.autoride.TrackRider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.suke.widget.SwitchButton;

import org.autoride.autoride.Account.SendFragment;
import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.Adapter.TrackMeAdapter;
import org.autoride.autoride.TrackRider.Model.TrackMeItem;
import org.autoride.autoride.TrackRider.Service.GPSTracker;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.autoride.autoride.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.autoride.constants.AppsConstants.FULL_NAME;
import static org.autoride.autoride.constants.AppsConstants.PROFILE_PHOTO;
import static org.autoride.autoride.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.RIDER_ID;
import static org.autoride.autoride.constants.AppsConstants.SESSION_SHARED_PREFERENCES;
import static org.autoride.autoride.utils.Constants.GLOBAL_TRACKING_STATUS_URL;
import static org.autoride.autoride.utils.Constants.SET_LOCATION_URL;
import static org.autoride.autoride.utils.Constants.TRACK_ME_LIST_URL;

public class RiderTrackingActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, NetworkConnectionReciever.ConnectivityRecieverListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Boolean isConnected;

    private static final int MENU_ITEM_LOGOUT = 1001;
    public static final String PRODUCT_ID = "PRODUCT_ID";

    private static final int DETAIL_REQUEST = 1111;
    public static final String RETURN_MESSAGE = "RETURN_MESSAGE";
    private static final String TAG = "RiderTrackingActivity";
    public static final String TRACK_MAP_FRAGMENT = "TrackMapFragment";

    private CoordinatorLayout coordinatorLayout;
    TextToSpeech tts;
    private boolean ttsInitialized;
    String globalLocationStatus;

    //    private List<Product> mProductList = DataProvider.productList;

    private boolean mIsTablet;
    private FusedLocationProviderClient mFusedLocationClient;
    Dialog globalSettingDialog;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;

    private ImageView imageRider, imageGlobalLocation;
    private TextView textRiderName;

    com.suke.widget.SwitchButton switchButton;
    TextView textGPSUpdate;
    ImageView imageGlobalGPS;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_tracking);


        initUi();


    }

    private void initUi() {
        imageRider = (ImageView) findViewById(R.id.imageRider);
        imageGlobalLocation = (ImageView) findViewById(R.id.imageGlobalLocation);

        imageGlobalLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGlobalLocationSettingDialog();

                switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                        String globalTrackingStatus;
                        if (isChecked) {

                            globalTrackingStatus = "on";
                            setGlobalTrackingStatus(globalTrackingStatus);
                            setRiderGPS();
                            textGPSUpdate.setTextColor(Color.parseColor("#FF53D264"));
                            textGPSUpdate.setText("Location update on");
                            imageGlobalGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_update));
                            AutoRideRiderApps.getInstance().setGlobalStatus(globalTrackingStatus);
                        } else {

                            globalTrackingStatus = "off";
                            imageGlobalGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_refresh));
                            textGPSUpdate.setTextColor(Color.parseColor("#DBDBDB"));
                            textGPSUpdate.setText("Location update off");
                            setGlobalTrackingStatus(globalTrackingStatus);
                            setRiderGPS();
                            AutoRideRiderApps.getInstance().setGlobalStatus(globalTrackingStatus);

                        }

                        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("switchkey", isChecked);
                        editor.commit();


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                globalSettingDialog.dismiss();
                            }
                        }, 2000);
                    }
                });


            }
        });

        tts = new TextToSpeech(getApplicationContext(), this);

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.globalsettings);
        toolbar.setOverflowIcon(drawable);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 2:
                        String speech = "To send invitation request for location tracking .please, Enter  name or number; or select; from contact";
                        saySomething(speech);
                        break;
                    case 1:
                        tts.stop();
                        break;
                    case 0:
                        tts.stop();
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        FrameLayout fragmentContainer =
                (FrameLayout) findViewById(R.id.detail_fragment_container);
        mIsTablet = (fragmentContainer != null);

        Log.i(TAG, "onCreate: mTablet=" + mIsTablet);

        if (mIsTablet) {

         /*   TrackMapFragment trackMapFragment = new TrackMapFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, trackMapFragment, TRACK_MAP_FRAGMENT)
                    .addToBackStack(null)
                    .commit();*/
            //  Toast.makeText(this, "tablet", Toast.LENGTH_SHORT).show();
        } else {

            //    Toast.makeText(this, "phone", Toast.LENGTH_SHORT).show();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Double lat = location.getLatitude();
                            Double lng = location.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            AutoRideRiderApps.getInstance().setRiderPickupLatLng(latLng);

                        } else {
                            LatLng latLng = new LatLng(23.8401959, 90.361566);
                            AutoRideRiderApps.getInstance().setRiderPickupLatLng(latLng);
                        }
                    }
                });
    }


    private void createGlobalLocationSettingDialog() {

        globalSettingDialog = new Dialog(RiderTrackingActivity.this, R.style.CustomDialog);
        globalSettingDialog.setCancelable(true);
        //set content
        //  inviteDialog.setContentView(R.layout.custom_invite_dialog);
        globalSettingDialog.setContentView(R.layout.show_protected_switch);

        switchButton = (com.suke.widget.SwitchButton) globalSettingDialog.findViewById(R.id.switch_button);
        imageGlobalGPS = (ImageView) globalSettingDialog.findViewById(R.id.imageGlobalGPS);
        textGPSUpdate = (TextView) globalSettingDialog.findViewById(R.id.textGPSUpdate);
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        boolean silent = settings.getBoolean("switchkey", false);
        switchButton.setChecked(silent);
        if (silent) {
            textGPSUpdate.setText("Location update on");
            textGPSUpdate.setTextColor(Color.parseColor("#FF53D264"));
            imageGlobalGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_update));
        } else {
            textGPSUpdate.setText("Location update off");
            imageGlobalGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_refresh));
            textGPSUpdate.setTextColor(Color.parseColor("#DBDBDB"));
        }


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(globalSettingDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.dimAmount = 0.9f;
        lp.gravity = Gravity.CENTER;
        globalSettingDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        globalSettingDialog.getWindow().setAttributes(lp);
        globalSettingDialog.show();

    }


    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Track other");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_pin_circle_black_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Track me");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add_location_black_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Add people");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_add_black_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
        TabLayout.Tab tab = tabLayout.getTabAt(2);

        SharedPreferences sp = RiderTrackingActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderPhoto = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);
        String riderFullName = sp.getString(FULL_NAME, DOUBLE_QUOTES);
        Glide.with(getBaseContext())
                .load(riderPhoto)
                .apply(new RequestOptions().optionalCircleCrop()

                )
                .into(new SimpleTarget<Drawable>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 90, 90, false);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapResized);
                        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                        roundedBitmapDrawable.setCircular(true);
//                        toolbar.setNavigationIcon(roundedBitmapDrawable);
                        imageRider.setImageDrawable(roundedBitmapDrawable);

                    }
                });
//        textRiderName.setText(riderFullName);

    }

    /**
     * Adding fragments to ViewPager
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TrackOtherFragment(), "Track other");
        adapter.addFrag(new TrackMeFragment(), "Track me");
        adapter.addFrag(new AddPeopleFragment(), "Add people");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onInit(int i) {

        if (i == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This language isn't supported", Toast.LENGTH_SHORT).show();

            } else {
                ttsInitialized = true;
            }
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
        }

    }


    @SuppressLint("NewApi")
    private void saySomething(String speech) {

        if (!ttsInitialized) {
            Toast.makeText(this, "Text to speech wasn't initialized", Toast.LENGTH_SHORT).show();
            return;
        }
        tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "azhar");

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   getMenuInflater().inflate(R.menu.menu_track, menu);

        //    menu.add(0, MENU_ITEM_LOGOUT, 1001, R.string.logout);

    /*    MenuItem item = (MenuItem) menu.findItem(R.id.action_cart);
        item.setActionView(R.layout.show_protected_switch);
        // Switch switchAB = item.getActionView().findViewById(R.id.switch_button);
        switchButton = (com.suke.widget.SwitchButton) item.getActionView().
                findViewById(R.id.switch_button);
        //switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                String globalTrackingStatus;
                if (isChecked) {
                    globalTrackingStatus = "on";
                    setGlobalTrackingStatus(globalTrackingStatus);
                    setRiderGPS();

                    AutoRideRiderApps.getInstance().setGlobalStatus(globalTrackingStatus);
                } else {
                    globalTrackingStatus = "off";
                    setGlobalTrackingStatus(globalTrackingStatus);
                    AutoRideRiderApps.getInstance().setGlobalStatus(globalTrackingStatus);
                }


            }
        });
        if (AutoRideRiderApps.getInstance().getGlobalStatus() != null) {
            if (AutoRideRiderApps.getInstance().getGlobalStatus().equalsIgnoreCase("on")) {
                switchButton.setChecked(true);
            } else {
                switchButton.setChecked(false);
            }
        }*/

        return true;
    }

    private void setGlobalTrackingStatus(String globalTrackingStatus) {


        if (checkConnectivity()) {

            try {
                calculateGlobalTrackingStatus(globalTrackingStatus);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showSnackBar();

        }
    }

    private void calculateGlobalTrackingStatus(String globalTrackingStatus) {
        SharedPreferences sp = RiderTrackingActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        String url = GLOBAL_TRACKING_STATUS_URL + riderId + "&status=" + globalTrackingStatus;
        final StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            if (statusCode.equalsIgnoreCase("200")) {
                                String status = jsonObject.getString("status");
                                String success = jsonObject.getString("success");
                                JSONObject dataObject = (JSONObject) jsonObject.get("data");
                                String globalStatus = dataObject.getString("status");
                            } else {
                                Snackbar.make(coordinatorLayout, "Slow Internet connection!", Snackbar.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                SharedPreferences sp = RiderTrackingActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

/*
        switch (id) {
            case R.id.action_settings:
                Snackbar.make(coordinatorLayout,
                        "You selected settings", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_cart:
                Snackbar.make(coordinatorLayout,
                        "You selected the Shopping Cart", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            case MENU_ITEM_LOGOUT:
                Snackbar.make(coordinatorLayout,
                        "You selected Logout", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
        }
*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DETAIL_REQUEST) {
            if (resultCode == RESULT_OK) {
                String message = data.getStringExtra(RETURN_MESSAGE);
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                        .setAction("Go to cart", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(RiderTrackingActivity.this,
                                        "Going to cart", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        }
    }


    private void setRiderGPS() {

        if (checkConnectivity()) {

            try {
                calculateGPS();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showSnackBar();

        }

    }

    private void getRiderGPS(final Double oldLatitude, final Double oldLongitude) {
        SharedPreferences sp = RiderTrackingActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        //"http://128.199.80.10/golden/app/user/set/user/tracking/location?userId=5adc77eac31240181c1a919c&lat=23.7897857&lng=90.4254355"
        String url = SET_LOCATION_URL + riderId + "&lat=" + oldLatitude + "&lng=" + oldLongitude;
        //  String url = SET_LOCATION_URL;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            if (statusCode.equalsIgnoreCase("200")) {
                                String status = jsonObject.getString("status");
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                            } else {
                                Snackbar.make(coordinatorLayout, "Slow Internet Connection!", Snackbar.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                SharedPreferences sp = RiderTrackingActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
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


    public boolean checkConnectivity() {
        return NetworkConnectionReciever.isConnected();
        // showSnackBar(isConnected);
    }

    @Override
    public void OnNetworkChange(boolean inConnected) {
        this.isConnected = inConnected;
    }


    public void showSnackBar() {

        Snackbar.make(coordinatorLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();

    }

    LocationManager locationManager;
    private android.location.LocationListener locationListener;
    Double oldLatitude, oldLongitude, newLatitude, newLongitude;

    private void calculateGPS() {


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LatLng latLng = AutoRideRiderApps.getInstance().getRiderPickupLatLng();
        oldLatitude = latLng.latitude;
        oldLongitude = latLng.longitude;
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (getApplicationContext() != null) {

                    newLatitude = location.getLatitude();
                    newLongitude = location.getLongitude();
                    // totalDistanceBefore.setText("before " + String.valueOf(totalDistance));

                    if (oldLatitude != newLatitude) {

                        oldLatitude = newLatitude;
                        oldLongitude = newLongitude;
                        getRiderGPS(oldLatitude, oldLongitude);
                        //    Toast.makeText(RiderTrackingActivity.this, "latitude"+oldLatitude+"\nlongitude"+oldLongitude, Toast.LENGTH_SHORT).show();

                    }

                    // totalDistanceAfter.setText("after " + String.valueOf(totalDistance));
                } else {
                    //  locationManager.removeUpdates(locationListener);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        long minTime = 1 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
        long minDistance = 1; // Minimum distance change for update in meters, i.e. 10 meters.

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
    }


}
