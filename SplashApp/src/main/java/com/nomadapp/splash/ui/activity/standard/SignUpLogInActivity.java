/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.nomadapp.splash.ui.activity.standard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.ProfileClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.ui.activity.standard.web.WebActivity;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.ForcedAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.nomadapp.splash.utils.sysmsgs.ConnectionLost;

public class SignUpLogInActivity extends AppCompatActivity implements View.OnKeyListener{

    //First screen after Splash Screen
    private RelativeLayout cRelativeTwo;
    //--------------------------------

    private TextView cWelcomeback, cEnterAccountInfo, cOr;
    private Button cLogInSignUpNow;
    private EditText cUserInput;
    private EditText cPassword, cPassword2;
    private EditText cEmailInput;
    private TextView cBecomeASplash, cVisiblePasswordHelper;
    private CheckBox mAgreementTOU;
    private TextView mAgreementTOULink;
    private LinearLayout mAgreementTOULinear;
    private boolean signUpActive = false;

    //Facebook SDK//
    private Button loginButton;
    private String fbName,fbEmail;
    private String fbProfilePicString;
    final List<String> permissions = Arrays.asList("public_profile", "email");
    //------------//

    //Forgot Password//
    private TextView cForgotPassword;
    private RelativeLayout cForgotPassRelative;
    private EditText cForgotPassUsername, cForgotPassEmail;
    //---------------//

