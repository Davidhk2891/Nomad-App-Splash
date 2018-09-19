package com.nomadapp.splash.ui.activity.splasherside;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Vibrator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.mapops.MarkersOps;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.ProfileClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.phonedialer.DialCall;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nomadapp.splash.utils.sysmsgs.connectionlost.ConnectionLost;
import com.nomadapp.splash.model.mapops.ClientRouteDataParser;

//---- GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
// is ABSTRACT CODE-----//
public class SplasherClientRouteActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, com.google.android.gms
                .location.LocationListener {
//------------------------------------------------------------------------------------------------//

    //----------------------//
    private GoogleMap mMap;
    private Button cAcceptRequest;
    private AlertDialog.Builder acceptingRequestDialog;
    private AlertDialog.Builder COCanceledRequestDialog2;
    private Polyline firstBlackLine;
    private LinearLayout cGettingLocationLinear;
    private LinearLayout mRouteTypeLinear;
    private android.support.v7.widget.SwitchCompat mRouteTypeSwitch;
    private LatLng userLocationRoute, carOwnerLocationRoute;
    //----------------------//

    //Checkers-------------------------------------------//
    private boolean pressed = false;
    private boolean alreadyExecuted4 = false;
    private boolean alraedyExecuted5 = false;
    private boolean alreadyExecuted6 = false;
    private boolean alreadyExecuted8 = false;
    private boolean alreadyExecuted9 = false;
    private boolean alreadyExecuted10 = false;
    private boolean alreadyExecuted11 = false;
    private boolean foundTheCarCheck = false;
    private boolean VibTime = false;
    private boolean switchIsActive = false;
    //---------------------------------------------------//

    private LocationManager locationManager;
    private LocationListener locationListener;

    //---Car Owner and Request variables---//
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private double currentLatitude;
    private double currentLongitude;
    private Double carOwnerRequestLatitude;
    private Double carOwnerRequestLongitude;
    private String recievedCOUntilTime;
    private String recievedCOUPrice;
    private String recievedCOPhoneNum;

    private String url;

    private LinearLayout mCarInfoServiceCallLinear;
    private RelativeLayout mSpaMajorBtnBackspace;
    private TextView cCoCarAddressEdit;
    private TextView cCoCarAddressDescEdit;
    private TextView cCoCarUntilTimeEdit;
    private TextView cCoCarServiceTypeEdit;
    private TextView mCarClientText;
    private TextView mCarClientColorPlateText;
    private String recievedCOUsername;
    private String recievedCOServiceType;
    private LinearLayout mNavigateLinear;
    private ImageView mSplasher_stats_co_call;
    private ImageView mNavIcon;
    private String carColorPlate;
    private String carOwnerPhoneNumber;
    //---///////////////////////////////---//

    //object variables//
    private String recievedGoBackKey;
    private String splasherNumWashes = null;
    private String splasherNumRating = null;

    //----ABSTRACT CODE-----//
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private PolylineOptions finalLineOptions;
    //---------------------//

    //private Handler handler = new Handler();
    private Handler handler2 = new Handler();

    //Car Found variables
    private RelativeLayout cFoundTheCarRelative;
    private TextView cFoundCarTextKm;
    private TextView cFoundItText;
    private Animation shake;
    private Vibrator vib;

    private ToastMessages toastMessages = new ToastMessages();
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog
            (SplasherClientRouteActivity.this);
    private ConnectionLost clm = new ConnectionLost(SplasherClientRouteActivity.this);
    private MenuItem cancelItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_client_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        shake = AnimationUtils.loadAnimation(SplasherClientRouteActivity.this, R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle
                // connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        cAcceptRequest = findViewById(R.id.acceptRequest);
        cGettingLocationLinear = findViewById(R.id.gettinLocaionLinear);

        //CarOwner's Details 2
        mCarInfoServiceCallLinear = findViewById(R.id.carInfoServiceCallLinear);
        cCoCarAddressEdit = findViewById(R.id.coCarAddressEdit);
        cCoCarAddressDescEdit = findViewById(R.id.coCarAddressDescEdit);
        cCoCarUntilTimeEdit = findViewById(R.id.coCarUntilTimeEdit);
        cCoCarServiceTypeEdit = findViewById(R.id.coCarServiceTypeEdit);
        mCarClientText = findViewById(R.id.carClientText);
        mCarClientColorPlateText = findViewById(R.id.carClientColorPlateText);
        mSpaMajorBtnBackspace = findViewById(R.id.spaMajorBtnBackspace);
        mNavigateLinear = findViewById(R.id.navigateLinear);
        mSplasher_stats_co_call = findViewById(R.id.splasher_stats_co_call);
        mNavIcon = findViewById(R.id.navIcon);
        mRouteTypeLinear = findViewById(R.id.routeTypeLinear);
        mRouteTypeSwitch = findViewById(R.id.routeTypeSwitch);
        //-------------------

        //"Found the car" definitions
        cFoundTheCarRelative = findViewById(R.id.foundTheCar);
        cFoundCarTextKm = findViewById(R.id.foundTheCarText);
        cFoundItText = findViewById(R.id.foundItText);

        cAcceptRequest.setClickable(false);
        cAcceptRequest.setEnabled(false);
        mRouteTypeLinear.setVisibility(View.GONE);

        Intent intent = getIntent();
        recievedCOUsername = intent.getStringExtra("carOwnerUsername");
        String recievedCOAddress = intent.getStringExtra("carOwnerCarAddress");
        String recievedCOAddressDesc = intent.getStringExtra("carOwnerCarAddressDesc");
        recievedCOUntilTime = intent.getStringExtra("carOwnerCarUntilTime");
        recievedCOServiceType = intent.getStringExtra("carOwnerCarServiceType");
        recievedCOUPrice = intent.getStringExtra("setPrice");
        //---------------------------------------------------------------------------------------//
        String recievedCOCarBrand = intent.getStringExtra("carOwnerCarBrand");
        String recievedCOCarModel = intent.getStringExtra("carOwnerCarModel");
        String recievedCOCarColor = intent.getStringExtra("carOwnerCarColor");
        String recievedCOCarPlate = intent.getStringExtra("carOwnerCarPlate");
        recievedCOPhoneNum = intent.getStringExtra("carOwnerPhoneNum");
        recievedGoBackKey = intent.getStringExtra("specData");
        //---------------------------------------------------------------------------------------//

        //---...---//
        if (recievedCOUsername != null) {
            if (recievedCOServiceType.contains("External") && recievedCOServiceType
                    .contains("wash")) {
                recievedCOServiceType = "External wash";
            }
            String fixedUntilTime = "";
            if (recievedCOUntilTime.contains("PM")) {
                Log.i("recievedCOUUntilTimePM", recievedCOUntilTime);
                fixedUntilTime = recievedCOUntilTime.replace("PM", "");
            } else if (recievedCOUntilTime.contains("AM")) {
                Log.i("recievedCOUUntilTimeAM", recievedCOUntilTime);
                fixedUntilTime = recievedCOUntilTime.replace("AM", "");
            }
            if (recievedCOAddressDesc.equals("") || recievedCOAddressDesc.isEmpty()) {
                recievedCOAddressDesc = getResources().getString(R.string
                        .act_car_owner_notSpecified);
            }
            String carClient = recievedCOCarBrand + " " + recievedCOCarModel;
            carColorPlate = recievedCOCarPlate + " | " + recievedCOCarColor;

            cCoCarUntilTimeEdit.setText(fixedUntilTime);
            cCoCarServiceTypeEdit.setText(recievedCOServiceType);
            mCarClientText.setText(carClient);
            criticalFeaturesState(false, getResources().getString(R.string
                    .act_car_owner_location_availableIfReqTaken)
                    , Color.parseColor("#BDBDBD"));
            //recievedCOUsername
            cCoCarAddressEdit.setText(recievedCOAddress);
            cCoCarAddressDescEdit.setText(recievedCOAddressDesc);
        }
        //---...---//

        //Here, if distance to car is less than 0.2, boolean = true, and if true --> text click
        //enabled with Intent to camera activity
        cFoundItText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (foundTheCarCheck) {
                    //intent to cameraIntentActivity // CHECK LIVE!
                    ParseQuery<ParseObject> picsSendCheck = ParseQuery.getQuery("Request");
                    picsSendCheck.whereEqualTo("splasherUsername", ParseUser.getCurrentUser()
                            .getUsername());
                    picsSendCheck.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e == null) {
                                for (ParseObject object : objects) {
                                    object.put("picturesInbound", "true");
                                    object.saveInBackground();
                                    Intent intent =
                                            new Intent(SplasherClientRouteActivity.this,
                                        SplasherCameraActivity.class);
                                    intent.putExtra("fetchedUntilTime", recievedCOUntilTime);
                                    intent.putExtra("fetchedPrice", recievedCOUPrice);
                                    startActivity(intent);
                                }
                            }else{
                                /*
                                 * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                                 */
                                clm.connectionLostDialog();
                            }
                        }
                    });
                } else {
                    toastMessages.productionMessage(getApplicationContext()
                         ,getResources().getString(R.string
                                    .carOwnerLocation_act_java_youNeedToBe100m)
                         ,1);
                }//LEFT HERE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            }
        });

        //Here. Splasher's in, set "taken" to "yes"//
        //setTakenStatus("yes");
        //-----------------------------------------//

        //Run this if specific data transfered from HomeActivity(Splasher reintegrates to app)//
        if (intent.getStringExtra("appWasClosed") != null){
            if (intent.getStringExtra("appWasClosed").equals("yes")){
                alreadyExecuted11 = true;
            }
        }
        //------------------------------------------------------------------------------------//

        requestCanceledTakenUpdateChecker.run();

        fetchProfileDataForRequest();

        clm.connectivityStatus(SplasherClientRouteActivity.this);
        mRouteTypeSwitch.setChecked(false);
        switchRouteType();
        Log.i("routeModeCheck1", String.valueOf(mRouteTypeSwitch.isChecked()));
        Log.i("weareinWHAT", String.valueOf(switchIsActive));
    }

    //TODO:0 FUCKED UP BUG. YOU HAVE TO FIX THIS
    private void switchRouteType(){
        mRouteTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!switchIsActive){
                    //switch is on(car route)
                    if (finalLineOptions != null) {
                        String carMode = "";
                        url = getUrl(userLocationRoute, carOwnerLocationRoute, carMode);
                        FetchUrl fetchUrl = new FetchUrl();
                        fetchUrl.execute(url);
                        Log.i("weareinCAR",": " + url);
                        new CountDownTimer(3000,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                cGettingLocationLinear.setVisibility(View.VISIBLE);
                                mRouteTypeSwitch.setEnabled(false);
                                mRouteTypeSwitch.setClickable(false);
                            }

                            @Override
                            public void onFinish() {
                                mMap.clear();
                                alraedyExecuted5 = false;
                                markerOpsPlacement();
                                cGettingLocationLinear.setVisibility(View.GONE);
                                if (finalLineOptions != null) {
                                    mMap.addPolyline(finalLineOptions);
                                }
                                mRouteTypeSwitch.setEnabled(true);
                                mRouteTypeSwitch.setClickable(true);
                                switchIsActive = true;
                            }
                        }.start();
                    }
                }else{
                    //switch is off(bicycle route)
                    if (finalLineOptions != null) {
                        String walkBiciMode = "&mode=walking";
                        url = getUrl(userLocationRoute, carOwnerLocationRoute, walkBiciMode);
                        FetchUrl fetchUrl = new FetchUrl();
                        fetchUrl.execute(url);
                        Log.i("weareinWALK/BY",": " + url);
                        new CountDownTimer(3000,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                cGettingLocationLinear.setVisibility(View.VISIBLE);
                                mRouteTypeSwitch.setEnabled(false);
                                mRouteTypeSwitch.setClickable(false);
                            }

                            @Override
                            public void onFinish() {
                                mMap.clear();
                                alraedyExecuted5 = false;
                                markerOpsPlacement();
                                cGettingLocationLinear.setVisibility(View.GONE);
                                if (finalLineOptions != null) {
                                    mMap.addPolyline(finalLineOptions);
                                }
                                mRouteTypeSwitch.setEnabled(true);
                                mRouteTypeSwitch.setClickable(true);
                                switchIsActive = false;
                            }
                        }.start();
                    }
                }
            }
        });
    }

