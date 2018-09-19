package com.nomadapp.splash.ui.activity.splasherside;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
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
    private TextView mSelectEpicenterAction;
    private TextView mSelectRangeAction;
    private int PLACE_PICKER_REQUEST2 = 2;
    private int MAP_RANGE_PICKER_REQUEST2 = 3;
    private String address;
    private LatLng epicenterCoordinates;
    private String epicenterName;
    private String epicenterLat;
    private String epicenterLon;
    //---////////////---//

    private String actualExt, actualExtInt, actualMoto;

    private ToastMessages toastMessages = new ToastMessages();
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(ctx);
    private ProfileClassQuery profileClassQuery = new ProfileClassQuery(ctx);
    private WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(ctx);

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

        mSelectEpicenterAction = findViewById(R.id.selectEpicenterAction);
        mSelectRangeAction = findViewById(R.id.selectRangeAction);

        widgetState(false,R.drawable.btn_shape_grey);
        fetchActualPriceFromServer();

        if (writeReadDataInFile.readFromFile("epicenterName") != null
                && writeReadDataInFile.readFromFile("epicenterLat") != null
                && writeReadDataInFile.readFromFile("epicenterLon") != null){
            readServiceEpicenterInfoFromFile();
        }
    }

    public void selectCenter(View v){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = builder.build(SplasherServicesActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST2);
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void selectRange(View v){
        if (mSelectEpicenterAction.getText().toString().isEmpty()
                || mSelectEpicenterAction.getText().toString().equals("")){
                toastMessages.productionMessage(ctx,"Please choose your service area's" +
                        " epicenter first",1);
        }else {
            writeServiceEpicenterInfoToFile();
            Intent intent = new Intent(SplasherServicesActivity.this
                    ,HomeActivity.class);
            intent.putExtra("fromServices","services");
            intent.putExtra("serviceCenterLat",epicenterLat);
            intent.putExtra("serviceCenterLon",epicenterLon);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == PLACE_PICKER_REQUEST2){
                Place place = PlacePicker.getPlace(this, data);
                address = String.format("%s", place.getAddress());//<--FINAL
                mSelectEpicenterAction.setText(address);
                epicenterCoordinates = place.getLatLng();//<--FINAL
                Double c1lat = place.getLatLng().latitude;
                Double c1lon = place.getLatLng().longitude;
                epicenterLat = c1lat.toString();
                epicenterLon = c1lon.toString();
                Log.i("Car Coordinates", String.valueOf(epicenterCoordinates));
                Log.i("car CoordLatSaved", String.valueOf(c1lat));
                Log.i("car CoordLonSaved", String.valueOf(c1lon));
                Log.i("car CoordLatSaved", epicenterLat);
                Log.i("car CoordLonSaved", epicenterLon);
            }else if(requestCode == MAP_RANGE_PICKER_REQUEST2){
                Log.i("fromSplasherMap", "we are here");
            }
        }
    }

    public void writeServiceEpicenterInfoToFile(){
        writeReadDataInFile.writeToFile(address, "epicenterName");
        writeReadDataInFile.writeToFile(epicenterLat, "epicenterLat");
        writeReadDataInFile.writeToFile(epicenterLon, "epicenterLon");
    }

    public void readServiceEpicenterInfoFromFile(){
        address = writeReadDataInFile.readFromFile("epicenterName");
        mSelectEpicenterAction.setText(address);
        epicenterLat = writeReadDataInFile.readFromFile("epicenterLat");
        epicenterLon = writeReadDataInFile.readFromFile("epicenterLon");
    }

    public void widgetState(boolean state, int bgType){
        //btn blue: R.drawable.btn_shape
        //btn grey: R.drawable.btn_shape_grey
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

    public void updatePrices(View v){
        sendUpdatedPriceToServer();
    }

    public void fetchActualPriceFromServer(){
        profileClassQuery.getUserProfileToUpdate(new ProfileClassInterface() {
            @Override
            public void updateChanges(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject profileObj : objects){
                            actualExt = profileObj.getString("setPrice");
                            actualExtInt = profileObj.getString("setPriceEInt");
                            actualMoto = profileObj.getString("setPriceMoto");
                            mExternalPrice.setText(actualExt);
                            mExtIntPrice.setText(actualExtInt);
                            mMotorcyclePrice.setText(actualMoto);
                        }
                    }
                }
            }
        });
    }

    public void sendUpdatedPriceToServer(){
        boxedLoadingDialog.showLoadingDialog();
        profileClassQuery.getUserProfileToUpdate(new ProfileClassInterface() {
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
        }else if(textExt.isEmpty() || textExtI.isEmpty() || textMoto.isEmpty()){
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
                                .splasher_services_pricesUpdated),1);
                        finish();
                    }else{
                        toastMessages.productionMessage(ctx,getResources().getString(R.string
                                .splasher_services_thereWasAProblem),1);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.services_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.services_action_edit){
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
        return super.onOptionsItemSelected(item);
    }
}
