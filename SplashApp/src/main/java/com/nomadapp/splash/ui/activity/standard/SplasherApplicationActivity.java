package com.nomadapp.splash.ui.activity.standard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nomadapp.splash.model.constants.PaymeConstants;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.ImageFileStorage;
import com.nomadapp.splash.model.localdatastorage.StoragePermission;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nomadapp.splash.R.id.socialIDProofFileUpload;
import static com.nomadapp.splash.R.id.bankAccProofFileUpload;
import static com.nomadapp.splash.R.id.incDocProofFileUpload;

import static com.nomadapp.splash.R.id.socialIDProofCameraUpload;
import static com.nomadapp.splash.R.id.bankAccProofCameraUpload;
import static com.nomadapp.splash.R.id.incDocProofCameraUpload;

import com.nomadapp.splash.utils.sysmsgs.connectionlost.ConnectionLost;

public class SplasherApplicationActivity extends AppCompatActivity {

    private Context ctx = SplasherApplicationActivity.this;

    private Spinner cSplasherSignUpCity;
    private EditText cSplasherSignUpName, cSplasherSignUpLastName, cSplasherSignUpEmail,
                     cSplasherSignUpPhoneNumber, cSplasherSignUpPassword, cSplasherSignUpPassword2,
                     cSplasherSignUpUserName;
    private Button cSplasherSignUpButton, cSplasherLogInButton;
    private TextView cOrSplashLogin;
    private TextView cCityTitle, cSplasherSignUpContractAgreement;
    private ImageView cSplasherLogoSignUp;
    private TextView cFirstText;
    private boolean signUpCheck = false;//<--SignUp Check. Login always showing first. So SignUp
    // always 'false' first
    ParseUser splasherUser = new ParseUser();
    //ParseUser currentUser = ParseUser.getCurrentUser();

    //Variables for functionality and Data holding relevant to Payme's create_seller API calls----//

    //Android widgets (EditText, RadioGroup, RadioButton)//
    private EditText cSplasherSignUpCountryID, cSplasherSignUpCountryIdIssued, cSplasherSignUpDateOfBirth;
    private RadioGroup cSplasherSignUpGender;
    private RadioButton cRadioM, cRadioF;
    private EditText cSplasherSignUpBankCodeNum, cSplasherSignUpBankBranchNum, cSplasherSignUpBankAccNum;
    private EditText cSplasherSignUpAddress, cSplasherSignUpAddressNum;
    //---------------------------------------------------//

    //Data that the seller won't see and that are relevant to Payme's create_seller API---//
    //private String sellerDescription, sellerSiteUrl, sellerPersonBussinessType;
    //private int sellerInc;
    //private String sellerAddressCountry;
    //private float sellerMarketFee;
    //------------------------------------------------------------------------------------//

    //3 photos of legal files seller needs to have (URIs to be returned and String-holded)//
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 1;
    private Uri targetUriIDString, targetUriBankString, targetUriIncString;
    private String rawImageString1, rawImageString2, rawImageString3;
    private Bitmap socialBitmapGal, bankBitmapGal, incDocBitmapGal;
    private boolean fromCam = false;
    private boolean fromGallery = false;
    private ParseFile socialFile, bankFile, incDocFile;
    //-----------------------------------------------------------------------------------//