//    private void routeSwitchOps(final PolylineOptions lineOptions){
//        new CountDownTimer(2000,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                cGettingLocationLinear.setVisibility(View.VISIBLE);
//                mRouteTypeSwitch.setEnabled(false);
//                mRouteTypeSwitch.setClickable(false);
//            }
//
//            @Override
//            public void onFinish() {
//                cGettingLocationLinear.setVisibility(View.GONE);
//                mMap.addPolyline(lineOptions);
//                mRouteTypeSwitch.setEnabled(true);
//                mRouteTypeSwitch.setClickable(true);
//            }
//        }.start();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_tool_bar_menu, menu);
        cancelItem = menu.findItem(R.id.action_cancel_wash);
        if (pressed){
            cancelItem.setVisible(true);
        }else{
            cancelItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cancel_wash){
            String justCancelingMessage = getResources().getString(R.string
                    .carOwnerLocation_act_java_cancelingRequestsWill);
            cancelRequestToSetBackToClear(justCancelingMessage);
        }
        return super.onOptionsItemSelected(item);
    }

    //Runnable for request canceled or taken requests
    // TODO: SplasherClientRouteActivity RUNNABLE 1 - Check for Canceled request.
    public Runnable requestCanceledTakenUpdateChecker = new Runnable() {
        @Override
        public void run() {
            //if (cRequestDetailsInFull.getVisibility() == View.GONE) {
                Log.i("Handler2 Works?", "Handler2 Works");
                checkForUnacceptedCancelRequest();
            int delayTwo = 8000;
            handler2.postDelayed(this, delayTwo);
            //}
        }
    };

    //CANCEL REQUEST 3
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSpaMajorBtnBackspace.getVisibility() == View.GONE) {
                moveTaskToBack(true);
            } else {
                if(recievedGoBackKey.equals("CORAKey")) {
                    setTakenStatus("no");
                    startActivity(new Intent(SplasherClientRouteActivity.this
                            , HomeActivity.class));
                } else if (recievedGoBackKey.equals("COAKey")){
                    setTakenStatus("no");
                    startActivity(new Intent(SplasherClientRouteActivity.this
                            , HomeActivity.class));
                }
            }
        }
        //if (keyCode == KeyEvent.KEYCODE_HOME) {
            //Do nothing for now
        //}
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();

        handler2.removeCallbacks(requestCanceledTakenUpdateChecker);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");
        //Disconnect from API onPause()
        /*
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        */
    }

    public void callCarOwner(View v){
        //call car owner
        DialCall dialCall = new DialCall(SplasherClientRouteActivity.this);
        dialCall.fetchPhoneNumToDial(recievedCOPhoneNum);
    }

    public void currentLocationToRequest(Location location) {
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        currentLatitude = location.getLatitude();//for Google getDirections----------------------//
        currentLongitude = location.getLongitude();//for Google getDirections--------------------//
        userLocationRoute = new LatLng(currentLatitude, currentLongitude);
        Intent intent = getIntent();
        carOwnerRequestLatitude = intent.getDoubleExtra("requestLatitudes", 0);//-//
        carOwnerRequestLongitude = intent.getDoubleExtra("requestLongitudes", 0);///
        carOwnerLocationRoute = new LatLng(carOwnerRequestLatitude, carOwnerRequestLongitude);

        ArrayList<LatLng> latLngs = new ArrayList<>();

        latLngs.add(userLocationRoute);

        latLngs.add(carOwnerLocationRoute);

        markerOpsPlacement();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }

        LatLngBounds bounds = builder.build();

        int padding = 100;
        //---ABSTRACT CODE-------------------------------------------------------//
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        //TODO:1
        //routeType
        String walkBiciMode = "&mode=walking";

        // Getting URL to the Google Directions API
        url = getUrl(userLocationRoute, carOwnerLocationRoute, walkBiciMode);
        Log.i("routeMode",url);
        FetchUrl fetchUrl = new FetchUrl();
        switchIsActive = false;

        // Start downloading json data from Google Directions API
        fetchUrl.execute(url);
        //----------------------------------------------------------------------//

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height
                , padding);

        mMap.moveCamera(cameraUpdate);

        if(alreadyExecuted6) {
            firstBlackLine = mMap.addPolyline(new PolylineOptions()
                    .add(userLocationRoute, carOwnerLocationRoute)
                    .width(8)
                    .color(Color.BLACK));
        }
    }

    private void markerOpsPlacement(){
        if (recievedCOUsername != null) {
            MarkersOps markersOps = new MarkersOps(SplasherClientRouteActivity.this);
            if (recievedCOServiceType.equals("motorcycle") || recievedCOServiceType.equals("אופנוע")) {
                if (!alraedyExecuted5) {
                    markersOps.customMapSmallMarkerPlacer(
                            R.drawable.bigbikepinforrequest
                            , getResources().getString(R.string.carOwnerLocation_act_java_yourClient)
                            , mMap
                            , carOwnerLocationRoute);
                    alraedyExecuted5 = true;
                }
            } else {
                if (!alraedyExecuted5) {
                    markersOps.customMapSmallMarkerPlacer(
                            R.drawable.bigcarpinforrequest
                            , getResources().getString(R.string.carOwnerLocation_act_java_yourClient)
                            , mMap
                            , carOwnerLocationRoute);
                    alraedyExecuted5 = true;
                }
            }
        }
    }

    public void locationManagment(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                alreadyExecuted6 = true;

                Log.i("Called?", "Called");

                if(alreadyExecuted6) {
                    if (!alreadyExecuted10) {
                        cAcceptRequest.setClickable(true);
                        cAcceptRequest.setEnabled(true);
                        cGettingLocationLinear.setVisibility(View.GONE);
                        currentLocationToRequest(location);
                        alreadyExecuted10 = true;

                        if (alreadyExecuted11){
                            requestAcceptedFirstOperations();
                            alreadyExecuted11 = false;
                        }
                    }
                }

                //Sending location updates to server//
                ParseGeoPoint splasherGeoPoint = null;
                if (ParseUser.getCurrentUser() != null) {
                    splasherGeoPoint = new ParseGeoPoint(location.getLatitude(), location
                            .getLongitude());
                    ParseUser.getCurrentUser().put("location", splasherGeoPoint);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("Connected?", "Connected and sending location");
                            }
                        }
                    });
                }
                //----------------------------------//

                //set updater to check if distance to request car is less than 0.002 km
                Intent intent = getIntent();
                carOwnerRequestLatitude = intent.getDoubleExtra
                        ("requestLatitudes", 0);
                carOwnerRequestLongitude = intent.getDoubleExtra
                        ("requestLongitudes", 0);

                LatLng carOwnerLocation = new LatLng(carOwnerRequestLatitude
                        , carOwnerRequestLongitude);
                ParseGeoPoint carRequestLocationParsed = new ParseGeoPoint(carOwnerLocation
                        .latitude, carOwnerLocation.longitude);
                if (splasherGeoPoint != null) {
                    Double distanceInKm = splasherGeoPoint.distanceInKilometersTo
                            (carRequestLocationParsed);
                    Log.i("raw distance in Km", distanceInKm.toString());
                    Double distanceInKmOneDP = (double) Math.round(distanceInKm * 10) / 10;
                    String disInKmDPString = distanceInKmOneDP.toString();
                    String disInKmDPStringFull = disInKmDPString + " Km";
                    cFoundCarTextKm.setText(disInKmDPStringFull);
                    Log.i("km rounded and with 1DP", distanceInKmOneDP.toString());

                    if (distanceInKm < 0.1) { //100 meters to accurrate for testing.
                        if (pressed) {
                            foundTheCarCheck = true;
                            cFoundItText.setTextColor(Color.GREEN);
                            cFoundItText.startAnimation(shake);
                            if (!VibTime) {
                                toastMessages.productionMessage(getApplicationContext()
                                ,getResources().getString(R.string
                                                .carOwnerLocation_act_java_youAreWithinA100)
                                ,1);
                                vib.vibrate(500);
                                VibTime = true;
                                locationManager.removeUpdates(locationListener);
                            }
                        }
                    } else {
                        foundTheCarCheck = false;
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManagment();

        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest
                    .permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Consider calling:
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0
                    , 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0
                    , 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager
                    .GPS_PROVIDER);
            Location lastKnownLocation2 = locationManager.getLastKnownLocation(LocationManager
                    .NETWORK_PROVIDER);
            if (lastKnownLocation == null) {
                if (lastKnownLocation2 != null) {
                    currentLocationToRequest(lastKnownLocation2);
                }
            } else {
                if(lastKnownLocation != null){
                    currentLocationToRequest(lastKnownLocation);
                }
            }
        } else {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, 1);

            } else {

                //We have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0
                        , 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0
                        , 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation
                        (LocationManager.GPS_PROVIDER);
                Location lastKnownLocation2 = locationManager.getLastKnownLocation
                        (LocationManager.NETWORK_PROVIDER);

                if(lastKnownLocation == null){
                    if (lastKnownLocation2 != null) {
                        currentLocationToRequest(lastKnownLocation2);
                    }
                } else {
                    if(lastKnownLocation != null) {
                        currentLocationToRequest(lastKnownLocation);
                    }
                }
            }
        }
    }

    public void checkForUnacceptedCancelRequest(){

        /*
         PROBLEM:
         If both (2 or many) phones click on take the request at the same time, The first phone
         to reach the column in the request will be the one with "my Username. We good". However,
         for some reason, the other phones that got "someone else. NOT good", will override the
         "splasherName" column and place their value instead. Until the last phone in the bunch
         will override the column and his name will stay. But with the pop up message (since this
         one like the rest had the "someoneElse. NOT good" msg). The phone that got the "my username
         we good" will has this switched to "someoneelse not good" bc the "splasherUsername" column
         with his username will be overriden by the other splashers.

         POSSIBLE SOLUTIONS:
         1- LEAVE IT AS IS, AND MAKE SURE ANYONE THAT GOT THE POP UP MESSAGE , WHEN CLICK OK, SETS
         "splasherUsername" BACK TO CLEAR. THAT WAY EVERYONE GETS FUCKED. NO CAR WASH FOR NO-ONE. no moneys :(
         2- MAKE THE OPTION FOR AS SOON AS SOMEONE TAPS A REQUEST PIN OR IN THE LIST, THIS ONE BECOMES
         UNAVALIABLE TO THE REST OF SPLASHERS(you can do this with boolean (also for listRequest activity)).
         IF THE USER CLOSES THE REQUEST OR LEAVES IT OR LEAVES THE POP UP WINDOW, THE PIN/REQUEST
         GETS RELEASED.
         3- TALK TO ISAAC TO REMOVE THE LISTVIEW WITH REQUESTS.
         */

        Intent intent = getIntent();
        String userNameTemp = intent.getStringExtra("carOwnerUsername");
        ParseQuery<ParseObject> checkForNonExistentUsername = ParseQuery.getQuery("Request");
        checkForNonExistentUsername.whereEqualTo("username", userNameTemp);
        checkForNonExistentUsername.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            String splasherUser = ParseUser.getCurrentUser().getUsername();
                            Log.i("splasher", "is: " + splasherUser);
                            if (object.getString("splasherUsername").equals("clear") ||
                                    object.getString("splasherUsername").equals("canceled")) {
                                toastMessages.debugMesssage(getApplicationContext()
                                        , "clear. we good", 1);

                            } else if (object.getString("splasherUsername").equals(splasherUser)) {
                                toastMessages.debugMesssage(getApplicationContext()
                                        , "My username. we good", 1);
                                if (alreadyExecuted8) {
                                    if (!alreadyExecuted4) {
                                        cancelationThreeDialogTakenNo();
                                        alreadyExecuted4 = true;
                                    }
                                }
                                alreadyExecuted9 = true;
                                //RUN AND TEST

                            } else if (!object.getString("splasherUsername").equals(splasherUser)) {
                                toastMessages.debugMesssage(getApplicationContext(),
                                        "Someone else. NOT good", 1);
                                if (!alreadyExecuted9) {
                                    if (!alreadyExecuted4) {
                                        cancelationThreeDialogTakenYes();//IF THERE IS DISPUTE NO ONE GETS ANY $
                                        alreadyExecuted4 = true;
                                    }
                                    alreadyExecuted8 = true;

                                } else {
                                    if (!alreadyExecuted4) {
                                        cancelationThreeDialogTakenNo();
                                        alreadyExecuted4 = true;
                                    }
                                }
                            }
                        }
                    }else{
                        toastMessages.debugMesssage(getApplicationContext()
                                ,"request no longer extists. NOT good.",1);
                        if(!alreadyExecuted4){
                            cancelationTwoDialog();
                            alreadyExecuted4 = true;
                        }
                    }
                }
            }
        });
    }

    public void cancelationTwoDialog(){
        COCanceledRequestDialog2 = new AlertDialog.Builder(SplasherClientRouteActivity.this);
        COCanceledRequestDialog2.setTitle(getResources().getString(R.string
                .carOwnerLocation_act_java_carOwnerCanceled));
        COCanceledRequestDialog2.setIcon(android.R.drawable.ic_dialog_alert);
        COCanceledRequestDialog2.setMessage(getResources().getString(R.string
                .carOwnerLocation_act_java_thisCarWashRequestWasCanceled));
        COCanceledRequestDialog2.setPositiveButton(getResources().getString(R.string
                .carOwnerLocation_act_java_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseUser.getCurrentUser().put("location", "location");
                ParseUser.getCurrentUser().saveInBackground();
                startActivity(new Intent(SplasherClientRouteActivity.this
                        , HomeActivity.class));
            }
        });
        COCanceledRequestDialog2.setCancelable(false);
        COCanceledRequestDialog2.show();
    }

    public void cancelationThreeDialogTakenNo(){
        COCanceledRequestDialog2 = new AlertDialog.Builder(SplasherClientRouteActivity.this);
        COCanceledRequestDialog2.setTitle(getResources().getString(R.string
                .carOwnerLocation_act_java_carOwnerCanceled));
        COCanceledRequestDialog2.setIcon(android.R.drawable.ic_dialog_alert);
        COCanceledRequestDialog2.setMessage(getResources().getString(R.string
                .carOwnerLocation_act_java_thisCarWashRequestWasCanceled));
        alreadyExecuted8 = true;
        COCanceledRequestDialog2.setPositiveButton(getResources().getString(R.string
                .carOwnerLocation_act_java_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setTakenStatus("no"); //<-- DIFFERENCE BETWEEN THIS DIALOG AND cancelationTwoDialog
                ParseUser.getCurrentUser().put("location", "location");
                ParseUser.getCurrentUser().saveInBackground();
                startActivity(new Intent(SplasherClientRouteActivity.this
                        , HomeActivity.class));
            }
        });
        COCanceledRequestDialog2.setCancelable(false);
        COCanceledRequestDialog2.show();
    }

    public void cancelationThreeDialogTakenYes(){
        COCanceledRequestDialog2 = new AlertDialog.Builder(SplasherClientRouteActivity.this);
        COCanceledRequestDialog2.setTitle(getResources().getString(R.string
                .carOwnerLocation_act_java_carOwnerCanceled));
        COCanceledRequestDialog2.setIcon(android.R.drawable.ic_dialog_alert);
        COCanceledRequestDialog2.setMessage(getResources().getString(R.string
                .carOwnerLocation_act_java_thisCarWashRequestWasCanceled));
        alreadyExecuted8 = true;
        COCanceledRequestDialog2.setPositiveButton(getResources().getString(R.string
                .carOwnerLocation_act_java_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setTakenStatusNoSplasher(); //<-- DIFFERENCE BETWEEN THIS DIALOG AND
                // cancelationTwoDialog
                ParseUser.getCurrentUser().put("location", "location");
                ParseUser.getCurrentUser().saveInBackground();
                startActivity(new Intent(SplasherClientRouteActivity.this
                        , HomeActivity.class));
            }
        });
        COCanceledRequestDialog2.setCancelable(false);
        COCanceledRequestDialog2.show();
    }

    /*
    public void checkForCanceledRequest(){

        ParseQuery<ParseObject> queryCOCanceled = ParseQuery.getQuery("Request");

        queryCOCanceled.whereEqualTo("splasherUsername", ParseUser.getCurrentUser().getUsername());

        queryCOCanceled.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e == null && objects.size() == 0){//request with above requirements has to be non existent for down code to fire up
                    if(!alreadyExecuted3) {
                        COCanceledRequestDialog = new AlertDialog.Builder(SplasherClientRouteActivity.this);
                        COCanceledRequestDialog.setTitle("Car Owner Canceled");
                        COCanceledRequestDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        COCanceledRequestDialog.setMessage("Your Car Owner has either canceled his/her request or reached its time deadline, the Splash team encourages you to" +
                                " keep looking for another car wash request");
                        COCanceledRequestDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUser.getCurrentUser().put("location", "location");
                                ParseUser.getCurrentUser().saveInBackground();
                                startActivity(new Intent(SplasherClientRouteActivity.this, HomeActivity.class));
                            }
                        });
                        COCanceledRequestDialog.setCancelable(false);
                        COCanceledRequestDialog.show();
                        alreadyExecuted3 = true;
                    }
                }
            }
        });
    }
    */

    //TODO:2
    //-----ABSTRACT CODE TO DRAW PATH FROM LOCATION TO LOCATION-------------------------------------
    private String getUrl(LatLng origin, LatLng dest, String mode) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        //Mode
        //String mode = "mode=walking";
        //String mode2 = "mode=bicycling";
        //All put together with modes (Example):
        //http://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=
        //Montreal&sensor=false&mode=walking

        // Output format
        String output = "json";

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+mode;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection = null;
        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            /*
             * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
             */
            clm.connectionLostDialog();
            Log.d("Exception", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

            // Fetches data from url passed using ParseTask class
                                          //params, progress, result
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format. uses DataParse.java class
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0]);
                ClientRouteDataParser parser = new ClientRouteDataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                /*
                 * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                 */
                clm.connectionLostDialog();
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result){
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            try {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(9);
                    lineOptions.color(Color.parseColor("#2196F3"));

                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                }
            }catch(NullPointerException e){
                AlertDialog.Builder connectionLostDialog = new AlertDialog
                        .Builder(SplasherClientRouteActivity.this);
                connectionLostDialog.setTitle(getResources().getString(R.string
                        .carOwnerLocation_act_java_connectionLost));
                connectionLostDialog.setMessage(getResources().getString(R.string
                        .carOwnerLocation_act_java_connectionLostMakeSure));
                connectionLostDialog.setIcon(android.R.drawable.ic_dialog_alert);
                connectionLostDialog.setPositiveButton(getResources().getString(R.string
                        .carOwnerLocation_act_java_ok), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                connectionLostDialog.setCancelable(false);
                connectionLostDialog.show();
            }
            finalLineOptions = lineOptions;

//----------SPLASHER ACCEPTS THE REQUEST----------------------------------------------------------//
            cAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptingRequestDialog = new AlertDialog
                            .Builder(SplasherClientRouteActivity.this);
                    acceptingRequestDialog.setTitle(getResources()
                            .getString(R.string.carOwnerLocation_act_java_acceptingRequests));
                    acceptingRequestDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    acceptingRequestDialog.setMessage(getResources()
                            .getString(R.string.carOwnerLocation_act_java_youAreAgreeingToWash));
                    acceptingRequestDialog.setPositiveButton(getResources()
                            .getString(R.string.carOwnerLocation_act_java_yes)
                            , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop unaccpeted canceled or taken requests runnable:
                            //handler2.removeCallbacks(requestCanceledTakenUpdateChecker);
                            boxedLoadingDialog.showLoadingDialog();
                            requestAcceptedFirstOperations();
                        }
                    });
                    acceptingRequestDialog.setNegativeButton(getResources()
                            .getString(R.string.carOwnerLocation_act_java_no), null);
                    acceptingRequestDialog.show();
                }
            });
