package org.autoride.autoride;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.maps.android.ui.IconGenerator;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionLayout;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;
import com.skyfishjy.library.RippleBackground;
import com.telenav.expandablepager.ExpandablePager;
import com.telenav.expandablepager.listener.OnSliderStateChangeListener;

import org.autoride.autoride.Account.RiderTransactionActivity;
import org.autoride.autoride.TrackRider.RiderTrackingActivity;
import org.autoride.autoride.activity.RiderWelcomeActivity;
import org.autoride.autoride.applications.AutoRideRiderApps;
import org.autoride.autoride.autorideReference.ReferenceActivity;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.history.ride.RideHistoryActivity;
import org.autoride.autoride.listeners.ParserListener;
import org.autoride.autoride.listeners.ParserListenerDriver;
import org.autoride.autoride.message.MessageActivity;
import org.autoride.autoride.model.DriverInfo;
import org.autoride.autoride.model.FareInfo;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.model.TripDetailsInfo;
import org.autoride.autoride.networks.RiderApiUrl;
import org.autoride.autoride.networks.managers.api.CallApi;
import org.autoride.autoride.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.autoride.networks.managers.api.RequestedUrlBuilder;
import org.autoride.autoride.networks.managers.data.ManagerData;
import org.autoride.autoride.notifications.commons.Common;
import org.autoride.autoride.notifications.helpers.FCMService;
import org.autoride.autoride.notifications.helpers.GoogleAPI;
import org.autoride.autoride.profiles.RiderProfileActivity;
import org.autoride.autoride.settings.RiderSettingsActivity;
import org.autoride.autoride.trackings.UserTrackingActivity;
import org.autoride.autoride.utils.AppUtils;
import org.autoride.autoride.utils.Book;
import org.autoride.autoride.utils.BookAdapter;
import org.autoride.autoride.utils.Constants;
import org.autoride.autoride.utils.CustomInfoWindow;
import org.autoride.autoride.utils.FareDetailActivity;
import org.autoride.autoride.utils.FetchAddressIntentService;
import org.autoride.autoride.utils.Operation;
import org.autoride.autoride.utils.RoundImage;
import org.autoride.autoride.utils.ScreenUtility;
import org.autoride.autoride.utils.markerAnimation.LatLngInterpolator;
import org.autoride.autoride.utils.markerAnimation.MarkerAnimation;
import org.autoride.autoride.utils.reference.ReferenceItem;
import org.autoride.autoride.utils.reference.receiver.NetworkConnectionReciever;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;

