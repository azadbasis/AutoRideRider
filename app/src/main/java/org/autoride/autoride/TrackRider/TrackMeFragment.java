package org.autoride.autoride.TrackRider;


import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.Adapter.TrackMeAdapter;
import org.autoride.autoride.TrackRider.Adapter.TrackPeopleAdapter;
import org.autoride.autoride.TrackRider.Model.TrackMeItem;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.autoride.autoride.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.autoride.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.RIDER_ID;
import static org.autoride.autoride.constants.AppsConstants.SESSION_SHARED_PREFERENCES;
import static org.autoride.autoride.utils.Constants.TRACK_ME_LIST_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackMeFragment extends Fragment implements NetworkConnectionReciever.ConnectivityRecieverListener, TrackMeAdapter.CallBack {


    Boolean isConnected;
    ArrayList<TrackMeItem> trackMeItems;

    ViewGroup trackerListContainer;
    RecyclerView recyclerView;


    public TrackMeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  getTrackMePeopleList();
    }


    public static TrackOtherFragment newInstance() {

        Bundle args = new Bundle();

        TrackOtherFragment fragment = new TrackOtherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    TrackMeAdapter adapter;
    ProgressDialog progressDialog;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_track_others, container, false);

        if(savedInstanceState == null){
            initUi();
        }

        return view;
    }

    private void initUi() {
        trackerListContainer = (ViewGroup) view.findViewById(R.id.trackerListContainer);

        trackMeItems = new ArrayList<>();

        adapter = new TrackMeAdapter(getActivity(), trackMeItems,this);
        // progressDialog = new ProgressDialog(getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(adapter);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("TrackMe...");
        progressDialog.show();

        getTrackMePeopleList();
    }

    public void getTrackMePeopleList() {

        if (checkConnectivity()) {

            try {
                trackingMeList();

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

    private void trackingMeList() {

        SharedPreferences sp = getActivity().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        String url = TRACK_ME_LIST_URL + riderId;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");
                           if(statusCode.equalsIgnoreCase("200")){
                               JSONArray jsonArray = jsonObject.getJSONArray("data");
                               for (int i = 0; i < jsonArray.length(); i++) {
                                   JSONObject objectData = (JSONObject) jsonArray.get(i);
                                   TrackMeItem trackMeItem = new TrackMeItem();
                                   trackMeItem.trackerId = objectData.getString("trackerId");
                                   trackMeItem.name = objectData.getString("name");
                                   trackMeItem.phone = objectData.getString("phone");
                                   trackMeItem.role = objectData.getString("role");
                                   trackMeItem.imageUrl = objectData.getString("imageUrl");
                                   trackMeItem.trakingStatus = objectData.getString("trakingStatus");
                                   trackMeItem.authStatus = objectData.getString("authStatus");

                                   trackMeItems.add(trackMeItem);


                               }
                           }else {
                               Snackbar.make(getView(), "Slow Internet connection!", Snackbar.LENGTH_SHORT).show();
                               progressDialog.dismiss();
                           }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null){
            progressDialog.dismiss();
        }

}
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }


    }

    @Override
    public void yourMethodName() {

        setUserVisibleHint(true);

    }
}
