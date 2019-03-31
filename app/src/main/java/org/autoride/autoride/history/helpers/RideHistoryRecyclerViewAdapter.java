package org.autoride.autoride.history.helpers;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import org.autoride.autoride.R;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.notifications.helpers.GoogleAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryRecyclerViewAdapter extends RecyclerView.Adapter<RideHistoryRecyclerViewAdapter.RideHistoryHolder> implements AppsConstants {

    private AppCompatActivity context;
    private List<RideHistoryInfo> rideHistoryInfoList;
    private OnItemClickListener clickListener;

    public RideHistoryRecyclerViewAdapter(AppCompatActivity context, List<RideHistoryInfo> rideHistoryInfoList, OnItemClickListener clickListener) {
        this.context = context;
        this.rideHistoryInfoList = rideHistoryInfoList;
        this.clickListener = clickListener;
    }

    @Override
    public RideHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_history_item_list, parent, false);
        return new RideHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(RideHistoryHolder holder, int position) {
        if (rideHistoryInfoList != null) {
            holder.tvRideDate.setText(rideHistoryInfoList.get(position).getRideDate());
            holder.tvAmount.setText(rideHistoryInfoList.get(position).getAmount());
            holder.tvVehicle.setText(rideHistoryInfoList.get(position).getVehicleDesc());
            holder.tvPaymentType.setText(rideHistoryInfoList.get(position).getPaymentType().toUpperCase());
            String status = rideHistoryInfoList.get(position).getRideStatus();
            if (status.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {
                holder.tvRideStatus.setVisibility(View.GONE);
            } else {
                holder.tvRideStatus.setText(status);
            }
            holder.initializeMapView();
        }
    }

    @Override
    public void onViewAttachedToWindow(final RideHistoryHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RideHistoryHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return rideHistoryInfoList.size();
    }

    class RideHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {

        private static final String TAG = "HistoryHolder";
        private TextView tvRideDate, tvAmount, tvVehicle, tvPaymentType, tvRideStatus;
        private GoogleMap rhMap;
        private MapView mapViewRideHistory;
        private static final float MAP_ZOOM = 16.0f;
        private Marker pickupMarker, destinationMarker;
        private Polyline polyline;
        private GoogleAPI mService;
        private List<LatLng> polyLineList;

        RideHistoryHolder(View itemView) {
            super(itemView);
            tvRideDate = (TextView) itemView.findViewById(R.id.tv_ride_date);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_ride_amount);
            tvVehicle = (TextView) itemView.findViewById(R.id.tv_ride_vehicle_desc);
            tvPaymentType = (TextView) itemView.findViewById(R.id.tv_ride_payment_type);
            tvRideStatus = (TextView) itemView.findViewById(R.id.tv_ride_status);
            mapViewRideHistory = (MapView) itemView.findViewById(R.id.map_view_ride_history);

            mService = Common.getGoogleAPI();

            itemView.setOnClickListener(this);
        }

        void initializeMapView() {
            if (mapViewRideHistory != null) {
                mapViewRideHistory.onCreate(null);
                mapViewRideHistory.onResume();
                mapViewRideHistory.getMapAsync(this);
            }
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(view, getAdapterPosition());
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context.getApplicationContext());
            rhMap = googleMap;
            rhMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            rhMap.setMyLocationEnabled(false);
            rhMap.getUiSettings().setZoomControlsEnabled(false);
            rhMap.getUiSettings().setAllGesturesEnabled(false);
            GoogleMapOptions options = new GoogleMapOptions().liteMode(true);

            rhMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    clickListener.onClick(mapViewRideHistory, getAdapterPosition());
                }
            });

            LatLng p = (rideHistoryInfoList.get(getAdapterPosition()).getPickupLatLng());
            LatLng d = (rideHistoryInfoList.get(getAdapterPosition()).getDropLatLng());
            String status = rideHistoryInfoList.get(getAdapterPosition()).getRideStatus();

            rhMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, MAP_ZOOM));
            detailsRoute(p, d, status);
        }

        private void detailsRoute(final LatLng pickup, LatLng destination, final String rStatus) {
            try {

                if (pickupMarker != null) {
                    pickupMarker.remove();
                }
                if (destinationMarker != null) {
                    destinationMarker.remove();
                }
                if (polyline != null) {
                    polyline.remove();
                }

                mService.getPath(Common.directionsApi(pickup, destination, context)).enqueue(new Callback<String>() {
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

                            /*JSONObject object = jsonArray.getJSONObject(0);
                            JSONArray legs = object.getJSONArray("legs");
                            JSONObject legsObject = legs.getJSONObject(0);

                            String startPoint = legsObject.getString("start_address");
                            String endPoint = legsObject.getString("end_address");*/

                            if (rStatus.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }

                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 130);
                                rhMap.moveCamera(mCameraUpdate);

                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.parseColor("#FFA000"));
                                polylineOptions.width(8);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                polyline = rhMap.addPolyline(polylineOptions);

                                IconGenerator destIconGen = new IconGenerator(context);
                                int destShapeSize = context.getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                                Drawable shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.rider_destination_marker, null);
                                destIconGen.setBackground(shapeDrawable);
                                View destView = new View(context);
                                destView.setLayoutParams(new ViewGroup.LayoutParams(destShapeSize, destShapeSize));
                                destIconGen.setContentView(destView);
                                Bitmap destBitmap = destIconGen.makeIcon();
                                Bitmap destBitmapResized = Bitmap.createScaledBitmap(destBitmap, 25, 25, false);
                                destinationMarker = rhMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(destBitmapResized)));
                            }

                            IconGenerator pickupIconGen = new IconGenerator(context);
                            int pickupShapeSize = context.getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                            Drawable shapeDrawablePickup = ResourcesCompat.getDrawable(context.getResources(), R.drawable.rider_pickup_marker, null);
                            pickupIconGen.setBackground(shapeDrawablePickup);
                            View pickupView = new View(context);
                            pickupView.setLayoutParams(new ViewGroup.LayoutParams(pickupShapeSize, pickupShapeSize));
                            pickupIconGen.setContentView(pickupView);
                            Bitmap pickupBitmap = pickupIconGen.makeIcon();
                            Bitmap pickupBitmapResized = Bitmap.createScaledBitmap(pickupBitmap, 28, 28, false);
                            pickupMarker = rhMap.addMarker(new MarkerOptions().position(pickup).icon(BitmapDescriptorFactory.fromBitmap(pickupBitmapResized)));

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
    }
}