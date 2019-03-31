package org.autoride.autoride.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.R;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.model.CountryInfo;
import org.autoride.autoride.model.CountryInfoList;
import org.autoride.autoride.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.listeners.ParserListener;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.constants.CountryConstants;
import org.autoride.autoride.notifications.commons.Common;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiderLoginActivity extends BaseAppCompatNoDrawerActivity implements RiderApiUrl, AppsConstants {

    private static final String TAG = "RiderLogin";
    private RiderInfo riderInfo;
    private ViewFlipper vfRiderLogin;
    private Spinner spinnerCountryCodes;
    private CountryInfoList countryInfoList;
    private ImageView ivCountryFlag;
    private EditText etRiderPhone, etRiderPassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_login);

        setUiComponent();
        getUserLastLocation();
    }

    private void setUiComponent() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pDialog = new ProgressDialog(RiderLoginActivity.this);
        riderInfo = new RiderInfo();

        etRiderPhone = (EditText) findViewById(R.id.et_rider_login_phone);
        etRiderPassword = (EditText) findViewById(R.id.et_rider_login_password);
        etRiderPassword.setTypeface(typeface);
        ivCountryFlag = (ImageView) findViewById(R.id.iv_rider_login_mobile_country_flag);
        spinnerCountryCodes = (Spinner) findViewById(R.id.spinner_rider_login_mobile_country_code);
        vfRiderLogin = (ViewFlipper) findViewById(R.id.view_flipper_rider_login);
        vfRiderLogin.setDisplayedChild(0);

        countryInfoList = CountryConstants.getCountryConstants();
        Collections.sort(countryInfoList.getCountries());
        List<String> countryDialCodes = new ArrayList<>();
        for (CountryInfo bean : countryInfoList.getCountries()) {
            countryDialCodes.add(bean.getDialCode());
        }
        ArrayAdapter<String> adapterCountryCodes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryDialCodes);
        adapterCountryCodes.setDropDownViewResource(R.layout.item_spinner);
        spinnerCountryCodes.setAdapter(adapterCountryCodes);
        spinnerCountryCodes.setSelection(210);

        Glide.with(getApplicationContext())
                .load("file:///android_asset/" + "flags/"
                        + countryInfoList.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop())
                .into(ivCountryFlag);

        spinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + countryInfoList.getCountries().get(position).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(ivCountryFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + countryInfoList.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(ivCountryFlag);
            }
        });
    }

    public void onRiderPhoneCheck(View view) {
        Common.startWaitingDialog(pDialog);
        if (collectMobileNumber()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                phoneAvailabilityCheck(riderInfo.getPhone());
            } else {
                snackBarNoInternet();
            }
        }
    }

    private boolean collectMobileNumber() {
        if (spinnerCountryCodes.getSelectedItem().toString().equalsIgnoreCase("")) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, getString(R.string.message_please_select_a_country_dial_code), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }
        if (etRiderPhone.getText().toString().equalsIgnoreCase("")) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, getString(R.string.message_phone_number_is_required), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }
        riderInfo.setPhone(spinnerCountryCodes.getSelectedItem().toString() + etRiderPhone.getText().toString());
        return true;
    }

    public void phoneAvailabilityCheck(final String phone) {
        JSONObject postBody = getBodyJson(phone, null);
        ManagerData.taskManager(POST, PHONE_NUMBER_EXIST_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    if (riderInfo.isAvailable()) {
                        Snackbar.make(coordinatorLayout, getString(R.string.message_valid_login_phone),
                                Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    } else {
                        vfRiderLogin.setInAnimation(slideLeftIn);
                        vfRiderLogin.setOutAnimation(slideLeftOut);
                        vfRiderLogin.showNext();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG).
                            setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    public void onRiderLogin(View view) {
        Common.startWaitingDialog(pDialog);
        if (collectPassword()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                if (AutoRideRiderApps.isLocationEnabled()) {
                    performRiderLogin(riderInfo.getPhone(), riderInfo.getPassword());
                } else {
                    Common.stopWaitingDialog(pDialog);
                    Snackbar.make(coordinatorLayout, R.string.no_gps_connection, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                }
            } else {
                snackBarNoInternet();
            }
        }
    }

    private boolean collectPassword() {
        riderInfo.setPassword(etRiderPassword.getText().toString());
        if (riderInfo.getPassword() == null || riderInfo.getPassword().equals("")) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.message_password_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getPassword().length() < 6) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getPassword().length() > 20) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.message_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performRiderLogin(String phone, final String password) {
        JSONObject postBody = getBodyJson(phone, password);
        ManagerData.taskManager(POST, LOGIN_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    Log.i(TAG, "loginLatitude " + lastLatitude + "loginLongitude " + lastLongitude);

                    AutoRideRiderApps.saveToSession(riderInfo);

                    mainActivity();

                    /*Intent intent = new Intent(RiderLoginActivity.this, RiderMainActivity.class);
                    startActivity(intent);
                    finish();*/
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderLoginActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RiderLoginActivity.this, RiderWelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    private void mainActivity() {
        Intent intent = new Intent(RiderLoginActivity.this, RiderMainActivity.class);
        intent.putExtra(RiderMainActivity.EXTRA_REVEAL_X, 10);
        intent.putExtra(RiderMainActivity.EXTRA_REVEAL_Y, 10);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderLoginActivity.this.finish();
    }

    private JSONObject getBodyJson(String phone, String password) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(PHONE, phone);
            if (password != null) {
                postData.put(PASSWORD, password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    public void onRiderForgotPasswordClicked(View view) {
        Intent intent = new Intent(RiderLoginActivity.this, RiderForgetPasswordActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderLoginActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return super.onCreateOptionsMenu(menu);
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
        int index = vfRiderLogin.getDisplayedChild();
        if (index > 0) {
            vfRiderLogin.setInAnimation(slideRightIn);
            vfRiderLogin.setOutAnimation(slideRightOut);
            vfRiderLogin.showPrevious();
        } else {
            Intent intent = new Intent(RiderLoginActivity.this, RiderWelcomeActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RiderLoginActivity.this.finish();
        }
    }

    private void snackBarNoInternet() {
        Snackbar.make(coordinatorLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
    }

    private void snackBarSlowInternet() {
        Snackbar.make(coordinatorLayout, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
    }
}