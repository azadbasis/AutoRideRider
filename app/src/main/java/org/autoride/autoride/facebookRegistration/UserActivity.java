package org.autoride.autoride.facebookRegistration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import android.widget.ViewFlipper;

import com.google.firebase.auth.PhoneAuthProvider;

import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.autoride.listeners.ParserListener;
import org.autoride.autoride.message.PopupMessage;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.utils.Operation;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserActivity extends BaseAppCompatNoDrawerActivity implements RiderApiUrl, AppsConstants {

    private static final String TAG = "RegistrationActivity";
    private RiderInfo riderInfo;
    private ViewFlipper vfRiderRegistration;
    private EditText etFirstName, etLastName, etEmail, etPassword, etPromoCode;


    private String mVerificationId, promotionCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String uRole = "user";
    String riderPhoneNumber;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (getIntent() != null) {
            riderPhoneNumber = getIntent().getStringExtra("RIDER-PHONE-NUMBER");
        }
        View mainView = getLayoutInflater().inflate(R.layout.activity_user, null);
        riderInfo = new RiderInfo();
        riderInfo.setPhone(riderPhoneNumber);
        // checkPhoneNumber(mainView);
        setUiComponent();
        getUserLastLocation();
    }

    private void setUiComponent() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  swipeView.setPadding(0, 0, 0, 0);
        swipeView.setPadding(0, (int) mActionBarHeight, 0, 0);

        //  riderInfo = new RiderInfo();

        vfRiderRegistration = (ViewFlipper) findViewById(R.id.view_flipper_rider_registration);
        vfRiderRegistration.setDisplayedChild(0);


        etFirstName = (EditText) findViewById(R.id.et_rider_registration_first_name);
        etLastName = (EditText) findViewById(R.id.et_rider_registration_last_name);

        etEmail = (EditText) findViewById(R.id.et_rider_registration_email);
        etPassword = (EditText) findViewById(R.id.et_rider_registration_password);
        etPromoCode = (EditText) findViewById(R.id.et_rider_registration_promo_code);


        etFirstName.setTypeface(typeface);
        etLastName.setTypeface(typeface);
        etEmail.setTypeface(typeface);
        etPassword.setTypeface(typeface);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
    }


    void checkPhoneNumber(View mainView) {
        mainView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (AutoRideRiderApps.isNetworkAvailable()) {

            if (!riderPhoneNumber.equalsIgnoreCase("")) {
//                    swipeView.setRefreshing(true);
                if (riderPhoneNumber != null) {
                    swipeView.setRefreshing(false);

                    vfRiderRegistration.setInAnimation(slideLeftIn);
                    vfRiderRegistration.setOutAnimation(slideLeftOut);
                    vfRiderRegistration.showNext();
                    // getSupportActionBar().show();
                    swipeView.setPadding(0, (int) mActionBarHeight, 0, 0);
                } else {
                    swipeView.setRefreshing(false);

                }


            } else {

                Snackbar.make(coordinatorLayout, getString(R.string.message_invalid_verification_code), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            }


            if (collectMobileNumber()) {
                //phoneAvailabilityCheck(riderInfo.getPhone());
//                    phoneAvailabilityCheck(riderPhoneNumber);
            }

        } else {

            new PopupMessage(this).show(NO_NETWORK_AVAILABLE);
        }
    }


    public void onRiderPhoneVerification(View view) {

        if (AutoRideRiderApps.isNetworkAvailable()) {


            if (collectMobileNumber()) {
                //phoneAvailabilityCheck(riderInfo.getPhone());
//                    phoneAvailabilityCheck(riderPhoneNumber);
            }

            swipeView.setRefreshing(true);
            if (riderPhoneNumber != null) {
                swipeView.setRefreshing(false);

                vfRiderRegistration.setInAnimation(slideLeftIn);
                vfRiderRegistration.setOutAnimation(slideLeftOut);
                vfRiderRegistration.showNext();
                // getSupportActionBar().show();
                swipeView.setPadding(0, (int) mActionBarHeight, 0, 0);
            } else {
                swipeView.setRefreshing(false);

            }
        } else {
            new PopupMessage(this).show(NO_NETWORK_AVAILABLE);
        }
    }

    private boolean collectMobileNumber() {

        if (riderPhoneNumber.toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinatorLayout, getString(R.string.message_phone_verified_successfully), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }

        riderInfo.setPhone(riderPhoneNumber);
        return true;
    }

    private void phoneAvailabilityCheck(final String phone) {
        swipeView.setRefreshing(true);
        JSONObject postBody = getPhoneBodyJson(phone);
        ManagerData.taskManager(POST, PHONE_NUMBER_EXIST_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                if (riderInfo.isAvailable()) {
                    if (collectEmailAddress()) {
                        getDefaultPromotionCode(lastLatitude, lastLatitude);
                        vfRiderRegistration.setInAnimation(slideLeftIn);
                        vfRiderRegistration.setOutAnimation(slideLeftOut);
                        vfRiderRegistration.showNext();
                    }
                } else {
                    Snackbar.make(coordinatorLayout, getString(R.string.message_is_already_registered),
                            Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                if (riderInfo != null) {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                }
            }
        });
    }

    private void phoneVerification(String riderPhoneNumber) {

//          riderPhoneNumber=   riderInfo.getPhone();
        riderPhoneNumber = Operation.getString("RIDER-PHONE-NUMBER", "");
        Log.i(TAG, "code_phone " + riderPhoneNumber);

        Snackbar.make(coordinatorLayout, R.string.message_phone_verified_successfully, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
        swipeView.setRefreshing(false);
    }


    public void onRiderRegistrationEmail(View view) {
        phoneAvailabilityCheck(riderInfo.getPhone());
    }

    private boolean collectEmailAddress() {
        riderInfo.setEmail(etEmail.getText().toString());
        if (riderInfo.getEmail() == null || riderInfo.getEmail().equals("")) {
            riderInfo.setEmail("");
            return true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(riderInfo.getEmail()).matches()) {
            Snackbar.make(coordinatorLayout, R.string.message_enter_a_valid_email_address, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void getDefaultPromotionCode(double lat, double lng) {
        swipeView.setRefreshing(true);
        JSONObject postBody = getLatLngBodyJson(lat, lng);
        ManagerData.taskManager(GET, PROMO_CODE_SEARCH_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                promotionCode = riderInfo.getPromotionCode();
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                promotionCode = null;
            }
        });
    }

    public void onRiderRegistrationName(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectName()) {
            Log.i(TAG, "promotionCode " + promotionCode);
            if (promotionCode != null) {
                etPromoCode.setText(promotionCode);
                vfRiderRegistration.setInAnimation(slideLeftIn);
                vfRiderRegistration.setOutAnimation(slideLeftOut);
                vfRiderRegistration.showNext();
            } else {
                Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE,
                        Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            }
        }
    }

    private boolean collectName() {
        riderInfo.setFirstName(etFirstName.getText().toString());
        riderInfo.setLastName(etLastName.getText().toString());

        if (riderInfo.getFirstName() == null || riderInfo.getFirstName().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.first_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getFirstName().length() < 3) {
            Snackbar.make(coordinatorLayout, "First Name is too small", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getFirstName().length() > 14) {
            Snackbar.make(coordinatorLayout, "First Name is too long", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }

        if (riderInfo.getLastName() == null || riderInfo.getLastName().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.last_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getLastName().length() < 3) {
            Snackbar.make(coordinatorLayout, "Last Name is too small", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getLastName().length() > 14) {
            Snackbar.make(coordinatorLayout, "Last Name is too long", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    public void onRiderRegistrationPromoCode(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectPromoCode()) {
            vfRiderRegistration.setInAnimation(slideLeftIn);
            vfRiderRegistration.setOutAnimation(slideLeftOut);
            vfRiderRegistration.showNext();
        }
    }

    private boolean collectPromoCode() {
        String pCode = etPromoCode.getText().toString();
        if (pCode.equals("") || pCode.equalsIgnoreCase("null")) {
            ///////////////////// promoCode default set
            return true;
        } else {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                promoCodeAvailabilityCheck(pCode);
            } else {
                new PopupMessage(this).show(NO_NETWORK_AVAILABLE);
            }
            return false;
        }
    }

    public void promoCodeAvailabilityCheck(final String promoCode) {
        swipeView.setRefreshing(true);
        JSONObject postBody = getPromoCodeBodyJson(promoCode);
        ManagerData.taskManager(POST, PROMO_CODE_EXIST_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                if (riderInfo.isAvailable()) {
                    promotionCode = riderInfo.getPromotionCode();
                    vfRiderRegistration.setInAnimation(slideLeftIn);
                    vfRiderRegistration.setOutAnimation(slideLeftOut);
                    vfRiderRegistration.showNext();
                } else {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(),
                            Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                if (riderInfo != null) {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                }
            }
        });
    }

    public void onRiderRegistration(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (collectPassword()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                if (AutoRideRiderApps.isLocationEnabled()) {
                    riderInfo.setLastLatitude(String.valueOf(lastLatitude));
                    riderInfo.setLastLongitude(String.valueOf(lastLongitude));
                    performRiderRegistration();
                } else {
                    new PopupMessage(this).show(LOCATION_DISABLE, "location");
                }
            } else {
                new PopupMessage(this).show(NO_NETWORK_AVAILABLE);
            }
        }
    }

    private boolean collectPassword() {
        riderInfo.setPassword(etPassword.getText().toString());
        if (riderInfo.getPassword() == null || riderInfo.getPassword().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.message_password_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getPassword().length() < 6) {
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getPassword().length() > 20) {
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performRiderRegistration() {
        swipeView.setRefreshing(true);
        JSONObject postBody = getRegistrationBodyJson();
        ManagerData.taskManager(POST, REGISTRATION_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                AutoRideRiderApps.saveToSession(riderInfo);

//                Toast.makeText(getApplicationContext(), "lastLatitude " + riderInfo.getLastLatitude(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "lastLongitude " + riderInfo.getLastLongitude(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), riderInfo.getWebMessage(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), RiderMainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                swipeView.setRefreshing(false);
                if (riderInfo != null) {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                }
            }
        });
    }

    private JSONObject getPhoneBodyJson(String phone) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private JSONObject getLatLngBodyJson(double lat, double lng) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("lat", lat);
            postData.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private JSONObject getPromoCodeBodyJson(String pCode) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("role", uRole);
            postData.put("promoCode", pCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private JSONObject getRegistrationBodyJson() {
        JSONObject postBody = new JSONObject();
        try {

            postBody.put("phone", riderInfo.getPhone());
            postBody.put("firstName", riderInfo.getFirstName());
            postBody.put("lastName", riderInfo.getLastName());
            postBody.put("role", uRole);
            postBody.put("promoCode", promotionCode);
            postBody.put("password", riderInfo.getPassword());
            postBody.put("email", riderInfo.getEmail());
            postBody.put("lat", riderInfo.getLastLatitude());
            postBody.put("lng", riderInfo.getLastLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "RegistrationBodyJson " + postBody);
        return postBody;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                lytContent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                onHomeClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onHomeClick();
            return true;
        }
        return false;
    }

    private void onHomeClick() {
        int index = vfRiderRegistration.getDisplayedChild();
        if (index > 1) {
            vfRiderRegistration.setInAnimation(slideRightIn);
            vfRiderRegistration.setOutAnimation(slideRightOut);
            vfRiderRegistration.showPrevious();
        } else if (index == 1) {
            vfRiderRegistration.setInAnimation(slideRightIn);
            vfRiderRegistration.setOutAnimation(slideRightOut);
            vfRiderRegistration.showPrevious();
            swipeView.setPadding(0, 0, 0, 0);

        } else {
            startActivity(new Intent(this, RiderWelcomeActivity.class));
            finish();
        }
    }
}