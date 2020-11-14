package com.nomadapp.splash.model.payment.paymeapis.buyer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.ui.activity.carownerside.WashReqParamsActivity;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.paymeservice.android.PayMe;
import com.paymeservice.android.error.PayMeError;
import com.paymeservice.android.model.CaptureBuyerRequest;
import com.paymeservice.android.model.CaptureBuyerResponse;

/**
 * Created by David on 5/6/2018 for Splash.
 */
public class SplashCaptureBuyer {

    private Context context;
    private Activity activity;
    private String cleanStringNameCC,cleanStringPhoneN,cleanStringCCNumber,cleanStringExpiryDate
    ,cleanStringCVV,cleanIdNumber,userEmailHolder;
    private CaptureBuyerRequest request;
    private EditText et1, et2, et3, et4, et5, et6;
    private CheckBox cb;
    private Button b;

    private ToastMessages toastMessages = new ToastMessages();
    private MetricsClassQuery metricsClassQuery;

    public SplashCaptureBuyer(CaptureBuyerRequest request, Context context, Activity activity,
                              String cleanStringNameCC, String cleanStringPhoneN,
                              String cleanStringCCNumber, String cleanStringExpiryDate,
                              String cleanStringCVV,String cleanIdNumber, String userEmailHolder,
                              EditText et1, EditText et2, EditText et3, EditText et4, EditText et5,
                              EditText et6, CheckBox cb, Button b) {

        this.activity = activity;
        this.et1 = et1;
        this.et2 = et2;
        this.et3 = et3;
        this.et4 = et4;
        this.et5 = et5;
        this.et6 = et6;
        this.cb = cb;
        this.b = b;

        this.request = request;
        this.context = context;
        this.cleanStringNameCC = cleanStringNameCC;
        this.cleanStringPhoneN = cleanStringPhoneN;
        this.cleanStringCCNumber = cleanStringCCNumber;
        this.cleanStringExpiryDate = cleanStringExpiryDate;
        this.cleanStringCVV = cleanStringCVV;
        this.userEmailHolder = userEmailHolder;
        this.cleanIdNumber = cleanIdNumber;

        metricsClassQuery = new MetricsClassQuery(context);
    }

    //TODO:Payme code 1: Register Car Owner purchase details to Capture_buyer API
    public void captureBuyerData() {
        request.setBuyerName(cleanStringNameCC);
        request.setBuyerPhone(cleanStringPhoneN);
        request.setCreditCardNumber(cleanStringCCNumber);
        request.setCreditCardExp(cleanStringExpiryDate);
        request.setCreditCardCvv(cleanStringCVV);
        request.setBuyerSocialId(cleanIdNumber);
        request.setBuyerEmail(userEmailHolder);
        //THIS MUST REMAIN TRUE ALWAYS!-//
        request.setBuyerIsPermanent(true);
        //------------------------------//
    }

