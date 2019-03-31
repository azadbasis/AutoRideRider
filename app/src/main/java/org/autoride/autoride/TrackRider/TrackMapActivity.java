package org.autoride.autoride.TrackRider;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.ui.IconGenerator;

import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.TrackRider.Adapter.TrackPeopleAdapter;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.listeners.ParserListener;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.notifications.helpers.GoogleAPI;
import org.autoride.autoride.utils.CustomInfoWindow;
import org.autoride.autoride.utils.Operation;
import org.autoride.autoride.utils.ScreenUtility;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static org.autoride.autoride.utils.Constants.TRACK_LOCATION_URL;
import static org.autoride.autoride.utils.Constants.TRACK_USER_LIST_URL;

public class TrackMapActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener,
        GoogleMap.OnMarkerClickListener,
        RiderApiUrl,
        AppsConstants,
        OnMapReadyCallback,
        NetworkConnectionReciever.ConnectivityRecieverListener {

    private static final String TAG = "RiderProfile";
    private GoogleAPI mService;
    private GoogleMap map;
    private LatLng gps;
    private StreetViewPanoramaFragment fragmentStreetViewPanorama;

    private SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
    private Marker currentMarker, tapedMarker, longPressedMarker, trackPersonMarker;
    private static final int ANIMATION_TIME_PER_ROUTE = 4000;

    private String trackerUserId;
    private String name;
    private String role;
    private String imageUrl;
    private String phone;
    private String latitude;
    private String longitude;
    ViewGroup containerTrackLocation;
    Boolean isConnected;
    RoundedBitmapDrawable roundedBitmapDrawable, roundedBitmapDrawableRider;

    private ImageView imageTrackingPerson;
    private TextView textTrackingAddress, textTrackedName;
    ProgressDialog pDialog;
    String lng, lat;
    private List<LatLng> polyLineList;
    private double riderDestinationDistance, riderDestinationDuration;
    private Polyline blackPolyline, backgroundPolyline, foregroundPolyline;
    private ValueAnimator polyLineAnimator;

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

    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_track_map);
        containerTrackLocation = (ViewGroup) findViewById(R.id.containerTrackLocation);
        mService = Common.getGoogleAPI();
        pDialog = new ProgressDialog(this);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        getTrackingLocation();

    }

    private void setUiComponent() {

        textTrackingAddress = (TextView) findViewById(R.id.textTrackingAddress);
        textTrackedName = (TextView) findViewById(R.id.textTrackedName);
        imageTrackingPerson = (ImageView) findViewById(R.id.imageTrackingPerson);

        trackerUserId = this.getIntent().getExtras().getString("userId");
        name = this.getIntent().getExtras().getString("name");
        role = this.getIntent().getExtras().getString("role");
        imageUrl = this.getIntent().getExtras().getString("imageUrl");
        phone = this.getIntent().getExtras().getString("phone");
        latitude = this.getIntent().getExtras().getString("latitude");
        longitude = this.getIntent().getExtras().getString("longitude");
        polyLineList = new ArrayList<>();
        textTrackedName.setText(name);

        String trackerAddress = getAddress(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)));
   /*     new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTrackPersonMarker(Double.valueOf(latitude), Double.valueOf(longitude));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)), 14));
            }
        }, 2000);
*/
        textTrackingAddress.setText(trackerAddress);


        if (!imageUrl.equalsIgnoreCase("http://128.199.80.10/golden/image")) {
            imageTrackingPerson(imageUrl);
            roundedBitmapDrawable = getDrawable(imageUrl);
        } else {
            imageUrl = "https://cdn4.iconfinder.com/data/icons/map-navigation-3/512/8-512.png";
            imageTrackingPerson(imageUrl);
            roundedBitmapDrawable = getDrawable(imageUrl);
        }

        // getTrackingLocation();

    }

    private void imageTrackingPerson(String imageUrl) {
        Glide.with(getApplicationContext())
                .load(imageUrl)
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
                        imageTrackingPerson.setImageDrawable(roundedBitmapDrawable);
                        //CroppedDrawable cd = new CroppedDrawable(bitmapResized);
//                            toggle.setHomeAsUpIndicator(cd);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private void setUpMap() {

        map.setOnMyLocationChangeListener(myLocationChangeListener);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;

        }
        map.setMyLocationEnabled(true);

    }

    Bitmap bitmapResized;

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
                final String myAdress[] = getGPSAddress(lat, lng);

                currentMarker = map.addMarker(new MarkerOptions()
                        .position(gps)
                        .title(myAdress[1])
                        .snippet(myAdress[0])
//                        .icon(BitmapDescriptorFactory.fromBitmap(getDrawableRider().getBitmap()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                        .infoWindowAnchor(0.5f, 0.5f)
                        .draggable(true));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 16));
            }


        }
    };


    private RoundedBitmapDrawable getDrawable(String imageUrl) {
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .apply(new RequestOptions().optionalCircleCrop()

                )
                .into(new SimpleTarget<Drawable>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                        bitmapResized = Bitmap.createScaledBitmap(bitmap, 90, 90, false);
                        Drawable d = new BitmapDrawable(getResources(), bitmapResized);
                        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapResized);
                        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                        roundedBitmapDrawable.setCircular(true);

                    }
                });

        return roundedBitmapDrawable;
    }

    private RoundedBitmapDrawable getDrawableRider() {
        SharedPreferences sp = TrackMapActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderPhoto = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);

        Glide.with(getApplicationContext())
                .load(riderPhoto)
                .apply(new RequestOptions().optionalCircleCrop()

                )
                .into(new SimpleTarget<Drawable>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                        bitmapResized = Bitmap.createScaledBitmap(bitmap, 90, 90, false);
                        Drawable d = new BitmapDrawable(getResources(), bitmapResized);
                        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapResized);
                        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                        roundedBitmapDrawable.setCircular(true);

                    }
                });

        return roundedBitmapDrawable;
    }


    private Bitmap createUserBitmap() {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            Bitmap bitmap = getDrawableRider().getBitmap();
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        setUpMap();

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

        return false;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    private void getTrackingLocation() {

        if (checkConnectivity()) {

            try {
                trackingLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showSnackBar();
        }

    }

    private void trackingLocation() {
//5adc77eac31240181c1a919c&trackerId=5b31e7d1c3124067240a6dc1
        SharedPreferences sp = TrackMapActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        String url = TRACK_LOCATION_URL + riderId + "&trackerId=" + trackerUserId;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            if(statusCode.equalsIgnoreCase("200")){
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                for (int i = 0; i < jsonObjectData.length(); i++) {
                                    String fireBaseToken = jsonObjectData.getString("fireBaseToken");
                                    lng = jsonObjectData.getString("lng");
                                    lat = jsonObjectData.getString("lat");
                                    String trackerAddress = getAddress(new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                                    if(lng!=null&lat!=null){

                                        setTrackPersonMarker(Double.valueOf(lat), Double.valueOf(lng));
                                    }
                                    Log.d("tag", fireBaseToken + "\n" + lng + "\n" + lat);
                                }
                            }else {
                                Snackbar.make(containerTrackLocation,"Slow Internet Connection!",Snackbar.LENGTH_LONG).show();
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


                SharedPreferences sp = TrackMapActivity.this.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
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

    private void setTrackPersonMarker(Double lat, Double lng) {
        if (trackPersonMarker != null) {
            trackPersonMarker.remove();
        }
        if (currentMarker != null) {
            currentMarker.remove();
        }
        LatLng latLng = new LatLng(lat, lng);

        Bitmap bitmap = createUserBitmap();

        trackPersonMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .anchor(0.5f, 0.5f)
                .draggable(true));


        String url = getUrl(gps, latLng);
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

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

        Snackbar.make(containerTrackLocation, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();

    }

    String trackerPlace;

    private String getAddress(LatLng latLng) {

        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = addressList.get(0);
            trackerPlace = address.getAddressLine(0);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return trackerPlace;
    }


    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(getResources().getColor(R.color.colorPrimaryDark));

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                map.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


}