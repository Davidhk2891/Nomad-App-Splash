package com.nomadapp.splash.model.payment.paymeapis.ssale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.constants.PaymeConstants;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nomadapp.splash.model.constants.PaymeConstants.creditCardFeeProcessor;
import static com.nomadapp.splash.model.constants.PaymeConstants.splashFeeProcessor;

/**
 * Created by David on 6/2/2018 for Splash.
 */
public class SplashGenerateSaleManual {

    private boolean paymentExecuted = false;
    private boolean secondTryDeleteReq = false;
    private boolean clearForPayment = false;
    private boolean recordCreated = false;

    private Context context;
    private Activity activity;

    private String splasherName;
    private String locationOfPay;
    private String locationDetsOfPay;
    private String serviceGiven;
    private String shekel = "₪";
    private String rawResponseFromPay;

    private double tip = 0.0;
    private double originalPrice;
    private double creditCardfeeTotal;

    //Payme generate_sale variables//
    private String seller_seller_id;
    private String productName = PaymeConstants.PRODUCT_NAME;//checked// //<<FINAL-->DATA TO PAY
    private int installments = PaymeConstants.INSTALLMENTS;//checked//<<<<<<<FINAL-->DATA TO PAY
    //String buyer_buyer_key
    private double splashFeeTotal;
    private double orgPricePlusFeePlusCCPlusTip;
    private double orgPricePlusFeePlusCCPlusTipInFull;
    //-----------------------------//

    private DecimalFormat df = new DecimalFormat("#.##");

    public SplashGenerateSaleManual(Context ctx, Activity act){
        this.context = ctx;
        this.activity = act;
    }

