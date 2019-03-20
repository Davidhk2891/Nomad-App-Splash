package com.nomadapp.splash.ui.activity.standard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.constants.PaymeConstants;
import com.nomadapp.splash.model.constants.serverconstants.ProfileConstants;
import com.nomadapp.splash.utils.sysmsgs.DialogAcceptInterface;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.questiondialogs.AlertDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SplasherOnboarding extends AppCompatActivity {

    private TextView mSplasher_onboarding_name, mSplasher_onboarding_last,
            mSplasher_onboarding_email, mSplasher_onboarding_phone, mSplasher_onboarding_id,
            mSplasher_onboarding_address, mSplasher_onboarding_password,
            mSplasher_onboarding_repeat_password;

    private BoxedLoadingDialog boxedLoadingDialog;
    private ToastMessages toastMessages = new ToastMessages();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_onboarding);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        createSplasherAccount();
    }

    private void createSplasherAccount(){
        final ToastMessages toastMessages = new ToastMessages();
        boxedLoadingDialog = new BoxedLoadingDialog(SplasherOnboarding.this);
        mSplasher_onboarding_name = findViewById(R.id.splasher_onboarding_name);
        mSplasher_onboarding_last = findViewById(R.id.splasher_onboarding_last);
        mSplasher_onboarding_email = findViewById(R.id.splasher_onboarding_email);
        mSplasher_onboarding_phone = findViewById(R.id.splasher_onboarding_phone);
        mSplasher_onboarding_id = findViewById(R.id.splasher_onboarding_id);
        mSplasher_onboarding_address = findViewById(R.id.splasher_onboarding_address);
        mSplasher_onboarding_password = findViewById(R.id.splasher_onboarding_password);
        mSplasher_onboarding_repeat_password = findViewById
                (R.id.splasher_onboarding_repeat_password);

        Button mSplasher_onboarding_button = findViewById(R.id.splasher_onboarding_button);

        mSplasher_onboarding_email.requestFocus();

        mSplasher_onboarding_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mSplasher_onboarding_name.getText().toString().isEmpty() ||
                    mSplasher_onboarding_last.getText().toString().isEmpty() ||
                    mSplasher_onboarding_email.getText().toString().isEmpty() ||
                    mSplasher_onboarding_phone.getText().toString().isEmpty() ||
                    mSplasher_onboarding_id.getText().toString().isEmpty() ||
                    mSplasher_onboarding_address.getText().toString().isEmpty() ||
                    mSplasher_onboarding_password.getText().toString().isEmpty() ||
                    mSplasher_onboarding_repeat_password.getText().toString().isEmpty()) {
                    toastMessages.productionMessage(SplasherOnboarding.this
                            , getResources().getString
                                    (R.string.becomeSplasher_act_java_pleaseFillAll), 1);
                } else if(!mSplasher_onboarding_password.getText().toString()
                        .equals(mSplasher_onboarding_repeat_password.getText().toString())) {
                    toastMessages.productionMessage(SplasherOnboarding.this
                            ,getResources().getString(R.string
                                    .becomeSplasher_act_java_bothPasswords), 1);
                } else if(!mSplasher_onboarding_email.getText().toString().contains("@")
                            || !mSplasher_onboarding_email.getText().toString().contains(".com")) {
                        toastMessages.productionMessage(SplasherOnboarding.this
                                ,getResources().getString(R.string
                                        .becomeSplasher_act_java_pleaseEnterValidEmail),
                                1);
                }else{
                    boxedLoadingDialog.showLoadingDialog();
                    createParseSplasherUser();
                }
            }
        });
    }

    private void createParseSplasherUser(){
        final String splasher = ProfileConstants.CLASS_PROFILE_SPLAHER;//<-- TYPE OF USER
        ParseUser splasherUser = new ParseUser();
        splasherUser.setUsername(mSplasher_onboarding_email.getText().toString());
        splasherUser.setPassword(mSplasher_onboarding_password.getText().toString());
        splasherUser.setEmail(mSplasher_onboarding_email.getText().toString());
        splasherUser.put("city", "not set");
        splasherUser.put("phonenumber", mSplasher_onboarding_phone.getText().toString());
        splasherUser.put("name", mSplasher_onboarding_name.getText().toString());
        splasherUser.put("lastname", mSplasher_onboarding_last.getText().toString());
        splasherUser.put("CarOwnerOrSplasher", splasher);
        splasherUser.signUpInBackground(new SignUpCallback(){
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    createSplasherProfile();
                }else{
                    signUpFailed(e);
                }
            }
        });
    }

    private void createSplasherDocument(){
        final ParseObject documents = new ParseObject("Documents");
        documents.put("username", mSplasher_onboarding_email.getText().toString());
        documents.put("sellerId", PaymeConstants.SPLASH_SELLER_KEY);
        documents.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    startActivity(new Intent(
                            SplasherOnboarding.this
                            , SignUpLogInActivity.class));
                }else{
                    signUpFailed(e);
                }
            }
        });
    }

    private void createSplasherProfile(){

        ParseObject profile = new ParseObject(ProfileConstants.CLASS_PROFILE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_USERNAME,
                mSplasher_onboarding_name.getText().toString() + " " +
                mSplasher_onboarding_last.getText().toString());
        profile.put(ProfileConstants.CLASS_PROFILE_COL_CAR_OWNER_OR_SPLASHER,
                ProfileConstants.CLASS_PROFILE_SPLAHER);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_EMAIL, mSplasher_onboarding_email
                .getText().toString());
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SPLASHER_TYPE,
                ProfileConstants.CLASS_PROFILE_SET_INDEPENDENT);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_OLD_AVG_RATING
                , ProfileConstants.CLASS_PROFILE_OLD_AVG_RATING);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_WASHES,
                ProfileConstants.CLASS_PROFILE_WASHES);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_WASHES_CANCELED,
                ProfileConstants.CLASS_PROFILE_WASHES_CANCELED);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_NUMERICAL_BADGE,
                ProfileConstants.CLASS_PROFILE_NUMERICAL_BADGE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SET_RPICE,
                ProfileConstants.CLASS_PROFILE_SET_PRICE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SET_PRICE_E_INT,
                ProfileConstants.CLASS_PROFILE_SET_PRICE_E_INT);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SET_PRICE_MOTO,
                ProfileConstants.CLASS_PROFILE_SET_PRICE_MOTO);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_SERVICE_RANGE,
                ProfileConstants.CLASS_PROFILE_SERVICE_RANGE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SERVICE_EC,
                ProfileConstants.CLASS_PROFILE_SERVICE_EC);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_EC_COORDS,
                ProfileConstants.CLASS_PROFILE_ECCOORDS);

        profile.put(ProfileConstants.CLASS_PROFILE_SET_COL_SPLASHER_PROF_PIC,
                ProfileConstants.CLASS_PROFILE_SPLASHER_PROF_PIC_DEFAULT//TEST//
                        (SplasherOnboarding.this));

        profile.put(ProfileConstants.CLASS_PROFILE_COL_STATUS,
                ProfileConstants.CLASS_PROFILE_SET_ACTIVE);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_VERIFIED,
                ProfileConstants.CLASS_PROFILE_SET_FALSE);

        profile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    boxedLoadingDialog.hideLoadingDialog();
                    AlertDialog alertDialog = new AlertDialog(SplasherOnboarding.this);
                    alertDialog.generalPurposeQuestionDialog(SplasherOnboarding.this
                            , getResources().getString(R.string
                                    .splasher_onboarding_alert_accBeing_title)
                            , getResources().getString(R.string
                                    .splasher_onboarding_alert_accBeing_message)
                            , getResources().getString(R.string
                                    .splasher_onboarding_alert_accBeing_ok)
                            , null, new DialogAcceptInterface() {
                                @Override
                                public void onAcceptOption() {
                                    ParseUser.logOutInBackground(new LogOutCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null){
                                                createSplasherDocument();
                                            }
                                        }
                                    });
                                }
                            });
                }else{
                    signUpFailed(e);
                }
            }
        });
    }

    private void signUpFailed(ParseException e){
        boxedLoadingDialog.hideLoadingDialog();
        Log.i("splasherSignUp", "SignUp Failed");
        toastMessages.productionMessage(getApplicationContext(),"SignUp Failed."
                + " " + e.getMessage(), 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
