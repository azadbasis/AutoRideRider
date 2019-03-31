package org.autoride.autoride.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
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
import org.autoride.autoride.webRegistration.WebRegistrationActivity;
import org.autoride.autoride.widgets.OTPEditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiderRegistrationActivity extends BaseAppCompatNoDrawerActivity implements RiderApiUrl, AppsConstants {

    private static final String TAG = "RiderRegistration";
    private RiderInfo riderInfo;
    private ViewFlipper vfRiderRegistration;
    private EditText etFirstName, etLastName, etPhone, etEmail, etPassword, etPromoCode;
    private LinearLayout llVerification;
    private TextView txtVerificationLabel;
    private Spinner spinnerCountryCodes;
    private CountryInfoList countryInfoList;
    private ImageView ivCountryFlag;
    private OTPEditText oetOne, oetTwo, oetThree, oetFour, oetFive, oetSix;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId, promotionCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean isVerificationEnabled;
    private ProgressDialog pDialog;
    private String uRole = "user";

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
        setContentView(R.layout.activity_rider_registration);

        setUiComponent();
        getUserLastLocation();
        // ATTENTION: This was auto-generated to handle app links.
        handleIntent(getIntent());
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            String recipeId = appLinkData.getLastPathSegment();
          /*  Uri appData = Uri.parse("content://com.recipe_app/recipe/").buildUpon()
                    .appendPath(recipeId).build();
            showRecipe(appData);*/
        }
    }
    private void setUiComponent() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pDialog = new ProgressDialog(RiderRegistrationActivity.this);
        riderInfo = new RiderInfo();

        vfRiderRegistration = (ViewFlipper) findViewById(R.id.view_flipper_rider_registration);
        vfRiderRegistration.setDisplayedChild(0);

        llVerification = (LinearLayout) findViewById(R.id.ll_rider_registration_mobile_otp);
        txtVerificationLabel = (TextView) findViewById(R.id.ctv_rider_registration_mobile_otp_label);

        spinnerCountryCodes = (Spinner) findViewById(R.id.spinner_rider_registration_mobile_country_code);
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

        ivCountryFlag = (ImageView) findViewById(R.id.iv_rider_registration_mobile_country_flag);

        etFirstName = (EditText) findViewById(R.id.et_rider_registration_first_name);
        etLastName = (EditText) findViewById(R.id.et_rider_registration_last_name);
        etPhone = (EditText) findViewById(R.id.et_rider_registration_phone);
        etEmail = (EditText) findViewById(R.id.et_rider_registration_email);
        etPassword = (EditText) findViewById(R.id.et_rider_registration_password);
        etPromoCode = (EditText) findViewById(R.id.et_rider_registration_promo_code);

        oetOne = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_one);
        oetTwo = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_two);
        oetThree = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_three);
        oetFour = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_four);
        oetFive = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_five);
        oetSix = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_six);

        oetOne.setTypeface(typeface);
        oetTwo.setTypeface(typeface);
        oetThree.setTypeface(typeface);
        oetFour.setTypeface(typeface);
        oetFive.setTypeface(typeface);
        oetSix.setTypeface(typeface);

        etFirstName.setTypeface(typeface);
        etLastName.setTypeface(typeface);
        etPhone.setTypeface(typeface);
        etEmail.setTypeface(typeface);
        etPassword.setTypeface(typeface);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        firebaseAuth = FirebaseAuth.getInstance();
        setVerificationLayoutVisibility(false);

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

        oetOne.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength1 = oetOne.getText().length();
                if (textlength1 >= 1) {
                    oetOne.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    oetTwo.requestFocus();
                } else {
                    oetOne.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        oetTwo.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength2 = oetTwo.getText().length();
                if (textlength2 >= 1) {
                    oetTwo.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    oetThree.requestFocus();
                } else {
                    oetTwo.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        oetThree.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength3 = oetThree.getText().length();
                if (textlength3 >= 1) {
                    oetThree.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    oetFour.requestFocus();
                } else {
                    oetThree.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        oetFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = oetFour.getText().toString().length();
                if (textlength4 == 1) {
                    oetFour.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    oetFive.requestFocus();
                } else {
                    oetFour.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        oetFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = oetFive.getText().toString().length();
                if (textlength4 == 1) {
                    oetFive.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    oetSix.requestFocus();
                } else {
                    oetFive.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        oetSix.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = oetSix.getText().toString().length();
                if (textlength4 == 1) {
                    oetSix.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                } else {
                    oetSix.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        oetSix.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = oetSix.getText().toString().length();
                if (i == 0) {
                    oetFive.setText("");
                    oetFive.requestFocus();
                }
            }
        });

        oetFive.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = oetFive.getText().toString().length();
                if (i == 0) {
                    oetFour.setText("");
                    oetFour.requestFocus();
                }
            }
        });

        oetFour.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = oetFour.getText().toString().length();
                if (i == 0) {
                    oetThree.setText("");
                    oetThree.requestFocus();
                }
            }
        });

        oetThree.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = oetThree.getText().toString().length();
                if (i == 0) {
                    oetTwo.setText("");
                    oetTwo.requestFocus();
                }
            }
        });

        oetTwo.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {
                int i = oetTwo.getText().toString().length();
                if (i == 0) {
                    oetOne.setText("");
                    oetOne.requestFocus();
                }
            }
        });

        oetSix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (oetTwo.getText().toString().length() == 0) {
                        oetTwo.requestFocus();
                    } else if (oetThree.getText().toString().length() == 0) {
                        oetThree.requestFocus();
                    } else if (oetFour.getText().toString().length() == 0) {
                        oetFour.requestFocus();
                    } else if (oetFour.getText().toString().length() == 0) {
                        oetFive.requestFocus();
                    }
                }
            }
        });

        oetFive.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (oetTwo.getText().toString().length() == 0) {
                        oetTwo.requestFocus();
                    } else if (oetThree.getText().toString().length() == 0) {
                        oetThree.requestFocus();
                    } else if (oetFour.getText().toString().length() == 0) {
                        oetFour.requestFocus();
                    }
                }
            }
        });

        oetFour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (oetTwo.getText().toString().length() == 0) {
                        oetTwo.requestFocus();
                    } else if (oetThree.getText().toString().length() == 0) {
                        oetThree.requestFocus();
                    }
                }
            }
        });

        oetThree.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    } else if (oetTwo.getText().toString().length() == 0) {
                        oetTwo.requestFocus();
                    }
                }
            }
        });

        oetTwo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (oetOne.getText().toString().length() == 0) {
                        oetOne.requestFocus();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Common.startWaitingDialog(pDialog);
                Log.i(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                verificationCodeSender(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Common.stopWaitingDialog(pDialog);
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.i(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.i(TAG, "onVerificationFailed: " + e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.i(TAG, "onVerificationFailed: " + e);
                }
                Snackbar.make(coordinatorLayout, R.string.message_phone_verification_failed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Common.stopWaitingDialog(pDialog);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.i(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Snackbar.make(coordinatorLayout, getString(R.string.message_verification_code_sent_to) + " " + riderInfo.getPhone(),
                        Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                setVerificationLayoutVisibility(true);

            }
        };
    }

    private void setVerificationLayoutVisibility(boolean isVisible) {
        if (isVisible) {
            //  findViewById(R.id.webContainer).setVisibility(View.GONE);
            llVerification.setVisibility(View.VISIBLE);
            txtVerificationLabel.setVisibility(View.VISIBLE);
            oetOne.requestFocus();
            isVerificationEnabled = true;
        } else {
            llVerification.setVisibility(View.GONE);
            txtVerificationLabel.setVisibility(View.GONE);
            // findViewById(R.id.webContainer).setVisibility(View.VISIBLE);
            oetOne.setText("");
            oetTwo.setText("");
            oetThree.setText("");
            oetFour.setText("");
            oetFive.setText("");
            oetSix.setText("");
            isVerificationEnabled = false;
        }
    }

    public void onRiderPhoneVerification(View view) {
        Common.startWaitingDialog(pDialog);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            if (isVerificationEnabled) {
                Common.stopWaitingDialog(pDialog);
                String otpCode = "" + oetOne.getText().toString() + oetTwo.getText().toString()
                        + oetThree.getText().toString() + oetFour.getText().toString()
                        + oetFive.getText().toString() + oetSix.getText().toString();

                if (!otpCode.equalsIgnoreCase("")) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
                    verificationCodeSender(credential);
                } else {
                    // findViewById(R.id.webContainer).setVisibility(View.VISIBLE);
                    Snackbar.make(coordinatorLayout, getString(R.string.message_invalid_verification_code), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
                }
            } else {
                // findViewById(R.id.webContainer).setVisibility(View.VISIBLE);
                if (collectMobileNumber()) {
                    phoneAvailabilityCheck(riderInfo.getPhone());
                }
            }
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    private boolean collectMobileNumber() {
        if (spinnerCountryCodes.getSelectedItem().toString().equalsIgnoreCase("")) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, getString(R.string.message_please_select_a_country_dial_code), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }
        if (etPhone.getText().toString().equalsIgnoreCase("")) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, getString(R.string.message_phone_number_is_required), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), snackBarDismissOnClickListener).show();
            return false;
        }
        riderInfo.setPhone(spinnerCountryCodes.getSelectedItem().toString() + etPhone.getText().toString());
        return true;
    }

    private void phoneAvailabilityCheck(final String phone) {
        JSONObject postBody = getPhoneBodyJson(phone);
        ManagerData.taskManager(POST, PHONE_NUMBER_EXIST_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    if (riderInfo.isAvailable()) {
                        phoneVerification();
                    } else {
                        Snackbar.make(coordinatorLayout, getString(R.string.message_is_already_registered),
                                Snackbar.LENGTH_LONG).setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    private void phoneVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                riderInfo.getPhone(),        // Phone number to verify
                2,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);

        Log.i(TAG, "phone_registration_time " + riderInfo.getPhone());

        Snackbar.make(coordinatorLayout, R.string.message_sending_verification_code, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
    }

    private void verificationCodeSender(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Common.stopWaitingDialog(pDialog);
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "signInWithCredential:success");

                    FirebaseUser user = task.getResult().getUser();

                    Log.i(TAG, "onComplete: " + new Gson().toJson(task));

                    vfRiderRegistration.setInAnimation(slideLeftIn);
                    vfRiderRegistration.setOutAnimation(slideLeftOut);
                    vfRiderRegistration.showNext();
                    // getSupportActionBar().show();
                } else {
                    Common.stopWaitingDialog(pDialog);
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Snackbar.make(coordinatorLayout, R.string.message_invalid_verification_code, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    }
                }
            }
        });
    }

    public void onRiderRegistrationEmail(View view) {
        Common.startWaitingDialog(pDialog);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            if (collectEmailAddress()) {
                getDefaultPromotionCode(lastLatitude, lastLatitude);
                vfRiderRegistration.setInAnimation(slideLeftIn);
                vfRiderRegistration.setOutAnimation(slideLeftOut);
                vfRiderRegistration.showNext();
            }
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    private boolean collectEmailAddress() {
        riderInfo.setEmail(etEmail.getText().toString());
        if (riderInfo.getEmail() == null || riderInfo.getEmail().equals("")) {
            riderInfo.setEmail("");
            return true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(riderInfo.getEmail()).matches()) {
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.message_enter_a_valid_email_address, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void getDefaultPromotionCode(double lat, double lng) {
        JSONObject postBody = getLatLngBodyJson(lat, lng);
        ManagerData.taskManager(GET, PROMO_CODE_SEARCH_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                promotionCode = riderInfo.getPromotionCode();
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                promotionCode = null;
            }
        });
    }

    public void onRiderRegistrationName(View view) {
        if (collectName()) {
            Log.i(TAG, PROMOTION_CODE + " " + promotionCode);
            if (promotionCode != null) {
                etPromoCode.setText(promotionCode);
                vfRiderRegistration.setInAnimation(slideLeftIn);
                vfRiderRegistration.setOutAnimation(slideLeftOut);
                vfRiderRegistration.showNext();
            } else {
                Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG).
                        setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
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
        }
        if (riderInfo.getLastName() == null || riderInfo.getLastName().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.last_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    public void onRiderRegistrationPromoCode(View view) {
        if (collectPromoCode()) {
            vfRiderRegistration.setInAnimation(slideLeftIn);
            vfRiderRegistration.setOutAnimation(slideLeftOut);
            vfRiderRegistration.showNext();
        }
    }

    private boolean collectPromoCode() {
        String pCode = etPromoCode.getText().toString();
        if (pCode.equals(DOUBLE_QUOTES) || pCode.equalsIgnoreCase(NULLS)) {
            ///////////////////// promoCode default set
            return true;
        } else {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                promoCodeAvailabilityCheck(pCode);
            } else {
                snackBarNoInternet();
            }
            return false;
        }
    }

    public void promoCodeAvailabilityCheck(final String promoCode) {
        Common.startWaitingDialog(pDialog);
        JSONObject postBody = getPromoCodeBodyJson(promoCode);
        ManagerData.taskManager(POST, PROMO_CODE_EXIST_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    if (riderInfo.isAvailable()) {
                        promotionCode = riderInfo.getPromotionCode();
                        vfRiderRegistration.setInAnimation(slideLeftIn);
                        vfRiderRegistration.setOutAnimation(slideLeftOut);
                        vfRiderRegistration.showNext();
                    } else {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG).
                                setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    }
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    public void onRiderRegistration(View view) {
        Common.startWaitingDialog(pDialog);
        if (collectPassword()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                if (AutoRideRiderApps.isLocationEnabled()) {
                    riderInfo.setLastLatitude(String.valueOf(lastLatitude));
                    riderInfo.setLastLongitude(String.valueOf(lastLongitude));
                    performRiderRegistration();
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
        riderInfo.setPassword(etPassword.getText().toString());
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

    private void performRiderRegistration() {
        JSONObject postBody = getRegistrationBodyJson();
        ManagerData.taskManager(POST, REGISTRATION_URL, postBody, new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);

                if (riderInfo != null) {
                    Log.i(TAG, "registrationLatitude " + riderInfo.getLastLatitude() + "registrationLongitude " + riderInfo.getLastLongitude());

                    AutoRideRiderApps.saveToSession(riderInfo);
                    Intent intent = new Intent(RiderRegistrationActivity.this, RiderMainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                } else {
                    snackBarSlowInternet();
                }
            }
        });
    }

    private JSONObject getPhoneBodyJson(String phone) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(PHONE, phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private JSONObject getLatLngBodyJson(double lat, double lng) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(LAT, lat);
            postData.put(LNG, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private JSONObject getPromoCodeBodyJson(String pCode) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(ROLE, uRole);
            postData.put(PROMOTION_CODE, pCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private JSONObject getRegistrationBodyJson() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(PHONE, riderInfo.getPhone());
            postBody.put(FIRST_NAME, riderInfo.getFirstName());
            postBody.put(LAST_NAME, riderInfo.getLastName());
            postBody.put(ROLE, uRole);
            postBody.put(PROMOTION_CODE, promotionCode);
            postBody.put(PASSWORD, riderInfo.getPassword());
            postBody.put(EMAIL, riderInfo.getEmail());
            postBody.put(LAT, riderInfo.getLastLatitude());
            postBody.put(LNG, riderInfo.getLastLongitude());
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
            setVerificationLayoutVisibility(false);
        } else {
            Intent intent = new Intent(RiderRegistrationActivity.this, RiderWelcomeActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RiderRegistrationActivity.this.finish();
        }
    }

    public void onWelcomeRiderRegistrationWeb(View view) {
        startActivity(new Intent(RiderRegistrationActivity.this, WebRegistrationActivity.class));

       /* Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setClassName(RiderRegistrationActivity.this, "org.autoride.autoride.webRegistration.WebRegistrationActivity");
        startActivity(intent);*/
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