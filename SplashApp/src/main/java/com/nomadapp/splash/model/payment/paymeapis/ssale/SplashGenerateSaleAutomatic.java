package com.nomadapp.splash.model.payment.paymeapis.ssale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.nomadapp.splash.model.constants.PaymeConstants;
import com.nomadapp.splash.model.server.parseserver.HistoryClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.HistoryClassSend;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

public class SplashGenerateSaleAutomatic {

    private Context context;

    //AUTOMATIC PAYMENT REQUIRED STRING VARIABLES//
    private String rawSellerId12;
    private String rawCOUsername12;
    private String rawUntilTime12;
    private String rawPrice12;
    private String rawLocation12;
    private String rawLocationDets12;
    private String rawServiceGiven12;
    private String rawBuyerKey12;
    private String rawResponseFromPay12;
    private String rawCar, color ,brand, model, plate;
    //AUTOMATIC PAYMENT REQUIRED DOUBLE VARIABLES//
    private double definitivePriceRegular;
    private double doubleProcFetchedPrice12HFull;
    //AUTOMATIC PAYMENT REQUIRED BOOLEAN VARIABLES//
    private boolean executeOncePerLife = false;
    private boolean executeOncePerLifeBackup = false;

    private ToastMessages toastMessages = new ToastMessages();

    public SplashGenerateSaleAutomatic(Context ctx){
        this.context = ctx;
    }

