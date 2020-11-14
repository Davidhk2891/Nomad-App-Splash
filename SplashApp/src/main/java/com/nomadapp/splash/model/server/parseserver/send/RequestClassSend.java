package com.nomadapp.splash.model.server.parseserver.send;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.model.server.CloudTriggers;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;

/**
 * Created by David on 7/10/2018 for Splash.
 */
public class RequestClassSend {

    private Context context;
    private ToastMessages toastMessages = new ToastMessages();
    private MetricsClassQuery metricsClassQuery;
    private boolean serverNotiToSplashersSent = false;

    public RequestClassSend(Context ctx){
        this.context = ctx;
        metricsClassQuery = new MetricsClassQuery(context);
    }

    //Load Request to Parse Server 2
    public void loadRequest(String address, final LatLng carCoor, String carAddressDescription
            ,final String selectedTime, final String getServiceType, String carBrandToUpload
            , String carModelToUpload, String carColorToUpload, String carPlateToUpload
            , String dollarSetPrice, boolean temporalKeyActive, String[] splashersWanted
            , String[] splasherPrices, String[] carOwnerPrices, String splasherUsername
            , String splasherShowingName, String requestType) {

        String profilePicToUpload;
        String profPicNoFbString = "none";
        if (!(ParseUser.getCurrentUser().getString("fbProfilePic") == null)) {
            profilePicToUpload = ParseUser.getCurrentUser().getString("fbProfilePic");
            if (!profilePicToUpload.contains("https")) {
                ParseFile profPicNoFbFile = ParseUser.getCurrentUser()
                        .getParseFile("localProfilePic");
                profPicNoFbString = profPicNoFbFile.getUrl();
            } else if (profilePicToUpload.contains("https")) {
                profPicNoFbString = "none";
            }
        } else {
            profilePicToUpload = "none";
            profPicNoFbString = "none";
        }

        final WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(context);
        ParseObject request = new ParseObject("Request");
        request.put("username", ParseUser.getCurrentUser().getUsername());
        request.put("carAddress", address); //1
        ParseGeoPoint carGeoPoint = new ParseGeoPoint(carCoor.latitude,carCoor.longitude);
        request.put("carCoordinates", carGeoPoint); //2
        request.put("carAddressDesc", carAddressDescription); //3
        request.put("untilTime", selectedTime); //4
        request.put("serviceType", getServiceType); //5
        request.put("carBrand", carBrandToUpload); //6
        request.put("carModel", carModelToUpload); //7
        request.put("carColor", carColorToUpload); //8
        request.put("carplateNumber", carPlateToUpload); //10
        request.put("priceWanted", dollarSetPrice);//11
        request.put("fbProfilePic", profilePicToUpload); //12
        if (!profilePicToUpload.contains("https")) {
            request.put("ProfPicNoFb", profPicNoFbString); //13
        }
        //Temporary until we implement back the badge system://
        int numericalBadge = 2;
        request.put("badgeWanted", String.valueOf(numericalBadge)); //14
        request.put("taken", "no"); //15
        request.put("splashersWanted", Arrays.asList(splashersWanted)); //16
        request.put("splasherPrices", Arrays.asList(splasherPrices));//17
        request.put("carOwnerPrices", Arrays.asList(carOwnerPrices));//18
        request.put("splasherUsername", splasherUsername);//19
        request.put("splasherShowingName", splasherShowingName);//20
        request.put("picturesInbound", "false"); //21
        request.put("washFinished", "no");//22
        request.put("paid", "no");//23
        request.put("requestType", requestType);//24
        //TODO: Payme Code: Sending Car owner's buyer_key to Request PUT
        //!writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")
        if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){
            //Temporal buyer_key
            request.put("buyerKey", writeReadDataInFile.readFromFile
                    ("buyer_key_temporal"));//25
            //Activate boolean value that marks it is a temporal buyer_key
            temporalKeyActive = true;
        }else if(writeReadDataInFile.readFromFile("buyer_key_temporal").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")){
            //Permanent buyer_key
            request.put("buyerKey", writeReadDataInFile.readFromFile
                    ("buyer_key_permanent"));//26
        }
        String carOwnerPhoneNum = writeReadDataInFile.readFromFile("cleanStringPhoneN");
        Log.i("carOwnerPhoneNum", carOwnerPhoneNum);
        request.put("carOwnerPhoneNum",carOwnerPhoneNum);//27
        //-----------------------------------------------
        final boolean finalTemporalKeyActive = temporalKeyActive;
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    metricsClassQuery.queryMetricsToUpdate("orderWash");
                    //Works. Now work on this boolean below: findCarWasherRequestActive
                    //findCarWasherRequestActive = true;
                    toastMessages.debugMesssage(context.getApplicationContext()
                            ,context.getResources().getString(R.string
                                    .washMyCar_act_java_washRequestSent),1);
                    Double carCoordinatesLatitude = carCoor.latitude;
                    Double carCoordinatesLongitude = carCoor.longitude;
                    Intent intent = new Intent(context,
                            HomeActivity.class);
                    intent.putExtra("requestActive", "active");
                    intent.putExtra("carLat", carCoordinatesLatitude);
                    intent.putExtra("carLon", carCoordinatesLongitude);
                    intent.putExtra("selectedTime", selectedTime);
                    if(getServiceType.equals(context.getResources().getString(R.string
                    .act_wash_my_car_motorcycle))){
                        writeReadDataInFile.writeToFile("bike", "bikeOrNot");
                    }else{
                        writeReadDataInFile.writeToFile("noBike", "bikeOrNot");
                    }
                    if (finalTemporalKeyActive){
                        writeReadDataInFile.writeToFile("",
                                "buyer_key_temporal");
                        toastMessages.debugMesssage(context.getApplicationContext()
                                ,"temporal key sent to request class on server and " +
                                        "destroyed from local .txt file right after",1);
                        toastMessages.productionMessage(context, context.getResources()
                                        .getString(R.string.act_wash_my_car_requestSent)
                                ,1);
                    }
                    if (!serverNotiToSplashersSent) {
                        CloudTriggers.triggerCloudFuncNoti();
                        Log.i("HowManyRuns", "run 1");
                        serverNotiToSplashersSent = true;
                    }
                    context.startActivity(intent);
                }
            }
        });
    }
}
