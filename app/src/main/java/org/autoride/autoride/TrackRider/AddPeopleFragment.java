package org.autoride.autoride.TrackRider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.autoride.autoride.R;
import org.autoride.autoride.applications.AutoRideRiderApps;

import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.notifications.helpers.rider.RiderFCMService;
import org.autoride.autoride.utils.Constants;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressImageButton;

import static org.autoride.autoride.constants.AppsConstants.ACCESS_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.DOUBLE_QUOTES;
import static org.autoride.autoride.constants.AppsConstants.FIRST_NAME;
import static org.autoride.autoride.constants.AppsConstants.FULL_NAME;
import static org.autoride.autoride.constants.AppsConstants.PHONE;
import static org.autoride.autoride.constants.AppsConstants.PROFILE_PHOTO;
import static org.autoride.autoride.constants.AppsConstants.PROMOTION_CODE;
import static org.autoride.autoride.constants.AppsConstants.REMEMBER_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.RIDER_FIRE_BASE_TOKEN;
import static org.autoride.autoride.constants.AppsConstants.RIDER_ID;
import static org.autoride.autoride.constants.AppsConstants.ROLE;
import static org.autoride.autoride.constants.AppsConstants.SESSION_SHARED_PREFERENCES;


public class AddPeopleFragment extends Fragment implements NetworkConnectionReciever.ConnectivityRecieverListener {

    EditText searchEt;
    ListView contactLv;
    CustomAdapter customAdapter;
    private ProgressDialog mProgressDialog;
    Context context;
    ViewGroup inviteUserContainer;
    Dialog inviteDialog;

    CircularProgressImageButton inviteActionButton;
    FloatingActionButton revertActionButton;
    TextView textStatus, textName, textPhone;
    ImageView imageAvator;
    Boolean isConnected;
    public static final String NA = "NA";
    CircularProgressImageButton buttonSendInvitation;
    private FusedLocationProviderClient mFusedLocationClient;
    private String name, number;