    public void automaticPaymentForUnpayedRequests(){
        //i actually need to check for every unpayed request to the splasher's name. not just one
        //so i need to fetch all the data from every request that still exist(if it was deleted the
        // splasher would have gotten payed by it any way)
        //I need to get: <priceWanted>, <untilTime>, <buyerKey>
        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().getUsername() != null) {
                ParseQuery<ParseObject> sellerKeyQuery = ParseQuery.getQuery("Documents");
                sellerKeyQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                sellerKeyQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                for (ParseObject sellerKeyObj : objects) {
                                    rawSellerId12 = sellerKeyObj.getString("sellerId");//READY//
                                    final ParseQuery<ParseObject> keyQuery = ParseQuery
                                            .getQuery("Request");
                                    try {
                                        keyQuery.whereEqualTo("splasherUsername",
                                                ParseUser.getCurrentUser().getUsername());
                                        keyQuery.whereEqualTo("washFinished", "yes");
                                        keyQuery.whereEqualTo("paid", "no");
                                        keyQuery.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects
                                                    , ParseException e) {
                                                if (e == null) {
                                                    if (objects.size() > 0) {
                                                        for (ParseObject unPayedObj : objects) {
                                                            rawCOUsername12 = unPayedObj.getString("username");
                                                            rawUntilTime12 = unPayedObj.getString("untilTime");
                                                            rawPrice12 = unPayedObj.getString("priceOriginal");
                                                            rawLocation12 = unPayedObj.getString("carAddress");
                                                            rawLocationDets12 = unPayedObj.getString("carAddressDesc");
                                                            rawServiceGiven12 = unPayedObj.getString("serviceType");
                                                            rawBuyerKey12 = unPayedObj.getString("buyerKey");

                                                            color = unPayedObj.getString("carColor");
                                                            brand = unPayedObj.getString("carBrand");
                                                            model = unPayedObj.getString("carModel");
                                                            plate = unPayedObj.getString("carplateNumber");

                                                            rawCar = color + " " + brand + " " + model
                                                                    + " " + plate;

                                                            //Phone's Actual time and date------------------------------------//
                                                            int hourSplasherSide = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                                            int minuteSplasherSide = Calendar.getInstance().get(Calendar.MINUTE);
                                                            int daySplasherSide = Calendar.getInstance().get(Calendar.DATE);
                                                            int monthSplasherSide = Calendar.getInstance().get(Calendar.MONTH);
                                                            if (monthSplasherSide == 12)
                                                                monthSplasherSide = 1;
                                                            else
                                                                monthSplasherSide = Calendar.getInstance().get(Calendar.MONTH) + 1;

                                                            int yearSplasherSide = Calendar.getInstance().get(Calendar.YEAR);
                                                            String newFullDateSS;
                                                            newFullDateSS = String.valueOf(daySplasherSide) + "-" + String.valueOf(monthSplasherSide)
                                                                    + "-" + String.valueOf(yearSplasherSide);

                                                            @SuppressLint("DefaultLocale")
                                                            final String fullTotalCurrentDate = newFullDateSS + " " +
                                                                    String.format("%02d:%02d", hourSplasherSide,
                                                                            minuteSplasherSide).toUpperCase(Locale.getDefault());
                                                            //----------------------------------------------------------------//
                                                            //Selected Until Time Request-------------------------------------//
                                                            String untilTimeFor12Hours = rawUntilTime12;
                                                            String untilTimeFor12HoursFixed;
                                                            if (untilTimeFor12Hours.contains(" AM")) {
                                                                untilTimeFor12HoursFixed = untilTimeFor12Hours.replace(" AM", "");
                                                            } else {
                                                                untilTimeFor12HoursFixed = untilTimeFor12Hours.replace(" PM", "");
                                                            }
                                                            //----------------------------------------------------------------//
                                                            //---------Current request "Until Time" and "Phone" Date----------//
                                                            @SuppressLint("SimpleDateFormat")
                                                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                                            Date currentTimeDateFormat12 = null;
                                                            Date untilTimeFinal12;
                                                            Date finalFullSavedTimeDateFormatPlus12 = null;
                                                            try {
                                                                //FINAL CURRENT TIME FOR 12 HOUR AFTER AUTOPAYMENT//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                                                currentTimeDateFormat12 = sdf.parse(fullTotalCurrentDate);

                                                                untilTimeFinal12 = sdf.parse(untilTimeFor12HoursFixed);

                                                                Calendar calendar12 = Calendar.getInstance();
                                                                calendar12.setTime(untilTimeFinal12);

                                                                int hourPlus12Before = calendar12.get(Calendar.HOUR_OF_DAY);
                                                                Log.i("hourPlus12Before", String.valueOf(hourPlus12Before));//check
                                                                calendar12.add(Calendar.HOUR_OF_DAY, 12);
                                                                int hourPlus12After = calendar12.get(Calendar.HOUR_OF_DAY);
                                                                Log.i("hourPlus12After", String.valueOf(hourPlus12After));

                                                                int minutePlus12After = calendar12.get(Calendar.MINUTE);

                                                                int dayPlus12After = calendar12.get(Calendar.DATE);
                                                                //if((hourPlus12Before >= 12 && hourPlus12Before < 24)) {
                                                                //If this is true, when added 12 to
                                                                // 'hourPlus12Before' it'll end up in > 24
                                                                //a.k.a the next day. so +1 must be added to DATE
                                                                //calendar12.add(Calendar.DATE, 1);
                                                                //dayPlus12After = calendar12.get(Calendar.DATE);
                                                                //}else{
                                                                //dayPlus12After = calendar12.get(Calendar.DATE);
                                                                //}

                                                                int monthPlus12After = calendar12.get(Calendar.MONTH);

                                                                int yearPlus12After = calendar12.get(Calendar.YEAR);
                                                                String newFullDatePlus12After = String.valueOf(dayPlus12After) + "-" + String.valueOf(monthPlus12After + 1)
                                                                        + "-" + String.valueOf(yearPlus12After);

                                                                @SuppressLint("DefaultLocale")
                                                                String finalFullSavedTimePlus12 = newFullDatePlus12After + " " +
                                                                        String.format("%02d:%02d", hourPlus12After,
                                                                                minutePlus12After).toUpperCase(Locale.getDefault());
                                                                //FINAL SAVED TIME FOR 12 HOUR AFTER AUTOPAYMENT//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                                                finalFullSavedTimeDateFormatPlus12 = sdf.parse(finalFullSavedTimePlus12);

                                                            } catch (java.text.ParseException eDate) {
                                                                eDate.printStackTrace();
                                                            }
                                                            Log.i("Checking Until time12", "is: " + untilTimeFor12HoursFixed);
                                                            Log.i("Checking Phone's time12", "is: " + fullTotalCurrentDate);
                                                            Log.i("Check fCurrentDate12", "is: " + currentTimeDateFormat12);
                                                            Log.i("Check fSavedDate12", "is: " + finalFullSavedTimeDateFormatPlus12);
                                                            //--------------------------------------------------------------//
                                                            //----execution of comparison of the 2 dates----//
                                                            //currentSSDate = current date
                                                            if (currentTimeDateFormat12 != null &&
                                                                    (currentTimeDateFormat12.compareTo(finalFullSavedTimeDateFormatPlus12) > 0)) {
                                                                //-----------GET PRICE RIGHT-----------//
                                                                //22.2 ₪ is the way you get it raw//
                                                                String fetchedPrice12H = rawPrice12;
                                                                String fetchedPrice12HNoSign = "";
                                                                if (fetchedPrice12H.contains("₪")) {
                                                                    fetchedPrice12HNoSign = fetchedPrice12H.replace("₪", "");
                                                                } else if (fetchedPrice12H.contains("$")) {
                                                                    fetchedPrice12HNoSign = fetchedPrice12H.replace("$", "");
                                                                }
                                                                definitivePriceRegular = Double.parseDouble(fetchedPrice12HNoSign);
                                                                //double splashFee = splashFeeProcessor(doubleFetchedPrice12H);
                                                                //double ccFee = creditCardFeeProcessor(doubleFetchedPrice12H);
                                                                double definitivePriceToPayme = PaymeConstants.priceShownToCarOwner(definitivePriceRegular);
                                                                doubleProcFetchedPrice12HFull = definitivePriceToPayme * 100;//READY//
                                                                Log.i("doublePrice", String.valueOf(definitivePriceToPayme));
                                                                Log.i("doublePricePayme", String.valueOf(doubleProcFetchedPrice12HFull));
                                                                //-------------------------------------//
                                                                if (!executeOncePerLifeBackup) {
                                                                    if (!executeOncePerLife) {
                                                                        //------>//EXTREMELY IMPORTANT THAT THIS RUNS ONLY >>>>O.N.C.E<<<<!!!!!!!!!!!!//<------//
                                                                        unPayedObj.put("paid", "yes");
                                                                        unPayedObj.saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null){
                                                                                    autoPaymentAPICall(rawBuyerKey12, rawCOUsername12, doubleProcFetchedPrice12HFull);
                                                                                }
                                                                            }
                                                                        });
                                                                        //-------------------------------------------------------------------------------------//
                                                                        Log.i("executeONCE", String.valueOf(executeOncePerLife));
                                                                        executeOncePerLife = true;
                                                                    }
                                                                    executeOncePerLifeBackup = true;
                                                                }
                                                            }
                                                            //---------------------------------------------//
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    } catch (NullPointerException np5) {
                                        np5.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private void autoPaymentAPICall(String buyerKey, final String coUsername, double finalPrice){
        String productName = PaymeConstants.PRODUCT_NAME;
        int installments = PaymeConstants.INSTALLMENTS;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("seller_payme_id", rawSellerId12);
        payParams.put("sale_price", finalPrice);//WHOLE NUMBER(EX: 50.75 -> 5075)
        payParams.put("currency", PaymeConstants.CURRENCY);
        payParams.put("product_name", productName);
        payParams.put("installments", installments);
        payParams.put("buyer_key", buyerKey);
        JSONObject parameter = new JSONObject(payParams);
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
                Log.e("SALE error response", call.request().body().toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("SALE success", "SALE success");
                rawResponseFromPay12 = response.body().string();
                createSaleRecord(coUsername);
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void createSaleRecord(final String coUsername){
        final HistoryClassSend historyClassSend = new HistoryClassSend(context);
        final UserClassQuery userClassQuery = new UserClassQuery(context);
        String reqType = "automatic";
        String successfulReqStatus = "successful";
        final double defaultTip = 0.0;
        final String defaultRating = "3";
        historyClassSend.createRequestRecord(userClassQuery.userName(), rawCOUsername12
                ,rawLocation12,rawLocationDets12,rawServiceGiven12,definitivePriceRegular
                ,defaultTip,defaultRating,rawResponseFromPay12,successfulReqStatus,rawBuyerKey12
                ,rawUntilTime12,rawCar,reqType,new HistoryClassInterface
                        .onCreateReqRecord(){
                @Override
                public void onRecordCreated() {
                    toastMessages.debugMesssage(context.getApplicationContext(),
                            "record created",1);
                    Log.i("record", "CREATED");
                    deleteRequest(coUsername);
                }
                @Override
                public void recordFailedToCreate() {
                    toastMessages.productionMessage(context.getApplicationContext()
                            , "record" + " failed to create",1);
                    Log.i("record", "FAILED");
                }
             });
        //FINISHED CLASSIFYING SYSTEM. TEST BOTH MANUAL AND AUTOMATIC PAYMENT!!!!
        //IMPLEMENT RECORD CREATED FOR WHEN REQUEST IS CANCELED FOR WHATEVER REASON!!!!
    }

    private void deleteRequest(String coUsername){
        ParseQuery<ParseObject> delRequest = new ParseQuery<>("Request");
        delRequest.whereEqualTo("splasherUsername", ParseUser.getCurrentUser().getUsername());
        delRequest.whereEqualTo("username", coUsername);
        delRequest.whereEqualTo("washFinished", "yes");
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
                                        toastMessages.productionMessage(context
                                                        .getApplicationContext(),
                                                "new income added :)",1);
                                    }else{
                                        toastMessages.productionMessage(context
                                                        .getApplicationContext()
                                                ,e.getMessage(),1);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

}
