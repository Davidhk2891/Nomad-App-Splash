package com.nomadapp.splash.ui.activity.standard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.server.parseserver.MessageClassInterface;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.ProfileClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.MessageClassSend;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ContactUsActivity extends AppCompatActivity {

    private Context ctx = ContactUsActivity.this;

    private TextView mContactUsUsername, mContactUsEmail, mMessageConEdit;
    private String currentUsername, currentEmail;

    private Bitmap socialBitmapGal;
    private TextView mPhotoUploadCUTextView;

    private byte[] bytes1;

    private UserClassQuery userClassQuery = new UserClassQuery(ctx);
    private ToastMessages toastMessages = new ToastMessages();
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(ctx);

    private int secLockInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        mContactUsUsername = findViewById(R.id.contactUsUsername);
        mContactUsEmail = findViewById(R.id.contactUsEmail);
        mMessageConEdit = findViewById(R.id.messageConEdit);
        mPhotoUploadCUTextView = findViewById(R.id.photoUploadCUTextView);

        getUserData();
    }

    public void getUserData(){
        if (userClassQuery.userExists()){
            currentUsername = userClassQuery.userName();
            currentEmail = userClassQuery.email();
            //get profile here user here
            UserClassQuery ucq = new UserClassQuery(ctx);
            ProfileClassQuery pcq = new ProfileClassQuery(ctx);
            String splasher = "splasher";
            if (ucq.userIsCarOwnerOrSplasher(splasher)){
                pcq.fetchAllSplashersInfo(userClassQuery.userName(), new ProfileClassInterface
                        .allSplashersInfo() {
                    @Override
                    public void getInfo(ParseObject object) {
                        mContactUsUsername.setText(object.get("username").toString());
                        mContactUsEmail.setText(currentEmail);
                    }

                    @Override
                    public void afterLoop(List<ParseObject> objects) {

                    }
                });
            }else{
                mContactUsUsername.setText(currentUsername);
                mContactUsEmail.setText(currentEmail);
            }
        }
    }

    public void sendMessage(View view){
        if (userClassQuery.userExists()){
            if (!mMessageConEdit.getText().toString().isEmpty()){
                boxedLoadingDialog.showLoadingDialog();
                String lockedMessage = mMessageConEdit.getText().toString();
                MessageClassSend mcs = new MessageClassSend();
                mcs.sendMessagesToServer2(currentUsername, currentEmail, lockedMessage
                        , mPhotoUploadCUTextView.getText().toString(), compressedMessageFile()
                        , new MessageClassInterface() {
                            @Override
                            public void afterMessageSent(ParseException e) {
                                if (e == null) {
                                    toastMessages.productionMessage(getApplicationContext(),
                                            getResources().getString(R.string
                                                    .act_contactUs_messageSend), 1);
                                    finish();
                                } else {
                                    Log.i("messageSendError", "is " + e.getMessage());
                                    toastMessages.productionMessage(getApplicationContext(),
                                            getResources().getString(R.string
                                                    .act_contactUs_messageNotSend), 1);
                                    boxedLoadingDialog.hideLoadingDialog();
                                }
                            }
                        });

            }else{
                toastMessages.productionMessage(getApplicationContext(),
                        getResources().getString(R.string.act_contactUs_pleaseWriteAMsg)
                        ,1);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void photoUpload(View v){
        isStoragePermissionGranted();
        if (isStoragePermissionGranted()){
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 1){
                Uri targetUriID = data.getData();
                if (targetUriID != null) {
                    String onlyFileNameID = targetUriID.toString().substring(targetUriID.getPath()
                            .lastIndexOf(File.separator) + 1);
                    String onlyFileNameID2 = onlyFileNameID.substring(onlyFileNameID
                            .lastIndexOf("/") + 1);
                    try {
                        socialBitmapGal = MediaStore.Images.Media.getBitmap(this
                                .getContentResolver(), targetUriID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("photoUpID", targetUriID.toString());
                    Log.i("photoUp name onlyID", onlyFileNameID);
                    Log.i("uriNamephotoID", onlyFileNameID2);
                    String filePrefix = "message_file_" + onlyFileNameID2;
                    mPhotoUploadCUTextView.setText(filePrefix);
                }
            }
        }
    }

    public ParseFile compressedMessageFile(){
        if (socialBitmapGal != null) {
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            socialBitmapGal.compress(Bitmap.CompressFormat.JPEG, 20, stream1);
            bytes1 = stream1.toByteArray();
        }
        return new ParseFile("messageFile.png", bytes1);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission. READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission","Permission is granted, press again");
                return true;
            } else {
                Log.i("permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest
                        .permission. READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.i("permission","Permission is granted, press again");
            return true;
        }
    }

    public void toSellerApp(View view){
        Log.i("secretLevel", " is tapped");
        if (userClassQuery.userExists()) {
            if (secLockInt == 0 || secLockInt == 1 || secLockInt == 2 || secLockInt == 3
                    || secLockInt == 4 || secLockInt == 5) {
                secLockInt++;
                Log.i("secretLevel", " is at: " + String.valueOf(secLockInt));
            } else if (secLockInt == 6) {
                secLockInt = 0;
                toastMessages.productionMessage(ContactUsActivity.this
                        , "Secret Level Unlocked", 1);
                Intent intent2 = new Intent(ContactUsActivity.this,
                        SplasherApplicationActivity.class);
                startActivity(intent2);
            }
        }else{
            toastMessages.productionMessage(ContactUsActivity.this
                    ,"Not yet", 1);
        }
    }
}
