package com.nomadapp.splash.ui.activity.standard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.constants.WebConstants;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.model.mapops.AddressCoordsLocator;
import com.nomadapp.splash.model.mapops.MarkersOps;
import com.nomadapp.splash.model.mapops.OnAddressFetched;
import com.nomadapp.splash.model.payment.paymeapis.seller.SplashCreateSeller;
import com.nomadapp.splash.model.payment.paymeapis.ssale.SplashGenerateSaleAutomatic;
import com.nomadapp.splash.model.server.Installation;
import com.nomadapp.splash.model.server.parseserver.DocumentsClassInterface;
import com.nomadapp.splash.model.server.parseserver.HistoryClassInterface;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.model.server.parseserver.RequestClassInterface;
import com.nomadapp.splash.model.server.parseserver.UserClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.HistoryClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.ProfileClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.RequestClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.AddressesClassSend;
import com.nomadapp.splash.model.server.parseserver.send.DocumentsClassSend;
import com.nomadapp.splash.model.server.parseserver.send.MetricsClassSend;
import com.nomadapp.splash.ui.activity.carownerside.QuickTourActivity;
import com.nomadapp.splash.ui.activity.splasherside.SplasherServicesActivity;
import com.nomadapp.splash.ui.activity.splasherside.SplasherWalletActivity;
import com.nomadapp.splash.ui.activity.splasherside.WashRequestsActivity;
import com.nomadapp.splash.model.constants.PaymeConstants;
import com.nomadapp.splash.ui.activity.carownerside.payment.PaymentSettingsActivity;
import com.nomadapp.splash.ui.activity.carownerside.WashReqParamsActivity;
import com.nomadapp.splash.ui.activity.carownerside.WashServiceShowActivity;
import com.nomadapp.splash.ui.activity.splasherside.SplasherClientRouteActivity;
import com.nomadapp.splash.ui.activity.standard.web.WebActivity;
import com.nomadapp.splash.utils.customclocksops.HourDifferentiator;
import com.nomadapp.splash.utils.forcedupdate.ForceUpdateChecker;
import com.nomadapp.splash.utils.phonedialer.DialCall;
import com.nomadapp.splash.utils.radar.RadarView;
import com.nomadapp.splash.utils.sysmsgs.DialogAcceptInterface;
import com.nomadapp.splash.utils.sysmsgs.connectionlost.ConnectionLost;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.localnotifications.Notifications;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.utils.sysmsgs.questiondialogs.ForcedAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.questiondialogs.LogoutMessage;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import com.parse.SaveCallback;
import com.paymeservice.android.PayMe;
import com.paymeservice.android.model.Settings;
import static com.paymeservice.android.model.Settings.Environment.PRODUCTION;