    //Variables for everything related to the UI for the 3 files upload//
    private RelativeLayout cSocialIDProofRelative, cBankAccProofRelative, cIncProofRelative;
    private TextView cSocialIDProofTextView, cBankAccProofTextView, cIncDocProofTextView;
    //camera function for all camera-file-upload operations: docsFromCamera()
    //gallery function for all gallery-file-upload operations: docsFromGallery()
    //-----------------------------------------------------------------//

    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(ctx);
    ToastMessages toastMessages = new ToastMessages();
    ConnectionLost clm = new ConnectionLost(ctx);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_application);

        if(getSupportActionBar() != null)
        getSupportActionBar().hide();

        ImageFileStorage.createImageGallery();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //
        cSplasherSignUpUserName = findViewById(R.id.splasherSignUpUserName);
        cSplasherSignUpPassword = findViewById(R.id.splasherSignUpPassword);
        cOrSplashLogin = findViewById(R.id.orSplashLogIn);
        cSplasherSignUpButton = findViewById(R.id.splasherSignUpButton);
        cSplasherLogInButton = findViewById(R.id.splasherLogInButton);
        //
        cFirstText = findViewById(R.id.firstText);
        cSplasherSignUpCity = findViewById(R.id.splasherSignUpCity);
        cSplasherSignUpName = findViewById(R.id.SplasherSignUpName);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpLastName = findViewById(R.id.SplasherSignUpLastName);//<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpEmail = findViewById(R.id.splasherSignUpEmail);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpPhoneNumber = findViewById(R.id.splasherSignUpPhoneNumber);//<<<<<<<<<<<<<<<<
        cSplasherSignUpPassword2 = findViewById(R.id.splasherSignUpPassword2);
        cSplasherLogoSignUp = findViewById(R.id.splasherLogoSignUp);
        RelativeLayout cBecomeSplasherRelative = findViewById(R.id.becomeSplashRelative);
        cCityTitle = findViewById(R.id.cityTitle);
        cSplasherSignUpContractAgreement = findViewById(R.id.splasherSignUpContactAgreement);
        //Variables for functionality and Data holding relevant to Payme's create_seller API calls//
        //Android widgets (EditText, RadioGroup, RadioButton)//
        cSplasherSignUpCountryID = findViewById(R.id.splasherSignUpCountryID);//<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpCountryIdIssued = findViewById(R.id.splasherSignUpCountryIdIssued);//<<<<<<<<
        cSplasherSignUpDateOfBirth = findViewById(R.id.splasherSignUpDateOfBirth);//<<<<<<<<<<<<<<<<
        cSplasherSignUpGender = findViewById(R.id.splasherSignUpGender);
        cRadioM = findViewById(R.id.radioM);                                //////<<<<<<<<<<<<<<<<<<
        cRadioF = findViewById(R.id.radioF);
        cSplasherSignUpBankCodeNum = findViewById(R.id.SplasherSignUpBankCodeNum);//<<<<<<<<<<<<<<<<
        cSplasherSignUpBankBranchNum = findViewById(R.id.SplasherSignUpBankBranchNum);//<<<<<<<<<<<<
        cSplasherSignUpBankAccNum = findViewById(R.id.splasherSignUpBankAccNum);//<<<<<<<<<<<<<<<<<<
        cSplasherSignUpAddress = findViewById(R.id.SplasherSignUpAddress);//<<<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpAddressNum = findViewById(R.id.SplasherSignUpAddressNum);//<<<<<<<<<<<<<<<<<<

        cSocialIDProofRelative = findViewById(R.id.socialIDProofLinear);
        cBankAccProofRelative = findViewById(R.id.bankAccProofLinear);
        cIncProofRelative = findViewById(R.id.incDocProofLinear);
        cSocialIDProofTextView = findViewById(R.id.socialIDProofTextView);
        cBankAccProofTextView = findViewById(R.id.bankAccProofTextView);
        cIncDocProofTextView = findViewById(R.id.incDocProofTextView);

        //hide fields for signUp
        cFirstText.setText(getResources().getString(R.string.becomeSplasher_act_java_welcomeBack));
        hideOrShowFields(View.GONE, R.id.splasherSignUpPassword);
        centerStuff(cFirstText);
        centerLogo();

        cSplasherLogoSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        cBecomeSplasherRelative.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        cOrSplashLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(!signUpCheck){

                    cFirstText.setText(getResources().getString(R.string.splashPartnersProgramsTitle));
                    hideOrShowFields(View.VISIBLE, R.id.incDocProofLinear);
                    moveUpStuff(cFirstText);
                    moveUpLogo();

                    cSplasherSignUpButton.setVisibility(View.VISIBLE);

                    cSplasherLogInButton.setVisibility(View.INVISIBLE);

                    //or login
                    cOrSplashLogin.setText(getResources().getString(R.string.becomeSplasher_act_java_login));

                    signUpCheck = true;

                    Log.i("Status", "SignUp enabled");

                } else {

                    cFirstText.setText(getResources().getString(R.string.becomeSplasher_act_java_welcomeBack));
                    hideOrShowFields(View.GONE, R.id.splasherSignUpPassword2);
                    centerStuff(cFirstText);
                    centerLogo();

                    cSplasherSignUpButton.setVisibility(View.INVISIBLE);

                    cSplasherLogInButton.setVisibility(View.VISIBLE);

                    // or signup
                    cOrSplashLogin.setText(getResources().getString(R.string.becomeSplasher_act_java_signup));

                    signUpCheck = false;

                    Log.i("Status", "Login enabled");

                }

            }
        });

        spinnerCities();

        clm.connectivityStatus(ctx);

    }

    public void splasherSignUp(View view){

        final String splasher = "splasher";//<-- TYPE OF USER : carOwner

        final String washes = "0";
        final String oldAvgRating = "0";
        final String washesCanceled = "0";

        final String numericalBadge = "2";

        final String setPrice = "15";
        final String setPriceEInt = "15";
        final String setPriceMoto = "15";

        //If im giving 3 of Rating, then it has to be counted as 1 done wash for math porpuses.
        //When it'll come to show number of washes to the Splasher, simply substract 1 from the var.

        //convert city to string before doing check

        String  cityText = String.valueOf(cSplasherSignUpCity.getSelectedItem());

        if(cityText.isEmpty() || cSplasherSignUpName.getText().toString().isEmpty() ||
                cSplasherSignUpLastName.getText().toString().isEmpty() ||
                cSplasherSignUpEmail.getText().toString().isEmpty() ||
                cSplasherSignUpUserName.getText().toString().isEmpty() ||
                cSplasherSignUpPhoneNumber.getText().toString().isEmpty() ||
                cSplasherSignUpPassword.getText().toString().isEmpty() ||
                cSplasherSignUpPassword2.getText().toString().isEmpty() ||
                cSplasherSignUpCountryID.getText().toString().isEmpty() ||
                cSplasherSignUpCountryIdIssued.getText().toString().isEmpty() ||
                cSplasherSignUpDateOfBirth.getText().toString().isEmpty() ||
                cSplasherSignUpBankCodeNum.getText().toString().isEmpty() ||
                cSplasherSignUpBankBranchNum.getText().toString().isEmpty() ||
                cSplasherSignUpBankAccNum.getText().toString().isEmpty() ||
                cSplasherSignUpAddress.getText().toString().isEmpty() ||
                cSplasherSignUpAddressNum.getText().toString().isEmpty() ||
                cSocialIDProofTextView.getText().toString().isEmpty() ||
                cBankAccProofTextView.getText().toString().isEmpty() ||
                cIncDocProofTextView.getText().toString().isEmpty()){

            toastMessages.productionMessage(ctx
                    ,getResources().getString(R.string.becomeSplasher_act_java_pleaseFillAll),
                    1);

        } else if(!cSplasherSignUpPassword.getText().toString().equals(cSplasherSignUpPassword2
                .getText().toString())) {

            toastMessages.productionMessage(ctx
                    ,getResources().getString(R.string.becomeSplasher_act_java_bothPasswords),
                    1);

        } else if(!cSplasherSignUpEmail.getText().toString().contains("@")
                || !cSplasherSignUpEmail.getText().toString().contains(".com")) {

            toastMessages.productionMessage(ctx
                    ,getResources().getString(R.string
                            .becomeSplasher_act_java_pleaseEnterValidEmail),
                    1);

        } else {

            boxedLoadingDialog.showLoadingDialog();
            splasherUser = new ParseUser();
            splasherUser.setUsername(cSplasherSignUpUserName.getText().toString());
            splasherUser.setPassword(cSplasherSignUpPassword.getText().toString());
            splasherUser.setEmail(cSplasherSignUpEmail.getText().toString());//<<<<<<<<<<<<<<<<<<<<<
            splasherUser.put("city", cityText);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            splasherUser.put("phonenumber", cSplasherSignUpPhoneNumber.getText().toString());//<<<<<
            splasherUser.put("name", cSplasherSignUpName.getText().toString());//<<<<<<<<<<<<<<<<<<<
            splasherUser.put("lastname", cSplasherSignUpLastName.getText().toString());//<<<<<<<<<<<
            splasherUser.put("CarOwnerOrSplasher", splasher);
            //upload images
            splasherUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        Log.i("splasherSignUp", "signUp SuccessFul");
                        //RATING///////////////////////////////
                        ParseObject profile = new ParseObject("Profile");
                        profile.put("username", ParseUser.getCurrentUser().getUsername());
                        profile.put("CarOwnerOrSplasher", splasher);
                        profile.put("splasherType", "independent");

                        profile.put("oldAvgRating", oldAvgRating);
                        profile.put("washes", washes);
                        profile.put("washesCanceled", washesCanceled);

                        profile.put("numericalBadge", numericalBadge);
                        profile.put("setPrice", setPrice);
                        profile.put("setPriceEInt",setPriceEInt);
                        profile.put("setPriceMoto",setPriceMoto);

                        profile.put("serviceRange", "not set");
                        profile.put("serviceEC", "not set");
                        profile.put("ECCoords", "not set");

                        profile.put("status", "active");

                        profile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                backToMainFromSignUp();
                            }
                        });
                        ///////////////////////////////////////
                    } else {
                        Log.i("splasherSignUp", "signUp Failed");
                        toastMessages.productionMessage(getApplicationContext(), e.getMessage()
                                , 1);
                    }
                }
            });
        }
    }

    public void hideOrShowFields(int visibility, int belowId){

        cSplasherSignUpCity.setVisibility(visibility);
        cSplasherSignUpName.setVisibility(visibility);
        cSplasherSignUpLastName.setVisibility(visibility);
        cSplasherSignUpEmail.setVisibility(visibility);
        cSplasherSignUpPhoneNumber.setVisibility(visibility);
        cSplasherSignUpPassword2.setVisibility(visibility);
        cCityTitle.setVisibility(visibility);
        cSplasherSignUpContractAgreement.setVisibility(visibility);
        cSplasherSignUpCountryIdIssued.setVisibility(visibility);
        cSplasherSignUpCountryID.setVisibility(visibility);
        cSplasherSignUpDateOfBirth.setVisibility(visibility);
        cSplasherSignUpGender.setVisibility(visibility);
        cRadioF.setVisibility(visibility);
        cRadioM.setVisibility(visibility);
        cSplasherSignUpBankCodeNum.setVisibility(visibility);
        cSplasherSignUpBankBranchNum.setVisibility(visibility);
        cSplasherSignUpBankAccNum.setVisibility(visibility);
        cSplasherSignUpAddress.setVisibility(visibility);
        cSplasherSignUpAddressNum.setVisibility(visibility);
        cSocialIDProofRelative.setVisibility(visibility);
        cBankAccProofRelative.setVisibility(visibility);
        cIncProofRelative.setVisibility(visibility);

        RelativeLayout.LayoutParams params1 = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.BELOW, belowId);
        params1.addRule(Gravity.CENTER);
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //cSplasherLogInButton/signUp -- centerHorizontal
        //fix photo ID camera and uploadFile icons
        cSplasherLogInButton.setLayoutParams(params1);
        cSplasherSignUpButton.setLayoutParams(params1);
    }

    public void centerStuff(View view){
        RelativeLayout.LayoutParams params2 = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.addRule(RelativeLayout.CENTER_VERTICAL);
        view.setLayoutParams(params2);
    }

    public void moveUpStuff(View view){
        RelativeLayout.LayoutParams params3 = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params3.addRule(RelativeLayout.BELOW, R.id.splasherLogoSignUp);
        view.setLayoutParams(params3);
    }

    public void centerLogo(){
        RelativeLayout.LayoutParams params4 = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        params4.addRule(RelativeLayout.ABOVE, R.id.firstText);
        params4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cSplasherLogoSignUp.setLayoutParams(params4);
    }

    public void moveUpLogo(){
        RelativeLayout.LayoutParams params5 = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        params5.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params5.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cSplasherLogoSignUp.setLayoutParams(params5);
    }

    public void splasherLogIn(View view) {
        final String carOwnerWrong = "carOwner";
        cSplasherSignUpUserName = findViewById(R.id.splasherSignUpUserName);
        cSplasherSignUpPassword = findViewById(R.id.splasherSignUpPassword);
        if (cSplasherSignUpUserName.getText().toString().isEmpty()
                || cSplasherSignUpPassword.getText().toString().isEmpty()) {
            toastMessages.productionMessage(getApplicationContext(),getResources(
            ).getString(R.string.becomeSplasher_act_java_userNameAndPass),1);
        } else {
            ParseUser.logInInBackground(cSplasherSignUpUserName.getText().toString()
                    , cSplasherSignUpPassword.getText().toString()
                    , new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                if (user.getString("CarOwnerOrSplasher")
                                        .equals(carOwnerWrong)) {
                                    cWrongUserTypeDialog();
                                } else {
                                    Log.i("LogIn", "logIn Successful");
                                    backToMainFromLogin();
                                }
                            } else {
                                Log.i("LogIn", "LogIn Failed");
                                toastMessages.productionMessage(getApplicationContext()
                                        ,getResources()
                                        .getString(R.string.becomeSplasher_act_java_loginFailed)
                                        ,1);
                            }
                        }
                    });
        }
    }

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getApplicationContext()
                    .getSystemService(INPUT_METHOD_SERVICE);
            if(imm != null) {
                if(getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void backToMainFromLogin(){
        Intent intent = new Intent(ctx, HomeActivity. class);
        startActivity(intent);
    }

    public void backToMainFromSignUp(){

        final Intent intent = new Intent(ctx, HomeActivity. class);

        intent.putExtra("PaymentClientKey", PaymeConstants.PAYMENT_CLIENT_KEY);//READY//
        intent.putExtra("sellerFirstName", cSplasherSignUpName.getText().toString());//READY//
        intent.putExtra("sellerLastName", cSplasherSignUpLastName.getText()
                .toString());//READY//
        intent.putExtra("sellerSocialId", cSplasherSignUpCountryID.getText()
                .toString());//READY//
        intent.putExtra("sellerBirthDate", cSplasherSignUpDateOfBirth.getText()
                .toString());//READY//
        intent.putExtra("sellerSocialIdIssued", cSplasherSignUpCountryIdIssued.getText()
                .toString());//READY//
        intent.putExtra("sellerGender",
                splasherGenderProcessor(cSplasherSignUpGender));//READY// ----//Int//----
        intent.putExtra("sellerEmail", cSplasherSignUpEmail.getText().toString());//READY//
        intent.putExtra("sellerPhoneNumber", cSplasherSignUpPhoneNumber.getText()
                .toString());//READY//

        intent.putExtra("sellerBankCode", Integer.parseInt(cSplasherSignUpBankCodeNum
                .getText().toString()));//READY// ----//Int//----
        intent.putExtra("sellerBankBranch", Integer.parseInt(cSplasherSignUpBankBranchNum
                .getText().toString()));//READY// ----//Int//----
        intent.putExtra("sellerBankAccountNumber", Integer.parseInt(cSplasherSignUpBankAccNum
                .getText().toString()));//READY// ----//Int//----
        WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(ctx);
        writeReadDataInFile.writeToFile("sellerBankCode",cSplasherSignUpBankCodeNum
                .getText().toString());
        writeReadDataInFile.writeToFile("sellerBankBranch",cSplasherSignUpBankBranchNum
                .getText().toString());
        writeReadDataInFile.writeToFile("sellerBankAccountNumber",cSplasherSignUpBankAccNum
                .getText().toString());

        intent.putExtra("sellerDescription", PaymeConstants.SELLER_DESCRIPTION);//READY//
        intent.putExtra("sellerSiteUrl", PaymeConstants.SELLER_SITE_URL);//READY//
        intent.putExtra("sellerPersonBussinessType"
                ,PaymeConstants.SELLER_PERSON_BUSINESS_TYPE);//READY//
        intent.putExtra("sellerInc", PaymeConstants.SELLER_INC);//READY// ----//Int//----

        intent.putExtra("sellerAddressCity", cSplasherSignUpCity.getSelectedItem()
                .toString());//READY//
        intent.putExtra("sellerAddressStreet", cSplasherSignUpAddress.getText()
                .toString());//READY//
        intent.putExtra("sellerAddressStreetNumber", Integer
                .parseInt(cSplasherSignUpAddressNum.getText().toString()));//READY// ----//Int//----
        intent.putExtra("sellerAddressCountry", PaymeConstants.SELLER_COUNTRY);//READY//

        intent.putExtra("sellerMarketFee", PaymeConstants.MARKET_FEE);//READY//----//Double//-

        //--PARAMETER RELEVANT TO APP IN PRODUCTION ONLY--//
        intent.putExtra("sellerPlan", PaymeConstants.MONTHLY_CREDIT_TRACK);
        //------------------------------------------------//

        byte[] bytes1;
        byte[] bytes2;
        byte[] bytes3;
        if(fromGallery && !fromCam){

            intent.putExtra("fromGallery", true);

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            socialBitmapGal.compress(Bitmap.CompressFormat.JPEG, 20, stream1);
            bytes1 = stream1.toByteArray();//READY//
            socialFile = new ParseFile("socialFile.png", bytes1);

            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bankBitmapGal.compress(Bitmap.CompressFormat.JPEG, 20, stream2);
            bytes2 = stream2.toByteArray();//READY//
            bankFile = new ParseFile("bankFile.png", bytes2);

            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            incDocBitmapGal.compress(Bitmap.CompressFormat.JPEG, 20, stream3);
            bytes3 = stream3.toByteArray();//READY//
            incDocFile = new ParseFile("incDocFile.png", bytes3);

        }else if(!fromGallery && fromCam){

            intent.putExtra("fromGallery", false);

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            Bitmap socialBitmap = BitmapFactory.decodeFile(rawImageString1);
            socialBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream1);
            bytes1 = stream1.toByteArray();//READY//
            socialFile = new ParseFile("socialFile.jpeg", bytes1);

            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            Bitmap bankBitmap = BitmapFactory.decodeFile(rawImageString2);
            bankBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream2);
            bytes2 = stream2.toByteArray();//READY//
            bankFile = new ParseFile("bankFile.jpeg", bytes2);

            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            Bitmap incDocBitmap = BitmapFactory.decodeFile(rawImageString3);
            incDocBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream3);
            bytes3 = stream3.toByteArray();//READY//
            incDocFile = new ParseFile("incDocFile.jpeg", bytes3);
        }

        final ParseObject documents = new ParseObject("Documents");
        documents.put("username", cSplasherSignUpUserName.getText().toString());
        documents.put("socialIdProof", socialFile);
        documents.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    documents.put("bankProof", bankFile);
                    documents.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                documents.put("incDocProof", incDocFile);
                                documents.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public void cWrongUserTypeDialog(){
        ParseUser.logOut();
        AlertDialog.Builder wrongUserTypeDialog = new AlertDialog
                .Builder(ctx);
        wrongUserTypeDialog.setTitle(getResources().getString(R.string
                .becomeSplasher_act_java_wrongUserType));
        wrongUserTypeDialog.setIcon(android.R.drawable.ic_dialog_alert);
        wrongUserTypeDialog.setMessage(getResources()
                .getString(R.string.becomeSplasher_act_java_pleaseLoginThroughRespec));
        wrongUserTypeDialog.setPositiveButton(getResources().getString(R.string
                .becomeSplasher_act_java_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ctx, SignUpLogInActivity.class);
                startActivity(intent);
            }
        });
        wrongUserTypeDialog.show();
    }

    public void spinnerCities(){
        cSplasherSignUpCity = findViewById(R.id.splasherSignUpCity);
        List<String> cityList = new ArrayList<>();
        cityList.add(getResources().getString(R.string.becomeSplasher_act_java_telAviv));
        cityList.add(getResources().getString(R.string.becomeSplasher_act_java_herzliya));
        cityList.add(getResources().getString(R.string.becomeSplasher_act_java_ramatGan));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(ctx, android.R.layout
                .simple_spinner_item, cityList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cSplasherSignUpCity.setAdapter(dataAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(ctx, SignUpLogInActivity.class));
        }
        return false;
    }

    //Proof File upload from GALLERY
    public void docsFromGallery(View view){
        StoragePermission.isStoragePermissionGranted(ctx,SplasherApplicationActivity.this);
        if(StoragePermission.isStoragePermissionGranted(ctx,SplasherApplicationActivity.this)){
            switch(view.getId()) {
                case socialIDProofFileUpload:
                     Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                     startActivityForResult(intent, 1);
                     break;
                case bankAccProofFileUpload:
                    Intent intent2 = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent2, 2);
                    break;
                case incDocProofFileUpload:
                    Intent intent3 = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent3, 3);
                    break;
            }
        }
    }

    //Proof File upload from CAMERA
    public void docsFromCamera(View view){
        StoragePermission.isWrittingToStoragePermissionGranted
                (ctx,SplasherApplicationActivity.this);
        if(StoragePermission.isWrittingToStoragePermissionGranted
                (ctx,SplasherApplicationActivity.this)){
            try{
                switch (view.getId()){
                    case socialIDProofCameraUpload:
                        String socialIdCamPic = "socialID_";
                        File socialIDFile = ImageFileStorage.createImageFile(socialIdCamPic);
                        targetUriIDString = ImageFileStorage.contentCamFile(ctx,socialIDFile);
                        rawImageString1 = socialIDFile.getAbsolutePath();
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, targetUriIDString);
                        startActivityForResult(i, 4);
                        break;
                    case bankAccProofCameraUpload:
                        String bankAccCamPic = "bankAcc_";
                        File bankAccFile = ImageFileStorage.createImageFile(bankAccCamPic);
                        targetUriBankString = ImageFileStorage.contentCamFile(ctx,bankAccFile);
                        rawImageString2 = bankAccFile.getAbsolutePath();
                        Intent ii = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        ii.putExtra(MediaStore.EXTRA_OUTPUT, targetUriBankString);
                        startActivityForResult(ii, 5);
                        break;
                    case incDocProofCameraUpload:
                        String incDocCamPic = "incDoc_";
                        File incFile = ImageFileStorage.createImageFile(incDocCamPic);
                        targetUriIncString = ImageFileStorage.contentCamFile(ctx,incFile);
                        rawImageString3 = incFile.getAbsolutePath();
                        Intent iii = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        iii.putExtra(MediaStore.EXTRA_OUTPUT, targetUriIncString);
                        startActivityForResult(iii, 6);
                        break;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                toastMessages.productionMessage(getApplicationContext(),getResources()
                        .getString(R.string.cameraIntent_act_java_permissionGranted),1);
            } else {
                toastMessages.productionMessage(getApplicationContext(),getResources()
                        .getString(R.string.cameraIntent_act_java_permissionToWrite),1);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK) {
                    fromGallery = true;
                    fromCam = false;
                    Uri targetUriID = data.getData();
                    if (targetUriID != null) {
                        String onlyFileNameID = targetUriID.toString().substring(targetUriID
                                .getPath().lastIndexOf(File.separator) + 1);
                        String onlyFileNameID2 = onlyFileNameID.substring(onlyFileNameID
                                .lastIndexOf("/") + 1);
                        try {
                            socialBitmapGal = MediaStore.Images.Media
                                    .getBitmap(this.getContentResolver(), targetUriID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("uriID", targetUriID.toString());
                        Log.i("uri name onlyID", onlyFileNameID);
                        Log.i("uri name only 2ID", onlyFileNameID2);
                        cSocialIDProofTextView.setText(onlyFileNameID2);
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK) {
                    fromGallery = true;
                    fromCam = false;
                    Uri targetUriBank = data.getData();
                    if (targetUriBank != null) {
                        String onlyFileNameBank = targetUriBank.toString()
                                .substring(targetUriBank.getPath().lastIndexOf(File.separator) + 1);
                        String onlyFileNameBank2 = onlyFileNameBank
                                .substring(onlyFileNameBank.lastIndexOf("/") + 1);
                        try {
                            bankBitmapGal = MediaStore.Images.Media
                                    .getBitmap(this.getContentResolver(), targetUriBank);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("uriBank", targetUriBank.toString());
                        Log.i("uri name onlyBank", onlyFileNameBank);
                        Log.i("uri name only 2Bank", onlyFileNameBank2);
                        cBankAccProofTextView.setText(onlyFileNameBank2);
                    }
                }
                break;
            case 3:
                if(resultCode == RESULT_OK) {
                    fromGallery = true;
                    fromCam = false;
                    Uri targetUriInc = data.getData();
                    if (targetUriInc != null) {
                        String onlyFileNameInc = targetUriInc.toString()
                                .substring(targetUriInc.getPath().lastIndexOf(File.separator) + 1);
                        String onlyFileNameInc2 = onlyFileNameInc
                                .substring(onlyFileNameInc.lastIndexOf("/") + 1);
                        try {
                            incDocBitmapGal = MediaStore.Images.Media
                                    .getBitmap(this.getContentResolver(), targetUriInc);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("uriInc", targetUriInc.toString());
                        Log.i("uri name onlyInc", onlyFileNameInc);
                        Log.i("uri name only 2Inc", onlyFileNameInc2);
                        cIncDocProofTextView.setText(onlyFileNameInc2);
                    }
                }
                break;
            case 4:
                if(resultCode == RESULT_OK){
                    fromGallery = false;
                    fromCam = true;
                    //targetUriID = data.getData();/////////////////<<<LOCAL URI>>>
                    if (targetUriIDString != null) {
                        String stringedID = targetUriIDString.toString();
                        String onlyFileNameIDCam = stringedID.substring(stringedID.
                                lastIndexOf(File.separator) + 1);
                        String onlyFileNameIDCam2 = onlyFileNameIDCam
                                .substring(onlyFileNameIDCam.lastIndexOf("/") + 1);
                        Log.i("uriIDCam", stringedID);
                        Log.i("uri name onlyIDCam", onlyFileNameIDCam);
                        Log.i("uri name only 2IDCam", onlyFileNameIDCam2);
                        cSocialIDProofTextView.setText(onlyFileNameIDCam2);
                    }
                }
                break;
            case 5:
                if(resultCode == RESULT_OK){
                    fromGallery = false;
                    fromCam = true;
                    //targetUriBank = data.getData();/////////////////<<<LOCAL URI>>>
                    if (targetUriBankString != null) {
                        String stringedBank = targetUriBankString.toString();
                        String onlyFileNameBankCam = stringedBank.substring(stringedBank.
                                lastIndexOf(File.separator) + 1);
                        String onlyFileNameBankCam2 = onlyFileNameBankCam
                                .substring(onlyFileNameBankCam.lastIndexOf("/") + 1);
                        Log.i("uriBankCam", stringedBank);
                        Log.i("uri name onlyBankCam", onlyFileNameBankCam);
                        Log.i("uri name only 2BankCam", onlyFileNameBankCam2);
                        cBankAccProofTextView.setText(onlyFileNameBankCam2);
                    }
                }
                break;
            case 6:
                if(resultCode == RESULT_OK){
                    fromGallery = false;
                    fromCam = true;
                    //targetUriInc = data.getData();/////////////////<<<LOCAL URI>>>
                    if (targetUriIncString != null) {
                        String stringedDocInc = targetUriIncString.toString();
                        String onlyFileNameIncCam = stringedDocInc.substring(stringedDocInc.
                                lastIndexOf(File.separator) + 1);
                        String onlyFileNameIncCam2 = onlyFileNameIncCam
                                .substring(onlyFileNameIncCam.lastIndexOf("/") + 1);
                        Log.i("uriIncCam", stringedDocInc);
                        Log.i("uri name onlyIncCam", onlyFileNameIncCam);
                        Log.i("uri name only 2IncCam", onlyFileNameIncCam2);
                        cIncDocProofTextView.setText(onlyFileNameIncCam2);
                    }
                }
                break;
        }
    }

    public int splasherGenderProcessor(RadioGroup radioGroup){
        //gender: male=0; female=1;
        int gender = 3;
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton genderRadioButton = findViewById(selectedId);
        if(genderRadioButton.getText().equals("Male")){
            gender = 0;
        }else if(genderRadioButton.getText().equals("Female")){
            gender = 1;
        }
        return gender;
    }
}