    public AddPeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private RiderFCMService riderFCMService;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        riderFCMService = Common.getRiderFCMService();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);

        contactLv = (ListView) rootView.findViewById(R.id.contactLv);
        contactLv.setTextFilterEnabled(true);
        searchEt = (EditText) rootView.findViewById(R.id.searchEt);
        buttonSendInvitation = (CircularProgressImageButton) rootView.findViewById(R.id.buttonSendInvitation);
        inviteUserContainer = (ViewGroup) rootView.findViewById(R.id.inviteUserContainer);
        buttonSendInvitation.setVisibility(View.GONE);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("addPeople........");
        mProgressDialog.show();
        getAllContactsM();

        contactLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                name = getItem(position).getContactName();
                number = getItem(position).getContactPhoneNumber();
                String n1 = PhoneNumberUtils.stripSeparators(number);
                searchEt.setText(n1);
                if (searchEt.getText().length() > 0) {
                    buttonSendInvitation.setVisibility(View.VISIBLE);
                    buttonSendInvitation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            animateAndDoneFastInvite(buttonSendInvitation);
                        }
                    });
                } else {
                    buttonSendInvitation.setVisibility(View.GONE);
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                filterConatctBook();
            }
        }, 2000);


        return rootView;
    }

    private void filterConatctBook() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                buttonSendInvitation.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                System.out.println("Text [" + s + "]- Start [" + start + "] - Before [" + before + "] - Count [" + count + "]");
                if (count < before) {
                    // We're deleting char so we need to reset the adapter data
                    customAdapter.resetData();

                }
                customAdapter.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonSendInvitation.setVisibility(View.VISIBLE);

                buttonSendInvitation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        animateAndDoneFastInvite(buttonSendInvitation);


                    }
                });
            }

        });
    }

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };




    private void submitInvitation() {

        createInviteDialog();
        if (inviteDialog.isShowing()) {
            inviteDialog.hide();
            showAppUser();
        }

        revertActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteDialog.dismiss();

            }
        });
        inviteActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animateAndDoneFast(inviteActionButton);

            }
        });

    }

    private void sendInvitation() {
        if (textStatus.getText().toString().equalsIgnoreCase("Request!")) {
            //request
            SharedPreferences sp = getActivity().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String riderPhoto = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);
            String riderFullName = sp.getString(FULL_NAME, DOUBLE_QUOTES);
            String riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);

            String phone = sp.getString(PHONE, DOUBLE_QUOTES);
            String fName = sp.getString(FIRST_NAME, DOUBLE_QUOTES);

            LatLng latLng = AutoRideRiderApps.getInstance().getRiderPickupLatLng();
            String latitude = String.valueOf(latLng.latitude);
            String longitude = String.valueOf(latLng.longitude);
            String address = getAddress(latLng);
            String Strlatlng = latLng.toString();
            String title = fName + " " + "share location";
            Common c = new Common(riderFCMService, title, "would you share your location with", riderToken, AutoRideRiderApps.getInstance().getTrackerFireBaseToken(), riderPhoto, riderFullName, fName, phone, address, AutoRideRiderApps.getInstance().getRiderUserId(), AutoRideRiderApps.getInstance().getTrackerUserId(), latitude, longitude);
            c.invitationNotification();
            inviteDialog.dismiss();


        } else {
            SharedPreferences sp = getActivity().getBaseContext().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);

            SmsManager sms = SmsManager.getDefault();
            String number = textPhone.getText().toString();
            String message = "'Autoride' is a mindless helper that fills any kind of guilty for emergency support. Go on";

            String riderRole = sp.getString(ROLE, DOUBLE_QUOTES);
            String promotionCode = sp.getString(PROMOTION_CODE, DOUBLE_QUOTES);
            if (riderRole.equalsIgnoreCase("executiveUser")) {
                // message += "\nhttps://goo.gl/F7Mxtu";
                message += "\nhttps://goo.gl/9Sog4M";
                message += "\nplease,use this promoCode:" + promotionCode;

                sms.sendTextMessage(number, null, message, null, null);
                Toast.makeText(getActivity(), "sms send successfully " + number, Toast.LENGTH_SHORT).show();
                inviteDialog.dismiss();
            } else {
                // message += "\nhttps://goo.gl/F7Mxtu";
                message += "\nhttps://goo.gl/9Sog4M";
                sms.sendTextMessage(number, null, message, null, null);
                inviteDialog.dismiss();
            }
        }
    }

    public void showAppUser() {
        if (checkConnectivity()) {

            try {
                getAppUser();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showSnackBar();
            mProgressDialog.dismiss();
        }

    }

    public boolean checkConnectivity() {
        return NetworkConnectionReciever.isConnected();
    }

    @Override
    public void OnNetworkChange(boolean inConnected) {
        this.isConnected = inConnected;
    }


    public void showSnackBar() {

        Snackbar.make(inviteUserContainer, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();

    }

    public void getAppUser() throws Exception {
        String userNumber = searchEt.getText().toString();
        SharedPreferences sp = getActivity().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        final String riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        LatLng latLng = AutoRideRiderApps.getInstance().getRiderPickupLatLng();
        double lat = latLng.latitude;
        double lng = latLng.longitude;

        String url = Constants.INVITE_REQUEST_CHECK_URL + riderId + "&phone=" + userNumber + "&lat=" + lat + "&lng=" + lng;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Result", response);
                //    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String statusCode = jsonObject.getString("statusCode");
                    if (statusCode.equalsIgnoreCase("200")) {
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("Success")) {
                            JSONObject anotherJsonObject = (JSONObject) jsonObject.get("data");
                            for (int j = 0; j < anotherJsonObject.length(); j++) {

                                String isExist = anotherJsonObject.getString("isExist");
                                String isBind = anotherJsonObject.getString("isBind");
                                String imageUrl = anotherJsonObject.getString("imageUrl");
                                String userId = anotherJsonObject.getString("userId");

                                AutoRideRiderApps.getInstance().setAppInstallStatus(isExist);
                                AutoRideRiderApps.getInstance().setImageUrlTracker(imageUrl);
                                AutoRideRiderApps.getInstance().setIsBindStatus(isBind);

                                if (userId.equalsIgnoreCase(riderId)) {
                                    Snackbar.make(getView(), "You can't add self!", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    if (isBind.equalsIgnoreCase("true")) {
                                        Snackbar.make(getView(), "You have already added !", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        if (AutoRideRiderApps.getInstance().getAppInstallStatus().equalsIgnoreCase("true")) {
                                            AutoRideRiderApps.getInstance().setTrackerFireBaseToken(anotherJsonObject.getString("fireBaseToken"));
                                            AutoRideRiderApps.getInstance().setTrackerUserId(anotherJsonObject.getString("userId"));
                                            textStatus.setText("Request!");
                                            Glide.with(getActivity())
                                                    .load(AutoRideRiderApps.getInstance().getImageUrlTracker())
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
                                                            imageAvator.setImageDrawable(roundedBitmapDrawable);
                                                            inviteDialog.show();
                                                        }
                                                    });

                                        }
                                    }
                                }


                            }
                        } else {
                            textStatus.setText("Invite!");
                            inviteDialog.show();
                        }

                    } else {
                        Snackbar.make(getView(), "Slow Internet connection!", Snackbar.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

            }
        }) {
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
        AutoRideRiderApps.getInstance().addToRequestQueue(stringRequest);
    }


    private void getAllContactsM() {
        List<Contact> contactVOList = new ArrayList();
        Contact contactVO;
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    contactVO = new Contact();
                    contactVO.setContactName(name);
                    contactVO.setContactPhoneNumber(number);
                    contactVOList.add(contactVO);
                }
            } finally {
                cursor.close();
            }
            customAdapter = new CustomAdapter(getActivity(), contactVOList);
            contactLv.setAdapter(customAdapter);
        }
    }


    String riderPlace;

    private String getAddress(LatLng latLng) {

        try {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = addressList.get(0);
            riderPlace = address.getAddressLine(0);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return riderPlace;
    }

    private void createInviteDialog() {
        inviteDialog = new Dialog(context);
        inviteDialog.setCancelable(false);
        //set content
        //  inviteDialog.setContentView(R.layout.custom_invite_dialog);
        inviteDialog.setContentView(R.layout.invite_dialog_new);



        inviteActionButton = (CircularProgressImageButton) inviteDialog.findViewById(R.id.inviteActionButton);
        revertActionButton = (FloatingActionButton) inviteDialog.findViewById(R.id.revertActionButton);

        //   revertActionButton.setVisibility(View.GONE);
        textStatus = (TextView) inviteDialog.findViewById(R.id.textStatus);
        textName = (TextView) inviteDialog.findViewById(R.id.contactName);
        textPhone = (TextView) inviteDialog.findViewById(R.id.textPhone);
        imageAvator = (ImageView) inviteDialog.findViewById(R.id.imageAvator);


        textName.setText(name);
        textPhone.setText(searchEt.getText().toString());


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(inviteDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.9f;
        inviteDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        inviteDialog.getWindow().setAttributes(lp);
 //style id

        inviteDialog.show();
    }

    public Contact getItem(int position) {
        final Contact pos = customAdapter.getItem(position);
        return pos;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        searchEt.setText("");
    }

    private void animateAndDoneFast(final CircularProgressImageButton animatedButton) {
        animatedButton.startAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animatedButton.doneLoadingAnimation(
                        //ContextCompat.getColor(animatedButton, R.color.black),
                        ContextCompat.getColor(getContext(), R.color.ok),
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_done));

            }
        }, 3000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animatedButton.revertAnimation();
                animatedButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_done));

                sendInvitation();
            }
        }, 5000);

    }

    private void animateAndDoneFastInvite(final CircularProgressImageButton animatedButton) {
        animatedButton.startAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animatedButton.doneLoadingAnimation(
                        //ContextCompat.getColor(animatedButton, R.color.black),
                        ContextCompat.getColor(getContext(), R.color.ok),
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_done));
                submitInvitation();

            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animatedButton.revertAnimation();
                animatedButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_done));

            }
        }, 5000);

    }
    public void dismissInviteDialog() {
        if (inviteDialog != null && inviteDialog.isShowing()) {
            inviteDialog.dismiss();
        }
    }
}
