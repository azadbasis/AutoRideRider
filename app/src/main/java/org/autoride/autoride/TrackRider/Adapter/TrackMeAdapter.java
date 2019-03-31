package org.autoride.autoride.TrackRider.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.suke.widget.SwitchButton;

import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.DetailActivity;
import org.autoride.autoride.TrackRider.Model.TrackMeItem;
import org.autoride.autoride.TrackRider.RiderTrackingActivity;
import org.autoride.autoride.TrackRider.TrackMapActivity;
import org.autoride.autoride.TrackRider.TrackMeFragment;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.utils.Operation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static org.autoride.autoride.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.autoride.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.RIDER_ID;
import static org.autoride.autoride.constants.AppsConstants.SESSION_SHARED_PREFERENCES;
import static org.autoride.autoride.utils.Constants.LOCAL_TRACKING_STATUS_URL;
import static org.autoride.autoride.utils.Constants.TRACK_ME_LIST_URL;
import static org.autoride.autoride.utils.Constants.USER_ACTION_SELF_TRACKING_URL;

/**
 * Created by goldenReign on 7/3/2018.
 */

public class TrackMeAdapter extends RecyclerView.Adapter<TrackMeAdapter.MyViewHolder> {

    private ArrayList<TrackMeItem> trackItemsList;
    Context context;
    ProgressDialog progressDialog;
    Boolean isSuccessAction;
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;
    private CallBack mCallBack;
    public Double lastLatitude;
    public Double lastLongitude;
    public TrackMeAdapter(CallBack callback) {
        mCallBack = callback;
    }

