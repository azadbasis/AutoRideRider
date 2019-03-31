package org.autoride.autoride.TrackRider;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.autoride.autoride.R;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackMapFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener,
        GoogleMap.OnMarkerClickListener,
        RiderApiUrl,
        AppsConstants,
        OnMapReadyCallback,
        NetworkConnectionReciever.ConnectivityRecieverListener {

    private GoogleMap map;
    private Marker currentMarker, trackPersonMarker;
    LatLng gps;
    public String city, state, country, postalCode, knownName, address;
    RoundedBitmapDrawable roundedBitmapDrawable;
    String name, role, imageUrl, latitude, longitude;
    Bitmap bitmapResized;
    public TrackMapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapWhere);
        mapFragment.getMapAsync(this);


        name = AutoRideRiderApps.getInstance().getTrackerName();
        imageUrl = AutoRideRiderApps.getInstance().getImageUrlTracker();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        setUpMap();

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
                final String myAdress[] = getGPSAddress(lat, lng);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentMarker = map.addMarker(new MarkerOptions()
                                .position(gps)
                                .title(myAdress[1])
                                .snippet(myAdress[0])
                                .icon(BitmapDescriptorFactory.fromBitmap(getDrawableRider().getBitmap()))
                                .anchor(0.5f, 0.5f)
                                .draggable(true));
                    }
                }, 5000);
                // latitude = TrackMapFragment.this.getIntent().getExtras().getString("latitude");
                // longitude = TrackMapFragment.this.getIntent().getExtras().getString("longitude");
                LatLng trackerLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));


                String url = getUrl(gps, trackerLatLng);
                Log.d("onMapClick", url.toString());
                TrackMapFragment.FetchUrl FetchUrl = new TrackMapFragment.FetchUrl();

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
                //move map camera
                map.moveCamera(CameraUpdateFactory.newLatLng(trackerLatLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(16));

            }
        }
    };

    private void setUpMap() {

        map.setOnMyLocationChangeListener(myLocationChangeListener);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;

        }
        map.setMyLocationEnabled(true);

    }

    private RoundedBitmapDrawable getDrawableRider() {
        SharedPreferences sp = getActivity().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderPhoto = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);

        Glide.with(getActivity())
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void OnNetworkChange(boolean inConnected) {

    }

    private String[] getGPSAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

            TrackMapFragment.ParserTask parserTask = new TrackMapFragment.ParserTask();

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