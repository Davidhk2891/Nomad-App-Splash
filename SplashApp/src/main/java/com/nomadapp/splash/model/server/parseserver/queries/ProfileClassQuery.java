package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.model.server.parseserver.ProfileClassInterface;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by David on 6/21/2018 for Splash.
 */
public class ProfileClassQuery {

    private Context context;
    private ToastMessages toastMessages = new ToastMessages();
    private GlideImagePlacement glideImagePlacement;
    private  String accountVerificationStatus = "";

    public ProfileClassQuery(Context ctx){
        this.context = ctx;
        glideImagePlacement = new GlideImagePlacement(context);
    }

    public int getMyRatingBadge(final ImageView iv){
        final int[] splasherNumericalBadge = {2};
        UserClassQuery userClassQuery = new UserClassQuery(context);
        final ParseQuery<ParseObject> queryGetMyRating = ParseQuery.getQuery("Profile");
        queryGetMyRating.whereEqualTo("email", userClassQuery.userName());
        queryGetMyRating.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject ratingObject : objects) {
                        String badgeDeterminator;
                        badgeDeterminator = ratingObject.getString("numericalBadge");
                        int numBadgeDeterminator = Integer.parseInt(badgeDeterminator);
                        if (numBadgeDeterminator == 4) {
                            toastMessages.debugMesssage(context.getApplicationContext(),
                                    "EXELLENT", 1);
                            showBadge(R.drawable.platbadge,iv);
                            splasherNumericalBadge[0] = 4; //EXCELLENT
                        } else if (numBadgeDeterminator == 3) {
                            toastMessages.debugMesssage(context.getApplicationContext(),
                                    "GOOD", 1);
                            showBadge(R.drawable.goldbadge,iv);
                            splasherNumericalBadge[0] = 3; //GOOD
                        } else if (numBadgeDeterminator == 2) {
                            toastMessages.debugMesssage(context.getApplicationContext(),
                                    "REGULAR", 1);
                            showBadge(R.drawable.silverbadge,iv);
                            splasherNumericalBadge[0] = 2; //REGULAR
                        } else if (numBadgeDeterminator == 1) {
                            toastMessages.debugMesssage(context.getApplicationContext(),
                                    "BAD", 1);
                            showBadge(R.drawable.bronzebadge,iv);
                            splasherNumericalBadge[0] = 1; //BAD
                        }
                    }
                } else {
                    toastMessages.productionMessage(context.getApplicationContext(),
                            e.getMessage(), 1);
                }
            }
        });
        return splasherNumericalBadge[0];
    }

    private void showBadge(int drawable, ImageView cRatingBadge){
        glideImagePlacement.roundImagePlacementFromDrawable(drawable, cRatingBadge);
    }

    public void getUserProfileToUpdate(final ProfileClassInterface profileClassInterface){
        UserClassQuery userClassQuery = new UserClassQuery(context);
        final ParseQuery<ParseObject> profUserQuery = ParseQuery.getQuery("Profile");
        profUserQuery.whereEqualTo("email", userClassQuery.userName());
        profUserQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                profileClassInterface.updateChanges(objects, e);
            }
        });
    }

    public void getSplashersList(final ProfileClassInterface profileClassInterface){
        profileClassInterface.beforeQueryFetched();
        final ParseQuery<ParseObject> splasherListQuery = ParseQuery.getQuery("Profile");
        splasherListQuery.whereEqualTo("CarOwnerOrSplasher", "splasher");
        splasherListQuery.whereEqualTo("accountverified", "true");
        splasherListQuery.whereEqualTo("status", "active");
        splasherListQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                profileClassInterface.updateChanges(objects, e);
            }
        });
    }

    public void splasherType(final ProfileClassInterface.walletStatus walletStatus){
        final UserClassQuery userClassQuery = new UserClassQuery(context);
        final String[] splasherType = {""};
        if (userClassQuery.userExists()){
            if (userClassQuery.userIsCarOwnerOrSplasher("splasher")){
                getUserProfileToUpdate(new ProfileClassInterface() {
                    @Override
                    public void beforeQueryFetched() {}
                    @Override
                    public void updateChanges(List<ParseObject> objects, ParseException e) {
                        if (e == null){
                            if (objects.size() > 0){
                                for (ParseObject object : objects){
                                    splasherType[0] = object.getString("splasherType");
                                    Log.i("splasherType", "is " + " "
                                            + userClassQuery.userName() + " "
                                            + splasherType[0]);
                                    walletStatus.onWalletStatusAccess(splasherType[0]);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public void splasherAccVerification(String splasherUsername, final ProfileClassInterface
            .verificationStatus verificationStatus){
        final ParseQuery<ParseObject> profUserQuery = ParseQuery.getQuery("Profile");
        profUserQuery.whereEqualTo("email", splasherUsername);
        profUserQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject splasherAcc : objects){
                           accountVerificationStatus =
                                   splasherAcc.getString("accountverified");
                           Log.i("purpleAccVerStatus", accountVerificationStatus);
                           verificationStatus.accVerifiedStatus(accountVerificationStatus);
                        }
                    }
                }else{
                    toastMessages.productionMessage(context.getApplicationContext(),
                            e.getMessage(), 1);
                }
            }
        });
    }

    public void fetchAllSplashersInfo(String splasherUsername, final ProfileClassInterface
            .allSplashersInfo allSplashersInfo){
        ParseQuery<ParseObject> allSplashersQuery = ParseQuery.getQuery("Profile");
        allSplashersQuery.whereNotEqualTo("email", splasherUsername);
        allSplashersQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for(ParseObject splasherObj : objects){
                            allSplashersInfo.getInfo(splasherObj);
                        }
                        allSplashersInfo.afterLoop(objects);
                    }
                }
            }
        });
    }
}
