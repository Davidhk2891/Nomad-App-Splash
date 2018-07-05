package com.nomadapp.splash.ui.activity.carownerside;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import com.nomadapp.splash.model.constants.PaymeConstants;
import com.nomadapp.splash.ui.activity.carownerside.payment.PaymentSettingsActivity;
import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.ui.fragment.SplasherListFragment;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.nomadapp.splash.model.localdatastorage.CarLocalDatabaseHandler;
import com.nomadapp.splash.utils.sysmsgs.connectionlost.ConnectionLost;
import com.nomadapp.splash.model.objects.MyCar;
import com.nomadapp.splash.model.imagehandler.GlideApp;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WashReqParamsActivity extends AppCompatActivity implements
        SplasherListFragment.OnFragmentInteractionListener{

    private static final int REQUEST_CC_FROM_CCDETS = 50;

    //Location and Time
    private EditText cCrLocationDescriptionEdit;
    private TextView cCrLocationEdit, cCrUntilEdit;
    private Spinner cCrServicesEdit;
    private ArrayAdapter<String> spinnerAdapter;
    private TimePicker cTimePick;
    private Button cSetTimeNow, cCancelTimeNow;
    private boolean todaySwitchedToTomorrow = false;
    private TextView cOfTomorrow;
    private Switch cUntilTimeSwitchTodayTomorrow;

    //CarOwner's Car
    private TextView cCarSelected;
    private Button cAddcar;
    private ListView cCarList;
    private TextView mEmptyCarList;
    private ArrayList<MyCar> dbCars = new ArrayList<>();
    private CarLocalDatabaseHandler dbh;
    private AlertDialog.Builder deleteCarDialog;
    private ImageView cSpinnerArrow;
    private boolean carListVisible = false;
    private boolean justAddedCar = false;

    //loadRequest()
    private boolean locationChecked = false;
    private boolean carChecked = false;
    private boolean timeChecked = false;
    private Button cSaveRequest;
    private TextView cRequestSteps;

    //primitive variables
    private String address;
    private LatLng carCoordinates;
    private String carAddressDescription;
    private String selectedTime;
    private String getServiceType;
    private String carBrandToUpload;
    private String carModelToUpload;
    private String carColorToUpload;
    private String carPlateToUpload;
    private String profPicNoFbString;
    private String stringPriceSet;
    private String dollarSetPrice;
    private String fullDate;

    //Rating and pricing
    private RelativeLayout cRatingAndPricingRelative;
    private ImageView cTheRating;
    private SeekBar cThePrice;
    private TextView cSplasherPriceSet;
    private TextView cPayMethodTextView;
    private double readyPrice;
    private double dpValue;
    private int numericalBadge;
    private TextView mSplasherPriceSetComingSoon;
    private boolean temporalKeyActive = false;
    private WriteReadDataInFile writeReadDataInFile =
            new WriteReadDataInFile(WashReqParamsActivity.this);
    private Button cFinallyOrder;
    private String[] items;

    //primitive variables
    private int PLACE_PICKER_REQUEST = 1;
    private Calendar requestDateTime = Calendar.getInstance();
    private int dateOfRequest = requestDateTime.get(Calendar.DATE);
    private int monthOfRequest = requestDateTime.get(Calendar.MONTH);
    private int yearOfRequest = requestDateTime.get(Calendar.YEAR);
    private boolean carCoordinatesIn = false;
    private boolean untilTimeWithinRules = true;
    private boolean timePickerMoved = false;

    //others
    private RelativeLayout cFirstRelative;
    private RelativeLayout cElPropioRelative;

    private ToastMessages toastMessages = new ToastMessages();
    private ConnectionLost clm = new ConnectionLost(WashReqParamsActivity.this);
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(WashReqParamsActivity.this);

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        cCarList = findViewById(R.id.carList);
        mEmptyCarList = findViewById(R.id.emptyCarList);
        CarAdapter myCarAdapter = new CarAdapter(WashReqParamsActivity.this, R.layout.car_row
                , dbCars);
        cCarList.setAdapter(myCarAdapter);
        setEmptyListText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(cRatingAndPricingRelative.getVisibility() == View.VISIBLE){
                cFirstRelative.setClickable(true);
                cElPropioRelative.setClickable(true);
                cCrLocationDescriptionEdit.setVisibility(View.VISIBLE);
                cCrLocationEdit.setVisibility(View.VISIBLE);
                cCrUntilEdit.setVisibility(View.VISIBLE);
                cCarSelected.setVisibility(View.VISIBLE);
                //cCrServicesEdit.setVisibility(View.VISIBLE);
                cCrServicesEdit.setVisibility(View.GONE);
                cCarList.setVisibility(View.GONE);
                cSpinnerArrow.setVisibility(View.VISIBLE);
                cSaveRequest.setVisibility(View.VISIBLE);
                cRatingAndPricingRelative.setVisibility(View.GONE);
                cRatingAndPricingRelative.animate().translationXBy(1000f).setDuration(500);
            }else{
                startActivity(new Intent(WashReqParamsActivity.this, HomeActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_req_params);
        mEmptyCarList = findViewById(R.id.emptyCarList);
        cCarList = findViewById(R.id.carList);
        cCarList.setVisibility(View.GONE);
        mEmptyCarList.setVisibility(View.GONE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Location and Time
        cCrLocationEdit = findViewById(R.id.crLocationEdit);
        cCrLocationDescriptionEdit = findViewById(R.id.crlocationDescriptionEdit);
        cCrUntilEdit = findViewById(R.id.crUntilEdit);
        cCrServicesEdit = findViewById(R.id.crServicesEdit);
        cTimePick = findViewById(R.id.timePick);
        cSetTimeNow = findViewById(R.id.setTimeNow);
        cCancelTimeNow = findViewById(R.id.cancelTimeNow);
        LinearLayout cSetTimeCancelTTLinear = findViewById(R.id.setTimeCancelTTLinear);
        cUntilTimeSwitchTodayTomorrow = findViewById(R.id.untilTimeSwitchTodayTomorrow);
        cOfTomorrow = findViewById(R.id.ofTomorrow);
        cTimePick.setIs24HourView(false);
        cTimePick.bringToFront();
        cSetTimeCancelTTLinear.bringToFront();

        //CarOwner's car
        dbh = new CarLocalDatabaseHandler(WashReqParamsActivity. this);
        cCarSelected = findViewById(R.id.carSelected);
        cAddcar = findViewById(R.id.addCar);
        cSpinnerArrow = findViewById(R.id.spinnerArrow);
        cCarSelected.setHintTextColor(Color.parseColor("#BDBDBD"));

        //Rest
        cSaveRequest = findViewById(R.id.saveCarOwner);
        cFirstRelative = findViewById(R.id.firstRelative);
        cElPropioRelative = findViewById(R.id.elPropioRelative);
        cRequestSteps = findViewById(R.id.requestSteps);

        //car Owner car address Details hide keyboard onPress outside
        cFirstRelative.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                hideKeyboardTwo();clockState(View.GONE, View.VISIBLE, true);}});
        cCrLocationEdit.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                hideKeyboardTwo();clockState(View.GONE, View.VISIBLE, true);}});
        cCrUntilEdit.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                hideKeyboardTwo();clockState(View.GONE, View.VISIBLE, true);}});
        cCarSelected.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                hideKeyboardTwo();clockState(View.GONE, View.VISIBLE, true);}});
        cElPropioRelative.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                hideKeyboardTwo();clockState(View.GONE, View.VISIBLE, true);}});

        //com.kid.splash.utils.rating and pricing 2
        cRatingAndPricingRelative = findViewById(R.id.ratingAndPriceRelative);
        cTheRating = findViewById(R.id.theRating);
        cThePrice = findViewById(R.id.thePrice);
        cSplasherPriceSet = findViewById(R.id.splasherPriceSet);
        cPayMethodTextView = findViewById(R.id.payMethodTextView);
        mSplasherPriceSetComingSoon = findViewById(R.id.splasherPriceSetComingSoon);
        cFinallyOrder = findViewById(R.id.finallyOrder);
        cRatingAndPricingRelative.setVisibility(View.GONE);
        cRatingAndPricingRelative.setTranslationX(1000f);
        mSplasherPriceSetComingSoon.setVisibility(View.GONE);

        //Payments
        //TODO: Handling 'know-how' for app to recognize where data comes from 1
        Log.i("Hi", "Checking");//startActivityForResult() and OnActivityResult()

        address = String.valueOf(getResources()
                .getString(R.string.washMyCar_act_java_inputLocation));
        carAddressDescription = "";
        selectedTime = String.valueOf(getResources()
                .getString(R.string.washMyCar_act_java_selectTime));

        cAddcar.setVisibility(View.GONE);
        cTimePick.setVisibility(View.GONE);
        cSetTimeNow.setVisibility(View.GONE);
        cCancelTimeNow.setVisibility(View.GONE);

        if (cCarList.getVisibility() == View.VISIBLE) {
            // Its visible
            Log.i("visibility state", "VISIBLE");
        } else {
            // Either gone or invisible
            Log.i("visibility state", "GONE");
        }

        //----------------------------------------------------------------------------------------//

        //Go to Rating and Price setting
        cSaveRequest.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //UNTIL TIME: HANDLER FOR TAPPING cSaveRequest WHEN "UNTIL-TIME" WAS NOT TOUCHED--//
                //selectedTime = cCrUntilEdit.getText().toString(); //<--FINAL
                //fullDate + " " + selectedTime
                //Phone's Actual time and date------//
                Calendar saveRequestCalendar = Calendar.getInstance();
                int currentSoleHour = saveRequestCalendar.get(Calendar.HOUR_OF_DAY);
                Log.i("normalPrePoint", String.valueOf(currentSoleHour));
                int currentSoleMinute = saveRequestCalendar.get(Calendar.MINUTE);
                int currentDate = saveRequestCalendar.get(Calendar.DATE);
                //saveRequestCalendar.add(saveRequestCalendar.get(Calendar.DATE), 1);
                int currentDate2 = saveRequestCalendar.get(Calendar.DATE) + 1;

                int currentMonth = saveRequestCalendar.get(Calendar.MONTH);
                if (currentMonth == 12)
                    currentMonth = 1;
                else
                    currentMonth = saveRequestCalendar.get(Calendar.MONTH) + 1;

                int currentYear = saveRequestCalendar.get(Calendar.YEAR);
                String newFullDate;
                newFullDate = String.valueOf(currentDate) + "-" + String.valueOf(currentMonth)
                            + "-" + String.valueOf(currentYear);
                int currentSoleHourPlusOne;
                if(currentSoleHour == 24) {
                    currentSoleHourPlusOne = 1;
                    Log.i("24point", "24point");
                }else {
                    currentSoleHourPlusOne = saveRequestCalendar.get(Calendar.HOUR_OF_DAY) + 1;
                    Log.i("normalpoint", "normalpoint");
                    Log.i("normalPoint", String.valueOf(saveRequestCalendar
                            .get(Calendar.HOUR_OF_DAY)));
                }
                //---------------------------------//

                //FULL USER SELECTED UNTIL TIME-----------------------------------//
                String newFullDateForST;
                if(todaySwitchedToTomorrow || (writeReadDataInFile
                        .readFromFile("todayTomorrow") != null &&
                    writeReadDataInFile.readFromFile("todayTomorrow").equals("true"))) {
                    newFullDateForST = String.valueOf(currentDate2) + "-"
                            + String.valueOf(currentMonth)
                            + "-" + String.valueOf(currentYear);
                }else{
                    newFullDateForST = String.valueOf(currentDate) + "-"
                            + String.valueOf(currentMonth)
                            + "-" + String.valueOf(currentYear);
                }
                String fullTotalSelectedDate = newFullDateForST + " " + selectedTime;
                final String cutFullTotalSelectedDate;
                if (fullTotalSelectedDate.contains(" AM")){
                    cutFullTotalSelectedDate = fullTotalSelectedDate.replace(" AM"
                            , "");
                } else {
                    cutFullTotalSelectedDate = fullTotalSelectedDate.replace(" PM"
                            , "");
                }
                //----------------------------------------------------------------//

                //FULL USER'S DEVICE CURRENT DATE + HOUR + MINUTE-----------------//
                @SuppressLint("DefaultLocale")
                final String cutFullTotalCurrentDateTime = newFullDate + " " +
                        String.format("%02d:%02d", currentSoleHourPlusOne,
                                currentSoleMinute).toUpperCase(Locale.getDefault());
                //----------------------------------------------------------------//
                //Logs and to-Date convertion-------------------------------------//
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date currentDate1 = null;
                Date savedDate2 = null;
                try {
                    currentDate1 = sdf.parse(cutFullTotalCurrentDateTime);
                    savedDate2 = sdf.parse(cutFullTotalSelectedDate);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                final Date currentFinalDate1 = currentDate1;
                final Date savedFinalDate2 = savedDate2;
                Log.i("datesWash", "is: " + currentDate1 + " and " + savedDate2);
                //----------------------------------------------------------------//
                //--------------------------------------------------------------------------------//
                carAddressDescription = cCrLocationDescriptionEdit.getText().toString();
                toastMessages.debugMesssage(getApplicationContext()
                        ,"is: " + cCarSelected.getText().toString(),1);
                try {
                    if (cCrLocationEdit.getText().toString().isEmpty()
                            || cCrUntilEdit.getText().toString().isEmpty() ||
                            cCarSelected.getText().toString().isEmpty() ||
                            !carCoordinatesIn) {

                        warningDialog(getResources()
                                .getString(R.string.washMyCar_act_java_missingFields)
                                , getResources().getString(R.string
                                        .washMyCar_act_java_pleaseFillAll));

                    }else if(cCarSelected.getText().toString().equals(getResources()
                            .getString(R.string.act_wash_my_car_chooseACar))){

                        warningDialog(getResources()
                                .getString(R.string.washMyCar_act_java_missingFields)
                                , getResources().getString(R.string
                                        .washMyCar_act_java_pleaseFillAll));

                    } else if (cutFullTotalCurrentDateTime.equals(cutFullTotalSelectedDate)) {

                        warningDialog(getResources().getString
                                (R.string.washMyCar_act_java_washTimeLimit)
                                , getResources().getString(R.string
                                        .washMyCar_act_java_yourWashLimitShort));

                    } else if (currentFinalDate1 != null && savedFinalDate2 != null) {

                        Log.i("currentDate", currentFinalDate1.toString());
                        Log.i("savedDate", savedFinalDate2.toString());

                        if (currentFinalDate1.compareTo(savedFinalDate2
                        ) > 0 && !todaySwitchedToTomorrow) {

                            Log.i("datesAgain", "are: " + currentFinalDate1
                                    + " " + savedFinalDate2);

                            warningDialog(getResources().getString(R.string
                                    .washMyCar_act_java_washTimeLimit), getResources()
                                    .getString(R.string.washMyCar_act_java_yourWashTimeLimit));

                        } else {
                            cRatingAndPricingRelative.setVisibility(View.VISIBLE);
                            cCrServicesEdit.setVisibility(View.VISIBLE);
                            cRatingAndPricingRelative.animate().translationXBy(-1000f)
                                    .setDuration(500);
                            cSaveRequest.setVisibility(View.GONE);
                            new CountDownTimer(800, 800) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    cFirstRelative.setClickable(false);
                                    cElPropioRelative.setClickable(false);
                                    cCrLocationDescriptionEdit.setVisibility(View.GONE);
                                    cCrLocationEdit.setVisibility(View.GONE);
                                    cCrUntilEdit.setVisibility(View.GONE);
                                    cCarSelected.setVisibility(View.GONE);
                                    //cCrServicesEdit.setVisibility(View.GONE);
                                    cCarList.setVisibility(View.GONE);
                                    cSpinnerArrow.setVisibility(View.GONE);
                                    //now set up all the views from the com.kid.splash.utils
                                    // .rating and payment. and set up the controller for it
                                }
                            }.start();
                        }
                    }
                }catch(NullPointerException n1){
                    n1.printStackTrace();
                    warningDialog(getResources().getString(R.string.washMyCar_act_java_missingFields)
                            , getResources().getString(R.string.washMyCar_act_java_pleaseFillAll));
                }
            }

        });

        //Load Request to Parse Server 1
        cFinallyOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cPayMethodTextView.getText().toString().isEmpty() ||
                        cPayMethodTextView .getText().toString().equals(getResources()
                                .getString(R.string.act_wash_my_car_paymentMethod)) ||
                        ((writeReadDataInFile.readFromFile("buyer_key_temporal")
                                .isEmpty()) && writeReadDataInFile
                                .readFromFile("buyer_key_permanent").isEmpty())) {
                    toastMessages.productionMessage(getApplicationContext()
                    ,getResources().getString(R.string.act_wash_my_car_pleaseChoosePaymentM)
                    ,1);
                }else{
                    boxedLoadingDialog.showLoadingDialog();
                    loadRequest();
                }
            }
        });

        //"English"
        //"עברית"
        //cSplasherPriceSet.setText("10.0₪");
        //TODO:Rating 3
        //Rating and Pricing 3
        fetchDataFromFile();
