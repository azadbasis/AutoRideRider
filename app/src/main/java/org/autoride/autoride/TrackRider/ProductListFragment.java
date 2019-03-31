package org.autoride.autoride.TrackRider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.autoride.autoride.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.autoride.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.SESSION_SHARED_PREFERENCES;
import static org.autoride.autoride.utils.Constants.TRACK_USER_LIST_URL;


public class ProductListFragment extends ListFragment implements NetworkConnectionReciever.ConnectivityRecieverListener{

    private ListFragmentListener mListener;
    private List<Product> products = DataProvider.productList;

    Boolean isConnected;
    List<TrackingPeopleListItem> trackingPeopleListItems;
    TrackingPeopleListItem trackPeopleItem;
    ViewGroup  trackerListContainer;

    public ProductListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackingPeopleListItems = new ArrayList<>();
        trackPeopleItem = new TrackingPeopleListItem();
        getTrackingPeopleList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        getTrackingPeopleList();
        /*ProductListAdapter adapter = new ProductListAdapter(
                getContext(), R.layout.list_item, products);*/
       /* ListView lv = (ListView) view.findViewById(R.id.listView);
        lv.setAdapter(adapter);*/
      //  setListAdapter(adapter);
/*

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String uriString = "http://hplussport.com/catalog/productid/" + position;
                Uri uri = Uri.parse(uriString);
                mListener.onListItemClick(uri);
            }
        });
*/

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String uriString = "http://hplussport.com/catalog/productid/" + position;
        Uri uri = Uri.parse(uriString);
        mListener.onListItemClick(uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListFragmentListener) {
            mListener = (ListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ListFragmentListener {
        void onListItemClick(Uri uri);
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

        Snackbar.make(trackerListContainer, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();

    }
    private void trackingUserList() {
        String url = TRACK_USER_LIST_URL;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String statusCode = jsonObject.getString("statusCode");

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objectData = (JSONObject) jsonArray.get(i);

                                for (int j = 0; j < objectData.length(); j++) {


                                    trackPeopleItem.userId = objectData.getString("userId");
                                    trackPeopleItem.name = objectData.getString("name");
                                    trackPeopleItem.phone = objectData.getString("phone");
                                    trackPeopleItem.role = objectData.getString("role");
                                    trackPeopleItem.imageUrl = objectData.getString("imageUrl");
                                    trackPeopleItem.trakingStatus = objectData.getString("trakingStatus");
                                    trackPeopleItem.authStatus = objectData.getString("authStatus");
                                    JSONObject latlngObj = objectData.getJSONObject("lastLocation");


                                    for (int k = 0; k < latlngObj.length(); k++) {
                                        trackPeopleItem.lat = latlngObj.getString("lat");
                                        trackPeopleItem.lng = latlngObj.getString("lng");

                                        k = j;

                                        if (k == j) {
                                            break;
                                        }
                                    }

                                    TrackingPeopleListItem peopleListItem = new TrackingPeopleListItem(trackPeopleItem.userId,
                                            trackPeopleItem.name, trackPeopleItem.phone, trackPeopleItem.role, trackPeopleItem.imageUrl, trackPeopleItem.lat, trackPeopleItem.lng, trackPeopleItem.trakingStatus, trackPeopleItem.authStatus);
                                    trackingPeopleListItems.add(peopleListItem);
//                                    TrackPeopleAdapter adapter = new TrackPeopleAdapter(trackingPeopleListItems);
                                    ProductListAdapter adapter = new ProductListAdapter(
                                            getContext(), R.layout.custom_track_userlist, trackingPeopleListItems);
                                    setListAdapter(adapter);
                                   // recyclerView.setAdapter(adapter);

                                    j = i;

                                    if (j == i) {
                                        break;
                                    }


                                }

                                Log.d("object", objectData + "");
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


                SharedPreferences sp = getActivity().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
}
