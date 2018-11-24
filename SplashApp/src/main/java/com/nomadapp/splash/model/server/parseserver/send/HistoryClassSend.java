package com.nomadapp.splash.model.server.parseserver.send;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.nomadapp.splash.model.server.parseserver.HistoryClassInterface;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David on 10/12/2018 for Splash.
 */
public class HistoryClassSend {

    private Context context;

    public HistoryClassSend(Context ctx){

        this.context = ctx;

    }

    @SuppressLint("SimpleDateFormat")
    public void createRequestRecord(String splasherName, String coun, String locationOfPay
            , String locationDetsOfPay, String serviceGiven, double originalPrice, double tip
            , String splasherRating, String rawResponseFromPay, String reqStatus, String buyerKey
            , String untilTime, String car, String reqType
            , final HistoryClassInterface.onCreateReqRecord onCreateReqRecord){
        String dateTimeOfPay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Log.i("dateTimeOfPay", dateTimeOfPay);
        ParseObject historyReq = new ParseObject("History");
        historyReq.put("carOwnerUsername", coun);
        historyReq.put("splasherUsername", splasherName);
        historyReq.put("dateTimeTransaction", dateTimeOfPay);
        historyReq.put("reqStatus", reqStatus);
        historyReq.put("buyerKey", buyerKey);
        historyReq.put("car", car);
        historyReq.put("untilTime", untilTime);
        historyReq.put("location", locationOfPay);
        historyReq.put("locationDesc", locationDetsOfPay);
        historyReq.put("serviceGiven", serviceGiven);
        historyReq.put("price", originalPrice);
        historyReq.put("tip", tip);
        historyReq.put("type", reqType);
        historyReq.put("priceWithTip", originalPrice + tip);
        historyReq.put("splasherRating", splasherRating);
        historyReq.put("rawResponsePayme", rawResponseFromPay);
        historyReq.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    onCreateReqRecord.onRecordCreated();
                } else {
                    onCreateReqRecord.recordFailedToCreate();
                }
            }
        });
    }

}