    private ToastMessages toastMessages = new ToastMessages();
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(SignUpLogInActivity.this);
    private ForcedAlertDialog forcedAlertDialog = new ForcedAlertDialog(SignUpLogInActivity.this);
    private ConnectionLost clm = new ConnectionLost(SignUpLogInActivity.this);
    ProfileClassQuery profileClassQuery = new ProfileClassQuery(SignUpLogInActivity.this);

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            if (!signUpActive) {
                logInSignUpNow(v);
            }
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        return false;
    }

    public void signUpLogic(){
        signUpActive = true;
        cRelativeTwo.setVisibility(View.GONE);
        cWelcomeback.setText(getResources().getString(R.string.main_act_java_welcome));
        cEnterAccountInfo.setText(getResources().getString(R.string.main_act_java_letsGetStarted));
        cLogInSignUpNow.setVisibility(View.VISIBLE);
        cLogInSignUpNow.setText(getResources().getString(R.string.main_act_java_signup));
        cUserInput.setVisibility(View.VISIBLE);
        cPassword.setVisibility(View.VISIBLE);
        cEmailInput.setVisibility(View.VISIBLE);
        cPassword2.setVisibility(View.VISIBLE);
        cVisiblePasswordHelper.setVisibility(View.VISIBLE);
        mAgreementTOULinear.setVisibility(View.VISIBLE);
        //Facebook SDK//
        loginButton.setVisibility(View.VISIBLE);
        //------------//
        cOr.setVisibility(View.VISIBLE);
        cForgotPassword.setVisibility(View.GONE);

        cEmailInput.requestFocus();

        editTextsState(true);
    }

    public void signUp(View view){
        signUpLogic();
    }

    public void logIn(View view){
        signUpActive = false;
        cOr.setVisibility(View.VISIBLE);
        cUserInput.setVisibility(View.VISIBLE);
        cPassword.setVisibility(View.VISIBLE);
        cRelativeTwo.setVisibility(View.GONE);
        cEmailInput.setVisibility(View.GONE);
        cPassword2.setVisibility(View.GONE);
        cLogInSignUpNow.setVisibility(View.VISIBLE);
        cVisiblePasswordHelper.setVisibility(View.GONE);
        cLogInSignUpNow.setText(getResources().getString(R.string.main_act_java_login));
        cWelcomeback.setText(getResources().getString(R.string.act_main_welcomeBack));
        cEnterAccountInfo.setText(getResources().getString(R.string.act_main_enterYourAccountInfo));
        //Facebook SDK//
        loginButton.setVisibility(View.VISIBLE);
        //------------//
        cForgotPassword.setVisibility(View.VISIBLE);

        cUserInput.requestFocus();

        editTextsState(true);
    }

    public void logInSignUpNow(View view){

        if (cLogInSignUpNow.getText().equals("LogIn") || cLogInSignUpNow.getText().equals("התחברות")) {
            final String splasher = "splasher";
            cUserInput = findViewById(R.id.userNameInput);
            cPassword = findViewById(R.id.passwordInput);
            if (cUserInput.getText().toString().isEmpty() || cPassword.getText().toString().isEmpty()) {
                toastMessages.productionMessage(getApplicationContext()
                        ,getResources().getString(R.string.main_act_java_userPassReq),1);
            } else {
                boxedLoadingDialog.showLoadingDialog();
                ParseUser.logInInBackground(cUserInput.getText().toString(), cPassword.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null && e == null) {
                                    if (user.getString("CarOwnerOrSplasher").equals(splasher)) {
                                        profileClassQuery.splasherAccVerification(cUserInput
                                                .getText()
                                                .toString(), new ProfileClassInterface
                                                .verificationStatus() {
                                            @Override
                                            public void accVerifiedStatus(String splasherStatus) {
                                                Log.i("purple", "is " + splasherStatus);
                                                if (splasherStatus.equals("true")){
                                                    Log.i("LogIn", "splasherAcc ver "
                                                            + splasherStatus + " LogIn Successful");
                                                    //TEST TEST TEST WHOLE SYSTEM. SPLASHER SIGN UP,
                                                    //PREVENTION FROM LOGIN IN, ENABALING
                                                    //LANDS IN lOGIN FAILED WHEN IT SHOULD LAND IN LOGIN SUCC. FIX
                                                    navigateBackFirst();
                                                }else{
                                                    Log.i("LogIn", "splasherAcc ver "
                                                            + splasherStatus + " LogIn Failed");
                                                    boxedLoadingDialog.hideLoadingDialog();
                                                    accNotVerifiedNoti();
                                                    //tell user their acc is still under verification
                                                }
                                            }
                                        });
                                    } else {
                                        Log.i("LogIn", "LogIn Successful");
                                        navigateBackFirst();
                                    }
                                } else {
                                    boxedLoadingDialog.hideLoadingDialog();
                                    Log.i("LogIn", "Failed: " + e.getMessage());
                                    if (e.getMessage().contains("Invalid username/password.")) {
                                        toastMessages.productionMessage(getApplicationContext()
                                        ,e.getMessage(),1);
                                    }
                                }
                            }
                        });
            }
        } else if (cLogInSignUpNow.getText().equals("SignUp") || cLogInSignUpNow.getText().equals("הירשם")) {

            final String carOwner = "carOwner";//<-- TYPE OF USER : carOwner

            cUserInput = findViewById(R.id.userNameInput);
            cPassword = findViewById(R.id.passwordInput);
            cPassword2 = findViewById(R.id.passwordInput2);
            cEmailInput = findViewById(R.id.emailInput);

            if (cUserInput.getText().toString().isEmpty() || cPassword.getText().toString().isEmpty() ||
                    cPassword2.getText().toString().isEmpty() || cEmailInput.getText().toString().isEmpty()) {

                toastMessages.productionMessage(getApplicationContext()
                        , getResources().getString(R.string.main_act_java_userPassEmailReq),1);

            } else if (!cPassword.getText().toString().equals(cPassword2.getText().toString())) {

                toastMessages.productionMessage(getApplicationContext()
                        , getResources().getString(R.string.main_act_java_bothPassMustMatch),1);

            } else if (!cEmailInput.getText().toString().contains("@")) {

                toastMessages.productionMessage(getApplicationContext()
                        ,getResources().getString(R.string.main_act_java_enterValidEmail),1);

            } else {

                if (mAgreementTOU.isChecked()) {
                    boxedLoadingDialog.showLoadingDialog();
                    ParseUser user = new ParseUser();
                    user.setUsername(cUserInput.getText().toString());
                    user.setPassword(cPassword.getText().toString());
                    user.setEmail(cEmailInput.getText().toString());
                    user.put("CarOwnerOrSplasher", carOwner);//<-- SELECTOR OF WHAT TYPE OF USER
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("SignUp", "SignUp Successful");
                                //Toast.makeText(getApplicationContext(), "This Phone's Serial number: " +
                                //Build.SERIAL, Toast.LENGTH_LONG).show();
                                navigateBackFirstOnSignUp();
                            } else {
                                boxedLoadingDialog.hideLoadingDialog();
                                Log.i("SignUp", "SignUp Failed");
                                toastMessages.productionMessage(getApplicationContext()
                                        ,e.getMessage(),1);
                            }
                        }
                    });
                }else{
                    forcedAlertDialog.mustAcceptTOUFirst();
                }
            }
        }
    }

    private void accNotVerifiedNoti(){
        UserClassQuery userClassQuery = new UserClassQuery(SignUpLogInActivity.this);
        if(userClassQuery.userExists()){
            userClassQuery.logOutUserNoEP();
            ForcedAlertDialog forcedAlertDialog = new ForcedAlertDialog
                    (SignUpLogInActivity.this);
            forcedAlertDialog.generalPurposeForcedDialogNoAction(
                    getResources().getString(R.string.main_act_java_accountNotVerified)
                    , getResources().getString(R.string.main_act_java_yourAccountHasntBeen)
                    ,getResources().getString(R.string.main_act_java_ok));
        }
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            try {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (NullPointerException np1) {
                np1.printStackTrace();
            }
        }
    }

    public void navigateBackFirst(){
        Intent firstIntent = new Intent(SignUpLogInActivity. this, HomeActivity. class);
        startActivity(firstIntent);
    }

    public void navigateBackFirstOnSignUp(){
        Intent firstIntent = new Intent(SignUpLogInActivity. this, HomeActivity. class);
        firstIntent.putExtra("addressKey", "address");
        startActivity(firstIntent);
    }

    //public void fbLogin(View view){
        //Facebook SDK// Printing unique Key Hash to Console
        // Add code to print out the key hash
        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.parse.starter",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("UniqueKeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
        */
        //------------------------------------------------//
    //}

    public void parseFbLogin(){
        ParseFacebookUtils.logInWithReadPermissionsInBackground(SignUpLogInActivity.this
                , permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            boxedLoadingDialog.hideLoadingDialog();
                            Log.d("fbLogin", "The user cancelled the Facebook login.");
                            if (err.getMessage() != null) {
                                boxedLoadingDialog.hideLoadingDialog();
                                Log.d("fbError", "is " + err.getMessage());
                                Log.d("fbError2", "is " + err.getCode());
                                Log.d("fbError3", "is " + err.getCause());
                            }
                        } else if (user.isNew()) {
                            boxedLoadingDialog.hideLoadingDialog();
                            Log.d("fbLogin", "User signed up and logged in through Facebook!");
                            getUserDetailFromFB();
                        } else {
                            Log.d("fbLogin", "User logged in through Facebook!");
                            getUserDetailFromParseReturningUser();
                        }
                    }
                });
    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signuplogin);

    if (getSupportActionBar() != null)
      getSupportActionBar().hide();

      //THIS METHOD MAY NOT ALLOW TO LOG IN THORUGH FACEBOOK RIGHT AFTER LOGIN OUT FROM SPLASH.

      //Eventually you gonna have to find a better way to handle this//
      try {
          ParseFacebookUtils.initialize(SignUpLogInActivity.this);
          Log.d("fbInit","yes");
      }catch(IllegalStateException e){
          e.printStackTrace();
      }
      //-------------------------------------------------------------//

      //user = new ParseUser(); <--This goes in "Become a Splasher" signUp sheet

      //final String splasher = "splasher";//<-- TYPE OF USER : carOwner <--This goes in "Become a Splasher" signUp sheet

      //First screen after splash screen
      cRelativeTwo = findViewById(R.id.relativeTwo);
      //--------------------------------

      cEnterAccountInfo = findViewById(R.id.enterAccountInfo);
      cWelcomeback = findViewById(R.id.welcomeBack);
      RelativeLayout cBackgroundRelativelayout = findViewById(R.id.BackgroundRelativeLayout);
      ImageView cMainLogo2 = findViewById(R.id.mainLogo2);
      cUserInput = findViewById(R.id.userNameInput);
      cPassword = findViewById(R.id.passwordInput);
      cPassword2 = findViewById(R.id.passwordInput2);
      cEmailInput = findViewById(R.id.emailInput);
      cBecomeASplash = findViewById(R.id.becomeASplash);
      cLogInSignUpNow = findViewById(R.id.logInNow);
      cVisiblePasswordHelper = findViewById(R.id.visiblePasswordHelper);
      mAgreementTOU = findViewById(R.id.agreementTOU);
      mAgreementTOULink = findViewById(R.id.agreementTOULink);
      mAgreementTOULinear = findViewById(R.id.agreementTOULinear);
      //Facebook SDK//
      loginButton = findViewById(R.id.fb_login_button);
      //------------//
      cOr = findViewById(R.id.or);

      cUserInput.setVisibility(View.GONE);
      cPassword.setVisibility(View.GONE);
      cPassword2.setVisibility(View.GONE);
      cOr.setVisibility(View.GONE);
      cEmailInput.setVisibility(View.GONE);
      cVisiblePasswordHelper.setVisibility(View.GONE);
      cLogInSignUpNow.setVisibility(View.GONE);
      mAgreementTOULinear.setVisibility(View.GONE);
      //Facebook SDK//
      loginButton.setVisibility(View.GONE);
      //------------//
      cRelativeTwo.setVisibility(View.VISIBLE);
      editTextsState(false);

      //Forgot Password//
      cForgotPassRelative = findViewById(R.id.forgotPassRelative);
      cForgotPassUsername = findViewById(R.id.forgotPassUserName);
      cForgotPassEmail = findViewById(R.id.forgotPassEmail);
      cForgotPassword = findViewById(R.id.forgotPassword);
            //--//
      cForgotPassRelative.setVisibility(View.GONE);
      //---------------//

      //English and Hebre definer//
      /*
      cUserInput = (EditText) findViewById(R.id.userNameInput);
      cPassword = (EditText) findViewById(R.id.passwordInput);
      cPassword2 = (EditText) findViewById(R.id.passwordInput2);
      cEmailInput = (EditText) findViewById(R.id.emailInput);
       */
      if(Locale.getDefault().getDisplayLanguage().equals("עברית")) {

          cUserInput.setGravity(Gravity.END);//<<<Needed to RTL
          cUserInput.setGravity(Gravity.RIGHT);//<<<Needed to RTL
          cUserInput.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);//<<<Needed to RTL

          cPassword.setGravity(Gravity.END);
          cPassword.setGravity(Gravity.RIGHT);
          cPassword.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

          cPassword2.setGravity(Gravity.END);
          cPassword2.setGravity(Gravity.RIGHT);
          cPassword2.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

          cEmailInput.setGravity(Gravity.END);
          cEmailInput.setGravity(Gravity.RIGHT);
          cEmailInput.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

      }else if(Locale.getDefault().getDisplayLanguage().equals("English")){

          cUserInput.setGravity(Gravity.START);//<<<Needed to RTL
          cUserInput.setGravity(Gravity.LEFT);//<<<Needed to RTL
          cUserInput.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);//<<<Needed to RTL

          cPassword.setGravity(Gravity.START);
          cPassword.setGravity(Gravity.LEFT);
          cPassword.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

          cPassword2.setGravity(Gravity.START);
          cPassword2.setGravity(Gravity.LEFT);
          cPassword2.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

          cEmailInput.setGravity(Gravity.START);
          cEmailInput.setGravity(Gravity.LEFT);
          cEmailInput.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

      }
      //-------------------------//

      //Facebook SDK//
      loginButton.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
              try {
                  if (signUpActive) {
                      if (mAgreementTOU.isChecked()) {
                          boxedLoadingDialog.showLoadingDialog();
                          parseFbLogin();
                      } else {
                          forcedAlertDialog.mustAcceptTOUFirst();
                      }
                  } else {
                      boxedLoadingDialog.showLoadingDialog();
                      parseFbLogin();
                  }
              }catch(NullPointerException e){
                  e.printStackTrace();
              }
          }
      });
      //-------------//

      cBecomeASplash.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
                Intent intent = new Intent(SignUpLogInActivity. this,
                        SplasherApplicationActivity. class);
                startActivity(intent);
          }
      });

      cBackgroundRelativelayout.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
              hideKeyboard();
          }
      });

      cMainLogo2.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
              hideKeyboard();
          }
      });

      cPassword.setOnKeyListener(new View.OnKeyListener() {
          @Override
          public boolean onKey(View v, int keyCode, KeyEvent event) {
              if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                  if (cLogInSignUpNow.getText().equals("SignUp") || cLogInSignUpNow.getText()
                          .equals("הירשם")) {
                      cPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                  }else{
                      logInSignUpNow(v);
                  }
              }
              return false;
          }
      });

      cPassword2.setOnKeyListener(SignUpLogInActivity.this);
      ParseAnalytics.trackAppOpenedInBackground(getIntent());
      clm.connectivityStatus(SignUpLogInActivity.this);
      checkForQuickTourKey();
      goToISSF();
  }

  public void editTextsState(boolean state){
      cUserInput.setClickable(state);
      cPassword.setClickable(state);
      cPassword2.setClickable(state);
      cEmailInput.setClickable(state);
      mAgreementTOULinear.setClickable(state);
  }

  public void userFormState(boolean state){

      cUserInput.setEnabled(state);
      cUserInput.setClickable(state);

      cPassword.setEnabled(state);
      cPassword.setClickable(state);

      cPassword2.setEnabled(state);
      cPassword2.setClickable(state);

      cEmailInput.setEnabled(state);
      cEmailInput.setClickable(state);

      mAgreementTOULinear.setEnabled(state);
      mAgreementTOULinear.setClickable(state);

      cLogInSignUpNow.setEnabled(state);
      cLogInSignUpNow.setClickable(state);

      loginButton.setEnabled(state);
      loginButton.setClickable(state);

      cBecomeASplash.setEnabled(state);
      cBecomeASplash.setClickable(state);

      cForgotPassword.setEnabled(state);
      cForgotPassword.setClickable(state);

  }

  public void forgotPassword(View view){
      cForgotPassRelative.setVisibility(View.VISIBLE);
      userFormState(false);
  }

  public void fpSend(View view2){
      //!cEmailInput.getText().toString().contains("@") || !cEmailInput.getText().toString()
      // .contains(".com")
      if(cForgotPassUsername.getText().toString().isEmpty() || cForgotPassEmail.getText()
              .toString().isEmpty()){
          toastMessages.productionMessage(getApplicationContext(),getResources()
                  .getString(R.string.main_act_java_nameEmailRequired),1);
      }else if(!cForgotPassEmail.getText().toString().contains("@") || !cForgotPassEmail
              .getText().toString().contains(".com")) {
          toastMessages.productionMessage(getApplicationContext(),getResources()
                  .getString(R.string.main_act_java_enterValidEmail),1);
      }else{
          ParseUser.requestPasswordResetInBackground(cForgotPassEmail.getText().toString(),
                  new RequestPasswordResetCallback(){
              @Override
              public void done(ParseException e) {
                  if(e == null) {
                      toastMessages.productionMessage(getApplicationContext(),getResources()
                              .getString(R.string.main_act_java_emailSent),1);
                  }else{
                      toastMessages.productionMessage(getApplicationContext()
                              ,e.getMessage(),1);
                  }
              }
          });
          cForgotPassRelative.setVisibility(View.GONE);
          cForgotPassUsername.setText("");
          cForgotPassEmail.setText("");
          userFormState(true);
      }
  }

  public void fpCancel(View view3){
      cForgotPassRelative.setVisibility(View.GONE);
      cForgotPassUsername.setText("");
      cForgotPassEmail.setText("");
      userFormState(true);
  }

  public void checkForQuickTourKey(){
        Bundle qtKey = getIntent().getExtras();
        if (qtKey != null){
            String qtKeyString = qtKey.getString("quickTourKey");
            if (qtKeyString != null && qtKeyString.equals("on")){
                signUpLogic();
            }
        }
  }

  //Facebook SDK SignUp logic//
    public void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if(response != null) {
                    try {
                        fbName = object.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        fbEmail = object.getString("email");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject data = response.getJSONObject();
                        if (data.has("picture")) {
                            fbProfilePicString = data.getJSONObject("picture")
                                    .getJSONObject("data").getString("url");
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    saveNewUser();
                }else{
                    /*
                     * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                     */
                    boxedLoadingDialog.hideLoadingDialog();
                    clm.connectionLostDialog();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }
    public void saveNewUser(){
        try {
            String carOwner2 = "carOwner";
            ParseUser user = ParseUser.getCurrentUser();
            user.setUsername(fbName);
            user.setEmail(fbEmail);
            user.put("fbProfilePic", fbProfilePicString);
            user.put("CarOwnerOrSplasher", carOwner2);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    //go to carOwnerActivity.java
                    if (e == null) {
                        navigateBackFirstOnSignUp();
                    } else {
                        boxedLoadingDialog.hideLoadingDialog();
                        Log.i("SignUp", "Facebook SignUp Failed");
                        toastMessages.productionMessage(getApplicationContext(), e.getMessage()
                                , 1);
                    }
                }
            });
        }catch(IllegalArgumentException iae){
            boxedLoadingDialog.hideLoadingDialog();
            iae.printStackTrace();
            forcedAlertDialog.somethingWentWrongFacebook();
            fbEmail = "";
            fbName = "";
            fbProfilePicString = "";
        }
    }
    public void getUserDetailFromParseReturningUser(){
        ParseUser user = ParseUser.getCurrentUser();
        fbName = user.getUsername();
        fbEmail = user.getEmail();
        navigateBackFirst();
    }
  //Facebook SDK//

  //Facebook SDK//
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
  }
  //Facebook SDK//

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(cRelativeTwo.getVisibility() == View.GONE && cForgotPassRelative.getVisibility() == View.GONE) {

                cRelativeTwo.setVisibility(View.VISIBLE);
                cLogInSignUpNow.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                cUserInput.setVisibility(View.GONE);
                cPassword.setVisibility(View.GONE);
                cPassword2.setVisibility(View.GONE);
                cEmailInput.setVisibility(View.GONE);
                mAgreementTOULinear.setVisibility(View.GONE);
                signUpActive = false;

            } else if(cForgotPassRelative.getVisibility() == View.VISIBLE) {

                cForgotPassRelative.setVisibility(View.GONE);

                cForgotPassUsername.setText("");
                cForgotPassEmail.setText("");

                userFormState(true);

            } else {

                startActivity(new Intent(SignUpLogInActivity.this, HomeActivity.class));

            }
        }
        /*
        if (keyCode == KeyEvent.KEYCODE_HOME) {

            //Do nothing for now
        }
        */
        return false;
    }
    public void toTOU(View view){
        Intent touIntent = new Intent(SignUpLogInActivity.this,WebActivity.class);
        touIntent.putExtra("webKey","termsOfUse");
        startActivity(touIntent);
    }

    private void goToISSF(){
        TextView mBecome_independent_splasher = findViewById(R.id.become_independent_splasher);
        mBecome_independent_splasher.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpLogInActivity.this,
                        SplasherOnboarding.class));
            }
        });
    }
}