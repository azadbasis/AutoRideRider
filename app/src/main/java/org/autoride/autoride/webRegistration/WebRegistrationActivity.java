package org.autoride.autoride.webRegistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.autoride.autoride.R;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.notifications.commons.Common;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WebRegistrationActivity extends AppCompatActivity {

    private WebView webViewRegistration;

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
        setContentView(R.layout.web_registration);

        ProgressDialog pDialog = new ProgressDialog(WebRegistrationActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activiy_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_rider_registration);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WebRegistrationActivity.this, RiderWelcomeActivity.class));
                finish();
            }
        });

        Common.startWaitingDialog(pDialog);

        if (!AutoRideRiderApps.isNetworkAvailable()) {
            showSnackBar();
        } else {
            webViewRegistration = findViewById(R.id.webViewRegistration);
            webViewRegistration.setWebViewClient(new WebViewClient());

            webViewRegistration.getSettings().setJavaScriptEnabled(true);
            webViewRegistration.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            webViewRegistration.loadUrl("https://www.autoride.org/extra/registration");
//            webViewRegistration.loadUrl("https://www.autoride.org/vehicle/registration");
            Common.stopWaitingDialog(pDialog);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webViewRegistration.canGoBack()) {
                        webViewRegistration.goBack();
                        startActivity(new Intent(WebRegistrationActivity.this, RiderWelcomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(WebRegistrationActivity.this, RiderWelcomeActivity.class));
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.mybookinglayout), "No internet connection!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}