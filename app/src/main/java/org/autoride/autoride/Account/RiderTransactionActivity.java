package org.autoride.autoride.Account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiderTransactionActivity extends AppCompatActivity {

    public static final String SEND_FRAGMENT = "sendFragment";
    public static final String REQUEST_FRAGMENT = "requestFragment";
    public static final String LOAD_FRAGMENT = "loadFragment";
    public static final String ACTIVITY_FRAGMENT = "activityFragment";

    private int mRequestCode;
    private boolean isFirstBack = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Xerox Serif Wide.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_money_transaction:
                    showActivity();
                    return true;
                case R.id.navigation_send_money:
                    sendMoney();
                    return true;
                case R.id.navigation_request_money:
                    requestMoney();
                    return true;
                case R.id.navigation_load_money:
                    loadMoney();
                    return true;
            }
            return false;
        }
    };

    private void loadMoney() {
        LoadFragment loadFragment = new LoadFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, loadFragment, LOAD_FRAGMENT)
                .addToBackStack(null)
                .commit();

    }


    private void requestMoney() {


        RequestFragment requestFragment = new RequestFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, requestFragment, REQUEST_FRAGMENT)
                .addToBackStack(null)
                .commit();

    }

    private void sendMoney() {

        Bundle arg = new Bundle();

        if (result != null) {
            arg.putString("scanResult", result);
        }

        SendFragment sendFragment = new SendFragment();
        sendFragment.setArguments(arg);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sendFragment, SEND_FRAGMENT)
                .addToBackStack(null)
                .commit();

    }

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);//disable BottomNavigationView shift mode
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SendFragment sendFragment = new SendFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .add(R.id.fragment_container, sendFragment, SEND_FRAGMENT)
                .addToBackStack(null)
                .commit();


        result = getIntent().getStringExtra("ScanResult");

        //  Toast.makeText(this, "RESULT "+result, Toast.LENGTH_SHORT).show();

    }

    private void showActivity() {

        ActivitiesFragment activitiesFragment = new ActivitiesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragment_container, activitiesFragment, ACTIVITY_FRAGMENT)
                .addToBackStack(null)
                .commit();

    }

    int k;

    @Override
    public void onBackPressed() {
        ++k; //initialise k when you first start your activity.
        if (k == 1) {
            //do whatever you want to do on first click for example:
            sendMoney();
          //  Toast.makeText(this, "Press back one more time to exit", Toast.LENGTH_LONG).show();
        } else {
            //do whatever you want to do on the click after the first for example:
            finish();
        }

    }
}