//------------------------------------------------------------------------------------------------//
        }
    }

    private void fetchProfileDataForRequest(){
        ProfileClassQuery profileClassQuery = new ProfileClassQuery
                (SplasherClientRouteActivity.this);
        profileClassQuery.getUserProfileToUpdate(new ProfileClassInterface() {
            @Override
            public void updateChanges(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject object : objects){
                            splasherNumRating = object.getString("oldAvgRating");
                            splasherNumWashes = object.getString("washes");
                        }
                    }
                }
            }
        });
    }

    public void requestAcceptedFirstOperations(){
        ParseQuery<ParseObject> directionQuery = ParseQuery.getQuery("Request");
        directionQuery.whereEqualTo("username", recievedCOUsername);
        directionQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for (ParseObject object : objects){
                            object.put("splasherUsername", ParseUser.getCurrentUser()
                                    .getUsername());
                            object.put("taken","yes");
                            ParseFile pf1 = ParseUser.getCurrentUser()
                                    .getParseFile("localProfilePic");
                            if(pf1 != null){
                                String spf1 = pf1.getUrl();
                                if (spf1 != null) {
                                    object.put("splasherProfilePic", spf1);
                                }
                            }
                            //need to fetch num of washes and rating somehow
                            UserClassQuery userClassQuery = new UserClassQuery
                                    (SplasherClientRouteActivity.this);
                            if (splasherNumWashes != null && splasherNumWashes != null) {
                                object.put("splasherWashesNum", splasherNumWashes);
                                object.put("splasherRatingNum", splasherNumRating);
                            }
                            object.put("splasherPhoneNum", userClassQuery.phone());
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        //check 'where are you'app for when is location upadted call
                                        pressed = true;
                                        cancelItem.setVisible(true);
                                        if (firstBlackLine.isVisible()) {
                                            firstBlackLine.remove();
                                        }
                                        if(alreadyExecuted6) {
                                            if (finalLineOptions != null) {
                                                mMap.addPolyline(finalLineOptions);
                                            }
                                        }
                                        if(ContextCompat.checkSelfPermission(
                                                SplasherClientRouteActivity. this
                                                , android.Manifest.permission.ACCESS_FINE_LOCATION)
                                                == PackageManager.PERMISSION_GRANTED) {
                                            locationManager.requestLocationUpdates(LocationManager
                                                    .GPS_PROVIDER, 0, 0
                                                    , locationListener);
                                            locationManager.requestLocationUpdates(LocationManager
                                                    .NETWORK_PROVIDER, 0, 0
                                                    , locationListener);
                                            Location lastKnownLocation = locationManager
                                                    .getLastKnownLocation(LocationManager
                                                            .GPS_PROVIDER);
                                            Location lastKnownLocation2 = locationManager
                                                    .getLastKnownLocation(LocationManager
                                                            .NETWORK_PROVIDER);
                                            if (lastKnownLocation == null) { //delete
                                                if (lastKnownLocation2 != null) {
                                                    ParseGeoPoint splasherGeoPoint =
                                                            new ParseGeoPoint(lastKnownLocation2
                                                                    .getLatitude(),
                                                                    lastKnownLocation2
                                                                    .getLongitude());
                                                    ParseUser.getCurrentUser().put("location"
                                                            , splasherGeoPoint);
                                                    ParseUser.getCurrentUser()
                                                            .saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if(e == null){
                                                                Log.i("splasherLocation"
                                                                        , "works");
                                                            }else{
                                                                clm.connectionLostDialog();
                                                            }
                                                        }
                                                    });
                                                }
                                            } else {
                                                ParseGeoPoint splasherGeoPoint
                                                        = new ParseGeoPoint(lastKnownLocation
                                                        .getLatitude(), lastKnownLocation
                                                        .getLongitude());
                                                ParseUser.getCurrentUser().put("location"
                                                        , splasherGeoPoint);
                                                ParseUser.getCurrentUser().saveInBackground
                                                        (new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e == null){
                                                            Log.i("splasherLocation"
                                                                    , "works");
                                                        }else{
                                                            clm.connectionLostDialog();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        boxedLoadingDialog.hideLoadingDialog();
                                        dashboardTransformOnPressed();
                                    }else{
                                        clm.connectionLostDialog();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void dashboardTransformOnPressed(){
        mRouteTypeLinear.setVisibility(View.VISIBLE);
        mSpaMajorBtnBackspace.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params= new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mCarInfoServiceCallLinear.setLayoutParams(params);
        criticalFeaturesState(true,carColorPlate,Color.parseColor("#03a9f4"));
    }

    private void criticalFeaturesState(boolean state,String colorPlateText,int color){
        mNavigateLinear.setClickable(state);
        mNavigateLinear.setEnabled(state);

        mNavIcon.setColorFilter(color);
        mSplasher_stats_co_call.setColorFilter(color);

        mSplasher_stats_co_call.setClickable(state);
        mSplasher_stats_co_call.setEnabled(state);

        mCarClientColorPlateText.setText(colorPlateText);
    }

    public void getDirectionsProcess(){

        //here Google Directions API happen
        //currentLatitude, currentLongitude
        //carOwnerRequestLatitude, carOwnerRequestLongitude

        Intent directionsIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="
                        + currentLatitude + "," + currentLongitude +
                        "&daddr=" + carOwnerRequestLatitude + "," +
                        carOwnerRequestLongitude));

        startActivity(directionsIntent);

    }

    public void getDirections(View view){

        Log.i("getDirections", "works");
        getDirectionsProcess();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        /*
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(6000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location locationz = LocationServices.FusedLocationApi.getLastLocation
            (mGoogleApiClient);
        }
        */
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this
                        , CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
                /*
                 * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                 */
                clm.connectionLostDialog();
            Log.e("Error", "Location services connection failed with code "
                    + connectionResult.getErrorCode());
        }
    }
    @Override
    public void onConnectionSuspended(int i){}
    @Override
    public void onLocationChanged(Location location){}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){}
    @Override
    public void onProviderEnabled(String provider){}
    @Override
    public void onProviderDisabled(String provider){}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission
                        .ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                            , 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                            , 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation
                            (LocationManager.GPS_PROVIDER);
                    Location lastKnownLocation2 = locationManager.getLastKnownLocation
                            (LocationManager.NETWORK_PROVIDER);
                    if(lastKnownLocation == null) {
                        if (lastKnownLocation2 != null) {
                            currentLocationToRequest(lastKnownLocation2);
                        }
                    } else {
                        if(lastKnownLocation != null){
                            currentLocationToRequest(lastKnownLocation);
                        }
                    }
                    toastMessages.debugMesssage(getApplicationContext()
                    ,getResources().getString(R.string.carOwnerLocation_act_java_connected)
                    ,1);

                } else {

                    toastMessages.productionMessage(getApplicationContext()
                    ,"Problem with permissions. Close app and try again",1);
                    Log.e("RequestPermission Error", "problem with context compat or" +
                            " checking self permission");

                }

            } else {

                toastMessages.productionMessage(getApplicationContext()
                ,"grant result error",1);
                Log.e("grantResult error", "grant result error");

            }

        } else {

            toastMessages.productionMessage(getApplicationContext()
                    ,"request code error. Close app and try again",1);
            Log.e("request code error", "request code error");

        }

    }
    //----------------------------------------------------------------------------------------------

    //CANCEL REQUEST 5
    public void cancelRequestToSetBackToClear(String message){

        AlertDialog.Builder cancelingRequestDialog = new AlertDialog
                .Builder(SplasherClientRouteActivity.this);
        cancelingRequestDialog.setTitle(getResources().getString
                (R.string.carOwnerLocation_act_java_areYouSure));
        cancelingRequestDialog.setIcon(android.R.drawable.ic_dialog_alert);
        cancelingRequestDialog.setMessage(message);
        cancelingRequestDialog.setPositiveButton(getResources()
                .getString(R.string.carOwnerLocation_act_java_yes), new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseQuery<ParseObject> directionQuery = ParseQuery.getQuery("Request");
                directionQuery.whereEqualTo("username", recievedCOUsername);
                directionQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e == null){
                            if(objects.size() > 0){
                                for (final ParseObject object : objects){
                                    object.put("splasherUsername", "canceled");
                                    object.put("taken", "no");
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(pressed) {
                                                ParseQuery<ParseObject> directionQuery = ParseQuery
                                                        .getQuery("Profile");
                                                directionQuery.whereEqualTo("username", ParseUser
                                                        .getCurrentUser().getUsername());
                                                directionQuery.findInBackground(
                                                        new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> objects
                                                            , ParseException e) {
                                                        if (e == null) {
                                                            if (objects.size() > 0) {
                                                                int totalCanceledWashes;
                                                                int newTotalCanceledWashes;
                                                                for (ParseObject object : objects) {
                                                                    totalCanceledWashes =
                                                                            Integer.parseInt(object
                                                                                    .getString
                                                                            ("washesCanceled"));
                                                                    newTotalCanceledWashes =
                                                                            totalCanceledWashes + 1;
                                                                    object.put("washesCanceled",
                                                                            newTotalCanceledWashes);
                                                                    object.saveInBackground(new
                                                                              SaveCallback() {
                                                                        @Override
                                                                        public void done
                                                                                (ParseException e) {
                                                                            ParseUser
                                                                                .getCurrentUser()
                                                                                .saveInBackground();
                                                                            Intent intent
                                                                            = new Intent
                                                         (SplasherClientRouteActivity
                                                                 .this, HomeActivity.class);
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                    }
                                                });
                                            }else{
                                                ParseUser.getCurrentUser().saveInBackground();
                                                Intent intent = new Intent
                                                        (SplasherClientRouteActivity
                                                                .this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        }else{
                            /*
                             * Connection Lost Message<%%%%%%
                             */
                            clm.connectionLostDialog();
                        }
                    }
                });
            }
        });
        cancelingRequestDialog.setNegativeButton(getResources().getString(R.string
                .carOwnerLocation_act_java_ok), null);
        cancelingRequestDialog.setCancelable(false);
        cancelingRequestDialog.show();
    }

    public void setTakenStatus(final String status){
        ParseQuery<ParseObject> takenTempCheck = ParseQuery.getQuery("Request");
        takenTempCheck.whereEqualTo("username", recievedCOUsername);
        takenTempCheck.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0){
                    for (ParseObject object : objects){
                        object.put("taken", status);
                        object.put("splasherUsername", "clear");
                        object.saveInBackground();
                    }
                }
            }
        });
    }

    public void setTakenStatusNoSplasher(){
        ParseQuery<ParseObject> takenTempCheck = ParseQuery.getQuery("Request");
        takenTempCheck.whereEqualTo("username", recievedCOUsername);
        takenTempCheck.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0){
                    for (ParseObject object : objects){
                        object.put("taken", "yes");
                        object.saveInBackground();
                    }
                }
            }
        });
    }
}
