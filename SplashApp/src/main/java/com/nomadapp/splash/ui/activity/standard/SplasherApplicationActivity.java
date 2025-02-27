package com.nomadapp.splash.ui.activity.standard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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
import com.nomadapp.splash.model.payment.paymeapis.seller.SplashCreateSeller;
import com.nomadapp.splash.model.server.parseserver.DocumentsClassInterface;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.DocumentsClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.ProfileClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.DocumentsClassSend;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

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

import com.nomadapp.splash.utils.sysmsgs.ConnectionLost;

public class SplasherApplicationActivity extends AppCompatActivity {

    private Context ctx = SplasherApplicationActivity.this;

    private Spinner cSplasherSignUpCity;
    private EditText cSplasherSignUpName, cSplasherSignUpLastName, cSplasherSignUpEmail,
                     cSplasherSignUpPhoneNumber;

    //Variables for functionality and Data holding relevant to Payme's create_seller API calls----//

    //Android widgets (EditText, RadioGroup, RadioButton)//
    private EditText cSplasherSignUpCountryID, cSplasherSignUpCountryIdIssued, cSplasherSignUpDateOfBirth;
    private RadioGroup cSplasherSignUpGender;
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
    private String sellerFileSocialId, sellerFileCheque, sellerFileCorporate;
    //-----------------------------------------------------------------------------------//

    //Variables for everything related to the UI for the 3 files upload//
    private TextView cSocialIDProofTextView, cBankAccProofTextView, cIncDocProofTextView;
    //camera function for all camera-file-upload operations: docsFromCamera()
    //gallery function for all gallery-file-upload operations: docsFromGallery()
    //-----------------------------------------------------------------//

    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(ctx);
    private ToastMessages toastMessages = new ToastMessages();
    private ConnectionLost clm = new ConnectionLost(ctx);
    private UserClassQuery ucq = new UserClassQuery(SplasherApplicationActivity.this);

