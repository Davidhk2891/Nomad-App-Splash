package com.nomadapp.splash.ui.activity.standard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.model.localdatastorage.StoragePermission;
import com.nomadapp.splash.model.server.parseserver.UserClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.nomadapp.splash.model.imagehandler.ImageRotation;

public class AccountActivity extends AppCompatActivity {

    private Context ctx = AccountActivity.this;

    private static File mGalleryFolder;
    private EditText cUsernameEdit, cLastNameEdit, cEmailEdit, cPhoneEdit;
    private ImageView cAccountPicHolder;

    private Uri imageFileLocation;
    private String choosenGalFileLocation;
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 70;
    private boolean fieldsOnEdit = false;
    private boolean picChosen = false;
    private boolean chosenFromGal = false;
    private String rawImageString;

    private ParseFile profileFile;
    private String localPictureString;

    private UserClassQuery userClassQuery = new UserClassQuery(ctx);
    private GlideImagePlacement glideImagePlacement = new GlideImagePlacement
            (AccountActivity.this);
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog
            (AccountActivity.this);
    private ToastMessages toastMessages = new ToastMessages();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //Navigate back to parent activity
        createImageGallery();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------
        cAccountPicHolder = findViewById(R.id.accountPicHolder);
        cUsernameEdit = findViewById(R.id.usernameEdit);
        cLastNameEdit = findViewById(R.id.lastNameEdit);
        cEmailEdit = findViewById(R.id.emailEdit);
        cPhoneEdit = findViewById(R.id.phoneEdit);

        setEditTextState(false);
        getProfilePicture();

