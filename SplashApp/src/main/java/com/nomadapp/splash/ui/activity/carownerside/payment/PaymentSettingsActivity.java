package com.nomadapp.splash.ui.activity.carownerside.payment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.payment.paymeapis.buyer.SplashCaptureBuyer;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.ui.activity.carownerside.WashReqParamsActivity;
import com.nomadapp.splash.ui.activity.standard.web.WebActivity;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.ParseUser;

import com.paymeservice.android.PayMe;
import com.paymeservice.android.model.CaptureBuyerRequest;

import java.util.Locale;

public class PaymentSettingsActivity extends AppCompatActivity implements View.OnKeyListener{

    private EditText cCardHolderInput, cPhoneNumberCCInput, cCCNumber,  cExpiryDateNumber,
    cCvvNumber, cIdNumber;

    //PLACE HOLDERS for necessary data to execute capture_buyer
    private String userEmailHolder;
    private String cleanStringNameCC, cleanStringPhoneN, cleanStringCCNumber
            , cleanStringExpiryDate, cleanStringCVV, cleanIdNumber;
    private CheckBox cSaveCardNumber;
    private Button cCcAdded;

    private BoxedLoadingDialog boxedLoadingDialog =
            new BoxedLoadingDialog(PaymentSettingsActivity.this);
    private ToastMessages toastMessages = new ToastMessages();
    private WriteReadDataInFile writeReadDataInFile =
            new WriteReadDataInFile(PaymentSettingsActivity.this);

    private boolean ccMaskEmpty = false;
    private boolean fieldsOnEdit = false;

    private MetricsClassQuery metricsClassQuery = new MetricsClassQuery
            (PaymentSettingsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PREVENT SCREEN SHOTS IN THIS SCREEN//
        //Android HONEYCOMB (API 11-13) can get seriously fucked with below snippet.
        //Not that it matters since my minimum is API 19
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager
                .LayoutParams.FLAG_SECURE);
        //-----------------------------------//
        setContentView(R.layout.activity_payment_settings);

        //Hide soft input keyboard during onCreate run-time
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        metricsClassQuery.queryMetricsToUpdate("addCC");

        cCcAdded = findViewById(R.id.ccAdded);

        cCardHolderInput = findViewById(R.id.cardHolderInput);
        cPhoneNumberCCInput = findViewById(R.id.phoneNumberCCInput);
        cCCNumber = findViewById(R.id.ccNumber);
        cExpiryDateNumber = findViewById(R.id.expiryDateNumber);
        cCvvNumber = findViewById(R.id.cvvNumber);
        cSaveCardNumber = findViewById(R.id.saveCardNumber);
        cIdNumber = findViewById(R.id.idNumberInput);

        cCardHolderInput.requestFocus();

        //THIS MUST REMAIN TRUE ALWAYS!//
        cSaveCardNumber.setChecked(true);
        Log.i("savedCCstate", String.valueOf(cSaveCardNumber.isChecked()));
        //-----------------------------//

        if(!writeReadDataInFile.readFromFile("cleanStringNameCC").equals("")) {
            loadCCDataIfSaved();
        }else{
            loadNothingDataNotSaved();
        }

        if (ParseUser.getCurrentUser() != null){
            userEmailHolder = ParseUser.getCurrentUser().getEmail();//<<<<<<<<<<<<<FINAL
            Log.i("emailNow", ParseUser.getCurrentUser().getEmail());
        }else{
            toastMessages.productionMessage(getApplicationContext(),getResources()
                    .getString(R.string.paymentSettings_act_java_thereWasAProblem),1);
        }