//        switch (Locale.getDefault().getDisplayLanguage()) {
//            case "English": {
//                stringPriceSet = "10.0";
//                String defaultPrice = "10.0 ₪";
//                cThePrice.setMax(600);
//                if (!(writeReadDataInFile.readFromFile("languageSateSafety").isEmpty())) {
//                    if (writeReadDataInFile.readFromFile("languageSateSafety").equals("English")) {
//                        fetchDataFromFile();
//                    } else {
//                        defaultBadgePrice(defaultPrice);
//                    }
//                } else {
//                    defaultBadgePrice(defaultPrice);
//                }
//                cThePrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        //double
//                        dpValue = ((double) progress / 10.0);
//                        //double
//                        //CHANGE VALUES TO THEIR RESPECTIVE SHEKEL COUNTERPARTS
//                        readyPrice = dpValue + 10.0;
//                        if (readyPrice >= 10.0 && readyPrice <= 18.1) {
//                            showBadge(R.drawable.bronzebadge);
//                            numericalBadge = 1; //BAD
//                        } else if (readyPrice >= 18.2 && readyPrice <= 29.1) {
//                            showBadge(R.drawable.silverbadge);
//                            numericalBadge = 2; //REGULAR
//                        } else if (readyPrice >= 29.2 && readyPrice <= 57.2) {
//                            showBadge(R.drawable.goldbadge);
//                            numericalBadge = 3; //GOOD
//                        } else if (readyPrice >= 57.3 && readyPrice <= 70.0) {
//                            showBadge(R.drawable.platbadge);
//                            numericalBadge = 4; //EXCELLENT
//                        }
//                        if (cThePrice.getProgress() == 1) {
//                            stringPriceSet = String.valueOf(10.0);
//                        } else if (cThePrice.getProgress() == 600) {
//                            stringPriceSet = String.valueOf(70.0);
//                        } else {
//                            stringPriceSet = String.valueOf(readyPrice);
//                        }
//                        String shekels = "₪";
//                        String finalPrice2 = stringPriceSet;
//                        String finalPrice3 = finalPrice2 + " " + shekels;
//                        cSplasherPriceSet.setText(finalPrice3);
//                    }
//
//                    //FIX SAVING RATING DATA TO FILE PROPERLY, FETCHING IT
//                    //AND SENDING THE PROPER PRICE DATA TO SERVER
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                    }
//                });
//
//                break;
//            }
//            case "עברית": {
//                stringPriceSet = "10.0";
//                String defaultPrice = "10.0 ₪";
//                cThePrice.setMax(600);
//                if (!(writeReadDataInFile.readFromFile("languageSateSafety").isEmpty())) {
//                    if (writeReadDataInFile.readFromFile("languageSateSafety").equals("עברית")) {
//                        fetchDataFromFile();
//                    } else {
//                        defaultBadgePrice(defaultPrice);
//                    }
//                } else {
//                    defaultBadgePrice(defaultPrice);
//                }
//                cThePrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        //double
//                        dpValue = ((double) progress / 10.0);
//                        //double
//                        //CHANGE VALUES TO THEIR RESPECTIVE SHEKEL COUNTERPARTS
//                        readyPrice = dpValue + 10.0;
//                        if (readyPrice >= 10.0 && readyPrice <= 18.1) {
//                            showBadge(R.drawable.bronzebadge);
//                            numericalBadge = 1; //BAD
//                        } else if (readyPrice >= 18.2 && readyPrice <= 29.1) {
//                            showBadge(R.drawable.silverbadge);
//                            numericalBadge = 2; //REGULAR
//                        } else if (readyPrice >= 29.2 && readyPrice <= 57.2) {
//                            showBadge(R.drawable.goldbadge);
//                            numericalBadge = 3; //GOOD
//                        } else if (readyPrice >= 57.3 && readyPrice <= 70.0) {
//                            showBadge(R.drawable.platbadge);
//                            numericalBadge = 4; //EXCELLENT
//                        }
//                        if (cThePrice.getProgress() == 1) {
//                            stringPriceSet = String.valueOf(10.0);
//                        } else if (cThePrice.getProgress() == 600) {
//                            stringPriceSet = String.valueOf(70.0);
//                        } else {
//                            stringPriceSet = String.valueOf(readyPrice);
//                        }
//                        String shekels = "₪";
//                        String finalPrice2 = stringPriceSet;
//                        String finalPrice3 = finalPrice2 + " " + shekels;
//                        cSplasherPriceSet.setText(finalPrice3);
//                    }
//
//                    //FIX SAVING RATING DATA TO FILE PROPERLY, FETCHING IT
//                    //AND SENDING THE PROPER PRICE DATA TO SERVER
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                    }
//                });
//
//                break;
//            }
//            case "español": {
//                stringPriceSet = "10.0";
//                String defaultPrice = "10.0 ₪";
//                cThePrice.setMax(600);
//                if (!(writeReadDataInFile.readFromFile("languageSateSafety").isEmpty())) {
//                    if (writeReadDataInFile.readFromFile("languageSateSafety").equals("español")) {
//                        fetchDataFromFile();
//                    } else {
//                        defaultBadgePrice(defaultPrice);
//                    }
//                } else {
//                    defaultBadgePrice(defaultPrice);
//                }
//                cThePrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        //double
//                        dpValue = ((double) progress / 10.0);
//                        //double
//                        //CHANGE VALUES TO THEIR RESPECTIVE SHEKEL COUNTERPARTS
//                        readyPrice = dpValue + 10.0;
//                        if (readyPrice >= 10.0 && readyPrice <= 18.1) {
//                            showBadge(R.drawable.bronzebadge);
//                            numericalBadge = 1; //BAD
//                        } else if (readyPrice >= 18.2 && readyPrice <= 29.1) {
//                            showBadge(R.drawable.silverbadge);
//                            numericalBadge = 2; //REGULAR
//                        } else if (readyPrice >= 29.2 && readyPrice <= 57.2) {
//                            showBadge(R.drawable.goldbadge);
//                            numericalBadge = 3; //GOOD
//                        } else if (readyPrice >= 57.3 && readyPrice <= 70.0) {
//                            showBadge(R.drawable.platbadge);
//                            numericalBadge = 4; //EXCELLENT
//                        }
//                        if (cThePrice.getProgress() == 1) {
//                            stringPriceSet = String.valueOf(10.0);
//                        } else if (cThePrice.getProgress() == 600) {
//                            stringPriceSet = String.valueOf(70.0);
//                        } else {
//                            stringPriceSet = String.valueOf(readyPrice);
//                        }
//                        String shekels = "₪";
//                        String finalPrice2 = stringPriceSet;
//                        String finalPrice3 = finalPrice2 + " " + shekels;
//                        cSplasherPriceSet.setText(finalPrice3);
//                    }
//
//                    //FIX SAVING RATING DATA TO FILE PROPERLY, FETCHING IT
//                    //AND SENDING THE PROPER PRICE DATA TO SERVER
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                    }
//                });
//
//                break;
//            }
//        }

        //Car Address 1
        cCrLocationEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(WashReqParamsActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        //Until Time 1
        cCrUntilEdit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //hourOfDay >= 12 && hourOfDay < 24
                Calendar c = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= 23) {
                    cTimePick.setHour(c.get(Calendar.HOUR_OF_DAY));
                    cTimePick.setMinute(c.get(Calendar.MINUTE));
                    //cTimePick.setHour(c.get(Calendar.HOUR));
                    //cTimePick.setMinute(c.get(Calendar.MINUTE));
                    cCrUntilEdit.setText(writeReadDataInFile.readFromFile("untilTime"));
                }else{
                    cTimePick.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                    cTimePick.setCurrentMinute(c.get(Calendar.MINUTE));
                    //cTimePick.setCurrentHour(c.get(Calendar.HOUR));
                    //cTimePick.setCurrentHour(c.get(Calendar.MINUTE));
                    cCrUntilEdit.setText(writeReadDataInFile.readFromFile("untilTime"));
                }

                //here
                cTimePick.setVisibility(View.VISIBLE);
                cSetTimeNow.setVisibility(View.VISIBLE);
                cCancelTimeNow.setVisibility(View.VISIBLE);
                cSaveRequest.setVisibility(View.INVISIBLE);

                //invalidate views
                cCrLocationEdit.setEnabled(false);
                cCrLocationEdit.setClickable(false);

                cCrLocationDescriptionEdit.setEnabled(false);
                cCrLocationDescriptionEdit.setClickable(false);

                cCarSelected.setEnabled(false);
                cCarSelected.setClickable(false);

                cCrServicesEdit.setEnabled(false);
                cCrServicesEdit.setClickable(false);

                cSpinnerArrow.setEnabled(false);
                cSpinnerArrow.setClickable(false);

                cAddcar.setVisibility(View.GONE);
                cCarList.setVisibility(View.GONE);
                cSpinnerArrow.setRotation(360);
                carListVisible = false;

            }
        });

        //Until Time 2
        cTimePick.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                /*
                 If hourOfDay is 12 or higher, the AM/PM indicator will be PM. Of course, this has
                 nothing to do with Android, and everything to do with knowing hour
                 12- vs. 24-hour time conventions work

                 if hour of Day is 12 or higher AND less than 24

                 int currentSoleHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                 int currentSoleMinute = Calendar.getInstance().get(Calendar.MINUTE);
                 */
                timePickerMoved = true;
                Calendar timeChanged = Calendar.getInstance();
                int currentSoleHourToSet = timeChanged.get(Calendar.HOUR_OF_DAY);
                if(hourOfDay >= 12 && hourOfDay < 24){

                    //We are in PM
                    //SET UP A SYSTEM TO TELL THE USER HE HAS TO SET UP A TIME AT LEAST 2 HOURS AHEAD
                    //OF THE ACTUAL CURRENT TIME.
                    if(hourOfDay == currentSoleHourToSet || hourOfDay == currentSoleHourToSet + 1 ||
                            hourOfDay == currentSoleHourToSet + 2){
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText = String.format("%02d:%02d", hourOfDay, minute)
                                .toUpperCase(Locale.getDefault()) + " PM";
                        cCrUntilEdit.setText(cCrUntilTimeText);
                        untilTimeWithinRules = false;
                    }else {//compare also for automatic set time of current time that happens as soon as timepicker opens
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText2 = String.format("%02d:%02d", hourOfDay, minute)
                                .toUpperCase(Locale.getDefault()) + " PM";
                        cCrUntilEdit.setText(cCrUntilTimeText2);
                        untilTimeWithinRules = true;
                    }
                } else {

                    //We are in AM
                    //SET UP A SYSTEM TO TELL THE USER HE HAS TO SET UP A TIME AT LEAST 2 HOURS AHEAD
                    //OF THE ACTUAL CURRENT TIME.
                    if(hourOfDay == currentSoleHourToSet || hourOfDay == currentSoleHourToSet + 1 ||
                            hourOfDay == currentSoleHourToSet + 2){
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText = String.format("%02d:%02d", hourOfDay, minute)
                                .toUpperCase(Locale.getDefault()) + " AM";
                        cCrUntilEdit.setText(cCrUntilTimeText);
                        untilTimeWithinRules = false;
                    }else {
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText2 = String.format("%02d:%02d", hourOfDay, minute)
                                .toUpperCase(Locale.getDefault()) + " AM";
                        cCrUntilEdit.setText(cCrUntilTimeText2);
                        untilTimeWithinRules = true;
                    }
                }

                cTimePick.setIs24HourView(false);

                selectedTime = cCrUntilEdit.getText().toString(); //<--FINAL

            }
        });

        //Until Time 3
        cSetTimeNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(untilTimeWithinRules && timePickerMoved) {
                    Log.i("datesSetTime1", "works");
                    layOutTransformUntilTime();
                    timePickerMoved = false;
                    timeChecked = true;
                    enableBtnIfReady();
                }else if(!untilTimeWithinRules && timePickerMoved && todaySwitchedToTomorrow) {
                    Log.i("datesSetTime2", "works");
                    layOutTransformUntilTime();
                    timePickerMoved = false;
                    timeChecked = true;
                    enableBtnIfReady();
                }else if(!timePickerMoved && todaySwitchedToTomorrow){
                    //timePicker unmoved here. Set phone's current local time.
                    Log.i("datesSetTime3", "works");
                    int currentSoleHourToSet = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    int currentSoleMinuteToSet = Calendar.getInstance().get(Calendar.MINUTE);
                    if(currentSoleHourToSet >= 12 && currentSoleHourToSet < 24){//left here. checking ad correcting the time additions
                        //we are in PM
                        @SuppressLint("DefaultLocale")
                        String timeHolder = String.format("%02d:%02d", currentSoleHourToSet,
                                currentSoleMinuteToSet).toUpperCase(Locale.getDefault()) + " PM";
                        //String timeHolderPlusTomorrow = timeHolder + ofTomorrow;
                        cCrUntilEdit.setText(timeHolder);
                    }else{
                        //we are in AM
                        @SuppressLint("DefaultLocale")
                        String timeHolder2 = String.format("%02d:%02d", currentSoleHourToSet,
                                currentSoleMinuteToSet).toUpperCase(Locale.getDefault()) + " AM";
                        //String timeHolder2PlusTomorrow = timeHolder2 + ofTomorrow;
                        cCrUntilEdit.setText(timeHolder2);
                    }
                    layOutTransformUntilTime();
                    timePickerMoved = false;
                    timeChecked = true;
                    enableBtnIfReady();
                    selectedTime = cCrUntilEdit.getText().toString(); //<--FINAL
                }else if(!untilTimeWithinRules && timePickerMoved){
                    Log.i("datesSetTime4", "works");
                    toastMessages.productionMessage(getApplicationContext()
                    ,getResources().getString(R.string.washMyCar_act_java_minimumDuration)
                    ,1);
                    timePickerMoved = false;
                }else{
                    Log.i("datesSetTime5", "works");
                    toastMessages.productionMessage(getApplicationContext()
                            ,getResources().getString(R.string.washMyCar_act_java_minimumDuration)
                            ,1);
                    timePickerMoved = false;
                }
                writeReadDataInFile.writeToFile(String.valueOf(todaySwitchedToTomorrow)
                        , "todayTomorrow");
            }
        });

        //Until Time 4
        cCancelTimeNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //here...View.GONE or View.VISIBLE or View.INVISIBLE are integers
                //gone, visible, true
                clockState(View.GONE, View.VISIBLE, true);
                if (!writeReadDataInFile.readFromFile("untilTime").isEmpty()) {
                    cCrUntilEdit.setText(writeReadDataInFile.readFromFile("untilTime"));
                }
                timePickerMoved = false;
            }
        });

        //Until Time 5
        //If and only if switch set on "tomorrow" then below code executes
        //switch is on(today):
        //move nothing:

        if (!writeReadDataInFile.readFromFile("todayTomorrow").isEmpty()) {//TEST//
            if (writeReadDataInFile.readFromFile("todayTomorrow").equals("true")) {
                fullDate = String.valueOf(dateOfRequest + 1) + "-" + String.valueOf(monthOfRequest
                        + 1) + "-" + String.valueOf(yearOfRequest);
            } else {
                fullDate = String.valueOf(dateOfRequest) + "-" + String.valueOf(monthOfRequest + 1)
                        + "-" + String.valueOf(yearOfRequest);
            }
        }
        cUntilTimeSwitchTodayTomorrow.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //switch is off(tomorrow):
                    //move DAY 1+:
                    todaySwitchedToTomorrow = true;
                    cOfTomorrow.setVisibility(View.VISIBLE);
                    fullDate = String.valueOf(dateOfRequest + 1) + "-" + String
                            .valueOf(monthOfRequest + 1)
                            + "-" + String.valueOf(yearOfRequest);
                }else{
                    todaySwitchedToTomorrow = false;
                    cOfTomorrow.setVisibility(View.GONE);
                    fullDate = String.valueOf(dateOfRequest) + "-" + String
                            .valueOf(monthOfRequest + 1)
                            + "-" + String.valueOf(yearOfRequest);
                }
            }
        });

        //Payme - Isaac. tell what happened
        //bug - if switch untouched but switchedToTomorrow=true, ad +1 to day
        //Idea - badge + price deduction

        //Car Services 1
        String externalWash = getResources().getString(R.string.act_wash_my_car_externalWash);
        String intExtWash = getResources().getString(R.string.act_wash_my_car_extAndIntWash);
        String motorcycle = getResources().getString(R.string.act_wash_my_car_motorcycle);
        items = new String[]{externalWash, intExtWash, motorcycle};
        spinnerAdapter = new ArrayAdapter<>(this
                , android.R.layout.simple_spinner_dropdown_item, items);
        cCrServicesEdit.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboardTwo();
                if (position == 1){
                    //HERE IS THE PROBLEM//
                    mSplasherPriceSetComingSoon.setVisibility(View.VISIBLE);
                    cSplasherPriceSet.setVisibility(View.INVISIBLE);
                    cFinallyOrder.setBackgroundResource(R.drawable.btn_shape_grey);
                    cFinallyOrder.setEnabled(false);
                    cFinallyOrder.setClickable(false);
                }else{
                    mSplasherPriceSetComingSoon.setVisibility(View.INVISIBLE);
                    cSplasherPriceSet.setVisibility(View.VISIBLE);
                    cFinallyOrder.setBackgroundResource(R.drawable.btn_shape);
                    cFinallyOrder.setEnabled(true);
                    cFinallyOrder.setClickable(true);
                    if (writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")
                            && writeReadDataInFile.readFromFile("buyer_key_temporal")
                            .equals("")){
                        cFinallyOrder.setBackgroundResource(R.drawable.btn_shape_grey);
                    }
                }
                getServiceType = cCrServicesEdit.getSelectedItem().toString();//<--FINAL1
                toastMessages.debugMesssage(getApplicationContext(),"here",1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hideKeyboardTwo();
            }
        });
        cCrServicesEdit.setAdapter(spinnerAdapter);

        //add Car 1
        cAddcar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                hideKeyboardTwo();
                //Writte half the data to file
                loadHalfDataToFile();
                if (!cCrLocationDescriptionEdit.getText().toString().isEmpty()){
                    writeLocationDescToFile(cCrLocationDescriptionEdit
                            .getText().toString());
                }
                startActivity(new Intent(WashReqParamsActivity
                        .this,CarAdditionActivity.class));
            }
        });

        //Open car list
        //Disable main scroll when this opens, enable when it closes
        //or.. get rid of fields, add 'NEXT' button to be anchored to bottom of screen(alignParentBottom) and remove scrollView on XML again
        //Location, Location details, until time, Choose a car
        cCarSelected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setEmptyListText();
                if(!carListVisible) {
                    showCarList();
                }else{
                    hideCarList();
                }
            }
        });

        cSpinnerArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setEmptyListText();
                if(!carListVisible) {
                    showCarList();
                }else{
                    hideCarList();
                }
            }
        });

        if(dbCars.isEmpty()){
            cCarSelected.setHint(getResources().getString(R.string.washMyCar_act_java_chooseACar));
        }

        refreshData();

        clm.connectivityStatus(WashReqParamsActivity.this);

        if(Locale.getDefault().getDisplayLanguage().equals("עברית")) {
            cCrUntilEdit.setGravity(Gravity.END);//<<<Needed to RTL
            cCrUntilEdit.setGravity(Gravity.RIGHT);//<<<Needed to RTL
            cCrUntilEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);//<<<Needed to RTL

            cCrLocationEdit.setGravity(Gravity.END);
            cCrLocationEdit.setGravity(Gravity.RIGHT);
            cCrLocationEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCrLocationDescriptionEdit.setGravity(Gravity.END);
            cCrLocationDescriptionEdit.setGravity(Gravity.RIGHT);
            cCrLocationDescriptionEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarSelected.setGravity(Gravity.END);
            cCarSelected.setGravity(Gravity.RIGHT);
            cCarSelected.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        }else if(Locale.getDefault().getDisplayLanguage().equals("English")){

            cCrUntilEdit.setGravity(Gravity.START);//<<<Needed to RTL
            cCrUntilEdit.setGravity(Gravity.LEFT);//<<<Needed to RTL
            cCrUntilEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);//<<<Needed to RTL

            cCrLocationEdit.setGravity(Gravity.START);
            cCrLocationEdit.setGravity(Gravity.LEFT);
            cCrLocationEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCrLocationDescriptionEdit.setGravity(Gravity.START);
            cCrLocationDescriptionEdit.setGravity(Gravity.LEFT);
            cCrLocationDescriptionEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarSelected.setGravity(Gravity.START);
            cCarSelected.setGravity(Gravity.LEFT);
            cCarSelected.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        }

        //Hide page index 1 of 2 (1)
        cElPropioRelative.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = cElPropioRelative.getRootView().getHeight() - cElPropioRelative
                        .getHeight();
                if (heightDiff > dpToPx(WashReqParamsActivity.this)) { // if more than 200 dp, it's probably a keyboard...
                    //Handle code which triggers when keyboard is open
                    cRequestSteps.setVisibility(View.GONE);
                }else{
                    cRequestSteps.setVisibility(View.VISIBLE);
                }
            }
        });

        //cCarList.setVisibility(View.GONE);
        //cAddcar.setVisibility(View.GONE);
        onStartSubmitButtonState();

        splasherListFragmentInit();
    }

    public void splasherListFragmentInit(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SplasherListFragment splasherListFragment = new SplasherListFragment();
        fragmentTransaction.add(R.id.splasher_fragment_container, splasherListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Maybe for fragment interaction from this activity i might need to do something
    }

    public void setEmptyListText(){
        if (dbCars.size() == 0){
            mEmptyCarList.setVisibility(View.VISIBLE);
        }else{
            mEmptyCarList.setVisibility(View.GONE);
        }
    }

    public void setEmptyCarSelectedView(){
        cCarSelected.setText("");
        cCarSelected.setHint(getResources().getString(R.string
                .act_wash_my_car_chooseACar));
    }

    public void showCarList(){
        cAddcar.setVisibility(View.VISIBLE);
        cCarList.setVisibility(View.VISIBLE);
        cSpinnerArrow.setRotation(180);
        carListVisible = true;
    }

    public void hideCarList(){
        cAddcar.setVisibility(View.GONE);
        cCarList.setVisibility(View.GONE);
        mEmptyCarList.setVisibility(View.GONE);
        cSpinnerArrow.setRotation(360);
        carListVisible = false;
    }

    //System that enables or disables the 'Next' button by turning its bg color grey or blue------//
    public void onStartSubmitButtonState(){
        if (cCrLocationEdit.getText().toString().isEmpty() || cCrUntilEdit.getText().toString()
                .isEmpty() || cCarSelected.getText().toString().isEmpty()){
            //left here
            cSaveRequest.setBackgroundResource(R.drawable.btn_shape_grey);
        }
        if (!cCrLocationEdit.getText().toString().isEmpty()){
            locationChecked = true;
            Log.i("checkFirst", "true");
        }
        if (!cCarSelected.getText().toString().isEmpty()){
            carChecked = true;
            Log.i("checkSecond", "true");
        }
        if (!cCrUntilEdit.getText().toString().isEmpty()){
            timeChecked = true;
            Log.i("checkThird", "true");
        }
    }
    public void enableBtnIfReady(){
        Log.i("checks::", "are " + locationChecked + " " + carChecked + " " +
        timeChecked);
        if (locationChecked && carChecked && timeChecked){
            cSaveRequest.setBackgroundResource(R.drawable.btn_shape);
        }
    }
    public void finallyOrderBtnInactiveBg(){
        cFinallyOrder.setBackgroundResource(R.drawable.btn_shape_grey);
    }
    public void finallyOrderBtnActiveBg(){
        cFinallyOrder.setBackgroundResource(R.drawable.btn_shape);
    }
    //--------------------------------------------------------------------------------------------//

    public void clockState(int layoutState1, int layoutState2 ,boolean clockState){

        cTimePick.setVisibility(layoutState1);
        cSetTimeNow.setVisibility(layoutState1);
        cCancelTimeNow.setVisibility(layoutState1);
        cSaveRequest.setVisibility(layoutState2);

        //Validate Views
        cCrLocationEdit.setEnabled(clockState);
        cCrLocationEdit.setClickable(clockState);

        cCrLocationDescriptionEdit.setEnabled(clockState);
        cCrLocationDescriptionEdit.setClickable(clockState);

        cCarSelected.setEnabled(clockState);
        cCarSelected.setClickable(clockState);

        cCrServicesEdit.setEnabled(clockState);
        cCrServicesEdit.setClickable(clockState);

        cSpinnerArrow.setEnabled(clockState);
        cSpinnerArrow.setClickable(clockState);

        if (!writeReadDataInFile.readFromFile("untilTime").isEmpty()) {
            cCrUntilEdit.setText(writeReadDataInFile.readFromFile("untilTime"));
        }
        timePickerMoved = false;
    }

    //Hide page index 1 of 2 (2)
    public static float dpToPx(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 200, metrics);
    }

    //Until Time Extention
    public void layOutTransformUntilTime(){
        cTimePick.setVisibility(View.GONE);
        cSetTimeNow.setVisibility(View.GONE);
        cCancelTimeNow.setVisibility(View.GONE);
        cSaveRequest.setVisibility(View.VISIBLE);
        //Validate Views
        cCrLocationEdit.setEnabled(true);
        cCrLocationEdit.setClickable(true);
        cCrLocationDescriptionEdit.setEnabled(true);
        cCrLocationDescriptionEdit.setClickable(true);
        cCarSelected.setEnabled(true);
        cCarSelected.setClickable(true);
        cCrServicesEdit.setEnabled(true);
        cCrServicesEdit.setClickable(true);
        cSpinnerArrow.setEnabled(true);
        cSpinnerArrow.setClickable(true);
        writeReadDataInFile.writeToFile(cCrUntilEdit.getText().toString(), "untilTime");
    }

    //Car Address 2: Actual Address and Coordinates(Latitude and Longitude)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == PLACE_PICKER_REQUEST) {

                Place place = PlacePicker.getPlace(this, data);

                address = String.format("%s", place.getAddress());//<--FINAL

                cCrLocationEdit.setText(address);

                carCoordinates = place.getLatLng();//<--FINAL

                Double c1lat = place.getLatLng().latitude;
                Double c1lon = place.getLatLng().longitude;

                String sC1lat = c1lat.toString();
                String sC1lon = c1lon.toString();

                writeReadDataInFile.writeToFile(sC1lat, "lat");
                writeReadDataInFile.writeToFile(sC1lon, "lon");

                carCoordinatesIn = true;
                locationChecked = true;
                enableBtnIfReady();

                Log.i("Car Coordinates", String.valueOf(carCoordinates));
                Log.i("car CoordLatSaved", String.valueOf(c1lat));
                Log.i("car CoordLonSaved", String.valueOf(c1lon));
                Log.i("car CoordLatSaved", sC1lat);
                Log.i("car CoordLonSaved", sC1lon);

            } else if (requestCode == REQUEST_CC_FROM_CCDETS) {

                String ccMask = data.getStringExtra("paymeCCMask");

                Log.i("RequestCode", "Were in CC_FROM_CCDETS");
                Log.i("RequestCodeNData", ccMask);

                if (ccMask.equals("Payment Method")) {
                    cPayMethodTextView.setText(ccMask);
                } else {
                    cPayMethodTextView.setText(ccMask);
                }
            }
        }
    }

    public void warningDialog(String title, String message){
        android.support.v7.app.AlertDialog.Builder missingStuffDialog = new android.support.v7.app
                .AlertDialog.Builder(WashReqParamsActivity.this);
        missingStuffDialog.setTitle(title);
        missingStuffDialog.setIcon(android.R.drawable.ic_dialog_alert);
        missingStuffDialog.setMessage(message);
        missingStuffDialog.setPositiveButton(getResources().getString(R.string
                .washMyCar_act_java_ok), null);
        missingStuffDialog.setCancelable(false);
        missingStuffDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toastMessages.debugMesssage(getApplicationContext(),"called",1);
        //it works. do btn bg deduction here. create method for it and call it twice. one here one
        //in onCreate()
        buyerKeyDeducer();
        hideCarList();
    }

    public void buyerKeyDeducer(){
        if(!writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){

            cPayMethodTextView.setText(writeReadDataInFile.readFromFile("cleanStringCCMask"));
            toastMessages.debugMesssage(getApplicationContext(),"there is a permanent key"
            ,1);
            finallyOrderBtnActiveBg();

            if (mSplasherPriceSetComingSoon.getVisibility() == View.VISIBLE){
                finallyOrderBtnInactiveBg();
            }

        }else if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){

            cPayMethodTextView.setText(getResources().getString(R.string.act_wash_my_car_ccNotSaved));
            toastMessages.debugMesssage(getApplicationContext()
                    ,"there is a temporal key",1);
            finallyOrderBtnActiveBg();

            if (mSplasherPriceSetComingSoon.getVisibility() == View.VISIBLE){
                finallyOrderBtnInactiveBg();
            }

        }else if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){
            //This means both buyer_keys are empty
            cPayMethodTextView.setHint(getResources().getString(R.string.act_wash_my_car_paymentMethod));
            toastMessages.debugMesssage(getApplicationContext()
            ,"there is no key, no saved credit card",1);
            finallyOrderBtnInactiveBg();

        }else{
            toastMessages.productionMessage(getApplicationContext()
                    ,"Error",1);
        }
    }

    public void writeLocationDescToFile(String string){
        if(!cCrLocationDescriptionEdit.getText().toString().isEmpty()) {
            writeReadDataInFile.writeToFile(string, "locationDesc"); //writtenToFile1
        }
    }

    //When click to add new car
    public void loadHalfDataToFile(){
        if(!cCrLocationEdit.getText().toString().isEmpty()) {
            writeReadDataInFile.writeToFile(cCrLocationEdit.getText().toString(), "location"); //writtenToFile1
        }
        if(!cCrUntilEdit.getText().toString().isEmpty()){
            writeReadDataInFile.writeToFile(cCrUntilEdit.getText().toString(), "untilTime"); //writtenToFile 3
        }
        //Language state safety//
        writeReadDataInFile.writeToFile(String.valueOf(Locale.getDefault().getDisplayLanguage()), "languageSateSafety");
        //---------------------//
    }

    //TODO:Rating 4
    public void loadDataToFile(){

        writeReadDataInFile.writeToFile(cCrLocationEdit.getText().toString(), "location"); //writtenToFile1
        writeReadDataInFile.writeToFile(cCrUntilEdit.getText().toString(), "untilTime"); //writtenToFile 3
        //String setPrice = stringPriceSet;
        //TEMPORARY!!!//
        String setPrice = String.valueOf(PaymeConstants.STATIC_TEMPORAL_PRICE);
        stringPriceSet = String.valueOf(PaymeConstants.STATIC_TEMPORAL_PRICE);
        dollarSetPrice = setPrice;//<--FINAL
        String setRawNum = stringPriceSet;
        writeReadDataInFile.writeToFile(setRawNum, "setPrice"); //writtenToFile5
        writeReadDataInFile.writeToFile(setPrice, "thePriceIsRight"); //writtenToFile6
        writeReadDataInFile.writeToFile(String.valueOf(cThePrice.getProgress()), "progress");
        //Language state safety//
        writeReadDataInFile.writeToFile(String.valueOf(Locale.getDefault().getDisplayLanguage())
                , "languageSateSafety");
        //---------------------//

    }

    public void fetchDataFromFile(){
        /*
            writeReadDataInFile.writeToFile(cCrLocationEdit.getText().toString(), "location");
            writeReadDataInFile.writeToFile(sC1lat, "lat");
            writeReadDataInFile.writeToFile(sC1lon, "lon");
            writeReadDataInFile.writeToFile(cCrLocationDescriptionEdit.getText().toString(), "locationDesc");
            writeReadDataInFile.writeToFile(cCrUntilEdit.getText().toString(), "untilTime");
            writeReadDataInFile.writeToFile(cCarSelected.getText().toString(), "carSelected");
            writeReadDataInFile.writeToFile(setRating, "setRating");
            writeReadDataInFile.writeToFile(setPrice, "setPrice");
         */
        /*
        **THE BUG WITH THE STRING-TO-FLOAT NUMBER IS THAT IT SEEMS TO BE GRABBING THE SHEKEL SYMBOL WHICH OBVS
            CAN NOT PARSE TO A FLOAT DIGIT. FIX THIS<<TEST TOMORROW>>
        ** HERE IN ISRAEL, I HAVE TO HAVE THE SHEKEL SYSTEM ALSO FOR PPL WITH THEIR PHONE IN ENGLISH LANGUAGE. DON'T FORGET
         */
        if(!writeReadDataInFile.readFromFile("location").isEmpty() && !writeReadDataInFile.readFromFile("lat").isEmpty()
                && !writeReadDataInFile.readFromFile("lon").isEmpty()){
            cCrLocationEdit.setText(writeReadDataInFile.readFromFile("location"));
            address = cCrLocationEdit.getText().toString();
            Double getSavedLat = Double.parseDouble(writeReadDataInFile.readFromFile("lat"));
            Double getSavedLon = Double.parseDouble(writeReadDataInFile.readFromFile("lon"));
            Log.i("new carcoords", getSavedLat.toString());
            Log.i("new carcoords2", getSavedLon.toString());
            carCoordinates = new LatLng(getSavedLat, getSavedLon);
            Log.i("final carcoords", String.valueOf(carCoordinates.latitude));
            Log.i("final carcoords2", String.valueOf(carCoordinates.longitude));
            carCoordinatesIn = true;
        }
        if(!writeReadDataInFile.readFromFile("untilTime").isEmpty()){
            cCrUntilEdit.setText(writeReadDataInFile.readFromFile("untilTime"));
            selectedTime = cCrUntilEdit.getText().toString();
        }
        if(!writeReadDataInFile.readFromFile("locationDesc").isEmpty()){
            cCrLocationDescriptionEdit
                    .setText(writeReadDataInFile.readFromFile("locationDesc"));
        }
        if (!writeReadDataInFile.readFromFile("todayTomorrow").isEmpty()){
            //if true=tomorrow else today
            if (writeReadDataInFile.readFromFile("todayTomorrow").equals("true")){
                cUntilTimeSwitchTodayTomorrow.setChecked(true);
                todaySwitchedToTomorrow = true;
                cOfTomorrow.setVisibility(View.VISIBLE);
            }else{
                cUntilTimeSwitchTodayTomorrow.setChecked(false);
                todaySwitchedToTomorrow = false;
                cOfTomorrow.setVisibility(View.GONE);
            }
        }
//        switch (Locale.getDefault().getDisplayLanguage()) {
//            case "English": {//<<<<<<<<<<<<<<<<<<<<<<<<<<
//                String shekels = "₪";
//                String savedPrice = "";
//                String fullSavedPrice;
//                if (!writeReadDataInFile.readFromFile("setPrice").isEmpty() && !writeReadDataInFile.readFromFile("thePriceIsRight").isEmpty()) {
//                    savedPrice = writeReadDataInFile.readFromFile("thePriceIsRight");
//                    stringPriceSet = savedPrice;
//                    fullSavedPrice = savedPrice + " " + shekels;
//                    cSplasherPriceSet.setText(fullSavedPrice);
//                } else {
//                    stringPriceSet = "10.0";
//                }
//                //NEED TO FIX HERE REALTION OF PROGRESS BAR WITH SHEKEL VALUES. AND IN THE ACTUAL SEEKBAR ABOVE.
//                //CHECK THAT THE DOLLAR COUNTERPART OPERATES CORRECTLY
//                if (!writeReadDataInFile.readFromFile("progress").isEmpty()) {
//                    Log.i("progress saved", writeReadDataInFile.readFromFile("progress"));
//                    int priceBackToInt = Integer.parseInt(writeReadDataInFile.readFromFile("progress"));
//                    Log.i("progress saved 2", String.valueOf(priceBackToInt));
//                    Log.i("progress max value", String.valueOf(cThePrice.getMax()));
//                    cThePrice.setProgress(priceBackToInt);//<<FIX BADGE ACCORDING TO PROGRESS POSITION (INT)-->FIXED./
//                    Log.i("splasherSetPrice", savedPrice);
//                    float floatSplashPrice = Float.parseFloat(savedPrice);
//                    Log.i("splasherSetPriceFl", "is: " + floatSplashPrice);
//                    if (floatSplashPrice >= 10.0 && floatSplashPrice <= 18.1) {
//                        showBadge(R.drawable.bronzebadge);
//                        numericalBadge = 1; //BAD
//                    } else if (floatSplashPrice >= 18.2 && floatSplashPrice <= 29.1) {
//                        showBadge(R.drawable.silverbadge);
//                        numericalBadge = 2; //REGULAR
//                    } else if (floatSplashPrice >= 29.2 && floatSplashPrice <= 57.2) {
//                        showBadge(R.drawable.goldbadge);
//                        numericalBadge = 3; //GOOD
//                    } else if (floatSplashPrice >= 57.3 && floatSplashPrice <= 70.0) {
//                        showBadge(R.drawable.platbadge);
//                        numericalBadge = 4; //EXCELLENT
//                    }
//                    //NOW NEED TO PUT THE NUMERICAL BADGE FROM THE CHOOSEN BADGE SPLASHER, INTO THE REQUEST CLASS IN SERVER, SO THAT I CAN FILTER OUT REQUESTS
//                    //FROM SPLASHER SIDE ACCORDING TO REQUESTED SPLASHER(BADGE) AND SPLASHER'S CURRENT LEVEL(BADGE).
//                } else {
//                    String defaultPrice2 = "10.0 ₪";
//                    defaultBadgePrice(defaultPrice2);
//                }
//                break;
//            }
//            case "עברית": {
//                String shekels = "₪";
//                String savedPrice = "";
//                String fullSavedPrice;
//                if (!writeReadDataInFile.readFromFile("setPrice").isEmpty() && !writeReadDataInFile.readFromFile("thePriceIsRight").isEmpty()) {
//                    savedPrice = writeReadDataInFile.readFromFile("thePriceIsRight");
//                    stringPriceSet = savedPrice;
//                    fullSavedPrice = savedPrice + " " + shekels;
//                    cSplasherPriceSet.setText(fullSavedPrice);
//                } else {
//                    stringPriceSet = "10.0";
//                }
//                //NEED TO FIX HERE REALTION OF PROGRESS BAR WITH SHEKEL VALUES. AND IN THE ACTUAL SEEKBAR ABOVE.
//                //CHECK THAT THE DOLLAR COUNTERPART OPERATES CORRECTLY
//                if (!writeReadDataInFile.readFromFile("progress").isEmpty()) {
//                    Log.i("progress saved", writeReadDataInFile.readFromFile("progress"));
//                    int priceBackToInt = Integer.parseInt(writeReadDataInFile.readFromFile("progress"));
//                    Log.i("progress saved 2", String.valueOf(priceBackToInt));
//                    Log.i("progress max value", String.valueOf(cThePrice.getMax()));
//                    cThePrice.setProgress(priceBackToInt);//<<FIX BADGE ACCORDING TO PROGRESS POSITION (INT)-->FIXED./
//                    Log.i("splasherSetPrice", savedPrice);
//                    float floatSplashPrice = Float.parseFloat(savedPrice);
//                    Log.i("splasherSetPriceFl", "is: " + floatSplashPrice);
//                    if (floatSplashPrice >= 10.0 && floatSplashPrice <= 18.1) {
//                        showBadge(R.drawable.bronzebadge);
//                        numericalBadge = 1; //BAD
//                    } else if (floatSplashPrice >= 18.2 && floatSplashPrice <= 29.1) {
//                        showBadge(R.drawable.silverbadge);
//                        numericalBadge = 2; //REGULAR
//                    } else if (floatSplashPrice >= 29.2 && floatSplashPrice <= 57.2) {
//                        showBadge(R.drawable.goldbadge);
//                        numericalBadge = 3; //GOOD
//                    } else if (floatSplashPrice >= 57.3 && floatSplashPrice <= 70.0) {
//                        showBadge(R.drawable.platbadge);
//                        numericalBadge = 4; //EXCELLENT
//                    }
//                    //NOW NEED TO PUT THE NUMERICAL BADGE FROM THE CHOOSEN BADGE SPLASHER, INTO THE REQUEST CLASS IN SERVER, SO THAT I CAN FILTER OUT REQUESTS
//                    //FROM SPLASHER SIDE ACCORDING TO REQUESTED SPLASHER(BADGE) AND SPLASHER'S CURRENT LEVEL(BADGE).
//                } else {
//                    String defaultPrice2 = "10.0 ₪";
//                    defaultBadgePrice(defaultPrice2);
//                }
//                break;
//            }
//            case "español": {//<<<<<<<<<<<<<<<<<<<<<<<<<<
//                String shekels = "₪";
//                String savedPrice = "";
//                String fullSavedPrice;
//                if (!writeReadDataInFile.readFromFile("setPrice").isEmpty() && !writeReadDataInFile.readFromFile("thePriceIsRight").isEmpty()) {
//                    savedPrice = writeReadDataInFile.readFromFile("thePriceIsRight");
//                    stringPriceSet = savedPrice;
//                    fullSavedPrice = savedPrice + " " + shekels;
//                    cSplasherPriceSet.setText(fullSavedPrice);
//                } else {
//                    stringPriceSet = "10.0";
//                }
//                //NEED TO FIX HERE REALTION OF PROGRESS BAR WITH SHEKEL VALUES. AND IN THE ACTUAL SEEKBAR ABOVE.
//                //CHECK THAT THE DOLLAR COUNTERPART OPERATES CORRECTLY
//                if (!writeReadDataInFile.readFromFile("progress").isEmpty()) {
//                    Log.i("progress saved", writeReadDataInFile.readFromFile("progress"));
//                    int priceBackToInt = Integer.parseInt(writeReadDataInFile.readFromFile("progress"));
//                    Log.i("progress saved 2", String.valueOf(priceBackToInt));
//                    Log.i("progress max value", String.valueOf(cThePrice.getMax()));
//                    cThePrice.setProgress(priceBackToInt);//<<FIX BADGE ACCORDING TO PROGRESS POSITION (INT)-->FIXED./
//                    Log.i("splasherSetPrice", savedPrice);
//                    float floatSplashPrice = Float.parseFloat(savedPrice);
//                    Log.i("splasherSetPriceFl", "is: " + floatSplashPrice);
//                    if (floatSplashPrice >= 10.0 && floatSplashPrice <= 18.1) {
//                        showBadge(R.drawable.bronzebadge);
//                        numericalBadge = 1; //BAD
//                    } else if (floatSplashPrice >= 18.2 && floatSplashPrice <= 29.1) {
//                        showBadge(R.drawable.silverbadge);
//                        numericalBadge = 2; //REGULAR
//                    } else if (floatSplashPrice >= 29.2 && floatSplashPrice <= 57.2) {
//                        showBadge(R.drawable.goldbadge);
//                        numericalBadge = 3; //GOOD
//                    } else if (floatSplashPrice >= 57.3 && floatSplashPrice <= 70.0) {
//                        showBadge(R.drawable.platbadge);
//                        numericalBadge = 4; //EXCELLENT
//                    }
//                    //NOW NEED TO PUT THE NUMERICAL BADGE FROM THE CHOOSEN BADGE SPLASHER, INTO THE REQUEST CLASS IN SERVER, SO THAT I CAN FILTER OUT REQUESTS
//                    //FROM SPLASHER SIDE ACCORDING TO REQUESTED SPLASHER(BADGE) AND SPLASHER'S CURRENT LEVEL(BADGE).
//                } else {
//                    String defaultPrice2 = "10.0 ₪";
//                    defaultBadgePrice(defaultPrice2);
//                }
//                break;
//            }
//        }
    }

    public void defaultBadgePrice(String defaultPrice){
        showBadge(R.drawable.bronzebadge);
        numericalBadge = 1; //BAD
        cThePrice.setProgress(1);
        cSplasherPriceSet.setText(defaultPrice);
    }

    //Load Request to Parse Server 2
    public void loadRequest() {

        try {

            if (!cCrLocationDescriptionEdit.getText().toString().isEmpty())
                carAddressDescription = cCrLocationDescriptionEdit.getText().toString();//<--FINAL
            else
                carAddressDescription = "empty";

            String profilePicToUpload;
            if (!(ParseUser.getCurrentUser().getString("fbProfilePic") == null)) {
                profilePicToUpload = ParseUser.getCurrentUser().getString("fbProfilePic");
                if (!profilePicToUpload.contains("https")) {
                    ParseFile profPicNoFbFile = ParseUser.getCurrentUser().getParseFile("localProfilePic");
                    profPicNoFbString = profPicNoFbFile.getUrl();
                } else if (profilePicToUpload.contains("https")) {
                    profPicNoFbString = "none";
                }
            } else {
                profilePicToUpload = "none";
                profPicNoFbString = "none";
            }

            //write to file
            loadDataToFile();

            //set location description to nothing
            if (!cCrLocationDescriptionEdit.getText().toString().isEmpty())
            writeLocationDescToFile("");

            Log.i("address", address); //Address

            Log.i("latlng", carCoordinates.toString()); //Coordinates

            Log.i("carAddressDescription", carAddressDescription); // Address description

            Log.i("Until Time Selected", fullDate + " " + selectedTime); //Until Time

            Log.i("Car Wash Service Type", getServiceType); //Service Type

            Log.i("Selected Car: ", carBrandToUpload + " " + carModelToUpload + " " + carColorToUpload
                    + " " + " " + carPlateToUpload); //Selected Car

            Log.i("Final Price", dollarSetPrice); // set Price

            Log.i("Profile Pic", profilePicToUpload); //Profile Pic

            Log.i("numerical badge", String.valueOf(numericalBadge));

            if (!profilePicToUpload.contains("https")) {
                Log.i("Profile Pic NoFb", profPicNoFbString); //Profile Pic Not Facebook
            }

            //TODO: Payme Code: Sending Car owner's buyer_key to Request Log
            //!writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")
            if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                    !writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){
                //Temporal buyer_key
                Log.i("disposableBuyerKey", writeReadDataInFile.readFromFile("buyer_key_temporal"));
            }else if(writeReadDataInFile.readFromFile("buyer_key_temporal").equals("") &&
                    !writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")){
                //Permanent buyer_key
                Log.i("permanentBuyerKey", writeReadDataInFile.readFromFile("buyer_key_permanent"));
            }

            //-----------------------------------------------------------------------------------------
            ParseObject request = new ParseObject("Request");

            request.put("username", ParseUser.getCurrentUser().getUsername());

            request.put("carAddress", address); //1

            ParseGeoPoint carGeoPoint = new ParseGeoPoint(carCoordinates.latitude, carCoordinates.longitude);

            request.put("carCoordinates", carGeoPoint); //2

            request.put("carAddressDesc", carAddressDescription); //3

            request.put("untilTime", fullDate + " " + selectedTime); //4

            request.put("serviceType", getServiceType); //5

            request.put("carBrand", carBrandToUpload); //6

            request.put("carModel", carModelToUpload); //7

            request.put("carColor", carColorToUpload); //8

            request.put("carplateNumber", carPlateToUpload); //10

            String shekels = "₪";
            String shekelsSetPriceFinal = dollarSetPrice + " " + shekels;
            request.put("priceWanted", shekelsSetPriceFinal);

            request.put("fbProfilePic", profilePicToUpload); //13

            if (!profilePicToUpload.contains("https")) {
                request.put("ProfPicNoFb", profPicNoFbString); //14
            }

            //Temporary until we implement back the badge system://
            numericalBadge = 2;
            request.put("badgeWanted", String.valueOf(numericalBadge)); //15

            request.put("taken", "no"); //16

            request.put("splasherUsername", "clear"); //17

            request.put("picturesInbound", "false"); //18

            request.put("washFinished", "no");//19

            request.put("paid", "no");//20

            //TODO: Payme Code: Sending Car owner's buyer_key to Request PUT
            //!writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")
            if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                    !writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){
                //Temporal buyer_key
                request.put("buyerKey", writeReadDataInFile.readFromFile("buyer_key_temporal"));//21
                //Activate boolean value that marks it is a temporal buyer_key
                temporalKeyActive = true;
            }else if(writeReadDataInFile.readFromFile("buyer_key_temporal").equals("") &&
                    !writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")){
                //Permanent buyer_key
                request.put("buyerKey", writeReadDataInFile.readFromFile("buyer_key_permanent"));//21
            }

            //-----------------------------------------------

            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        //Works. Now work on this boolean below: findCarWasherRequestActive
                        //findCarWasherRequestActive = true;
                        toastMessages.debugMesssage(getApplicationContext()
                        ,getResources().getString(R.string.washMyCar_act_java_washRequestSent),1);
                        Double carCoordinatesLatitude = carCoordinates.latitude;
                        Double carCoordinatesLongitude = carCoordinates.longitude;
                        Intent intent = new Intent(WashReqParamsActivity.this,
                                HomeActivity.class);
                        intent.putExtra("requestActive", "active");
                        intent.putExtra("carLat", carCoordinatesLatitude);
                        intent.putExtra("carLon", carCoordinatesLongitude);
                        intent.putExtra("selectedTime", fullDate + " " + selectedTime);
                        if(getServiceType.equals("Motorcycle") || getServiceType.equals("אופנוע")){
                            writeReadDataInFile.writeToFile("bike", "bikeOrNot");
                        }else{
                            writeReadDataInFile.writeToFile("noBike", "bikeOrNot");
                        }
                        if (temporalKeyActive){
                            writeReadDataInFile.writeToFile("",
                                    "buyer_key_temporal");
                            toastMessages.debugMesssage(getApplicationContext()
                            ,"temporal key sent to request class on server and destroyed" +
                                            " from local .txt file right after",1);
                        }
                        startActivity(intent);
                    }
                }
            });

        }catch(NullPointerException e){

            e.printStackTrace();
        }
    }

    //TODO: Handling 'know-how' for app to recognize where data comes from 1
    public void toCCDetails(View view){

        Intent intent = new Intent(WashReqParamsActivity.this, PaymentSettingsActivity.class);
        startActivityForResult(intent, REQUEST_CC_FROM_CCDETS);

    }

    private void refreshData(){

        dbCars.clear();

        dbh = new CarLocalDatabaseHandler(WashReqParamsActivity.this);

        ArrayList<MyCar> carsFromDB = dbh.getCars();

        for(int i = 0; i < carsFromDB.size(); i++){

            //String title = wishesFromDB.get(i).getTitle();

            String refreshedBrand = carsFromDB.get(i).getBrand();

            String refreshedModel = carsFromDB.get(i).getModel();

            String refreshedColor = carsFromDB.get(i).getColorz();

            String refreshedPlate = carsFromDB.get(i).getPlateNum();

            int myId = carsFromDB.get(i).getItemId();

            MyCar refreshedCar = new MyCar();

            refreshedCar.setBrand(refreshedBrand);

            refreshedCar.setModel(refreshedModel);

            refreshedCar.setColorz(refreshedColor);

            refreshedCar.setPlateNum(refreshedPlate);

            refreshedCar.setItemId(myId);

            dbCars.add(refreshedCar);

        }

        dbh.close();

        CarAdapter myCarAdapter = new CarAdapter(WashReqParamsActivity.this, R.layout.car_row, dbCars);

        cCarList.setAdapter(myCarAdapter);

        myCarAdapter.notifyDataSetChanged();

        Bundle keyToKeepListOpen = getIntent().getExtras();
        if (keyToKeepListOpen != null) {
            String keyToOpen = keyToKeepListOpen.getString("listStatus");
            if (keyToOpen != null && keyToOpen.equals("keepOpen")) {
                cAddcar.setVisibility(View.VISIBLE);
                cCarList.setVisibility(View.VISIBLE);
                cSpinnerArrow.setRotation(180);
                carListVisible = true;
                justAddedCar = true;
                carBrandToUpload = keyToKeepListOpen.getString("carBrand");
                carModelToUpload = keyToKeepListOpen.getString("carModel");
                carColorToUpload = keyToKeepListOpen.getString("carColor");
                carPlateToUpload = keyToKeepListOpen.getString("carPlate");
                String carSelectedHolder = carBrandToUpload + " " + carModelToUpload;
                cCarSelected.setText(carSelectedHolder);
                carChecked = true;
                enableBtnIfReady();
            }
        }
    }

    public void hideKeyboardTwo(){

        try {

            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }

    public class CarAdapter extends ArrayAdapter<MyCar>{

        private Activity activity;
        private int layoutResource;
        private ArrayList<MyCar> mData;
        private int finalSelecId;

        //Constructor
        public CarAdapter(Activity act, int resource, ArrayList<MyCar> data){
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

        /*
        public int getLayoutResource() {
            return layoutResource;
        }
        public void setLayoutResource(int layoutResource) {
            this.layoutResource = layoutResource;
        }
        public MyCar getCarro() {
            return carro;
        }
        public void setCarro(MyCar carro) {
            this.carro = carro;
        }
        public ArrayList<MyCar> getmData() {
            return mData;
        }
        public void setmData(ArrayList<MyCar> mData) {
            this.mData = mData;
        }
        */
        //getCount, getItem, getPosition, getItemId, getView

        @Override
        public int getCount() {
            return mData.size();
        }

        @Nullable
        @Override
        public MyCar getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getPosition(@Nullable MyCar item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView,
                            @NonNull ViewGroup parent) {
            //return super.getView(position, convertView, parent);

            View row = convertView;
            ViewHolder holder;

            if (row == null || (row.getTag()) == null){

                LayoutInflater theInflator = LayoutInflater.from(activity);

                row = theInflator.inflate(layoutResource, null);

                holder = new ViewHolder();

                holder.mBrand = row.findViewById(R.id.carBrandRow);

                holder.mModel = row.findViewById(R.id.carModelRow);

                holder.mColor = row.findViewById(R.id.carColorRow);

                holder.mPlate = row.findViewById(R.id.carPlateRow);

                row.setTag(holder);

            } else {

                holder = (ViewHolder) row.getTag();

            }

            holder.holderCar = getItem(position);

            if (holder.holderCar != null) {
                holder.mBrand.setText(holder.holderCar.getBrand());
            }

            if (holder.holderCar != null) {
                holder.mModel.setText(holder.holderCar.getModel());
            }

            if (holder.holderCar != null) {
                holder.mColor.setText(holder.holderCar.getColorz());
            }

            if (holder.holderCar != null) {
                holder.mPlate.setText(holder.holderCar.getPlateNum());
            }

            final ViewHolder finalHolder = holder;
            row.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    //String text = finalHolder.theWish.getContent().toString();

                    carBrandToUpload = finalHolder.holderCar.getBrand();
                    carModelToUpload = finalHolder.holderCar.getModel();
                    carColorToUpload = finalHolder.holderCar.getColorz();
                    carPlateToUpload = finalHolder.holderCar.getPlateNum();

                    Log.i("values", carBrandToUpload);

                    finalSelecId = finalHolder.holderCar.getItemId(); //I think here is how to do it

                    String carSelectedHolder = carBrandToUpload + " " + carModelToUpload;
                    cCarSelected.setText(carSelectedHolder);
                    carChecked = true;
                    enableBtnIfReady();

                    //Hide listView-------------------
                    cAddcar.setVisibility(View.GONE);
                    cCarList.setVisibility(View.GONE);
                    cSpinnerArrow.setRotation(360);
                    carListVisible = false;
                    //--------------------------------

                    hideKeyboardTwo();

                }
            });

            final int mid = finalHolder.holderCar.getItemId();

            row.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {

                    deleteCarDialog = new AlertDialog.Builder(WashReqParamsActivity. this);

                    //set message
                    deleteCarDialog.setMessage(getResources()
                            .getString(R.string.washMyCar_act_java_deleteCar));
                    //set delete
                    deleteCarDialog.setPositiveButton(getResources()
                            .getString(R.string.washMyCar_act_java_yes),
                            new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dbh.deleteCar(mid);
                            dbCars.remove(position);
                            cCarSelected.setHint(getResources().getString(R.string
                                    .washMyCar_act_java_chooseACar));
                            setEmptyListText();
                            setEmptyCarSelectedView();
                            notifyDataSetChanged();
                        }
                    });

                    //set cancel
                    deleteCarDialog.setNegativeButton(getResources().getString(R.string
                            .act_wash_my_car_cancel), null);

                    //Create Dialog
                    AlertDialog alertD = deleteCarDialog.create();

                    //Show Dialog
                    alertD.show();

                    return false;
                }
            });

            return row;

        }

    }

    class ViewHolder{

        MyCar holderCar;

        TextView mBrand;
        TextView mModel;
        TextView mColor;
        TextView mPlate;
    }

    public void showBadge(int drawable){
        GlideApp
                .with(WashReqParamsActivity.this)
                .load(drawable)
                .timeout(20000)
                .into(cTheRating);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(cRatingAndPricingRelative.getVisibility() == View.VISIBLE) {

                cFirstRelative.setClickable(true);
                cElPropioRelative.setClickable(true);
                cCrLocationDescriptionEdit.setVisibility(View.VISIBLE);
                cCrLocationEdit.setVisibility(View.VISIBLE);
                cCrUntilEdit.setVisibility(View.VISIBLE);
                cCarSelected.setVisibility(View.VISIBLE);
                //cCrServicesEdit.setVisibility(View.VISIBLE);
                cCrServicesEdit.setVisibility(View.GONE);
                cCarList.setVisibility(View.GONE);
                cSpinnerArrow.setVisibility(View.VISIBLE);
                cSaveRequest.setVisibility(View.VISIBLE);
                cRatingAndPricingRelative.setVisibility(View.GONE);
                cRatingAndPricingRelative.animate().translationXBy(1000f).setDuration(500);

            } else {
                writeLocationDescToFile("");//test
                startActivity(new Intent(WashReqParamsActivity.this
                        , HomeActivity.class));
            }
        }
        /*
        if (keyCode == KeyEvent.KEYCODE_HOME) {

            //Do nothing for now

        }
        */
        return false;
    }
}