import net.qiujuer.genius.ui.widget.SeekBar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback
        , ForceUpdateChecker.OnUpdateNeededListener{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button cFindMeAWash;
    private SeekBar cUpdatedSeekbar;

    //Checkers//
    private boolean findCarWasherRequestActive = false;
    private boolean alreadyExecuted = false;
    private boolean alreadyExecuted2 = false;
    private boolean alreadyExecuted3 = false;
    private boolean alreadyExecuted4 = false;
    private boolean alreadyExecuted5 = false;
    private boolean alreadyExecuted6 = false;
    private boolean alreadyExecuted8 = false;
    private boolean alreadyExecuted9 = false;
    private boolean alreadyExecuted10 = false;
    private boolean splasherActive = false;
    private boolean background = false;
    private boolean shutDownChecker = false;
    private boolean splasherCanceledCheck = false;
    private boolean splasherWasPrivate = false;
    private boolean timeCheckedOnce = false;
    private boolean washFinished = false;
    private boolean notiForSplasherRan = false;
    private boolean notiForCarOwnerRan = false;
    private boolean notiForCarOwnerRan2 = false;
    private boolean sServicesInterfaceRan = false;
    private boolean areaEpicenterReady = false;
    private boolean presetServiceSettingsRan = false;
    private boolean cdcUntilTimeRan = false;
    //---------//

    private RadarView cRadarView;
    private ImageView cMarkerView;
    private ImageView cMarkerView2;
    private LatLng savedCarLocationForUpdate;
    private Marker dotMarker = null;
    private MarkerOptions options;

    private String StringNotLoggedIn;

    private ParseUser currentUser = ParseUser.getCurrentUser();

    //splasher Status//
    private RelativeLayout mSplasher_info_toco_back_space;
    private ImageView mSplasherPicInStatusWindows;
    private TextView cSplasherStatus;
    private LatLng userLocation;
    private String isThereASplasher, splasherShowingName;
    private TextView mSplasher_stats_co_splasherName;
    private TextView mSplasher_stats_co_numWashes;
    private TextView mSplasher_status_co_distance_text;
    private MaterialRatingBar mSplasher_status_co_ratingBar;
    private String splasherPhoneNumber;
    //---------------//

    //Request Info Window//
    private RelativeLayout mRequest_info_window;
    private TextView mCarAddressInfoText, mCarInfoText, mCarAddDescText, mCarServiceText
            , mWindowPrice;
    //-------------------//

    //Car Owner Data for Splasher 1//
    private RelativeLayout cCarOwnerPrevDetailsRelative;
    private TextView cDistanceDetsValue;
    private TextView cAvailableUntilDetsValue;
    private TextView cYouGetDetsValue;
    private ImageView cCarOwnerDetsProfilePic;
    private ImageView cInfoWindowSplashBadge;
    private TextView cTomorrowText;
    //-----------------------------//

    private String splasher = "splasher";
    private String carOwner = "carOwner";
    private Integer tag = 0;

    private Handler handler = new Handler();
    private static final int WAITING_CONSTANT_ALL_RUNNABLE = 30000;
    private static final int WAITING_CONSTANT_CAR_OWNER = 30000;
    private static final int WAITING_CONSTANT_SPLASHER = 5000;

    private ActionBarDrawerToggle mToggle;
    private ImageView cRatingBadge;
    private int splasherNumericalBadge;
    private int requestNumercialBadge;
    private String splasherType;
    private MenuItem cancelItem;
    //-------------//

    private String currentSavedSelectedHour;

    //Local Storage----//
    private WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(this);
    //-----------------//

    //"Let's wash a car button"----------//
    private ArrayList<Integer> idsFromRequests = new ArrayList<>();
    //-----------------------------------//

    //Image Handler//
    private GlideImagePlacement glideImagePlacement = new GlideImagePlacement(HomeActivity.this);
    //-------------//

    private ToastMessages toastMessages = new ToastMessages();
    private Notifications notifications = new Notifications(HomeActivity.this);
    private ConnectionLost clm = new ConnectionLost(HomeActivity.this);

    //////////////////////////////////////DOTS ON MAP///////////////////////////////////////////////

    private ArrayList<Double> longitudeListCo = new ArrayList<>();
    private ArrayList<Double> latitudeListCo = new ArrayList<>();
    private ArrayList<String> userNameListCo = new ArrayList<>();
    private ArrayList<String> carAddressListCo = new ArrayList<>();
    private ArrayList<String> carAddressDescListCo = new ArrayList<>();
    private ArrayList<String> carUntilTimeListCo = new ArrayList<>();
    private ArrayList<String> carServiceTypeListCo = new ArrayList<>();
    private ArrayList<String> carOwnerSetPriceCo = new ArrayList<>();
    private ArrayList<String> carOwnerDistanceCo = new ArrayList<>();
    private ArrayList<String> profilePicListCo = new ArrayList<>();
    private ArrayList<String> profilePicListNoFbCo = new ArrayList<>();
    private ArrayList<String> requestedNumBadgeListCo = new ArrayList<>();
    private ArrayList<String> carOwnerPhoneNumListCo = new ArrayList<>();
    //----------------------------------------------------------------
    private ArrayList<String> carBrandListCo = new ArrayList<>();
    private ArrayList<String> carModelListCo = new ArrayList<>();
    private ArrayList<String> carColorListCo = new ArrayList<>();
    private ArrayList<String> carYearListCo = new ArrayList<>();
    private ArrayList<String> carPlateListCo = new ArrayList<>();
    //----------------------------------------------------------------

    private LatLng dotLocation;
    private Double longitudeCo, getLongitudeToTransfer;
    private Double latitudeCo, getLatitudeToTransfer;
    private String profilePicCo;
    private String profilePicNoFbCo;
    private String carOwnerUsernameCo, usernameToTransfer;
    private String carOwnerCarAddressCo, carAddressToTransfer;
    private String carOwnerCarAddressDescCo, carAddressDescToTransfer;
    private String carOwnerCarUntilTimeCo, carUntilTimeToTransfer;
    private String carOwnerCarServiceTypeCo, carServiceTypeToTransfer;
    private String requestedBadgeCo;
    private String carOwnerPhoneNum, carOwnerPhoneNToTransfer;
    private String priceCo, priceToTransfer;
    private String distanceCo;
    private String requestType;
    private String taken;

    private String carOwnerCarBrandCo, carBrandToTransfer;
    private String carOwnerCarModelCo, carModelToTransfer;
    private String carOwnerCarColorCo, carColorToTransfer;
    private String carOwnerCarYearCo, carYearToTransfer;
    private String carOwnerCarPlateCo, carPlateToTransfer;

    private boolean weHaveMotorcycle = false;

    //Documents for Splasher signUp coming from SplasherApplicationActivity.java-----//
    private String paymeClientKey, sellerFirstName, sellerLastName, sellerSocialId, sellerBirthDate,
            sellerSocialIdIssued, sellerEmail, sellerPhoneNumber;

    private int sellerGender, sellerBankCode, sellerBankBranch, sellerBankAccountNumber;

    private String sellerDescription, sellerSiteUrl, sellerPersonBussinessType;
    private int sellerInc;

    private String sellerAddressCity, sellerAddressStreet, sellerAddressCountry;
    private int sellerAddressStreetNumber;

    private Double sellerMarketFee;

    private String sellerFileSocialId, sellerFileCheque, sellerFileCorporate;

    //--PARAMETER RELEVANT TO APP IN PRODUCTION ONLY--//
    private String sellerPlan;
    //------------------------------------------------//
    //------------------------------------------------------------------------------//

    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 2;

    //Splasher wallet//
    private String salesWithTip;
    //private ArrayList<String> salesWithTipList = new ArrayList<>();
    //private int amountOfSales;
    private double doubleSalesWithTip, doubleFinal;
    private String stringFinal;
    private String stringFinalShekel = "₪ 00.00";
    private TextView cSplashWalletTextView;
    private RelativeLayout cSplashWalletRelative;
    private DecimalFormat df = new DecimalFormat("#.##");
    private int newTag = 0;
    //---------------//

    //to Account page//
    private String userEmailToSend;
    //---------------//

    private RelativeLayout cGettingLocationRelativeHome;

    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(HomeActivity.this);
    private ForcedAlertDialog forcedAlertDialog = new ForcedAlertDialog(HomeActivity.this);
    private com.nomadapp.splash.utils.sysmsgs.questiondialogs.AlertDialog alertDialog = new
            com.nomadapp.splash.utils.sysmsgs.questiondialogs.AlertDialog(HomeActivity.this);
    private UserClassQuery userClassQuery = new UserClassQuery(HomeActivity.this);
    private ProfileClassQuery profileClassQuery = new ProfileClassQuery(HomeActivity.this);
    private RequestClassQuery requestClassQuery = new RequestClassQuery(HomeActivity.this);
    private MetricsClassQuery metricsClassQuery = new MetricsClassQuery(HomeActivity.this);
    private MarkersOps markersOps = new MarkersOps(HomeActivity.this);
    private AddressCoordsLocator addressCoordsLocator
            = new AddressCoordsLocator(HomeActivity.this);
    private HourDifferentiator hd = new HourDifferentiator(HomeActivity.this);

    //PAYME---------------//
    private SplashCreateSeller splashCreateSeller = new SplashCreateSeller(HomeActivity.this);
    private SplashGenerateSaleAutomatic splashGenerateSaleAutomatic = new
            SplashGenerateSaleAutomatic(HomeActivity.this);
    //--------------------//

    //--Epicenter and area Range from services--//
    private String displayedRange, justRange;
    private int distanceToSet, rangeSBProgress;
    private SeekBar mServiceAreaSeekbar;
    private TextView mRangeText;
    private LatLng areaPointerLocation;
    private Button mSetArea;
    private TextView mLiveFetchedAdddress;
    //-----------------------------------------//

    //--Count down clock--//
    private RelativeLayout mCountDownClockRela;
    private TextView mCarAvailTime;
    private LinearLayout mSplasherInfoHolder;
    //--------------------//

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.i("requestActive?", String.valueOf(findCarWasherRequestActive));

        //ParseInstallation.getCurrentInstallation().saveInBackground();
        //TODO:Payme code 4: Setup SELLER_KEY and APP_STAGE for Payme API
        PayMe.initialize(new Settings(PaymeConstants.SPLASH_SELLER_KEY, PRODUCTION));
        updateHelperCall();
        Log.i("Locale is", "here: " + Locale.getDefault().getDisplayLanguage());

        background = false;
        Bundle addressBundle = getIntent().getExtras();
        try {
            if (addressBundle != null) {
                if (Objects.equals(addressBundle.getString("addressKey"), "address")) {
                    AddressesClassSend ac = new AddressesClassSend(HomeActivity.this);
                    MetricsClassSend mc = new MetricsClassSend(HomeActivity.this);
                    ac.createAddressClassForUser(userClassQuery.userName());
                    mc.createMetricsClassForUser(userClassQuery.userName(),carOwner);
                }
            }
        }catch(NullPointerException n1){
            n1.printStackTrace();
        }
        // Add code to print out the key hash <DEBUG HASH CODE ONLY>
        //package always used: com.kid.splash
        //new package in trial: com.kid.splash.ui.activity.standard
        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.kid.splash",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        */
        //drawer menu 2---//
        Toolbar mCustomToolBar = findViewById(R.id.custom_tool_bar);
        setSupportActionBar(mCustomToolBar);
        DrawerLayout mDrawerLayout = findViewById(R.id.home);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open
                , R.string.close);
        final NavigationView navigationView = findViewById(R.id.navView);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        View navHeader = navigationView.getHeaderView(0);
        RelativeLayout cHeaderRelative = navHeader.findViewById(R.id.headerRelative);
        TextView cUserProfileUsername = navHeader.findViewById(R.id.userProfileUsername);
        TextView cUserProfileEmail = navHeader.findViewById(R.id.userProfileEmail);
        TextView cUserProfileUsertype = navHeader.findViewById(R.id.userProfileUserType);
        ImageView cUserProfilePics = navHeader.findViewById(R.id.userProfilePics);
        cRatingBadge = navHeader.findViewById(R.id.ratingBadge);
        TextView cUserLessMsg = navHeader.findViewById(R.id.userlessMsg);
        //--------------//

        //Car Owner Data for Splasher 2
        cCarOwnerPrevDetailsRelative = findViewById(R.id.carOwnerPrevDetailsRelative);
        cDistanceDetsValue = findViewById(R.id.distanceDetsValue);
        cAvailableUntilDetsValue = findViewById(R.id.availableUntilDetsValue);
        cYouGetDetsValue = findViewById(R.id.youGetDetsValue);
        cCarOwnerDetsProfilePic = findViewById(R.id.carOwnerDetsProfilePic);
        cInfoWindowSplashBadge = findViewById(R.id.infoWindowSplashBadge);
        cTomorrowText = findViewById(R.id.tomorrowText);
        cGettingLocationRelativeHome = findViewById(R.id.gettinLocaionRelativeHome);
        //-----------------------------

        cFindMeAWash = findViewById(R.id.findMeAWash);

        //Request info window//
        mRequest_info_window = findViewById(R.id.request_window_info);
        mCarAddressInfoText = findViewById(R.id.carAddressInfoText);
        mCarInfoText = findViewById(R.id.carInfoText);
        mCarAddDescText = findViewById(R.id.carAddDescText);
        mCarServiceText = findViewById(R.id.carServiceText);
        mWindowPrice = findViewById(R.id.windowPrice);
        //-------------------//

        //splash wallet//
        cSplashWalletRelative = findViewById(R.id.splashWalletRelative);
        cSplashWalletTextView = findViewById(R.id.splashWalletTextView);
        //-------------//

        //Area range services//
        mServiceAreaSeekbar = findViewById(R.id.serviceAreaSeekbar);
        mRangeText = findViewById(R.id.rangeText);
        //-------------------//

        Installation installation = new Installation(HomeActivity.this);
        Button cLetsWashACar = findViewById(R.id.letsWashACar);
        cRadarView = findViewById(R.id.radarView);
        cMarkerView = findViewById(R.id.markerView);
        cMarkerView2 = findViewById(R.id.markerView2);
        //splasher status
        mSplasherPicInStatusWindows = findViewById(R.id.splasherPicInStatusWindow);
        mSplasher_info_toco_back_space = findViewById(R.id.splasher_info_toco_back_space);
        mSplasherInfoHolder = findViewById(R.id.splasherInfoHolder);
        ImageView mSplasher_stats_co_call = findViewById(R.id.splasher_stats_co_call);
        mSplasher_stats_co_splasherName = findViewById(R.id.splasher_stats_co_splasherName);
        mSplasher_stats_co_numWashes = findViewById(R.id.splasher_stats_co_numWashes);
        mSplasher_status_co_distance_text = findViewById(R.id.splasher_status_co_distance_text);
        mSplasher_status_co_ratingBar = findViewById(R.id.splasher_status_co_ratingBar);

        mCountDownClockRela = findViewById(R.id.countDownClockRela);
        mCarAvailTime = findViewById(R.id.carAvailTime);

        cSplasherStatus = findViewById(R.id.splasherStatus);
        cCarOwnerPrevDetailsRelative.setVisibility(View.GONE);
        cRatingBadge.setVisibility(View.GONE);
        cSplashWalletRelative.setVisibility(View.GONE);
        cSplashWalletTextView.setVisibility(View.GONE);
        cGettingLocationRelativeHome.setVisibility(View.VISIBLE);
        cUpdatedSeekbar = findViewById(R.id.updatedSeekBarOne);
        cUpdatedSeekbar.setVisibility(View.GONE);
        mRequest_info_window.setVisibility(View.GONE);
        mSplasher_info_toco_back_space.setVisibility(View.GONE);
        mSplasherInfoHolder.setVisibility(View.GONE);
        mSplasher_status_co_ratingBar.setEnabled(false);
        mSplasher_status_co_ratingBar.setClickable(false);
        if (!Locale.getDefault().getDisplayLanguage().equals("עברית")){
            mSplasher_stats_co_call.setScaleX(-1f);
        }
        if (cancelItem != null) {
            cancelItem.setVisible(false);
        }
        cFindMeAWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alreadyExecuted8) {
                    if (!userClassQuery.userExists()) {
                        StringNotLoggedIn = getResources().getString
                                (R.string.carOwner_act_java_youHaveToSignLogRequest);
                        notLoggedIn(StringNotLoggedIn);
                    } else {
                        if (cRadarView.getVisibility() == View.GONE) {
                            updateWashMyCarBtnCount();
                            Intent intent = new Intent(HomeActivity.this
                                    ,WashReqParamsActivity.class);
                            intent.putExtra("defaultLat", userLocation.latitude);
                            intent.putExtra("defaultLong",userLocation.longitude);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
        if (userClassQuery.userExists()) {
            Log.i("currentUser?", "is: " + currentUser.toString());
            String userNameForHeader = ParseUser.getCurrentUser().getUsername();
            String userEmailForHeader = ParseUser.getCurrentUser().getEmail();
            cUserProfileUsername.setText(userNameForHeader);
            cUserProfileEmail.setText(userEmailForHeader);

            Log.i("data payme preprepre","we in onCreate");
            //->THIS CONTROLS THE APPLICATION FLOW. DEPENDING ON USER TYPE: CAROWNER OR SPLASHER<-//
            try {
                if (userClassQuery.userIsCarOwnerOrSplasher(splasher)){
                    handler.removeCallbacks(requestUpdateChecker);
                    profileClassQuery.splasherType(new ProfileClassInterface.walletStatus() {
                        @Override
                        public void onWalletStatusAccess(String splasherType) {
                            if(splasherType.equals("independent")) {
                                Log.i("whichSplasher", "independent");
                                if (sServicesInterfaceRan){
                                    cSplashWalletRelative.setVisibility(View.GONE);
                                    cSplashWalletTextView.setVisibility(View.GONE);
                                }else{
                                    cSplashWalletRelative.setVisibility(View.VISIBLE);
                                    cSplashWalletTextView.setVisibility(View.VISIBLE);
                                }
                                cSplashWalletTextView.setText(getResources()
                                        .getString(R.string.carOwner_act_java_balance));
                            }else{
                                Log.i("whichSplasher", "hired");
                                cSplashWalletRelative.setVisibility(View.GONE);
                                cSplashWalletTextView.setVisibility(View.GONE);
                                cSplashWalletTextView.setText(getResources()
                                        .getString(R.string.carOwner_act_java_balance));
                            }
                        }
                    });
                    //create seller operations
                    dispatchDocumentsSplasherSignUp();

                    //Send new splashers to set up their services
                    runTakeNewSplasherToServices();

                    cFindMeAWash.setVisibility(View.GONE);
                    cCarOwnerPrevDetailsRelative.setVisibility(View.GONE);
                    cLetsWashACar.setVisibility(View.VISIBLE);
                    cUserLessMsg.setVisibility(View.GONE);
                    cRatingBadge.setVisibility(View.VISIBLE);

                    //R.string.carOwner_act_java_splasher
                    String stringUserType = getResources()
                            .getString(R.string.carOwner_act_java_splasher);
                    cUserProfileUsertype.setText(stringUserType);
                    splasherNumericalBadge = profileClassQuery.getMyRatingBadge(cRatingBadge);
                    installation.parseInstallationProcess("splashers");

                } else if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {

                    //R.string.carOwner_act_java_carOwner
                    String stringUserTypeTwo = getResources()
                            .getString(R.string.carOwner_act_java_carOwner);
                    cSplashWalletRelative.setVisibility(View.GONE);
                    cFindMeAWash.setVisibility(View.VISIBLE);
                    cLetsWashACar.setVisibility(View.GONE);
                    cUserProfileUsertype.setText(stringUserTypeTwo);
                    cUserLessMsg.setVisibility(View.GONE);
                    cRatingBadge.setVisibility(View.GONE);
                    cCarOwnerPrevDetailsRelative.setVisibility(View.GONE);

                    installation.parseInstallationProcess("carowners");

                    requestClassQuery.fetchCurrentRequestOne(new RequestClassInterface() {
                        @Override
                        public void requestClassMethod(ParseObject object) {
                            fetchCurrentRequestIfExists(object);
                        }
                        @Override
                        public void setCarWasherFinderToFalse() {
                            findCarWasherRequestActive = false;
                        }
                    });
                }
            }catch(NullPointerException npe){
                npe.printStackTrace();
                forcedAlertDialog.somethingWentWrong();
            }
            //------------------------------------------------------------------------------------//
        }

        //drawer menu 3-----------------------------------------------------------------------------
        if (!userClassQuery.userExists()) {
            navigationView.getMenu().findItem(R.id.fourNav).setTitle(getResources()
                    .getString(R.string.nav_menu_login)).setIcon(R.drawable.ic_login);

            cUserLessMsg.setVisibility(View.VISIBLE);
            cUserProfileUsername.setVisibility(View.GONE);
            cUserProfileEmail.setVisibility(View.GONE);
            cUserProfileUsertype.setVisibility(View.GONE);
            cRatingBadge.setVisibility(View.GONE);

            navigationView.getMenu().findItem(R.id.twoAndAHalfNav).setVisible(false);
            navigationView.getMenu().findItem(R.id.sixNav).setVisible(false);

            //QUICK TOUR SYSTEM: ONLY WORKS ONCE, WHEN FIRST TIME OPENING APP AFTER EMPTY DATA:
            //MEANING: APP JUST DOWNLOADED OR DATA WAS ERASED MANUALLY FROM PHONE
            runAutoQuickTourOnce();

        }else if(userClassQuery.userExists()){
            try {
                if (userClassQuery.userIsCarOwnerOrSplasher(splasher)) {
                    //Irrelevant to SplasherType, the following menu items should not be shown:
                    navigationView.getMenu().findItem(R.id.twoNav).setVisible(false);
                    navigationView.getMenu().findItem(R.id.threePointSeventyFiveNav).setVisible(false);
                    navigationView.getMenu().findItem(R.id.oneNav).setVisible(false);
                    profileClassQuery.splasherType(new ProfileClassInterface.walletStatus() {
                        @Override
                        public void onWalletStatusAccess(String splasherType) {
                            Log.i("splasherType", "is: " + userClassQuery.userName()
                                    + " splasher of type " + splasherType);
                            if (splasherType.equals("independent")) {
                                navigationView.getMenu().findItem(R.id.threePointTwentyFiveNav)
                                        .setTitle(getResources().getString(R.string
                                                .nav_menu_myWallet))
                                        .setIcon(R.drawable.ic_account_balance_wallet_black_24dp);
                            }else{
                                navigationView.getMenu().findItem(R.id.threePointTwentyFiveNav)
                                        .setVisible(false);
                            }
                        }
                    });
                } else if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {
                    navigationView.getMenu().findItem(R.id.twoAndAHalfNav).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sixNav).setVisible(false);
                }
            }catch(NullPointerException npe){
                npe.printStackTrace();
                alertDialog.generalPurposeQuestionDialog(HomeActivity.this, getResources()
                                .getString(R.string.carOwner_Act_java_logOutForErrTitle)
                        , getResources().getString(R.string.carOwner_Act_java_logOutForErrMsg)
                        , getResources().getString(R.string.main_act_java_ok), null,
                        new DialogAcceptInterface() {
                            @Override
                            public void onAcceptOption() {
                                userClassQuery.logOutUserEP(SignUpLogInActivity.class);
                            }
                        });
            }
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (userClassQuery.userExists()) {
                    switch (item.getItemId()) {
                        case R.id.oneNav:
                            startActivity(new Intent(HomeActivity.this
                                    , QuickTourActivity.class));
                            return true;
                        case R.id.twoNav:
                            startActivity(new Intent(HomeActivity.this
                                    , AlertsActivity.class));
                            return true;
                        case R.id.twoAndAHalfNav:
                            startActivity(new Intent(HomeActivity.this
                                    , SplasherServicesActivity.class));
                            return true;
                        case R.id.threeNav:
                            Intent fbIntent = new Intent(HomeActivity.this
                                    , WebActivity.class);
                            fbIntent.putExtra("webKey", "feedBack");
                            startActivity(fbIntent);
                            return true;
                        case R.id.threePointTwentyFiveNav:
                            if (userClassQuery.userIsCarOwnerOrSplasher(splasher)){
                                startActivity(new Intent(HomeActivity.this,
                                        SplasherWalletActivity.class));
                            }else {
                                startActivity(new Intent(HomeActivity.this
                                        , PaymentSettingsActivity.class));
                            }
                            return true;
                        case R.id.threePointFiveNav:
                            startActivity(new Intent(HomeActivity.this
                                    , AboutActivity.class));
                            return true;
                        case R.id.threePointSeventyFiveNav:
                            Intent faqIntent = new Intent(HomeActivity.this
                                    , WebActivity.class);
                            faqIntent.putExtra("webKey", "faq");
                            startActivity(faqIntent);
                            return true;
                        case R.id.fourNav:
                            //have to figure out a way to kill the request when logout
                            Log.i("requestActiveInMenu",
                                    String.valueOf(findCarWasherRequestActive));
                            if (findCarWasherRequestActive &&
                                    userClassQuery.userIsCarOwnerOrSplasher(carOwner) &&
                                    cRadarView.getVisibility() == View.VISIBLE) {
                                cLogOutRequestWarning();
                                break;
                            } else if (!findCarWasherRequestActive) {
                                //delete all shared preferences
                                handler.removeCallbacks(requestUpdateChecker);
                                shutDownChecker = true;
                                LogoutMessage logoutMessage = new
                                        LogoutMessage(HomeActivity.this);
                                logoutMessage.logoutMessage();
                            }
                            return true;
                        case R.id.sixNav:
                            //Only possible to access if(userExists && user="splasher")
                            Intent storeIntent = new Intent(HomeActivity.this
                            , WebActivity.class);
                            storeIntent.putExtra("webKey", "store");
                            startActivity(storeIntent);
                            return true;
                    }
                } else if (!userClassQuery.userExists()) {
                    switch (item.getItemId()) {
                        case R.id.oneNav:
                            startActivity(new Intent(HomeActivity.this
                                    , QuickTourActivity.class));
                            return true;
                        case R.id.twoNav:
                            String logInSecond = getResources()
                                    .getString(R.string.carOwner_act_java_youHaveToSignLogAlerts);
                            notLoggedIn(logInSecond);
                            return true;
                        case R.id.threeNav:
                            String logInThird = getResources()
                                    .getString(R.string.carOwner_act_java_youHaveToSignLogFeedback);
                            notLoggedIn(logInThird);
                            return true;
                        case R.id.threePointTwentyFiveNav:
                            String logInFourth = getResources()
                                    .getString(R.string.carOwner_act_java_youHaveToSignLogPayment);
                            notLoggedIn(logInFourth);
                            return true;
                        case R.id.threePointFiveNav:
                            startActivity(new Intent(HomeActivity.this
                                    , AboutActivity.class));
                            return true;
                        case R.id.threePointSeventyFiveNav:
                            Intent faqIntent = new Intent(HomeActivity.this
                                    , WebActivity.class);
                            faqIntent.putExtra("webKey", "faq");
                            startActivity(faqIntent);
                            return true;
                        case R.id.fourNav:
                            Intent toSignorLogScreen = new Intent(HomeActivity.this
                                    , SignUpLogInActivity.class);
                            startActivity(toSignorLogScreen);
                            return true;
                    }
                }
                return false;
            }
        });
        //------------------------------------------------------------------------------------------

        //Seekbar for actual status guidance (Disabling onTouchListener)
        cUpdatedSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //RECIEVE FACEBOOK DATA FOR PROFILE PIC AND PUT IT IN THE HEADER'S IMAGEVIEW FROM THE DRAWER
        //currentUser.getString("CarOwnerOrSplasher")
        //SAVE, RUN AND TEST, TRY DISPLAYING DEFAULT EMPTYFACE IF FETCHING PROFILE PIC FAILS
        //picture from server also on header if changed after resigning in with fb
        try {
            if (userClassQuery.getUserStringAttribute("fbProfilePic").contains("https")) {
                if (userClassQuery.getUserStringAttribute("fbProfilePic") != null) {
                    String fbPicHolder = userClassQuery.getUserStringAttribute("fbProfilePic");
                    Log.i("fetched FbPicData", "Is: " + fbPicHolder);
                    Uri fbPicHolderUri = Uri.parse(fbPicHolder);
                    if (fbPicHolder != null) {
                        glideImagePlacement.roundImagePlacementFromUri
                                (fbPicHolderUri, cUserProfilePics);
                    }
                } else {
                    Bitmap defaultPic = BitmapFactory.decodeResource(getResources()
                            , R.drawable.theemptyface);
                    cUserProfilePics.setImageBitmap(defaultPic);
                }
            } else if (!userClassQuery.getUserStringAttribute("fbProfilePic").contains("https")) {
                //OPTION 2 TO PLACE PROFILE PIC FROM PHONE ON BADGE AFTER REOPENING APP
                ParseFile profPicFromFile =
                        userClassQuery.getUserParseFileAttribute("localProfilePic");
                String profPicFromFileString = profPicFromFile.getUrl();
                Log.i("profPicLocalURI", "is " + profPicFromFileString);
                glideImagePlacement.roundImagePlacementFromString
                        (profPicFromFileString, cUserProfilePics);
            }else{
                Bitmap defaultPic = BitmapFactory.decodeResource(getResources()
                        , R.drawable.theemptyface);cUserProfilePics.setImageBitmap(defaultPic);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Bitmap defaultPic = BitmapFactory.decodeResource(getResources()
                    , R.drawable.theemptyface);
            cUserProfilePics.setImageBitmap(defaultPic);
        }

        //Go to AccountActivity//
        if (userClassQuery.userExists()) {
            userEmailToSend = userClassQuery.email();
            Log.i("stuff0.1", "is" + userEmailToSend);
        }
        cHeaderRelative.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (userClassQuery.userExists()) {
                    Intent i = new Intent(HomeActivity.this, AccountActivity.class);
                    i.putExtra("email", userEmailToSend);
                    Log.i("stuff0.2", "is " + userEmailToSend);
                    startActivity(i);
                } else {
                    notLoggedIn(getResources()
                            .getString(R.string.carOwner_act_java_youNeedToLogInBfProfPic));
                }
            }
        });
        //--------------------//
        //Handling uploading profile picture to holder if there isn't a facebook on already-------//
        //get username, email and phone number.
        cUserProfilePics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userClassQuery.userExists()) {
                    Intent i = new Intent(HomeActivity.this, AccountActivity.class);
                    i.putExtra("email", userEmailToSend);
                    Log.i("stuff0.2", "is " + userEmailToSend);
                    startActivity(i);
                } else {
                    notLoggedIn(getResources()
                            .getString(R.string.carOwner_act_java_youNeedToLogInBfProfPic));
                }
            }
        });
        //---------------------//

        if (userClassQuery.userExists()){
            try {
                if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {
                    metricsClassQuery.queryMetricsToUpdate("openedApp");
                }
            }catch(NullPointerException e){
                e.printStackTrace();
                toastMessages.productionMessage(HomeActivity.this
                        ,getResources().getString(R.string.forced_Alert_somethingWrong_msg),1);
                userClassQuery.logOutUserEP(SignUpLogInActivity.class);
            }
        }

        Log.i("requestActive2?", String.valueOf(findCarWasherRequestActive));
        clm.connectivityStatus(HomeActivity.this);
        background = false;
    }

    public void updateWashMyCarBtnCount(){
        MetricsClassQuery metricsClassQuery = new MetricsClassQuery(HomeActivity.this);
        metricsClassQuery.queryMetricsToUpdate("washMyCar");
    }

    private void cdcOps(int visibility1){
        if(userClassQuery.userExists()){
            if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)){
                //mCdcTime = findViewById(R.id.cdcTime); //invalidated. can't develop for now.
                mSplasher_info_toco_back_space.setVisibility(View.VISIBLE);
                viewFliper(RelativeLayout.BELOW, R.id.splasher_info_toco_back_space);
                mCountDownClockRela.setVisibility(visibility1);
            }
        }
    }

    public void fetchCurrentRequestIfExists(ParseObject object){
        toastMessages.debugMesssage(getApplicationContext()
                , "I have and active request", 1);
        //this way, the user can close and even shutdown the app, as long as his request
        //stays "true" checked on Request class on Parse server, he will be subject to a splasher
        //accepting his request. The only way to get rid of the "check" is to hit "cancel wash"
        //button which will set the "check" to false and delete the request
        //object from the Request class.
        findCarWasherRequestActive = true;
        //Here, if there is a request, focus on request's marker and not on self location

        //Populate infoWindow for CarOwner with data about his request: address, car, service, etc//
        mCarAddressInfoText.setText(object.getString("carAddress"));
        String carBrief = object.getString("carBrand") + " "
                + object.getString("carModel") + " "
                + object.getString("carplateNumber");
        mCarInfoText.setText(carBrief);
        String addressDesc = object.getString("carAddressDesc");
        if (addressDesc.equals("") || addressDesc.isEmpty()){
            mCarAddDescText.setText(getResources().getString(R.string.act_car_owner_notSpecified));
        }else {
            mCarAddDescText.setText(object.getString("carAddressDesc"));
        }

        taken = object.getString("taken");
        String splasherUsername = object.getString("splasherUsername");
        String service = object.getString("serviceType");
        currentSavedSelectedHour = object.getString("untilTime");
        serviceTextAdjustment(mCarServiceText,service);
        String priceFromServer = object.getString("priceWanted");
        requestType = object.getString("requestType");
        Log.i("priceFromServer", "is: " + priceFromServer);
        if (priceFromServer.contains(String.valueOf(PaymeConstants.STATIC_TEMPORAL_PRICE))) {
            mWindowPrice.setText(getResources().getString(R.string.thirty));
        }else if(priceFromServer.contains(String.valueOf(PaymeConstants
                .STATIC_TEMPORAL_PRICE_INTER))){
            mWindowPrice.setText(getResources().getString(R.string.seventy));
        }else if(priceFromServer.contains(String.valueOf(PaymeConstants
                .STATIC_TEMPORAL_PRICE_MOTO))){
            mWindowPrice.setText(getResources().getString(R.string.twenty));
        }
        //----------------------------------------------------------------------------------------//

        cdcOps(View.VISIBLE);
        cRadarView.setVisibility(View.VISIBLE); //--> Should set the Cancel button visible
        cFindMeAWash.setVisibility(View.GONE);
        mRequest_info_window.setVisibility(View.VISIBLE);
        cRadarView.startAnimation();
        cUpdatedSeekbar.setVisibility(View.VISIBLE);
        if (cancelItem != null) {
            cancelItem.setVisible(true);
        }
        cUpdatedSeekbar.setProgress(0);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        String stringedSavedLat = "";
        String stringedSavedLon = "";

        if (writeReadDataInFile.readFromFile("lat") != null
                && writeReadDataInFile.readFromFile("lon") != null) {
            stringedSavedLat = writeReadDataInFile.readFromFile("lat");
            stringedSavedLon = writeReadDataInFile.readFromFile("lon");
        }
        Double fetchedCarLat2;
        Double fetchedCarLon2;
        if (!stringedSavedLat.equals("") && !stringedSavedLon.equals("")) {
            fetchedCarLat2 = Double.parseDouble(stringedSavedLat);
            fetchedCarLon2 = Double.parseDouble(stringedSavedLon);

            Log.i("saved lat n lng now", "are: "
                    + String.valueOf(fetchedCarLat2) + ", " +
                    String.valueOf(fetchedCarLon2));
            savedCarLocationForUpdate = new LatLng(fetchedCarLat2,
                    fetchedCarLon2);
        }
        if (writeReadDataInFile.readFromFile("bikeOrNot") != null) {
            if (writeReadDataInFile.readFromFile("bikeOrNot").equals("bike")) {
                cMarkerView2.setVisibility(View.VISIBLE);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savedCarLocationForUpdate, 13));
                weHaveMotorcycle = true;
            } else if (writeReadDataInFile.readFromFile("bikeOrNot").equals("noBike")) {
                cMarkerView.setVisibility(View.VISIBLE);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savedCarLocationForUpdate, 13));
                weHaveMotorcycle = false;
            }
        }
        cSplasherStatus.setVisibility(View.VISIBLE);
        if (requestType.equals("public")) {
            cSplasherStatus.setText(getResources().getString(R.string
                    .act_car_owner_searchingForAvailSp));
        }else if(requestType.equals("private")){
            cSplasherStatus.setText(getResources().getString(R.string
                    .act_car_owner_waitingForAResponse));
        }
        try {
            Log.i("grey1", "is: " + splasherUsername);
            if (splasherUsername.equals("clear")) {
                Log.i("grey2", "There is no splasher. autoTimesUp will run");
                untilTimeReachedAutoCancel();
            }else{
                if (taken.equals("no")){
                    Log.i("grey2.1", "There is splasher but hasn't accepted" +
                            " the private request. autoTimesUp will run");
                    untilTimeReachedAutoCancel();
                }
            }
        } catch (java.text.ParseException e1) {
            e1.printStackTrace();
        }
        //HANDLER and RUNNABLE--------------------------------------------------
        requestUpdateChecker.run();
    }

    public void fetchCurrentSplasherIfExists(final ParseObject object){
        splasherActive = true; //-->if so, updateMap() = inactive (false).
        isThereASplasher = object.getString("splasherUsername");
        splasherShowingName = object.getString("splasherShowingName");
        final String splasherWashNum = object.getString("splasherWashesNum")
                + " " + getResources().getString(R.string.act_car_owner_washes);
        String splasherRatingNum = object.getString("splasherRatingNum");
        int intSplasherRating = 3;
        if (splasherRatingNum != null) {
            if (Integer.parseInt(splasherRatingNum) <= 5) {
                intSplasherRating = Integer.parseInt(splasherRatingNum);
            } else {
                intSplasherRating = 5;
            }
        }

        currentSavedSelectedHour = object.getString("untilTime");
        String currentWashState = object.getString("washFinished");
        splasherPhoneNumber = object.getString("splasherPhoneNum");
        washFinished = currentWashState.equals("yes");
        final String clear = "clear";
        final String canceled = "canceled";
        final String trueImgInbound = "true";
        if (!isThereASplasher.equals(clear) && !isThereASplasher
                .equals(canceled)) {//if a splasher actually accepted your req
            Log.i("Current Splasher is", "Your Splasher is "
                    + isThereASplasher);
            cdcOps(View.GONE);
            cRadarView.setVisibility(View.GONE);
            cSplasherStatus.setVisibility(View.GONE);
            cUpdatedSeekbar.setVisibility(View.VISIBLE);
            mSplasher_info_toco_back_space.setVisibility(View.VISIBLE);
            mSplasherInfoHolder.setVisibility(View.VISIBLE);
            cUpdatedSeekbar.setProgress(1);
            final int finalIntSplasherRating = intSplasherRating;
            userClassQuery.getCurrentUserDocument(isThereASplasher, new UserClassInterface() {
                @Override
                public void getCurrentUserRow() {
                    //TODO: Logically, the Push notification should trigger here.
                    if (background) {
                        if (!notiForCarOwnerRan) {
                            String requestTakenTitle = getResources()
                                    .getString(R.string.carOwner_act_java_status);
                            String requestTakenContent = getResources()
                                    .getString(R.string.carOwner_act_java_yourRequestHasBeenTaken);
                            int Id = 23454;
                            if (writeReadDataInFile.readFromFile("notificationStatus")

                                    != null) {
                                if (writeReadDataInFile.readFromFile("notificationStatus")
                                        .equals("1")) {
                                    newNotificationsFunction(HomeActivity.class
                                            , requestTakenTitle, requestTakenContent, Id);
                                } else if (writeReadDataInFile
                                        .readFromFile("notificationStatus").equals("")) {
                                    //If the code lands here,the settings were untouched, hence true
                                    newNotificationsFunction(HomeActivity.class,
                                            requestTakenTitle, requestTakenContent, Id);
                                }
                            } else {
                                //if its null, it means the switch was never touched, hence it's ON
                                newNotificationsFunction(HomeActivity.class
                                        , requestTakenTitle, requestTakenContent, Id);
                            }
                            notiForCarOwnerRan = true;
                        }
                    }

                    //Splasher Location
                    ParseGeoPoint splasherLocation = userClassQuery.getUserFetchedLocation();
                    Log.i("splasherLocation", " is " + splasherLocation.toString());
                    Double fetchedCarLat2;
                    Double fetchedCarLon2;
                    if (writeReadDataInFile.readFromFile("lat")
                            != null && writeReadDataInFile
                            .readFromFile("lon") != null) {
                        fetchedCarLat2 = Double.parseDouble(writeReadDataInFile
                                .readFromFile("lat"));
                        fetchedCarLon2 = Double.parseDouble(writeReadDataInFile
                                .readFromFile("lon"));
                        Log.i("saved lat n lng", "are: "
                                + String.valueOf(fetchedCarLat2) + ", " +
                                String.valueOf(fetchedCarLon2));
                        savedCarLocationForUpdate = new LatLng(fetchedCarLat2, fetchedCarLon2);
                    }
                    ParseGeoPoint carOwnerCarLocation = new ParseGeoPoint
                            (savedCarLocationForUpdate.latitude
                                    , savedCarLocationForUpdate.longitude);
                    Double distanceInKm = splasherLocation
                            .distanceInKilometersTo(carOwnerCarLocation);
                    final Double distanceOneDP = (double) Math.round(distanceInKm * 10) / 10;
                    cRadarView.stopAnimation();
                    cRadarView.setVisibility(View.GONE);
                    mSplasher_stats_co_splasherName.setText(splasherShowingName);//TEST WHOLE SYSTEM
                    mSplasher_stats_co_numWashes.setText(splasherWashNum);
                    int fixedRating = finalIntSplasherRating * 2;
                    mSplasher_status_co_ratingBar.setProgress(fixedRating);
                    Bitmap defaultPic = BitmapFactory.decodeResource(getResources()
                            , R.drawable.theemptyface);
                    mSplasherPicInStatusWindows.setImageBitmap(defaultPic);
                    String splasherProfPic = object.getString("splasherProfilePic");
                    if (splasherProfPic != null) {
                        glideImagePlacement.roundImagePlacementFromString(splasherProfPic,
                                mSplasherPicInStatusWindows);
                    } else {
                        mSplasherPicInStatusWindows.setImageBitmap(defaultPic);
                    }
                    object.getString("splasherProfilePic");
                    if (!alreadyExecuted4) {
                        String splasherUpdate = distanceOneDP.toString() + " Km";
                        mSplasher_status_co_distance_text.setText(splasherUpdate);
                    }
                    //splasher location on car owner screen set up------------------------------

                    mMap.clear();


                    LatLng splasherLocationLatLng = new LatLng(splasherLocation.getLatitude()
                            , splasherLocation.getLongitude());
                    ArrayList<Marker> markers2 = new ArrayList<>();
                    markers2.add(mMap.addMarker(new MarkerOptions()
                            .position(splasherLocationLatLng)
                            .title(isThereASplasher)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.splasherlogoonmap))));

                    if (writeReadDataInFile.readFromFile("bikeOrNot") != null) {
                        if (writeReadDataInFile.readFromFile("bikeOrNot")
                                .equals("bike")) {
                            cMarkerView2.setVisibility(View.GONE);

                            markers2.add(mMap.addMarker(new MarkerOptions()
                                    .position(savedCarLocationForUpdate)
                                    .title(getResources().getString
                                            (R.string.carOwner_act_java_yourBike))
                                    .icon(BitmapDescriptorFactory
                                            .fromBitmap(markersOps.customMapSmallMarkerAdjust
                                                    (R.drawable.bigbikepinforrequest)))));

                            //cMarkerView2.setVisibility(View.VISIBLE);
                        } else if (writeReadDataInFile.readFromFile("bikeOrNot")
                                .equals("noBike")) {
                            cMarkerView.setVisibility(View.GONE);

                            markers2.add(mMap.addMarker(new MarkerOptions()
                                    .position(savedCarLocationForUpdate)
                                    .title(getResources().getString
                                            (R.string.carOwner_act_java_yourCar))
                                    .icon(BitmapDescriptorFactory
                                            .fromBitmap(markersOps.customMapSmallMarkerAdjust
                                                    (R.drawable.bigcarpinforrequest)))));

                            //cMarkerView.setVisibility(View.VISIBLE);
                        }
                    }

                    LatLngBounds.Builder builderTwo = new LatLngBounds.Builder();
                    for (Marker marker : markers2) {
                        builderTwo.include(marker.getPosition());
                    }

                    LatLngBounds boundsTwo = builderTwo.build();

                    int paddingTwo = 150;

                    CameraUpdate splasherAndCO = CameraUpdateFactory.newLatLngBounds
                            (boundsTwo, paddingTwo);

                    if (!alreadyExecuted3) {
                        mMap.animateCamera(splasherAndCO);
                        mMap.getUiSettings().setScrollGesturesEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        mMap.getUiSettings().setZoomGesturesEnabled(true);
                        mMap.getUiSettings().setAllGesturesEnabled(true);
                        alreadyExecuted3 = true;
                    }
                    //--------------------------------------------------------------------------

                    if (!alreadyExecuted) {
                        alreadyExecuted = true;
                    }
                    //TODO: Second Notification: Car washed. Triggers if car owner never
                    // opened first
                    //Car Washed Notification//
                    if (background) {
                        if (object.getString("washFinished").equals("yes")) {
                            if (!notiForCarOwnerRan2) {
                                String requestTakenTitle2 = getResources()
                                        .getString(R.string.carOwner_act_java_status);
                                String requestTakenContent2 = getResources()
                                        .getString(R.string.carOwner_act_java_youCarHasBeenWashed);
                                int Id2 = 23456;
                                if (writeReadDataInFile.readFromFile
                                        ("notificationStatus") != null) {
                                    if (writeReadDataInFile.readFromFile
                                            ("notificationStatus").equals("1")) {
                                        newNotificationsFunction(HomeActivity.class
                                                , requestTakenTitle2, requestTakenContent2, Id2);
                                    } else if (writeReadDataInFile.readFromFile
                                            ("notificationStatus").equals("")) {
                                        //If the code lands here, it means the settings were
                                        // untouched, hence true
                                        newNotificationsFunction(HomeActivity.class
                                                , requestTakenTitle2, requestTakenContent2, Id2);
                                    }
                                } else {
                                    //if its null, it means the switch was never touched, hence
                                    // it's ON
                                    newNotificationsFunction(HomeActivity.class
                                            , requestTakenTitle2, requestTakenContent2, Id2);
                                }
                                notiForCarOwnerRan2 = true;
                            }
                        }
                    }
                    //-----------------------//
                    //HERE 571
                    //When the splasher has arrived to the carOnwer's car:
                    if (!background) {
                        if (object.getString("picturesInbound").equals(trueImgInbound)) {

                            if (!washFinished) {
                                if (!alreadyExecuted4) {
                                    Log.i("washFinished","inactive");
                                    cSplasherStatus.setVisibility(View.VISIBLE);
                                    viewFliper(RelativeLayout.ABOVE, R.id.updatedSeekBarOne);
                                    String wholeSplasherStatus = getResources()
                                            .getString(R.string.carOwner_act_java_yourSplasher)
                                            + " " + splasherShowingName + " " + getResources()
                                            .getString(R.string.carOwner_act_java_hasArrived);
                                    cSplasherStatus.setText(wholeSplasherStatus);
                                    cUpdatedSeekbar.setProgress(2);
                                    if(cancelItem != null) {
                                        cancelItem.setVisible(false);
                                    }
                                    new CountDownTimer(7000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            shutDownChecker = true;
                                        }

                                        @Override
                                        public void onFinish() {
                                            startActivity(new Intent(HomeActivity
                                                    .this, WashServiceShowActivity.class));
                                        }               //CHECK RUN & CHECK
                                        //bug: the startActivity intent will call eternally
                                        //because of the runnable. possible solutions: boolean
                                        //or stop runnable(not so good).
                                    }.start();
                                    alreadyExecuted4 = true;
                                }
                            } else {
                                if (!alreadyExecuted9) {
                                    Log.i("washFinished","active");
                                    pendingPaymentRequestDialog();
                                    alreadyExecuted9 = true;
                                }
                            }
                        }
                    }
                    //Can't go to checkout. find out why
                    //Fix production bug that affected that user on the 20th of Feb
                }
            });

        } else if (isThereASplasher.equals(canceled)) {
            splasherCanceledCheck = true;
            object.put("splasherUsername", "clear");
            object.saveInBackground();
            Log.i("Current Splasher", isThereASplasher);
        }
    }

    private void serviceTextAdjustment(TextView txtView, String service){
        Log.i("servicioType", service + " ran");
        if ((service.contains("External") && service.contains("wash"))
                || (service.contains("שטיפה")) && service.contains("חיצוני")){
            Log.i("servicioType2", service);
            txtView.setText(getResources().getString(R.string.act_car_owner_externalWash));
        }else if(service.contains("Internal") || service.contains("ופנימי")){
            Log.i("servicioType2", service);
            txtView.setText(getResources().getString(R.string.act_car_owner_extAndIntWash));
        }else if(service.contains("motorcycle") || service.contains("לאופנוע")){
            Log.i("servicioType2", service);
            txtView.setText(getResources().getString(R.string.act_car_owner_motorcycle));
        }
    }

    private void viewFliper(int aboveBelow, int parentReference){
        //RelativeLayout.ABOVE, R.id.updatedSeekBarOne
        RelativeLayout.LayoutParams params= new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(aboveBelow, parentReference);
        params.setMargins(0,0,0,40);
        cSplasherStatus.setLayoutParams(params);
    }

    private void runAutoQuickTourOnce(){
        if(writeReadDataInFile.readFromFile("autoQuickTourRan").isEmpty()){
            startActivity(new Intent(HomeActivity.this, QuickTourActivity.class));
        }
    }

    private void runTakeNewSplasherToServices(){//lets copy runAutoQuickTourOnce
        Log.i("purple1", "is " + writeReadDataInFile
                .readFromFile("firstTimeServices"));
        if (writeReadDataInFile.readFromFile("firstTimeServices").isEmpty()){
            alertDialog.generalPurposeQuestionDialog(HomeActivity.this
                    ,getResources().getString(R.string.carOwner_act_java_welcomeToSplash_title)
                    ,getResources().getString(R.string.carOwner_act_java_welcomeToSplash_message)
                    ,getResources().getString(R.string.carOwner_act_java_go)
                    , null, new DialogAcceptInterface() {
                        @Override
                        public void onAcceptOption() {
                            startActivity(new Intent(HomeActivity.this
                                   , SplasherServicesActivity.class));
                        }
                    });
        }
    }

    public void countDownStartRunnable(){
        new CountDownTimer(25000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                Log.i("backgroundCheck", "called");
                requestUpdateChecker.run();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        background = true;
        if (userClassQuery.userExists()) {
            if (userClassQuery.userIsCarOwnerOrSplasher(splasher)) {
                notiForSplasherRan = false;
                countDownStartRunnable();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        background = false;
          if (userClassQuery.userExists()) {
            if (userClassQuery.userIsCarOwnerOrSplasher(splasher)) {
                Log.i("backgroundCheckStop", "called");
                handler.removeCallbacks(requestUpdateChecker);
            }
        }
    }

    //CANT STOP THIS RUNNABLE. FIND OUT WHY AND FIX IT
    public Runnable requestUpdateChecker = new Runnable() {
        @Override
        public void run() {
            if(!shutDownChecker) {
                Log.i("background job?", "Running");
                try {
                    if (userClassQuery.userExists()) {
                        if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)){
                            checkForUpdates();
                        }
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, WAITING_CONSTANT_ALL_RUNNABLE); //30 seconds
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_tool_bar_menu, menu);
        cancelItem = menu.findItem(R.id.action_cancel_wash);
        if (findCarWasherRequestActive){
            cancelItem.setVisible(true);
        }else{
            cancelItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }else if (item.getItemId() == R.id.action_cancel_wash){
            if (cRadarView.getVisibility() == View.VISIBLE) {
                String cancelTitle1 = getResources().getString
                        (R.string.carOwner_act_java_cancelRequest);
                String noSplasherCancel = getResources().getString
                        (R.string.carOwner_act_java_areYouSureCancel);
                metricsClassQuery.queryMetricsToUpdate("canceledWash");
                cancelRequestDialog(cancelTitle1, noSplasherCancel);
            } else {
                String cancelTitle2 = getResources().getString
                        (R.string.carOwner_act_java_cancelRequest);
                String yesSplasherCancel = getResources().getString
                        (R.string.carOwner_act_java_aSplasherIsOnTheWay);
                cancelRequestDialog(cancelTitle2, yesSplasherCancel);
            }
        }else if(sServicesInterfaceRan){
            Log.i("servicesBackArrow", "Ran");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void listening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @SuppressLint("DefaultLocale")
    public void untilTimeReachedAutoCancel() throws java.text.ParseException {
        Log.i("untilTimeReachedAutoCa", "ran");
        //Phone's Actual time and date------//
        Calendar calendarforAutoCancel = Calendar.getInstance();
        int currentSoleHour = calendarforAutoCancel.get(Calendar.HOUR_OF_DAY);
        int currentSoleMinute = calendarforAutoCancel.get(Calendar.MINUTE);
        int currentDate = calendarforAutoCancel.get(Calendar.DATE);

        int currentMonth = calendarforAutoCancel.get(Calendar.MONTH);
        if (currentMonth == 12)
            currentMonth = 1;
        else
            currentMonth = calendarforAutoCancel.get(Calendar.MONTH) + 1;

        int currentYear = calendarforAutoCancel.get(Calendar.YEAR);
        String fullDate = String.valueOf(currentDate) + "-" + String.valueOf(currentMonth)
                + "-" + String.valueOf(currentYear);
        //---------------------------------//

        int currentSoleHourPlusOne;
        if (currentSoleHour == 24)
            currentSoleHourPlusOne = 1;
        else
            currentSoleHourPlusOne = currentSoleHour + 1;

        String finalPhoneHour;
        String cutPMCurrentSavedSelectedHour;

        //Here you have to remove the PM/AM from both String:finalPhoneHour and
        // String:currentSavedSelectedHour. String str = "manchester united (with nice players)";
        //System.out.println(str.replace("(with nice players)", ""));
        Log.i("untilTimeCancel", "running");
        if (currentSavedSelectedHour != null) {
            Log.i("currentSavedSelected", "not null");
            Log.i("currentSavedSelected2", currentSavedSelectedHour);
            if (currentSavedSelectedHour.contains(" AM")) {
                cutPMCurrentSavedSelectedHour = currentSavedSelectedHour
                        .replace(" AM", "");
            } else {
                cutPMCurrentSavedSelectedHour = currentSavedSelectedHour
                        .replace(" PM", "");
            }
            @SuppressLint("DefaultLocale")
            String phoneHourWithoutSufix = fullDate + " " + String.format("%02d:%02d"
                    , currentSoleHourPlusOne, currentSoleMinute).toUpperCase(Locale.getDefault());
            //--------------------------------------------------------------------------//

            //--AM PM Format--//
            if (currentSoleHour >= 12 && currentSoleHour < 24) {
                //we are in PM
                finalPhoneHour = fullDate + " " + String.format("%02d:%02d"
                        , currentSoleHourPlusOne, currentSoleMinute)
                        .toUpperCase(Locale.getDefault()) + " PM";
            } else {
                //we are in AM
                finalPhoneHour = fullDate + " " + String.format("%02d:%02d"
                        , currentSoleHourPlusOne, currentSoleMinute)
                        .toUpperCase(Locale.getDefault()) + " AM";
            }

            Log.i("Checkingandhour", "Saved hour is: " + currentSavedSelectedHour
                    + " and current hour +1 is : " + finalPhoneHour);

            Log.i("Checkingcutdates", "Cut Saved Date is: "
                    + cutPMCurrentSavedSelectedHour +
                    " and phone's Cut: " + phoneHourWithoutSufix);

            //Need to add AM PM to "currentSavedSelectedHour"


            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date1 = sdf.parse(phoneHourWithoutSufix);
            Date date2 = sdf.parse(cutPMCurrentSavedSelectedHour);

            Log.i("Checkingdates", "date 1 is: " + date1 +
                    " and date 2 is: " + date2);

            //---------------//
            //-------------------------//
            //TO CHECK IF FINALPHONEHOUR IS GREATER THAN FILE, NEED TO CONVERT NUMBERS TO INTEGERS
            //IF TIMES UP, KILL REQUEST FIRST, THEN UPDATE CAR OWNER WITH ALERTDIALOG,
            // THAT WAY THE SPLASHER IS ALSO UPDATES ON SUCH REQUEST ON THE SPOT WITHOUT HAVING TO
            // WAIT FOR CAROWNER TO AKNOWLEDGE(HIT OK) THAT HIS REQUEST TIME IS UP
            if (currentSavedSelectedHour.equals(finalPhoneHour)) {
                //User selected time meets final hour. Time to auto delete the request
                //AGAIN BUG ABOUT THROWING TIMES UP WHEN DOESNT HAVE TO. FIX
                //NOW IT IS NOT EVEN THROWING TIMES UP WHEN IT SHOULD. FIX
                String timeUpTitle = getResources()
                        .getString(R.string.carOwner_act_java_yourTimesUp);
                String timeUpMessage = getResources()
                        .getString(R.string.carOwner_act_java_noSplasherPickedUp);
                if (!timeCheckedOnce) {
                    cancelAndDeleteCarWashRequest();
                    timesUpRequestDialog(timeUpTitle, timeUpMessage);
                    timeCheckedOnce = true;
                }
            } else if (date1.compareTo(date2) > 0) {
                Log.i("Checking comparison", "date1 is after date2");
                String timeUpTitle = getResources()
                        .getString(R.string.carOwner_act_java_yourTimesUp);
                String timeUpMessage = getResources()
                        .getString(R.string.carOwner_act_java_noSplasherPickedUp);
                if (!timeCheckedOnce) {
                    cancelAndDeleteCarWashRequest();
                    timesUpRequestDialog(timeUpTitle, timeUpMessage);
                    timeCheckedOnce = true;
                }
            }
            //change splashername to showingName in lil msg that shows up when splasher has arrived
            cdcOps(View.GONE);
            if(!cdcUntilTimeRan) {
                Log.i("ran","ran here");
                hd.hourDiffCalculator(phoneHourWithoutSufix, cutPMCurrentSavedSelectedHour
                        , mCarAvailTime);
                cdcUntilTimeRan = true;
            }
        }
    }

    public void checkForUpdates() throws java.text.ParseException {
        if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {
            requestClassQuery.fetchCurrentTakenRequest(new RequestClassInterface.TakenRequest() {
                @Override
                public void fetchThisTakenRequest(ParseObject object) {
                    if (object.getString("requestType").equals("public")) {
                        Log.i("here1", "here345");
                        fetchCurrentSplasherIfExists(object);
                    }else if(object.getString("requestType").equals("private")){
                        Log.i("here1.5", "here345");
                        currentSavedSelectedHour = object.getString("untilTime");
                        if (object.getString("taken").equals("yes")){
                            Log.i("here2", "here345");
                            fetchCurrentSplasherIfExists(object);
                        }else if(object.getString("taken").equals("no") &&
                                object.getString("splasherUsername")
                                        .equals("canceled")){
                            //private request and waiting//
                            Log.i("here3", "here345");
                            splasherCanceledCheck = true;
                            splasherWasPrivate = true;
                        }else if((object.getString("taken").equals("no")) &&
                                !(object.getString("splasherUsername").equals("canceled")
                                || object.getString("splasherUsername").equals("clear"))){
                            Log.i("here4", "here345");
                            untilTimeReachedRunner();
                        }
                    }
                }
            });
            /*- do all the resets to bring it back to "searching for a splasher"
              - This triggers if splasherCanceledCheck is true which occurs when a splasher
                cancel the request after accepting it
              - option 1: call this code only once<<<
             */
            //Whenever splasherCancelationDialog does not fire, check this boolean's status:
            Log.i("splasherCanceledCheck", String.valueOf(splasherCanceledCheck));
            if (splasherCanceledCheck) {
                if (!alreadyExecuted2) {
                    cdcOps(View.GONE);//BUG HERE. CDC WONT HIDE AFTER REQ CANCELLED BY SPLASHER
                    AlertDialog.Builder splasherCanceled = new AlertDialog
                            .Builder(HomeActivity.this);
                    splasherCanceled.setTitle(getResources().getString
                            (R.string.carOwner_act_java_splasherCanceled));
                    splasherCanceled.setIcon(android.R.drawable.ic_dialog_alert);
                    if (!splasherWasPrivate) {
                        splasherCanceled.setMessage(getResources().getString
                                (R.string.carOwner_act_java_splasherCanceledYourRequest2));
                    }else{
                        splasherCanceled.setMessage(getResources().getString
                                (R.string.carOwner_act_java_splasherCanceledYourRequest));
                    }
                    splasherCanceled.setPositiveButton(getResources()
                                    .getString(R.string.carOwner_act_java_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!splasherWasPrivate) {
                                        splasherCanceledCheck = false;
                                        alreadyExecuted2 = true;
                                        finish();
                                        startActivity(getIntent());
                                    }else{
                                        splasherCanceledCheck = false;
                                        alreadyExecuted2 = true;
                                        cancelAndDeleteCarWashRequest();
                                    }
                                    cdcOps(View.VISIBLE);
                                }
                            });
                    splasherCanceled.setCancelable(false); //Check if this works
                    splasherCanceled.show();
                    alreadyExecuted2 = true;
                }
            }
        }
        Log.i("washFinishedStatus", String.valueOf(washFinished));
        Log.i("untilTimeSActive"," is " + isThereASplasher);
        String clear = "clear";
        String canceled = "canceled";
        Log.i("yellow1", "is: " + isThereASplasher);
        if (isThereASplasher != null) {
            if (requestType.equals("public")) {
                Log.i("yellow1.1", "ran");
                if (isThereASplasher.equals(clear) || isThereASplasher.equals(canceled)) {
                    Log.i("yellow1.1.1", "ran");
                    untilTimeReachedRunner();
                }
            }else if(requestType.equals("private")){
                Log.i("yellow1.2", "ran");
                if(taken.equals("no")) {
                    Log.i("yellow1.2.1", "ran");
                    untilTimeReachedRunner();
                }
            }
        }
    }

    private void untilTimeReachedRunner(){
        try {
            untilTimeReachedAutoCancel();
        } catch (java.text.ParseException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //getting your location logic:
        mMap = googleMap;
        if (cRadarView.getVisibility() != View.VISIBLE) {
            mMap.clear();
        }

        //Text watcher for splash-wallet
        //splashWalletTextWatcher();
        locationManagement();

        //---Splasher---//
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null) {

                    //---->>SUCH MARKER CLICK<<-------------------//
                    int id = (Integer) marker.getTag();

                    cCarOwnerPrevDetailsRelative.setVisibility(View.VISIBLE);

                    //Data from Car Owner Profile and his request
                    getSomedataForCarOwnerInfoWindow(id);
                    //-------------------------------------------//
                    return false;
                } else {
                    return true;
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                cCarOwnerPrevDetailsRelative.setVisibility(View.GONE);
            }
        });
        //---------------//
    }

    public void locationManagement() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                alreadyExecuted8 = true;
                //Car-Owner
                updateMap(location);
                //---------
                //Splasher
                callSplasherOps(location);
                //--------

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        };
        //updateMap(lastKnownLocation);
        if (Build.VERSION.SDK_INT < 23) {

            listening();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (userClassQuery.userExists()) {
                if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, WAITING_CONSTANT_CAR_OWNER, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, WAITING_CONSTANT_CAR_OWNER, 0, locationListener);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, WAITING_CONSTANT_SPLASHER, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, WAITING_CONSTANT_SPLASHER, 0, locationListener);
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, WAITING_CONSTANT_SPLASHER, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, WAITING_CONSTANT_SPLASHER, 0, locationListener);
            }

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location lastKnownLocation2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            LatLng lastLatLong;

            LatLng lastLatLong2;

            try {
                if (lastKnownLocation != null) {
                    lastLatLong = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLong, 13));
                    updateMap(lastKnownLocation);
                    callSplasherOps(lastKnownLocation);
                } else {
                    lastLatLong2 = new LatLng(lastKnownLocation2.getLatitude(), lastKnownLocation2.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLong2, 13));
                    updateMap(lastKnownLocation2);
                    callSplasherOps(lastKnownLocation2);
                }
            }catch(NullPointerException npe1){
                toastMessages.productionMessage(getApplicationContext()
                        ,getResources()
                                .getString(R.string.carOwner_act_java_internetAndGPSDont),1);
            }

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                //We have permission

                if (userClassQuery.userExists()) {
                    if (currentUser.getString("CarOwnerOrSplasher").equals(carOwner)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                                , WAITING_CONSTANT_CAR_OWNER, 0, locationListener);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                                , WAITING_CONSTANT_CAR_OWNER, 0, locationListener);
                    }else{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                                , WAITING_CONSTANT_SPLASHER, 0, locationListener);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                                , WAITING_CONSTANT_SPLASHER, 0, locationListener);
                    }
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                            , WAITING_CONSTANT_SPLASHER, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                            , WAITING_CONSTANT_SPLASHER, 0, locationListener);
                }

                Location lastKnownLocation = null;
                LatLng lastLatLong = null;
                Location lastKnownLocation2 = null;
                LatLng lastLatLong2 = null;
                try {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager
                            .GPS_PROVIDER);
                    lastLatLong = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation
                            .getLongitude());
                    lastKnownLocation2 = locationManager.getLastKnownLocation(LocationManager
                            .NETWORK_PROVIDER);
                    lastLatLong2 = new LatLng(lastKnownLocation2.getLatitude(), lastKnownLocation2
                            .getLongitude());
                }catch (NullPointerException npe){
                    npe.printStackTrace();
                }

                if (lastKnownLocation == null) {
                    if (lastKnownLocation2 != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLong2, 13));
                        updateMap(lastKnownLocation2);
                        callSplasherOps(lastKnownLocation2);
                    }
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLong, 13));
                    updateMap(lastKnownLocation);
                    callSplasherOps(lastKnownLocation);
                }
            }
        }
    }

    //CAR OWNER LOCATION BUSINESS UPDATES///////////////////////////////////////////////////////////
    public void updateMap(Location location) {
        if (userClassQuery.userExists()) {
            if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {

                if (alreadyExecuted8)
                    cGettingLocationRelativeHome.setVisibility(View.GONE);

                if (!splasherActive) { //let's see if it doesn't fuck everything up

                    Log.i("onLocationCheck", " Is Runnings");

                    if (cRadarView.getVisibility() != View.VISIBLE) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                //buildGoogleApiClient();
                                mMap.setMyLocationEnabled(true);
                            }
                        } else {
                            //buildGoogleApiClient();
                            mMap.setMyLocationEnabled(true);
                        }
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (alreadyExecuted8) {
                            if (!alreadyExecuted6) {
                                //----------------------------------------------------------------//
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation
                                        , 13));
                                //----------------------------------------------------------------//
                                alreadyExecuted6 = true;
                            }
                        }
                    }

                    //--VERY IMPORTANT IF - ELSE STATEMENT: PREVENTS MULTIPLE MARKER FLASHING
                    // ON SPLASHER SCREEN
                    if (userClassQuery.userExists()) {
                        if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {
                            mMap.clear();
                        }
                    }

                    Bundle myKeyExtras = getIntent().getExtras();
                    String keyForActive = "";
                    if (myKeyExtras != null) {
                        keyForActive = myKeyExtras.getString("requestActive");
                    }
                    if (keyForActive != null || cRadarView.getVisibility() == View.VISIBLE) {

                        //String checkfromIntent = myKeyExtras.getString("requestActive", "none");
                        //String nada = "";
                        //if (checkfromIntent != nada && currentUser2 != null) {

                        if (userClassQuery.userExists()) {

                            findCarWasherRequestActive = true;

                            if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {

                                Double fetchedCarLat;
                                Double fetchedCarLon;
                                if (cRadarView.getVisibility() == View.VISIBLE) {

                                    if (!writeReadDataInFile.readFromFile("lat").equals("")
                                            && !writeReadDataInFile.readFromFile("lon")
                                            .equals("")) {
                                        fetchedCarLat = Double.parseDouble(writeReadDataInFile
                                                .readFromFile("lat"));
                                        fetchedCarLon = Double.parseDouble(writeReadDataInFile
                                                .readFromFile("lon"));
                                        Log.i("check", fetchedCarLat.toString() + " "
                                                + fetchedCarLon.toString());
                                        LatLng savedUserLocation2 = new LatLng(fetchedCarLat
                                                , fetchedCarLon);

                                        if (fetchedCarLat != 0.0 && fetchedCarLon != 0.0) {
                                            //TODO:motorbike or car 3
                                            if (writeReadDataInFile
                                                    .readFromFile("bikeOrNot") != null) {
                                                if (writeReadDataInFile
                                                        .readFromFile("bikeOrNot")
                                                        .equals("bike")) {
                                            /*
                                            mMap.addMarker(new MarkerOptions().position
                                            (savedUserLocation2).title(String.valueOf
                                            (R.string.carOwner_act_java_yourBike))
                                                    .icon(BitmapDescriptorFactory.fromResource(
                                                    R.drawable.motorbikelocationsmall)));
                                                    */
                                                    cMarkerView2.setVisibility(View.VISIBLE);
                                                } else if (writeReadDataInFile
                                                        .readFromFile("bikeOrNot")
                                                        .equals("noBike")) {
                                            /*
                                            mMap.addMarker(new MarkerOptions()
                                            .position(savedUserLocation2).title(String
                                            .valueOf(R.string.carOwner_act_java_yourCar))
                                                    .icon(BitmapDescriptorFactory
                                                    .fromResource(R.drawable.carlocationsmalltwo)));
                                                    */
                                                    cMarkerView.setVisibility(View.VISIBLE);
                                                }
                                            }
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                                                    (savedUserLocation2, 13));
                                            mMap.getUiSettings().setScrollGesturesEnabled(false);
                                            mMap.getUiSettings().setZoomControlsEnabled(false);
                                            mMap.getUiSettings().setZoomGesturesEnabled(false);
                                            mMap.getUiSettings().setAllGesturesEnabled(false);
                                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                        }
                                    }
                                }
                            }
                        } else {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation
                                    , 13));
                        }
                    }
                }
            }
        } else {
            if (alreadyExecuted8)
                cGettingLocationRelativeHome.setVisibility(View.GONE);
            //Her since no user signed in/logged in, call only once in this file
            if (cRadarView.getVisibility() != View.VISIBLE) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    //buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (alreadyExecuted8) {
                    if (!alreadyExecuted6) {
                        //------------------------------------------------------------------------//
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
                        //------------------------------------------------------------------------//
                        alreadyExecuted6 = true;
                    }
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SPLASHER LOCATION BUSINESS UPDATES////////////////////////////////////////////////////////////
    public void callSplasherOps(Location location) {
        if (userClassQuery.userExists()) {
            if (userClassQuery.userIsCarOwnerOrSplasher(splasher)) {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    String sServices = bundle.getString("fromServices");
                    if (sServices != null) {
                        Log.i("splasherLocCall","1");
                        runIfComingFromSServices(location);
                    }else{
                        Log.i("splasherLocCall","2");
                        upToTwentyRequestDots(location);
                    }
                }
            }
        }
    }

    private void setServiceSettingsIfEmpty(){
        profileClassQuery.getUserProfileToUpdate(new ProfileClassInterface() {
            @Override
            public void beforeQueryFetched() {

            }
            @Override
            public void updateChanges(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject object : objects){
                            String areaRange = "3.0 Km";
                            LatLng epicenterCoords = addressCoordsLocator
                                    .locationCoordsFetcher(mMap);
                            Log.i("presetCoordsSpl",String.valueOf(epicenterCoords));
                            ParseGeoPoint epicenterCoordsGeoP = new ParseGeoPoint(epicenterCoords
                                    .latitude, epicenterCoords.longitude);
                            final String epicenterAddress = addressCoordsLocator
                                    .locationAddressFetcher(epicenterCoords, new OnAddressFetched(){
                                        @Override
                                        public void setOnAddressFetched() {
                                            Log.i("presetCoordsAddress1", "success");
                                        }
                                    });
                            Log.i("presetCoordsAddress2", epicenterAddress);
                            object.put("serviceEC", epicenterAddress);
                            object.put("ECCoords", epicenterCoordsGeoP);
                            object.put("serviceRange", areaRange);
                            object.saveInBackground();
                        }
                    }
                }
            }
        });
    }

    private void runIfComingFromSServices(Location location){
        //focus epicenter location(use a 13 zoom)
        if (!sServicesInterfaceRan) {
            if (getSupportActionBar() != null) {
                //withActionBarDrawerToggle(false);
                //setDrawerIndicatorEnabled(false);
                mToggle.setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Set area range");
            }

            Button mLetsWashACar = findViewById(R.id.letsWashACar);
            mLetsWashACar.setVisibility(View.GONE);
            mSetArea = findViewById(R.id.setArea);
            mSetArea.setVisibility(View.VISIBLE);
            cGettingLocationRelativeHome.setVisibility(View.GONE);
            cSplashWalletRelative.setVisibility(View.GONE);
            cSplasherStatus.setVisibility(View.VISIBLE);
            cSplasherStatus.setText(getResources().getString(R.string
                    .splasher_services_selectEpicenter));

            if (mMap != null) {
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                locationGenerator(location);

            }

            sServicesInterfaceRan = true;
        }
    }
    private void locationGenerator(final Location location){
        //autoFetching the location for the center of the screen on the map//
        ImageView mServiceMarkerView = findViewById(R.id.serviceMarkerView);
        mLiveFetchedAdddress = findViewById(R.id.liveFetchedAddress);
        mServiceMarkerView.setVisibility(View.VISIBLE);
        mLiveFetchedAdddress.setVisibility(View.VISIBLE);
        final LatLng areaLocation = new LatLng(location.getLatitude(), location
            .getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(areaLocation, 14));

        locationParser(mLiveFetchedAdddress);

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                mLiveFetchedAdddress.setText(getResources().getString(R.string.threeDots));
                areaButtonState(mSetArea,false,R.drawable.btn_shape_grey);
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                locationParser(mLiveFetchedAdddress);
            }
        });
    }
    private void locationParser(TextView addressText){
        areaPointerLocation = addressCoordsLocator.locationCoordsFetcher(mMap);
        Log.i("areaPointerLocation",String.valueOf(areaPointerLocation));
        addressText.setText(addressCoordsLocator.locationAddressFetcher(areaPointerLocation
                , new OnAddressFetched() {
                    @Override
                    public void setOnAddressFetched() {
                        areaButtonState(mSetArea,true,R.drawable.btn_shape);
                    }
                }));
    }
    private void areaButtonState(Button areaButton, boolean state, int color){
        areaButton.setClickable(state);
        areaButton.setEnabled(state);
        areaButton.setBackgroundResource(color);
    }
    private void drawMarkerCircleRange(LatLng epicenter, int radius){
        mMap.clear();
        CircleOptions circleOptions = new CircleOptions()
                .center(epicenter)
                .radius(radius)
                .fillColor(Color.parseColor("#5940c4ff"))
                .strokeColor(getResources().getColor(R.color.ColorPrimaryDark))
                .strokeWidth(3);
        mMap.addCircle(circleOptions);
    }
    private void setRangeAreaOnSeekbar(final LatLng epicenter){
        drawMarkerCircleRange(epicenter, 1000);
        mServiceAreaSeekbar.setProgress(2);
        mLiveFetchedAdddress.setVisibility(View.GONE);
        String defaultDT = "- 1.0 Km -";
        mRangeText.setText(defaultDT);
        mServiceAreaSeekbar.setVisibility(View.VISIBLE);
        mRangeText.setVisibility(View.VISIBLE);
        mServiceAreaSeekbar.setMin(1);
        mServiceAreaSeekbar.setMax(10);
        mServiceAreaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double factor = (progress * 0.5);
                Log.i("radiusTrackerProg",String.valueOf(progress));
                Log.i("radiusTracker", String.valueOf(factor));
                if (progress == 1){
                    distanceToSet = 500;
                }else{
                    distanceToSet = (int) (factor * 1000);
                    Log.i("radiusTracker2", String.valueOf(distanceToSet) + " has a " +
                            "factor of " + String.valueOf(factor));
                }
                rangeSBProgress = progress;
                justRange = String.valueOf(factor) + " Km";
                displayedRange = "- " + String.valueOf(factor) + " Km -";
                mRangeText.setText(displayedRange);
                drawMarkerCircleRange(epicenter, distanceToSet);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                areaButtonState(mSetArea,false,R.drawable.btn_shape_grey);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                areaButtonState(mSetArea,true,R.drawable.btn_shape);
            }
        });
    }
    public void setNewArea(View v){
        if(!areaEpicenterReady){
            //We are in 1st step: set Epicenter//
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            cSplasherStatus.setText(R.string.splasher_services_selectHowWide);
            setRangeAreaOnSeekbar(areaPointerLocation);
            areaEpicenterReady = true;
            LatLngBounds unmovableCenter =
                    new LatLngBounds(areaPointerLocation,areaPointerLocation);
            mMap.setLatLngBoundsForCameraTarget(unmovableCenter);
        }else{
            //We are in 2nd step: set area range//
            boxedLoadingDialog.showLoadingDialog();
            profileClassQuery.getUserProfileToUpdate(new ProfileClassInterface() {
                @Override
                public void beforeQueryFetched() {

                }
                @Override
                public void updateChanges(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() > 0){
                            for (ParseObject profileObj : objects){
                                if (justRange == null){
                                    justRange = "1.0 Km";
                                }
                                profileObj.put("serviceRange",justRange);
                                profileObj.put("serviceEC",mLiveFetchedAdddress
                                        .getText().toString());
                                ParseGeoPoint ECCoordsGeoPoint = new ParseGeoPoint(
                                        areaPointerLocation.latitude,areaPointerLocation.longitude);
                                profileObj.put("ECCoords",ECCoordsGeoPoint);
                                profileObj.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            writeReadDataInFile.writeToFile
                                                    (mLiveFetchedAdddress.getText()
                                                            .toString(),"savedServiceArea");
                                            setResult(Activity.RESULT_OK,
                                                    new Intent().putExtra("centerAdd",
                                                            mLiveFetchedAdddress.getText()
                                                                    .toString())
                                                            .putExtra("range", justRange));
                                            finish();
                                        }else{
                                            toastMessages.productionMessage
                                                    (HomeActivity.this,getResources()
                                                            .getString(R.string
                                                    .splasher_services_thereWasAProblem),1);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }
    public void forwardSplasherToReqState(){
        if (userClassQuery.userExists()) {
            if (userClassQuery.userIsCarOwnerOrSplasher(splasher)) {
                requestClassQuery.fetchCurrRequestForSplasher(userClassQuery.userName(),
                    new RequestClassInterface.TakenRequest() {
                        @Override
                        public void fetchThisTakenRequest(final ParseObject object) {
                            if (object.getString("picturesInbound").equals("true")) {
                                //deduce by whether or not the first 4 pics cells have data or not.
                                requestClassQuery.fetchedParseFile(userClassQuery.userName()
                                        , "beforeRight", new RequestClassInterface
                                                .splasherBeforeOrAfterPics() {
                                            @Override
                                            public void beforePics() {
                                                forcedAlertDialog
                                                        .requestPendingForSplasherDialogCam(object
                                                                , "before");
                                            }

                                            @Override
                                            public void afterPics() {
                                                forcedAlertDialog
                                                        .requestPendingForSplasherDialogCam(object
                                                        , "after");
                                            }
                                        });
                            }else{
                                forcedAlertDialog.requestPendingForSplasherDialogRoute(
                                        object,
                                        dotLocation,
                                        getLongitudeToTransfer,
                                        getLatitudeToTransfer
                                );
                            }
                        }
                    });
            }
        }
    }

    public void upToTwentyRequestDots(Location location) {
        Log.i("upTo20ReqDots_func", "working");
        if (userClassQuery.userExists()) {
            if (userClassQuery.userIsCarOwnerOrSplasher(splasher)) {
                if (alreadyExecuted8)
                    cGettingLocationRelativeHome.setVisibility(View.GONE);

                queryNonDepositedMoney();
                splashGenerateSaleAutomatic.automaticPaymentForUnpayedRequests();

                Log.i("Run check splasher", " Is Running");
                //Auto location on self-location pinpoint will only happens once.
                final LatLng splasherLocation = new LatLng(location.getLatitude(), location
                        .getLongitude());
                if (alreadyExecuted8) {
                    if (!alreadyExecuted5) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                //buildGoogleApiClient();
                                mMap.setMyLocationEnabled(true);
                            }
                        } else {
                            //buildGoogleApiClient();
                            mMap.setMyLocationEnabled(true);
                        }
                        mMap.clear();
                        //------------------------------------------------------------------------//
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(splasherLocation, 13));
                        //------------------------------------------------------------------------//
                        //Forward Splasher to his step on existing request if exists//
                        forwardSplasherToReqState();
                        //----------------------------------------------------------//
                        alreadyExecuted5 = true;
                    }
                }
                //LEFT HERE. KEEP ON REFACTORING AND WHEN YOU ARE DONE WITH THE CLASS(ACTIVITY)
                //TEST.
                requestClassQuery.fetchCurrCloseRequestsForSplasher(userClassQuery.userName()
                        , splasherLocation, new RequestClassInterface.clearData() {
                            @Override
                            public void clearAllDataOnly() {
                                longitudeListCo.clear();
                                latitudeListCo.clear();
                                userNameListCo.clear();
                                carAddressListCo.clear();
                                carAddressDescListCo.clear();
                                carUntilTimeListCo.clear();
                                carServiceTypeListCo.clear();
                                carOwnerSetPriceCo.clear();
                                carOwnerDistanceCo.clear();
                                profilePicListCo.clear();
                                profilePicListNoFbCo.clear();
                                requestedNumBadgeListCo.clear();
                                carOwnerPhoneNumListCo.clear();
                                //--------------------------//
                                carBrandListCo.clear();
                                carModelListCo.clear();
                                carColorListCo.clear();
                                carYearListCo.clear();
                                carPlateListCo.clear();
                                //-------------------------//
                                idsFromRequests.clear();

                                mMap.clear();
                                tag = 0;
                            }
                        }, new RequestClassInterface.TakenRequest() {
                            @Override
                            public void fetchThisTakenRequest(ParseObject object) {
                                if (object.getString("requestType").equals("public")) {
                                    ParseGeoPoint requestLocation = (ParseGeoPoint)//1
                                            object.get("carCoordinates");
                                    //mount logo and stuff here
                                    getCloseRequestInfoOperations(object,requestLocation
                                            ,splasherLocation);

                                }else if(object.getString("requestType").equals("private")
                                        && object.getString("splasherUsername")
                                        .equals(userClassQuery.userName())){
                                    ParseGeoPoint requestLocation = (ParseGeoPoint)//1
                                            object.get("carCoordinates");
                                    //mount logo and stuff here//
                                    getCloseRequestInfoOperations(object,requestLocation
                                            ,splasherLocation);

                                    if (!alreadyExecuted10) {
                                        alertDialog.takePrivateRequest();
                                        alreadyExecuted10 = true;
                                    }
                                }
                            }
                        });
                if(!writeReadDataInFile.readFromFile("savedServiceArea").isEmpty()
                        || !writeReadDataInFile.readFromFile("savedServiceArea")
                        .equals("")){
                    if (!presetServiceSettingsRan) {
                        toastMessages.debugMesssage(HomeActivity.this
                                , "there is a set location", 1);
                        presetServiceSettingsRan = true;
                    }
                }else{
                    if (!presetServiceSettingsRan) {
                        toastMessages.debugMesssage(HomeActivity.this
                                ,"there ISN'T a set location. setting one now",1);
                        setServiceSettingsIfEmpty();
                        presetServiceSettingsRan = true;
                    }
                }
            }
        }
    }

    private void getCloseRequestInfoOperations(ParseObject object, ParseGeoPoint requestLoc
            ,LatLng splasherLocation){
        carOwnerUsernameCo = object.getString("username");//2
        carOwnerCarAddressCo = object.getString("carAddress");//3
        profilePicCo = object.getString("fbProfilePic");
        profilePicNoFbCo = object.getString("ProfPicNoFb");
        carOwnerCarAddressDescCo = object.getString("carAddressDesc");//4
        carOwnerCarUntilTimeCo = object.getString("untilTime");//5
        carOwnerCarServiceTypeCo = object.getString("serviceType");//6
        priceCo = object.getString("priceWanted");//7
        requestedBadgeCo = object.getString("badgeWanted");//8
        carOwnerPhoneNum = object.getString("carOwnerPhoneNum");//9
        //--------------------------------------------------------//
        carOwnerCarBrandCo = object.getString("carBrand");//10
        carOwnerCarModelCo = object.getString("carModel");//11
        carOwnerCarColorCo = object.getString("carColor");//12
        carOwnerCarYearCo = object.getString("carYear");//13
        carOwnerCarPlateCo = object.getString("carplateNumber");//14

        Log.i("stuff", carOwnerCarAddressCo + " "
                + carOwnerCarAddressDescCo + " " +
                carOwnerCarUntilTimeCo + " " + carOwnerCarServiceTypeCo
                + " " + carOwnerCarBrandCo + " " + carOwnerCarModelCo
                + " " + carOwnerCarColorCo + " " + carOwnerCarYearCo
                + " " + carOwnerCarPlateCo);

        ParseGeoPoint geoPointSelfLocation = new ParseGeoPoint
                (splasherLocation.latitude, splasherLocation.longitude);

        Double distanceToRequestsInKm = geoPointSelfLocation
                .distanceInKilometersTo(requestLoc);
        Double distanceInOneDP = (double) Math.round
                (distanceToRequestsInKm * 10) / 10;
        distanceCo = String.valueOf(distanceInOneDP);

        //Populating...
        //maybe all i need is the strings holding the data from
        //the servermaybe i dont need an arraylist

        //Delete marker if its pre hour limit has passed
        //1 way you can solve this is by retrieving phone's
        //actual time and adding +1 to HOUR_OF_DAY var from it
        //, then runninga checking for when phone's HOUR_OF_DAY +1 is
        //equal or greater than untilTime var if so, prevent the
        //marker from showing. Since we are in a loop, this would
        //apply to all active requests that meet this query's
        //constraints. madafaka.

        //Phone's Actual time and date--------------------------------//
        Calendar calendarforReqMap = Calendar.getInstance();
        int hourSplasherSide = calendarforReqMap.get(Calendar
                .HOUR_OF_DAY);
        int minuteSplasherSide = calendarforReqMap.get(Calendar.MINUTE);
        int daySplasherSide = calendarforReqMap.get(Calendar.DATE);

        int monthSplasherSide = calendarforReqMap.get(Calendar.MONTH);
        if (monthSplasherSide == 12)
            monthSplasherSide = 1;
        else
            monthSplasherSide = calendarforReqMap.get(Calendar.MONTH)
                    + 1;

        int yearSplasherSide = calendarforReqMap.get(Calendar.YEAR);
        String newFullDateSS;
        newFullDateSS = String.valueOf(daySplasherSide) + "-" + String
                .valueOf(monthSplasherSide)
                + "-" + String.valueOf(yearSplasherSide);

        int hourPlusOneSS;
        if (hourSplasherSide == 24)
            hourPlusOneSS = 1;
        else
            hourPlusOneSS = hourSplasherSide + 1;

        @SuppressLint("DefaultLocale") final String fullTotalDateSS
                = newFullDateSS + " " +
                String.format("%02d:%02d", hourPlusOneSS,
                        minuteSplasherSide).toUpperCase(Locale
                        .getDefault());
        //------------------------------------------------------------//

        //Selected Until Time Request---------------------------------//
        String cutCarOwnerCarUntilTimeCo;
        if (carOwnerCarUntilTimeCo.contains(" AM")) {
            cutCarOwnerCarUntilTimeCo = carOwnerCarUntilTimeCo
                    .replace(" AM", "");
        } else {
            cutCarOwnerCarUntilTimeCo = carOwnerCarUntilTimeCo
                    .replace(" PM", "");
        }
        //------------------------------------------------------------//

        //Current request "Until Time" and "Phone" Date-----//
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat
                ("dd-MM-yyyy HH:mm");
        Date currentSSDate1 = null;
        Date savedSSDate2 = null;
        try {
            currentSSDate1 = sdf.parse(fullTotalDateSS);
            savedSSDate2 = sdf.parse(cutCarOwnerCarUntilTimeCo);
        } catch (java.text.ParseException eDate) {
            eDate.printStackTrace();
        }
        Log.i("Checking Until time", "is: "
                + cutCarOwnerCarUntilTimeCo);
        Log.i("Checking Phone's time", "is: "
                + fullTotalDateSS);
        Log.i("Checking CurrentDate", "is: " + currentSSDate1);
        Log.i("Checking SavedDate", "is: " + savedSSDate2);
        //--------------------------------------------------//

        //TODO: motorbike or car 4
        if (object.getString("serviceType").contains("Motorcycle")
                || object.getString("serviceType")
                .contains("אופנוע")) {
            dotLocation = new LatLng(requestLoc.getLatitude()
                    , requestLoc.getLongitude());
            options = new MarkerOptions()
                    .position(dotLocation)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(markersOps.smallMarker
                                    (R.drawable.bigbikepinforrequest)));
        } else {
            dotLocation = new LatLng(requestLoc.getLatitude()
                    , requestLoc.getLongitude());
            options = new MarkerOptions()
                    .position(dotLocation)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(markersOps.smallMarker
                                    (R.drawable.bigcarpinforrequest)));
        }
        //error//sometimes untilTime Date get sets to null in the server
        if (currentSSDate1 != null) {
            if (!(currentSSDate1.compareTo(savedSSDate2) > 0)) {
                dotMarker = mMap.addMarker(options);
                dotMarker.setTag(tag);
                idsFromRequests.add(tag);
            }
        }

        Log.i("tagList tag", "is:" + String.valueOf(tag));
        Log.i("tagList total", "is:"
                + String.valueOf(idsFromRequests.size()));

        longitudeCo = dotLocation.longitude;
        latitudeCo = dotLocation.latitude;

        longitudeListCo.add(longitudeCo);
        latitudeListCo.add(latitudeCo);
        profilePicListCo.add(profilePicCo);
        profilePicListNoFbCo.add(profilePicNoFbCo);
        userNameListCo.add(carOwnerUsernameCo);
        carAddressListCo.add(carOwnerCarAddressCo);
        carAddressDescListCo.add(carOwnerCarAddressDescCo);
        carUntilTimeListCo.add(carOwnerCarUntilTimeCo);
        carServiceTypeListCo.add(carOwnerCarServiceTypeCo);
        carOwnerSetPriceCo.add(priceCo);
        carOwnerDistanceCo.add(distanceCo);
        requestedNumBadgeListCo.add(requestedBadgeCo);
        carOwnerPhoneNumListCo.add(carOwnerPhoneNum);
        //--------------------------//
        carBrandListCo.add(carOwnerCarBrandCo);
        carModelListCo.add(carOwnerCarModelCo);
        carColorListCo.add(carOwnerCarColorCo);
        carYearListCo.add(carOwnerCarYearCo);
        carPlateListCo.add(carOwnerCarPlateCo);
        //-------------------------//
        tag++;
    }

    public void getSomedataForCarOwnerInfoWindow(int position) {

        //Tag position index must match that of a listView onItemClick Function which is zero (0).
        if (longitudeListCo.size() > position && latitudeListCo.size() > position) {

            //Data from Car Owner Profile and his request TO BE SHOWN ON INFOWINDOW
            String distanceInKm = carOwnerDistanceCo.get(position) + " Km";
            cDistanceDetsValue.setText(distanceInKm);
            cYouGetDetsValue.setText(carOwnerSetPriceCo.get(position));
            String uncutFullUntilTime = carUntilTimeListCo.get(position) + "  ";
            requestNumercialBadge = Integer.parseInt(requestedNumBadgeListCo.get(position));
            if(requestNumercialBadge == 4){
                showBadge2(R.drawable.platbadge);
            }else if(requestNumercialBadge == 3){
                showBadge2(R.drawable.goldbadge);
            }else if(requestNumercialBadge == 2){
                showBadge2(R.drawable.silverbadge);
            }else if(requestNumercialBadge == 1){
                showBadge2(R.drawable.bronzebadge);
            }
            //Phone's DATE for infoWindow Date removal---//
            Calendar someDataCalendar = Calendar.getInstance();

            int daySplasherSideIW = someDataCalendar.get(Calendar.DATE);
            int daySplasherSideIW2 = someDataCalendar.get(Calendar.DATE) + 1;

            int monthSplasherSideIW = someDataCalendar.get(Calendar.MONTH);
            if(monthSplasherSideIW == 12)
                monthSplasherSideIW = 1;
            else
                monthSplasherSideIW = someDataCalendar.get(Calendar.MONTH) + 1;

            int yearSplasherSideIW = someDataCalendar.get(Calendar.YEAR);

            String newFullDateIW = String.valueOf(daySplasherSideIW) + "-" + String
                    .valueOf(monthSplasherSideIW) + "-" + String.valueOf(yearSplasherSideIW);
            String newFullDateIW2 = String.valueOf(daySplasherSideIW2) + "-" + String
                    .valueOf(monthSplasherSideIW) + "-" + String.valueOf(yearSplasherSideIW);
            Log.i("info dates", uncutFullUntilTime + "." + newFullDateIW);
            //18-1-2018 20:30 PM..
            //0123456789

            String newUntilTime = "";
            if(uncutFullUntilTime.contains(newFullDateIW)){
                cTomorrowText.setVisibility(View.GONE);
                if(daySplasherSideIW < 10){
                    if(monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(9,17);
                    }else{//>
                        newUntilTime = uncutFullUntilTime.substring(10,18);
                    }
                }else{//>
                    if(monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(10,18);
                    }else{//>
                        newUntilTime = uncutFullUntilTime.substring(11,19);
                    }
                }
            }else if(uncutFullUntilTime.contains(newFullDateIW2)){
                cTomorrowText.setVisibility(View.VISIBLE);
                String stringOfTomorrow = getResources().getString(R.string
                        .carOwner_act_java_ofTomorrow);
                if(daySplasherSideIW < 10){
                    if(monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(9,17);
                        cTomorrowText.setText(stringOfTomorrow);
                    }else{//>
                        newUntilTime = uncutFullUntilTime.substring(10,18);
                        cTomorrowText.setText(stringOfTomorrow);
                    }
                }else{//>
                    if(monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(10,18);
                        cTomorrowText.setText(stringOfTomorrow);
                    }else{//>
                        newUntilTime = uncutFullUntilTime.substring(11,19);
                        cTomorrowText.setText(stringOfTomorrow);
                    }
                }
            }
            Log.i("info dates2", "is: " + newUntilTime);
            //-------------------------------------------//
            cAvailableUntilDetsValue.setText(newUntilTime);
            //---------------------------------------------------------------------

            //Data to Transfer to CarOwnerLocation activity1
            getLongitudeToTransfer = longitudeListCo.get(position);
            getLatitudeToTransfer = latitudeListCo.get(position);
            String getProfilePicToTransfer = profilePicListCo.get(position);
            String getProfilePicNoFbToTransfer = profilePicListNoFbCo.get(position);
            usernameToTransfer = userNameListCo.get(position);
            carAddressToTransfer = carAddressListCo.get(position);
            carAddressDescToTransfer = carAddressDescListCo.get(position);
            carUntilTimeToTransfer = carUntilTimeListCo.get(position);
            carServiceTypeToTransfer = carServiceTypeListCo.get(position);
            priceToTransfer = carOwnerSetPriceCo.get(position);
            carOwnerPhoneNToTransfer = carOwnerPhoneNumListCo.get(position);
            //distanceToTransfer = carOwnerDistanceCo.get(position);
            carBrandToTransfer = carBrandListCo.get(position);
            carModelToTransfer = carModelListCo.get(position);
            carColorToTransfer = carColorListCo.get(position);
            carYearToTransfer = carYearListCo.get(position);
            carPlateToTransfer = carPlateListCo.get(position);
            //---------------------------------------------

            //Toast.makeText(getApplicationContext(), "works even", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), getProfilePicToTransfer
            // , Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), getProfilePicNoFbToTransfer
            // , Toast.LENGTH_LONG).show();

            //ProfilePic
            try {
                if (getProfilePicToTransfer.contains("https")) {
                    Uri uriProfilePic = Uri.parse(getProfilePicToTransfer);
                    if (!getProfilePicToTransfer.equals("none")) {
                        toastMessages.debugMesssage(getApplicationContext()
                                ,"face",1);
                        glideImagePlacement.roundImagePlacementFromUri(uriProfilePic
                                , cCarOwnerDetsProfilePic);
                        //cBeforeWashFull.setRotation(90);
                    }

                } else if (!getProfilePicToTransfer.contains("https")) {
                    toastMessages.debugMesssage(getApplicationContext()
                            ,"local",1);
                    if (getProfilePicNoFbToTransfer != null) {
                        Uri profilePicNoFb = Uri.parse(getProfilePicNoFbToTransfer);
                        glideImagePlacement.roundImagePlacementFromUri(profilePicNoFb
                                , cCarOwnerDetsProfilePic);
                        //cBeforeWashFull.setRotation(90);
                    }
                    if (getProfilePicNoFbToTransfer != null && !getProfilePicNoFbToTransfer
                            .contains("content")) {
                        Bitmap defaultPic = BitmapFactory.decodeResource(getResources()
                                , R.drawable.theemptyface);
                        cCarOwnerDetsProfilePic.setImageBitmap(defaultPic);
                    }
                } else {
                    toastMessages.debugMesssage(getApplicationContext(), "none", 1);
                    Bitmap defaultPic = BitmapFactory.decodeResource(getResources()
                            , R.drawable.theemptyface);
                    cCarOwnerDetsProfilePic.setImageBitmap(defaultPic);
                }
            }catch(NullPointerException npe1){
                npe1.printStackTrace();
                Bitmap defaultPic = BitmapFactory.decodeResource(getResources()
                        , R.drawable.theemptyface);
                cCarOwnerDetsProfilePic.setImageBitmap(defaultPic);
            }
        }
    }

    public void goCODets(View view) {

        //Here need to do something to prevent crashing if the splasher clicks on a marker,
        // and after sometime
        //of not doing anythign with its window, the request is gone for wahtever reason.
        //Most reasonable idea so far is to notify the splasher about it on "OK" goCODets() onClick.
        //Trigger some kind of (-if-)

        //Data to transfer to CarOwnerLocation activity2
        if (requestNumercialBadge <= splasherNumericalBadge) {
            Intent intent = new Intent(HomeActivity.this,
                    SplasherClientRouteActivity.class);

            intent.putExtra("requestLatitudes", getLatitudeToTransfer);
            intent.putExtra("requestLongitudes", getLongitudeToTransfer);
            intent.putExtra("carOwnerUsername", usernameToTransfer); //keep changing the
            // keys to the exact ones from CarOWnerRequestsActivity
            intent.putExtra("carOwnerCarAddress", carAddressToTransfer);
            intent.putExtra("carOwnerCarAddressDesc", carAddressDescToTransfer);
            intent.putExtra("carOwnerCarUntilTime", carUntilTimeToTransfer);
            intent.putExtra("carOwnerCarServiceType", carServiceTypeToTransfer);
            intent.putExtra("setPrice", priceToTransfer);
            intent.putExtra("carOwnerPhoneNum", carOwnerPhoneNToTransfer);
            //intent.putExtra("carOwnerDistanceFromMap", distanceToTransfer);

            intent.putExtra("carOwnerCarBrand", carBrandToTransfer);
            intent.putExtra("carOwnerCarModel", carModelToTransfer);
            intent.putExtra("carOwnerCarColor", carColorToTransfer);
            intent.putExtra("carOwnerCarYear", carYearToTransfer);
            intent.putExtra("carOwnerCarPlate", carPlateToTransfer);
            intent.putExtra("specData", "COAKey");
            startActivity(intent);
            //----------------------------------------------

            Log.i("transfer Lat:", "is" + getLatitudeToTransfer);
        } else if (requestNumercialBadge > splasherNumericalBadge) {
            toastMessages.productionMessage(getApplicationContext()
                    , getResources().getString(R.string.carOwner_act_java_yourRatingNotHigh)
                    , 1);
        }
        Log.i("badgeRequested", String.valueOf(requestNumercialBadge));
        Log.i("badgeSplasher", String.valueOf(splasherNumericalBadge));
    }

    public void cancelCODets(View view) {
        cCarOwnerPrevDetailsRelative.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission
                            .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (userClassQuery.userExists()) {
                            if (userClassQuery.userIsCarOwnerOrSplasher(carOwner)) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        WAITING_CONSTANT_CAR_OWNER, 0, locationListener);
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                        WAITING_CONSTANT_CAR_OWNER, 0, locationListener);
                            } else {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        WAITING_CONSTANT_SPLASHER, 0, locationListener);
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                        WAITING_CONSTANT_SPLASHER, 0, locationListener);
                            }
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    WAITING_CONSTANT_SPLASHER, 0, locationListener);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                    WAITING_CONSTANT_SPLASHER, 0, locationListener);
                        }
                        Location lastKnownLocation = locationManager.getLastKnownLocation
                                (LocationManager.GPS_PROVIDER);
                        Location lastKnownLocation2 = locationManager.getLastKnownLocation
                                (LocationManager.NETWORK_PROVIDER);
                        if (lastKnownLocation == null) {
                            if (lastKnownLocation2 != null) {
                                updateMap(lastKnownLocation2);

                                callSplasherOps(lastKnownLocation2);
                            }
                        } else {
                            updateMap(lastKnownLocation);

                            callSplasherOps(lastKnownLocation);
                        }
                        toastMessages.debugMesssage(getApplicationContext()
                                , "Connected", 1);

                    } else {
                        toastMessages.productionMessage(getApplicationContext()
                                , "problem with context compat or checking self permission"
                                , 1);
                        Log.e("RequestPermission Error"
                                , "problem with context compat or checking self permission");
                    }
                }catch(NullPointerException npe){
                    npe.printStackTrace();
                }
            } else {
                toastMessages.productionMessage(getApplicationContext()
                        ,"grant result error",1);
                Log.e("grantResult error", "grant result error");
            }

        } else {
            toastMessages.productionMessage(getApplicationContext()
                    ,"request code error",1);

            Log.e("request code error", "request code error");
        }

        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                toastMessages.productionMessage(getApplicationContext()
                        ,getResources().getString(R.string.cameraIntent_act_java_permissionGranted)
                        ,1);
            } else {
                toastMessages.productionMessage(getApplicationContext()
                        ,getResources().getString(R.string.cameraIntent_act_java_permissionToWrite)
                        ,1);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public void showBadge(int drawable){
        glideImagePlacement.roundImagePlacementFromDrawable(drawable, cRatingBadge);
    }
    public void showBadge2(int drawable){
        glideImagePlacement.roundImagePlacementFromDrawable(drawable, cInfoWindowSplashBadge);
    }

    public void letsWashACar(View view) {
        if (userClassQuery.userExists()) {
            toastMessages.debugMesssage(getApplicationContext()
                    , "Car Owner request pressed", 1);
            Intent intent = new Intent(HomeActivity.this, WashRequestsActivity.class);
            writeReadDataInFile.writeToFile(String.valueOf(splasherNumericalBadge)
                    , "mySplasherBadge");
            startActivity(intent);
        } else {
            String stringNotLoggedIn2 = getResources().getString
                    (R.string.carOwner_act_java_youHaveToSignOrLog);
            notLoggedIn(stringNotLoggedIn2);
        }
    }

    public void notLoggedIn(String string) {
        AlertDialog.Builder requestDialog = new AlertDialog.Builder(HomeActivity.this);
        requestDialog.setTitle(getResources().getString(R.string.carOwner_act_java_notLoggedIn));
        requestDialog.setIcon(android.R.drawable.ic_dialog_alert);
        requestDialog.setMessage(string);
        requestDialog.setPositiveButton(getResources().getString(R.string.carOwner_act_java_ok)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HomeActivity.this,
                        SignUpLogInActivity.class);
                startActivity(intent);
            }
        });
        requestDialog.setNegativeButton(getResources().getString(R.string
                .carOwner_act_java_cancel), null);
        requestDialog.show();
    }

    public void cancelAndDeleteCarWashRequest() {
        requestClassQuery.cancelAndDeleteRequest(userClassQuery.userName()
                , new RequestClassInterface.requestDeletion() {
                    @Override
                    public void deleteRequest() {
                        boxedLoadingDialog.hideLoadingDialog();
                        toastMessages.productionMessage(getApplicationContext()
                                ,getResources().getString
                                        (R.string.carOwner_act_java_requestDeleted)
                                ,1);
                        handler.removeCallbacks(requestUpdateChecker);
                        shutDownChecker = true;
                    }

                    @Override
                    public void handleRest() {
                        handler.removeCallbacks(requestUpdateChecker);
                        shutDownChecker = true;
                        writeReadDataInFile.writeToFile("empty", "bikeOrNot");
                        //this way, the user can close and even shutdown the app,
                        // as long as his request stays "true" checked on Request class
                        // on Parse, he will be subject to a splasher accepting his request.
                        // The only way to get rid of the "check" is to hit "cancel wash"
                        // button which will set the "check" to false and delete the request
                        // object from the request class.
                        if (cRadarView.getVisibility() == View.VISIBLE) {
                            cancelationOperations();
                        }else{ //<--If splasher is indeed active, then:
                           cancelationOperations();
                        }
                    }
                });
    }

    private void cancelationOperations(){
        writeReadDataInFile.writeToFile("empty", "bikeOrNot");
        cRadarView.stopAnimation();
        cSplasherStatus.setVisibility(View.GONE);
        cancelItem.setVisible(false);
        mRequest_info_window.setVisibility(View.GONE);
        mSplasher_info_toco_back_space.setVisibility(View.GONE);
        mSplasherInfoHolder.setVisibility(View.GONE);
        cRadarView.setVisibility(View.GONE);
        cMarkerView.setVisibility(View.GONE);
        cMarkerView2.setVisibility(View.GONE);
        cUpdatedSeekbar.setVisibility(View.GONE);
        mCountDownClockRela.setVisibility(View.GONE);
        cFindMeAWash.setVisibility(View.VISIBLE);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //cancel boolean
        findCarWasherRequestActive = false; //<-- switched to false
        //check to see if splasherActive switch to false
        splasherActive = false;
        handler.removeCallbacks(requestUpdateChecker);
        shutDownChecker = true;
        alreadyExecuted6 = false;
        try{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,13));
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        mMap.clear();
        //emptyCoordinatesFiles();
    }

    public void cLogOutRequestWarning(){
        AlertDialog.Builder logOutRequestWarning = new AlertDialog
                .Builder(HomeActivity.this);
        logOutRequestWarning.setTitle(getResources().getString(R.string
                .carOwner_act_java_carWashReqActive));
        logOutRequestWarning.setIcon(android.R.drawable.ic_dialog_alert);
        logOutRequestWarning.setMessage(getResources().getString(R.string
                .carOwner_act_java_youHaveSentACarWash));
        logOutRequestWarning.setPositiveButton(getResources().getString(R.string
                        .carOwner_act_java_logoutAnyway),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.removeCallbacks(requestUpdateChecker);
                        shutDownChecker = true;
                        userClassQuery.logOutUserEP(SignUpLogInActivity.class);

                    }
                });
        logOutRequestWarning.setNegativeButton(getResources()
                .getString(R.string.carOwner_act_java_ok), null);
        logOutRequestWarning.setCancelable(false);
        logOutRequestWarning.show();
    }

    public void cancelRequestDialog(String cancelTitle, String cancelMessage){
        Log.i("radar VISIBLE?", String.valueOf(cRadarView.getVisibility() == View.VISIBLE));
        AlertDialog.Builder cancelWhileSplasherOn = new AlertDialog
                .Builder(HomeActivity.this);
        cancelWhileSplasherOn.setTitle(cancelTitle);
        cancelWhileSplasherOn.setIcon(android.R.drawable.ic_dialog_alert);
        cancelWhileSplasherOn.setMessage(cancelMessage);
        cancelWhileSplasherOn.setPositiveButton(getResources().getString
                        (R.string.carOwner_act_java_yes),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boxedLoadingDialog.showLoadingDialog();
                        cancelAndDeleteCarWashRequest();
                    }
                });
        cancelWhileSplasherOn.setNegativeButton(getResources()
                .getString(R.string.carOwner_act_java_no), null);
        cancelWhileSplasherOn.setCancelable(false);
        cancelWhileSplasherOn.show();
    }

    public void newNotificationsFunction(Class targetClass, String title, String content, int id){
        notifications.newLocalNotification(targetClass,title,content,id);
    }

    public void timesUpRequestDialog(String cancelTitle, String cancelMessage){
        AlertDialog.Builder timesUpDialog = new AlertDialog.Builder(HomeActivity.this);
        timesUpDialog.setTitle(cancelTitle);
        timesUpDialog.setIcon(android.R.drawable.ic_dialog_alert);
        timesUpDialog.setMessage(cancelMessage);
        timesUpDialog.setPositiveButton(getResources().getString(R.string.carOwner_act_java_ok)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alreadyExecuted6  = false;
                writeReadDataInFile.writeToFile("empty", "bikeOrNot");
            }
        });
        timesUpDialog.setCancelable(false);
        timesUpDialog.show();
    }

    public void pendingPaymentRequestDialog(){
        AlertDialog.Builder pendingPaymentDialog = new AlertDialog.Builder(HomeActivity.this);
        pendingPaymentDialog.setTitle(getResources().getString(R.string
                .carOwner_act_java_pendingReqTitle));
        pendingPaymentDialog.setIcon(android.R.drawable.ic_dialog_alert);
        pendingPaymentDialog.setMessage(getResources()
                .getString(R.string.carOwner_act_java_pendingReqBody));
        pendingPaymentDialog.setPositiveButton(getResources()
                .getString(R.string.carOwner_act_java_go), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HomeActivity.this
                        , WashServiceShowActivity.class);
                String scratchKey = "scratch";
                intent.putExtra("key", scratchKey);
                startActivity(intent);
            }
        });
        pendingPaymentDialog.setCancelable(false);
        pendingPaymentDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!sServicesInterfaceRan) {
                moveTaskToBack(true);
            }else{
                finish();
            }
        }
        /*
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //Do nothing for now
        }
        */
        return false;
    }

    public void dispatchDocumentsSplasherSignUp(){
        Log.i("data payme 1st","1st call");
        if(userClassQuery.userIsCarOwnerOrSplasher(splasher)) {
            Log.i("data payme 2nd","2nd call");
            String paymeKey;
            Bundle dataFromSplasherSignUp = getIntent().getExtras();
            if(dataFromSplasherSignUp != null) {
                Log.i("data payme 3rd","3rd call");
                paymeKey = dataFromSplasherSignUp.getString("PaymentClientKey");
                if (paymeKey != null) {
                    Log.i("data payme 4th","4th call");
                    if (paymeKey.equals(PaymeConstants.PAYMENT_CLIENT_KEY)) {
                        Log.i("data payme", "We in payme function");
                        paymeClientKey = dataFromSplasherSignUp.getString("PaymentClientKey");
                        Log.i("data payme2", paymeClientKey);
                        sellerFirstName = dataFromSplasherSignUp.getString("sellerFirstName");
                        Log.i("data payme3", sellerFirstName);
                        sellerLastName = dataFromSplasherSignUp.getString("sellerLastName");
                        sellerSocialId = dataFromSplasherSignUp.getString("sellerSocialId");
                        sellerBirthDate = dataFromSplasherSignUp.getString("sellerBirthDate");
                        sellerSocialIdIssued = dataFromSplasherSignUp
                                .getString("sellerSocialIdIssued");
                        sellerGender = dataFromSplasherSignUp.getInt("sellerGender");//Int//
                        sellerEmail = dataFromSplasherSignUp.getString("sellerEmail");
                        sellerPhoneNumber = dataFromSplasherSignUp
                                .getString("sellerPhoneNumber");

                        sellerBankCode = dataFromSplasherSignUp.getInt("sellerBankCode");//Int//
                        sellerBankBranch = dataFromSplasherSignUp
                                .getInt("sellerBankBranch");//Int//
                        sellerBankAccountNumber = dataFromSplasherSignUp
                                .getInt("sellerBankAccountNumber");//Int//

                        sellerDescription = dataFromSplasherSignUp
                                .getString("sellerDescription");
                        sellerSiteUrl = dataFromSplasherSignUp.getString("sellerSiteUrl");
                        sellerPersonBussinessType = dataFromSplasherSignUp
                                .getString("sellerPersonBussinessType");
                        sellerInc = dataFromSplasherSignUp.getInt("sellerInc");//Int//

                        sellerAddressCity = dataFromSplasherSignUp
                                .getString("sellerAddressCity");
                        sellerAddressStreet = dataFromSplasherSignUp
                                .getString("sellerAddressStreet");
                        sellerAddressStreetNumber = dataFromSplasherSignUp
                                .getInt("sellerAddressStreetNumber");//Int//
                        sellerAddressCountry = dataFromSplasherSignUp
                                .getString("sellerAddressCountry");

                        sellerMarketFee = dataFromSplasherSignUp
                                .getDouble("sellerMarketFee");//Double//

                        //--PARAMETER RELEVANT TO APP IN PRODUCTION ONLY--//
                        sellerPlan = dataFromSplasherSignUp.getString("sellerPlan");
                        //------------------------------------------------//

                        //Query URLs of all 3 documents from the Parse server 'Documents' class://
                        //Test that both from gal and from cam work well first
                        DocumentsClassSend documentsClassSend
                                = new DocumentsClassSend(HomeActivity.this);
                        documentsClassSend.sendSplasherDocumentsToServer
                                (new DocumentsClassInterface.sendSplasherDocs() {
                            @Override
                            public void sendDocs(ParseObject fileObject) {
                                ParseFile socialIdFile = fileObject
                                        .getParseFile("socialIdProof");
                                if(socialIdFile != null){
                                    Log.i("socialIdProof", socialIdFile.getUrl());
                                    sellerFileSocialId = socialIdFile.getUrl();

                                    ParseFile bankFile = fileObject.getParseFile("bankProof");
                                    if(bankFile != null){
                                        Log.i("bankProof", bankFile.getUrl());
                                        sellerFileCheque = bankFile.getUrl();

                                        ParseFile incDocFile = fileObject
                                                .getParseFile("incDocProof");
                                        if(incDocFile != null){
                                            Log.i("incDocProof", incDocFile.getUrl());
                                            sellerFileCorporate = incDocFile.getUrl();

                                            splashCreateSeller.sendDataToPaymentServer(
                                                    paymeClientKey,sellerFirstName
                                                    ,sellerLastName, sellerSocialId
                                                    ,sellerBirthDate,sellerSocialIdIssued
                                                    ,sellerGender, sellerEmail
                                                    ,sellerPhoneNumber,sellerBankCode
                                                    ,sellerBankBranch,sellerBankAccountNumber
                                                    ,sellerDescription,sellerSiteUrl
                                                    ,sellerPersonBussinessType,sellerInc
                                                    ,sellerAddressCity,sellerAddressStreet
                                                    ,sellerAddressStreetNumber,sellerAddressCountry
                                                    ,sellerMarketFee,sellerFileSocialId
                                                    ,sellerFileCheque,sellerFileCorporate
                                                    ,sellerPlan
                                            );
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    //Query all the money the Splasher has made
    public void queryNonDepositedMoney(){
        try {
            HistoryClassQuery historyClassQuery = new HistoryClassQuery(HomeActivity.this);
            historyClassQuery.querySplasherNonDepositedMoney
                    (new HistoryClassInterface.queryNonDepositedMoney() {
                @Override
                public void beforeMainOperation() {
                    tag = 0;
                    doubleSalesWithTip = 0;
                    doubleFinal = 0;
                }

                @Override
                public void queryMoney(ParseObject moneyObj) {
                    String shekel = getResources().getString(R.string.shekel);
                    //carOwnerUsernameCo = dotObject.getString("username");//2
                    double salesWithTipNum = moneyObj.getDouble("priceWithTip");
                    double saleTipTwoDP = Double.parseDouble(df.format(salesWithTipNum));
                    salesWithTip = String.valueOf(saleTipTwoDP);
                    doubleSalesWithTip = Double.parseDouble(salesWithTip);
                    doubleFinal += doubleSalesWithTip;
                    stringFinal = String.valueOf(df.format(doubleFinal));
                    stringFinalShekel = shekel + " " + stringFinal;
                    cSplashWalletTextView.setText(stringFinalShekel);
                    newTag++;
                }
            });
        }catch(NullPointerException npe2){
            npe2.printStackTrace();
        }
    }

    public void goToMyWallet(View v){
        startActivity(new Intent(HomeActivity.this,SplasherWalletActivity.class));
    }

    public void callSplasher(View v){
        DialCall dialCall = new DialCall(HomeActivity.this);
        dialCall.fetchPhoneNumToDial(splasherPhoneNumber);
    }

    //Firebase Forced Update//
    private void updateHelperCall(){
        ForceUpdateChecker
                .with(HomeActivity.this)
                .onUpdateNeeded(HomeActivity.this)
                .check();
    }
    @Override
    public void onUpdateNeeded(final String updateUrl) {
        alertDialog
                .generalPurposeQuestionDialog(
                        HomeActivity.this
                        ,getResources().getString(R.string.carOwner_Act_java_forcedUpdateTitle)
                        ,getResources().getString(R.string.carOwner_Act_java_forcedUpdateMessage)
                        ,getResources().getString(R.string.carOwner_Act_java_forcedUpdateYes)
                        ,null
                        ,new DialogAcceptInterface() {
                            @Override
                            public void onAcceptOption() {
                                redirectStore(WebConstants.STORE_LINK);
                            }
                        });
    }
    private void redirectStore(String updateUrl) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)));
        finish();
        /*
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        */
    }
    //----------------------//
}