    //STAND ALONE METHOD; IT GETS CALLED AT THE END OF ONCREATE()
    public void getAllDataForManualPayment(final TextView mBillCarWashPrice
            , final TextView mBillTipPrice, final TextView mBillSplashFeePrice
            , final TextView mBillCCFeePrice, final TextView mBillTotalPrice){
        //Details
        final BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(context);
        boxedLoadingDialog.showLoadingDialog();
        ParseQuery<ParseObject> payData = new ParseQuery<>("Request");
        payData.whereEqualTo("username", ParseUser.getCurrentUser().getUsername()); //sole row that contains the carOwner using this phone
        payData.whereExists("splasherUsername");
        payData.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject payObject : objects){
                            boxedLoadingDialog.hideLoadingDialog();
                            String originalPriceString = payObject.getString("priceWanted");
                            String originalPriceStringFixed = originalPriceString.replace("₪", "");//
                            originalPrice = Double.parseDouble(originalPriceStringFixed);
                            splasherName = payObject.getString("splasherUsername");
                            locationOfPay = payObject.getString("carAddress");
                            locationDetsOfPay = payObject.getString("carAddressDesc");
                            serviceGiven = payObject.getString("serviceType");

                            //transactionSplashFee: 8.5% of originalPrice//
                            if (!payObject.getString("tip").equals("")) {
                                tip = Double.parseDouble(payObject.getString("tip"));
                            }
                            //
                            //originalPrice = originalPrice;//
                            //tip = tip;//
                            splashFeeTotal = splashFeeProcessor(originalPrice + tip);
                            creditCardfeeTotal = creditCardFeeProcessor(originalPrice + tip);
                            //
                            orgPricePlusFeePlusCCPlusTip = originalPrice + tip + splashFeeTotal +
                                    creditCardfeeTotal;//<<<FINAL PRICE
                            orgPricePlusFeePlusCCPlusTipInFull = orgPricePlusFeePlusCCPlusTip * 100;//<<<FINAL PRICE TO PAYME

                            Log.i("finalPricePaymeFormat", String
                                    .valueOf(orgPricePlusFeePlusCCPlusTipInFull));

                            String washPrice = shekel + String.valueOf(df.format(originalPrice));
                            mBillCarWashPrice.setText(washPrice);

                            String splasherTip = shekel + String.valueOf(tip);
                            mBillTipPrice.setText(splasherTip);

                            //df.format rules: deposited in a string that needs to format a double
                            double splashFeeTwoDp = splashFeeProcessor(originalPrice + tip);//TEST//
                            String splashFeeTwoDpYes = String.valueOf(df.format(splashFeeTwoDp));
                            Log.i("logDouble", String.valueOf(splashFeeTwoDp));
                            Log.i("logDouble", String.valueOf(splashFeeTwoDpYes));
                            String splashTransFee = String.valueOf(splashFeeTwoDpYes);
                            String splashCCFee = String.valueOf(df.format(creditCardFeeProcessor(originalPrice + tip)));
                            String splashTransFeeComplete = shekel + splashTransFee;
                            String splashCCFeeComplete = shekel + splashCCFee;
                            mBillSplashFeePrice.setText(splashTransFeeComplete);
                            mBillCCFeePrice.setText(splashCCFeeComplete);

                            double finalPriceTwoDP = orgPricePlusFeePlusCCPlusTip;
                            String stringedFinalPriceTwoDP = String.valueOf(df.format(finalPriceTwoDP));
                            String totalPrice = shekel + stringedFinalPriceTwoDP;
                            mBillTotalPrice.setText(totalPrice);

                            clearForPayment = true;
                        }
                    }
                }
            }
        });
    }

    //SEQUENTIAL SCRIPT FOR MANUAL PAYMENT//
    public void checkout(final String splasherRating, final String buyer_buyer_key){
        final BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(context);
        if (secondTryDeleteReq) {
            boxedLoadingDialog.showLoadingDialog();
            deleteRequest(boxedLoadingDialog);
        }else if(clearForPayment) {
            boxedLoadingDialog.showLoadingDialog();
            ParseQuery<ParseObject> splasherData = new ParseQuery<>("Documents");
            splasherData.whereEqualTo("username", splasherName);
            splasherData.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                        if(objects.size() > 0){
                            for(ParseObject splasherObject : objects){
                                seller_seller_id = splasherObject.getString("sellerId");//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<FINAL-->DATA TO PAY
                                Log.i("data1", seller_seller_id);
                                Log.i("data2", String.valueOf(orgPricePlusFeePlusCCPlusTipInFull));
                                Log.i("data3", productName);
                                Log.i("data4", String.valueOf(installments));
                                sendDataAndPay(seller_seller_id, orgPricePlusFeePlusCCPlusTipInFull
                                        ,productName,installments,buyer_buyer_key,boxedLoadingDialog
                                        ,splasherRating);
                            }
                        }
                    }else{
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                        activity.finish();
                    }
                }
            });
        }
    }

    //string double string int string
    private void sendDataAndPay(String sellerId, double orgPricePlusFeePlusTipInFull
    , String productName, int installments, String buyerBuyerKey
    , final BoxedLoadingDialog boxedLoadingDialog, final String splasherRating){
        if (!paymentExecuted) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map<String, Object> params = new HashMap<>();
            params.put("seller_payme_id", sellerId);
            params.put("sale_price", orgPricePlusFeePlusTipInFull); //MAKE SURE ITS A WHOLE NUMBER (EX: 50.75 -> 5075)
            params.put("product_name", productName);
            params.put("installments", installments);
            params.put("buyer_key", buyerBuyerKey);
            JSONObject parameter = new JSONObject(params);
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, parameter.toString());
            Request request = new Request.Builder()
                    .url(PaymeConstants.GENERATE_SALE_URL)
                    .post(body)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    paymentExecuted = false;
                    Toast.makeText(context.getApplicationContext(), "Paymennt error. Transaction" +
                                    " did not go through. Please try again"
                            , Toast.LENGTH_LONG).show();
                    boxedLoadingDialog.hideLoadingDialog();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("SALE good response", "SALE good response");
                    paymentExecuted = true;
                    rawResponseFromPay = response.body().string();
                    createSaleRecord(boxedLoadingDialog, splasherRating);
                }
            });
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void createSaleRecord(final BoxedLoadingDialog boxedLoadingDialog, String splasherRating){
        if (!recordCreated) {
            String manual = "manual";
            String dateTimeOfPay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Log.i("dateTimeOfPay", dateTimeOfPay);
            ParseObject historyReq = new ParseObject("History");
            historyReq.put("carOwnerUsername", ParseUser.getCurrentUser().getUsername());
            historyReq.put("splasherUsername", splasherName);
            historyReq.put("dateTimeTransaction", dateTimeOfPay);
            historyReq.put("location", locationOfPay);
            historyReq.put("locationDesc", locationDetsOfPay);
            historyReq.put("serviceGiven", serviceGiven);
            historyReq.put("price", originalPrice);
            historyReq.put("tip", tip);
            historyReq.put("type", manual);
            historyReq.put("priceWithTip", originalPrice + tip);
            historyReq.put("splasherRating", splasherRating);
            historyReq.put("rawResponsePayme", rawResponseFromPay);
            historyReq.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        deleteRequest(boxedLoadingDialog);
                        recordCreated = true;
                    } else {
                        boxedLoadingDialog.hideLoadingDialog();
                    }
                }
            });
        }
    }

    private void deleteRequest(final BoxedLoadingDialog boxedLoadingDialog){
        ParseQuery<ParseObject> delRequest = new ParseQuery<>("Request");
        delRequest.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        delRequest.whereExists("splasherUsername");
        delRequest.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e ==null){
                    if(objects.size() > 0){
                        for(ParseObject deleteObj : objects){
                            deleteObj.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        Toast.makeText(context.getApplicationContext(),
                                                context.getResources().getString
                                                        (R.string.payment_bill_thankYouForChoosing)
                                                , Toast.LENGTH_LONG).show();
                                        activity.startActivity(new Intent(context, HomeActivity.class));
                                    }else{
                                        slowConnectionMessage();
                                        boxedLoadingDialog.hideLoadingDialog();
                                        secondTryDeleteReq = true;
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void slowConnectionMessage(){
        Toast.makeText(context.getApplicationContext(),
                context.getResources().getString(R.string
                        .carBeingWashed_act_java_slowConnectionPlease)
                , Toast.LENGTH_LONG).show();
    }

}
