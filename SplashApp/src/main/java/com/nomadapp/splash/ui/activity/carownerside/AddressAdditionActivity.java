package com.nomadapp.splash.ui.activity.carownerside;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.R;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Locale;

import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

import static com.nomadapp.splash.R.id.homeAddress;
import static com.nomadapp.splash.R.id.workAddress;
import static com.nomadapp.splash.R.id.otherAddress1;
import static com.nomadapp.splash.R.id.otherAddress2;
import static com.nomadapp.splash.R.id.otherAddress3;

public class AddressAdditionActivity extends AppCompatActivity {

    private TextView cHomeAddress, cWorkAddress, cOtherAddress1, cOtherAddress2, cOtherAddress3;

    private ParseUser currentUser = new ParseUser();

    private ToastMessages toastMessages = new ToastMessages();

    private WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(AddressAdditionActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_addition);

        Button cSaveLanguage = findViewById(R.id.saveLanguage);
        cHomeAddress = findViewById(R.id.homeAddress);
        cWorkAddress = findViewById(R.id.workAddress);
        cOtherAddress1 = findViewById(R.id.otherAddress1);
        cOtherAddress2 = findViewById(R.id.otherAddress2);
        cOtherAddress3 = findViewById(R.id.otherAddress3);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        //--------------------------------

