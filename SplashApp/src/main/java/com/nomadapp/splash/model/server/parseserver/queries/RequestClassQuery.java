package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.server.parseserver.RequestClassInterface;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
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
                        requestClassInterface.requestClassMethod();
                        Log.i("interfaceSuccess", "ran and all good");
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

    public void fetchCurrCloseRequestsForSplasher(String username, LatLng splasherLocation
                        ,final RequestClassInterface.clearData clearData
                        ,final RequestClassInterface.TakenRequest takenRequest){
        String[] splashStatus = {"clear", "canceled", username};
        final ParseQuery<ParseObject> dotQuery = ParseQuery.getQuery("Request");
        final ParseGeoPoint geoPointSelfLocation = new ParseGeoPoint
                (splasherLocation.latitude, splasherLocation.longitude);
        dotQuery.whereNear("carCoordinates", geoPointSelfLocation);
        dotQuery.whereContainedIn("splasherUsername", Arrays.asList(splashStatus));
        dotQuery.whereEqualTo("taken", "no");
        dotQuery.setLimit(30);
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
                            object.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        requestDeletion.deleteRequest();
                                    }else{
                                        toastMessages.productionMessage(context
                                        .getApplicationContext(),context
                                                .getResources().getString(R.string
                                                .carOwner_act_java_requestNotDeletedTry),1);
                                    }
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

    public void fetchReqForNotiSplasher(final RequestClassInterface.TakenRequest takenRequest){
        UserClassQuery ucq = new UserClassQuery(context);
        String splasherUsername = ucq.userName();
        String[] splashStatus = {"clear", "canceled", splasherUsername};
        final ParseQuery<ParseObject> dotQuery = ParseQuery.getQuery("Request");
        dotQuery.whereContainedIn("splasherUsername", Arrays.asList(splashStatus));
        dotQuery.whereEqualTo("taken", "no");
        dotQuery.setLimit(30);
        dotQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        takenRequest.fetchThisTakenRequest(null);
                    }
                }
            }
        });
    }
}