        cCCNumber.addTextChangedListener(new TextWatcher() {
            int prevL = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = cCCNumber.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if ((prevL < length) && (length == 4 || length == 9 || length == 14)) {
                    //s.append('-');
                    String data = cCCNumber.getText().toString();
                    String finalForm = data + "-";
                    cCCNumber.setText(finalForm);
                    cCCNumber.setSelection(length + 1);
                }
            }
        });

        cExpiryDateNumber.addTextChangedListener(new TextWatcher() {
            int prevL = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = cExpiryDateNumber.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if ((prevL < length) && (length == 2)) {
                    //s.append('/');
                    String data = cExpiryDateNumber.getText().toString();
                    String finalForm = data + "/";
                    cExpiryDateNumber.setText(finalForm);
                    cExpiryDateNumber.setSelection(length + 1);
                }
            }
        });

        cCcAdded.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Start adding Payme SDK code
                //finish();
                if((!cCardHolderInput.getText().toString().isEmpty()) && (!cPhoneNumberCCInput
                        .getText().toString().isEmpty())
                    && (!cCCNumber.getText().toString().isEmpty()) && (!cExpiryDateNumber
                        .getText().toString().isEmpty())
                    && (!cCvvNumber.getText().toString().isEmpty())){
                    cleanStringNameCC = cCardHolderInput.getText().toString();//<<<<<<<<<<<<<<<<<<<<<<FINAL
                    cleanStringPhoneN = cPhoneNumberCCInput.getText().toString();//<<<<<<<<<<<<<<<<<<<FINAL
                    cleanStringCCNumber = cCCNumber.getText().toString()
                            .replace("-","");//<<<<FINAL
                    cleanStringExpiryDate = cExpiryDateNumber.getText().toString()
                            .replace("/","");//<<<<FINAL
                    cleanStringCVV = cCvvNumber.getText().toString();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<FINAL
                    cleanIdNumber = cIdNumber.getText().toString();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<FINAL
                    //userEmailHolder//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<FINAL

                    //-----------------------------------------------------------//
                    Log.i("CCcleanStringNameCC",cleanStringNameCC);
                    Log.i("CCcleanStringPhoneN",cleanStringPhoneN);
                    Log.i("CCcleanStringCCNumber", cleanStringCCNumber);
                    Log.i("CCcleanStringExpiryDate", cleanStringExpiryDate);
                    Log.i("CCcleanStringCVV",cleanStringCVV);
                    Log.i("CCcleanStringIdNumber", cleanIdNumber);
                    Log.i("CCuserEmailHolder",userEmailHolder);
                    //-----------------------------------------------------------//
                    Log.i("CC------", "------------------------");
                    Log.i("CCNumber", cCCNumber.getText().toString());
                    Log.i("CCExpDate", cExpiryDateNumber.getText().toString());

                    //TODO:Payme code 1: Register Car Owner purchase details to Capture_buyer API
                    CaptureBuyerRequest request = new CaptureBuyerRequest();
                    SplashCaptureBuyer splashCaptureBuyer = new SplashCaptureBuyer
                            (request, PaymentSettingsActivity.this,
                                    PaymentSettingsActivity.this, cleanStringNameCC,
                                    cleanStringPhoneN, cleanStringCCNumber, cleanStringExpiryDate,
                                    cleanStringCVV, cleanIdNumber, userEmailHolder, cCardHolderInput,
                                    cPhoneNumberCCInput, cCCNumber, cExpiryDateNumber, cCvvNumber,
                                    cIdNumber, cSaveCardNumber, cCcAdded);
                    //REGISTER DATA TO CAPTURE_BUYER//
                    splashCaptureBuyer.captureBuyerData();
                    if (!isValid(request)) {
                        return;
                    }
                    boxedLoadingDialog.showLoadingDialog();
                    //RUN THE CAPTURE_BUYER API//
                    splashCaptureBuyer.runCaptureBuyer(boxedLoadingDialog, cSaveCardNumber);
                    ///////////////TEST////////////////
                }else{
                    String missingFieldsTitle = getResources().getString(R.string
                            .paymentSettings_act_java_missingFields);
                    String missingFieldsMessage = getResources().getString(R.string
                            .paymentSettings_act_java_pleaseFillAll);
                    warningDialog(missingFieldsTitle, missingFieldsMessage);
                }
            }
        });
        spannableText();
    }

    public void spannableText(){
        SpannableString ss = new SpannableString(getResources().getString
                (R.string.paymentSettings_act_java_byClickingDone));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent paymeTouIntent = new Intent(PaymentSettingsActivity.this
                        ,WebActivity.class);
                paymeTouIntent.putExtra("webKey","paymeTermsOfUse");
                startActivity(paymeTouIntent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        if (Locale.getDefault().getDisplayLanguage().equals("English")) {
            ss.setSpan(clickableSpan, 116, 128, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if (Locale.getDefault().getDisplayLanguage().equals("עברית")){
            ss.setSpan(clickableSpan, 64, 74, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        TextView mPaymentTOUAgreement = findViewById(R.id.paymentTOUAgreement);
        mPaymentTOUAgreement.setText(ss);
        mPaymentTOUAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        mPaymentTOUAgreement.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(ccMaskEmpty){
                MoveDataOnFinish("Payment Method");
            }else {
                finish();
            }
        }else if(item.getItemId() == R.id.action_delete){
            if(!writeReadDataInFile.readFromFile("cleanStringNameCC").equals("")) {
                deleteCCDetails();
            }else{
                toastMessages.productionMessage(getApplicationContext(),getResources()
                        .getString(R.string.payment_menu_youHaveNotSaved),1);
            }
        }else if(item.getItemId() == R.id.action_edit){
            if(!writeReadDataInFile.readFromFile("cleanStringNameCC").equals("")) {
                editCCDetails();
            }else{
                toastMessages.productionMessage(getApplicationContext(),getResources()
                        .getString(R.string.payment_menu_youHaveNotSaved),1);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(ccMaskEmpty){
                MoveDataOnFinish("Payment Method");
            }else {
                finish();
            }
        }

        return false;
    }

    public void warningDialog(String title, String message){
        android.support.v7.app.AlertDialog.Builder missingStuff2Dialog = new android
                .support.v7.app.AlertDialog.Builder(PaymentSettingsActivity.this);
        missingStuff2Dialog.setTitle(title);
        missingStuff2Dialog.setIcon(android.R.drawable.ic_dialog_alert);
        missingStuff2Dialog.setMessage(message);
        missingStuff2Dialog.setPositiveButton(getResources().getString(R.string.washMyCar_act_java_ok), null);
        missingStuff2Dialog.setCancelable(false);
        missingStuff2Dialog.show();
    }

    //TODO:Payme code 2: Email validator for Car Owner Capture_buyer API
    public boolean isValid(CaptureBuyerRequest request) {

        if (!PayMe.Validator.isEmail(request.getBuyerEmail())) {
            warningDialog(getResources().getString(R.string.paymentSettings_act_java_wrongEmail),
                    getResources().getString(R.string.paymentSettings_act_java_emailIsIncorrect));
            return false;
        }

        return true;
    }

    public void loadCCDataIfSaved(){
        String savedNameCC = writeReadDataInFile.readFromFile("cleanStringNameCC");
        String savedPhoneCC = writeReadDataInFile.readFromFile("cleanStringPhoneN");
        String savedMaskCC = writeReadDataInFile.readFromFile("cleanStringCCMask");
        String savedExpiryCC = writeReadDataInFile.readFromFile("cleanStringCCExpiry");
        String savedCVVCC = writeReadDataInFile.readFromFile("cleanStringCCCvv");
        String savedIdNumberCC = writeReadDataInFile.readFromFile("cleanStringCCIdNumber");
        cSaveCardNumber.setChecked(true);
        toastMessages.debugMesssage(getApplicationContext(),"Something",1);
        toastMessages.debugMesssage(getApplicationContext(),writeReadDataInFile
                .readFromFile("buyer_key_permanent"),1);

        cCardHolderInput.setText(savedNameCC);
        cPhoneNumberCCInput.setText(savedPhoneCC);
        cCCNumber.setText(savedMaskCC);
        cExpiryDateNumber.setText(savedExpiryCC);
        cCvvNumber.setText(savedCVVCC);
        cIdNumber.setText(savedIdNumberCC);

        inputFieldsState(false);
    }

    public void loadNothingDataNotSaved(){
        cSaveCardNumber.setChecked(true);
        toastMessages.debugMesssage(getApplicationContext(),"Nothing",1);
    }

    public void deleteCCDetails(){
        android.support.v7.app.AlertDialog.Builder missingStuff2Dialog = new android.support.v7.app.AlertDialog.Builder(PaymentSettingsActivity.this);
        missingStuff2Dialog.setTitle(getResources().getString(R.string.payment_menu_trash));
        missingStuff2Dialog.setIcon(android.R.drawable.ic_dialog_alert);
        missingStuff2Dialog.setMessage(getResources().getString(R.string.payment_menu_deleteCCDetails));
        missingStuff2Dialog.setNegativeButton(getResources().getString(R.string.washMyCar_act_java_cancel), null);
        missingStuff2Dialog.setPositiveButton(getResources().getString(R.string.washMyCar_act_java_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeReadDataInFile.writeToFile("", "cleanStringNameCC");
                        writeReadDataInFile.writeToFile("", "cleanStringPhoneN");
                        writeReadDataInFile.writeToFile("", "cleanStringCCMask");
                        writeReadDataInFile.writeToFile("", "cleanStringCCExpiry");
                        writeReadDataInFile.writeToFile("", "cleanStringCCCvv");
                        writeReadDataInFile.writeToFile("", "cleanStringCCIdNumber");
                        writeReadDataInFile.writeToFile("", "buyer_key_permanent");
                        toastMessages.debugMesssage(getApplicationContext(), "permanent" +
                                " destroyed, and by logic, temporal also destroyed", 1);
                        cSaveCardNumber.setChecked(true);
                        cCardHolderInput.setText("");
                        cPhoneNumberCCInput.setText("");
                        cCCNumber.setText("");
                        cExpiryDateNumber.setText("");
                        cCvvNumber.setText("");
                        cIdNumber.setText("");
                        inputFieldsState(true);
                        ccMaskEmpty = true;
                    }
                });
        missingStuff2Dialog.setCancelable(false);
        missingStuff2Dialog.show();
    }

    public void editCCDetails(){
        if (!fieldsOnEdit) {
            android.support.v7.app.AlertDialog.Builder editStuff2Dialog = new android.support.v7.app.AlertDialog.Builder(PaymentSettingsActivity.this);
            editStuff2Dialog.setTitle(getResources().getString(R.string.payment_menu_editCreditCard));
            editStuff2Dialog.setIcon(android.R.drawable.ic_dialog_alert);
            editStuff2Dialog.setMessage(getResources().getString(R.string.payment_menu_editCreditCardDets));
            editStuff2Dialog.setNegativeButton(getResources().getString(R.string.washMyCar_act_java_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fieldsOnEdit = false;
                    }
                });
            editStuff2Dialog.setPositiveButton(getResources().getString(R.string.washMyCar_act_java_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputFieldsState(true);
                        cCCNumber.setText("");
                        cExpiryDateNumber.setText("");
                        cCvvNumber.setText("");
                        fieldsOnEdit = true;
                    }
                });
            editStuff2Dialog.setCancelable(false);
            editStuff2Dialog.show();
        }
    }

    public void inputFieldsState(Boolean state){
        cCardHolderInput.setEnabled(state);
        cCardHolderInput.setClickable(state);
        cPhoneNumberCCInput.setEnabled(state);
        cPhoneNumberCCInput.setClickable(state);
        cCCNumber.setEnabled(state);
        cCCNumber.setEnabled(state);
        cExpiryDateNumber.setEnabled(state);
        cExpiryDateNumber.setClickable(state);
        cCvvNumber.setClickable(state);
        cCvvNumber.setEnabled(state);
        cIdNumber.setClickable(state);
        cIdNumber.setEnabled(state);
        cSaveCardNumber.setClickable(state);
        cSaveCardNumber.setEnabled(state);
        cCcAdded.setEnabled(state);
        cCcAdded.setClickable(state);
    }

    public void MoveDataOnFinish(String ccMask){
        Intent previousScreen = new Intent(PaymentSettingsActivity.this
                , WashReqParamsActivity.class);
        previousScreen.putExtra("paymeCCMask", ccMask);
        setResult(Activity.RESULT_OK, previousScreen);
        finish();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
            if (event.getKeyCode() == KeyEvent.KEYCODE_POWER){
                screenShotNotPermitedMsg();
            }
        }else if(event.getKeyCode() == KeyEvent.KEYCODE_POWER){
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
                screenShotNotPermitedMsg();
            }
        }
        return false;
    }
    public void screenShotNotPermitedMsg(){
        toastMessages.productionMessage(getApplicationContext(),getResources()
                .getString(R.string.paymentSettings_act_java_cantTakeScreen),1);
    }
}
