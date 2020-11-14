package com.nomadapp.splash.ui.activity.splasherside;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.ProfileClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.sysmsgs.DialogAcceptInterface;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.CustomAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.ForcedAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Locale;

public class SplasherServicesActivity extends AppCompatActivity {

    private Context ctx = SplasherServicesActivity.this;

    //---service prices---//
    private EditText mExternalPrice, mExtIntPrice, mMotorcyclePrice;
    private TextView mRecommended_price_external, mRecommended_price_external_internal
            ,mRecommended_price_motorcycle;
    private Button mSaveServices;
    //---//////////////---//

    //---service area---//
    private TextView mSelectRangeAction;
    //---////////////---//

    private String actualExt, actualExtInt, actualMoto;

    double extSum = 0, extIntSum = 0, motoSum = 0;
    double extTotal = 0, extIntTotal = 0, motoTotal = 0;

    private ToastMessages toastMessages = new ToastMessages();
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(ctx);
    private ProfileClassQuery profileClassQuery = new ProfileClassQuery(ctx);

    private static final int REQUEST_GET_MAP_LOCATION = 0;

    private boolean firstTimeServices = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_services);
        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------
        mExternalPrice = findViewById(R.id.externalPrice);
        mExtIntPrice = findViewById(R.id.externalInternalPrice);
        mMotorcyclePrice = findViewById(R.id.motorcyclePrice);
        mSaveServices = findViewById(R.id.saveServices);
        mSelectRangeAction = findViewById(R.id.selectRangeAction);
        mRecommended_price_external = findViewById(R.id.Recommended_price_external);
        mRecommended_price_external_internal = findViewById
                (R.id.Recommended_price_external_internal);
        mRecommended_price_motorcycle = findViewById(R.id.Recommended_price_motorcycle);

        widgetState(false,R.drawable.btn_shape_grey);
        fetchServicesInfoFromServer();
        firstTimeServicesChecker();
        saveServiceSettingsFirst();
    }

    private void saveServiceSettingsFirst(){
        WriteReadDataInFile wrd = new WriteReadDataInFile(SplasherServicesActivity.this);
        wrd.writeToFile("ran","firstTimeServices");
    }

    public void selectArea(){
        Intent intent = new Intent(SplasherServicesActivity.this
                ,HomeActivity.class);
        intent.putExtra("fromServices","services");
        startActivityForResult(intent, REQUEST_GET_MAP_LOCATION);
    }

    public void widgetState(boolean state, int bgType){
        //btn blue: R.drawable.btn_shape
        //btn grey: R.drawable.btn_shape_grey

        mSelectRangeAction.setClickable(state);
        mExternalPrice.setClickable(state);
        mExternalPrice.setEnabled(state);

        mExtIntPrice.setClickable(state);
        mExtIntPrice.setEnabled(state);

        mMotorcyclePrice.setClickable(state);
        mMotorcyclePrice.setEnabled(state);

        mSaveServices.setClickable(state);
        mSaveServices.setEnabled(state);
        mSaveServices.setBackgroundResource(bgType);
    }

    public void updateChanges(View v){
        sendUpdatedPriceToServer();
    }

    private void fetchServicesInfoFromServer(){
        profileClassQuery.getUserProfileToUpdate(new ProfileClassInterface() {
            @Override
            public void beforeQueryFetched() {

            }
            @Override
            public void updateChanges(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject profileObj : objects){
                            actualExt = profileObj.getString("setPrice");
                            actualExtInt = profileObj.getString("setPriceEInt");
                            actualMoto = profileObj.getString("setPriceMoto");

                            String actualRange = profileObj.getString("serviceRange");
                            String actualECA = profileObj.getString("serviceEC");

                            areaECPlacer(actualECA,actualRange);

                            mExternalPrice.setText(decimalPlaceConcatenator(actualExt));
                            mExtIntPrice.setText(decimalPlaceConcatenator(actualExtInt));
                            mMotorcyclePrice.setText(decimalPlaceConcatenator(actualMoto));
                        }
                    }
                }
            }
        });
        marketPriceOps();
    }

    private void marketPriceOps(){
        UserClassQuery userClassQuery = new UserClassQuery(ctx);
        profileClassQuery.fetchAllSplashersInfo(userClassQuery.userName(),
                new ProfileClassInterface.allSplashersInfo() {
                    @Override
                    public void getInfo(ParseObject object) {
                        //ANYTHING IN HERE, IS INSIDE THE LOOP
                        extSum += Double.parseDouble(object.getString("setPrice"));
                        extIntSum += Double.parseDouble(object.getString("setPriceEInt"));
                        motoSum += Double.parseDouble(object.getString("setPriceMoto"));
                    }
                    @Override
                    public void afterLoop(List<ParseObject> objects) {
                        //ANYTHING IN HERE, IS AFTER THE LOOP
                        extTotal = Math.round((extSum / objects.size()) * 10) / 10.0;
                        extIntTotal  = Math.round((extIntSum / objects.size() * 10) / 10.0);
                        motoTotal = Math.round((motoSum / objects.size() * 10) / 10.0);

                        mRecommended_price_external.setText(currencyDirAllocator(String
                                .valueOf(extTotal)));
                        mRecommended_price_external_internal.setText(currencyDirAllocator(String
                                .valueOf(extIntTotal)));
                        mRecommended_price_motorcycle.setText(currencyDirAllocator(String
                                .valueOf(motoTotal)));
                    }
                });
    }

    private String currencyDirAllocator(String string){
        String shekel = getResources().getString(R.string.shekel);
        String string_with_currency;
        Log.i("blue3",Locale.getDefault().getDisplayLanguage());
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "עברית":
                string_with_currency = shekel + " " + string;
                break;
            case "English":
                string_with_currency = string + " " + shekel;
                break;
            default:
                string_with_currency = string + " " + shekel;
                break;
        }
        return string_with_currency;
    }

    private String decimalPlaceConcatenator(final String data){
        String finalForm;
        if (!data.contains(".")){
            finalForm = data + ".0";
        }else{
            finalForm = data;
        }
        return finalForm;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GET_MAP_LOCATION && resultCode == Activity.RESULT_OK) {
            String rangeText = data.getStringExtra("centerAdd");
            String epicenterText = data.getStringExtra("range");
            areaECPlacer(epicenterText, rangeText);
        }
    }

    private void areaECPlacer(String epicenter, String range){
        String area = epicenter + " - " + range;
        mSelectRangeAction.setGravity(Gravity.CENTER);
        mSelectRangeAction.setText(area);
    }

    public void sendUpdatedPriceToServer(){
        boxedLoadingDialog.showLoadingDialog();

        mExternalPrice.setText(decimalPlaceConcatenator(mExternalPrice.getText().toString()));
        mExtIntPrice.setText(decimalPlaceConcatenator(mExtIntPrice.getText().toString()));
        mMotorcyclePrice.setText(decimalPlaceConcatenator(mMotorcyclePrice.getText().toString()));

        profileClassQuery.getUserProfileToUpdate(new ProfileClassInterface() {
            @Override
            public void beforeQueryFetched() {

            }
            @Override
            public void updateChanges(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject profileObj : objects){
                            getDataFromWidgetsToUpdate(profileObj);
                        }
                    }
                }
            }
        });
    }

    public void getDataFromWidgetsToUpdate(ParseObject obj){
        String rangeText = mSelectRangeAction.getText().toString();
        String textExt = mExternalPrice.getText().toString();
        String textExtI = mExtIntPrice.getText().toString();
        String textMoto = mMotorcyclePrice.getText().toString();

        if (textExt.equals(actualExt) && textExtI.equals(actualExtInt)
                && textMoto.equals(actualMoto)) {
            //dialog or toast
            boxedLoadingDialog.hideLoadingDialog();
            toastMessages.productionMessage(ctx, getResources().getString(R.string
                    .splasher_services_cantUpdateSamePrice), 1);
        }else if((textExt.equals("0") || textExt.equals("00"))
                || (textExtI.equals("0") || textExtI.equals("00"))
                || (textMoto.equals("0") || textMoto.equals("00"))) {
            boxedLoadingDialog.hideLoadingDialog();
            toastMessages.productionMessage(ctx, getResources().getString(R.string
                    .splasher_services_pricesCantBeZero), 1);
        }else if(textExt.isEmpty() || textExtI.isEmpty() || textMoto.isEmpty()
                || rangeText.isEmpty()) {
            boxedLoadingDialog.hideLoadingDialog();
            toastMessages.productionMessage(ctx, getResources().getString(R.string
                    .splasher_services_pleaseSetAll), 1);
        }else if(Double.parseDouble(mExternalPrice.getText().toString()) > 99.9 ||
                 Double.parseDouble(mExtIntPrice.getText().toString()) > 99.9 ||
                 Double.parseDouble(mMotorcyclePrice.getText().toString()) > 99.9){
            boxedLoadingDialog.hideLoadingDialog();
            ForcedAlertDialog forcedAlertDialog = new ForcedAlertDialog(ctx);
            forcedAlertDialog.generalPurposeForcedDialogNoAction(
                    getResources().getString(R.string.splasher_services_priceOver)
                    , getResources().getString(R.string.splasher_services_PricesCannotGo)
                    ,getResources().getString(R.string.splasher_services_ok));
        }else{
            obj.put("setPrice",textExt);
            obj.put("setPriceEInt",textExtI);
            obj.put("setPriceMoto",textMoto);
            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        toastMessages.productionMessage(ctx,getResources().getString(R.string
                                .splasher_services_servicesUpdated),1);
                        if (!firstTimeServices) {
                            finish();
                        }else{
                            startActivity(new Intent(SplasherServicesActivity.this
                                    , HomeActivity.class));
                        }
                    }else{
                        toastMessages.productionMessage(ctx,getResources().getString(R.string
                                .splasher_services_thereWasAProblem),1);
                    }
                }
            });
        }
    }

    public void editPrices(View v){
        //when clicked, dialog, enable widget state then edit
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(ctx);
        customAlertDialog.generalPurposeQuestionDialog(ctx, getResources().getString(R.string
                .splasher_services_dialogTitle), getResources().getString(R.string
                .splasher_services_dialogMsg), getResources().getString(R.string
                .splasher_services_dialogYes), getResources().getString(R.string
                .splasher_services_dialogNo), new DialogAcceptInterface() {
            @Override
            public void onAcceptOption() {
                widgetState(true,R.drawable.btn_shape);
                mExternalPrice.requestFocus();
                mExternalPrice.setText("");
            }
        });
    }

    public void editServiceArea(View v){
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(ctx);
        customAlertDialog.generalPurposeQuestionDialog(ctx, getResources().getString(R.string
                .splasher_services_dialogAreaTitle), getResources().getString(R.string
                .splasher_services_dialogAreaMsg), getResources().getString(R.string
                .splasher_services_dialogAreaYes), getResources().getString(R.string
                .splasher_services_dialogAreaNo), new DialogAcceptInterface() {
            @Override
            public void onAcceptOption() {
                selectArea();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void firstTimeServicesChecker(){
        Bundle keyToKeep = getIntent().getExtras();
        if (keyToKeep != null) {
            String keyToOpen = keyToKeep.getString("signup");
            if (keyToOpen != null && keyToOpen.equals("redirect")) {
                firstTimeServices = true;
            }
        }
    }
}
