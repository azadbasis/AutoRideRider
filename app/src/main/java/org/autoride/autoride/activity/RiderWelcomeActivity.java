package org.autoride.autoride.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import org.autoride.autoride.facebookRegistration.UserActivity;
import org.autoride.autoride.R;
import org.autoride.autoride.SMSAuth.SMSAuthActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.webRegistration.WebRegistrationActivity;

public class RiderWelcomeActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private String phoneNumberString;
    private View.OnClickListener snackBarDismissListener;
    private LinearLayout llWelcomeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_rider_welcome);

        mCallbackManager = CallbackManager.Factory.create();
        llWelcomeContainer = (LinearLayout) findViewById(R.id.ll_welcome_container);
        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                v.setVisibility(View.GONE);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        AutoRideRiderApps.logout();
    }

    public void onWelcomeRiderLogin(View view) {
        Intent intent = new Intent(RiderWelcomeActivity.this, RiderLoginActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderWelcomeActivity.this.finish();
    }

    public void onWelcomeRiderRegistration(View view) {
//        Intent intent = new Intent(RiderWelcomeActivity.this, RiderRegistrationActivity.class);
        Intent intent = new Intent(RiderWelcomeActivity.this, SMSAuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void faceBookSignIn(View view) {
        if (AutoRideRiderApps.isNetworkAvailable()) {
            if (AutoRideRiderApps.isLocationEnabled()) {
                // Handle Error
                final Intent intent = new Intent(RiderWelcomeActivity.this, AccountKitActivity.class);

                AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                        new AccountKitConfiguration.AccountKitConfigurationBuilder(
                                LoginType.PHONE,

                                AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
                // ... perform additional configuration ...
                intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
                startActivityForResult(intent, 101);
            } else {
                snackBarNoGps();
            }
        } else {
            snackBarNoInternet();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();

                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                //goToMyLoggedInActivity();

                // Surface the result to your user in an appropriate way.
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        // Get Account Kit ID
                        String accountKitId = account.getId();
                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        if (phoneNumber != null) {
                            phoneNumberString = phoneNumber.toString();

                            Intent intent1 = new Intent(RiderWelcomeActivity.this, UserActivity.class);
                            intent1.putExtra("RIDER-PHONE-NUMBER", phoneNumberString);
                            startActivity(intent1);
                            finish();
                            // Operation.saveString("RIDER-PHONE-NUMBER", phoneNumberString);
                            //  Toast.makeText(RiderWelcomeActivity.this, "phoneNumberString" + phoneNumberString, Toast.LENGTH_SHORT).show();
                        }

                        // Get email
                        String email = account.getEmail();
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        // Handle Error
                    }
                });
            }
        }
    }

    public void webRegistrationFab(View view) {
        if (AutoRideRiderApps.isNetworkAvailable()) {
            startActivity(new Intent(RiderWelcomeActivity.this, WebRegistrationActivity.class));
            finish();
        } else {
            snackBarNoInternet();
        }
    }

    private void snackBarNoInternet() {
        Snackbar snackbar = Snackbar.make(llWelcomeContainer, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void snackBarNoGps() {
        Snackbar snackbar = Snackbar.make(llWelcomeContainer, R.string.no_gps_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).setActionTextColor(Color.YELLOW);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}