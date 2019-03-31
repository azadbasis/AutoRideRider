package org.autoride.autoride.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.R;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.custom.activity.BaseAppCompatNoDrawerActivity;
import org.autoride.autoride.model.Address;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.listeners.ParserListener;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.utils.ImageEncodeReducer;
import org.autoride.autoride.constants.AppsConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiderSettingsActivity extends BaseAppCompatNoDrawerActivity implements RiderApiUrl, AppsConstants {

    private static final String TAG = "RiderSettings";
    private ViewFlipper vfRiderSettings;
    private TextView tvRFirstName, tvRLastName, tvREmail, tvRPhone, tvRHouse, tvRRoad, tvRUnit, tvRFax, tvRZipCode, tvRCity, tvRCountry;
    private EditText etRFirstName, etRLastName, etREmail, etRHouse, etRRoad, etRUnit, etRFax, etRZipCode, etRCity, etRCountry, etRCurPassword, etRPassword;
    private RiderInfo riderInfo;
    private Address riderAddress;
    private ImageView ivRiderProfilePhoto;
    private CircleImageView civRiderProfilePhoto;
    private String accessToken, rememberToken, riderId, mCurrentPhotoPath;
    private Button btnSaveFirstName, btnSaveLastName, btnSaveEmail, btnSaveAddress, btnUpdatePassword, btnSaveProfilePhoto;
    private SharedPreferences sp;
    private ProgressDialog pDialog;
    private LinearLayout llPhotoSave, llPhotoTake;
    private int CAMERA_RUNTIME_PERMISSION = 1, WRITE_EXTERNAL_PERMISSION_RUNTIME = 2;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 3, CAMERA_IMAGE_REQUEST_CODE = 4;
    private ImageCaptureModal imageCaptureModal;

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
        setContentView(R.layout.activity_rider_settings);
        setUiComponent();
    }

    private void setUiComponent() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        riderInfo = new RiderInfo();
        riderAddress = new Address();
        pDialog = new ProgressDialog(RiderSettingsActivity.this);

        vfRiderSettings = (ViewFlipper) findViewById(R.id.view_flipper_rider_settings);
        vfRiderSettings.setDisplayedChild(0);

        civRiderProfilePhoto = (CircleImageView) findViewById(R.id.civ_rider_settings__profile_photo);

        tvRFirstName = (TextView) findViewById(R.id.tv_rider_settings_first_name);
        tvRLastName = (TextView) findViewById(R.id.tv_rider_settings_last_name);
        tvREmail = (TextView) findViewById(R.id.tv_rider_settings_email);
        tvRPhone = (TextView) findViewById(R.id.tv_rider_settings_phone);
        tvRHouse = (TextView) findViewById(R.id.tv_rider_settings_house);
        tvRRoad = (TextView) findViewById(R.id.tv_rider_settings_road);
        tvRUnit = (TextView) findViewById(R.id.tv_rider_settings_unit);
        tvRFax = (TextView) findViewById(R.id.tv_rider_settings_fax);
        tvRZipCode = (TextView) findViewById(R.id.tv_rider_settings_zip_code);
        tvRCity = (TextView) findViewById(R.id.tv_rider_settings_city);
        tvRCountry = (TextView) findViewById(R.id.tv_rider_settings_country);

        ivRiderProfilePhoto = (ImageView) findViewById(R.id.iv_rider_settings_profile_photo);
        CardView cvImage = (CardView) findViewById(R.id.card_view_photo);

        llPhotoSave = (LinearLayout) findViewById(R.id.ll_photo_save);
        llPhotoTake = (LinearLayout) findViewById(R.id.ll_photo_take);

        etRFirstName = (EditText) findViewById(R.id.et_rider_settings_first_name);
        etRLastName = (EditText) findViewById(R.id.et_rider_settings_last_name);
        etREmail = (EditText) findViewById(R.id.et_rider_settings_email);
        etRHouse = (EditText) findViewById(R.id.et_rider_settings_house);
        etRRoad = (EditText) findViewById(R.id.et_rider_settings_road);
        etRUnit = (EditText) findViewById(R.id.et_rider_settings_unit);
        etRFax = (EditText) findViewById(R.id.et_rider_settings_fax);
        etRZipCode = (EditText) findViewById(R.id.et_rider_settings_zip_code);
        etRCity = (EditText) findViewById(R.id.et_rider_settings_city);
        etRCountry = (EditText) findViewById(R.id.et_rider_settings_country);
        etRCurPassword = (EditText) findViewById(R.id.et_rider_settings_current_password);
        etRPassword = (EditText) findViewById(R.id.et_rider_settings_new_password);

        btnSaveFirstName = (Button) findViewById(R.id.btn_save_first_name);
        btnSaveLastName = (Button) findViewById(R.id.btn_save_last_name);
        btnSaveEmail = (Button) findViewById(R.id.btn_save_email);
        btnSaveAddress = (Button) findViewById(R.id.btn_save_address);
        btnUpdatePassword = (Button) findViewById(R.id.btn_update_password);
        btnSaveProfilePhoto = (Button) findViewById(R.id.btn_save_profile_image);

        cvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vfRiderSettings.setInAnimation(slideRightIn);
                vfRiderSettings.setOutAnimation(slideRightOut);
                vfRiderSettings.setDisplayedChild(1);
                getSupportActionBar().setTitle("EDIT PROFILE PHOTO");
            }
        });

        LinearLayout llFirstName = (LinearLayout) findViewById(R.id.linear_layout_first_name);
        llFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(2);
                    etRFirstName.setText(tvRFirstName.getText());
                    getSupportActionBar().setTitle("EDIT FIRST NAME");
                } else {
                    snackBarNoInternet();
                }
            }
        });

        LinearLayout llLastName = (LinearLayout) findViewById(R.id.linear_layout_last_name);
        llLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(3);
                    etRLastName.setText(tvRLastName.getText());
                    getSupportActionBar().setTitle("EDIT LAST NAME");
                } else {
                    snackBarNoInternet();
                }
            }
        });

        LinearLayout llEmail = (LinearLayout) findViewById(R.id.linear_layout_email);
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(4);
                    etREmail.setText(tvREmail.getText());
                    getSupportActionBar().setTitle("EDIT EMAIL");
                } else {
                    snackBarNoInternet();
                }
            }
        });

        LinearLayout llAddress = (LinearLayout) findViewById(R.id.linear_layout_address);
        llAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(5);
                    etRHouse.setText(tvRHouse.getText());
                    etRRoad.setText(tvRRoad.getText());
                    etRUnit.setText(tvRUnit.getText());
                    etRFax.setText(tvRFax.getText());
                    etRZipCode.setText(tvRZipCode.getText());
                    etRCity.setText(tvRCity.getText());
                    etRCountry.setText(tvRCountry.getText());
                    getSupportActionBar().setTitle("EDIT ADDRESS");
                } else {
                    snackBarNoInternet();
                }
            }
        });

        LinearLayout llPassword = (LinearLayout) findViewById(R.id.linear_layout_password);
        llPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AutoRideRiderApps.isNetworkAvailable()) {
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(6);
                    getSupportActionBar().setTitle("CHANGE PASSWORD");
                } else {
                    snackBarNoInternet();
                }
            }
        });

        LinearLayout llPhone = (LinearLayout) findViewById(R.id.linear_layout_phone);
        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(coordinatorLayout, R.string.phone_edit_unable_msg, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            }
        });

        btnSaveProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUploadRiderProfilePhoto();
            }
        });

        btnSaveFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveRiderFirstName();
            }
        });

        btnSaveLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveRiderLastName();
            }
        });

        btnSaveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveRiderEmail();
            }
        });

        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveRiderAddress();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdateRiderPassword();
            }
        });

        sp = getBaseContext().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sp != null) {
            Log.i(TAG, "checkForSession " + sp.getAll());
            accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
            rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);
            riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
        }
        getRiderProfile();
    }

    // get set profile info
    private void getRiderProfile() {
        Common.startWaitingDialog(pDialog);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            performGetRiderProfile();
        } else {
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    // get set profile info
    private void performGetRiderProfile() {
        ManagerData.taskManager(GET, PROFILE_URL, getBodyJSON(null, null), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                if (riderInfo != null) {
                    setRiderProfileInfo(riderInfo);
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                if (riderInfo != null) {
                    Common.stopWaitingDialog(pDialog);
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderSettingsActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
                    } else {
                        Snackbar.make(coordinatorLayout, WEB_ERRORS_MESSAGE, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    }
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }
        });
    }

    private void setRiderProfileInfo(RiderInfo riderInfo) {

        tvRFirstName.setText(riderInfo.getFirstName().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getFirstName());
        tvRLastName.setText(riderInfo.getLastName().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getLastName());
        tvREmail.setText(riderInfo.getEmail().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getEmail());
        tvRPhone.setText(riderInfo.getPhone().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getPhone());

        tvRHouse.setText(riderInfo.getRiderAddress().getHouse().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getHouse());
        tvRRoad.setText(riderInfo.getRiderAddress().getRoad().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getRoad());
        tvRUnit.setText(riderInfo.getRiderAddress().getUnit().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getUnit());
        tvRFax.setText(riderInfo.getRiderAddress().getFax().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getFax());
        tvRZipCode.setText(riderInfo.getRiderAddress().getZipCode().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getZipCode());
        tvRCity.setText(riderInfo.getRiderAddress().getCity().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getCity());
        tvRCountry.setText(riderInfo.getRiderAddress().getCountry().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getCountry());

        Glide.with(getBaseContext())
                .load(riderInfo.getProfilePhoto())
                .apply(new RequestOptions()
                        .centerCrop()
                        .fitCenter()
                        .error(R.drawable.ic_profile_photo_default)
                        .fallback(R.drawable.ic_profile_photo_default))
                .into(civRiderProfilePhoto);

        Glide.with(getBaseContext())
                .load(riderInfo.getProfilePhoto())
                .apply(new RequestOptions()
                        .centerCrop()
                        .fitCenter())
                .into(ivRiderProfilePhoto);

        Common.stopWaitingDialog(pDialog);
    }

    public void onProfileTakePhoto(View view) {
        imageCaptureModal = new ImageCaptureModal();
        imageCaptureModal.show(getSupportFragmentManager(), TAG);
    }

    public void onImageFromGallery(View view) {
        if (ActivityCompat.checkSelfPermission(RiderSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) RiderSettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_RUNTIME);
            if (imageCaptureModal != null) {
                imageCaptureModal.dismiss();
            }
        } else {
            if (imageCaptureModal != null) {
                imageCaptureModal.dismiss();
            }
            photoSelectFromGallery();
        }
    }

    public void onImageFromCamera(View view) {
        if (ActivityCompat.checkSelfPermission(RiderSettingsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) RiderSettingsActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_RUNTIME_PERMISSION);
            if (imageCaptureModal != null) {
                imageCaptureModal.dismiss();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(RiderSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) RiderSettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_RUNTIME);
                if (imageCaptureModal != null) {
                    imageCaptureModal.dismiss();
                }
            } else {
                if (imageCaptureModal != null) {
                    imageCaptureModal.dismiss();
                }
                photoCaptureFromCamera();
            }
        }
    }

    public void onImageCancel(View view) {
        if (imageCaptureModal != null) {
            imageCaptureModal.dismiss();
        }
    }

    // first name save/update
    private void onSaveRiderFirstName() {
        Common.startWaitingDialog(pDialog);
        btnSaveFirstName.setText(R.string.text_btn_waiting);
        btnSaveFirstName.setEnabled(false);
        if (collectFirstName()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                performSetFirstName();
            } else {
                btnSaveFirstName.setText(R.string.btn_save);
                btnSaveFirstName.setEnabled(true);
                Common.stopWaitingDialog(pDialog);
                snackBarNoInternet();
            }
        }
    }

    private boolean collectFirstName() {
        riderInfo.setFirstName(etRFirstName.getText().toString());
        if (riderInfo.getFirstName() == null || riderInfo.getFirstName().equals(DOUBLE_QUOTES)) {
            btnSaveFirstName.setText(R.string.btn_save);
            btnSaveFirstName.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.first_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getFirstName().length() < 3) {
            btnSaveFirstName.setText(R.string.btn_save);
            btnSaveFirstName.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, "First Name is too small", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getFirstName().length() > 14) {
            btnSaveFirstName.setText(R.string.btn_save);
            btnSaveFirstName.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, "First Name is too long", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performSetFirstName() {
        ManagerData.taskManager(POST, SAVE_FIRST_NAME, getBodyJSON(FIRST_NAME, riderInfo.getFirstName()), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {

                btnSaveFirstName.setText(R.string.btn_save);
                btnSaveFirstName.setEnabled(true);

                if (riderInfo != null) {

                    tvRFirstName.setText(riderInfo.getFirstName());

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(FIRST_NAME, riderInfo.getFirstName());
                    editor.commit();

                    getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_rider_settings));
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(0);
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                    Common.stopWaitingDialog(pDialog);
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {

                btnSaveFirstName.setText(R.string.btn_save);
                btnSaveFirstName.setEnabled(true);
                Common.stopWaitingDialog(pDialog);

                if (riderInfo != null) {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderSettingsActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
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

    // last name save/update
    private void onSaveRiderLastName() {
        Common.startWaitingDialog(pDialog);
        btnSaveLastName.setText(R.string.text_btn_waiting);
        btnSaveLastName.setEnabled(false);
        if (collectLastName()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                performSetLastName();
            } else {
                btnSaveLastName.setText(R.string.btn_save);
                btnSaveLastName.setEnabled(true);
                Common.stopWaitingDialog(pDialog);
                snackBarNoInternet();
            }
        }
    }

    private boolean collectLastName() {
        riderInfo.setLastName(etRLastName.getText().toString());
        if (riderInfo.getLastName() == null || riderInfo.getLastName().equals(DOUBLE_QUOTES)) {
            btnSaveLastName.setText(R.string.btn_save);
            btnSaveLastName.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.last_name_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getLastName().length() < 3) {
            btnSaveLastName.setText(R.string.btn_save);
            btnSaveLastName.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, "Last Name is too small", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getLastName().length() > 14) {
            btnSaveLastName.setText(R.string.btn_save);
            btnSaveLastName.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, "Last Name is too long", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performSetLastName() {
        ManagerData.taskManager(POST, SAVE_LAST_NAME, getBodyJSON(LAST_NAME, riderInfo.getLastName()), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {

                btnSaveLastName.setText(R.string.btn_save);
                btnSaveLastName.setEnabled(true);

                if (riderInfo != null) {

                    tvRLastName.setText(riderInfo.getLastName());

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(LAST_NAME, riderInfo.getLastName());
                    editor.commit();

                    getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_rider_settings));
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(0);
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                    Common.stopWaitingDialog(pDialog);
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {

                btnSaveLastName.setText(R.string.btn_save);
                btnSaveLastName.setEnabled(true);
                Common.stopWaitingDialog(pDialog);

                if (riderInfo != null) {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderSettingsActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
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

    // email save/update
    private void onSaveRiderEmail() {
        Common.startWaitingDialog(pDialog);
        btnSaveEmail.setText(R.string.text_btn_waiting);
        btnSaveEmail.setEnabled(false);
        if (collectEmail()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                performSetEmail();
            } else {
                btnSaveEmail.setText(R.string.btn_save);
                btnSaveEmail.setEnabled(true);
                Common.stopWaitingDialog(pDialog);
                snackBarNoInternet();
            }
        }
    }

    private boolean collectEmail() {
        riderInfo.setEmail(etREmail.getText().toString());
        if (riderInfo.getEmail() == null || riderInfo.getEmail().equals(DOUBLE_QUOTES)) {
            btnSaveEmail.setText(R.string.btn_save);
            btnSaveEmail.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.message_email_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(riderInfo.getEmail()).matches()) {
            btnSaveEmail.setText(R.string.btn_save);
            btnSaveEmail.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.message_enter_a_valid_email_address, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performSetEmail() {
        ManagerData.taskManager(POST, SAVE_EMAIL, getBodyJSON(EMAIL, riderInfo.getEmail()), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {

                btnSaveEmail.setText(R.string.btn_save);
                btnSaveEmail.setEnabled(true);

                if (riderInfo != null) {

                    tvREmail.setText(riderInfo.getEmail());
                    getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_rider_settings));
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(0);
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                    Common.stopWaitingDialog(pDialog);
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {

                btnSaveEmail.setText(R.string.btn_save);
                btnSaveEmail.setEnabled(true);
                Common.stopWaitingDialog(pDialog);

                if (riderInfo != null) {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderSettingsActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
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

    // address save/update
    private void onSaveRiderAddress() {
        Common.startWaitingDialog(pDialog);
        btnSaveAddress.setText(R.string.text_btn_waiting);
        btnSaveAddress.setEnabled(false);
        if (collectAddress()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                performSetAddress();
            } else {
                btnSaveAddress.setText(R.string.btn_save);
                btnSaveAddress.setEnabled(true);
                Common.stopWaitingDialog(pDialog);
                snackBarNoInternet();
            }
        }
    }

    private boolean collectAddress() {

        riderAddress.setHouse(etRHouse.getText().toString());
        riderAddress.setRoad(etRRoad.getText().toString());
        riderAddress.setUnit(etRUnit.getText().toString());
        riderAddress.setFax(etRFax.getText().toString());
        riderAddress.setZipCode(etRZipCode.getText().toString());
        riderAddress.setCity(etRCity.getText().toString());
        riderAddress.setCountry(etRCountry.getText().toString());

        if (riderAddress.getHouse().length() == 0 ^
                riderAddress.getRoad().length() == 0 ^
                riderAddress.getUnit().length() == 0 ^
                riderAddress.getFax().length() == 0 ^
                riderAddress.getZipCode().length() == 0 ^
                riderAddress.getCity().length() == 0 ^
                riderAddress.getCountry().length() == 0) {

            btnSaveAddress.setText(R.string.btn_save);
            btnSaveAddress.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.address_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performSetAddress() {
        ManagerData.taskManager(POST, SAVE_ADDRESS, getAddressBodyJSON(), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {

                btnSaveAddress.setText(R.string.btn_save);
                btnSaveAddress.setEnabled(true);

                if (riderInfo.getRiderAddress() != null) {

                    tvRHouse.setText(riderInfo.getRiderAddress().getHouse().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getHouse());
                    tvRRoad.setText(riderInfo.getRiderAddress().getRoad().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getRoad());
                    tvRUnit.setText(riderInfo.getRiderAddress().getUnit().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getUnit());
                    tvRFax.setText(riderInfo.getRiderAddress().getFax().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getFax());
                    tvRZipCode.setText(riderInfo.getRiderAddress().getZipCode().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getZipCode());
                    tvRCity.setText(riderInfo.getRiderAddress().getCity().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getCity());
                    tvRCountry.setText(riderInfo.getRiderAddress().getCountry().equals(NULLS) ? DOUBLE_QUOTES : riderInfo.getRiderAddress().getCountry());

                    getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_rider_settings));
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(0);
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                    Common.stopWaitingDialog(pDialog);
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {

                btnSaveAddress.setText(R.string.btn_save);
                btnSaveAddress.setEnabled(true);
                Common.stopWaitingDialog(pDialog);

                if (riderInfo.getRiderAddress() != null) {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderSettingsActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
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

    // password update
    private void onUpdateRiderPassword() {
        Common.startWaitingDialog(pDialog);
        btnUpdatePassword.setText(R.string.text_btn_waiting);
        btnUpdatePassword.setEnabled(false);
        if (collectPassword()) {
            if (AutoRideRiderApps.isNetworkAvailable()) {
                performUpdatePassword();
            } else {
                btnUpdatePassword.setText(R.string.change_pass);
                btnUpdatePassword.setEnabled(true);
                Common.stopWaitingDialog(pDialog);
                snackBarNoInternet();
            }
        }
    }

    private boolean collectPassword() {
        riderInfo.setPassword(etRPassword.getText().toString());
        riderInfo.setCurrentPassword(etRCurPassword.getText().toString());
        if (riderInfo.getCurrentPassword() == null || riderInfo.getCurrentPassword().equals(DOUBLE_QUOTES)) {
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.current_pass, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getPassword() == null || riderInfo.getPassword().equals(DOUBLE_QUOTES)) {
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.new_pass, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getPassword().length() < 6) {
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.new_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        } else if (riderInfo.getPassword().length() > 20) {
            btnUpdatePassword.setText(R.string.change_pass);
            btnUpdatePassword.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            Snackbar.make(coordinatorLayout, R.string.new_password_minimum_character, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        return true;
    }

    private void performUpdatePassword() {
        ManagerData.taskManager(POST, UPDATE_PASSWORD_URL, getPasswordBodyJSON(), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {

                btnUpdatePassword.setText(R.string.change_pass);
                btnUpdatePassword.setEnabled(true);
                Common.stopWaitingDialog(pDialog);

                if (riderInfo != null) {
                    Toast.makeText(getBaseContext(), riderInfo.getWebMessage(), Toast.LENGTH_SHORT).show();
                    logOutHere();
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {

                btnUpdatePassword.setText(R.string.change_pass);
                btnUpdatePassword.setEnabled(true);
                Common.stopWaitingDialog(pDialog);

                if (riderInfo != null) {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderSettingsActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
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

    // body and header json
    private JSONObject getBodyJSON(String key, String value) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(USER_ID, riderId);
            if (key != null && value != null) {
                postBody.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getPasswordBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(USER_ID, riderId);
            postBody.put(CUR_PASS, riderInfo.getCurrentPassword());
            postBody.put(NEW_PASS, riderInfo.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getAddressBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(USER_ID, riderId);
            postBody.put(HOUSE, riderAddress.getHouse());
            postBody.put(ROAD, riderAddress.getRoad());
            postBody.put(UNIT, riderAddress.getUnit());
            postBody.put(FAX, riderAddress.getFax());
            postBody.put(ZIP_CODE, riderAddress.getZipCode());
            postBody.put(CITY, riderAddress.getCity());
            postBody.put(COUNTRY, riderAddress.getCountry());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private JSONObject getHeaderJSON() {
        JSONObject postHeader = new JSONObject();
        try {
            postHeader.put(ACCESS_TOKENS, accessToken);
            postHeader.put(REMEMBER_TOKEN, rememberToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postHeader;
    }

    // image get set for view/save
    public void onImageRetake(View view) {
        imageCaptureModal = new ImageCaptureModal();
        imageCaptureModal.show(getSupportFragmentManager(), TAG);
    }

    private void photoSelectFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_IMAGE_REQUEST_CODE);
    }

    private void photoCaptureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "org.autoride.autoride.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST_CODE);
            } else {
                Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mCurrentPhotoPath = ImageEncodeReducer.getRealPathFromURI(data.getData(), RiderSettingsActivity.this);
            showSelectedImage();
        } else if (requestCode == CAMERA_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {  // never check data null here
            showSelectedImage();
        }
    }

    private void showSelectedImage() {
        ivRiderProfilePhoto.setImageBitmap(ImageEncodeReducer.getBitmapImage(mCurrentPhotoPath));
        llPhotoTake.setVisibility(View.GONE);
        llPhotoSave.setVisibility(View.VISIBLE);
        vfRiderSettings.setInAnimation(slideRightIn);
        vfRiderSettings.setOutAnimation(slideRightOut);
        vfRiderSettings.setDisplayedChild(1);
    }

    // profile photo upload
    private void onUploadRiderProfilePhoto() {
        Common.startWaitingDialog(pDialog);
        btnSaveProfilePhoto.setText(R.string.text_btn_waiting);
        btnSaveProfilePhoto.setEnabled(false);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            performUploadProfilePhoto();
        } else {
            btnSaveProfilePhoto.setText(R.string.btn_save);
            btnSaveProfilePhoto.setEnabled(true);
            Common.stopWaitingDialog(pDialog);
            snackBarNoInternet();
        }
    }

    private void performUploadProfilePhoto() {
        ManagerData.taskManager(POST, UPLOAD_PROFILE_PHOTO_URL, getBodyJSON(PROFILE_PICTURE, ImageEncodeReducer.compressImage(mCurrentPhotoPath)), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                btnSaveProfilePhoto.setText(R.string.btn_save);
                btnSaveProfilePhoto.setEnabled(true);
                if (riderInfo != null) {
                    String url = riderInfo.getProfilePhoto();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(PROFILE_PHOTO, url);
                    editor.commit();

                    Glide.with(getBaseContext())
                            .load(url)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .fitCenter())
                            .into(civRiderProfilePhoto);

                    Glide.with(getBaseContext())
                            .load(url)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .fitCenter())
                            .into(ivRiderProfilePhoto);

                    llPhotoTake.setVisibility(View.VISIBLE);
                    llPhotoSave.setVisibility(View.GONE);

                    getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_rider_settings));
                    vfRiderSettings.setInAnimation(slideRightIn);
                    vfRiderSettings.setOutAnimation(slideRightOut);
                    vfRiderSettings.setDisplayedChild(0);
                    Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                    mCurrentPhotoPath = null;
                    Common.stopWaitingDialog(pDialog);
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                btnSaveProfilePhoto.setText(R.string.btn_save);
                btnSaveProfilePhoto.setEnabled(true);
                Common.stopWaitingDialog(pDialog);
                if (riderInfo != null) {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Snackbar.make(coordinatorLayout, riderInfo.getWebMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", snackBarDismissOnClickListener).show();
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        Toast.makeText(RiderSettingsActivity.this, WEB_ERRORS_MESSAGE, Toast.LENGTH_LONG).show();
                        logOutHere();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                lytContent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                onGoBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onGoBack();
    }

    private void onGoBack() {
        int index = vfRiderSettings.getDisplayedChild();
        if (index > 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_rider_settings));
            vfRiderSettings.setInAnimation(slideRightIn);
            vfRiderSettings.setOutAnimation(slideRightOut);
            vfRiderSettings.setDisplayedChild(0);
        } else {
            Intent intent = new Intent(RiderSettingsActivity.this, RiderMainActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RiderSettingsActivity.this.finish();
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

    private void logOutHere() {
        AutoRideRiderApps.logout();
        Intent intent = new Intent(RiderSettingsActivity.this, RiderWelcomeActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderSettingsActivity.this.finish();
    }

    public static class ImageCaptureModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_photo_dialogue, null);
            dialog.setContentView(view);
        }
    }
}