        //Get username and email from HomeActivity.java//
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String email = bundle.getString("email");
            String phoneNumber = null;
            String userName = null;
            String userLast = null;
            if (userClassQuery.userExists()){
                phoneNumber = userClassQuery.currentUserObject().getString("phonenumber");
                userName = userClassQuery.currentUserObject().getString("name");
                userLast = userClassQuery.currentUserObject().getString("lastname");
            }
            Log.i("stuff", "is" + email);
            if (email != null){
                cEmailEdit.setText(email);
                if (userName != null) {
                    cUsernameEdit.setText(userName);
                }
                if (userLast != null){
                    cLastNameEdit.setText(userLast);
                }
                if (phoneNumber != null) {
                    cPhoneEdit.setText(phoneNumber);
                }
            }
        }
        //-------------------------------------------------//
    }
    public void setEditTextState(boolean state){
        cUsernameEdit.setEnabled(state);
        cUsernameEdit.setClickable(state);

        cLastNameEdit.setEnabled(state);
        cLastNameEdit.setClickable(state);

        cEmailEdit.setEnabled(state);
        cEmailEdit.setClickable(state);

        cPhoneEdit.setEnabled(state);
        cPhoneEdit.setClickable(state);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if (item.getItemId() == R.id.action_save_acc){
            saveAccountChanges();
        }else if (item.getItemId() == R.id.action_edit_acc){
            editAccDetails();
        }
        return super.onOptionsItemSelected(item);
    }
    public void saveAccountChanges(){
        if (userClassQuery.userExists()){
            if (cUsernameEdit.getText().toString().isEmpty() && cEmailEdit.getText().toString()
                    .isEmpty()
                    && cPhoneEdit.getText().toString().isEmpty() && !picChosen) {
                toastMessages.productionMessage(getApplicationContext()
                ,"Nothing to update",1);
            }else {
                boxedLoadingDialog.showLoadingDialog();
                if (!cUsernameEdit.getText().toString().isEmpty()){
                    String updatedUsername = cUsernameEdit.getText().toString();
                    userClassQuery.currentUserObject().put("name",updatedUsername);
                }
                if (!cLastNameEdit.getText().toString().isEmpty()){
                    String updatedLastName = cLastNameEdit.getText().toString();
                    userClassQuery.currentUserObject().put("lastname",updatedLastName);
                }
                if (!cEmailEdit.getText().toString().isEmpty()){
                    String updatedUserEmail = cEmailEdit.getText().toString();
                    userClassQuery.currentUserObject().put("emailUpdated",updatedUserEmail);
                }
                if (!cPhoneEdit.getText().toString().isEmpty()){
                    String updatedPhonenumber = cPhoneEdit.getText().toString();
                    userClassQuery.currentUserObject().put("phonenumber", updatedPhonenumber);
                }
                if (picChosen){
                    processPicture();
                    userClassQuery.currentUserObject().put("fbProfilePic", localPictureString);
                    userClassQuery.currentUserObject().put("localProfilePic", profileFile);
                }
                if (userClassQuery.userExists()){
                    userClassQuery.updateUser(new UserClassInterface.updateUserRow() {
                        @Override
                        public void updateUser(ParseException e) {
                            if (e == null){
                                if (!picChosen) {
                                    finishAll();
                                }else{
                                    if (userClassQuery.currentUserObject()
                                            .getString("CarOwnerOrSplasher")
                                            .equals("splasher")){
                                        setProfPicForSplasher();
                                    }else{
                                        finishAll();
                                    }
                                }
                            }else{
                                uploadFailedMsg();
                            }
                        }
                    });
                }
            }
        }
    }
    public void uploadFailedMsg(){
        toastMessages.productionMessage(getApplicationContext()
                ,getResources().getString(R.string.act_account_failedToUpdateInfo)
                ,1);
    }
    public void finishAll(){
        toastMessages.productionMessage(getApplicationContext()
                , getResources().getString(R.string.act_account_accInfoUpdated)
                , 1);
        Intent i = new Intent(AccountActivity.this, HomeActivity.class);
        startActivity(i);
    }
    public void setProfPicForSplasher(){
        ParseQuery<ParseObject> profileQuery = ParseQuery.getQuery("Profile");
        profileQuery.whereEqualTo("email", userClassQuery.userName());
        profileQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject splasherObj : objects){
                            splasherObj.put("splasherProfPic", profileFile);
                            splasherObj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        finishAll();
                                    }else{
                                        uploadFailedMsg();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    public void processPicture(){
        //imageFileLocation
        //updatedProfilePic
        Bitmap updatedProfilePic;
        Bitmap fixedUpdatedProfPic = null;
        if (chosenFromGal){
            try {
                updatedProfilePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        Uri.parse(choosenGalFileLocation));
                Log.i("fromGallery", "is " + choosenGalFileLocation);
                localPictureString = choosenGalFileLocation;
                fixedUpdatedProfPic = ImageRotation.rotateImageIfRequired(updatedProfilePic,
                        AccountActivity.this, Uri.parse(localPictureString));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fixedUpdatedProfPic != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                fixedUpdatedProfPic.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                byte[] byteArray = stream.toByteArray();
                profileFile = new ParseFile("ProfilePic.jpeg", byteArray);
            }
        }else{
            updatedProfilePic = BitmapFactory.decodeFile(rawImageString);
            Log.i("fromCam", "is " + rawImageString);
            localPictureString = rawImageString + "&camera";
            if (updatedProfilePic != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap finalProfilePic = null;
                try {
                    Uri uriImageFileLocation = Uri.parse(rawImageString);
                    Log.i("uriFileLocation", "is: " + String.valueOf(uriImageFileLocation));
                    finalProfilePic = ImageRotation.rotateImageIfRequired(updatedProfilePic,
                            AccountActivity.this, uriImageFileLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (finalProfilePic != null) {
                    finalProfilePic.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    byte[] byteArray = stream.toByteArray();
                    profileFile = new ParseFile("ProfilePic.jpeg", byteArray);
                }
            }
        }
    }
    public void getProfilePicture(){
        ParseFile profPicFromFile = userClassQuery.currentUserObject()
                .getParseFile("localProfilePic");
        if(profPicFromFile != null) {
            String profPicFromFileString = profPicFromFile.getUrl();
            Log.i("profPicLocalURI", "is " + profPicFromFileString);
            glideImagePlacement.roundImagePlacementFromString
                    (profPicFromFileString, cAccountPicHolder);
        }
    }
    public void editAccDetails(){
        if(!fieldsOnEdit) {
            android.support.v7.app.AlertDialog.Builder editStuff3Dialog =
                    new android.support.v7.app.AlertDialog.Builder(AccountActivity.this);
            editStuff3Dialog.setTitle(getResources().getString(R.string.act_account_editTitle));
            editStuff3Dialog.setIcon(android.R.drawable.ic_dialog_alert);
            editStuff3Dialog.setMessage(getResources().getString(R.string.act_account_editMessage));
            editStuff3Dialog.setNegativeButton(getResources().getString(R.string
                            .act_account_editCancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fieldsOnEdit = false;
                    }
                });
            editStuff3Dialog.setPositiveButton(getResources().getString(R.string.act_account_editOk),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setEditTextState(true);
                        fieldsOnEdit = true;
                    }
                });
            editStuff3Dialog.setCancelable(false);
            editStuff3Dialog.show();
        }
    }
    //Choose from gallery//
    public void accChooseFromGallery(View v){
        StoragePermission.isStoragePermissionGranted(ctx,AccountActivity.this);
        if (StoragePermission.isStoragePermissionGranted(ctx,AccountActivity.this)) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 90);
        }else{
            toastMessages.productionMessage(getApplicationContext()
            ,getResources().getString(R.string.carOwner_act_java_pressAgain),1);
        }
    }
    //-------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 80) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //finalBitmap1 = BitmapFactory.decodeFile(imageFileLocation);
                glideImagePlacement.roundImagePlacementFromUri(imageFileLocation, cAccountPicHolder);
                picChosen = true;
            }else if (requestCode == 90) {
                Uri requestedUri = data.getData();
                choosenGalFileLocation = String.valueOf(requestedUri);
                if (requestedUri != null) {
                    glideImagePlacement.roundImagePlacementFromUri(requestedUri, cAccountPicHolder);
                    picChosen = true;
                    chosenFromGal = true;
                }
            }
        }
    }
    //Take a picture//
    public void accTakePhoto(View v) {
        StoragePermission.isWrittingToStoragePermissionGranted(ctx,AccountActivity.this);
        if(StoragePermission.isWrittingToStoragePermissionGranted(ctx,AccountActivity.this)) {
            imageFileLocation = createImageUri("_profile_pic");
            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileLocation);
            startActivityForResult(i, 80);
        }else{
            toastMessages.productionMessage(getApplicationContext()
            ,getResources().getString(R.string.carOwner_act_java_pressAgain),1);
        }
    }
    public void createImageGallery(){
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //path = Environment.getExternalStorageDirectory() + "/" + "Splash/";
        String GALLERY_LOCATION = "Splasher";
        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if(!mGalleryFolder.exists()){
            //noinspection ResultOfMethodCallIgnored
            mGalleryFolder.mkdirs();
        }
        Log.i("AND", mGalleryFolder.toString());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                toastMessages.productionMessage(getApplicationContext()
                ,getResources().getString(R.string.cameraIntent_act_java_permissionGranted),1);
            } else {
                toastMessages.productionMessage(getApplicationContext()
                ,getResources().getString(R.string.cameraIntent_act_java_permissionToWrite),1);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public Uri createImageUri(String prefix) {
        String timeStamp = DateFormat.getDateTimeInstance().format(new Date());
        String imageFileName = prefix + timeStamp + "_";
        //For imageFileName2:------------
        Random rand = new Random();
        int min = 1, max = 10000;
        int randomNumForFile = rand.nextInt((max - min) + 1) + min;
        String imageFileName2 = prefix + randomNumForFile + "_";
        //-------------------------------
        File image1;
        Uri imageUri = null;
        try {
            File rawImageFile;
            if (Build.VERSION.SDK_INT > 19) {
                image1 = File.createTempFile(imageFileName, ".jpg", mGalleryFolder);
                rawImageFile = image1;
                rawImageString = rawImageFile.getAbsolutePath();
                Log.i("rawImageString", rawImageString);
                String authorities = getApplicationContext().getPackageName() + ".fileProvider";
                imageUri = FileProvider.getUriForFile(this, authorities, image1);
                Log.i("imageUriNew", imageUri.toString());
                Log.i("AND3", image1.toString());
            } else {
                image1 = File.createTempFile(imageFileName2, ".jpg", mGalleryFolder);
                rawImageFile = image1;
                rawImageString = rawImageFile.getAbsolutePath();
                Log.i("rawImageString", rawImageString);
                String authorities = getApplicationContext().getPackageName() + ".fileProvider";
                imageUri = FileProvider.getUriForFile(this, authorities, image1);
                Log.i("imageUriNew", imageUri.toString());
                Log.i("AND2", image1.toString());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return imageUri;
    }
    //-----------------//
}
