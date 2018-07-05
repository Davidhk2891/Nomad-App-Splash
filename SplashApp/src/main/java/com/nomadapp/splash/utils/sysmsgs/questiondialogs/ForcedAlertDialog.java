package com.nomadapp.splash.utils.sysmsgs.questiondialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.activity.splasherside.SplasherCameraActivity;
import com.nomadapp.splash.ui.activity.splasherside.SplasherClientRouteActivity;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.ui.activity.standard.SignUpLogInActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by David on 5/19/2018 for Splash.
 */
public class ForcedAlertDialog {

    private Context context;

    public ForcedAlertDialog(Context ctx){
        this.context = ctx;
    }

    public void requestPendingForSplasherDialogCam(final ParseObject object){
        AlertDialog.Builder pendingPaymentDialog = new AlertDialog.Builder(context);
        pendingPaymentDialog.setTitle(context.getResources().getString
                (R.string.forced_alert_dialog_title));
        pendingPaymentDialog.setIcon(android.R.drawable.ic_dialog_alert);
        pendingPaymentDialog.setMessage(context.getResources()
                .getString(R.string.forced_alert_dialog_msg));
        pendingPaymentDialog.setPositiveButton(context.getResources()
                .getString(R.string.forced_alert_dialog_go), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goSplashCamFromScratch(object);
            }
        });
        pendingPaymentDialog.setCancelable(false);
        pendingPaymentDialog.show();
    }
    private void goSplashCamFromScratch(ParseObject object){
        Intent intent = new Intent(context, SplasherCameraActivity.class);
        intent.putExtra("fetchedUntilTime", object.getString("untilTime"));
        intent.putExtra("fetchedPrice", object.getString("priceWanted"));
        context.startActivity(intent);
    }

    public void requestPendingForSplasherDialogRoute(final ParseObject object, final LatLng latLng,
                                                     final Double d1, final Double d2){

        AlertDialog.Builder pendingPaymentDialog = new AlertDialog.Builder(context);
        pendingPaymentDialog.setTitle(context.getResources().getString
                (R.string.forced_alert_dialog_title));
        pendingPaymentDialog.setIcon(android.R.drawable.ic_dialog_alert);
        pendingPaymentDialog.setMessage(context.getResources()
                .getString(R.string.forced_alert_dialog_msg));
        pendingPaymentDialog.setPositiveButton(context.getResources()
                .getString(R.string.forced_alert_dialog_go), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goCODetsFromScratch(object, latLng, d1, d2);
            }
        });
        pendingPaymentDialog.setCancelable(false);
        pendingPaymentDialog.show();

    }
    private void goCODetsFromScratch(ParseObject object, LatLng latLng, Double d1, Double d2){
        Intent intent = new Intent(context,SplasherClientRouteActivity.class);

        ParseGeoPoint requestLocation = (ParseGeoPoint)//1
                object.get("carCoordinates");
        latLng = new LatLng(requestLocation.getLatitude(),
                requestLocation.getLongitude());
        d1 = latLng.longitude;
        d2 = latLng.latitude;

        intent.putExtra("requestLatitudes"
                , d2);
        intent.putExtra("requestLongitudes"
                , d1);
        intent.putExtra("carOwnerUsername", object
                .getString("username"));
        intent.putExtra("carOwnerCarAddress", object
                .getString("carAddress"));
        intent.putExtra("carOwnerCarAddressDesc", object
                .getString("carAddressDesc"));
        intent.putExtra("carOwnerCarUntilTime", object
                .getString("untilTime"));
        intent.putExtra("carOwnerCarServiceType", object
                .getString("serviceType"));
        intent.putExtra("setPrice", object
                .getString("priceWanted"));
        //----------Car dets-----------//
        intent.putExtra("carOwnerCarBrand", object
                .getString("carBrand"));
        intent.putExtra("carOwnerCarModel", object
                .getString("carModel"));
        intent.putExtra("carOwnerCarColor", object
                .getString("carColor"));
        intent.putExtra("carOwnerCarPlate", object
                .getString("carplateNumber"));
        intent.putExtra("specData", "COAKey");
        intent.putExtra("appWasClosed", "yes");
        //-----------------------------//
        context.startActivity(intent);
    }
    public void mustAcceptTOUFirst(){
        AlertDialog.Builder TOUDialog = new AlertDialog.Builder(context);
        TOUDialog.setTitle(context.getResources().getString
                (R.string.forced_alert_missing_tou_title));
        TOUDialog.setIcon(android.R.drawable.ic_dialog_alert);
        TOUDialog.setMessage(context.getResources()
                .getString(R.string.forced_alert_missing_tou_msg));
        TOUDialog.setPositiveButton(context.getResources()
                .getString(R.string.forced_Alert_missing_tou_ok), null);
        TOUDialog.setCancelable(false);
        TOUDialog.show();
    }
    public void somethingWentWrong(){
        AlertDialog.Builder TOUDialog = new AlertDialog.Builder(context);
        TOUDialog.setTitle(context.getResources().getString
                (R.string.forced_Alert_somethingWrong_title));
        TOUDialog.setIcon(android.R.drawable.ic_dialog_alert);
        TOUDialog.setMessage(context.getResources()
                .getString(R.string.forced_Alert_somethingWrong_msg));
        TOUDialog.setPositiveButton(context.getResources()
                .getString(R.string.forced_Alert_somethingWrong_ok), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        HomeActivity homeActivity = new HomeActivity();
                        homeActivity.finish();
                    }
                });
            }
        });
        TOUDialog.setCancelable(false);
        TOUDialog.show();
    }
    public void somethingWentWrongFacebook(){
        AlertDialog.Builder TOUDialog = new AlertDialog.Builder(context);
        TOUDialog.setTitle(context.getResources().getString
                (R.string.forced_Alert_somethingWrong_title));
        TOUDialog.setIcon(android.R.drawable.ic_dialog_alert);
        TOUDialog.setMessage(context.getResources()
                .getString(R.string.forced_Alert_somethingWrong_msg));
        TOUDialog.setPositiveButton(context.getResources()
                .getString(R.string.forced_Alert_somethingWrong_ok), null);
        TOUDialog.setCancelable(false);
        TOUDialog.show();
    }
}
