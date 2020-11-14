package com.nomadapp.splash.ui.activity.splasherside;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.model.server.parseserver.RequestClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.RequestClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.nomadapp.splash.utils.sysmsgs.ConnectionLost;
import com.nomadapp.splash.model.objects.SplasherRequest;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

public class WashRequestsActivity extends AppCompatActivity {

    private RelativeLayout cLoadingPanel;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private String carOwnerUsername;
    private String carOwnerCarAddress;
    private String carOwnerCarAddressDesc;
    private String carOwnerCarUntilTime;
    private String carOwnerCarServiceType;
    private String carOwnerReqType;
    private String carOwnerPhoneNum;

    private String carOwnerCarBrand;
    private String carOwnerCarModel;
    private String carOwnerCarColor;
    private String carOwnerCarYear;
    private String carOwnerCarPlate;
    private String requestNumBadge;
    private String price;
    private Uri fbPic;
    private Uri noFbPic;

    private ArrayList<Double> requestLatitudes = new ArrayList<>();
    private ArrayList<Double> requestLongitudes = new ArrayList<>();
    private ArrayList<String> userNameList = new ArrayList<>();
    private ArrayList<String> carAddressList = new ArrayList<>();
    private ArrayList<String> carAddressDescList = new ArrayList<>();
    private ArrayList<String> carUntilTimeList = new ArrayList<>();
    private ArrayList<String> carServiceTypeList = new ArrayList<>();
    private ArrayList<String> carOwnerSetPrice = new ArrayList<>();
    private ArrayList<Integer> requestNumBadgeList = new ArrayList<>();
    private ArrayList<String> carOwnerPhoneNumList = new ArrayList<>();
    //----------------------------------------------------------------
    private ArrayList<String> carBrandList = new ArrayList<>();
    private ArrayList<String> carModelList = new ArrayList<>();
    private ArrayList<String> carColorList = new ArrayList<>();
    private ArrayList<String> carYearList = new ArrayList<>();
    private ArrayList<String> carPlateList = new ArrayList<>();
    //----------------------------------------------------------------

    //Sending after pictures status-----------------------------------
    private LinearLayout cPicturesAfterSentStatus;
    private TextView cPicSendingAfterAnim1, cPicSendingAfterAnim2, cPicSendingAfterAnim3
            , cPicSendingAfterAnim4;
    private ImageView cPicSentAfter1, cPicSentAfter2, cPicSentAfter3, cPicSentAfter4;
    private ProgressBar cAnimationSendingAfter;
    private boolean allMarksVisible = false;
    //----------------------------------------------------------------

    private ToastMessages toastMessages = new ToastMessages();
    private ConnectionLost clm = new ConnectionLost(WashRequestsActivity.this);
    private UserClassQuery userClassQuery = new UserClassQuery(WashRequestsActivity.this);
    private RequestClassQuery requestClassQuery = new RequestClassQuery
            (WashRequestsActivity.this);

    //TODO:1
    private ArrayList<SplasherRequest> requestList = new ArrayList<>();
    private RequestAdapter myRequestAdapter;
    private TextView cEmptyList;
    private boolean emptyListReady;
    private boolean runRequestUpdateChecker = false;

    public void loadRequests() {
        new CountDownTimer(3100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cLoadingPanel.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFinish() {
                cLoadingPanel.setVisibility(View.GONE);
                emptyListReady = true;
                if (requestList.size() == 0) {
                    cEmptyList.setVisibility(View.VISIBLE);
                }else{
                    cEmptyList.setVisibility(View.GONE);
                }
            }
        }.start();
    }

