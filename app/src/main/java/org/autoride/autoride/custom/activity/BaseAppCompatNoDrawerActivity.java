package org.autoride.autoride.custom.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import org.autoride.autoride.R;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.configs.AppsSingleton;
import org.autoride.autoride.constants.AppsConstants;

import java.io.File;

import static org.autoride.autoride.applications.AutoRideRiderApps.AUTH_TOKEN_NOT_AVAILABLE;
import static org.autoride.autoride.applications.AutoRideRiderApps.NETWORK_NOT_AVAILABLE;
import static org.autoride.autoride.applications.AutoRideRiderApps.SERVER_CONNECTION_AVAILABLE;


public class BaseAppCompatNoDrawerActivity extends BaseActivity implements AppsConstants {

  //  protected FileOp fop = new FileOp(this);
    protected CoordinatorLayout lytBase;
    protected FrameLayout lytContent;
    public SwipeRefreshLayout swipeView;
    protected CoordinatorLayout coordinatorLayout;
    protected Toolbar toolbar;
    protected View lytProgress;
    private ProgressBar progressBase;
    private View lytMessage;
    private TextView txtMessage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                isBluetoothEnableRequestShown = false;
            } else {
                isBluetoothEnableRequestShown = false;
                Snackbar.make(coordinatorLayout, "Bluetooth Not Enabled", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", snackBarDismissOnClickListener).show();
            }
        }*/
    }

    public void initViewBase() {

        initBase();
        //	getActionBar().setHomeButtonEnabled(true);

        //FacebookSdk.sdkInitialize(this.getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbarProfileSetting);
        setSupportActionBar(toolbar);
        lytContent = (FrameLayout) findViewById(R.id.lyt_contents_base_appcompat_no_drawer);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout_base_appcompat_no_drawer);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }*/

        lytProgress = (View) findViewById(R.id.lyt_progress_base);
        progressBase = (ProgressBar) findViewById(R.id.progress_base_appcompat_no_drawer);

        lytMessage = (View) findViewById(R.id.lyt_default_message_base_appcompat_no_drawer);
        txtMessage = (TextView) findViewById(R.id.txt_default_message_base_appcompat_no_drawer);

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_base_appcompat_no_drawer);
        swipeView.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        swipeView.setEnabled(false);
        swipeView.setProgressViewOffset(false, 0,
                (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()) + mActionBarHeight));

  /*      if (android.os.Build.VERSION.SDK_INT >= 21) {
            swipeView.setPadding(0, (int) (getStatusBarHeight()), 0, 0);
        }*/

        setProgressScreenVisibility(false, false);
        setMessageScreenVisibility(false, "Nothing To Show");
    }

    @Override
    public void setContentView(final int layoutResID) {
        lytBase = (CoordinatorLayout) getLayoutInflater().inflate(R.layout.layout_base_appcompat_no_drawer, null);
        lytContent = (FrameLayout) lytBase.findViewById(R.id.lyt_contents_base_appcompat_no_drawer);
        getLayoutInflater().inflate(layoutResID, lytContent, true);
        super.setContentView(lytBase);
        initViewBase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                lytContent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                //mVibrator.vibrate(25);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* int getServerConnectionAvailableStatus(boolean isSnackbarEnabled) {
        if (Config.getInstance().getAuthToken() == null || Config.getInstance().getAuthToken().equals("")) {
            if (AutoRideRiderApps.checkForToken() && !Config.getInstance().getAuthToken().equals("")) {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    return SERVER_CONNECTION_AVAILABLE;
                } else {
                    if (isSnackbarEnabled)
                        Snackbar.make(coordinatorLayout, AppConstants.NO_NETWORK_AVAILABLE, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    return NETWORK_NOT_AVAILABLE;
                }
            } else {
                if (isSnackbarEnabled)
                    Snackbar.make(coordinatorLayout, AppConstants.WEB_ERROR_MSG, Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", snackBarDismissOnClickListener).show();
                return AutoRideRiderApps.AUTH_TOKEN_NOT_AVAILABLE;
            }
        } else {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                return SERVER_CONNECTION_AVAILABLE;
            } else {
                if (isSnackbarEnabled)
                    Snackbar.make(coordinatorLayout, AppConstants.NO_NETWORK_AVAILABLE, Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", snackBarDismissOnClickListener).show();
                return NETWORK_NOT_AVAILABLE;
            }
        }
    }*/

    protected void setMessageScreenVisibility(boolean isScreenVisible, String message) {
        if (isScreenVisible) {
            lytMessage.setVisibility(View.VISIBLE);
            txtMessage.setText(message);
        } else
            lytMessage.setVisibility(View.GONE);
    }

    protected void setProgressScreenVisibility(boolean isScreenVisible, boolean isProgressVisible) {
        if (isScreenVisible) {
            lytProgress.setVisibility(View.VISIBLE);
            if (isProgressVisible) {
                progressBase.setVisibility(View.VISIBLE);
            } else {
                progressBase.setVisibility(View.GONE);
            }
        } else {
            lytProgress.setVisibility(View.GONE);
        }
    }


    public void onToolbarBackClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        onBackPressed();
    }

    public void onToolbarSearchClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

