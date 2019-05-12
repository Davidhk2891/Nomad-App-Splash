package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.server.parseserver.HistoryClassInterface;
import com.nomadapp.splash.model.server.parseserver.RequestClassInterface;
import com.nomadapp.splash.model.server.parseserver.send.HistoryClassSend;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by David on 7/21/2018 for Splash.
 */
public class RequestClassQuery {

    private Context context;

    private ToastMessages toastMessages = new ToastMessages();

    public RequestClassQuery(Context ctx){

        this.context = ctx;

    }

    public void fetchedParseFile(String requestSplasherName, final String parseFileName,
                                 final RequestClassInterface.splasherBeforeOrAfterPics
                                         splasherBeforeOrAfterPics){
        final String[] fileURL = new String[1];
        fileURL[0] = "empty";
        ParseQuery<ParseObject> fileQuery = new ParseQuery<>("Request");
        fileQuery.whereEqualTo("splasherUsername", requestSplasherName);
        fileQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for(ParseObject fileUrlObj : objects){
                            ParseFile firstBeforePic = fileUrlObj.getParseFile(parseFileName);
                            if (firstBeforePic != null) {
                                fileURL[0] = firstBeforePic.getUrl();
                                Log.i("purpleOrange1", "ran: " + fileURL[0]);
                                //INSTEAD OF RETURNING, RUN AN INTERFACE THROUGH HERE
                                //THIS WAY YOU GUARANTEE THAT YOU RUN THROUGH HERE
                                splasherBeforeOrAfterPics.afterPics();
                            }else{
                                Log.i("purpleOrange2", "ran");
                                splasherBeforeOrAfterPics.beforePics();
                            }
                        }
                    }
                }
            }
        });
    }

    public void fetchCurrentRequestOne(final RequestClassInterface requestClassInterface){
        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                //if there are no errors
                if (e == null) {
                    //there should be 1 and only 1 object in the arrayList.
                    // the Sole request of the current user
                    if (objects.size() > 0) {
                        for (ParseObject reqObj : objects) {
                            requestClassInterface.requestClassMethod(reqObj);
                            Log.i("interfaceSuccess", "ran and all good");
                        }
                    } else {
                        requestClassInterface.setCarWasherFinderToFalse();
                        Log.i("interfaceFailure", "just ran which is ok");
                    }
                }
            }
        });
    }

    public void fetchCurrentTakenRequest(final RequestClassInterface.TakenRequest takenRequest){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereExists("splasherUsername");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject object : objects){
                            takenRequest.fetchThisTakenRequest(object);
                        }
                    }
                }
            }
        });
    }

    //For ongoing request that was already taken and started
    public void fetchCurrRequestForSplasher(String parseUsername,
                                      final RequestClassInterface.TakenRequest takenRequest){
        final ParseQuery<ParseObject> forwardSplasher = ParseQuery.getQuery("Request");
        forwardSplasher.whereEqualTo("splasherUsername", parseUsername);
        forwardSplasher.whereEqualTo("taken", "yes");
        forwardSplasher.whereEqualTo("washFinished", "no");
        forwardSplasher.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject reqObj : objects){
                            takenRequest.fetchThisTakenRequest(reqObj);
                        }
                    }
                }
            }
        });
    }

    //Opportunity request for every splasher in the SplasherWanted Array
    public void fetchCurrCloseRequestsForSplasher(String username, LatLng splasherLocation
                        ,final RequestClassInterface.clearData clearData
                        ,final RequestClassInterface.TakenRequest takenRequest){
        String[] splashStatus = {"clear", "canceled"};
        final ParseQuery<ParseObject> dotQuery = ParseQuery.getQuery("Request");
        final ParseGeoPoint geoPointSelfLocation = new ParseGeoPoint
                (splasherLocation.latitude, splasherLocation.longitude);
        dotQuery.whereNear("carCoordinates", geoPointSelfLocation);
        dotQuery.whereContainedIn("splasherUsername", Arrays.asList(splashStatus));
        dotQuery.whereContains("splashersWanted", username);//test
        dotQuery.whereEqualTo("taken", "no");
        dotQuery.setLimit(100);
        dotQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    clearData.clearAllDataOnly();
                    int listSize = objects.size();
                    Log.i("Car Owner list size", String.valueOf(listSize));
                    if (objects.size() > 0 ){
                        for (ParseObject reqObj : objects){
                            takenRequest.fetchThisTakenRequest(reqObj);
                            takenRequest.afterUpdates();
                        }
                    }
                }
            }
        });
    }

    public void cancelAndDeleteRequest(String username
            , final RequestClassInterface.requestDeletion requestDeletion){
        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject object : objects){

                            makeRecordAndDelete(object, new RequestClassInterface
                                    .recCreatedBeforeDel() {
                                @Override
                                public void onRecCreatedBeforeDel() {
                                    requestDeletion.deleteRequest();
                                }

                                @Override
                                public void recNotCreatedErrMsg() {
                                    toastMessages.productionMessage(context
                                            .getApplicationContext(),context
                                            .getResources().getString(R.string
                                                    .carOwner_act_java_requestNotDeletedTry),1);
                                }
                            });
                        }
                        requestDeletion.handleRest();
                    }
                }else{
                    toastMessages.productionMessage(context.getApplicationContext()
                            ,context.getResources().getString
                                    (R.string
                                            .carOwner_act_java_requestNotDeletedTry)
                            ,1);
                }
            }
        });
    }

    private void makeRecordAndDelete(final ParseObject object, final RequestClassInterface
            .recCreatedBeforeDel recCreatedBeforeDel){
        String splasherName = object.getString("splasherUsername");
        String COUsername = object.getString("username");
        String locationOfPay = object.getString("carAddress");
        String locationDets = object.getString("carAddressDesc");
        String serviceGiven = "Expected: " + object.getString("serviceType");

        double originalPrice = 0.0;

        double tip = 0.0;

        String splasherRating = "0";
        String rawResponseFromPayme = "payment process not executed";
        String reqStatus = "canceled";
        String buyerKey = object.getString("buyerKey");
        String untilTime = object.getString("untilTime");

        String color = object.getString("carColor");
        String brand = object.getString("carBrand");
        String model = object.getString("carModel");
        String plate = object.getString("carplateNumber");
        String car = color + " " + brand + " " + model + " " + plate;

        String reqType = "none";

        HistoryClassSend historyClassSend = new HistoryClassSend(context);
        historyClassSend.createRequestRecord(splasherName, COUsername, locationOfPay, locationDets
                , serviceGiven, originalPrice, tip, splasherRating, rawResponseFromPayme
                , reqStatus, buyerKey, untilTime, car, reqType
                , new HistoryClassInterface.onCreateReqRecord() {
                    @Override
                    public void onRecordCreated() {
                        object.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    recCreatedBeforeDel.onRecCreatedBeforeDel();
                                }else{
                                    recCreatedBeforeDel.recNotCreatedErrMsg();
                                }
                            }
                        });
                    }

                    @Override
                    public void recordFailedToCreate() {

                    }
                });
    }
}
