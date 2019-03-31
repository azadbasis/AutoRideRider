package org.autoride.autoride.SMSAuth;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.autoride.autoride.R;
import org.autoride.autoride.SMSAuth.BroadcastReceiver.SmsReceiver;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.facebookRegistration.UserActivity;
import org.autoride.autoride.utils.Constants;
import org.autoride.autoride.utils.Operation;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.autoride.autoride.webRegistration.WebRegistrationActivity;
import org.autoride.autoride.widgets.OTPEditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SMSAuthActivity extends AppCompatActivity implements NetworkConnectionReciever.ConnectivityRecieverListener {

    private static final String TAG = "SMSAuthActivity";
    private EditText phoneNumberEt;
    private String myPhoneNumber;
    private SmsReceiver receiver;
    private SMSItem smsItem;
    private static final String NA = "NA";
    private Boolean isConnected;
    private LinearLayout containerAuthorisedCode;
    private LinearLayout containerPhoneNumber;
    private OTPEditText oetOne, oetTwo, oetThree, oetFour, oetFive, oetSix;
    private ArrayList<OTPEditText> myOtpEditTexts;
    private Button btnRegister;

    private String authorizedStatus;
    private String authorizedMessage;
    private FloatingActionButton fabCheckCode;

    boolean isMyNumber;
    private Dialog dialog, webDialog;
    private FloatingActionButton showBtn, cancelBtn;
    private EditText contentEditText;
    private ProgressBar progress_bar;
    private Toolbar toolbar, toolbarOTP;
    private LinearLayout smsContainer, containerWeb;
    private RelativeLayout containerCustomView;
    private TextView tvWeb;
    private String getCode;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsauth);

        initUI();
        createDialog();
        dialog.show();

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn.setVisibility(View.VISIBLE);
                showProgress();
                if (checkConnectivity()) {
                    myPhoneNumber = contentEditText.getText().toString();
                    myPhoneNumber = myPhoneNumber.replaceAll("\\s+", "");
                    if (myPhoneNumber.equalsIgnoreCase("")) {
                        hideProgress();
                        containerWeb.setVisibility(View.INVISIBLE);
                        Snackbar.make(containerCustomView, getString(R.string.message_phone_number_is_required), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.btn_dismiss), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).setActionTextColor(Color.RED).show();

                    } else {
                        uploadSMS(myPhoneNumber);
                    }

                } else {
                    showSnackBar();
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progress_bar.isShown()) {
                            hideProgress();
                            if (checkConnectivity()) {
                                containerWeb.setVisibility(View.VISIBLE);
                                containerWeb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(SMSAuthActivity.this, WebRegistrationActivity.class));
                                    }
                                });
                            } else {
                                showSnackBar();
                            }

                        }
                    }
                }, 10000);

                //start the thread

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //smsContainer.setVisibility(View.VISIBLE);

                dialog.dismiss();
                startActivity(new Intent(SMSAuthActivity.this, RiderWelcomeActivity.class));
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SMSAuthActivity.this, RiderWelcomeActivity.class));
                finish();
            }
        });


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Registration");
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
    }

    private void createDialog() {

        dialog = new Dialog(this);
        dialog.setCancelable(false);
        //set content
        dialog.setContentView(R.layout.custom_view);
        toolbar = (Toolbar) dialog.findViewById(R.id.toolbarInviteDialog);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showBtn = (FloatingActionButton) dialog.findViewById(R.id.showTxt);
        cancelBtn = (FloatingActionButton) dialog.findViewById(R.id.cancelTxt);

        cancelBtn.setVisibility(View.GONE);
        contentEditText = (EditText) dialog.findViewById(R.id.contentEditText);
        progress_bar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
        containerWeb = (LinearLayout) dialog.findViewById(R.id.containerWeb);
        containerCustomView = (RelativeLayout) dialog.findViewById(R.id.containerCustomView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void initUI() {

        toolbarOTP = (Toolbar) findViewById(R.id.toolbarOTP);
        setSupportActionBar(toolbarOTP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarOTP.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                smsContainer.setVisibility(View.INVISIBLE);
            }
        });

        smsContainer = findViewById(R.id.smsContainer);
        smsContainer.setVisibility(View.INVISIBLE);
        smsItem = new SMSItem();
        myOtpEditTexts = new ArrayList<>();
        fabCheckCode = (FloatingActionButton) findViewById(R.id.fabCheckCode);
        containerAuthorisedCode = (LinearLayout) findViewById(R.id.containerAuthorisedCode);

        oetOne = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_one);
        oetTwo = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_two);
        oetThree = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_three);
        oetFour = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_four);
        oetFive = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_five);
        oetSix = (OTPEditText) findViewById(R.id.oet_rider_registration_mobile_six);
        myOtpEditTexts.add(oetOne);
        myOtpEditTexts.add(oetTwo);
        myOtpEditTexts.add(oetThree);
        myOtpEditTexts.add(oetFour);
        myOtpEditTexts.add(oetFive);
        myOtpEditTexts.add(oetSix);

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
    }

    private void uploadSMS(String phoneNumber) {
        if (checkConnectivity()) {
            try {
                sendSMS(phoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showSnackBar();
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
        //into threa
        Snackbar.make(containerCustomView, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_setting), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();
    }

    private void sendSMS(String phoneNumber) {

        String url = Constants.SMS_URL + phoneNumber;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseSmsItem(response);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AutoRideRiderApps.getInstance().addToRequestQueue(stringRequest);
    }

    private void parseSmsItem(String response) {

        JSONObject smsItemObject;

        try {
            smsItemObject = new JSONObject(response);

            for (int i = 0; i < smsItemObject.length(); i++) {

                if (isContain(smsItemObject, "statusCode")) {

                    if (smsItemObject.getString("statusCode").equalsIgnoreCase("null")) {
                        smsItem.statusCode = "Server response error";
                    } else {
                        smsItem.statusCode = smsItemObject.getString("statusCode");
                    }
                } else {
                    smsItem.statusCode = NA;
                }
                if (isContain(smsItemObject, "status")) {

                    if (smsItemObject.getString("status").equalsIgnoreCase("error")) {
                        smsItem.status = smsItemObject.getString("status");

                        if (isContain(smsItemObject, "errors")) {

                            if (smsItemObject.getString("errors").equalsIgnoreCase("null")) {
                                smsItem.errors = "Server response error";
                            } else {
                                smsItem.errors = smsItemObject.getString("errors");
                            }

                        } else {
                            smsItem.success = NA;
                        }

                        if (isContain(smsItemObject, "message")) {

                            if (smsItemObject.getString("message").equalsIgnoreCase("null")) {
                                smsItem.message = "Server response error";
                            } else {
                                smsItem.message = smsItemObject.getString("message");
                            }
                        } else {
                            smsItem.message = NA;
                        }
                    }
                    if (smsItemObject.getString("status").equalsIgnoreCase("Success")) {
                        smsItem.status = smsItemObject.getString("status");

                        if (isContain(smsItemObject, "success")) {

                            if (smsItemObject.getString("success").equalsIgnoreCase("null")) {
                                smsItem.success = "Server response error";
                            } else {
                                smsItem.success = smsItemObject.getString("success");
                            }

                        } else {
                            smsItem.success = NA;
                        }

                        if (isContain(smsItemObject, "code")) {

                            if (smsItemObject.getString("code").equalsIgnoreCase("null")) {
                                smsItem.code = "Server response error";
                            } else {
                                smsItem.code = smsItemObject.getString("code");
                            }

                        } else {
                            smsItem.code = NA;
                        }

                        if (isContain(smsItemObject, "message")) {

                            if (smsItemObject.getString("message").equalsIgnoreCase("null")) {
                                smsItem.message = "Server response error";
                            } else {
                                smsItem.message = smsItemObject.getString("message");
                            }
                        } else {
                            smsItem.message = NA;
                        }

                    }

                } else {
                    smsItem.status = NA;
                }

                authorizedStatus = smsItem.status;
                Operation.saveString("AUTHORISED_STATUS", authorizedStatus);
                authorizedMessage = smsItem.message;

                if (authorizedStatus.equalsIgnoreCase("Success")) {
                    smsContainer.setVisibility(View.VISIBLE);
                    registerCode();
                    confirmRegistration();
                    dialog.dismiss();
                    hideProgress();
                    //CANCEL
                }

                if (authorizedStatus.equalsIgnoreCase("error")) {
                    // dialog.dismiss();

                    Snackbar.make(containerCustomView, authorizedMessage, Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.btn_dismiss), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).setActionTextColor(Color.RED).show();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isContain(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void registerCode() {
        IntentFilter intentFilter = new IntentFilter(
                "SmsMessage.intent.MAIN");

        receiver = new SmsReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);

                String message = intent.getStringExtra("get_msg");
                if (message != null) {
                    String[] sampleTokensAgain = message.split(":");
                    List<String> myList = new ArrayList<String>(Arrays.asList(sampleTokensAgain));
                    myList.remove("");
                    Log.d("size", myList.size() + "");
                    for (int sms = 0; sms < myList.size(); sms++) {
                        if (sms == 1) {
                            String myCode = myList.get(sms);
                            String strArray[] = myCode.split(" ");
                            getCode = strArray[2];
                            Operation.saveString("GET_CODE", getCode);
                            break;
                        }
                    }
                }
            }
        };
        this.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    private void confirmRegistration() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String getMyCode = Operation.getString("GET_CODE", "");
                if (getMyCode != null) {
                    if (authorizedStatus.equalsIgnoreCase("Success") && getMyCode.equalsIgnoreCase(smsItem.code)) {
                        containerAuthorisedCode.setVisibility(View.VISIBLE);

                        String[] sampleTokensAgain = getMyCode.split("");
                        List<String> myList = new ArrayList<String>(Arrays.asList(sampleTokensAgain));
                        myList.remove("");


                        for (int n = 0; n < myList.size(); n++) {
                            String mycode = myList.get(n);
                            for (int ot = 0; ot < myOtpEditTexts.size(); ot++) {
                                OTPEditText mOtpEditText = myOtpEditTexts.get(ot);
                                if (n == ot) {
                                    mOtpEditText.setText(mycode);
                                    isMyNumber = true;
                                    break;
                                }
                            }
                        }
                        return;
                    }

                }
            }
        }, 3000);
    }

    public void checkSMSCode(View view) {

        String otpCode = "" + oetOne.getText().toString() + oetTwo.getText().toString()
                + oetThree.getText().toString() + oetFour.getText().toString()
                + oetFive.getText().toString() + oetSix.getText().toString();
        if (otpCode.equalsIgnoreCase(smsItem.code)) {
            Intent intent = new Intent(SMSAuthActivity.this, UserActivity.class);
            intent.putExtra("RIDER-PHONE-NUMBER", myPhoneNumber);
            startActivity(intent);
        }
    }

    private void showProgress() {
        progress_bar.setVisibility(View.VISIBLE);
        // Disable all buttons while progress indicator shows.
        //  setViewsEnabled(false, R.id.cancel_button);
    }

    /**
     * Hide progress spinner and enable buttons
     **/
    private void hideProgress() {
        progress_bar.setVisibility(View.INVISIBLE);

        // Enable buttons once progress indicator is hidden.
        //   setViewsEnabled(true, R.id.cancel_button);
    }

    /**
     * Enable or disable multiple views
     **/
    private void setViewsEnabled(boolean enabled, int... ids) {
        for (int id : ids) {
            findViewById(id).setEnabled(enabled);
        }
    }
}