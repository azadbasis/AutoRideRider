package org.autoride.autoride.TrackRider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.Adapter.TrackPeopleAdapter;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.internal.zzahn.runOnUiThread;
import static org.autoride.autoride.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.autoride.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.RIDER_ID;
import static org.autoride.autoride.constants.AppsConstants.SESSION_SHARED_PREFERENCES;
import static org.autoride.autoride.utils.Constants.TRACK_USER_LIST_URL;


public class TrackOtherFragment extends Fragment implements NetworkConnectionReciever.ConnectivityRecieverListener {

    Boolean isConnected;
    ArrayList<TrackingPeopleListItem> trackingPeopleListItems;

    ViewGroup trackerListContainer;
    TrackPeopleAdapter adapter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;


    public TrackOtherFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getTrackingPeopleList();
    }

View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.track_people_list, container, false);

        if(savedInstanceState == null){
            initUi();
        }

        return view;
    }

    private void initUi() {
        trackerListContainer = (ViewGroup) view.findViewById(R.id.trackerListContainer);
        trackingPeopleListItems = new ArrayList<>();


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new TrackPeopleAdapter(getActivity(), trackingPeopleListItems);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("TrackOther...");
        progressDialog.show();

        if (getUserVisibleHint()) { // fragment is visible
            getTrackingPeopleList();
        }

    }

    private void getTrackingPeopleList() {

        if (checkConnectivity()) {

            try {
                trackingUserList();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showSnackBar();
            progressDialog.dismiss();
        }

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

        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();

    }

    private void trackingUserList() {
        SharedPreferences sp = getActivity().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        String url = TRACK_USER_LIST_URL + riderId;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                            if (statusCode.equalsIgnoreCase("200")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject objectData = (JSONObject) jsonArray.get(i);
                                    TrackingPeopleListItem trackPeopleItem = new TrackingPeopleListItem();
                                    trackPeopleItem.userId = objectData.getString("userId");
                                    trackPeopleItem.name = objectData.getString("name");
                                    trackPeopleItem.phone = objectData.getString("phone");
                                    trackPeopleItem.role = objectData.getString("role");
                                    trackPeopleItem.imageUrl = objectData.getString("imageUrl");
                                    trackPeopleItem.trakingStatus = objectData.getString("trakingStatus");
                                    trackPeopleItem.authStatus = objectData.getString("authStatus");
                                    JSONObject latlngObj = objectData.getJSONObject("lastLocation");
                                    trackPeopleItem.lat = latlngObj.getString("lat");
                                    trackPeopleItem.lng = latlngObj.getString("lng");
                                    trackingPeopleListItems.add(trackPeopleItem);

                                }
                            } else {
                                Snackbar.make(getView(), "Slow Internet connection!", Snackbar.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();

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


                SharedPreferences sp = getActivity().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
                String rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);

                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", accessToken);
                params.put("rememberToken", rememberToken);

                return params;
            }
        };

        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(postRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        AutoRideRiderApps.getInstance().setConnectivityReciever(this);
        // final ProgressDialog loading = ProgressDialog.show(getContext(), "Fetching Data", "Please wait...", false, false);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            // getTrackingPeopleList();
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


}
