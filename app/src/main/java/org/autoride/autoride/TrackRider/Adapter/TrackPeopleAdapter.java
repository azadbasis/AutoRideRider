package org.autoride.autoride.TrackRider.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;
import org.autoride.autoride.TrackRider.TrackMapActivity;
import org.autoride.autoride.TrackRider.TrackMapFragment;
import org.autoride.autoride.applications.AutoRideRiderApps;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by goldenReign on 7/3/2018.
 */

public class TrackPeopleAdapter extends RecyclerView.Adapter<TrackPeopleAdapter.MyViewHolder> {

    private ArrayList<TrackingPeopleListItem> trackItemsList;
    Context context;
    public Double lastLatitude;
    public Double lastLongitude;

    public TrackPeopleAdapter(Context context, ArrayList<TrackingPeopleListItem> trackItemsList) {
        this.context = context;
        this.trackItemsList = trackItemsList;


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
                .inflate(R.layout.track_otheritem, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final TrackingPeopleListItem trackItem = trackItemsList.get(position);

        final String name = trackItem.name;
        holder.textName.setText(name);

        final String role = trackItem.role;
        //  holder.textRole.setText(role);

        final String phone = trackItem.phone;
        final String strLat = trackItem.lat;
        final String strLng = trackItem.lng;
        final Double lat = Double.valueOf(strLat);
        final Double lng = Double.valueOf(strLng);
        LatLng latLng = new LatLng(lat, lng);
        String trackerPlace = getAddress(latLng);
        String address = role + "\n" + trackerPlace + "\n" + phone;
        if(trackerPlace!=null){
            holder.textRole.setText(trackerPlace + " —Now");
        }else {
            holder.textRole.setText("- - -");
        }

        final String imgUrl = trackItem.imageUrl;
        final String userId = trackItem.userId;

if(lastLatitude!=null&lastLongitude!=null &lat!=null&lng!=null){
    Location loc1 = new Location("");
    loc1.setLatitude(lastLatitude);
    loc1.setLongitude(lastLongitude);

    Location loc2 = new Location("");//23.8054712,90.3655219
    loc2.setLatitude(lat);
    loc2.setLongitude(lng);

    double distance = loc1.distanceTo(loc2) / 1000;
    holder.textDistance.setText((new DecimalFormat("##.##").format(distance)+ "km"));

}

        String trakingStatus = trackItem.trakingStatus;
        if (trakingStatus.equalsIgnoreCase("on")) {
            holder.imagetrakingStatus.setImageResource(android.R.drawable.presence_online);
        } else {
            holder.imagetrakingStatus.setImageResource(android.R.drawable.presence_offline);

        }
        final String authStatus = trackItem.authStatus;
        if (authStatus.equalsIgnoreCase("pending")) {
            holder.imageAuthStatus.setImageResource(R.drawable.ic_refresh_black_24dp);
        } else {
            holder.imageAuthStatus.setImageResource(R.drawable.ic_check_circle_black_24dp);

        }


        holder.setOnClickListener(new ItemClick() {
            @Override
            public void onItemClick(int position) {
                //  Toast.makeText(context, "name"+trackItemsList.get(position).name, Toast.LENGTH_SHORT).show();
                // trackItemsList.remove(position);


                if (authStatus.equalsIgnoreCase("pending")) {
                    // openDetailActivity(userId,name,role,imgUrl,phone);
                    holder.textRole.setVisibility(View.INVISIBLE );
                    holder.textDistance.setVisibility(View.INVISIBLE);
                    Snackbar.make(holder.media_card_view, "Approved First!", Snackbar.LENGTH_SHORT).show();
                } else {
                    openDetailActivity(userId, name, role, imgUrl, phone, strLat, strLng);
                    AutoRideRiderApps.getInstance().setImageUrlTracker(imgUrl);
                    AutoRideRiderApps.getInstance().setTrackerName(name);
                    AutoRideRiderApps.getInstance().setTrackerLat(strLat);
                    AutoRideRiderApps.getInstance().setTrackerLng(strLng);
                }


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
                        //CroppedDrawable cd = new CroppedDrawable(bitmapResized);
//                            toggle.setHomeAsUpIndicator(cd);
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView textName, textRole, textDistance;
        private ImageView imageView, imageAuthStatus, imagetrakingStatus;
        ItemClick itemClick;
        CardView media_card_view;

        public MyViewHolder(View view) {
            super(view);
            textName = (TextView) view.findViewById(R.id.contactName);
            textRole = (TextView) view.findViewById(R.id.contactNumber);
            textDistance = (TextView) view.findViewById(R.id.textDistance);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            imageAuthStatus = (ImageView) view.findViewById(R.id.imageAuthStatus);
            imagetrakingStatus = (ImageView) view.findViewById(R.id.imagetrakingStatus);
            media_card_view = (CardView) view.findViewById(R.id.media_card_view);
            view.setOnClickListener(this);
            // year = (TextView) view.findViewById(R.id.year);
        }

        public void setOnClickListener(ItemClick itemClick) {
            this.itemClick = itemClick;
        }

        @Override
        public void onClick(View view) {
            this.itemClick.onItemClick(this.getLayoutPosition());
        }
    }

    private void openDetailActivity(String userId, String name, String role, String imageUrl, String phone, String latitude, String longitude) {
        Intent intent = new Intent(context, TrackMapActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("name", name);
        intent.putExtra("role", role);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("phone", phone);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("name", name);
        args.putString("role", role);
        args.putString("imageUrl", imageUrl);
        args.putString("phone", phone);
        args.putString("latitude", latitude);
        args.putString("longitude", longitude);
        TrackMapFragment teamFragment = new TrackMapFragment();
        teamFragment.setArguments(args);

        context.startActivity(intent);
    }

    String trackerPlace;

    private String getAddress(LatLng latLng) {

        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = addressList.get(0);
            trackerPlace = address.getAddressLine(0);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return trackerPlace;
    }

    Button btnCancel, btnSubmit;
    ImageView imageTrackSetting;
    TextView textAuthPersonName;
    private RadioGroup radioAuthStatusGroupBtn;
    private RadioButton radioApprovedBtn, radioDeleteBtn;

    public void showPopup(View view, String name, String imageUrl, String authStatus) {
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

        popupWindow.showAsDropDown(popupView, 0, 0);

    }


    Location lastLocation;

    public Location getUserLastLocation() {

        if (AutoRideRiderApps.isLocationEnabled()) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lastLocation = location;
                            lastLatitude = location.getLatitude();
                            lastLongitude = location.getLongitude();
                        }
                    }
                });
            }


        }

        return lastLocation;
    }

}