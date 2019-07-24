package com.nomadapp.splash.ui.activity.carownerside;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;

import com.nomadapp.splash.model.objects.CarOwnerRequest;
import com.nomadapp.splash.model.objects.users.SplasherSelector;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.RequestClassSend;
import com.nomadapp.splash.ui.activity.carownerside.payment.PaymentSettingsActivity;
import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.fragment.SplasherListFragment;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.ForcedAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.nomadapp.splash.utils.sysmsgs.ConnectionLost;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

import java.util.Arrays;

public class WashReqParamsActivity extends AppCompatActivity implements
        SplasherListFragment.OnFragmentInteractionListener{

    //Location, Time, Services
    private GridLayout mCrServicesGridLayout;
    private Button mExternalButton, mExtIntButton, mMotoButton;
    private String externalWash;
    private String intExtWash;
    private String motorcycle;

    private static LatLng carCoordinates;
    private double coords_lat, coords_lon;
    private static String getServiceType;
    private String[] splasherswanted;
    private String[] ogPricesWanted;
    private String[] pricesWanted;
    private String requestType = "";

    //Rating and pricing
    private WriteReadDataInFile writeReadDataInFile =
            new WriteReadDataInFile(WashReqParamsActivity.this);
    public Button cFinallyOrder;
    private CheckBox mSelectAll_checkbox;

    private boolean internalWashedPicked = false;

    //others
    private RelativeLayout cElPropioRelative;
    private RelativeLayout mSplasher_fragment_container;

    private int splashDarkBlue;
    private int white;

    public static boolean individuallyChecked = false;
    public static boolean allListSelected = false;

    private ToastMessages toastMessages = new ToastMessages();
    private ConnectionLost clm = new ConnectionLost(WashReqParamsActivity.this);
    private BoxedLoadingDialog boxedLoadingDialog =
            new BoxedLoadingDialog(WashReqParamsActivity.this);
    private MetricsClassQuery metricsClassQuery =
            new MetricsClassQuery(WashReqParamsActivity.this);
    private SplasherListFragment splasherListFragment = new SplasherListFragment();
    private ForcedAlertDialog forcedAlertDialog =
            new ForcedAlertDialog(WashReqParamsActivity.this);
    private SplasherSelector splasherSelector =
            new SplasherSelector(WashReqParamsActivity.this);
    private CarOwnerRequest carOwnerRequest = new CarOwnerRequest();

    //Getters//
    public LatLng getCarCoordinates(){
        return carCoordinates;
    }
    public String getGetServiceType() {
        return getServiceType;
    }

    private MenuItem refreshItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splasher_list_menu, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        } else if (item.getItemId() == R.id.action_refresh) {
            Log.i("black1", "ran");
            unselectAllSplashersOps();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_req_params);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mCrServicesGridLayout = findViewById(R.id.crServicesGridLayout);
        mExternalButton = findViewById(R.id.externalButton);
        mExtIntButton = findViewById(R.id.extIntButton);
        mMotoButton = findViewById(R.id.motoButton);

        cElPropioRelative = findViewById(R.id.elPropioRelative);
        mSplasher_fragment_container = findViewById(R.id.splasher_fragment_container);

        cFinallyOrder = findViewById(R.id.finallyOrder);
        mSelectAll_checkbox = findViewById(R.id.selectAll_checkbox);

        splashDarkBlue = getResources().getColor(R.color.ColorPrimaryDark);
        white = getResources().getColor(R.color.colorRatingBarSplasher);

        //Payments
        //TODO: Handling 'know-how' for app to recognize where data comes from 1
        Log.i("Hi", "Checking");//startActivityForResult() and OnActivityResult()

        externalWash = getResources().getString(R.string.act_wash_my_car_externalWash);
        intExtWash = getResources().getString(R.string.act_wash_my_car_extAndIntWash);
        motorcycle = getResources().getString(R.string.act_wash_my_car_motorcycle);

        showSecondStageParams();

        //Load Request to Parse Server 1
        cFinallyOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (SplasherSelector.COLLECTOR_LIST.size() == 0) {
                    Log.i("orange3", String.valueOf(SplasherSelector.COLLECTOR_LIST.size()));
                    forcedAlertDialog.generalPurposeForcedDialogNoAction(
                            getResources().getString(R.string.washMyCar_act_java_noSplasherSelected)
                            , getResources().getString(R.string.washMyCar_act_java_youMustSelectAtLeast)
                            , getResources().getString(R.string.washMyCar_act_java_ok));
                } else {
                    if ((writeReadDataInFile.readFromFile("buyer_key_temporal")
                            .isEmpty()) && (writeReadDataInFile
                            .readFromFile("buyer_key_permanent").isEmpty())) {
                        toastMessages.productionMessage(getApplicationContext()
                                , getResources().getString(R.string.act_wash_my_car_pleaseChoosePaymentM)
                                , 1);
                        startActivity(new Intent(WashReqParamsActivity.this
                                , PaymentSettingsActivity.class));
                    } else {
                        splasherswanted = splasherSelector.getCollectorArrayToSend();
                        ogPricesWanted = splasherSelector.getRespectiveOgPricesArrayToSend();
                        pricesWanted = splasherSelector.getRespectivePricesArrayToSend();
                        Log.i("orange", Arrays.asList(splasherswanted).toString());
                        if (SplasherSelector.COLLECTOR_LIST.size() == 1) {
                            requestType = "private";
                        } else if (SplasherSelector.COLLECTOR_LIST.size() >= 2) {
                            requestType = "public";
                        }
                        if (allListSelected) {
                            metricsClassQuery.queryMetricsToUpdate("allSplashers");
                        }
                        boxedLoadingDialog.showLoadingDialog();
                        RequestClassSend requestClassSend = new RequestClassSend
                                (WashReqParamsActivity.this);
                        setFinalDataForRequest();
                        allDataForRequest();
                        writeDataToFile();
                        requestClassSend.loadRequest(carOwnerRequest.getAddress(), carOwnerRequest.getAddressCoords()
                                , carOwnerRequest.getAddressDetails(), carOwnerRequest.getCarUntilTime()
                                , carOwnerRequest.getServiceType(), carOwnerRequest.getCarBrand()
                                , carOwnerRequest.getCarModel(), carOwnerRequest.getCarColor()
                                , carOwnerRequest.getCarPlateNum(), carOwnerRequest.getInitialSetPrice()
                                , carOwnerRequest.isTemporalKeyActive(), carOwnerRequest.getSplashersWanted()
                                , carOwnerRequest.getOgPricesWanted(), carOwnerRequest.getPricesWanted()
                                , carOwnerRequest.getSplasherUsername(), carOwnerRequest.getSplasherShowingName()
                                , carOwnerRequest.getRequestType());
                    }
                }
            }
        });

        clm.connectivityStatus(WashReqParamsActivity.this);

        serviceGridOperations();
        finallyOrderBtnInactiveBg();

        //Empty Splashers collection as soon as activity starts
        splasherSelector.deleteSplasherCollection();

        showSecondStageParams();
        getDataForRequest();
    }

    private void getDataForRequest(){
        Bundle request_extras = getIntent().getExtras();
        if (request_extras != null) {
            coords_lat = request_extras.getDouble("address_coords_lat");
            coords_lon = request_extras.getDouble("address_coords_lon");
            carCoordinates = new LatLng(coords_lat, coords_lon);
            String address = request_extras.getString("address");
            String address_details = request_extras.getString("address_details");

            String until_time = request_extras.getString("until_time");

            String brand = request_extras.getString("carBrand");
            String model = request_extras.getString("carModel");
            String color = request_extras.getString("carColor");
            String plate = request_extras.getString("carPlate");

            carOwnerRequest.setAddress(address);
            carOwnerRequest.setAddressCoords(carCoordinates);
            carOwnerRequest.setAddressDetails(address_details);

            carOwnerRequest.setCarUntilTime(until_time);
            carOwnerRequest.setCarBrand(brand);
            carOwnerRequest.setCarModel(model);
            carOwnerRequest.setCarColor(color);
            carOwnerRequest.setCarPlateNum(plate);
        }
        Log.i("carCoords", "is " + carCoordinates);
        if(carCoordinates != null) {
            getServiceType = externalWash;
            splasherListFragmentInit();
        }
    }

    private void setFinalDataForRequest(){
        String initialSetPrice = "0";
        carOwnerRequest.setServiceType(getServiceType);
        carOwnerRequest.setInitialSetPrice(initialSetPrice);
        carOwnerRequest.setTemporalKeyActive(false);
        carOwnerRequest.setSplashersWanted(splasherswanted);
        carOwnerRequest.setPricesWanted(pricesWanted);
        carOwnerRequest.setOgPricesWanted(ogPricesWanted);
        String splasherUsername = "clear";
        carOwnerRequest.setSplasherUsername(splasherUsername);
        String splasherShowingName = "clear";
        carOwnerRequest.setSplasherShowingName(splasherShowingName);
        carOwnerRequest.setRequestType(requestType);
    }

    private void allDataForRequest() {
        Log.i("rawRequestAddress", carOwnerRequest.getAddress());
        Log.i("rawRequestAddressCoords", String.valueOf(carOwnerRequest.getAddressCoords()));
        Log.i("rawRequestAddressDets", carOwnerRequest.getAddressDetails());
        Log.i("rawRequestUntilTime", carOwnerRequest.getCarUntilTime());
        Log.i("rawRequestBrand", carOwnerRequest.getCarBrand());
        Log.i("rawRequestModel", carOwnerRequest.getCarModel());
        Log.i("rawRequestColor", carOwnerRequest.getCarColor());
        Log.i("rawRequestPlate", carOwnerRequest.getCarPlateNum());
        Log.i("rawRequestInitialPrice", carOwnerRequest.getInitialSetPrice());
        Log.i("rawRequestTemporalKey", String.valueOf(carOwnerRequest.isTemporalKeyActive()));
        for (String splasher : carOwnerRequest.getSplashersWanted()){
            Log.i("rawRequestSplashersWant",splasher);
        }
        for (String price : carOwnerRequest.getPricesWanted()){
            Log.i("rawRequestPricesWanted",price);
        }
        for (String ogPrice : carOwnerRequest.getOgPricesWanted()) {
            Log.i("rawRequestOgPricesWant",ogPrice);
        }
        Log.i("rawRequestSplasherUser",carOwnerRequest.getSplasherUsername());
        Log.i("rawRequestSplasherShow",carOwnerRequest.getSplasherShowingName());
        Log.i("rawRequestReqType",carOwnerRequest.getRequestType());

        //ADDRESS AND ADDRESS DETAILS DID NOT MAKE IT THROUGH. FIND OUT WHY
    }

    private void writeDataToFile(){
        WriteReadDataInFile wr = new WriteReadDataInFile(WashReqParamsActivity.this);
        wr.writeToFile(String.valueOf(coords_lat), "lat");
        wr.writeToFile(String.valueOf(coords_lon), "lon");
    }

    private void showSecondStageParams(){
        Log.i("fragVisible",String.valueOf(isFragmentVisible()));
        if (isFragmentVisible()){
            cFinallyOrder.setVisibility(View.VISIBLE);
        }else {
            cFinallyOrder.setVisibility(View.VISIBLE);
        }
        cElPropioRelative.setClickable(false);
        if (refreshItem != null){
            refreshItem.setVisible(true);
        }
    }

    private void gridLayoutState(boolean state){
        mCrServicesGridLayout.setClickable(state);
        mCrServicesGridLayout.setEnabled(state);
    }

    public void serviceGridOperations(){
        flipBgServiceBtn(mExternalButton,splashDarkBlue,white);
        mExternalButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                individuallyChecked = false;
                cFinallyOrder.setEnabled(true);
                cFinallyOrder.setClickable(true);
                gridLayoutState(false);
                flipBgServiceBtn(mExternalButton,splashDarkBlue,white);
                setWhiteBgBlueStroke(mExtIntButton,splashDarkBlue);
                setWhiteBgBlueStroke(mMotoButton,splashDarkBlue);
                getServiceType = externalWash;
                unselectAllSplashersOps();
                mSelectAll_checkbox.setChecked(false);
                gridLayoutState(true);
            }
        });
        mExtIntButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hideKeyboardTwo();
                individuallyChecked = false;
                if (!internalWashedPicked) {
                    updateInternalWashCounter();
                    internalWashedPicked = true;
                }
                gridLayoutState(false);
                setWhiteBgBlueStroke(mExternalButton,splashDarkBlue);
                flipBgServiceBtn(mExtIntButton,splashDarkBlue,white);
                setWhiteBgBlueStroke(mMotoButton,splashDarkBlue);
                getServiceType = intExtWash;
                unselectAllSplashersOps();
                mSelectAll_checkbox.setChecked(false);
                gridLayoutState(true);
            }
        });
        mMotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                individuallyChecked = false;
                cFinallyOrder.setEnabled(true);
                cFinallyOrder.setClickable(true);
                gridLayoutState(false);
                setWhiteBgBlueStroke(mExternalButton,splashDarkBlue);
                setWhiteBgBlueStroke(mExtIntButton,splashDarkBlue);
                flipBgServiceBtn(mMotoButton,splashDarkBlue,white);
                getServiceType = motorcycle;
                unselectAllSplashersOps();
                mSelectAll_checkbox.setChecked(false);
                gridLayoutState(true);
            }
        });
        //Select All Splashers
        mSelectAll_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                individuallyChecked = false;
                if (isChecked){
                    selectAllSplashersOps();
                }else{
                    unselectAllSplashersOps();
                }
            }
        });
    }

    private void selectAllSplashersOps(){
        allListSelected = true;
        splasherListFragment.getSplasherList(getServiceType,getResources()
                .getColor(R.color.ColorPrimary), getResources()
                .getColor(R.color.pureWhite));
        splasherSelector.addWholeSplasherColletion(splasherListFragment
                .getSplasherUserNames(), splasherListFragment.getSplasherUserPrice(),
                splasherListFragment.getCarOwnerUserPrice());
        Log.i("greenList", SplasherSelector.COLLECTOR_LIST.toString() + " - "
            + SplasherSelector.RESPECTIVE_OG_PRICES_LIST.toString() + " - "
            + SplasherSelector.RESPECTIVE_PRICES_LIST.toString());
        splasherSelector.splasherListCheckerToOrder(cFinallyOrder);
    }

    private void unselectAllSplashersOps(){
        allListSelected = false;
        splasherListFragment.getSplasherList(getServiceType,getResources()
                .getColor(R.color.pureWhite),getResources()
                .getColor(R.color.ColorPrimaryDark));
        splasherSelector.deleteSplasherCollection();
        Log.i("greenList1", SplasherSelector.COLLECTOR_LIST.toString());
        splasherSelector.splasherListCheckerToOrder(cFinallyOrder);
    }

    private void flipBgServiceBtn(Button button, int bgWhiteBlueStroke, int textColor){
        button.setBackgroundColor(bgWhiteBlueStroke);
        button.setTextColor(textColor);
    }

    private void setWhiteBgBlueStroke(Button button, int textColor){
        button.setBackgroundResource(R.drawable.selective_bg_filled_stroked);
        button.setTextColor(textColor);
    }

    public void updateInternalWashCounter(){
        metricsClassQuery.queryMetricsToUpdate("internalWash");
    }

    public void splasherListFragmentInit(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.splasher_fragment_container, splasherListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        if (isFragmentVisible()){
            cFinallyOrder.setVisibility(View.VISIBLE);
        }
    }

    public boolean isFragmentVisible(){
        return mSplasher_fragment_container.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Maybe for fragment interaction from this activity i might need to do something
    }

    public void finallyOrderBtnInactiveBg(){
        cFinallyOrder.setBackgroundResource(R.drawable.btn_shape_grey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toastMessages.debugMesssage(getApplicationContext(),"called",1);
        buyerKeyDeducer();
    }

    public void buyerKeyDeducer(){
        if(!writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){

            toastMessages.debugMesssage(getApplicationContext(),"there is a permanent key"
            ,1);

        }else if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){

            toastMessages.debugMesssage(getApplicationContext()
                    ,"there is a temporal key",1);

        }else if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){
            //This means both buyer_keys are empty
            toastMessages.debugMesssage(getApplicationContext()
            ,"there is no key, no saved credit card",1);

        }else{
            toastMessages.productionMessage(getApplicationContext()
                    ,"Error",1);
        }
    }

    public void hideKeyboardTwo(){
        try {
            InputMethodManager imm = (InputMethodManager) getApplicationContext()
                    .getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
