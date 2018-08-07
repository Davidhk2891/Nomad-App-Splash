package com.nomadapp.splash.model.server.parseserver.send;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by David on 7/10/2018 for Splash.
 */
public class RequestClassSend {

    private Context context;
    private ToastMessages toastMessages = new ToastMessages();
    private MetricsClassQuery metricsClassQuery;

    public RequestClassSend(Context ctx){
        this.context = ctx;
        metricsClassQuery = new MetricsClassQuery(context);
    }

    //Load Request to Parse Server 2
    public void loadRequest(String address, final LatLng carCoor, String carAddressDescription
            , final String fullDate, final String selectedTime, final String getServiceType
            , String carBrandToUpload, String carModelToUpload, String carColorToUpload
            , String carPlateToUpload, String dollarSetPrice, int numericalBadge
            , boolean temporalKeyActive, String splasherUsername, String requestType) {

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
        request.put("untilTime", fullDate + " " + selectedTime); //4
        request.put("serviceType", getServiceType); //5
        request.put("carBrand", carBrandToUpload); //6
        request.put("carModel", carModelToUpload); //7
        request.put("carColor", carColorToUpload); //8
        request.put("carplateNumber", carPlateToUpload); //10
        String shekels = context.getResources().getString(R.string.shekel);
        String shekelsSetPriceFinal = dollarSetPrice + " " + shekels;
        request.put("priceWanted", shekelsSetPriceFinal);
        request.put("fbProfilePic", profilePicToUpload); //13
        if (!profilePicToUpload.contains("https")) {
            request.put("ProfPicNoFb", profPicNoFbString); //14
        }
        //Temporary until we implement back the badge system://
        numericalBadge = 2;
        request.put("badgeWanted", String.valueOf(numericalBadge)); //15
        request.put("taken", "no"); //16
        request.put("splasherUsername", splasherUsername); //17
        request.put("picturesInbound", "false"); //18
        request.put("washFinished", "no");//19
        request.put("paid", "no");//20
        request.put("requestType", requestType);//21
        //TODO: Payme Code: Sending Car owner's buyer_key to Request PUT
        //!writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")
        if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){
            //Temporal buyer_key
            request.put("buyerKey", writeReadDataInFile.readFromFile
                    ("buyer_key_temporal"));//22
            //Activate boolean value that marks it is a temporal buyer_key
            temporalKeyActive = true;
        }else if(writeReadDataInFile.readFromFile("buyer_key_temporal").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")){
            //Permanent buyer_key
            request.put("buyerKey", writeReadDataInFile.readFromFile
                    ("buyer_key_permanent"));//22
        }
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
                    intent.putExtra("selectedTime", fullDate + " " + selectedTime);
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
                    context.startActivity(intent);
                }
            }
        });
    }

}