    public void updateListView(Location location) {
        if (location != null) {
            final LatLng splasherLocation = new LatLng(location.getLatitude(), location
                    .getLongitude());
            requestClassQuery.fetchCurrCloseRequestsForSplasher(userClassQuery.userName()
                    , splasherLocation, new RequestClassInterface.clearData() {
                        @Override
                        public void clearAllDataOnly() {
                            requestList.clear();
                            requestLatitudes.clear();
                            requestLongitudes.clear();
                            userNameList.clear();
                            carAddressList.clear();
                            carAddressDescList.clear();
                            carUntilTimeList.clear();
                            carServiceTypeList.clear();
                            carOwnerSetPrice.clear();
                            requestNumBadgeList.clear();
                            carOwnerPhoneNumList.clear();
                            //--------------------------//
                            carBrandList.clear();
                            carModelList.clear();
                            carColorList.clear();
                            carYearList.clear();
                            carPlateList.clear();
                            //--------------------------//
                        }
                    }, new RequestClassInterface.TakenRequest() {
                        @Override
                        public void fetchThisTakenRequest(ParseObject object) {
                            //Rating request////////////////////////////////////////////////////
                            /*
                                The demands on this query for showing me the requests, are:
                                -1. to be "near" the splasher's geoLocation.
                                -2. For the set splasher in the request
                                    to be "clear"(Meaning that request is indeed avaliable).
                                -3. to meet the below Rating Demands which are: for the splasher's
                                 actual com.kid.splash.utils.rating to be
                                equal to X requested com.kid.splash.utils.rating or higher.
                             */
                            //Rating request////////////////////////////////////////////////////
                            // show me such list :D
                            ParseGeoPoint requestLocation = (ParseGeoPoint)//1
                                    object.get("carCoordinates");

                            carOwnerUsername = object.getString("username");//2  //<--------
                            carOwnerCarAddress = object.getString("carAddress");//3
                            carOwnerCarAddressDesc = object.getString("carAddressDesc");//4
                            carOwnerCarUntilTime = object.getString("untilTime");//5
                            carOwnerCarServiceType = object.getString("serviceType");//6
                            price = object.getString("priceWanted");
                            if(object.getString("fbProfilePic") != null) {
                                fbPic = Uri.parse(object.getString("fbProfilePic"));
                            }
                            if(object.getString("ProfPicNoFb") != null){
                                noFbPic = Uri.parse(object.getString("ProfPicNoFb"));//FIX
                            }
                            requestNumBadge = object.getString("badgeWanted");
                            carOwnerPhoneNum = object.getString("carOwnerPhoneNum");
                            //--------------------------------------------------------
                            carOwnerCarBrand = object.getString("carBrand");//7
                            carOwnerCarModel = object.getString("carModel");//8
                            carOwnerCarColor = object.getString("carColor");//9
                            carOwnerCarYear = object.getString("carYear");//10
                            carOwnerCarPlate = object.getString("carplateNumber");//11
                            carOwnerReqType = object.getString("requestType");

                            Log.i("stuff", carOwnerCarAddress + " "
                                    + carOwnerCarAddressDesc + " " +
                                    carOwnerCarUntilTime + " " + carOwnerCarServiceType + " " +
                                    carOwnerCarBrand + " " + carOwnerCarModel + " "
                                    + carOwnerCarColor + " " +
                                    carOwnerCarYear + " " + carOwnerCarPlate + " " +
                                    carOwnerReqType);

                            ParseGeoPoint geoPointSelfLocation = new ParseGeoPoint
                                    (splasherLocation.latitude, splasherLocation.longitude);

                            Double distanceToRequestsInKm = geoPointSelfLocation
                                    .distanceInKilometersTo(requestLocation);

                            Double distanceInOneDP = (double) Math.round(distanceToRequestsInKm
                                    * 10) / 10;//<--------

                            String distanceString = distanceInOneDP.toString() + " Km";

                            //requests.add(carOwnerUsername + " " + distanceString +
                            //" " + carOwnerCarUntilTime);

                            //FILTER OF TIME (ZELDA?) STARTS HERE (i think)-------------------//
                            //Phone's Actual time and date--------------------------------//
                            Calendar listViewCalendar = Calendar.getInstance();
                            int hourSplasherSide = listViewCalendar.get(Calendar.HOUR_OF_DAY);
                            int minuteSplasherSide = listViewCalendar.get(Calendar.MINUTE);
                            int daySplasherSide = listViewCalendar.get(Calendar.DATE);

                            int monthSplasherSide = listViewCalendar.get(Calendar.MONTH);
                            if (monthSplasherSide == 12)
                                monthSplasherSide = 1;
                            else
                                monthSplasherSide = listViewCalendar.get(Calendar.MONTH) + 1;

                            int yearSplasherSide = listViewCalendar.get(Calendar.YEAR);
                            String newFullDateSS;
                            newFullDateSS = daySplasherSide + "-"
                                    + monthSplasherSide + "-" + yearSplasherSide;

                            int hourPlusOneSS;
                            if (hourSplasherSide == 24)
                                hourPlusOneSS = 1;
                            else
                                hourPlusOneSS = hourSplasherSide + 1;

                            @SuppressLint("DefaultLocale")
                            final String fullTotalDateSS = newFullDateSS + " " +
                                    String.format("%02d:%02d", hourPlusOneSS,
                                            minuteSplasherSide).toUpperCase(Locale
                                            .getDefault());
                            //------------------------------------------------------------//

                            //Selected Until Time Request---------------------------------//
                            String cutCarOwnerCarUntilTime;
                            if (carOwnerCarUntilTime.contains(" AM")) {
                                cutCarOwnerCarUntilTime = carOwnerCarUntilTime
                                        .replace(" AM", "");
                            } else {
                                cutCarOwnerCarUntilTime = carOwnerCarUntilTime
                                        .replace(" PM", "");
                            }
                            //------------------------------------------------------------//

                            //Current Request's 'Until Time' and phone's Date-------------//
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat sdf = new
                                    SimpleDateFormat("dd-MM-yyyy HH:mm");
                            Date currentSSDate1 = null;
                            Date savedSSDate2 = null;
                            try {
                                currentSSDate1 = sdf.parse(fullTotalDateSS);
                                savedSSDate2 = sdf.parse(cutCarOwnerCarUntilTime);
                            } catch (java.text.ParseException eDate) {
                                eDate.printStackTrace();
                            }
                            //------------------------------------------------------------//

                            if(currentSSDate1 != null) {
                                if (!(currentSSDate1.compareTo(savedSSDate2) > 0)) {
                                    //TODO:1.5
                                    SplasherRequest request = new SplasherRequest();
                                    request.setDistance(distanceString);
                                    request.setUntilTime(carOwnerCarUntilTime);
                                    request.setService(carOwnerCarServiceType);
                                    String ofTomorrow = getResources().
                                            getString(R.string
                                                    .carOWnerRequest_act_java_ofTomorrow);
                                    request.setTomorrow(ofTomorrow);
                                    request.setProfPicFbUri(fbPic);
                                    request.setProfPicUri(noFbPic);
                                    request.setBikeService(carOwnerCarServiceType);
                                    request.setRequestType(carOwnerReqType);

                                    int intNumBadgeNew = Integer.parseInt(requestNumBadge);
                                    request.setNumBadge(intNumBadgeNew);

                                    requestList.add(request);
                                    cEmptyList.setVisibility(View.GONE);

                                    requestLatitudes.add(requestLocation.getLatitude());
                                    requestLongitudes.add(requestLocation.getLongitude());

                                    userNameList.add(carOwnerUsername);
                                    carAddressList.add(carOwnerCarAddress);
                                    carAddressDescList.add(carOwnerCarAddressDesc);
                                    carUntilTimeList.add(carOwnerCarUntilTime);
                                    carServiceTypeList.add(carOwnerCarServiceType);
                                    carOwnerSetPrice.add(price);
                                    requestNumBadgeList.add(intNumBadgeNew);
                                    carOwnerPhoneNumList.add(carOwnerPhoneNum);
                                    //---------------------------------------------//
                                    carBrandList.add(carOwnerCarBrand);
                                    carModelList.add(carOwnerCarModel);
                                    carColorList.add(carOwnerCarColor);
                                    carYearList.add(carOwnerCarYear);
                                    carPlateList.add(carOwnerCarPlate);
                                }
                            }
                        }
                        @Override
                        public void afterUpdates() {
                            myRequestAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    //Navigate back to parent activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (allMarksVisible) {
                cPicturesAfterSentStatus.setVisibility(View.GONE);
            }
            startActivity(new Intent(WashRequestsActivity.this, HomeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    //-------------------------------

    public void goToDirectionMapWithAllData(int position) {
        if (requestLatitudes.size() > position && requestLongitudes.size() > position) {

            Intent intent = new Intent(WashRequestsActivity.this
                    , SplasherClientRouteActivity.class);

            intent.putExtra("requestLatitudes", requestLatitudes.get(position));
            intent.putExtra("requestLongitudes", requestLongitudes.get(position));
            intent.putExtra("carOwnerUsername", userNameList.get(position));
            intent.putExtra("carOwnerCarAddress", carAddressList.get(position));
            intent.putExtra("carOwnerCarAddressDesc", carAddressDescList.get(position));
            intent.putExtra("carOwnerCarUntilTime", carUntilTimeList.get(position));
            intent.putExtra("carOwnerCarServiceType", carServiceTypeList.get(position));
            intent.putExtra("setPrice", carOwnerSetPrice.get(position));
            intent.putExtra("carOwnerPhoneNum", carOwnerPhoneNumList.get(position));
            //----------------------------------------------------------------------
            intent.putExtra("carOwnerCarBrand", carBrandList.get(position));
            intent.putExtra("carOwnerCarModel", carModelList.get(position));
            intent.putExtra("carOwnerCarColor", carColorList.get(position));
            intent.putExtra("carOwnerCarYear", carYearList.get(position));
            intent.putExtra("carOwnerCarPlate", carPlateList.get(position));
            //intent.putExtra("splasherRating", wholeCurrRating);//<--HERE
            intent.putExtra("specData", "CORAKey");
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_requests);

        loadRequests();
        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------
        cLoadingPanel = findViewById(R.id.loadingPanel);
        ListView cCarRequestList = findViewById(R.id.carRequestsList);
        //--------------------------------
        cPicturesAfterSentStatus = findViewById(R.id.picturesAfterSentStatus);
        cPicSendingAfterAnim1 = findViewById(R.id.picSendingAfterAnim1);
        cPicSendingAfterAnim2 = findViewById(R.id.picSendingAfterAnim2);
        cPicSendingAfterAnim3 = findViewById(R.id.picSendingAfterAnim3);
        cPicSendingAfterAnim4 = findViewById(R.id.picSendingAfterAnim4);
        cPicSentAfter1 = findViewById(R.id.picSentAfter1);
        cPicSentAfter2 = findViewById(R.id.picSentAfter2);
        cPicSentAfter3 = findViewById(R.id.picSentAfter3);
        cPicSentAfter4 = findViewById(R.id.picSentAfter4);
        cAnimationSendingAfter = findViewById(R.id.animationSendingAfter);
        cEmptyList = findViewById(R.id.emptyList);
        //--------------------------------
        //--------------REQUESTS LIST---------------------------------------------------------------

        cPicturesAfterSentStatus.setVisibility(View.GONE);
        cEmptyList.setVisibility(View.GONE);

        //TODO:2
        myRequestAdapter = new RequestAdapter(WashRequestsActivity.this, R.layout
                .car_owner_row, requestList);
        requestList.clear();
        cCarRequestList.setAdapter(myRequestAdapter);
        //get splasher com.kid.splash.utils.rating numBadge from intent from carOwnerActivity
        WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile
                (WashRequestsActivity.this);
        String stringedSplasherRating = writeReadDataInFile
                .readFromFile("mySplasherRating");
        if (stringedSplasherRating.equals("")){
            stringedSplasherRating = "2";
        }
        final int splashRating = Integer.parseInt(stringedSplasherRating);
        cCarRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(splashRating >= requestNumBadgeList.get(position)) {
                    if (allMarksVisible) {
                        cPicturesAfterSentStatus.setVisibility(View.GONE);
                    }
                    goToDirectionMapWithAllData(position);
                }else{
                    toastMessages.productionMessage(getApplicationContext()
                            ,getResources().getString(R.string
                                    .carOWnerRequest_act_java_yourRatingIsntHigh)
                            ,1);
                }
            }
        });

        //------------------------------------------------------------------------------------------
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(emptyListReady) {
                    if (requestList.size() == 0) {
                        //solution here
                        cEmptyList.setVisibility(View.VISIBLE);
                    }else{
                        cEmptyList.setVisibility(View.GONE);
                    }
                }
                updateListView(location);
                if(runRequestUpdateChecker){
                    requestUpdateCheckerFunc();
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
        if (Build.VERSION.SDK_INT < 23) {
            //listening();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission
                            .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                    , 10000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                    , 10000, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation
                    (LocationManager.GPS_PROVIDER);
            Location lastKnownLocation2 = locationManager.getLastKnownLocation
                    (LocationManager.NETWORK_PROVIDER);

            if(lastKnownLocation == null){
                if(lastKnownLocation2 != null) {
                    updateListView(lastKnownLocation2);
                }
            } else {
                updateListView(lastKnownLocation);
            }
        } else {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission
                    .ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest
                        .permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //We have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                        , 10000, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                        , 10000, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation
                        (LocationManager.GPS_PROVIDER);
                Location lastKnownLocation2 = locationManager.getLastKnownLocation
                        (LocationManager.NETWORK_PROVIDER);

                if(lastKnownLocation == null){
                    if(lastKnownLocation2 != null) {
                        updateListView(lastKnownLocation2);
                    }
                } else {
                    updateListView(lastKnownLocation);
                }
            }
        }

        //Recieve Intent extras with a Bundle here
        //fetchedRating.getString("myRating")
        Bundle theExtraPicKey = getIntent().getExtras();
        if(theExtraPicKey != null) {
            String stringedPicKey = theExtraPicKey.getString("picKey");
            if (stringedPicKey != null && stringedPicKey.equals("key1")) {
                cPicturesAfterSentStatus.setVisibility(View.VISIBLE);
                runRequestUpdateChecker = true;
            }
        }

        clm.connectivityStatus(WashRequestsActivity.this);

    }

    public void checkForRecievedPictures(String keyTitle,
                                         final TextView bar1, final ImageView mark1){
        if (ParseUser.getCurrentUser().getUsername() != null) {
            ParseQuery<ParseObject> picQuery = ParseQuery.getQuery("Request");
            //"clear" only for TESTING!
            picQuery.whereEqualTo("splasherUsername", ParseUser.getCurrentUser().getUsername());
            picQuery.whereExists(keyTitle);
            picQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        bar1.setVisibility(View.INVISIBLE);
                        mark1.setVisibility(View.VISIBLE);
                    } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        Log.i("Image status", "nothing again");
                    }
                }
            });
        }
    }

    public void requestUpdateCheckerFunc() {
        if(cPicSentAfter4.getVisibility() == View.VISIBLE){
            cAnimationSendingAfter.setVisibility(View.INVISIBLE);
            allMarksVisible = true;
        }
        checkForRecievedPictures("afterFront", cPicSendingAfterAnim1, cPicSentAfter1);
        checkForRecievedPictures("afterRear", cPicSendingAfterAnim2, cPicSentAfter2);
        checkForRecievedPictures("afterLeft", cPicSendingAfterAnim3, cPicSentAfter3);
        checkForRecievedPictures("afterRight", cPicSendingAfterAnim4, cPicSentAfter4);
        Log.i("Where are we", " WE ARE IN AFTER AGAIN");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED){

                if(ContextCompat.checkSelfPermission(this, android.Manifest
                        .permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                            , 10000, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                            , 10000, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation
                            (LocationManager.GPS_PROVIDER);
                    Location lastKnownLocation2 = locationManager.getLastKnownLocation
                            (LocationManager.NETWORK_PROVIDER);

                    if (lastKnownLocation == null){
                        if(lastKnownLocation2 != null) {
                            updateListView(lastKnownLocation2);
                        }
                    } else {
                        updateListView(lastKnownLocation);
                    }
                } else {
                    toastMessages.productionMessage(getApplicationContext()
                            ,"problem with context compat or checking self permission"
                            ,1);
                    Log.e("RequestPermission Error", "problem with context compat or" +
                            " checking self permission");
                }
            } else {
                toastMessages.productionMessage(getApplicationContext()
                        ,"grant result error"
                        ,1);
                Log.e("grantResult error", "grant result error");
            }
        } else {
            toastMessages.productionMessage(getApplicationContext()
                    ,"request code error"
                    ,1);
            Log.e("request code error", "request code error");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(WashRequestsActivity.this,HomeActivity.class));
        }
        /*
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //Do nothing for now
        }
        */
        return false;
    }

    public class RequestAdapter extends ArrayAdapter<SplasherRequest> {

        Activity activity;
        int layoutResource;
        SplasherRequest request;
        ArrayList<SplasherRequest> mData;

        //Constructor
        private RequestAdapter(Activity act, int resource, ArrayList<SplasherRequest> data) {
            super(act, resource, data);

            activity = act;
            layoutResource = resource;
            mData = data;

            notifyDataSetChanged();

        }

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public SplasherRequest getRequest() {
            return request;
        }

        public void setRequest(SplasherRequest request) {
            this.request = request;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Nullable
        @Override
        public SplasherRequest getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getPosition(@Nullable SplasherRequest item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            GlideImagePlacement glideImagePlacement = new GlideImagePlacement
                    (WashRequestsActivity.this);

            if((row == null) || (row.getTag() == null)){
                LayoutInflater theInflator = LayoutInflater.from(activity);
                row = theInflator.inflate(layoutResource, null);
                holder = new ViewHolder();
                holder.mDistance = row.findViewById(R.id.rowDistanceItem);
                holder.mUntiltime = row.findViewById(R.id.rowAvailableUntilItem);
                holder.mService = row.findViewById(R.id.rowServiceItem);
                holder.mTomorrow = row.findViewById(R.id.inListOfTomorrow);
                holder.mProfPicUri = row.findViewById(R.id.rowThumbNail);
                holder.mBikeService = row.findViewById(R.id.bikeService);
                row.setTag(holder);
            }else{
                holder = (ViewHolder) row.getTag();
            }
            holder.holderRequest = getItem(position);

            if(holder.holderRequest != null)
                holder.mDistance.setText(holder.holderRequest.getDistance());//<<<<<<<<<<<<<<<<<<<<<
            else
                toastMessages.productionMessage(getApplicationContext()
                        ,"Error. Pleasse try again",1);

            //DELETE DATE AND ADD (of tomorrow) IF DATE REGISTERS FOR NEXT DAY
            //Phone's DATE for infoWindow Date removal---//
            String uncutFullUntilTime = holder.holderRequest.getUntilTime() + "  ";
            Calendar getViewCalendar = Calendar.getInstance();
            int daySplasherSideIW = getViewCalendar.get(Calendar.DATE);
            int daySplasherSideIW2 = getViewCalendar.get(Calendar.DATE) + 1;
            //bug: rank to low when its actually ok in request list
            int monthSplasherSideIW = getViewCalendar.get(Calendar.MONTH);
            if(monthSplasherSideIW == 12)
                monthSplasherSideIW = 1;
            else
                monthSplasherSideIW = getViewCalendar.get(Calendar.MONTH) + 1;

            int yearSplasherSideIW = getViewCalendar.get(Calendar.YEAR);
            String newFullDateIW = daySplasherSideIW + "-"
                    + monthSplasherSideIW
                    + "-" + yearSplasherSideIW;
            String newFullDateIW2 = daySplasherSideIW2 + "-"
                    + monthSplasherSideIW
                    + "-" + yearSplasherSideIW;
            Log.i("info dates", uncutFullUntilTime + "." + newFullDateIW);
            //18-1-2018 20:30 PM..
            //0123456789
            String newUntilTime = "";
            if (uncutFullUntilTime.contains(newFullDateIW)) {
                if (daySplasherSideIW < 10) {
                    if (monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(9, 17);
                        holder.mTomorrow.setVisibility(View.GONE);
                    } else {//>
                        newUntilTime = uncutFullUntilTime.substring(10, 18);
                        holder.mTomorrow.setVisibility(View.GONE);
                    }
                } else {//>
                    if (monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(10, 18);
                        holder.mTomorrow.setVisibility(View.GONE);
                    } else {//>
                        newUntilTime = uncutFullUntilTime.substring(11, 19);
                        holder.mTomorrow.setVisibility(View.GONE);
                    }
                }
            } else if (uncutFullUntilTime.contains(newFullDateIW2)) {
                if (daySplasherSideIW < 10) {
                    if (monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(9, 17);
                        //holder.mTomorrow.setText(holder.holderRequest.getTomorrow());
                    } else {//>
                        newUntilTime = uncutFullUntilTime.substring(10, 18);
                        //holder.mTomorrow.setText(holder.holderRequest.getTomorrow());
                    }
                } else {//>
                    if (monthSplasherSideIW < 10) {
                        newUntilTime = uncutFullUntilTime.substring(10, 18);
                        //holder.mTomorrow.setText(holder.holderRequest.getTomorrow());
                    } else {//>
                        newUntilTime = uncutFullUntilTime.substring(11, 19);
                        //holder.mTomorrow.setText(holder.holderRequest.getTomorrow());
                    }
                }
            }else{
                holder.mTomorrow.setVisibility(View.GONE);
            }
            //cAvailableUntilDetsValue.setText(newUntilTime);
            holder.mUntiltime.setText(newUntilTime);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

            holder.mService.setText(holder.holderRequest.getService());//<<<<<<<<<<<<<<<<<<<<<<<<<<<

            if(holder.holderRequest.getBikeService().equals("motorcycle") ||
                    holder.holderRequest.getBikeService().equals("אופנוע")){
                holder.mBikeService.setVisibility(View.VISIBLE);
                glideImagePlacement.squaredImagePlacementFromDrawable
                        (R.drawable.motorbikealone,holder.mBikeService);
            }else{
                holder.mBikeService.setVisibility(View.GONE);
            }
            //----Image Resource1(prof pic)----//
            String fbPicString = holder.holderRequest.getProfPicFbUri().toString();
            if (fbPicString.contains("https")) {
                //THIS IS PIC FROM FACEBOOK
                glideImagePlacement.roundImagePlacementFromUri
                        (holder.holderRequest.getProfPicFbUri(),holder.mProfPicUri);
            } else if(fbPicString.contains("none")) {
                //THIS IF PIC FROM CAR OWNER'S LOCAL STORAGE
                glideImagePlacement.roundImagePlacementFromDrawable
                        (R.drawable.theemptyface,holder.mProfPicUri);
            }else{
                glideImagePlacement.roundImagePlacementFromUri
                        (holder.holderRequest.getProfPicUri(),holder.mProfPicUri);
            }
            //---------------------------------//
            return row;
        }
    }

    class ViewHolder{
        SplasherRequest holderRequest;
        TextView mDistance;
        TextView mUntiltime;
        TextView mService;
        TextView mTomorrow;
        ImageView mProfPicUri;
        ImageView mBikeService;
        //int mId;
    }
}