        //Read back from file if such file exists:--//
        if(!writeReadDataInFile.readFromFile("stringHome").isEmpty()){
            cHomeAddress.setText(writeReadDataInFile.readFromFile("stringHome"));
        }
        if(!writeReadDataInFile.readFromFile("stringWork").isEmpty()){
            cWorkAddress.setText(writeReadDataInFile.readFromFile("stringWork"));
        }
        if(!writeReadDataInFile.readFromFile("stringOther1").isEmpty()){
            cOtherAddress1.setText(writeReadDataInFile.readFromFile("stringOther1"));
        }
        if(!writeReadDataInFile.readFromFile("stringOther2").isEmpty()){
            cOtherAddress2.setText(writeReadDataInFile.readFromFile("stringOther2"));
        }
        if(!writeReadDataInFile.readFromFile("stringOther3").isEmpty()){
            cOtherAddress3.setText(writeReadDataInFile.readFromFile("stringOther3"));
        }
        //------------------------------------------//
        cSaveLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser != null) {
                    final ParseQuery<ParseObject> addressQuery = ParseQuery.getQuery("Addresses");
                    addressQuery.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());
                    addressQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e == null){
                                if(objects.size() > 0){
                                    for(ParseObject object : objects){
                                        if(!(cHomeAddress.getText().toString().isEmpty())) {
                                            object.put("HomeAddress", cHomeAddress.getText().toString());
                                        }
                                        if(!(cWorkAddress.getText().toString().isEmpty())) {
                                            object.put("WorkAddress", cWorkAddress.getText().toString());
                                        }
                                        if(!(cOtherAddress1.getText().toString().isEmpty())) {
                                            object.put("OtherAddress1", cOtherAddress1.getText().toString());
                                        }
                                        if(!(cOtherAddress2.getText().toString().isEmpty())) {
                                            object.put("OtherAddress2", cOtherAddress2.getText().toString());
                                        }
                                        if(!(cOtherAddress3.getText().toString().isEmpty())) {
                                            object.put("OtherAddress3", cOtherAddress3.getText().toString());
                                        }
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null) {
                                                    toastMessages.productionMessage(getApplicationContext()
                                                    ,"Addresses saved",1);
                                                    startActivity(new Intent(AddressAdditionActivity.this, HomeActivity.class));
                                                }else{
                                                    toastMessages.productionMessage(getApplicationContext()
                                                    ,e.getMessage() + " " + getResources()
                                                    .getString(R.string.addAddress_act_java_tryAgain)
                                                    ,1);
                                                }
                                            }
                                        });
                                    }
                                }
                            }else{
                                toastMessages.productionMessage(getApplicationContext()
                                        ,e.getMessage() + " " + getResources()
                                                .getString(R.string.addAddress_act_java_tryAgain)
                                        ,1);
                            }
                        }
                    });
                }else{
                    toastMessages.productionMessage(getApplicationContext()
                            ,currentUser.toString() + " " + getResources()
                                    .getString(R.string.addAddress_act_java_tryAgain)
                            ,1);
                }
            }
        });
        if(Locale.getDefault().getDisplayLanguage().equals("עברית")) {

            cHomeAddress.setGravity(Gravity.END);
            cHomeAddress.setGravity(Gravity.RIGHT);
            cHomeAddress.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cWorkAddress.setGravity(Gravity.END);
            cWorkAddress.setGravity(Gravity.RIGHT);
            cWorkAddress.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cOtherAddress1.setGravity(Gravity.END);
            cOtherAddress1.setGravity(Gravity.RIGHT);
            cOtherAddress1.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cOtherAddress2.setGravity(Gravity.END);
            cOtherAddress2.setGravity(Gravity.RIGHT);
            cOtherAddress2.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cOtherAddress3.setGravity(Gravity.END);
            cOtherAddress3.setGravity(Gravity.RIGHT);
            cOtherAddress3.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        }else if(Locale.getDefault().getDisplayLanguage().equals("English")){

            cHomeAddress.setGravity(Gravity.START);
            cHomeAddress.setGravity(Gravity.LEFT);
            cHomeAddress.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cWorkAddress.setGravity(Gravity.START);
            cWorkAddress.setGravity(Gravity.LEFT);
            cWorkAddress.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cOtherAddress1.setGravity(Gravity.START);
            cOtherAddress1.setGravity(Gravity.LEFT);
            cOtherAddress1.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cOtherAddress2.setGravity(Gravity.START);
            cOtherAddress2.setGravity(Gravity.LEFT);
            cOtherAddress2.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cOtherAddress3.setGravity(Gravity.START);
            cOtherAddress3.setGravity(Gravity.LEFT);
            cOtherAddress3.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void addAddress(View v){
        switch (v.getId()){
            case homeAddress:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(AddressAdditionActivity.this);
                    startActivityForResult(intent, 1);//<--1
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case workAddress:
                PlacePicker.IntentBuilder builder2 = new PlacePicker.IntentBuilder();
                Intent intent2;
                try {
                    intent2 = builder2.build(AddressAdditionActivity.this);
                    startActivityForResult(intent2, 2);//<--1
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case otherAddress1:
                PlacePicker.IntentBuilder builder3 = new PlacePicker.IntentBuilder();
                Intent intent3;
                try {
                    intent3 = builder3.build(AddressAdditionActivity.this);
                    startActivityForResult(intent3, 3);//<--1
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case otherAddress2:
                PlacePicker.IntentBuilder builder4 = new PlacePicker.IntentBuilder();
                Intent intent4;
                try {
                    intent4 = builder4.build(AddressAdditionActivity.this);
                    startActivityForResult(intent4, 4);//<--1
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case otherAddress3:
                PlacePicker.IntentBuilder builder5 = new PlacePicker.IntentBuilder();
                Intent intent5;
                try {
                    intent5 = builder5.build(AddressAdditionActivity.this);
                    startActivityForResult(intent5, 5);//<--1
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //Add saving feature to "Save" button instead
    //craft UI and code to be able to fetch saved addresses from the carLocation section at WashReqParamsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this, data);
                    String stringHome = String.format("%s", place.getAddress());
                    cHomeAddress.setText(stringHome);
                    Double doubleHomelat = place.getLatLng().latitude;
                    Double doubleHomelon = place.getLatLng().longitude;
                    String homeLat = doubleHomelat.toString();
                    String homeLon = doubleHomelon.toString();
                    writeReadDataInFile.writeToFile(stringHome, "stringHome");
                    writeReadDataInFile.writeToFile(homeLat, "homeLat");
                    writeReadDataInFile.writeToFile(homeLon, "homeLon");
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this, data);
                    String stringWork = String.format("%s", place.getAddress());
                    cWorkAddress.setText(stringWork);
                    Double doubleWorklat = place.getLatLng().latitude;
                    Double doubleWorklon = place.getLatLng().longitude;
                    String workLat = doubleWorklat.toString();
                    String workLon = doubleWorklon.toString();
                    writeReadDataInFile.writeToFile(stringWork, "stringWork");
                    writeReadDataInFile.writeToFile(workLat, "workLat");
                    writeReadDataInFile.writeToFile(workLon, "workLon");
                }
                break;
            case 3:
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this, data);
                    String stringOther1 = String.format("%s", place.getAddress());
                    cOtherAddress1.setText(stringOther1);
                    Double doubleOther1lat = place.getLatLng().latitude;
                    Double doubleOther1lon = place.getLatLng().longitude;
                    String otherLat1 = doubleOther1lat.toString();
                    String otherLon1 = doubleOther1lon.toString();
                    writeReadDataInFile.writeToFile(stringOther1, "stringOther1");
                    writeReadDataInFile.writeToFile(otherLat1, "otherLat1");
                    writeReadDataInFile.writeToFile(otherLon1, "otherLon1");
                }
                break;
            case 4:
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this, data);
                    String stringOther2 = String.format("%s", place.getAddress());
                    cOtherAddress2.setText(stringOther2);
                    Double doubleOther2lat = place.getLatLng().latitude;
                    Double doubleOther2lon = place.getLatLng().longitude;
                    String otherLat2 = doubleOther2lat.toString();
                    String otherLon2 = doubleOther2lon.toString();
                    writeReadDataInFile.writeToFile(stringOther2, "stringOther2");
                    writeReadDataInFile.writeToFile(otherLat2, "otherLat2");
                    writeReadDataInFile.writeToFile(otherLon2, "otherLon2");
                }
                break;
            case 5:
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this, data);
                    String stringOther3 = String.format("%s", place.getAddress());
                    cOtherAddress3.setText(stringOther3);
                    Double doubleOther3lat = place.getLatLng().latitude;
                    Double doubleOther3lon = place.getLatLng().longitude;
                    String otherLat3 = doubleOther3lat.toString();
                    String otherLon3 = doubleOther3lon.toString();
                    writeReadDataInFile.writeToFile(stringOther3, "stringOther3");
                    writeReadDataInFile.writeToFile(otherLat3, "otherLat3");
                    writeReadDataInFile.writeToFile(otherLon3, "otherLon3");
                }
                break;
        }
    }

}
