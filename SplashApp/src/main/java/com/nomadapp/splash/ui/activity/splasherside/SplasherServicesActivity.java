package com.nomadapp.splash.ui.activity.splasherside;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.ProfileClassQuery;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.sysmsgs.DialogAcceptInterface;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.questiondialogs.AlertDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class SplasherServicesActivity extends AppCompatActivity {

    private Context ctx = SplasherServicesActivity.this;

    //---service prices---//
    private EditText mExternalPrice, mExtIntPrice, mMotorcyclePrice;
    private Button mSaveServices;
    //---//////////////---//

    //---service area---//
    private TextView mSelectRangeAction;
    //---////////////---//

    private String actualExt;
    private String actualExtInt;
    private String actualMoto;

    private ToastMessages toastMessages = new ToastMessages();
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(ctx);
    private ProfileClassQuery profileClassQuery = new ProfileClassQuery(ctx);

    private static final int REQUEST_GET_MAP_LOCATION = 0;

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

        widgetState(false,R.drawable.btn_shape_grey);
        fetchServicesInfoFromServer();
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

    public void fetchServicesInfoFromServer(){
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

                            mExternalPrice.setText(actualExt);
                            mExtIntPrice.setText(actualExtInt);
                            mMotorcyclePrice.setText(actualMoto);
                        }
                    }
                }
            }
        });
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
                || rangeText.isEmpty()){
            boxedLoadingDialog.hideLoadingDialog();
            toastMessages.productionMessage(ctx,getResources().getString(R.string
                    .splasher_services_pleaseSetAll),1);
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
                        finish();
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
        AlertDialog alertDialog = new AlertDialog(ctx);
        alertDialog.generalPurposeQuestionDialog(ctx, getResources().getString(R.string
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
        AlertDialog alertDialog = new AlertDialog(ctx);
        alertDialog.generalPurposeQuestionDialog(ctx, getResources().getString(R.string
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

}