    public void runCaptureBuyer(final BoxedLoadingDialog boxedLoadingDialog, final CheckBox checkBox){
        PayMe.captureBuyer(request, new PayMe.TransactionListener<CaptureBuyerResponse>() {
            @Override
            public void onSuccess(CaptureBuyerResponse captureBuyerResponse) {
                boxedLoadingDialog.hideLoadingDialog();
                //figure this out. do it old school and pass inputFieldState and MoveDataOnFinish
                //manually
                toastMessages.debugMesssage(context, "Success",1);
                if (checkBox.isChecked()){
                    WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(context);
                    writeReadDataInFile.writeToFile(captureBuyerResponse.getBuyerName(),
                            "cleanStringNameCC");
                    writeReadDataInFile.writeToFile(captureBuyerResponse.getBuyerPhone(),
                            "cleanStringPhoneN");
                    writeReadDataInFile.writeToFile(captureBuyerResponse.getBuyerCardMask(),
                            "cleanStringCCMask");
                    writeReadDataInFile.writeToFile(captureBuyerResponse.getBuyerCardExp(),
                            "cleanStringCCExpiry");
                    writeReadDataInFile.writeToFile(captureBuyerResponse.getBuyerSocialId(),
                            "cleanStringCCIdNumber");
                    writeReadDataInFile.writeToFile("***", "cleanStringCCCvv");
                    String buyer_key = captureBuyerResponse.getBuyerKey();
                    writeReadDataInFile.writeToFile(buyer_key, "buyer_key_permanent");
                    writeReadDataInFile.writeToFile("","buyer_key_temporal");
                    toastMessages.debugMesssage(context, "permanent kept," +
                            " temporal destroyed",1);
                    toastMessages.debugMesssage(context, buyer_key,1);
                    Log.i("buyer_key_perma", captureBuyerResponse.getBuyerKey());
                    inputFieldsState();
                    MoveDataOnFinish(captureBuyerResponse.getBuyerCardMask());
                }else{
                    WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(context);

                    writeReadDataInFile.writeToFile("", "cleanStringNameCC");
                    writeReadDataInFile.writeToFile("", "cleanStringPhoneN");
                    writeReadDataInFile.writeToFile("", "cleanStringCCMask");
                    writeReadDataInFile.writeToFile("", "cleanStringCCExpiry");
                    writeReadDataInFile.writeToFile("", "cleanStringCCCvv");
                    writeReadDataInFile.writeToFile("", "cleanStringCCIdNumber");

                    writeReadDataInFile.writeToFile(captureBuyerResponse.getBuyerKey(),
                            "buyer_key_temporal");
                    writeReadDataInFile.writeToFile("", "buyer_key_permanent");
                    toastMessages.debugMesssage(context, "Temp kept," +
                            " permament destroyed",1);

                    Log.i("buyer_key_temp", captureBuyerResponse.getBuyerKey());

                    MoveDataOnFinish(captureBuyerResponse.getBuyerCardMask());
                }
            }

            @Override
            public void onFailed(Exception e, PayMeError error) {
                boxedLoadingDialog.hideLoadingDialog();
                if (error != null) {
                    toastMessages.productionMessage(context, "onFailed1: " +
                            error.getStatusErrorDetails(),1);
                    Log.e("error is 1", " " + error.getStatusErrorDetails());
                    Log.e("error is 1.1", " " + error.getStatusAdditionalInfo());
                    if(e != null){
                        toastMessages.productionMessage(context, "onFailed1.1: "
                                + e.getMessage(),1);
                        Log.e("error is 1.2", " " + e.getMessage());
                    }
                } else if (e != null) {
                    toastMessages.productionMessage(context, "onFailed2: "
                            + e.getMessage(),1);
                    Log.e("error is 1.2", " " + e.getMessage());
                } else {
                    toastMessages.productionMessage(context, "Failed3",1);
                    Log.e("error is 3", "Failed");
                }
            }
        });
    }

    private void deleteTempBuyerKey(){
        WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(context);
        writeReadDataInFile.writeToFile("", "buyer_key_temporal");
    }

    private void inputFieldsState(){
        et1.setEnabled(false);
        et1.setClickable(false);
        et2.setEnabled(false);
        et2.setClickable(false);
        et3.setEnabled(false);
        et3.setEnabled(false);
        et4.setEnabled(false);
        et4.setClickable(false);
        et5.setClickable(false);
        et5.setEnabled(false);
        et6.setClickable(false);
        et6.setEnabled(false);
        cb.setClickable(false);
        cb.setEnabled(false);
        b.setEnabled(false);
        b.setClickable(false);
    }

    private void MoveDataOnFinish(String ccMask){
        metricsClassQuery.queryMetricsToUpdate("CCAdded");
        Intent previousScreen = new Intent(context, WashReqParamsActivity.class);
        previousScreen.putExtra("paymeCCMask", ccMask);
        activity.setResult(Activity.RESULT_OK, previousScreen);
        activity.finish();
    }

}