public class RiderMainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        RiderApiUrl, AppsConstants,
        NetworkConnectionReciever.ConnectivityRecieverListener,
        GoogleApiClient.OnConnectionFailedListener,
        PlaceAutocompleteAdapter.PlaceAutoCompleteInterface {

    private final String EXTRA_NAME = "cheese_name";
    private final String TAG = "RiderMainActivity";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float MAP_ZOOM = 14.0f;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int ANIMATION_TIME_PER_ROUTE = 1000;
    private int backStage, driverContactStage = 0;
    private final LatLng mDefaultLocation = new LatLng(23.8365, 90.3695);
    //START RIDING
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Button btnRiderReference;
    private boolean isBtnApplyClose = false;
    private boolean doubleBackPressed = false;
    private boolean mLocationPermissionGranted;
    private SupportMapFragment riderMapsFragment;
    private GoogleMap riderMaps;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String accessToken, rememberToken, riderId, riderName, riderPhoto, riderPhone, riderRating,
            riderPickupPlace, riderDestinationPlace, vehicleType, driverId, tripPlace, riderRole;
    private PlaceAutocompleteFragment pickupLocation, riderDestination, riderConfirmAddressBar;
    private Marker riderPickupMarker, riderDestinationMarker, carMarker, bikeMarker;
    private Circle riderLocationCircle;
    private Location mLastKnownLocation;
    private LatLng riderPickupLatLng, riderDestinationLatLng, startPosition, endPosition, currentPosition, updateDestLatLng;
    private double riderDestinationDistance, riderDestinationDuration;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView tvRiderDrawerFullName, tvRiderRequestCancel, tvDriverFullName,
            tvVehicleBrand, tvDriverRating, tvVehicleDesc, tvConfirmPickup, tvRideContinue, tvRideCancel;
    private CircleImageView civRiderDrawerPhoto, civAcceptedDriverPhoto;
    private GoogleAPI mService;
    private FCMService fcmService;
    private SharedPreferences sp;
    private LinearLayout carInfoContainer;
    private List<Marker> bikeDriverMarker, carDriverMarker;
    private List<LatLng> polyLineList;
    private Polyline blackPolyline, backgroundPolyline, foregroundPolyline;
    private FareInfo fareInfo;
    private Handler handler;
    private ProgressDialog pDialog;
    private boolean isFirstPosition = true;
    private Double startLatitude;
    private Double startLongitude;
    private ValueAnimator polyLineAnimator;
    private String bikeFare, carFare, confirmFare, vTypes;
    private boolean isVehicleAvailAble = false;
    private boolean isRequestProcessing = false;
    private boolean isRiderTripProcessing = true;
    private boolean isNotCompleteTrip = true;
    private boolean isResetPickUp = true;
    private EditText etResetPickUp;
    private Marker myMarker = null;
    private OkHttpClient client;

    //RIDER OFFER/MESSAGE
    //USER MESSAGE
    private ImageView imgExpandableMessage;
    private Context con;
    private String[] permissionsRequired = new String[]{CALL_PHONE};
    //AUTO RIDE CAR INFORMATION
    private int duration = 200;
    private Integer[] imgId = {
            R.drawable.ic_scooter,
            R.drawable.ic_autoride_hire1

//            R.drawable.ic_scooter,
//            R.drawable.ic_tuk_tuk,
//            R.drawable.ic_autoride_premier,
//            R.drawable.ic_autoridexx,
//            R.drawable.ic_autoride_hire1,
//            R.drawable.ic_autoride_hire2
    };
    private String[] suitcases = {
            "1",
            "4"
    };
    private String[] passenger = {
            "1",
            "4"
    };
    private String fareDetails = FATE_NOTES;
    private List<Book> list = Arrays.asList(

            new Book(BIKE.toUpperCase()).setAuthor(BIKE_NOTES).setDescription(fareDetails),
            new Book(CAR.toUpperCase()).setAuthor(CAR_NOTES).setDescription(fareDetails)

           /* new Book("MOTO").setAuthor("Bikes for quick rides").setDescription(fareDetails),
            new Book("CNG").setAuthor("CNG sedans, top drivers").setDescription(fareDetails),
            new Book("PREMIER").setAuthor("Premium sedans, top drivers").setDescription(fareDetails),
            new Book("AUTORIDEX").setAuthor("Affordable, everyday rides").setDescription(fareDetails),
            new Book("HIRE 2HR").setAuthor("Rental for local city travel").setDescription(fareDetails),
            new Book("HIRE 4HR").setAuthor("Rental for local city travel").setDescription(fareDetails)*/

           /* new Book("Catch-22").setAuthor("Joseph Heller").setUrl("http://ecx.images-amazon.com/images/I/51kqbC3YKvL._SX322_BO1,204,203,200_.jpg").setDescription("Catch-22 is a satirical novel by the American author Joseph Heller. He began writing it in 1953; the novel was first published in 1961. It is frequently cited as one of the greatest literary works of the twentieth century. It uses a distinctive non-chronological third-person omniscient narration, describing events from the points of view of different characters. The separate storylines are out of sequence so that the timeline develops along with the plot."),
            new Book("Animal Farm").setAuthor("George Orwell").setUrl("http://ecx.images-amazon.com/images/I/51EjU6rQjsL._SX318_BO1,204,203,200_.jpg").setDescription("Animal Farm is an allegorical and dystopian novella by George Orwell, first published in England on 17 August 1945. According to Orwell, the book reflects events leading up to the Russian Revolution of 1917 and then on into the Stalinist era of the Soviet Union. Orwell, a democratic socialist, was a critic of Joseph Stalin and hostile to Moscow-directed Stalinism, an attitude that was critically shaped by his experiences during the Spanish Civil War."),
            new Book("To Kill a Mockingbird").setAuthor("Harper Lee").setUrl("http://ecx.images-amazon.com/images/I/51grMGCKivL._SX307_BO1,204,203,200_.jpg").setDescription("To Kill a Mockingbird is a novel by Harper Lee published in 1960. It was immediately successful, winning the Pulitzer Prize, and has become a classic of modern American literature. The plot and characters are loosely based on the author's observations of her family and neighbors, as well as on an event that occurred near her hometown in 1936, when she was 10 years old."),
            new Book("Fahrenheit 451").setAuthor("Ray Bradbury").setUrl("http://ecx.images-amazon.com/images/I/41Cx8mY2UNL._SX324_BO1,204,203,200_.jpg").setDescription("Fahrenheit 451 is a dystopian novel by Ray Bradbury published in 1953. It is regarded as one of his best works. The novel presents a future American society where books are outlawed and \"firemen\" burn any that are found. The title refers to the temperature that Bradbury asserted to be the autoignition temperature of paper. (In reality, scientists place the autoignition temperature of paper anywhere from high 440 degrees Fahrenheit to some 30 degrees hotter, depending on the study and type of paper.)")*/
    );
    private Toolbar toolbar, toolbar_in_maps, pickupToolbar;
    private ExpandablePager pager;
    private ImageView ivFoundCar;
    private TextView tvConfirmAutoride;
    private RippleBackground rippleBackgroundScooter, rippleBackgroundAutoRishaw, rippleBackgroundPremier,
            rippleBackgroundAutoridex, rippleBackgroundAutorideHire2, rippleBackgroundAutorideHire4, rippleStartRidingContent;
    private LinearLayout cashInfoContainer, carRequestContainer, confirm_pickup_container, llRiderConfirmAddressBar, llRiderDestinationSearchBar;
    private TextView tvCash, tvEstimateCashFare, tvEstimateBikeFare, tvEstimateCarFare, tvMaxPassenger;
    private List<Book> myList;
    private BookAdapter adapter;
    //ROUND IMAGE
    private RoundImage roundedImage;
    private Context mContext;
    private Resources mResources;
    private RelativeLayout mRelativeLayout;
    private Button mBTN;
    private ImageView ivConfirmPickup;
    //START RIDING
    private Bitmap mBitmap;
    //dragging map position
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput, mStateOutput, mCityOutput, mAreaOutput;
    private EditText mLocationAddress;
    private TextView mLocationText;
    private Toolbar mToolbar;
    private TextView mLocationMarkerText;
    private LinearLayout locationMarkerContainer;
    private ArgbEvaluator argbEvaluator;
    private View locButton;
    private BottomSheetBehavior mBottomSheetBehavior;
    private android.support.design.widget.FloatingActionButton myLocationButton;
    private com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton fab1;
    private com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton fab2;
    private com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton fab3;
    private FloatingActionLayout fabMyCar, fabScooter;
    private FloatingLayout floatingLayout;
    private View.OnClickListener snackBarDismissListener;
    private ReferenceItem referenceItem;
    private static ArrayList<ReferenceItem> myrReferenceItemsList;
    private boolean isConnected;
    private static final String NA = "NA";
    private ArrayList<LatLng> latLngArrayList;
    private int delay = 3 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
    private Runnable runnable;
    private LatLngInterpolator latLngInterpolator;
    private BottomSheetBehavior mBottomSheetBehaviour, fdBottomSheetBehaviour;
    private View bottomSheetContainer, bottomSheetFindDriver;

    private TextView tvRiderRideCancelClose, tvArrivingNowTime, tvDriverFullName2, tvVehicleBrand2, tvDriverRating2, tvVehicleDesc2,
            tvRiderDestination, tvArrivalTime, tvPaymentType, tvPaymentAmount, tvDriverName, tvDriverPhone, tvRiderChangeDestination, tvTripStatus;
    private CircleImageView civAcceptedDriverPhoto2;
    private ImageView ivAcceptedDriverVehicle;
    private LinearLayout llDriverInfoContainer, llDriverContact;
    private String driverPhone, updateDest;
    private DestChangeModal destChangeModal;
    private RequestCancelModal requestCancelModal;
    private LogOutModal logOutModal;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 11;
    private CollapsingToolbarLayout collapsingToolbar;
    private LinearLayout llDriverInfoShower;
    private String ridingModeStatus = null;

    public static final String EXTRA_REVEAL_X = "EXTRA_REVEAL_X";
    public static final String EXTRA_REVEAL_Y = "EXTRA_REVEAL_Y";
    private Window window;

    /*hello*/
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

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            revealAnimation();
        }

        latLngInterpolator = new LatLngInterpolator.Linear();
        pDialog = new ProgressDialog(RiderMainActivity.this);
        myrReferenceItemsList = new ArrayList<>();
        referenceItem = new ReferenceItem();

        Common.startWaitingDialog(pDialog);
        setUiComponent();

        window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        if (getIntent() != null) {
            String notificationStatus = getIntent().getStringExtra("notification_status");
            if (notificationStatus != null) {
                if (notificationStatus.equalsIgnoreCase("driver_request_accept")) {

                    setDriverInfoUi();

                    floatingLayout.setVisibility(View.GONE);
                    imgExpandableMessage.setVisibility(View.GONE);
                    isRequestProcessing = true;
                    isNotCompleteTrip = false;

                    llRiderConfirmAddressBar.setVisibility(View.GONE);

                    //testing
                    //llShowLocationSearchBox.setVisibility(View.GONE);
                    llRiderDestinationSearchBar.setVisibility(View.GONE);

                    riderDestination.setText("");
                    isRiderTripProcessing = false;
                    stopSetAllNearestDriverTask();

                    double destLat = getIntent().getDoubleExtra("dest_lat", -1.0);
                    double destLng = getIntent().getDoubleExtra("dest_lng", -1.0);

                    final double driverLat = getIntent().getDoubleExtra("driver_lat", -1.0);
                    final double driverLng = getIntent().getDoubleExtra("driver_lng", -1.0);
                    final double pickupLat = getIntent().getDoubleExtra("pickup_lat", -1.0);
                    final double pickupLng = getIntent().getDoubleExtra("pickup_lng", -1.0);

                    riderMapsFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            riderMaps = googleMap;
                            riderMaps.setTrafficEnabled(false);
                            riderMaps.setIndoorEnabled(false);
                            riderMaps.setBuildingsEnabled(false);
                            riderMaps.getUiSettings().setZoomControlsEnabled(false);
                            riderSeeVehicleDirection(driverLat, driverLng, pickupLat, pickupLng);
                        }
                    });

                    vTypes = getIntent().getStringExtra("vehicle_type");
                    driverId = getIntent().getStringExtra("driver_id");
                    String driverPhoto = getIntent().getStringExtra("driver_photo");
                    String vehiclePhoto = getIntent().getStringExtra("vehicle_photo");
                    String driverName = getIntent().getStringExtra("driver_name");
                    String vBrand = getIntent().getStringExtra("v_brand");
                    String driverRating = getIntent().getStringExtra("driver_rating");
                    String vDesc = getIntent().getStringExtra("v_desc");
                    String arrivingTime = getIntent().getStringExtra("arriving_time");
                    String conFare = getIntent().getStringExtra("confirm_fare");

                    driverPhone = getIntent().getStringExtra("driver_phone");
                    String riderDestinationPlace = getIntent().getStringExtra("rider_destination_place");

                    // set driver info
                    bottomSheetContainer.setVisibility(View.VISIBLE);

                    tvArrivingNowTime.setText(arrivingTime);
                    tvDriverFullName2.setText(driverName);
                    tvVehicleBrand2.setText(vBrand);
                    tvDriverRating2.setText(driverRating);
                    tvVehicleDesc2.setText(vDesc);
                    tvRiderDestination.setText(riderDestinationPlace);
                    tvArrivalTime.setText("11:11 am arrival");
                    tvPaymentType.setText("Cash");
                    tvPaymentAmount.setText(CURRENCY + conFare);
                    tvTripStatus.setText("Arriving Stage");
                    tvRiderRideCancelClose.setText("Cancel");
                    // collapsingToolbar.setTitle("Arriving Stage");

                    tvDriverName.setText(driverName);
                    tvDriverPhone.setText(driverPhone);

                    Glide.with(getBaseContext())
                            .load(driverPhoto)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default))
                            .into(civAcceptedDriverPhoto2);

                    Glide.with(getBaseContext())
                            .load(vehiclePhoto)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default))
                            .into(ivAcceptedDriverVehicle);

                    Glide.with(getBaseContext())
                            .load(driverPhoto)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default))
                            .into(civAcceptedDriverPhoto);

                    tvDriverFullName.setText(driverName);
                    tvVehicleBrand.setText(vBrand);
                    tvDriverRating.setText(driverRating);
                    tvVehicleDesc.setText(vDesc);
                } else if (notificationStatus.equalsIgnoreCase("driver_trip_completed")) {
                    Toast.makeText(RiderMainActivity.this, getIntent().getStringExtra("notifications_msg"), Toast.LENGTH_LONG).show();
                } else if (notificationStatus.equalsIgnoreCase("driver_trip_canceled")) {
                    Toast.makeText(RiderMainActivity.this, getIntent().getStringExtra("notifications_msg"), Toast.LENGTH_LONG).show();
                } else if (notificationStatus.equalsIgnoreCase("driver_start_trip")) {

                    setDriverInfoUi();

                    //testing part
                    // findViewById(R.id.ll_trip_cancel).setVisibility(View.VISIBLE);
                    floatingLayout.setVisibility(View.GONE);
                    imgExpandableMessage.setVisibility(View.GONE);
                    isRequestProcessing = true;
                    isNotCompleteTrip = false;

                    llRiderConfirmAddressBar.setVisibility(View.GONE);

                    //testing
                    //llShowLocationSearchBox.setVisibility(View.GONE);
                    llRiderDestinationSearchBar.setVisibility(View.GONE);

                    riderDestination.setText("");
                    isRiderTripProcessing = false;
                    stopSetAllNearestDriverTask();

                    double driverLat = getIntent().getDoubleExtra("driver_lat", -1.0);
                    double driverLng = getIntent().getDoubleExtra("driver_lng", -1.0);

                    final double destLat = getIntent().getDoubleExtra("dest_lat", -1.0);
                    final double destLng = getIntent().getDoubleExtra("dest_lng", -1.0);
                    final double pickupLat = getIntent().getDoubleExtra("pickup_lat", -1.0);
                    final double pickupLng = getIntent().getDoubleExtra("pickup_lng", -1.0);

                    riderMapsFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            riderMaps = googleMap;
                            riderMaps.setTrafficEnabled(false);
                            riderMaps.setIndoorEnabled(false);
                            riderMaps.setBuildingsEnabled(false);
                            riderMaps.getUiSettings().setZoomControlsEnabled(false);
                            riderSeeVehicleDirection(pickupLat, pickupLng, destLat, destLng);
                        }
                    });

                    vTypes = getIntent().getStringExtra("vehicle_type");
                    driverId = getIntent().getStringExtra("driver_id");
                    String driverPhoto = getIntent().getStringExtra("driver_photo");
                    String vehiclePhoto = getIntent().getStringExtra("vehicle_photo");
                    String driverName = getIntent().getStringExtra("driver_name");
                    String vBrand = getIntent().getStringExtra("v_brand");
                    String driverRating = getIntent().getStringExtra("driver_rating");
                    String vDesc = getIntent().getStringExtra("v_desc");
                    String conFare = getIntent().getStringExtra("confirm_fare");

                    String getTime = getIntent().getStringExtra("arriving_time");
                    double timeDouble = Double.valueOf(getTime);
                    String timeUnit;
                    if ((int) timeDouble < 2) {
                        timeUnit = "MINUTE";
                    } else {
                        timeUnit = "MINUTES";
                    }
                    String arrivingTime = (int) timeDouble + " " + timeUnit;

                    driverPhone = getIntent().getStringExtra("driver_phone");
                    String riderDestinationPlace = getIntent().getStringExtra("rider_destination_place");

                    // set driver info
                    bottomSheetContainer.setVisibility(View.VISIBLE);

                    tvArrivingNowTime.setText(arrivingTime);
                    tvDriverFullName2.setText(driverName);
                    tvVehicleBrand2.setText(vBrand);
                    tvDriverRating2.setText(driverRating);
                    tvVehicleDesc2.setText(vDesc);
                    tvRiderDestination.setText(riderDestinationPlace);
                    tvArrivalTime.setText("11:11 am arrival");
                    tvPaymentType.setText("Cash");
                    tvPaymentAmount.setText(CURRENCY + conFare);
                    tvTripStatus.setText("Trip Running");
                    tvRiderRideCancelClose.setText("Close");
                    // collapsingToolbar.setTitle("Trip Running");

                    tvDriverName.setText(driverName);
                    tvDriverPhone.setText(driverPhone);

                    Glide.with(getBaseContext())
                            .load(driverPhoto)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default))
                            .into(civAcceptedDriverPhoto2);

                    Glide.with(getBaseContext())
                            .load(vehiclePhoto)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default))
                            .into(ivAcceptedDriverVehicle);

                    Glide.with(getBaseContext())
                            .load(driverPhoto)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()
                                    .fitCenter()
                                    .error(R.drawable.ic_profile_photo_default)
                                    .fallback(R.drawable.ic_profile_photo_default))
                            .into(civAcceptedDriverPhoto);

                    tvDriverFullName.setText(driverName);
                    tvVehicleBrand.setText(vBrand);
                    tvDriverRating.setText(driverRating);
                    tvVehicleDesc.setText(vDesc);
                }
            }
        }

        if (isNotCompleteTrip) {
            client = new OkHttpClient();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            client = builder.build();
            performGetLastTrip();
        }

        floatingLayout.setOnMenuExpandedListener(new FloatingLayout.OnMenuExpandedListener() {
            @Override
            public void onMenuExpanded() {

                String manufacturer = "xiaomi";
                if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {

                    FrameLayout.LayoutParams p1 = (FrameLayout.LayoutParams) fab1.getLayoutParams();
                    p1.setMargins(48, 0, 0, 48);
                    fab1.setLayoutParams(p1);

                    FrameLayout.LayoutParams p2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
                    p2.setMargins(75, 0, 0, 0);
                    fab2.setLayoutParams(p2);

                    FrameLayout.LayoutParams p3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
                    p3.setMargins(75, 0, 0, 138);
                    fab2.setLayoutParams(p3);
                }

              /*  vehicleType = "Bike";
                getAllNearestDriver();

                if (isVehicleAvailAble) {

                } else {

                }

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(RIDER_SEARCH_VEHICLE_TYPE, "Bike");
                editor.commit();*/

            }

            @Override
            public void onMenuCollapsed() {

           /*     vehicleType = "Car";
                getAllNearestDriver();

                if (isVehicleAvailAble) {

                } else {

                }

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(RIDER_SEARCH_VEHICLE_TYPE, "Car");
                editor.commit();*/
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUiComponent() {

        latLngArrayList = new ArrayList<LatLng>();
        carDriverMarker = new ArrayList<Marker>();
        bikeDriverMarker = new ArrayList<Marker>();
        fareInfo = new FareInfo();

        mService = Common.getGoogleAPI();
        fcmService = Common.getFCMService();
        polyLineList = new ArrayList<>();
        handler = new Handler();

        toolbar_in_maps = (Toolbar) findViewById(R.id.toolbar_in_maps);
        setSupportActionBar(toolbar_in_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        snackBarDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                v.setVisibility(View.GONE);
            }
        };

        myLocationButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.myLocationButton);
        fab1 = (com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton) findViewById(R.id.fab3);
        floatingLayout = (FloatingLayout) findViewById(R.id.floating_layout);

        fab1.setFabTextColor(getResources().getColor(R.color.colorPrimaryDark));
        fab2.setFabTextColor(getResources().getColor(R.color.colorPrimaryDark));
        fab3.setFabTextColor(getResources().getColor(R.color.colorPrimaryDark));

        llRiderConfirmAddressBar = (LinearLayout) findViewById(R.id.ll_rider_confirm_address_bar);
        llRiderDestinationSearchBar = (LinearLayout) findViewById(R.id.ll_rider_destination_search_bar);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        civRiderDrawerPhoto = (CircleImageView) findViewById(R.id.civ_rider_drawer_photo);
        tvRiderDrawerFullName = (TextView) findViewById(R.id.tv_rider_drawer_full_name);
        tvRiderRequestCancel = (TextView) findViewById(R.id.tv_rider_request_cancel);
        //START RIDING
        ivConfirmPickup = (ImageView) findViewById(R.id.iv_confirm_pickup);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_marker_init);
        ivConfirmPickup.startAnimation(pulse);
        tvRideContinue = (TextView) findViewById(R.id.tvRideContinue);
        tvRideCancel = (TextView) findViewById(R.id.tvRideCancel);
        tvRideContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AutoRideRiderApps.isNetworkAvailable()) {

                    backStage = 3;
                    mapsLocIconDisable();
                    pickupToolbar.setVisibility(View.GONE);
                    myLocationButton.setVisibility(View.GONE);
                    imgExpandableMessage.setVisibility(View.GONE);
                    findViewById(R.id.continueRideFirstContent).setVisibility(View.GONE);
                    riderMaps.getUiSettings().setScrollGesturesEnabled(false);

                    String riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);

                    Common common = new Common(RiderMainActivity.this, fcmService, "rider_ride_request", riderToken,
                            vehicleType, riderId, riderDestinationPlace, confirmFare, riderRating, riderDestinationDistance,
                            riderDestinationDuration, riderPickupLatLng, riderDestinationLatLng);
                    common.notificationToSingleNearestDriver();

                    // handler.postDelayed(notificationSenderTask, 7000);

                    //  bottom sheet
                    findViewById(R.id.ll_driver_find_bottom_sheet_container).setVisibility(View.VISIBLE);

                  /*  final View llDfBsShower = findViewById(R.id.ll_df_bs_shower);
                    llDfBsShower.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            llDfBsShower.setVisibility(View.GONE);
                            fdBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });*/

                    bottomSheetFindDriver = (View) findViewById(R.id.ll_bottom_sheet_find_driver);
                    fdBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetFindDriver);
                    fdBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            switch (newState) {
                                case BottomSheetBehavior.STATE_DRAGGING:
                                    // llDfBsShower.setVisibility(View.GONE);
                                    break;

                                case BottomSheetBehavior.STATE_SETTLING:
                                    break;

                                case BottomSheetBehavior.STATE_EXPANDED:
                                    // llDfBsShower.setVisibility(View.GONE);
                                    break;

                                case BottomSheetBehavior.STATE_COLLAPSED:
                                    fdBottomSheetBehaviour.setPeekHeight(240);
                                    //  llDfBsShower.setVisibility(View.VISIBLE);
                                    break;

                                case BottomSheetBehavior.STATE_HIDDEN:
                                    break;
                            }
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                        }
                    });

                 /*   handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
                            Common common = new Common(RiderMainActivity.this, fcmService, "rider_ride_request", riderToken,
                                    vehicleType, riderId, riderDestinationPlace, confirmFare, riderRating, riderDestinationDistance,
                                    riderDestinationDuration, riderPickupLatLng, riderDestinationLatLng);
                            common.notificationToSingleNearestDriver();
                        }
                    }, 5000);*/

                    /* String riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
                    Common common = new Common(RiderMainActivity.this, fcmService, "rider_ride_request", riderToken,
                            vehicleType, riderId, riderDestinationPlace, confirmFare, riderRating, riderDestinationDistance,
                            riderDestinationDuration, riderPickupLatLng, riderDestinationLatLng);
                    common.notificationToSingleNearestDriver(); */

                /*Intent intent = getIntent();
                final String cheeseName = intent.getStringExtra(EXTRA_NAME);*/

               /* final Toolbar toolBarStartRiding = (Toolbar) findViewById(R.id.toolBarStartRiding);
                setSupportActionBar(toolBarStartRiding);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setSupportActionBar(toolBarStartRiding);
                toolBarStartRiding.setNavigationIcon(R.drawable.ic_globe);
                toolBarStartRiding.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        findViewById(R.id.bookingStartContaier).setVisibility(View.GONE);
                        findViewById(R.id.checkRequestContainer).setVisibility(View.GONE);
                        findViewById(R.id.confirm_pickup_container).setVisibility(View.GONE);
                        findViewById(R.id.carInfoContainer).setVisibility(View.VISIBLE);
                    }
                });
                setSupportActionBar(toolbar);

                CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(cheeseName);*/

                    startRideBooking();
                } else {
                    snackBarNoInternet();
                }
            }
        });
        tvRideCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.confirm_pickup_container).setVisibility(View.GONE);
                carInfoContainer.setVisibility(View.VISIBLE);
                backStage = 1;
                riderMaps.setPadding(0, 0, 0, ScreenUtility.getDeviceHeight(RiderMainActivity.this));

                if (riderDestinationLatLng != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    builder.include(riderDestinationLatLng);
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    riderMaps.animateCamera(cu, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onFinish() {
                            CameraUpdate zOut = CameraUpdateFactory.zoomBy(-1.0f);
                            riderMaps.animateCamera(zOut);
                        }
                    });
                }

                if (vehicleType.equalsIgnoreCase(DOUBLE_QUOTES)) {
                    vehicleType = CAR;
                }

                if (mLastKnownLocation != null) {
                    getAllNearestDriver();
                }

                if (riderPickupMarker != null) {
                    riderPickupMarker.setVisible(true);
                }

                if (riderDestinationMarker != null) {
                    riderDestinationMarker.setVisible(true);
                }

                if (backgroundPolyline != null) {
                    backgroundPolyline.setVisible(true);
                }

                if (foregroundPolyline != null) {
                    foregroundPolyline.setVisible(true);
                }

                if (polyLineAnimator != null) {
                    polyLineAnimator.start();
                }
            }
        });

        tvConfirmPickup = (TextView) findViewById(R.id.tv_confirm_pickup);
        btnRiderReference = (Button) findViewById(R.id.btn_rider_reference);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        riderMapsFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.rider_maps_fragment);
        riderMapsFragment.getMapAsync(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar_in_maps, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //CAR INFO
        initId();
        // setAdapterData();
        showFareDetail();
        showPaymentSystem();
        confirmYourCar();
        //CAR INFO

        //MESSAGE
        con = this;
        if (checkPermission()) {

        } else if (!checkPermission()) {
            requestPermission();
        }

        imgExpandableMessage = (ImageView) findViewById(R.id.imgExpandableMessage);
        imgExpandableMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity.start(RiderMainActivity.this);
            }
        });
        //MESSAGE

        mResultReceiver = new AddressResultReceiver(new Handler());
        //  mLocationMarkerText = (TextView) findViewById(R.id.locationMarkertext);
        locationMarkerContainer = (LinearLayout) findViewById(R.id.locationMarkerContainer);

        toggle.setHomeAsUpIndicator(R.drawable.ic_user);

     /*   toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        sp = getBaseContext().getSharedPreferences(SESSION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sp != null) {

            Log.i(TAG, "checkForSession " + sp.getAll());
            accessToken = sp.getString(ACCESS_TOKEN, DOUBLE_QUOTES);
            rememberToken = sp.getString(REMEMBER_TOKEN, DOUBLE_QUOTES);
            riderId = sp.getString(RIDER_ID, DOUBLE_QUOTES);
            AutoRideRiderApps.getInstance().setRiderUserId(riderId);
            tvRiderDrawerFullName.setText(sp.getString(FIRST_NAME, DOUBLE_QUOTES) + " " + sp.getString(LAST_NAME, DOUBLE_QUOTES));
            riderName = sp.getString(FIRST_NAME, DOUBLE_QUOTES);
            riderPhoto = sp.getString(PROFILE_PHOTO, DOUBLE_QUOTES);
            riderPhone = sp.getString(PHONE, DOUBLE_QUOTES);
            riderRating = sp.getString(RIDER_RATING, DOUBLE_QUOTES).equalsIgnoreCase(DOUBLE_QUOTES) ? "0.0" : sp.getString(RIDER_RATING, DOUBLE_QUOTES).equalsIgnoreCase(NULLS) ? "0.0" : sp.getString(RIDER_RATING, DOUBLE_QUOTES);
            vehicleType = sp.getString(RIDER_SEARCH_VEHICLE_TYPE, DOUBLE_QUOTES);
            riderRole = sp.getString(ROLE, DOUBLE_QUOTES);

            Glide.with(getBaseContext())
                    .load(riderPhoto)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()
                            .fitCenter()
                            .error(R.drawable.ic_profile_photo_default)
                            .fallback(R.drawable.ic_profile_photo_default))
                    .into(civRiderDrawerPhoto);

            Glide.with(getBaseContext())
                    .load(riderPhoto)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()
                            .fitCenter()
                            .error(R.drawable.ic_profile_photo_default)
                            .fallback(R.drawable.ic_profile_photo_default))
                    .into(imgExpandableMessage);

            Glide.with(getBaseContext())
                    .load(riderPhoto)
                    .apply(new RequestOptions().optionalCircleCrop()
                    )
                    .into(new SimpleTarget<Drawable>() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 150, 110, false);
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapResized);
                            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                            roundedBitmapDrawable.setCircular(true);
                            toggle.setHomeAsUpIndicator(roundedBitmapDrawable);
                            //CroppedDrawable cd = new CroppedDrawable(bitmapResized);
//                            toggle.setHomeAsUpIndicator(cd);
                        }
                    });
        }

        if (riderRole.equalsIgnoreCase("executiveUser")) {
            btnRiderReference.setVisibility(View.VISIBLE);
        }

        // rider set destination
        riderDestination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.rider_destination_search_bar);
        riderDestination.setHint("Tap to search destination");
        ((EditText) riderDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(12.0f);
        riderDestination.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        // example : way to access view from PlaceAutoCompleteFragment
                        // ((EditText) autocompleteFragment.getView()
                        // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                        carInfoContainer.setVisibility(View.GONE);
                        riderDestination.setText("");
                        view.setVisibility(View.GONE);
                    }
                });
        riderDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if (AutoRideRiderApps.isNetworkAvailable()) {

                    riderDestinationLatLng = place.getLatLng();
                    riderDestinationPlace = place.getName().toString();

                    if (riderPickupLatLng != null && riderDestinationLatLng != null) {

                        riderMaps.setInfoWindowAdapter(new CustomInfoWindow(RiderMainActivity.this, null, "My Location"));

//                    riderPickupMarker.showInfoWindow();
//                    riderPickupMarker.showInfoWindow();

                        if (backgroundPolyline != null) {
                            backgroundPolyline.remove();
                        }

                        if (foregroundPolyline != null) {
                            foregroundPolyline.remove();
                        }

                        if (polyLineAnimator != null) {
                            if (polyLineAnimator.isRunning()) {
                                polyLineAnimator.cancel();
                            }
                        }

                        setDestinationRoute();
                        floatingLayout.setVisibility(View.GONE);
                        imgExpandableMessage.setVisibility(View.GONE);

                        llRiderConfirmAddressBar.setVisibility(View.GONE);

                        if (getSupportActionBar() != null) {
                            toolbar_in_maps.setVisibility(View.GONE);
                            setConfirmVehicleToolBar();
                        }

                        // controlPage();

                        llRiderDestinationSearchBar.setVisibility(View.GONE);
                        riderDestination.setText("");
                        //  riderDestination.getView().setVisibility(View.GONE);

                        //searchAutoRideDriver();
                        // getDirection();
                    }
                    Log.e(TAG, "Place: " + place.getName());
                } else {
                    snackBarNoInternet();
                }
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        riderConfirmAddressBar = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.rider_confirm_address_bar);
        ((EditText) riderConfirmAddressBar.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(12.0f);
        etResetPickUp = (EditText) riderConfirmAddressBar.getView().findViewById(R.id.place_autocomplete_search_input);
        riderConfirmAddressBar.setHint("Tap to search pick-up");
        riderConfirmAddressBar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                riderPickupLatLng = place.getLatLng();
                if (riderPickupLatLng != null) {
                    isResetPickUp = false;
                    riderMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(riderPickupLatLng, MAP_ZOOM));
                }
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        if (AutoRideRiderApps.isNetworkAvailable()) {
            updateFireBaseToken();
            getFareRate();
        } else {
            snackBarNoInternet();
        }
        uploadReference();
        //getUserLastLocation();

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vehicleType = BIKE;
                getAllNearestDriver();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(RIDER_SEARCH_VEHICLE_TYPE, BIKE);
                editor.commit();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vehicleType = CAR;
                getAllNearestDriver();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(RIDER_SEARCH_VEHICLE_TYPE, CAR);
                editor.commit();
            }
        });

        // searchDest();
    }

    private Runnable notificationSenderTask = new Runnable() {
        @Override
        public void run() {
            // tvRiderRequestCancel.setText("Requesting");
          /*  String riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
            Common common = new Common(RiderMainActivity.this, fcmService, "rider_ride_request", riderToken,
                    vehicleType, riderId, riderDestinationPlace, confirmFare, riderRating, riderDestinationDistance,
                    riderDestinationDuration, riderPickupLatLng, riderDestinationLatLng);
            common.notificationToSingleNearestDriver();*/
        }
    };

    private boolean checkPermission() {
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        return result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(con)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void startRideBooking() {
        rippleStartRidingContent = (RippleBackground) findViewById(R.id.startRidingRippleContent);
        rippleStartRidingContent.startRippleAnimation();

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        imageView.startAnimation(pulse);
    }

    // save/update fire base token
    private void updateFireBaseToken() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, FIRE_BASE_TOKEN + " " + fcmToken);
        performSetFireBaseToken(fcmToken);
    }

    private void performSetFireBaseToken(String fcmToken) {
        ManagerData.taskManager(GET, SET_FIRE_BASE_TOKEN, getBodyJSON(FIRE_BASE_TOKEN, fcmToken), getHeaderJSON(), new ParserListener() {
            @Override
            public void onLoadCompleted(RiderInfo riderInfo) {
                if (riderInfo != null) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(RIDER_FIRE_BASE_TOKEN, riderInfo.getRiderFireBaseToken());
                    editor.commit();
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(RiderInfo riderInfo) {
                if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                    logOutHere();
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
            if (key != null & value != null) {
                postBody.put(key, value);
            }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        riderMaps = googleMap;
        riderMaps.setMyLocationEnabled(true);
        riderMaps.setTrafficEnabled(false);
        riderMaps.setIndoorEnabled(false);
        riderMaps.setBuildingsEnabled(false);
        riderMaps.getUiSettings().setZoomControlsEnabled(false);

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    private void revealAnimation() {

        final View mainLayout = findViewById(R.id.main_drawer_layout);
        final Intent intent = getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && intent.hasExtra(EXTRA_REVEAL_X) && intent.hasExtra(EXTRA_REVEAL_Y)) {
            mainLayout.setVisibility(View.INVISIBLE);
            ViewTreeObserver viewTreeObserver = mainLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealAnimator(mainLayout);
                        mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    private void revealAnimator(View rootLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator animator = ViewAnimationUtils.createCircularReveal(rootLayout, rootLayout.getMeasuredWidth() / 2, rootLayout.getMeasuredHeight() / 2, 50, finalRadius);
            animator.setDuration(800);
            animator.setInterpolator(new AccelerateInterpolator());
            rootLayout.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            finish();
        }
    }

    private void updatePickupLocation() {

        allMarkersRemover();
        riderConfirmAddressBar.setText(riderPickupPlace);
        riderMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), MAP_ZOOM));

        riderMaps.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");

                riderPickupLatLng = cameraPosition.target;



                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(riderPickupLatLng.latitude);
                    mLocation.setLongitude(riderPickupLatLng.longitude);

                    startIntentService(mLocation);

                    getSearchPlace(riderPickupLatLng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getSearchPlace(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = addressList.get(0);
            String riderSearchPlace = address.getAddressLine(0);

            if (isResetPickUp) {
                riderConfirmAddressBar.setText(riderSearchPlace);
            }
            isResetPickUp = true;

            Animation pulse = AnimationUtils.loadAnimation(RiderMainActivity.this, R.anim.pulse_marker);
            ivConfirmPickup.startAnimation(pulse);
        } catch (Exception e) {
            Snackbar.make(drawerLayout, "Feature support problem, Please restart your device", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
            e.printStackTrace();
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {

        if (riderMaps == null) {
            return;
        }

        try {
            if (mLocationPermissionGranted) {

                locButton = ((View) riderMapsFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

                if (locButton != null)
                    locButton.setVisibility(View.GONE);

                myLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (riderMaps != null) {
                            if (locButton != null)
                                locButton.callOnClick();
                        }
                    }
                });

                /*View locButton = ((View) riderMapsFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlPosition = (RelativeLayout.LayoutParams) locButton.getLayoutParams();
                rlPosition.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlPosition.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                rlPosition.setMargins(0, 70, 0, 0);
                rlPosition.setMarginEnd(42);*/

               /* riderMaps.setMyLocationEnabled(true);
                riderMaps.getUiSettings().setMyLocationButtonEnabled(true);*/

            } else {
                riderMaps.setMyLocationEnabled(false);
                riderMaps.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

                                riderPickupLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                // riderPickupMarker = riderMaps.addMarker(new MarkerOptions().position(riderPickupLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup)));
                                riderMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(riderPickupLatLng, MAP_ZOOM));

                                try {
                                    Geocoder geocoder = new Geocoder(getBaseContext());
                                    List<Address> addressList = geocoder.getFromLocation(riderPickupLatLng.latitude, riderPickupLatLng.longitude, 1);
                                    Address address = addressList.get(0);
                                    riderPickupPlace = address.getAddressLine(0);

                                    //testing
                                    //etPickupSearch.setText(riderPickupPlace);

                                    riderConfirmAddressBar.setText(riderPickupPlace);

                                    //Toast.makeText(rootView.getContext().getApplicationContext(), "curPlace " + driverCurrentPlace, Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (vehicleType.equalsIgnoreCase(DOUBLE_QUOTES)) {
                                    vehicleType = CAR;
                                }

                                if (isRiderTripProcessing) {
                                    startSetAllNearestDriverTask();

                                    // getAllNearestDriver();
                                }

                                // pagerShowRippleCar();
                                // carRippleAnimation();

//                                if (vehicleType.equalsIgnoreCase(DOUBLE_QUOTES)) {
//                                    vehicleType = "Car";
//                                }
//                                if (vehicleRange.equalsIgnoreCase(DOUBLE_QUOTES)) {
//                                    vehicleRange = "8";
//                                }
//                                getAutoRideDriver();
                            } else {
                                Log.e(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                riderMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, MAP_ZOOM));
                                riderMaps.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        } else {
                            Log.e(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            riderMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, MAP_ZOOM));
                            riderMaps.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
//            Snackbar.make(coordinatorLayout, R.string.message_something_went_wrong, Snackbar.LENGTH_LONG)
//                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // get nearest driver around km
    private void getAllNearestDriver() {
        if (AutoRideRiderApps.isNetworkAvailable()) {
            ManagerData.driverTaskManager(GET, NEAREST_DRIVER, getNearestDriverJSON(vehicleType), null, new ParserListenerDriver() {
                @Override
                public void onLoadCompleted(String driverInfo) {
                    if (driverInfo != null) {
                        allNearestDriverParser(driverInfo);
                    } else {
                        isVehicleAvailAble = false;
                        allMarkersRemover();
                        snackBarSlowInternet();
                        Log.i("driver_not_found", UNABLE_FOUND_DRIVER);
                    }
                }

                @Override
                public void onLoadFailed(String driverInfo) {
                    isVehicleAvailAble = false;
                    allMarkersRemover();
                    snackBarSlowInternet();
                    Log.i("driver_not_found", UNABLE_FOUND_DRIVER);
                }
            });
        } else {
            snackBarNoInternet();
        }
    }

    private void allNearestDriverParser(String response) {
        DriverInfo driverInfo = new DriverInfo();
        List<DriverInfo> driverInfoList = new ArrayList<DriverInfo>();
        JSONObject responseObj = null;
        try {
            if (response != null) {
                responseObj = new JSONObject(response);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(WEB_RESPONSE_CODE_200))) {

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                            if (responseObj.has(WEB_RESPONSE_DRIVER)) {
                                JSONArray driverArr = responseObj.optJSONArray(WEB_RESPONSE_DRIVER);
                                if (driverArr != null) {

                                    allMarkersRemover();

                                    isVehicleAvailAble = true;
                                    for (int i = 0; i < driverArr.length(); i++) {
                                        JSONObject arrayObj = driverArr.getJSONObject(i);

                                        driverInfo.setDriverId(arrayObj.getString(DRIVER_ID));
                                        driverInfo.setVehicleType(arrayObj.getString(VEHICLE_TYPE));
                                        driverInfo.setDriverFireBaseToken(arrayObj.getString(DRIVER_FIRE_BASE_TOKEN));

                                        JSONArray locArr = arrayObj.getJSONArray(DRIVER_LOCATION);
                                        for (int j = 0; j < locArr.length(); j++) {
                                            driverInfo.setDriverLat((Double) locArr.get(1));
                                            driverInfo.setDriverLng((Double) locArr.get(0));
                                        }
                                        driverInfoList.add(driverInfo);

                                        for (DriverInfo dd : driverInfoList) {
                                            LatLng latlng = new LatLng(dd.getDriverLat(), dd.getDriverLng());
                                            latLngArrayList.add(latlng);
                                            if (driverInfo.getVehicleType().equalsIgnoreCase(CAR)) {
                                                carDriverMarker.add(riderMaps.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_marker_icon))));

                                                /*handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        addAllVehicleMarkers(CAR);
                                                    }
                                                }, 3000);*/
                                            } else if (driverInfo.getVehicleType().equalsIgnoreCase(BIKE)) {
                                                bikeDriverMarker.add(riderMaps.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_marker_icon))));

                                            /*    handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        addAllVehicleMarkers(BIKE);
                                                    }
                                                }, 3000);*/
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            isVehicleAvailAble = false;
                            allMarkersRemover();
                            Log.i("driver_not_found", UNABLE_FOUND_DRIVER);
                            // Toast.makeText(this, responseObj.getString(WEB_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        isVehicleAvailAble = false;
                        allMarkersRemover();
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        isVehicleAvailAble = false;
                        allMarkersRemover();
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        isVehicleAvailAble = false;
                        allMarkersRemover();
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        isVehicleAvailAble = false;
                        allMarkersRemover();
                        Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            snackBarSlowInternet();
            e.printStackTrace();
        }
    }

    private void addAllVehicleMarkers(String markerType) {

        LatLng carLastDestination = null;
        LatLng bikeLastDestination = null;

        if (markerType.equalsIgnoreCase(CAR)) {
            // mover(latlng, CAR);
            //  carDriverMarker.add(riderMaps.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_marker_icon))));
            int i, j;
            for (i = 0; i < carDriverMarker.size(); i++) {
                Marker m = carDriverMarker.get(i);
                for (j = 0; j < latLngArrayList.size(); j++) {
                    carLastDestination = latLngArrayList.get(j);
                    if (i == j) {
                        MarkerAnimation.animateMarkerToGB(m, carLastDestination, latLngInterpolator);
                        break;
                    }
                }
            }
        } else if (markerType.equalsIgnoreCase(BIKE)) {
            // mover(latlng, BIKE);
            //   bikeDriverMarker.add(riderMaps.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_marker_icon))));

            Log.i(TAG, "bikeDriverMarker " + bikeDriverMarker.size());
            Log.i(TAG, "latLngArrayList " + latLngArrayList.size());

            int k, l;
            for (k = 0; k < bikeDriverMarker.size(); k++) {
                Marker m = bikeDriverMarker.get(k);
                for (l = 0; l < latLngArrayList.size(); l++) {
                    bikeLastDestination = latLngArrayList.get(l);
                    if (k == l) {
                        MarkerAnimation.animateMarkerToGB(m, bikeLastDestination, latLngInterpolator);
                        break;
                    }
                }
            }
        }
    }

    private void allMarkersRemover() {
        if (bikeDriverMarker.size() != 0) {
            for (Marker bm : bikeDriverMarker) {
                //  bikeDriverMarker.clear();
                bm.remove();
            }
        }
        if (carDriverMarker.size() != 0) {
            for (Marker cm : carDriverMarker) {
                cm.remove();
            }
        }
    }

    private JSONObject getNearestDriverJSON(String vType) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(LAT, mLastKnownLocation.getLatitude());
            postData.put(LNG, mLastKnownLocation.getLongitude());
            if (vType != null) {
                postData.put(VEHICLE, vType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            public void run() {

                fab1.setFabText("Bike");
                fab1.setFabIcon(getDrawable(R.drawable.ic_scooter));
                runnable = this;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fab1.setFabText("Car");
                        fab1.setFabIcon(getDrawable(R.drawable.icon_car));
                    }
                }, 5000);
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        // startSetAllNearestDriverTask();
        AutoRideRiderApps.getInstance().setConnectivityReciever(this);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
        Log.i(TAG, "calling onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "calling onDestroy");
        stopSetAllNearestDriverTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (riderMaps != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, riderMaps.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                boolean callPhoneAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && callPhoneAccepted) {
                    mLocationPermissionGranted = true;
                } else {
                    Snackbar.make(drawerLayout, R.string.location_permission, Snackbar.LENGTH_LONG);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CALL_PHONE},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
            }
            break;
        }
        updateLocationUI();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (riderMaps != null) {
            if (id == R.id.map_none) {
                riderMaps.setMapType(GoogleMap.MAP_TYPE_NONE);
            } else if (id == R.id.map_normal) {
                riderMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if (id == R.id.map_satellite) {
                riderMaps.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (id == R.id.map_hybrid) {
                riderMaps.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if (id == R.id.map_terrain) {
                riderMaps.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRiderProfile(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            Intent intent = new Intent(RiderMainActivity.this, RiderProfileActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RiderMainActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    public void onRiderRideHistory(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            Intent intent = new Intent(RiderMainActivity.this, RideHistoryActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RiderMainActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    public void onRiderSettings(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            Intent intent = new Intent(RiderMainActivity.this, RiderSettingsActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RiderMainActivity.this.finish();
        } else {
            snackBarNoInternet();
        }
    }

    public void getRiderTrackingActivity(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (AutoRideRiderApps.isNetworkAvailable()) {
            if (AutoRideRiderApps.isLocationEnabled()) {
                startActivity(new Intent(getBaseContext(), UserTrackingActivity.class));
                finish();
            } else {
                Snackbar.make(drawerLayout, R.string.no_gps_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
            }
        } else {
            snackBarNoInternet();
        }
    }

    public void onLogoutModalShow(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        logOutModal = new LogOutModal();
        logOutModal.setCancelable(false);
        logOutModal.show(getSupportFragmentManager(), TAG);
    }

    public void onRiderRequestCancel(View view) {

       /* if (tvRiderRequestCancel.getText().toString().equalsIgnoreCase("Requesting")) {

        } else {*/

        if (notificationSenderTask != null) {
            handler.removeCallbacks(notificationSenderTask);
        }

        Intent intent = new Intent(RiderMainActivity.this, RiderMainActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderMainActivity.this.finish();

       /* String driverToken = sp.getString(TRIP_DRIVER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        Common common = new Common();
        common.notificationSendToDriver(fcmService, driverToken, "rider_trip_canceled", "The rider has canceled his request");*/
//        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setFare() {

        FareInfo.setDiscount(0.0);
        FareInfo.setCoupon(0.0);

        bikeFare = String.format("%.2f", calculateBikeFare(riderDestinationDistance, riderDestinationDuration));
        carFare = String.format("%.2f", calculateCarFare(riderDestinationDistance, riderDestinationDuration));

        String[] estimateFare = {
                bikeFare,
                carFare
        };

        adapter = new BookAdapter(this, myList, imgId, suitcases, passenger, estimateFare);
        pager = (ExpandablePager) findViewById(R.id.expandable_pager_container);

        setAdapterData();
        controlPage();

        if (vehicleType.equalsIgnoreCase(BIKE)) {
            pager.setCurrentItem(0, true);
        } else if (vehicleType.equalsIgnoreCase(CAR)) {
            pager.setCurrentItem(1, true);
        }

        pagerShowRippleCar();
        carRippleAnimation();

        /*if (pager.getCurrentItem() == 0) {
            pagerShowRippleCar();
            carRippleAnimation();
            tvEstimateCashFare.setText(CURRENCY + bFare);
        } else if (pager.getCurrentItem() == 1) {
            pagerShowRippleCar();
            carRippleAnimation();
            tvEstimateCashFare.setText(CURRENCY + cFare);
        }*/

        tvEstimateBikeFare.setText(CURRENCY + bikeFare);
        tvEstimateCarFare.setText(CURRENCY + carFare);
    }

    private double calculateBikeFare(double km, double min) {
        return ((fareInfo.getBikeBaseFare() + (km * fareInfo.getBikeFarePerKm()) + (min * fareInfo.getBikeFarePerMinute())) - (FareInfo.getDiscount() + FareInfo.getCoupon()));
    }

    private double calculateCarFare(double km, double min) {
        return ((fareInfo.getCarBaseFare() + (km * fareInfo.getCarExeFarePerKm()) + (min * fareInfo.getCarExeFarePerMinute())) - (FareInfo.getDiscount() + FareInfo.getCoupon()));
    }

    private void getFareRate() {
        ManagerData.driverTaskManager(GET, FARE_RATE_URL, getBodyJSON(null, null), null, new ParserListenerDriver() {
            @Override
            public void onLoadCompleted(String driverInfo) {
                if (driverInfo != null) {
                    fareParser(driverInfo);
                } else {
                    snackBarSlowInternet();
                }
            }

            @Override
            public void onLoadFailed(String driverInfo) {
                snackBarSlowInternet();
            }
        });
    }

    private void fareParser(String response) {
        JSONObject responseObj = null;
        try {
            if (response != null) {
                responseObj = new JSONObject(response);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(200))) {

//                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        if (responseObj.has(WEB_RESPONSE_DATA)) {

                            JSONObject dataObj = responseObj.optJSONObject(WEB_RESPONSE_DATA);
                            if (dataObj != null) {

                                if (dataObj.has(CAR)) {
                                    JSONObject carObj = dataObj.optJSONObject(CAR);
                                    if (carObj != null) {

                                        if (carObj.has(BASE_FARE)) {
                                            fareInfo.setCarBaseFare(carObj.getInt(BASE_FARE));
                                        }

                                        if (carObj.has(MINIMUM_FARE)) {
                                            fareInfo.setCarMinimumFare(carObj.getInt(MINIMUM_FARE));
                                        }

                                        if (carObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setCarFarePerMinute(carObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (carObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setCarFarePerKm(carObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (carObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setCarFareOutOfCity(carObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(CAR_EXECUTIVE)) {
                                    JSONObject carExeObj = dataObj.optJSONObject(CAR_EXECUTIVE);
                                    if (carExeObj != null) {

                                        if (carExeObj.has(BASE_FARE)) {
                                            fareInfo.setCarExeBaseFare(carExeObj.getInt(BASE_FARE));
                                        }

                                        if (carExeObj.has(MINIMUM_FARE)) {
                                            fareInfo.setCarExeMinimumFare(carExeObj.getInt(MINIMUM_FARE));
                                        }

                                        if (carExeObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setCarExeFarePerMinute(carExeObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (carExeObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setCarExeFarePerKm(carExeObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (carExeObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setCarExeFareOutOfCity(carExeObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(CAR_HOURLY)) {
                                    JSONObject carHouObj = dataObj.optJSONObject(CAR_HOURLY);
                                    if (carHouObj != null) {

                                        if (carHouObj.has(BASE_FARE)) {
                                            fareInfo.setCarHouBaseFare(carHouObj.getInt(BASE_FARE));
                                        }

                                        if (carHouObj.has(MINIMUM_FARE)) {
                                            fareInfo.setCarHouMinimumFare(carHouObj.getInt(MINIMUM_FARE));
                                        }

                                        if (carHouObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setCarHouFarePerMinute(carHouObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (carHouObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setCarHouFarePerKm(carHouObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (carHouObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setCarHouFareOutOfCity(carHouObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(CAR_HOURLY_EXECUTIVE)) {
                                    JSONObject carHouExeObj = dataObj.optJSONObject(CAR_HOURLY_EXECUTIVE);
                                    if (carHouExeObj != null) {

                                        if (carHouExeObj.has(BASE_FARE)) {
                                            fareInfo.setCarHouExeBaseFare(carHouExeObj.getInt(BASE_FARE));
                                        }

                                        if (carHouExeObj.has(MINIMUM_FARE)) {
                                            fareInfo.setCarHouExeMinimumFare(carHouExeObj.getInt(MINIMUM_FARE));
                                        }

                                        if (carHouExeObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setCarHouExeFarePerMinute(carHouExeObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (carHouExeObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setCarHouExeFarePerKm(carHouExeObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (carHouExeObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setCarHouExeFareOutOfCity(carHouExeObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(BIKE)) {
                                    JSONObject bikeObj = dataObj.optJSONObject(BIKE);
                                    if (bikeObj != null) {

                                        if (bikeObj.has(BASE_FARE)) {
                                            fareInfo.setBikeBaseFare(bikeObj.getInt(BASE_FARE));
                                        }

                                        if (bikeObj.has(MINIMUM_FARE)) {
                                            fareInfo.setBikeMinimumFare(bikeObj.getInt(MINIMUM_FARE));
                                        }

                                        if (bikeObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setBikeFarePerMinute(bikeObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (bikeObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setBikeFarePerKm(bikeObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (bikeObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setBikeFareOutOfCity(bikeObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(BIKE_HOURLY)) {
                                    JSONObject bikeHouObj = dataObj.optJSONObject(BIKE_HOURLY);
                                    if (bikeHouObj != null) {

                                        if (bikeHouObj.has(BASE_FARE)) {
                                            fareInfo.setBikeHouBaseFare(bikeHouObj.getInt(BASE_FARE));
                                        }

                                        if (bikeHouObj.has(MINIMUM_FARE)) {
                                            fareInfo.setBikeHouMinimumFare(bikeHouObj.getInt(MINIMUM_FARE));
                                        }

                                        if (bikeHouObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setBikeHouFarePerMinute(bikeHouObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (bikeHouObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setBikeHouFarePerKm(bikeHouObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (bikeHouObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setBikeHouFareOutOfCity(bikeHouObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(CNG)) {
                                    JSONObject cngObj = dataObj.optJSONObject(CNG);
                                    if (cngObj != null) {

                                        if (cngObj.has(BASE_FARE)) {
                                            fareInfo.setCngBaseFare(cngObj.getInt(BASE_FARE));
                                        }

                                        if (cngObj.has(MINIMUM_FARE)) {
                                            fareInfo.setCngMinimumFare(cngObj.getInt(MINIMUM_FARE));
                                        }

                                        if (cngObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setCngFarePerMinute(cngObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (cngObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setCngFarePerKm(cngObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (cngObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setCngFareOutOfCity(cngObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(CNG_HOURLY)) {
                                    JSONObject cngHouObj = dataObj.optJSONObject(CNG_HOURLY);
                                    if (cngHouObj != null) {

                                        if (cngHouObj.has(BASE_FARE)) {
                                            fareInfo.setCngHouBaseFare(cngHouObj.getInt(BASE_FARE));
                                        }

                                        if (cngHouObj.has(MINIMUM_FARE)) {
                                            fareInfo.setCngHouMinimumFare(cngHouObj.getInt(MINIMUM_FARE));
                                        }

                                        if (cngHouObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setCngHouFarePerMinute(cngHouObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (cngHouObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setCngHouFarePerKm(cngHouObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (cngHouObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setCngHouFareOutOfCity(cngHouObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }

                                if (dataObj.has(PICKUP)) {
                                    JSONObject pickupObj = dataObj.optJSONObject(PICKUP);
                                    if (pickupObj != null) {

                                        if (pickupObj.has(BASE_FARE)) {
                                            fareInfo.setPickupBaseFare(pickupObj.getInt(BASE_FARE));
                                        }

                                        if (pickupObj.has(MINIMUM_FARE)) {
                                            fareInfo.setPickupMinimumFare(pickupObj.getInt(MINIMUM_FARE));
                                        }

                                        if (pickupObj.has(FARE_PER_MINUTE)) {
                                            fareInfo.setPickupFarePerMinute(pickupObj.getInt(FARE_PER_MINUTE));
                                        }

                                        if (pickupObj.has(FARE_PER_KILOMETER)) {
                                            fareInfo.setPickupFarePerKm(pickupObj.getInt(FARE_PER_KILOMETER));
                                        }

                                        if (pickupObj.has(FARE_OUT_OF_CITY)) {
                                            fareInfo.setPickupFareOutOfCity(pickupObj.getInt(FARE_OUT_OF_CITY));
                                        }
                                    }
                                }
                            }
                        }
//                        }

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            Toast.makeText(this, UNABLE_FOUND_FARE, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        Toast.makeText(this, UNABLE_FOUND_FARE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        Toast.makeText(this, UNABLE_FOUND_FARE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        Toast.makeText(this, UNABLE_FOUND_FARE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        Toast.makeText(this, UNABLE_FOUND_FARE, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //car info
    private void initId() {

        toolbar = (Toolbar) findViewById(R.id.toolbar_in_car_info);
        pickupToolbar = (Toolbar) findViewById(R.id.toolbar_pickup);
        rippleBackgroundScooter = (RippleBackground) findViewById(R.id.contentScooter);

        rippleBackgroundAutoRishaw = (RippleBackground) findViewById(R.id.contentAutoRishaw);
        rippleBackgroundPremier = (RippleBackground) findViewById(R.id.contentPremier);
        rippleBackgroundAutoridex = (RippleBackground) findViewById(R.id.contentAutoridex);
        rippleBackgroundAutorideHire2 = (RippleBackground) findViewById(R.id.contentAutorideHire2);
        rippleBackgroundAutorideHire4 = (RippleBackground) findViewById(R.id.contentAutorideHire4);
        tvConfirmAutoride = (TextView) findViewById(R.id.tv_confirm_auto_ride);
        handler = new Handler();
        ivFoundCar = (ImageView) findViewById(R.id.iv_ripple);
        cashInfoContainer = (LinearLayout) findViewById(R.id.cashInfoContainer);
        tvCash = (TextView) findViewById(R.id.tvCash);
        carRequestContainer = (LinearLayout) findViewById(R.id.carRequestContainer);
        myList = new ArrayList<>(list);

        //use PageAdapter
        // adapter = new BookAdapter(this, myList, imgId, passenger);
        // pager = (ExpandablePager) findViewById(R.id.expandable_pager_container);

        carInfoContainer = (LinearLayout) findViewById(R.id.carInfoContainer);
        confirm_pickup_container = (LinearLayout) findViewById(R.id.confirm_pickup_container);

        tvEstimateCashFare = (TextView) findViewById(R.id.tv_estimate_cash_fare);
        tvEstimateBikeFare = (TextView) findViewById(R.id.tv_estimate_bike_fare);
        tvEstimateCarFare = (TextView) findViewById(R.id.tv_estimate_car_fare);
        tvMaxPassenger = (TextView) findViewById(R.id.tv_max_passenger);
    }

    private void setAdapterData() {
        pager.setAdapter(adapter);
    }

    private void pagerShowRippleCar() {
        pager.setOnSliderStateChangeListener(new OnSliderStateChangeListener() {

            @Override
            public void onStateChanged(final View page, final int index, final int state) {
                toggleContent(page, state, duration);
            }

            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onPageChanged(final View page, int index, final int state) {
                toggleContent(page, state, 0);

                if (pager.getCurrentItem() == 0) {

                    confirmFare = bikeFare;
                    tvMaxPassenger.setText("1");
                    tvEstimateCashFare.setText(CURRENCY + bikeFare);

                    pager.setCurrentItem(0, true);
                    rippleBackgroundAutoRishaw.stopRippleAnimation();
                    rippleBackgroundScooter.startRippleAnimation();

                    /*rippleBackgroundPremier.stopRippleAnimation();
                    rippleBackgroundAutoridex.stopRippleAnimation();
                    rippleBackgroundAutorideHire2.stopRippleAnimation();
                    rippleBackgroundAutorideHire4.stopRippleAnimation();*/

                    foundBike();
                }

                if (pager.getCurrentItem() == 1) {

                    confirmFare = carFare;
                    tvMaxPassenger.setText("1-4");
                    tvEstimateCashFare.setText(CURRENCY + carFare);

                    pager.setCurrentItem(1, true);
                    rippleBackgroundScooter.stopRippleAnimation();
                    rippleBackgroundAutoRishaw.startRippleAnimation();

                    /*rippleBackgroundPremier.stopRippleAnimation();
                    rippleBackgroundAutoridex.stopRippleAnimation();
                    rippleBackgroundAutorideHire2.stopRippleAnimation();
                    rippleBackgroundAutorideHire4.stopRippleAnimation();*/

                    foundCar();
                }

                if (pager.getCurrentItem() == 2) {
                    pager.setCurrentItem(2, true);
                    rippleBackgroundScooter.stopRippleAnimation();
                    rippleBackgroundAutoRishaw.stopRippleAnimation();
                    rippleBackgroundAutoridex.stopRippleAnimation();
                    rippleBackgroundAutorideHire2.stopRippleAnimation();
                    rippleBackgroundAutorideHire4.stopRippleAnimation();
                    rippleBackgroundPremier.startRippleAnimation();
                    foundPremier();
                }

                if (pager.getCurrentItem() == 3) {
                    pager.setCurrentItem(3, true);
                    rippleBackgroundScooter.stopRippleAnimation();
                    rippleBackgroundAutoRishaw.stopRippleAnimation();
                    rippleBackgroundPremier.stopRippleAnimation();
                    rippleBackgroundAutorideHire2.stopRippleAnimation();
                    rippleBackgroundAutorideHire4.stopRippleAnimation();
                    rippleBackgroundAutoridex.startRippleAnimation();
                    foundAutoRideX();
                }

                if (pager.getCurrentItem() == 4) {
                    rippleBackgroundScooter.stopRippleAnimation();
                    rippleBackgroundAutoRishaw.stopRippleAnimation();
                    rippleBackgroundPremier.stopRippleAnimation();
                    rippleBackgroundAutoridex.stopRippleAnimation();
                    rippleBackgroundAutorideHire4.stopRippleAnimation();
                    rippleBackgroundAutorideHire2.startRippleAnimation();
                    foundAutoRideHire2();
                }

                if (pager.getCurrentItem() == 5) {
                    pager.setCurrentItem(5, true);
                    rippleBackgroundScooter.stopRippleAnimation();
                    rippleBackgroundAutoRishaw.stopRippleAnimation();
                    rippleBackgroundPremier.stopRippleAnimation();
                    rippleBackgroundAutoridex.stopRippleAnimation();
                    rippleBackgroundAutorideHire2.stopRippleAnimation();
                    rippleBackgroundAutorideHire4.startRippleAnimation();
                    foundAutoRideHire4();
                }
            }
        });
    }

    private void carRippleAnimation() {

        ImageView ivBike = (ImageView) findViewById(R.id.iv_auto_ride_bike);
        ivBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pager.setCurrentItem(0, true);
                rippleBackgroundAutoRishaw.stopRippleAnimation();
                rippleBackgroundScooter.startRippleAnimation();

              /*  rippleBackgroundPremier.stopRippleAnimation();
                rippleBackgroundAutoridex.stopRippleAnimation();
                rippleBackgroundAutorideHire2.stopRippleAnimation();
                rippleBackgroundAutorideHire4.stopRippleAnimation();*/

              /*  handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundBike();
                    }
                }, 3000);*/
            }
        });

        ImageView ivCar = (ImageView) findViewById(R.id.iv_auto_ride_car);
        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pager.setCurrentItem(1, true);
                rippleBackgroundScooter.stopRippleAnimation();
                rippleBackgroundAutoRishaw.startRippleAnimation();

                /*rippleBackgroundPremier.stopRippleAnimation();
                rippleBackgroundAutoridex.stopRippleAnimation();
                rippleBackgroundAutorideHire2.stopRippleAnimation();
                rippleBackgroundAutorideHire4.stopRippleAnimation();*/

               /* handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundCar();
                    }
                }, 3000);*/
            }
        });

        ImageView ivPremier = (ImageView) findViewById(R.id.imgPremier);
        ivPremier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(2, true);
                rippleBackgroundScooter.stopRippleAnimation();
                rippleBackgroundAutoRishaw.stopRippleAnimation();
                rippleBackgroundAutoridex.stopRippleAnimation();
                rippleBackgroundAutorideHire2.stopRippleAnimation();
                rippleBackgroundAutorideHire4.stopRippleAnimation();
                rippleBackgroundPremier.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundPremier();
                    }
                }, 3000);
            }
        });

        ImageView ivAutoRideX = (ImageView) findViewById(R.id.imgAutoridex);
        ivAutoRideX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(3, true);
                rippleBackgroundScooter.stopRippleAnimation();
                rippleBackgroundAutoRishaw.stopRippleAnimation();
                rippleBackgroundPremier.stopRippleAnimation();
                rippleBackgroundAutorideHire2.stopRippleAnimation();
                rippleBackgroundAutorideHire4.stopRippleAnimation();
                rippleBackgroundAutoridex.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundAutoRideX();
                    }
                }, 3000);
            }
        });

        ImageView ivAutoRideHire2 = (ImageView) findViewById(R.id.imgAutorideHire2);
        ivAutoRideHire2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(4, true);
                rippleBackgroundScooter.stopRippleAnimation();
                rippleBackgroundAutoRishaw.stopRippleAnimation();
                rippleBackgroundPremier.stopRippleAnimation();
                rippleBackgroundAutoridex.stopRippleAnimation();
                rippleBackgroundAutorideHire4.stopRippleAnimation();
                rippleBackgroundAutorideHire2.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundAutoRideHire2();
                    }
                }, 3000);
            }
        });

        ImageView ivAutoRideHire4 = (ImageView) findViewById(R.id.imgAutorideHire4);
        ivAutoRideHire4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(5, true);
                rippleBackgroundScooter.stopRippleAnimation();
                rippleBackgroundAutoRishaw.stopRippleAnimation();
                rippleBackgroundPremier.stopRippleAnimation();
                rippleBackgroundAutoridex.stopRippleAnimation();
                rippleBackgroundAutorideHire2.stopRippleAnimation();
                rippleBackgroundAutorideHire4.startRippleAnimation();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundAutoRideHire4();
                    }
                }, 3000);
            }
        });
    }

    private void confirmYourCar() {
        carRequestContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isVehicleAvailAble) {

                    //testing
                    //cardViewLocationSearch.setVisibility(View.VISIBLE);
                    //llConfirmPickupContainer.setVisibility(View.VISIBLE);

                    mapsLocIconEnabled();
                    llRiderConfirmAddressBar.setVisibility(View.VISIBLE);
                    findViewById(R.id.checkRequestContainer).setVisibility(View.VISIBLE);
                    ivConfirmPickup.setVisibility(View.VISIBLE);
                    findViewById(R.id.continueRideFirstContent).setVisibility(View.GONE);
                    stopSetAllNearestDriverTask();
                    updatePickupLocation();

                    // carInfoContainer.setVisibility(View.GONE);
                    carInfoContainer.setVisibility(View.INVISIBLE);
                    riderMaps.setPadding(0, 0, 0, 0);
                    riderMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), MAP_ZOOM));

                    if (riderPickupMarker != null) {
                        if (riderPickupMarker.isVisible()) {
                            riderPickupMarker.setVisible(false);
                        }
                    }

                    if (riderDestinationMarker != null) {
                        if (riderDestinationMarker.isVisible()) {
                            riderDestinationMarker.setVisible(false);
                        }
                    }

                    if (foregroundPolyline != null) {
                        if (foregroundPolyline.isVisible()) {
                            foregroundPolyline.setVisible(false);
                        }
                    }

                    if (backgroundPolyline != null) {
                        if (backgroundPolyline.isVisible()) {
                            backgroundPolyline.setVisible(false);
                        }
                    }

                    if (polyLineAnimator != null) {
                        if (polyLineAnimator.isRunning()) {
                            polyLineAnimator.cancel();
                        }
                    }

                    //  riderMaps.setMapType(GoogleMap.MAP_TYPE_NONE);
                    confirm_pickup_container.setVisibility(View.VISIBLE);
                    locationMarkerContainer.setVisibility(View.VISIBLE);
                    setPickupToolbar();
                    //  Toast.makeText(RiderMainActivity.this, "CAR REQUEST ON GOING!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RiderMainActivity.this, "Sorry, your selected vehicle not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onConfirmPickup(View view) {

        /*tvConfirmPickup.setText("Searching your driver...");
        tvConfirmPickup.setClickable(false);
        Common.startWaitingDialog(pDialog);*/

        /*String riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        Common common = new Common(RiderMainActivity.this, fcmService, "rider_ride_request", riderToken,
                vehicleType, riderId, riderDestinationPlace, confirmFare, riderDestinationDistance, riderDestinationDuration, riderPickupLatLng, riderDestinationLatLng);
        common.notificationToSingleNearestDriver();*/

        if (etResetPickUp.getText().toString().length() != 0) {
            mapsLocIconDisable();
            llRiderConfirmAddressBar.setVisibility(View.GONE);
            findViewById(R.id.checkRequestContainer).setVisibility(View.GONE);
            ivConfirmPickup.setVisibility(View.GONE);

            findViewById(R.id.continueRideFirstContent).setVisibility(View.VISIBLE);
        } else {
            Snackbar.make(drawerLayout, "Please set your pick-up location", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
        }

      /*      View bottomSheet = findViewById( R.id.bottom_sheet_container );
      //  bottomSheet.setVisibility(View.VISIBLE);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });
        mBottomSheetBehavior.setPeekHeight(400);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_SETTLING);*/

        // BottomSheetDialogFragment bottomSheetDialogFragment = new CarBookingFragment();
        // bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

    /*    String riderToken = sp.getString(RIDER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        Common common = new Common(RiderMainActivity.this, fcmService, "Auto Ride Rider Request", riderToken,
                vehicleType, riderId, riderDestinationPlace, confirmFare, riderPickupLatLng, riderDestinationLatLng);
        common.notificationToSingleNearestDriver();*/
    }

    /************************* back stage control
     * *********************/
    private void setConfirmVehicleToolBar() {

        backStage = 1;
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backStage = 0;
                mapsLocIconEnabled();
                riderMaps.setPadding(0, 0, 0, 0);
                riderMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), MAP_ZOOM));

                if (riderPickupMarker != null) {
                    riderPickupMarker.remove();
                }

                if (riderDestinationMarker != null) {
                    riderDestinationMarker.remove();
                }

                if (backgroundPolyline != null) {
                    backgroundPolyline.remove();
                }

                if (foregroundPolyline != null) {
                    foregroundPolyline.remove();
                }

                if (polyLineAnimator != null) {
                    if (polyLineAnimator.isRunning()) {
                        polyLineAnimator.cancel();
                    }
                }

                if (confirm_pickup_container.getVisibility() == View.VISIBLE) {
                    confirm_pickup_container.setVisibility(View.GONE);
                    locationMarkerContainer.setVisibility(View.GONE);
                    carInfoContainer.setVisibility(View.VISIBLE);
                } else {
                    carInfoContainer.setVisibility(View.INVISIBLE);
                }

                floatingLayout.setVisibility(View.VISIBLE);
                imgExpandableMessage.setVisibility(View.VISIBLE);

                toolbar_in_maps.setVisibility(View.VISIBLE);
                llRiderConfirmAddressBar.setVisibility(View.VISIBLE);
                //  riderDestination.getView().setVisibility(View.VISIBLE);
                riderDestination.setText("");

                //testing
                // llShowLocationSearchBox.setVisibility(View.VISIBLE);
                llRiderDestinationSearchBar.setVisibility(View.VISIBLE);

                // riderConfirmAddressBar.setText("");
            }
        });
    }

    private void setPickupToolbar() {

        backStage = 2;
        setSupportActionBar(pickupToolbar);
        pickupToolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        pickupToolbar.setNavigationIcon(R.drawable.back_arrow);
        setSupportActionBar(pickupToolbar);
        pickupToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backStage = 1;
                mapsLocIconDisable();
                riderMaps.setPadding(0, 0, 0, ScreenUtility.getDeviceHeight(RiderMainActivity.this));

                if (riderDestinationLatLng != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    builder.include(riderDestinationLatLng);
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    riderMaps.animateCamera(cu, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onFinish() {
                            CameraUpdate zOut = CameraUpdateFactory.zoomBy(-1.0f);
                            riderMaps.animateCamera(zOut);
                        }
                    });
                }

                if (vehicleType.equalsIgnoreCase(DOUBLE_QUOTES)) {
                    vehicleType = CAR;
                }
                if (mLastKnownLocation != null) {
                    getAllNearestDriver();
                }

                if (riderPickupMarker != null) {
                    riderPickupMarker.setVisible(true);
                }

                if (riderDestinationMarker != null) {
                    riderDestinationMarker.setVisible(true);
                }

                if (backgroundPolyline != null) {
                    backgroundPolyline.setVisible(true);
                }

                if (foregroundPolyline != null) {
                    foregroundPolyline.setVisible(true);
                }

                if (polyLineAnimator != null) {
                    polyLineAnimator.start();
                }

                //carMarker.setVisible(true);
                confirm_pickup_container.setVisibility(View.GONE);
                locationMarkerContainer.setVisibility(View.GONE);
                carInfoContainer.setVisibility(View.VISIBLE);
                riderDestination.setText("");
                // riderConfirmAddressBar.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (isRequestProcessing) {
            if (driverContactStage == 1) {
                driverContactStage = 0;
                llDriverInfoContainer.setVisibility(View.VISIBLE);
                llDriverContact.setVisibility(View.GONE);
            } else if (mBottomSheetBehaviour.getState() == mBottomSheetBehaviour.STATE_EXPANDED) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                moveTaskToBack(true);
            }
        } else if (backStage == 1) {

            backStage = 0;
            mapsLocIconEnabled();
            riderMaps.setPadding(0, 0, 0, 0);
            riderMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), MAP_ZOOM));

            if (riderPickupMarker != null) {
                riderPickupMarker.remove();
            }

            if (riderDestinationMarker != null) {
                riderDestinationMarker.remove();
            }

            if (backgroundPolyline != null) {
                backgroundPolyline.remove();
            }

            if (foregroundPolyline != null) {
                foregroundPolyline.remove();
            }

            if (polyLineAnimator != null) {
                if (polyLineAnimator.isRunning()) {
                    polyLineAnimator.cancel();
                }
            }

            if (confirm_pickup_container.getVisibility() == View.VISIBLE) {
                confirm_pickup_container.setVisibility(View.GONE);
                locationMarkerContainer.setVisibility(View.GONE);
                carInfoContainer.setVisibility(View.VISIBLE);
            } else {
                carInfoContainer.setVisibility(View.INVISIBLE);
            }

            floatingLayout.setVisibility(View.VISIBLE);
            imgExpandableMessage.setVisibility(View.VISIBLE);

            toolbar_in_maps.setVisibility(View.VISIBLE);
            llRiderConfirmAddressBar.setVisibility(View.VISIBLE);
            //  riderDestination.getView().setVisibility(View.VISIBLE);
            riderDestination.setText("");

            //testing
            // llShowLocationSearchBox.setVisibility(View.VISIBLE);
            llRiderDestinationSearchBar.setVisibility(View.VISIBLE);

            // riderConfirmAddressBar.setText("");
        } else if (backStage == 2) {

            backStage = 1;
            mapsLocIconDisable();
            riderMaps.setPadding(0, 0, 0, ScreenUtility.getDeviceHeight(this));

            if (riderDestinationLatLng != null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                builder.include(riderDestinationLatLng);
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                riderMaps.animateCamera(cu, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFinish() {
                        CameraUpdate zOut = CameraUpdateFactory.zoomBy(-1.0f);
                        riderMaps.animateCamera(zOut);
                    }
                });
            }
            if (vehicleType.equalsIgnoreCase(DOUBLE_QUOTES)) {
                vehicleType = CAR;
            }
            if (mLastKnownLocation != null) {
                getAllNearestDriver();
            }

            if (riderPickupMarker != null) {
                riderPickupMarker.setVisible(true);
            }

            if (riderDestinationMarker != null) {
                riderDestinationMarker.setVisible(true);
            }

            if (backgroundPolyline != null) {
                backgroundPolyline.setVisible(true);
            }

            if (foregroundPolyline != null) {
                foregroundPolyline.setVisible(true);
            }

            if (polyLineAnimator != null) {
                polyLineAnimator.start();
            }


            //carMarker.setVisible(true);
            confirm_pickup_container.setVisibility(View.GONE);
            locationMarkerContainer.setVisibility(View.GONE);
            carInfoContainer.setVisibility(View.VISIBLE);
            riderDestination.setText("");
            //  riderConfirmAddressBar.setText("");
        } else if (backStage == 3) {
            moveTaskToBack(true);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                if (fm.getBackStackEntryCount() > 1) {
                    fm.popBackStack();
                } else {
                    if (doubleBackPressed) {
                        moveTaskToBack(true);
                        // finish();
                    } else {
                        doubleBackPressed = true;

                        Snackbar.make(drawerLayout, R.string.back_press, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_dismiss, snackBarDismissListener).show();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleBackPressed = false;
                            }
                        }, 3000);
                    }
                }
            }
            // toolbar_in_maps.setVisibility(View.VISIBLE);
        }
    }

    private void showPaymentSystem() {
        tvCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setClassName(RiderMainActivity.this, "org.autoride.autoride.utils.PaymentMethodActivity");
                startActivity(intent);
            }
        });
    }

    private void showFareDetail() {
        cashInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RiderMainActivity.this, FareDetailActivity.class);
                if (pager.getCurrentItem() == 0) {
                    intent.putExtra("base_fare", fareInfo.getBikeBaseFare());
                    intent.putExtra("minimum_fare", fareInfo.getBikeMinimumFare());
                    intent.putExtra("fare_per_minute", fareInfo.getBikeFarePerMinute());
                    intent.putExtra("fare_per_km", fareInfo.getBikeFarePerKm());
                    intent.putExtra("estimate_toll", 0.00);
                    intent.putExtra("estimate_sur_charge", 0.00);
                } else if (pager.getCurrentItem() == 1) {
                    intent.putExtra("base_fare", fareInfo.getCarBaseFare());
                    intent.putExtra("minimum_fare", fareInfo.getCarMinimumFare());
                    intent.putExtra("fare_per_minute", fareInfo.getCarFarePerMinute());
                    intent.putExtra("fare_per_km", fareInfo.getCarFarePerKm());
                    intent.putExtra("estimate_toll", 0.00);
                    intent.putExtra("estimate_sur_charge", 0.00);
                }
                startActivity(intent);
            }
        });
    }

    private void foundBike() {

        vehicleType = BIKE;
        getAllNearestDriver();

        if (isVehicleAvailAble) {
            tvConfirmAutoride.setText("CONFIRM BIKE");
        } else {
            tvConfirmAutoride.setText("BIKES NOT AVAILABLE");
        }

        // tvConfirmAutoride.setText("CONFIRM BIKE");  //  BIKES NOT AVAILABLE
        ivFoundCar.setImageResource(R.drawable.ic_scooter);  //  ic_silent

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(RIDER_SEARCH_VEHICLE_TYPE, BIKE);
        editor.commit();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        ivFoundCar.setVisibility(View.VISIBLE);
        ivFoundCar.setColorFilter(ivFoundCar.getContext().getResources().getColor(R.color.scooterColorEnable), PorterDuff.Mode.SRC_ATOP);

        animatorSet.start();
    }

    private void foundCar() {

        vehicleType = CAR;
        getAllNearestDriver();

        if (isVehicleAvailAble) {
            tvConfirmAutoride.setText("CONFIRM CAR");
        } else {
            tvConfirmAutoride.setText("CAR NOT AVAILABLE");
        }

        ivFoundCar.setImageResource(R.drawable.ic_autoride_hire1);  //  ic_tuk_tuk

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(RIDER_SEARCH_VEHICLE_TYPE, CAR);
        editor.commit();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        ivFoundCar.setVisibility(View.VISIBLE);
        ivFoundCar.setColorFilter(ivFoundCar.getContext().getResources().getColor(R.color.scooterColorEnable), PorterDuff.Mode.SRC_ATOP);

        animatorSet.start();
    }

    private void foundPremier() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        ivFoundCar.setVisibility(View.VISIBLE);
        ivFoundCar.setImageResource(R.drawable.ic_autoride_premier);
        tvConfirmAutoride.setText("CONFIRM PREMIER");
        ivFoundCar.setColorFilter(ivFoundCar.getContext().getResources().getColor(R.color.scooterColorEnable), PorterDuff.Mode.SRC_ATOP);
        animatorSet.start();
    }

    private void foundAutoRideX() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        ivFoundCar.setVisibility(View.VISIBLE);
        ivFoundCar.setImageResource(R.drawable.ic_autoridexx);
        tvConfirmAutoride.setText("CONFIRM AUTORIDEX");
        ivFoundCar.setColorFilter(ivFoundCar.getContext().getResources().getColor(R.color.scooterColorEnable), PorterDuff.Mode.SRC_ATOP);
        animatorSet.start();
    }

    private void foundAutoRideHire2() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        ivFoundCar.setVisibility(View.VISIBLE);
        ivFoundCar.setImageResource(R.drawable.ic_autoride_hire1);
        tvConfirmAutoride.setText("CONFIRM HIRE 2HR");
        ivFoundCar.setColorFilter(ivFoundCar.getContext().getResources().getColor(R.color.scooterColorEnable), PorterDuff.Mode.SRC_ATOP);
        animatorSet.start();
    }

    private void foundAutoRideHire4() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(ivFoundCar, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        ivFoundCar.setVisibility(View.VISIBLE);
        ivFoundCar.setImageResource(R.drawable.ic_autoride_hire2);
        tvConfirmAutoride.setText("CONFIRM HIRE 4HR");
        ivFoundCar.setColorFilter(ivFoundCar.getContext().getResources().getColor(R.color.scooterColorEnable), PorterDuff.Mode.SRC_ATOP);
        animatorSet.start();
    }

    private void controlPage() {
        carInfoContainer.setVisibility(View.VISIBLE);
        if (pager.getSliderState() == ExpandablePager.STATE_HIDDEN) {
            findViewById(R.id.carDetails).setVisibility(View.GONE);
            pager.animateToState(ExpandablePager.STATE_HIDDEN);
            findViewById(R.id.carContent).setVisibility(View.GONE);
        }
        if (pager.getSliderState() == ExpandablePager.STATE_COLLAPSED) {
            findViewById(R.id.carDetails).setVisibility(View.VISIBLE);
            pager.animateToState(ExpandablePager.STATE_COLLAPSED);
            findViewById(R.id.carContent).setVisibility(View.VISIBLE);
        }
    }

    private void toggleContent(final View page, final int state, int duration) {
        final int headerHeight = (int) getResources().getDimension(R.dimen.header_height);
        if (page != null && isTablet()) {
            ValueAnimator animator = state == ExpandablePager.STATE_EXPANDED ? ValueAnimator.ofFloat(1, 0) : ValueAnimator.ofFloat(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    page.findViewById(R.id.header_title).setTranslationY(25 * (1 - ((Float) animation.getAnimatedValue())));
                    page.findViewById(R.id.header_title).setTranslationX(-headerHeight * (1 - ((Float) animation.getAnimatedValue())));
                    page.findViewById(R.id.header_subtitle).setAlpha(((Float) animation.getAnimatedValue()));
                    page.findViewById(R.id.header_img).setAlpha(((Float) animation.getAnimatedValue()));
                    page.findViewById(R.id.txtPerson).setAlpha(((Float) animation.getAnimatedValue()));
                }
            });
            animator.setDuration((long) (duration * .5));
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(state == ExpandablePager.STATE_EXPANDED ? 0 : getResources().getDisplayMetrics().density * 8);
    }

    private boolean isTablet() {
        int sizeMask = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        return sizeMask == Configuration.SCREENLAYOUT_SIZE_LARGE
                || sizeMask == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");
                //   Toast.makeText(con, "ADRESS" + mAreaOutput, Toast.LENGTH_SHORT).show();
                mLocationAddress.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSetAllNearestDriverTask() {
        handler.post(setAllNearestDriver);
    }

    private void stopSetAllNearestDriverTask() {
        if (setAllNearestDriver != null) {
            handler.removeCallbacks(setAllNearestDriver);
        }
    }

    private Runnable setAllNearestDriver = new Runnable() {
        @Override
        public void run() {
            if (vehicleType.equalsIgnoreCase(DOUBLE_QUOTES)) {
                vehicleType = CAR;
            }
            if (mLastKnownLocation != null) {
                getAllNearestDriver();
            }
            int HALF_MINUTE = 30000;
            handler.postDelayed(setAllNearestDriver, HALF_MINUTE);
        }
    };

    private void startVehicleAnimation() {
        handler.post(driverLocationChecker);
    }

    private void stopVehicleAnimation() {
        if (driverLocationChecker != null) {
            handler.removeCallbacks(driverLocationChecker);
        }
    }

    private Runnable driverLocationChecker = new Runnable() {
        @Override
        public void run() {
            getTrpDriverLocation();
            handler.postDelayed(driverLocationChecker, 8000);
        }
    };

    private void riderSeeVehicleDirection(final double dLat, final double dLng, double rLat, double rLng) {
        try {

            allMarkersRemover();

            if (carMarker != null) {
                carMarker.remove();
            }

            if (bikeMarker != null) {
                bikeMarker.remove();
            }

           /* String requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + dLat + "," + dLng + "&" +
                    "destination=" + rLat + "," + rLng + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api_key);

            Log.i(TAG, "requestApi requestRoute" + requestApi);*/

            mService.getPath(Common.directionsApi(new LatLng(dLat, dLng), new LatLng(rLat, rLng), this)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyLine = poly.getString("points");
                            polyLineList = Common.decodePoly(polyLine);
                        }

                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);
                        tripPlace = legsObject.getString("end_address");

                        if (ridingModeStatus != null && ridingModeStatus.equalsIgnoreCase(MODE_STATUS_RIDING)) {
                            if (tvRiderDestination != null) {
                                tvRiderDestination.setText(tripPlace);
                            }
                        }

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : polyLineList) {
                            builder.include(latLng);
                        }
                        LatLngBounds bounds = builder.build();
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                        riderMaps.animateCamera(mCameraUpdate, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onFinish() {
                                CameraUpdate zOut = CameraUpdateFactory.zoomBy(-1.0f);
                                riderMaps.animateCamera(zOut);
                            }
                        });

                        PolylineOptions blackPolylineOptions = new PolylineOptions();
                        blackPolylineOptions.color(Color.parseColor("#FFA000"));
                        blackPolylineOptions.width(10);
                        blackPolylineOptions.startCap(new SquareCap());
                        blackPolylineOptions.endCap(new SquareCap());
                        blackPolylineOptions.jointType(JointType.ROUND);
                        blackPolylineOptions.addAll(polyLineList);
                        blackPolyline = riderMaps.addPolyline(blackPolylineOptions);

                        IconGenerator iconGen = new IconGenerator(RiderMainActivity.this);
                        // Define the size you want from dimensions file
                        int shapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                        Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rider_destination_marker, null);
                        iconGen.setBackground(shapeDrawable);
                        // Create a view container to set the size
                        View view = new View(RiderMainActivity.this);
                        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
                        iconGen.setContentView(view);
                        // Create the bitmap
                        Bitmap bitmap = iconGen.makeIcon();
                        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 30, 30, false);
                        riderMaps.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(bitmapResized)));

                        startVehicleAnimation();
                        Common.stopWaitingDialog(pDialog);
                    } catch (Exception e) {
                        Common.stopWaitingDialog(pDialog);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Common.stopWaitingDialog(pDialog);
                    Log.i(TAG, "Throwable " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            e.printStackTrace();
        }
    }

    private void getTrpDriverLocation() {
        if (AutoRideRiderApps.isNetworkAvailable()) {
            ManagerData.driverTaskManager(GET, DRIVER_LOCATION_URL, tripDriverJSON(), null, new ParserListenerDriver() {
                @Override
                public void onLoadCompleted(String driverInfo) {
                    if (driverInfo != null) {
                        trpDriverLocationParser(driverInfo);
                    } else {
                        snackBarSlowInternet();
                        Log.i(TAG, "driverInLocationCheck " + UNABLE_FOUND_DRIVER);
                    }
                }

                @Override
                public void onLoadFailed(String driverInfo) {
                    snackBarSlowInternet();
                    Log.i(TAG, "driverInLocationCheck " + UNABLE_FOUND_DRIVER);
                }
            });
        } else {
            snackBarNoInternet();
        }
    }

    private JSONObject tripDriverJSON() {
        JSONObject postData = new JSONObject();
        try {
            postData.put(DRIVER_IDS, driverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private void trpDriverLocationParser(String response) {
        JSONObject responseObj = null;
        try {
            if (response != null) {
                responseObj = new JSONObject(response);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(200))) {

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                            if (responseObj.has(WEB_RESPONSE_DATA)) {
                                JSONObject dataObj = responseObj.optJSONObject(WEB_RESPONSE_DATA);
                                if (dataObj != null) {
                                    JSONObject locationObj = dataObj.optJSONObject(DRIVER_LOCATION);
                                    if (locationObj != null) {

                                        startLatitude = locationObj.getDouble(LAT);
                                        startLongitude = locationObj.getDouble(LNG);
                                        Log.i(TAG, "locations " + startLatitude + "--" + startLongitude);

                                        if (isFirstPosition) {

                                            startPosition = new LatLng(startLatitude, startLongitude);

                                            if (vTypes.equalsIgnoreCase(CAR)) {
                                                carMarker = riderMaps.addMarker(new MarkerOptions().position(startPosition).
                                                        flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_marker_icon)));
                                                carMarker.setAnchor(0.5f, 0.5f);
                                            } else if (vTypes.equalsIgnoreCase(BIKE)) {
                                                bikeMarker = riderMaps.addMarker(new MarkerOptions().position(startPosition).
                                                        flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_marker_icon)));
                                                bikeMarker.setAnchor(0.5f, 0.5f);
                                            }

                                           /* riderMaps.moveCamera(CameraUpdateFactory
                                                    .newCameraPosition
                                                            (new CameraPosition.Builder()
                                                                    .target(startPosition)
                                                                    .zoom(15.5f)
                                                                    .build())); */

                                            isFirstPosition = false;
                                        } else {

                                            endPosition = new LatLng(startLatitude, startLongitude);

                                            Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                            if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {
                                                Log.i(TAG, "NOT SAME");
                                                showVehicleAnimation(startPosition, endPosition, vTypes);
                                            } else {
                                                Log.i(TAG, "SAME");
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            snackBarSlowInternet();
                            Log.i(TAG, "driverInLocationCheck " + UNABLE_FOUND_DRIVER);
                            // Toast.makeText(this, UNABLE_FOUND_DRIVER, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        snackBarSlowInternet();
                        Log.i(TAG, "driverInLocationCheck " + WEB_ERRORS_MESSAGE);
                        // Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        snackBarSlowInternet();
                        Log.i(TAG, "driverInLocationCheck " + WEB_ERRORS_MESSAGE);
                        // Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        snackBarSlowInternet();
                        Log.i(TAG, "driverInLocationCheck " + WEB_ERRORS_MESSAGE);
                        // Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        snackBarSlowInternet();
                        Log.i(TAG, "driverInLocationCheck " + WEB_ERRORS_MESSAGE);
                        // Toast.makeText(this, WEB_ERRORS_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            snackBarSlowInternet();
            e.printStackTrace();
        }
    }

    private void showVehicleAnimation(final LatLng start, final LatLng end, final String vehicleTypes) {

        ValueAnimator vehicleAnimator = ValueAnimator.ofFloat(0, 1);
        vehicleAnimator.setDuration(ANIMATION_TIME_PER_ROUTE);
        vehicleAnimator.setInterpolator(new LinearInterpolator());
        vehicleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float v = valueAnimator.getAnimatedFraction();
                double lng = v * end.longitude + (1 - v) * start.longitude;
                double lat = v * end.latitude + (1 - v) * start.latitude;
                LatLng newPosition = new LatLng(lat, lng);

                if (vehicleTypes.equalsIgnoreCase(CAR)) {
                    carMarker.setPosition(newPosition);
                    carMarker.setAnchor(0.5f, 0.5f);
                    carMarker.setRotation(getBearing(start, end));
                } else if (vehicleTypes.equalsIgnoreCase(BIKE)) {
                    bikeMarker.setPosition(newPosition);
                    bikeMarker.setAnchor(0.5f, 0.5f);
                    bikeMarker.setRotation(getBearing(start, end));
                }

                riderMaps.moveCamera(CameraUpdateFactory
                        .newCameraPosition
                                (new CameraPosition.Builder()
                                        .target(newPosition)
                                        .zoom(15.5f)
                                        .build()));

                if (vehicleTypes.equalsIgnoreCase(CAR)) {
                    startPosition = carMarker.getPosition();
                } else if (vehicleTypes.equalsIgnoreCase(BIKE)) {
                    startPosition = bikeMarker.getPosition();
                }

            }
        });
        vehicleAnimator.start();
    }

    private float getBearing(LatLng begin, LatLng end) {

        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        } else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        } else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        } else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        }
        return -1;
    }

    private void setDestinationRoute() {
        try {
            Common.startWaitingDialog(pDialog);

         /*   String requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + riderPickupLatLng.latitude + "," + riderPickupLatLng.longitude + "&" +
                    "destination=" + riderDestinationLatLng.latitude + "," + riderDestinationLatLng.longitude + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api_key);

            Log.i(TAG, "requestApi destSearch" + requestApi);*/

            mService.getPath(Common.directionsApi(riderPickupLatLng, riderDestinationLatLng, this)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyLine = poly.getString("points");
                            polyLineList = Common.decodePoly(polyLine);
                        }

                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject distanceObj = legsObject.getJSONObject("distance");
                        String distanceText = distanceObj.getString("text");
                        riderDestinationDistance = Double.parseDouble(distanceText.replaceAll("[^0-9\\\\.]+", ""));

                        JSONObject durationObj = legsObject.getJSONObject("duration");
                        String durationText = durationObj.getString("text");
                        riderDestinationDuration = Double.parseDouble(durationText.replaceAll("[^0-9\\\\.]+", ""));

                        riderMaps.setPadding(0, 0, 0, ScreenUtility.getDeviceHeight(RiderMainActivity.this));
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : polyLineList) {
                            builder.include(latLng);
                        }
                        LatLngBounds bounds = builder.build();
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                        riderMaps.animateCamera(mCameraUpdate, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onFinish() {
                                CameraUpdate zOut = CameraUpdateFactory.zoomBy(-1.0f);
                                riderMaps.animateCamera(zOut);
                            }
                        });

                        PolylineOptions backgroundPolylineOptions = new PolylineOptions();
                        backgroundPolylineOptions.color(Color.WHITE);
                        backgroundPolylineOptions.width(10);
                        backgroundPolylineOptions.startCap(new SquareCap());
                        backgroundPolylineOptions.endCap(new SquareCap());
                        backgroundPolylineOptions.jointType(JointType.ROUND);
                        backgroundPolylineOptions.addAll(polyLineList);
                        backgroundPolyline = riderMaps.addPolyline(backgroundPolylineOptions);

                        PolylineOptions foregroundPolylineOptions = new PolylineOptions();
                        foregroundPolylineOptions.color(Color.parseColor("#FFA000"));
                        foregroundPolylineOptions.width(6);
                        foregroundPolylineOptions.startCap(new SquareCap());
                        foregroundPolylineOptions.endCap(new SquareCap());
                        foregroundPolylineOptions.jointType(JointType.ROUND);
                        foregroundPolyline = riderMaps.addPolyline(foregroundPolylineOptions);

                        polyLineAnimator = ValueAnimator.ofInt(0, 100);
                        polyLineAnimator.setDuration(ANIMATION_TIME_PER_ROUTE);
                        polyLineAnimator.setInterpolator(new LinearInterpolator());
                        polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                List<LatLng> points = backgroundPolyline.getPoints();
                                int percentValue = (int) valueAnimator.getAnimatedValue();
                                int size = points.size();
                                int newPoints = (int) (size * (percentValue / 100.0f));
                                List<LatLng> p = points.subList(0, newPoints);
                                p.clear();
                                foregroundPolyline.setPoints(points);
                            }
                        });
                        polyLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
                        polyLineAnimator.setRepeatMode(ValueAnimator.RESTART);
                        polyLineAnimator.start();

                        setFare();
                        if (riderDestinationMarker != null) {
                            riderDestinationMarker.remove();
                        }

                        if (riderPickupMarker != null) {
                            riderPickupMarker.remove();
                        }

                        mapsLocIconDisable();

                        IconGenerator iconGen1 = new IconGenerator(RiderMainActivity.this);
                        int shapeSize1 = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                        Drawable shapeDrawablePickup = ResourcesCompat.getDrawable(getResources(), R.drawable.rider_pickup_marker, null);
                        iconGen1.setBackground(shapeDrawablePickup);
                        View view1 = new View(RiderMainActivity.this);
                        view1.setLayoutParams(new ViewGroup.LayoutParams(shapeSize1, shapeSize1));
                        iconGen1.setContentView(view1);

                        Bitmap bitmapPickup = iconGen1.makeIcon();
                        Bitmap bitmapResizedPickup = Bitmap.createScaledBitmap(bitmapPickup, 34, 34, false);
                        riderPickupMarker = riderMaps.addMarker(new MarkerOptions().position(new LatLng(riderPickupLatLng.latitude, riderPickupLatLng.longitude)).icon(BitmapDescriptorFactory.fromBitmap(bitmapResizedPickup)));
                        //   riderMaps.setInfoWindowAdapter(new CustomInfoWindowRider(RiderMainActivity.this, null, "My Location"));
                        riderPickupMarker.showInfoWindow();

                        IconGenerator iconGen = new IconGenerator(RiderMainActivity.this);
                        // Define the size you want from dimensions file
                        int shapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);

                        Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rider_destination_marker, null);
                        iconGen.setBackground(shapeDrawable);

                        // Create a view container to set the size
                        View view = new View(RiderMainActivity.this);
                        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
                        iconGen.setContentView(view);


                        // Create the bitmap
                        Bitmap bitmap = iconGen.makeIcon();

                        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 30, 30, false);

                        riderDestinationMarker = riderMaps.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(bitmapResized)));

                        riderMaps.setInfoWindowAdapter(new CustomInfoWindow(RiderMainActivity.this, null, riderDestinationPlace));
                        riderDestinationMarker.showInfoWindow();
                        Common.stopWaitingDialog(pDialog);
                    } catch (Exception e) {
                        Common.stopWaitingDialog(pDialog);
                        Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Common.stopWaitingDialog(pDialog);
                    Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Throwable " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void performGetLastTrip() {
        if (AutoRideRiderApps.isNetworkAvailable()) {
            try {
                new GetLastTripStatus().execute(RIDING_MODE_URL).get();
            } catch (Exception e) {
                Common.stopWaitingDialog(pDialog);
                e.printStackTrace();
            }
        } else {
            // message no internet show time of fire base token update so no need show message here
            Common.stopWaitingDialog(pDialog);
        }
    }

    public void goUserAccount(View view) {
        startActivity(new Intent(this, RiderTransactionActivity.class));
    }

    public void goUserTracking(View view) {
        startActivity(new Intent(this,RiderTrackingActivity.class));
    }

    private class GetLastTripStatus extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.GET(
                        client,
                        RequestedUrlBuilder.buildRequestedGETUrl(url[0], getBodyJSON(null, null)),
                        RequestedHeaderBuilder.buildRequestedHeader(getHeaderJSON())
                );
            } catch (Exception e) {
                Log.i(TAG, ERROR_RESPONSE + e.toString());
                e.printStackTrace();
            }
            Log.i(TAG, HTTP_RESPONSE + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            lastTripParser(s);
        }
    }

    private void lastTripParser(String response) {
        TripDetailsInfo tripInfo;
        JSONObject responseObj = null;
        try {
            if (response != null) {
                responseObj = new JSONObject(response);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {
                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(WEB_RESPONSE_CODE_200))) {
                        if (responseObj.optString(RIDING_MODE_STATUS).equalsIgnoreCase(MODE_STATUS_RIDING)) {

                            ridingModeStatus = MODE_STATUS_RIDING;
                            setDriverInfoUi();

                            //testing part
                            // findViewById(R.id.ll_trip_cancel).setVisibility(View.VISIBLE);
                            floatingLayout.setVisibility(View.GONE);
                            imgExpandableMessage.setVisibility(View.GONE);
                            isRiderTripProcessing = false;
                            stopSetAllNearestDriverTask();

                            //testing
                            //llShowLocationSearchBox.setVisibility(View.GONE);
                            llRiderDestinationSearchBar.setVisibility(View.GONE);

                            llRiderConfirmAddressBar.setVisibility(View.GONE);
                            riderDestination.setText("");
                            isRequestProcessing = true;

                            bottomSheetContainer.setVisibility(View.VISIBLE);

                            tripInfo = new TripDetailsInfo();

                            if (responseObj.has(TRIP_DETAILS)) {
                                JSONObject tripObj = responseObj.optJSONObject(TRIP_DETAILS);
                                if (tripObj != null) {

                                    if (tripObj.has(PREDICTED_AMOUNT)) {
                                        tripInfo.setPredictedAmount(tripObj.getDouble(PREDICTED_AMOUNT));
                                    }

                                    if (tripObj.has(PREDICTED_KILOMETER)) {
                                        tripInfo.setPredictedKilometer(tripObj.getDouble(PREDICTED_KILOMETER));
                                    }

                                    if (tripObj.has(PREDICTED_MINUTE)) {
                                        tripInfo.setPredictedMinute(tripObj.getDouble(PREDICTED_MINUTE));
                                    }

                                    if (tripObj.has(PICKUP_LOCATION)) {
                                        JSONObject pickObj = tripObj.optJSONObject(PICKUP_LOCATION);
                                        if (pickObj != null) {
                                            if (pickObj.has(LAT)) {
                                                tripInfo.setPickupLat(pickObj.getDouble(LAT));
                                            }

                                            if (pickObj.has(LNG)) {
                                                tripInfo.setPickupLng(pickObj.getDouble(LNG));
                                            }
                                        }
                                    }

                                    if (tripObj.has(DESTINATION)) {
                                        JSONObject destObj = tripObj.optJSONObject(DESTINATION);
                                        if (destObj != null) {
                                            if (destObj.has(LAT)) {
                                                tripInfo.setDestLat(destObj.getDouble(LAT));
                                            }

                                            if (destObj.has(LNG)) {
                                                tripInfo.setDestLng(destObj.getDouble(LNG));
                                            }
                                        }
                                    }
                                }
                            }

                            if (responseObj.has(VEHICLE_DETAILS)) {
                                JSONObject vehicleObj = responseObj.optJSONObject(VEHICLE_DETAILS);
                                if (vehicleObj != null) {
                                    if (vehicleObj.has(VEHICLE_TYPE)) {
                                        tripInfo.setVehicleType(vehicleObj.getString(VEHICLE_TYPE));
                                        vTypes = vehicleObj.getString(VEHICLE_TYPE);
                                    }

                                    if (vehicleObj.has(VEHICLE_BRAND)) {
                                        tripInfo.setVehicleBrand(vehicleObj.getString(VEHICLE_BRAND));
                                    }

                                    if (vehicleObj.has(VEHICLE_MODEL)) {
                                        tripInfo.setVehicleModel(vehicleObj.getString(VEHICLE_MODEL));
                                    }

                                    if (vehicleObj.has(VEHICLE_NUMBER)) {
                                        tripInfo.setVehicleNumber(vehicleObj.getString(VEHICLE_NUMBER));
                                    }
                                }
                            }

                            if (responseObj.has(DRIVER_DETAILS)) {
                                JSONObject driverObj = responseObj.optJSONObject(DRIVER_DETAILS);
                                if (driverObj != null) {

                                    if (driverObj.has(DRIVER_IDS)) {
                                        tripInfo.setDriverId(driverObj.getString(DRIVER_IDS));
                                        driverId = driverObj.getString(DRIVER_IDS);
                                    }

                                    if (driverObj.has(FIRE_BASE_TOKEN)) {
                                        tripInfo.setRiderFcmToken(driverObj.getString(FIRE_BASE_TOKEN));
                                    }

                                    if (driverObj.has(PHONE)) {
                                        tripInfo.setDriverPhone(driverObj.getString(PHONE));
                                    }

                                    if (driverObj.has(FIRST_NAME)) {
                                        tripInfo.setDriverFirstName(driverObj.getString(FIRST_NAME));
                                    }

                                    if (driverObj.has(LAST_NAME)) {
                                        tripInfo.setDriverLastName(driverObj.getString(LAST_NAME));
                                    }

                                    if (driverObj.has(PROFILE_PHOTO)) {
                                        tripInfo.setDriverPhoto(driverObj.getString(PROFILE_PHOTO));
                                    }

                                    if (driverObj.has(COVER_PHOTO)) {
                                        tripInfo.setVehiclePhoto(driverObj.getString(COVER_PHOTO));
                                    }

                                    if (driverObj.has(RATING)) {
                                        tripInfo.setDriverRating(driverObj.getString(RATING));
                                    }
                                }
                            }

                            if (responseObj.has(RIDER_DETAILS)) {
                                JSONObject userObj = responseObj.optJSONObject(RIDER_DETAILS);
                                if (userObj != null) {

                                    if (userObj.has(RIDER_ID)) {
                                        tripInfo.setRiderId(userObj.getString(RIDER_ID));
                                    }

                                    if (userObj.has(FIRE_BASE_TOKEN)) {
                                        tripInfo.setRiderFcmToken(userObj.getString(FIRE_BASE_TOKEN));
                                    }

                                    if (userObj.has(PHONE)) {
                                        tripInfo.setRiderPhone(userObj.getString(PHONE));
                                    }

                                    if (userObj.has(FIRST_NAME)) {
                                        tripInfo.setRiderFirstName(userObj.getString(FIRST_NAME));
                                    }

                                    if (userObj.has(LAST_NAME)) {
                                        tripInfo.setRiderLastName(userObj.getString(LAST_NAME));
                                    }

                                    if (userObj.has(PROFILE_PHOTO)) {
                                        tripInfo.setRiderPhoto(userObj.getString(PROFILE_PHOTO));
                                    }

                                    if (userObj.has(RATING)) {
                                        tripInfo.setRiderRating(userObj.getString(RATING));
                                    }
                                }
                            }
                            showTripStage(tripInfo);
                        } else {
                            // handle here
                            Common.stopWaitingDialog(pDialog);
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        Common.stopWaitingDialog(pDialog);
                        snackBarSlowInternet();
                    }
                } else {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }

                if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                    Common.stopWaitingDialog(pDialog);
                    snackBarSlowInternet();
                }
            } else {
                Common.stopWaitingDialog(pDialog);
                snackBarSlowInternet();
            }
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            snackBarSlowInternet();
            e.printStackTrace();
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void showTripStage(final TripDetailsInfo tripDetails) {

        riderMapsFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                riderMaps = googleMap;
                riderMaps.setTrafficEnabled(false);
                riderMaps.setIndoorEnabled(false);
                riderMaps.setBuildingsEnabled(false);
                riderMaps.getUiSettings().setZoomControlsEnabled(false);
                riderSeeVehicleDirection(tripDetails.getPickupLat(), tripDetails.getPickupLng(), tripDetails.getDestLat(), tripDetails.getDestLng());
            }
        });

        double timeDouble = tripDetails.getPredictedMinute();
        String timeUnit;
        if ((int) timeDouble < 2) {
            timeUnit = "MINUTE";
        } else {
            timeUnit = "MINUTES";
        }
        final String arrivingTime = (int) timeDouble + " " + timeUnit;

        Glide.with(getBaseContext())
                .load(tripDetails.getDriverPhoto())
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop()
                        .fitCenter()
                        .error(R.drawable.ic_profile_photo_default)
                        .fallback(R.drawable.ic_profile_photo_default))
                .into(civAcceptedDriverPhoto);

        tvDriverFullName.setText(tripDetails.getDriverFirstName());
        tvVehicleBrand.setText(tripDetails.getVehicleBrand() + " " + tripDetails.getVehicleModel());
        tvDriverRating.setText((tripDetails.getDriverRating().equalsIgnoreCase("null") ? "0.0" : tripDetails.getDriverRating()));
        tvVehicleDesc.setText(tripDetails.getVehicleNumber());

        tvArrivingNowTime.setText(arrivingTime);
        tvDriverFullName2.setText(tripDetails.getDriverFirstName());
        tvVehicleBrand2.setText(tripDetails.getVehicleBrand() + " " + tripDetails.getVehicleModel());
        tvDriverRating2.setText((tripDetails.getDriverRating().equalsIgnoreCase("null") ? "0.0" : tripDetails.getDriverRating()));
        tvVehicleDesc2.setText(tripDetails.getVehicleNumber());
        // tvRiderDestination.setText(tripPlace);

        tvArrivalTime.setText("11:11 am arrival");
        tvPaymentType.setText("Cash");

        tvPaymentAmount.setText(CURRENCY + String.format("%.2f", tripDetails.getPredictedAmount()));
        tvTripStatus.setText("Trip Running");
        tvRiderRideCancelClose.setText("Close");
        // collapsingToolbar.setTitle("Arriving Stage");
        tvDriverName.setText(tripDetails.getDriverFirstName());
        driverPhone = tripDetails.getDriverPhone();
        tvDriverPhone.setText(driverPhone);
        Glide.with(getBaseContext())
                .load(tripDetails.getDriverPhoto())
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop()
                        .fitCenter()
                        .error(R.drawable.ic_profile_photo_default)
                        .fallback(R.drawable.ic_profile_photo_default))
                .into(civAcceptedDriverPhoto2);

        Glide.with(getBaseContext())
                .load(tripDetails.getVehiclePhoto())
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop()
                        .fitCenter()
                        .error(R.drawable.ic_profile_photo_default)
                        .fallback(R.drawable.ic_profile_photo_default))
                .into(ivAcceptedDriverVehicle);
    }

    private void snackBarNoInternet() {
        Snackbar.make(drawerLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
    }

    private void snackBarSlowInternet() {
        Snackbar.make(drawerLayout, R.string.slow_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissListener).show();
    }

    private void logOutHere() {
        AutoRideRiderApps.logout();
        Intent intent = new Intent(RiderMainActivity.this, RiderWelcomeActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
        RiderMainActivity.this.finish();
    }

    private void mapsLocIconEnabled() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        riderMaps.setMyLocationEnabled(true);
        locButton.setVisibility(View.GONE);
    }

    private void mapsLocIconDisable() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        riderMaps.setMyLocationEnabled(false);
        locButton.setVisibility(View.GONE);
    }

    //REFERENCE
    public void onShowMyUser(View view) {
        if (myrReferenceItemsList.size() != 0) {
            Intent intent = new Intent(RiderMainActivity.this, ReferenceActivity.class);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
            RiderMainActivity.this.finish();
        }
    }

    public void uploadReference() {
        if (checkConnectivity()) {
            try {
                getReference();
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
        Snackbar.make(drawerLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_setting), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();
    }

    public void getReference() throws Exception {
        String url = Constants.URL + riderId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Result", response);
                parseReference(response);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AutoRideRiderApps.getInstance().addToRequestQueue(stringRequest);
    }

    public void parseReference(String response) {

        JSONArray jsonArray;
        JSONObject referenceObject;

        try {
            referenceObject = new JSONObject(response);
            //  jsonArray = new JSONArray(new String(response));
            jsonArray = referenceObject.getJSONArray("reference");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject addressObject = jsonObject.getJSONObject("address");
                JSONObject registerLocationObject = jsonObject.getJSONObject("registeredLocation");

                if (isContain(jsonObject, "name")) {

                    if (jsonObject.getString("name").equalsIgnoreCase("null")) {
                        referenceItem.name = "--";
                    } else {
                        referenceItem.name = jsonObject.getString("name");
                    }
                } else {
                    referenceItem.name = NA;
                }
                if (isContain(jsonObject, "status")) {

                    if (jsonObject.getString("status").equalsIgnoreCase("null")) {
                        referenceItem.status = "--";
                    } else {
                        referenceItem.status = jsonObject.getString("status");
                    }

                } else {
                    referenceItem.status = NA;
                }
                if (isContain(jsonObject, "address")) {
                    referenceItem.address = jsonObject.getString("address");

                    for (int addressIndex = 0; addressIndex < addressObject.length(); addressIndex++) {

                        String house = addressObject.getString("house");
                        if (house.equalsIgnoreCase("null")) {
                            house = "--";
                        }
                        String road = addressObject.getString("road");
                        if (road.equalsIgnoreCase("null")) {
                            road = "--";
                        }
                        String stateProvince = addressObject.getString("stateProvince");
                        if (stateProvince.equalsIgnoreCase("null")) {
                            stateProvince = "district--";
                        }
                        String country = addressObject.getString("country");
                        if (country.equalsIgnoreCase("null")) {
                            country = "country--";
                        }
                        String unit = addressObject.getString("unit");
                        if (unit.equalsIgnoreCase("null")) {
                            unit = "--";
                        }

                        String zipCode = addressObject.getString("zipCode");
                        if (zipCode.equalsIgnoreCase("null")) {
                            zipCode = "--";
                        }
                        String fax = addressObject.getString("fax");
                        if (fax.equalsIgnoreCase("null")) {
                            fax = "--";
                        }
                        referenceItem.address = "House# " + house + ", " + "Road# " + road + "\n" + "Unit# " + unit + "," + "Zipcode# " + zipCode + "," + "Fax# " + fax + "\n" + stateProvince + ", " + country;

                    }
                } else {
                    referenceItem.address = NA;
                }
                if (isContain(jsonObject, "registeredLocation")) {

                    referenceItem.registeredLocation = jsonObject.getString("registeredLocation");
                    for (int latIndex = 0; latIndex < registerLocationObject.length(); latIndex++) {

                        String lng = registerLocationObject.getString("lng");
                        String lat = registerLocationObject.getString("lat");
                        double longitude = Double.parseDouble(lng);
                        double latitude = Double.parseDouble(lat);
                        if (longitude == 0 & latitude == 0) {
                            referenceItem.registeredLocation = "Registered Location --";
                        } else {

                            try {
                                Geocoder geocoder = new Geocoder(getBaseContext());
                                if (longitude != 0 & latitude != 0) {
                                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                    Address address = addressList.get(0);
                                    String riderSearchPlace = address.getAddressLine(0);
                                    referenceItem.registeredLocation = riderSearchPlace;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    referenceItem.registeredLocation = NA;
                }
                if (isContain(jsonObject, "accountNumber")) {

                    if (jsonObject.getString("accountNumber").equalsIgnoreCase("null")) {
                        referenceItem.accountNumber = "A/C--";
                    } else {
                        referenceItem.accountNumber = jsonObject.getString("accountNumber");
                    }

                } else {
                    referenceItem.status = NA;
                }
                if (isContain(jsonObject, "createdDate")) {

                    if (jsonObject.getString("createdDate").equalsIgnoreCase("null")) {
                        referenceItem.createdDate = "--";
                    } else {
                        referenceItem.createdDate = jsonObject.getString("createdDate");
                    }

                } else {
                    referenceItem.status = NA;
                }
                if (isContain(jsonObject, "imgeUrl")) {

                    if (jsonObject.getString("imgeUrl").equalsIgnoreCase("null")) {
                        referenceItem.imageUrl = "http://kardaconstruction.com/uploads/key-person/1510208641blank_male_avatar.jpg";
                    } else {
                        referenceItem.imageUrl = jsonObject.getString("imgeUrl");
                    }
                } else {
                    referenceItem.imageUrl = NA;
                }

//                Utils.generateReferenceItems(this).add(referenceItem);
                myrReferenceItemsList.add(new ReferenceItem(referenceItem.name, referenceItem.status, referenceItem.address, referenceItem.accountNumber, referenceItem.registeredLocation, referenceItem.createdDate, referenceItem.imageUrl));

                Log.d("referenceItemListsIZE", myrReferenceItemsList.size() + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ReferenceItem> generateReferenceItems(Context context) {
        ArrayList<ReferenceItem> referenceItems = new ArrayList<>();
        referenceItems = myrReferenceItemsList;
        return referenceItems;
    }

    public boolean isContain(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

    private class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));
            }
        }
    }

    // requested driver info setup
    private void setDriverInfoUi() {

        llDriverInfoShower = (LinearLayout) findViewById(R.id.ll_driver_info_shower);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_in_driver_info);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driverContactStage == 1) {
                    driverContactStage = 0;
                    llDriverInfoContainer.setVisibility(View.VISIBLE);
                    llDriverContact.setVisibility(View.GONE);
                } else {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_driver_info);

        bottomSheetContainer = (View) findViewById(R.id.bottom_sheet_container);
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetContainer);
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        getSupportActionBar().hide();
                        llDriverInfoShower.setVisibility(View.GONE);
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        getSupportActionBar().hide();
                        llDriverInfoShower.setVisibility(View.GONE);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        getSupportActionBar().show();
                        llDriverInfoShower.setVisibility(View.VISIBLE);
                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        llDriverInfoShower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDriverInfoShower.setVisibility(View.GONE);
                getSupportActionBar().hide();
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        llDriverInfoContainer = (LinearLayout) findViewById(R.id.ll_driver_info_container);
        llDriverContact = (LinearLayout) findViewById(R.id.ll_driver_contact);

        civAcceptedDriverPhoto2 = (CircleImageView) findViewById(R.id.civ_accepted_driver_photo2);
        ivAcceptedDriverVehicle = (ImageView) findViewById(R.id.iv_accepted_driver_vehicle);

        tvRiderRideCancelClose = (TextView) findViewById(R.id.tv_rider_ride_cancel_close);
        tvArrivingNowTime = (TextView) findViewById(R.id.tv_arriving_now_time);
        tvDriverFullName2 = (TextView) findViewById(R.id.tv_driver_full_name2);
        tvVehicleBrand2 = (TextView) findViewById(R.id.tv_vehicle_brand2);
        tvDriverRating2 = (TextView) findViewById(R.id.tv_driver_rating2);
        tvVehicleDesc2 = (TextView) findViewById(R.id.tv_vehicle_desc2);
        tvRiderDestination = (TextView) findViewById(R.id.tv_rider_destination);
        tvArrivalTime = (TextView) findViewById(R.id.tv_arrival_time);
        tvPaymentType = (TextView) findViewById(R.id.tv_payment_type);
        tvPaymentAmount = (TextView) findViewById(R.id.tv_payment_amount);
        tvTripStatus = (TextView) findViewById(R.id.tv_trip_status);

        tvDriverName = (TextView) findViewById(R.id.tv_driver_name);
        tvDriverPhone = (TextView) findViewById(R.id.tv_driver_phone);

        civAcceptedDriverPhoto = (CircleImageView) findViewById(R.id.civ_accepted_driver_photo);
        tvDriverFullName = (TextView) findViewById(R.id.tv_driver_full_name);
        tvVehicleBrand = (TextView) findViewById(R.id.tv_vehicle_brand);
        tvDriverRating = (TextView) findViewById(R.id.tv_driver_rating);
        tvVehicleDesc = (TextView) findViewById(R.id.tv_vehicle_desc);

        tvRiderChangeDestination = (TextView) findViewById(R.id.tv_rider_change_destination);
        tvRiderChangeDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationChanger();
            }
        });
    }

    private void destinationChanger() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(RiderMainActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                updateDest = place.getName().toString();
                updateDestLatLng = place.getLatLng();

                destChangeModal = new DestChangeModal();
                destChangeModal.setCancelable(false);
                destChangeModal.show(getSupportFragmentManager(), TAG);

                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void onRiderRideCancelClose(View view) {
        if (tvRiderRideCancelClose.getText().toString().equalsIgnoreCase("Cancel")) {

            requestCancelModal = new RequestCancelModal();
            requestCancelModal.setCancelable(false);
            requestCancelModal.show(getSupportFragmentManager(), TAG);

            // tripCancelDialog();
        } else if (tvRiderRideCancelClose.getText().toString().equalsIgnoreCase("Close")) {
            moveTaskToBack(true);
        }
    }

    public void onRequestCancelNo(View view) {
        if (requestCancelModal != null) {
            requestCancelModal.dismiss();
        }
    }

    public void onRequestCancelYes(View view) {
        Common.startWaitingDialog(pDialog);
        performRiderAcceptedRequestCancel();
    }

    private void performRiderAcceptedRequestCancel() {

        String driverToken = sp.getString(TRIP_DRIVER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        Common common = new Common();
        common.notificationToDriver(fcmService, driverToken, "rider_trip_canceled", "The rider has canceled ride");

        if (requestCancelModal != null) {
            requestCancelModal.dismiss();
        }

        ManagerData.driverTaskManager(POST, RIDE_CANCEL, getBodyJSON2("reason", "rider cancel ride"), null, new ParserListenerDriver() {
            @Override
            public void onLoadCompleted(String driverInfo) {
                Common.stopWaitingDialog(pDialog);
                Intent intent = new Intent(RiderMainActivity.this, RiderMainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                RiderMainActivity.this.finish();
            }

            @Override
            public void onLoadFailed(String driverInfo) {
                Common.stopWaitingDialog(pDialog);
                Intent intent = new Intent(RiderMainActivity.this, RiderMainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                RiderMainActivity.this.finish();
            }
        });
    }

    private JSONObject getBodyJSON2(String key, String value) {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put(DRIVER_IDS, riderId);
            if (key != null && value != null) {
                postBody.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    public void onContactToDriver(View view) {
        driverContactStage = 1;
        llDriverInfoContainer.setVisibility(View.GONE);
        llDriverContact.setVisibility(View.VISIBLE);
    }

    public void onRideToDriverSms(View view) {
        smsToPhone(driverPhone);
    }

    private void smsToPhone(String pNo) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + pNo)));
    }

    public void onRiderToDriverPhoneCall(View view) {
        callToPhone(driverPhone);
    }

    private void callToPhone(String pNo) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pNo)));
    }

    public void onDestChangeOk(View view) {

       /* String driverToken = sp.getString(TRIP_DRIVER_FIRE_BASE_TOKEN, DOUBLE_QUOTES);
        Common common = new Common();
        common.notificationToDriver(fcmService, driverToken, "rider_destination_update", "The rider has change destination");*/

        tvRiderDestination.setText(updateDest);
        riderDestinationPlace = updateDest;

        if (destChangeModal != null) {
            destChangeModal.dismiss();
        }

        riderDestinationLatLng = updateDestLatLng;
        setDestinationRoute(riderPickupLatLng, riderDestinationLatLng);
    }

    public void onDestChangeCancel(View view) {
        if (destChangeModal != null) {
            destChangeModal.dismiss();
        }
    }

    public static class RequestCancelModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            //super.setupDialog(dialog, style);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_request_cancel, null);
            dialog.setContentView(view);
        }
    }

    public static class DestChangeModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            //super.setupDialog(dialog, style);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dest_edit_alert, null);
            dialog.setContentView(view);
        }
    }

    private void setDestinationRoute(final LatLng pickup, LatLng destination) {
        try {
            Common.startWaitingDialog(pDialog);

           /* String requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + pickup.latitude + "," + pickup.longitude + "&" +
                    "destination=" + destination.latitude + "," + destination.longitude + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api_key);

            Log.i(TAG, "requestApi destChange" + requestApi);*/

            mService.getPath(Common.directionsApi(pickup, destination, this)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyLine = poly.getString("points");
                            polyLineList = Common.decodePoly(polyLine);
                        }

                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject distanceObj = legsObject.getJSONObject("distance");
                        String distanceText = distanceObj.getString("text");
                        riderDestinationDistance = Double.parseDouble(distanceText.replaceAll("[^0-9\\\\.]+", ""));

                        JSONObject durationObj = legsObject.getJSONObject("duration");
                        String durationText = durationObj.getString("text");
                        riderDestinationDuration = Double.parseDouble(durationText.replaceAll("[^0-9\\\\.]+", ""));

                        riderMaps.setPadding(0, 0, 0, ScreenUtility.getDeviceHeight(RiderMainActivity.this));
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : polyLineList) {
                            builder.include(latLng);
                        }
                        LatLngBounds bounds = builder.build();
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                        riderMaps.animateCamera(mCameraUpdate, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onFinish() {
                                CameraUpdate zOut = CameraUpdateFactory.zoomBy(-1.0f);
                                riderMaps.animateCamera(zOut);
                            }
                        });

                        PolylineOptions backgroundPolylineOptions = new PolylineOptions();
                        backgroundPolylineOptions.color(Color.WHITE);
                        backgroundPolylineOptions.width(10);
                        backgroundPolylineOptions.startCap(new SquareCap());
                        backgroundPolylineOptions.endCap(new SquareCap());
                        backgroundPolylineOptions.jointType(JointType.ROUND);
                        backgroundPolylineOptions.addAll(polyLineList);
                        backgroundPolyline = riderMaps.addPolyline(backgroundPolylineOptions);

                        PolylineOptions foregroundPolylineOptions = new PolylineOptions();
                        foregroundPolylineOptions.color(Color.parseColor("#FFA000"));
                        foregroundPolylineOptions.width(6);
                        foregroundPolylineOptions.startCap(new SquareCap());
                        foregroundPolylineOptions.endCap(new SquareCap());
                        foregroundPolylineOptions.jointType(JointType.ROUND);
                        foregroundPolyline = riderMaps.addPolyline(foregroundPolylineOptions);

                        polyLineAnimator = ValueAnimator.ofInt(0, 100);
                        polyLineAnimator.setDuration(ANIMATION_TIME_PER_ROUTE);
                        polyLineAnimator.setInterpolator(new LinearInterpolator());
                        polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                List<LatLng> points = backgroundPolyline.getPoints();
                                int percentValue = (int) valueAnimator.getAnimatedValue();
                                int size = points.size();
                                int newPoints = (int) (size * (percentValue / 100.0f));
                                List<LatLng> p = points.subList(0, newPoints);
                                p.clear();
                                foregroundPolyline.setPoints(points);
                            }
                        });
                        polyLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
                        polyLineAnimator.setRepeatMode(ValueAnimator.RESTART);
                        polyLineAnimator.start();

                        // setFare();
                        if (riderDestinationMarker != null) {
                            riderDestinationMarker.remove();
                        }

                        if (riderPickupMarker != null) {
                            riderPickupMarker.remove();
                        }

                        mapsLocIconDisable();

                        IconGenerator iconGen1 = new IconGenerator(RiderMainActivity.this);
                        int shapeSize1 = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                        Drawable shapeDrawablePickup = ResourcesCompat.getDrawable(getResources(), R.drawable.rider_pickup_marker, null);
                        iconGen1.setBackground(shapeDrawablePickup);
                        View view1 = new View(RiderMainActivity.this);
                        view1.setLayoutParams(new ViewGroup.LayoutParams(shapeSize1, shapeSize1));
                        iconGen1.setContentView(view1);

                        Bitmap bitmapPickup = iconGen1.makeIcon();
                        Bitmap bitmapResizedPickup = Bitmap.createScaledBitmap(bitmapPickup, 34, 34, false);
                        riderPickupMarker = riderMaps.addMarker(new MarkerOptions().position(new LatLng(pickup.latitude, pickup.longitude)).icon(BitmapDescriptorFactory.fromBitmap(bitmapResizedPickup)));
                        //   riderMaps.setInfoWindowAdapter(new CustomInfoWindowRider(RiderMainActivity.this, null, "My Location"));
                        riderPickupMarker.showInfoWindow();

                        IconGenerator iconGen = new IconGenerator(RiderMainActivity.this);
                        // Define the size you want from dimensions file
                        int shapeSize = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);

                        Drawable shapeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rider_destination_marker, null);
                        iconGen.setBackground(shapeDrawable);

                        // Create a view container to set the size
                        View view = new View(RiderMainActivity.this);
                        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
                        iconGen.setContentView(view);


                        // Create the bitmap
                        Bitmap bitmap = iconGen.makeIcon();

                        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 30, 30, false);

                        riderDestinationMarker = riderMaps.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(bitmapResized)));

                        riderMaps.setInfoWindowAdapter(new CustomInfoWindow(RiderMainActivity.this, null, riderDestinationPlace));
                        riderDestinationMarker.showInfoWindow();
                        Common.stopWaitingDialog(pDialog);
                    } catch (Exception e) {
                        Common.stopWaitingDialog(pDialog);
                        Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Common.stopWaitingDialog(pDialog);
                    Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Throwable " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Common.stopWaitingDialog(pDialog);
            Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static class LogOutModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_log_out, null);
            dialog.setContentView(view);
        }
    }

    public void onLogOutCancel(View view) {
        if (logOutModal != null) {
            logOutModal.dismiss();
        }
    }

    public void onLogOut(View view) {
        logOutHere();
    }

    private TripCancelModal tripCancelModal;

    public void onRiderTripCancel(View view) {
        tripCancelModal = new TripCancelModal();
        tripCancelModal.show(getSupportFragmentManager(), TAG);
    }

    public void onCancelTripNo(View view) {
        tripCancelModal.dismiss();
    }

    public void onCancelTripYes(View view) {
        tripCancelModal.dismiss();
    }

    public static class TripCancelModal extends BottomSheetDialogFragment {

        @Override
        public void setupDialog(Dialog dialog, int style) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_trip_cancel, null);
            dialog.setContentView(view);
        }
    }


    /*************************** custom search box start
     ************************/
    private EditText etDestSearch, etPickupSearch;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager llm;
    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_BANGLADESH = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private static int pcs = 0, etClickNo = 0;
    private LinearLayout llShowLocationSearchBox, llConfirmPickupContainer;
    private CardView cardViewLocationSearch;

    private void searchDest() {

        Toolbar toolbarLocationSearchBox = findViewById(R.id.toolbar_location_search_box);

        cardViewLocationSearch = (CardView) findViewById(R.id.card_view_location_search);
        llShowLocationSearchBox = (LinearLayout) findViewById(R.id.ll_show_location_search_box);

        llConfirmPickupContainer = (LinearLayout) findViewById(R.id.ll_confirm_pickup_container);
        final LinearLayout llPickupContainer = (LinearLayout) findViewById(R.id.ll_pickup_container);
        final LinearLayout llDestContainer = (LinearLayout) findViewById(R.id.ll_dest_container);

        etPickupSearch = (EditText) findViewById(R.id.et_pickup_search);
        etDestSearch = (EditText) findViewById(R.id.et_dest_search);

        final ImageView ivPickupClear = (ImageView) findViewById(R.id.iv_pickup_clear);
        final ImageView ivDestClear = (ImageView) findViewById(R.id.iv_dest_clear);

        toolbarLocationSearchBox.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*myLocationButton.setVisibility(View.VISIBLE);
                llShowLocationSearchBox.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
                cardViewLocationSearch.setVisibility(View.GONE);*/
                pcs = 0;
                etClickNo = 0;
                Intent intent = new Intent(RiderMainActivity.this, RiderMainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                RiderMainActivity.this.finish();
            }
        });

        ivPickupClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPickupSearch.getText().clear();
            }
        });

        ivDestClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDestSearch.getText().clear();
            }
        });

        llShowLocationSearchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.parseColor("#BFBEBF"));
                }

                myLocationButton.setVisibility(View.GONE);
                llShowLocationSearchBox.setVisibility(View.GONE);
                getSupportActionBar().hide();
                cardViewLocationSearch.setVisibility(View.VISIBLE);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_place_list);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);

        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_place_search, mGoogleApiClient, BOUNDS_BANGLADESH, null);
        mRecyclerView.setAdapter(mAdapter);

        etPickupSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pcs = 1;
                ivPickupClear.setVisibility(View.VISIBLE);
            }
        });

        etPickupSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean status) {
                if (status) {
                    etClickNo = 1;
                    ivDestClear.setVisibility(View.GONE);
                    etDestSearch.getText().clear();
                    llDestContainer.setBackgroundColor(Color.parseColor("#FFF9F6F9"));
                    llPickupContainer.setBackgroundColor(Color.parseColor("#E7ECED"));
                }
            }
        });

        etDestSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean status) {
                if (status) {
                    etClickNo = 2;
                    ivPickupClear.setVisibility(View.GONE);
                    llPickupContainer.setBackgroundColor(Color.parseColor("#FFF9F6F9"));
                    llDestContainer.setBackgroundColor(Color.parseColor("#E7ECED"));
                }
            }
        });
        etDestSearch.requestFocus();

        etPickupSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*ivDestClear.setVisibility(View.GONE);
                etDestSearch.getText().clear();
                llDestContainer.setBackgroundColor(Color.parseColor("#FFF9F6F9"));
                llPickupContainer.setBackgroundColor(Color.parseColor("#E7ECED"));*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (pcs == 1) {
                        ivPickupClear.setVisibility(View.VISIBLE);
                    }
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    ivPickupClear.setVisibility(View.GONE);
                    // if (mSavedAdapter != null && mSavedAddressList.size() > 0) {
                    //     mRecyclerView.setAdapter(mSavedAdapter);
                    // }
                }

                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDestSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ivPickupClear.setVisibility(View.GONE);
                llDestContainer.setBackgroundColor(Color.parseColor("#E7ECED"));
                llPickupContainer.setBackgroundColor(Color.parseColor("#FFF9F6F9"));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    ivDestClear.setVisibility(View.VISIBLE);
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    ivDestClear.setVisibility(View.GONE);
                    // if (mSavedAdapter != null && mSavedAddressList.size() > 0) {
                    //     mRecyclerView.setAdapter(mSavedAdapter);
                    // }
                }
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e("", "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, int position) {
        if (mResultList != null) {
            try {
                final String placeId = String.valueOf(mResultList.get(position).placeId);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getCount() == 1) {
                            //Do the things here on Click.....
                            Intent data = new Intent();
                            data.putExtra("lat", String.valueOf(places.get(0).getLatLng().latitude));
                            data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                            setResult(RiderMainActivity.RESULT_OK, data);

                            if (etClickNo == 1) {
                                etPickupSearch.setText(places.get(0).getAddress());
                                riderPickupLatLng = places.get(0).getLatLng();
                                etClickNo = 2;
                                if (mAdapter != null) {
                                    mAdapter.clearList();
                                }
                                etDestSearch.requestFocus();
                            } else if (etClickNo == 2) {

                                etClickNo = 0;
                                cardViewLocationSearch.setVisibility(View.GONE);
                                if (mAdapter != null) {
                                    mAdapter.clearList();
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    window.setStatusBarColor(Color.TRANSPARENT);
                                }

                                if (AutoRideRiderApps.isNetworkAvailable()) {

                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(etDestSearch.getWindowToken(), 0);

                                    riderDestinationLatLng = places.get(0).getLatLng();
                                    riderDestinationPlace = places.get(0).getAddress().toString();

                                    if (riderPickupLatLng != null && riderDestinationLatLng != null) {
                                        riderMaps.setInfoWindowAdapter(new CustomInfoWindow(RiderMainActivity.this, null, "My Location"));
                                        if (backgroundPolyline != null) {
                                            backgroundPolyline.remove();
                                        }

                                        if (foregroundPolyline != null) {
                                            foregroundPolyline.remove();
                                        }

                                        if (polyLineAnimator != null) {
                                            if (polyLineAnimator.isRunning()) {
                                                polyLineAnimator.cancel();
                                            }
                                        }

                                        setDestinationRoute();
                                        floatingLayout.setVisibility(View.GONE);
                                        imgExpandableMessage.setVisibility(View.GONE);

                                        llRiderConfirmAddressBar.setVisibility(View.GONE);

                                        if (getSupportActionBar() != null) {
                                            toolbar_in_maps.setVisibility(View.GONE);
                                            setConfirmVehicleToolBar();
                                        }

                                        llRiderDestinationSearchBar.setVisibility(View.GONE);
                                        riderDestination.setText("");
                                    }
                                } else {
                                    snackBarNoInternet();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
//        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        //  mGoogleApiClient.disconnect();
        super.onStop();
    }

    /*************************** custom search box end
     ************************/
}