    private boolean social_id_sent = false;
    private boolean bank_proof_sent = false;
    private boolean corp_entity_sent = false;
    private boolean requestSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_application);

        if(getSupportActionBar() != null)
        getSupportActionBar().hide();

        ImageFileStorage.createImageGallery();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView cFirstText = findViewById(R.id.firstText);
        cSplasherSignUpCity = findViewById(R.id.splasherSignUpCity);
        cSplasherSignUpName = findViewById(R.id.SplasherSignUpName);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpLastName = findViewById(R.id.SplasherSignUpLastName);//<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpEmail = findViewById(R.id.splasherSignUpEmail);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpPhoneNumber = findViewById(R.id.splasherSignUpPhoneNumber);//<<<<<<<<<<<<<<<<
        ImageView cSplasherLogoSignUp = findViewById(R.id.splasherLogoSignUp);
        RelativeLayout cBecomeSplasherRelative = findViewById(R.id.becomeSplashRelative);
        //Variables for functionality and Data holding relevant to Payme's create_seller API calls//
        //Android widgets (EditText, RadioGroup, RadioButton)//
        cSplasherSignUpCountryID = findViewById(R.id.splasherSignUpCountryID);//<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpCountryIdIssued = findViewById(R.id.splasherSignUpCountryIdIssued);//<<<<<<<<
        cSplasherSignUpDateOfBirth = findViewById(R.id.splasherSignUpDateOfBirth);//<<<<<<<<<<<<<<<<
        cSplasherSignUpGender = findViewById(R.id.splasherSignUpGender);
        cSplasherSignUpBankCodeNum = findViewById(R.id.SplasherSignUpBankCodeNum);//<<<<<<<<<<<<<<<<
        cSplasherSignUpBankBranchNum = findViewById(R.id.SplasherSignUpBankBranchNum);//<<<<<<<<<<<<
        cSplasherSignUpBankAccNum = findViewById(R.id.splasherSignUpBankAccNum);//<<<<<<<<<<<<<<<<<<
        cSplasherSignUpAddress = findViewById(R.id.SplasherSignUpAddress);//<<<<<<<<<<<<<<<<<<<<<<<<
        cSplasherSignUpAddressNum = findViewById(R.id.SplasherSignUpAddressNum);//<<<<<<<<<<<<<<<<<<

        cSocialIDProofTextView = findViewById(R.id.socialIDProofTextView);
        cBankAccProofTextView = findViewById(R.id.bankAccProofTextView);
        cIncDocProofTextView = findViewById(R.id.incDocProofTextView);

        //hide fields for signUp
        cFirstText.setText(getResources().getString(R.string.becomeSplasher_act_java_welcomeBack));

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
        spinnerCities();
        clm.connectivityStatus(ctx);
    }

    public void applyToSeller(View view){

        //If im giving 3 of Rating, then it has to be counted as 1 done wash for math porpuses.
        //When it'll come to show number of washes to the Splasher, simply substract 1 from the var.
        //SWITCH TO PAYME DEV MODE AND RUN THIS WHOLE THING TO SEE IF IT WORKS

        String cityText = String.valueOf(cSplasherSignUpCity.getSelectedItem());

        if(cityText.isEmpty() || cSplasherSignUpName.getText().toString().isEmpty() ||
                cSplasherSignUpLastName.getText().toString().isEmpty() ||
                cSplasherSignUpEmail.getText().toString().isEmpty() ||
                cSplasherSignUpPhoneNumber.getText().toString().isEmpty() ||
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
                cIncDocProofTextView.getText().toString().isEmpty()) {

            toastMessages.productionMessage(ctx
                    , getResources().getString(R.string.becomeSplasher_act_java_pleaseFillAll),
                    1);

        }else if(!cSplasherSignUpEmail.getText().toString().contains("@")) {
            toastMessages.productionMessage(ctx
                    ,getResources().getString(R.string
                            .becomeSplasher_act_java_pleaseEnterValidEmail),
                    1);
        } else {
            boxedLoadingDialog.showLoadingDialog();
            //need to query profile, then update (no object creation)
            //then query Documents, then update
            ProfileClassQuery pcq = new ProfileClassQuery
                    (SplasherApplicationActivity.this);
            pcq.fetchAllSplashersInfo(ucq.userName()
                    , new ProfileClassInterface.allSplashersInfo() {
                @Override
                public void getInfo(ParseObject object) {
                   object.put("splasherType", "independent");
                   object.saveInBackground(new SaveCallback() {
                       @Override
                       public void done(ParseException e) {
                           backToMainFromSignUp();
                       }
                   });
                }
                @Override
                public void afterLoop(List<ParseObject> objects) {

                }
            });
        }
    }

    public void backToMainFromSignUp(){
        byte[] bytes1;
        byte[] bytes2;
        byte[] bytes3;
        if(fromGallery && !fromCam){

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

        DocumentsClassQuery dcq = new DocumentsClassQuery(SplasherApplicationActivity.this);
        dcq.fetchSplasherDocuments(ucq.userName(), new DocumentsClassInterface.getSplasherDocs() {
            @Override
            public void getDocs(final ParseObject object) {
                object.put("socialIdProof", socialFile);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            object.put("bankProof", bankFile);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        object.put("incDocProof", incDocFile);
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null) {
                                                    dispatchDocumentsSplasherSignUp();
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
        });
    }

    private void dispatchDocumentsSplasherSignUp(){
        final SplashCreateSeller splashCreateSeller = new SplashCreateSeller
                (SplasherApplicationActivity.this);
        DocumentsClassSend documentsClassSend
                = new DocumentsClassSend(SplasherApplicationActivity.this);
        documentsClassSend.sendSplasherDocumentsToServer
            (new DocumentsClassInterface.sendSplasherDocs() {
                @Override
                public void sendDocs(ParseObject fileObject) {
                    ParseFile socialIdFile = fileObject
                            .getParseFile("socialIdProof");
                    if(socialIdFile != null){
                        if (!social_id_sent) {
                            Log.i("socialIdProof", socialIdFile.getUrl());
                            sellerFileSocialId = socialIdFile.getUrl();

                            ParseFile bankFile = fileObject.getParseFile("bankProof");
                            if (bankFile != null) {
                                if (!bank_proof_sent) {
                                    Log.i("bankProof", bankFile.getUrl());
                                    sellerFileCheque = bankFile.getUrl();

                                    ParseFile incDocFile = fileObject
                                            .getParseFile("incDocProof");
                                    if (incDocFile != null) {
                                        if (!corp_entity_sent) {
                                            Log.i("incDocProof", incDocFile.getUrl());
                                            sellerFileCorporate = incDocFile.getUrl();
                                            if (!requestSent) {
                                                splashCreateSeller.sendDataToPaymentServer(
                                                        PaymeConstants.PAYMENT_CLIENT_KEY
                                                        , cSplasherSignUpName.getText().toString()
                                                        , cSplasherSignUpLastName.getText().toString()
                                                        , cSplasherSignUpCountryID.getText().toString()
                                                        , cSplasherSignUpDateOfBirth.getText().toString()
                                                        , cSplasherSignUpCountryIdIssued.getText().toString()
                                                        , splasherGenderProcessor(cSplasherSignUpGender)
                                                        , cSplasherSignUpEmail.getText().toString()
                                                        , cSplasherSignUpPhoneNumber.getText().toString()
                                                        , Integer.parseInt(cSplasherSignUpBankCodeNum.getText().toString())
                                                        , Integer.parseInt(cSplasherSignUpBankBranchNum.getText().toString())
                                                        , Integer.parseInt(cSplasherSignUpBankAccNum.getText().toString())
                                                        , PaymeConstants.SELLER_DESCRIPTION
                                                        , PaymeConstants.SELLER_SITE_URL
                                                        , PaymeConstants.SELLER_PERSON_BUSINESS_TYPE
                                                        , PaymeConstants.SELLER_INC
                                                        , cSplasherSignUpCity.getSelectedItem().toString()
                                                        , cSplasherSignUpAddress.getText().toString()
                                                        , Integer.parseInt(cSplasherSignUpAddressNum
                                                                .getText().toString())
                                                        , PaymeConstants.SELLER_COUNTRY
                                                        , PaymeConstants.MARKET_FEE
                                                        , sellerFileSocialId
                                                        , sellerFileCheque
                                                        , sellerFileCorporate
                                                        , PaymeConstants.MONTHLY_CREDIT_TRACK
                                                );
                                                boxedLoadingDialog.hideLoadingDialog();
                                                requestSent = true;
                                            }
                                            corp_entity_sent = true;
                                        }
                                    }
                                    bank_proof_sent = true;
                                }
                            }
                            social_id_sent = true;
                        }
                    }
                }
            });
    }

    private void spinnerCities(){
        cSplasherSignUpCity = findViewById(R.id.splasherSignUpCity);
        List<String> cityList = new ArrayList<>();
        cityList.add(getResources().getString(R.string.becomeSplasher_act_java_telAviv));
        cityList.add(getResources().getString(R.string.becomeSplasher_act_java_herzliya));
        cityList.add(getResources().getString(R.string.becomeSplasher_act_java_ramatGan));
        cityList.add(getResources().getString(R.string.becomeSplasher_act_java_givatayim));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(ctx, android.R.layout
                .simple_spinner_item, cityList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cSplasherSignUpCity.setAdapter(dataAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
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

    private int splasherGenderProcessor(RadioGroup radioGroup){
        //gender: male=0; female=1;
        //Set to Male (0) by default
        int gender = 0;
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton genderRadioButton = findViewById(selectedId);
        if(genderRadioButton.getText().equals("Male")){
            gender = 0;
        }else if(genderRadioButton.getText().equals("Female")){
            gender = 1;
        }
        return gender;
    }

    private void hideKeyboard(){
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
}