    public TrackMeAdapter(Context context, ArrayList<TrackMeItem> trackItemsList, CallBack callback) {
        this.context = context;
        this.trackItemsList = trackItemsList;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("TrackMeAdapter...");
        mCallBack = callback;


        if (AutoRideRiderApps.isLocationEnabled()) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lastLatitude = location.getLatitude();
                        lastLongitude = location.getLongitude();
                    }
                }
            });

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_meitem, parent, false);

        return new MyViewHolder(itemView);
    }

    String trakingStatus;

    PopupMenu popup;

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final TrackMeItem trackItem = trackItemsList.get(position);
        holder.textName.setText(trackItem.name);

        String phone = trackItem.phone;
        String role = trackItem.role;
        holder.textRole.setText(role);
        final String trackerId = trackItem.trackerId;
        trakingStatus = trackItem.trakingStatus;

        if (trakingStatus.equalsIgnoreCase("on")) {
            holder.switchButton.setChecked(true);
        } else {
            holder.switchButton.setChecked(false);
        }

        final String authStatus = trackItem.authStatus;
        if (authStatus.equalsIgnoreCase("pending")) {
            holder.textAuthStatus.setVisibility(View.GONE);
            holder.switchButton.setVisibility(View.INVISIBLE);
        } else {
            holder.textAuthStatus.setVisibility(View.VISIBLE);
            holder.switchButton.setVisibility(View.VISIBLE);
            holder.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                    if (isChecked) {
                        trakingStatus = "on";
                        setLocalTrackingStatus(trakingStatus, trackerId);
                    } else {
                        trakingStatus = "off";
                        setLocalTrackingStatus(trakingStatus, trackerId);
                    }

                }
            });
        }

    /*    if (authStatus.equalsIgnoreCase("pending")) {
            holder.switchButton.setEnableEffect(false);
            holder.switchButton.setEnabled(false);
            Snackbar.make(holder.switchButton,"Approved First",Snackbar.LENGTH_LONG).show();
        } else {
            holder.switchButton.setEnableEffect(true);*/

        /*}*/


        String imgUrl = trackItem.imageUrl;
        holder.imageAuthStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageUrl = trackItem.imageUrl;
                String name = trackItem.name;
                String authStatus = trackItem.authStatus;
                popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.pop_up_menu, popup.getMenu());


                if (authStatus.equalsIgnoreCase("approved")) {
                    popup.getMenu().findItem(R.id.riderActionApproved).setVisible(false);
                    popup.getMenu().findItem(R.id.riderActionPending).setVisible(true);
                }

                if (authStatus.equalsIgnoreCase("pending")) {
                    popup.getMenu().findItem(R.id.riderActionPending).setVisible(false);
                    popup.getMenu().findItem(R.id.riderActionApproved).setVisible(true);
                }


                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        TrackMeItem itemTracker = trackItemsList.get(position);
                        String trackerId = itemTracker.trackerId;
                        String authStatus = itemTracker.authStatus;

                        if (id == R.id.riderActionApproved) {
                            String action = "approved";
                            progressDialog.show();
                            trackerAction(trackerId, action);

                            //    holder.textAuthStatus.setVisibility(View.VISIBLE);


                        }

                        if (id == R.id.riderActionPending) {
                            String action = "pending";
                            progressDialog.show();
                            trackerAction(trackerId, action);

                            // holder.textAuthStatus.setVisibility(View.GONE);

                        }
                        if (id == R.id.deleteFromWishList) {

                            TrackMeItem itemLabel = trackItemsList.get(position);
                            trackItemsList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, trackItemsList.size());
                            Toast.makeText(context, "Removed : " + itemLabel, Toast.LENGTH_SHORT).show();
                            String action = "delete";
                            progressDialog.show();
                            trackerAction(trackerId, action);

                        }
                        return true;
                    }
                });


            }
        });
        Glide.with(context)
                .load(imgUrl)
                .apply(new RequestOptions().optionalCircleCrop()

                )
                .into(new SimpleTarget<Drawable>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 150, 110, false);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmapResized);
                        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                        roundedBitmapDrawable.setCircular(true);
                        holder.imageView.setImageDrawable(roundedBitmapDrawable);

                    }
                });


    }


    @Override
    public int getItemCount() {
        return trackItemsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    CardView media_card_view;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textName, year, tvurl, textRole, textAuthStatus;
        private ImageView imageView, imageAuthStatus, imagetrakingStatus;
        ItemClick itemClick;
        com.suke.widget.SwitchButton switchButton;

        public MyViewHolder(View view) {
            super(view);
            textName = (TextView) view.findViewById(R.id.contactName);
            textRole = (TextView) view.findViewById(R.id.contactNumber);
            textAuthStatus = (TextView) view.findViewById(R.id.textAuthStatus);
            imageView = (ImageView) view.findViewById(R.id.imageView);

            imageAuthStatus = (ImageView) view.findViewById(R.id.imageRiderAction);
            imagetrakingStatus = (ImageView) view.findViewById(R.id.imageTrackStatus);
            switchButton = (com.suke.widget.SwitchButton) view.findViewById(R.id.switch_button);
            media_card_view = (CardView) view.findViewById(R.id.media_card_view);
        }

        public void setOnClickListener(ItemClick itemClick) {
            this.itemClick = itemClick;
        }

        @Override
        public void onClick(View view) {
            this.itemClick.onItemClick(this.getLayoutPosition());
        }
    }


    Button btnCancel, btnSubmit;
    ImageView imageTrackSetting;
    TextView textAuthPersonName;
    private RadioGroup radioAuthStatusGroupBtn;
    private RadioButton radioApprovedBtn, radioDeleteBtn;

    @SuppressLint("NewApi")
    public void showPopup(View view, String name, String imageUrl, String authStatus, final int position) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_window, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        btnCancel = (Button) popupView.findViewById(R.id.btnCancel);
        btnSubmit = (Button) popupView.findViewById(R.id.btnSubmit);
        radioAuthStatusGroupBtn = (RadioGroup) popupView.findViewById(R.id.radioAuthStatusGroupBtn);
        radioApprovedBtn = (RadioButton) popupView.findViewById(R.id.radioApprovedBtn);
        radioDeleteBtn = (RadioButton) popupView.findViewById(R.id.radioDeleteBtn);
        if (authStatus.equalsIgnoreCase("pending")) {
            radioApprovedBtn.setText("Approved");
        } else {
            radioApprovedBtn.setText("Pending");
        }

        textAuthPersonName = (TextView) popupView.findViewById(R.id.textAuthPersonName);
        textAuthPersonName.setText(name);
        imageTrackSetting = (ImageView) popupView.findViewById(R.id.imageTrackSetting);
        //  imageTrackSetting.setImageResource(R.drawable.ic_person);
        if (!imageUrl.equalsIgnoreCase("http://128.199.80.10/golden/image")) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().optionalCircleCrop()

                    )
                    .into(new SimpleTarget<Drawable>() {
                        @SuppressLint("NewApi")
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 150, 110, false);

                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmapResized);
                            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                            roundedBitmapDrawable.setCircular(true);
                            imageTrackSetting.setImageDrawable(roundedBitmapDrawable);

                        }
                    });
        } else {
            imageTrackSetting.setImageResource(R.drawable.ic_person);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* TrackMeItem itemTracker = trackItemsList.get(position);
                String trackerId=itemTracker.trackerId;
                approvedTracker(trackerId);*/

          /*      if (radioDeleteBtn.getText().toString().equalsIgnoreCase("Delete")) {

                    TrackMeItem itemLabel = trackItemsList.get(position);
                    trackItemsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, trackItemsList.size( ));
                    Toast.makeText(context, "Removed : " + itemLabel, Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }*/

            }
        });
        popupWindow.showAsDropDown(popupView, Gravity.CENTER, 0, 0);
    }

    private void trackerAction(String trackerId, String action) {

        SharedPreferences sp = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);


        String url = USER_ACTION_SELF_TRACKING_URL + action + "&userId=" + riderId + "&trackerId=" + trackerId+"&lat=" + lastLatitude + "&lng=" + lastLongitude;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            progressDialog.dismiss();
                            String statusCode = jsonObject.getString("statusCode");
                            String status = jsonObject.getString("status");
                            String success = jsonObject.getString("success");
                            String successMessage = jsonObject.getString("message");
/*
                         context.startActivity(new Intent(context, RiderTrackingActivity.class));
                            android.support.v4.app.Fragment newFragment = new TrackMeFragment();
                            android.support.v4.app.FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();

                            ft.sendorder(data.getorder_id());
                            ft.add(R.id.framelay, newFragment).commit();*/
                            TrackMeAdapter adapter = new TrackMeAdapter(mCallBack);
                            mCallBack.yourMethodName();

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
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                SharedPreferences sp = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
                String rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);

                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", accessToken);
                params.put("rememberToken", rememberToken);

                return params;
            }
        };
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(postRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    private void setLocalTrackingStatus(String trakingStatus, String trackerId) {

        SharedPreferences sp = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);



        String url = LOCAL_TRACKING_STATUS_URL + riderId + "&trackerId=" + trackerId + "&status=" + trakingStatus;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            String status = jsonObject.getString("status");
                            JSONObject objectData = (JSONObject) jsonObject.get("data");
                            String trackingStatus = objectData.getString("trakingStatus");

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


                SharedPreferences sp = context.getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
                String rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);

                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", accessToken);
                params.put("rememberToken", rememberToken);

                return params;
            }
        };
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(postRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }




    public interface CallBack {
        void yourMethodName();

    }
}