/*        startActivity(new Intent(this, SearchActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));*/
    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    public static class BaseAppCompatActivity extends BaseActivity {

        private static final String TAG = "BaseAppCompatActivity";

        static final int HOME_ACTIVITY = 1;

        private static int CURRENT_ACTIVITY = 1;

        FrameLayout lytContent;
        //protected FrameLayout leftDrawer;
        NavigationView leftDrawer;
        //	protected FrameLayout rightDrawer;
        DrawerLayout drawerLayout;
        ActionBarDrawerToggle mDrawerToggle;
        //protected  RelativeLayout lytContents;
        protected View lytActivity;

        final Handler mHandler = new Handler();

        protected SwipeRefreshLayout swipeView;

        protected CoordinatorLayout coordinatorLayout;

        protected Toolbar toolbar;

        private MenuItem menuProgress;
        private View lytDrawer;
        private EditText etxtEmail;
        private EditText etxtPassword;
        private View lytDrawerHeader;
        private ImageView ivUserDP;
        private View lytProgress;
        private ProgressBar progressBase;
        private ImageView ivProfilePhoto;
        private TextView txtName;
        private TextView txtEmail;
        private View lytMessage;
        private TextView txtMessage;

        /*	@Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //  setContentView(R.layout.layout_base);

            getActionBar().setDisplayShowHomeEnabled(false);
                getActionBar().setDisplayShowTitleEnabled(false);
        }*/

        private void initViewBase() {

            initBase();

            //        if (isGetLocationEnabled) {
//            initLocationPermissionCheck();
            initReadWritePermissionCheck();
            //	getActionBar().setHomeButtonEnabled(true);

            //        }
            //        FacebookSdk.sdkInitialize(this.getApplicationContext());

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //leftDrawer = (FrameLayout) findViewById(R.id.leftDrawer);
            leftDrawer = (NavigationView) findViewById(R.id.navigation_view_base_appcompat);
            //	rightDrawer = (FrameLayout)findViewById(R.id.rightDrawer);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_appcompat);
            lytContent = (FrameLayout) findViewById(R.id.lyt_contents_appcompat);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout_base_appcompat);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //            toolbar.setPadding(0, AutoRideRiderApps.getStatusBarHeight(getApplicationContext()), 0, 0);
                leftDrawer.setPadding(0, 0, 0, 0);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }

            lytProgress = (View) findViewById(R.id.lyt_progress_base_appcompat);
            progressBase = (ProgressBar) findViewById(R.id.progress_base_appcompat);

            lytMessage = (View) findViewById(R.id.lyt_default_message_base_appcompat);
            txtMessage = (TextView) findViewById(R.id.txt_default_message_base_appcompat);

            swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_base_appcompat);
            swipeView.setColorSchemeResources(R.color.holo_blue_dark, R.color.holo_blue_light,
                    R.color.holo_green_light, R.color.holo_orange_light);
            swipeView.setEnabled(false);
            swipeView.setProgressViewOffset(false, 0,
                    (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()) + mActionBarHeight));

            //	llBottomBarActionPopup=(LinearLayout)findViewById(R.id.ll_bottombar_popmenu);

            FrameLayout.LayoutParams param1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            FrameLayout.LayoutParams param2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            param1.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, r.getDisplayMetrics());
            param2.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 155, r.getDisplayMetrics());

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    /*				if(view == rightDrawer) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, leftDrawer);
                    } else if(view == leftDrawer) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, rightDrawer);
                    }*/
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    /*				if(drawerView == rightDrawer) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, leftDrawer);
                    } else if(drawerView == leftDrawer) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, rightDrawer);
                    }*/
                }
            };

            mDrawerToggle.setDrawerIndicatorEnabled(true);
            // Set the drawer toggle as the DrawerListener
            drawerLayout.addDrawerListener(mDrawerToggle);
            //drawerLayout.setDrawerShadow(R.drawable.shadow,

            //        AutoRideRiderApps.checkForToken();
            /*		Thread.setDefaultUncaughtExceptionHandler(this);
             */
            //	setupRightDrawer();
            setLeftDrawer();
            /*		setData();*/

            /*		mHandler.postDelayed(notiNotiTask, 1000);
            mHandler.postDelayed(notiReqTask, 2000);*/

            setProgressScreenVisibility(false, false);
            setMessageScreenVisibility(false, "Nothing To Show");

            mHandler.postDelayed(periodicTask, 3000);
        }

        public void onToolbarHomeClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            if (!drawerLayout.isDrawerOpen(leftDrawer)) {
                drawerLayout.openDrawer(leftDrawer);
            } else {
                drawerLayout.closeDrawer(leftDrawer);
            }
        }

        public void onToolbarSearchClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

    /*        startActivity(new Intent(this, SearchActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));*/
        }

        private void initLocationPermissionCheck() {
            if (CURRENT_ACTIVITY == HOME_ACTIVITY) {
//                if (!checkForLocationPermissions())
//                    getLocationPermissions();
//                checkLocationSettingsStatus();
            }
        }

        private void initReadWritePermissionCheck() {
            if (CURRENT_ACTIVITY == HOME_ACTIVITY) {
                if (!checkForReadWritePermissions())
                    getReadWritePermissions();
            }
        }

        private final Runnable periodicTask = new Runnable() {
            @Override
            public void run() {
                if (CURRENT_ACTIVITY != HOME_ACTIVITY) {
                    //getCurrentLocation();
                }
            }
        };

        @Override
        public void onPause() {
            super.onPause();

        }

    /*    @Override
        protected void onRestart() {
    //        getWindow().setBackgroundDrawableResource(getWindowBackgroudResourse());
            super.onRestart();
        }*/

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

        @Override
        protected void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            mDrawerToggle.syncState();
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            mDrawerToggle.onConfigurationChanged(newConfig);
        }

        protected void onResume() {

            super.onResume();

            //if(Config.getInstance().getAuthToken()==null||Config.getInstance().getAuthToken().equals(""))
            //        leftDrawer.setCheckedItem(getSelectedNavigationDrawerItem(CURRENT_ACTIVITY));

    /*        if (!Config.getInstance().isLoggedIn() || Config.getInstance().getAuthToken() == null
                    || Config.getInstance().getAuthToken().equals("")) {
                removeLoginDrawerFromNavigationView();
                addLoginDrawerToNavigationView();
            } else {
                removeLoginDrawerFromNavigationView();
            }*/

            if (AppsSingleton.getInstance().getAccessToken() != null && !AppsSingleton.getInstance().getAccessToken().equals("")) {
                //            if (getServerConnectionAvailableStatus(false) == SERVER_CONNECTION_AVAILABLE) {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    // fetchUserInfo();
                }
            }
        }

       /* private void fetchUserInfo() {

            HashMap<String, String> urlParams = new HashMap<>();

            urlParams.put("auth_token", Config.getInstance().getAuthToken());

            DataManager.fetchUserInfo(urlParams, Config.getInstance().getUserID(), new UserInfoListener() {
                @Override
                public void onLoadCompleted(RiderBean riderBean) {
                    System.out.println("Successfull  : RiderBean : " + riderBean);
                    BaseAppCompatActivity.this.riderBean = riderBean;


                    populateUserInfo(riderBean);
                    AutoRideRiderApps.saveToken();

                }

                @Override
                public void onLoadFailed(String errorMsg) {
                   *//* Snackbar.make(coordinatorLayout, errorMsg, Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", snackBarDismissOnClickListener).show();*//*

                }
            });

        }*/

        private void populateUserInfo(String riderBean) {
            try {
//                AppsSingleton.getInstance().setName(riderBean.getName());
//                AppsSingleton.getInstance().setProfilePhoto(riderBean.getProfilePhoto());
//                AppsSingleton.getInstance().setEmail(riderBean.getEmail());
//                AppsSingleton.getInstance().setAddHome(riderBean.getAddHome());
//                AppsSingleton.getInstance().setAddWork(riderBean.getAddWork());
//                AppsSingleton.getInstance().setHomeLatitude(riderBean.getHomeLatitude());
//                AppsSingleton.getInstance().setHomeLongitude(riderBean.getHomeLongitude());
//                AppsSingleton.getInstance().setWorkLatitude(riderBean.getWorkLatitude());
//                AppsSingleton.getInstance().setWorkLongitude(riderBean.getWorkLongitude());

                //  setRider();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setUser() {

            txtName.setText(AppsSingleton.getInstance().getFullName());
            // txtEmail.setText(AppsSingleton.getInstance().getEmail());

            Glide.with(getApplicationContext())
                    .load(AppsSingleton.getInstance().getProfilePhoto())
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_profile_photo_default)
                            .fallback(R.drawable.ic_profile_photo_default)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .circleCrop())
                    .into(ivProfilePhoto);
        }

        @Override
        public void setContentView(final int layoutResID) {
            DrawerLayout lytBase = (DrawerLayout) getLayoutInflater().inflate(R.layout.layout_base_appcompat, null);
            lytContent = (FrameLayout) lytBase.findViewById(R.id.lyt_contents_appcompat);
            getLayoutInflater().inflate(layoutResID, lytContent, true);
            super.setContentView(lytBase);
            initViewBase();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_base, menu);
    /*        menuProgress = menu.findItem(R.id.action_progress);
            // Extract the action-view from the menu item
            ProgressBar progressActionBar = (ProgressBar) MenuItemCompat.getActionView(menuProgress);
            progressActionBar.setIndeterminate(true);*/
            /*		menuNotiItem = menu.findItem(R.id.action_notifications);
            MenuItemCompat.setActionView(menuNotiItem, R.layout.actionbar_notification_icon);
            menuNoti = (View) MenuItemCompat.getActionView(menuNotiItem);
            txtNoti = (TextView) menuNoti.findViewById(R.id.txt_action_notification);
             *//*		menuNoti.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    //mVibrator.vibrate(25);
                    if(isNotificationVisible){
                        isNotificationVisible=false;
                        notificationDrawer.setVisibility(View.GONE);
                    }else{
                        isNotificationVisible=true;
                        notificationDrawer.setVisibility(View.VISIBLE);
                    }
                }
            });*/
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case android.R.id.home:
                    drawerLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    //mVibrator.vibrate(25);
                    if (drawerLayout.isDrawerOpen(leftDrawer))
                        drawerLayout.closeDrawer(leftDrawer);
                /*			else if(drawerLayout.isDrawerOpen(rightDrawer))
                    drawerLayout.closeDrawer(rightDrawer);*/
                    else if (!drawerLayout.isDrawerOpen(leftDrawer))
                        drawerLayout.openDrawer(leftDrawer);
                    return true;
    /*            case R.id.action_search:
                    drawerLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    //mVibrator.vibrate(25);

                    startActivity(new Intent(BaseAppCompatActivity.this, SearchActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    return true;*/
            }
            return super.onOptionsItemSelected(item);
        }

        private void setLeftDrawer() {

            LayoutInflater inflater = getLayoutInflater();
            lytDrawer = inflater.inflate(R.layout.layout_drawer, null);

            ivProfilePhoto = (ImageView) lytDrawer.findViewById(R.id.iv_drawer_profile_photo);
            txtName = (TextView) lytDrawer.findViewById(R.id.txt_drawer_name);
            txtEmail = (TextView) lytDrawer.findViewById(R.id.txt_drawer_email);

            //        leftDrawer.addView(lytDrawer);

    /*        ivUserDP = (ImageView) lytDrawer.findViewById(R.id.iv_drawer_profile_photo);
            txtName = (CustomTextView) lytDrawer.findViewById(R.id.txt_drawer_name);
            txtPhone = (CustomTextView) lytDrawer.findViewById(R.id.txt_drawer_phone);
            txtEmail = (CustomTextView) lytDrawer.findViewById(R.id.txt_drawer_email);*/

            leftDrawer.addView(lytDrawer);
        }

        public void onDrawerEditClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            //        //mVibrator.vibrate(25);
            drawerLayout.closeDrawers();
    /*        startActivity(new Intent(BaseAppCompatActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));*/
            //        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            //        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }

        public void onDrawerBackupRestoreClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            //        //mVibrator.vibrate(25);
            drawerLayout.closeDrawers();
    /*        startActivity(new Intent(BaseAppCompatActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));*/
            //        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            //        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }

        public void onDrawerAboutUsClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            //        //mVibrator.vibrate(25);
            drawerLayout.closeDrawers();
    /*        startActivity(new Intent(BaseAppCompatActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));*/
            //        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            //        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }

        public void onDrawerHelpClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            //        //mVibrator.vibrate(25);
            drawerLayout.closeDrawers();
    /*        startActivity(new Intent(BaseAppCompatActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));*/
            //        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            //        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }

        public void onDrawerFeedbackClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            //        //mVibrator.vibrate(25);
            drawerLayout.closeDrawers();
    /*        startActivity(new Intent(BaseAppCompatActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));*/
            //        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            //        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }

        public void onDrawerSettingsClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            //        //mVibrator.vibrate(25);
            drawerLayout.closeDrawers();
    /*        startActivity(new Intent(BaseAppCompatActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));*/
            //        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            //        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }


        private void clearApplicationData() {
            File cache = getApplication().getFilesDir();
            File appDir = new File(cache.getParent());
            if (appDir.exists()) {
                String[] children = appDir.list();
                for (String s : children) {
                    //		if (!s.equals("lib")) {
                    (new File(appDir, s)).delete();
                    //		}
                }
            }
        }

        /*
        private void saveToken() {
            System.out.println("SAVE STARTED");
            SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_NAME_SESSION, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_TOKEN, Config.getInstance().getAuthToken());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_USERID, Config.getInstance().getUserID());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_EMAIL, Config.getInstance().getEmail());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_PASSWORD, Config.getInstance().getPassword());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_GENDER, Config.getInstance().getGender());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_DOB, Config.getInstance().getDOB());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_FBACCESSTOKEN, Config.getInstance().getFacebookAccessToken());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_GOOGLEPLUSACCESSTOKEN, Config.getInstance().getGoogleAccessToken());
            editor.putString(AppConstants.PREFERENCE_KEY_SESSION_GCM_ID, Config.getInstance().getGCMID());
            editor.commit();
            fop.writeHash();
            System.out.println("SAVE COMPLETE");
        }
    */

        int getServerConnectionAvailableStatus(boolean isSnackbarEnabled) {
            if (AppsSingleton.getInstance().getAccessToken() == null || AppsSingleton.getInstance().getAccessToken().equals("")) {
                if (AutoRideRiderApps.checkSession() && !AppsSingleton.getInstance().getAccessToken().equals("")) {
                    // if (AppsSingleton.getInstance().isPhoneVerified()) {
                    if (AutoRideRiderApps.isNetworkAvailable()) {
                        return SERVER_CONNECTION_AVAILABLE;
                    } else {
                        if (isSnackbarEnabled)
                            Snackbar.make(coordinatorLayout, NO_NETWORK_AVAILABLE, Snackbar.LENGTH_LONG)
                                    .setAction("Dismiss", snackBarDismissOnClickListener).show();
                        return NETWORK_NOT_AVAILABLE;
                    }
//                    } else {
//                        //                    startActivity(new Intent(this, VerificationActivity.class));
//                        return AUTH_TOKEN_NOT_AVAILABLE;
//                    }
                } else {
                    if (isSnackbarEnabled)
                        Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    return AUTH_TOKEN_NOT_AVAILABLE;
                }
            } else {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    return SERVER_CONNECTION_AVAILABLE;
                } else {
                    if (isSnackbarEnabled)
                        Snackbar.make(coordinatorLayout, NO_NETWORK_AVAILABLE, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    return NETWORK_NOT_AVAILABLE;
                }
            }
        }

        protected void setMessageScreenVisibility(boolean isScreenVisible, String message) {
            if (isScreenVisible) {
                lytMessage.setVisibility(View.VISIBLE);
                txtMessage.setText(message);
            } else
                lytMessage.setVisibility(View.GONE);
        }

        protected void setProgressScreenVisibility(boolean isScreenVisible, boolean isProgressVisible) {
            if (isScreenVisible) {
                lytProgress.setVisibility(View.VISIBLE);
                if (isProgressVisible) {
                    progressBase.setVisibility(View.VISIBLE);
                } else {
                    progressBase.setVisibility(View.GONE);
                }
            } else {
                lytProgress.setVisibility(View.GONE);
            }
        }

        void setCurrentActivity(int currentActivity) {
            CURRENT_ACTIVITY = currentActivity;
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (drawerLayout.isDrawerOpen(leftDrawer)) {
                    drawerLayout.closeDrawer(leftDrawer);
                }
                /*			else if(drawerLayout.isDrawerOpen(rightDrawer)){
                    drawerLayout.closeDrawer(rightDrawer);
                }*/
                else {
                    onBackPressed();
                    //                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                openOptionsMenu();
            }
            return true;
        }

      /*  private void getLocationName() {
            //        swipeView.setRefreshing(true);

            String currentLatitude = Config.getInstance().getCurrentLatitude();
            String currentLongitude = Config.getInstance().getCurrentLongitude();

            System.out.println("Current Location : " + currentLatitude + "," + currentLongitude);

            HashMap<String, String> urlParams = new HashMap<>();
            //	postData.put("uid", id);
            urlParams.put("latlng", currentLatitude + "," + currentLongitude);
            urlParams.put("sensor", "true");

            LocationNameTask locationNameTask = new LocationNameTask(urlParams);
            locationNameTask.setLocationNameTaskListener(new LocationNameTask.LocationNameTaskListener() {

                @Override
                public void dataDownloadedSuccessfully(String address) {
                    //	System.out.println(landingBean.getStatus());
                    if (null != address) {
                        System.out.println("Location Name Retrieved : " + address);
                        Config.getInstance().setCurrentLocation(address);
                        *//*					txtLocation.setText(address);
                        Toast.makeText(CreateActivity.this,"Location Name Retrieved : "+address, Toast.LENGTH_SHORT).show();
                         *//*
                    }
                }

                @Override
                public void dataDownloadFailed() {

                }
            });
            locationNameTask.execute();
        }*/

        /*private void getLocationName(String latitude, String longitude) {

            //        swipeView.setRefreshing(true);

            *//*String currentLatitude = Config.getInstance().getCurrentLatitude();
            String currentLongitude = Config.getInstance().getCurrentLongitude();

            System.out.println("Current Location : " + currentLatitude + "," + currentLongitude);*//*

            HashMap<String, String> urlParams = new HashMap<>();
            //	postData.put("uid", id);
            urlParams.put("latlng", latitude + "," + longitude);
            urlParams.put("sensor", "true");
            urlParams.put("key", getString(R.string.browser_api_key));

            LocationNameTask locationNameTask = new LocationNameTask(urlParams);
            locationNameTask.setLocationNameTaskListener(new LocationNameTask.LocationNameTaskListener() {

                @Override
                public void dataDownloadedSuccessfully(String address) {
                    //	System.out.println(landingBean.getStatus());
                    if (null != address) {
                        System.out.println("Location Name Retrieved : " + address);
                        Config.getInstance().setCurrentLocation(address);
                        *//*					txtLocation.setText(address);
                        Toast.makeText(CreateActivity.this,"Location Name Retrieved : "+address, Toast.LENGTH_SHORT).show();
                         *//*
                    }
                }

                @Override
                public void dataDownloadFailed() {

                }
            });
            locationNameTask.execute();
        }*/

        @Override
        public void onConnectionSuspended(int arg0) {

        }

        /*public void onPromotionsClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            Intent intent = new Intent(BaseAppCompatActivity.this, PromotionActivity.class);
            startActivity(intent);
        }*/

    /*    public void onSettingsClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            Intent intent = new Intent(BaseAppCompatActivity.this, SettingsPageActivity.class);
            startActivity(intent);

        }*/

    /*    public void onEditClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            Intent intent = new Intent(BaseAppCompatActivity.this, ProfileEditActivity.class);
            intent.putExtra("bean", riderBean);
            startActivity(intent);
        }*/
    }

    public Double lastLatitude;
    public Double lastLongitude;

    public void getUserLastLocation() {
        if (AutoRideRiderApps.isLocationEnabled()) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lastLatitude = location.getLatitude();
                        lastLongitude = location.getLongitude();
                    }
                }
            });
        }
    }
}