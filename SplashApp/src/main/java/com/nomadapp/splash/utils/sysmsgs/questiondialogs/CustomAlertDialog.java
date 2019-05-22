package com.nomadapp.splash.utils.sysmsgs.questiondialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.activity.splasherside.WashRequestsActivity;
import com.nomadapp.splash.utils.sysmsgs.DialogAcceptInterface;

/**
 * Created by David on 7/12/2018 for Splash.
 */
public class CustomAlertDialog {

    private Context context;

    public CustomAlertDialog(Context ctx){
        this.context = ctx;
    }

    public void takePrivateRequest(){
        android.app.AlertDialog.Builder privateReqDialog =
                new android.app.AlertDialog.Builder(context);
        privateReqDialog.setTitle(context.getResources().getString
                (R.string.act_car_owner_youHaveNewOrders));
        privateReqDialog.setIcon(android.R.drawable.ic_dialog_alert);
        privateReqDialog.setMessage(context.getResources()
                .getString(R.string.act_car_owner_isAsking));
        privateReqDialog.setPositiveButton(context.getResources()
                .getString(R.string.act_car_owner_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(context, WashRequestsActivity.class));
            }
        });
        privateReqDialog.setNegativeButton(context.getResources().getString(R.string
                .act_car_owner_decline),null);
        privateReqDialog.setCancelable(false);
        privateReqDialog.show();
    }

    public void editBankDetsDialog(final DialogAcceptInterface dialogAcceptInterface){
        android.app.AlertDialog.Builder privateReqDialog =
                new android.app.AlertDialog.Builder(context);
        privateReqDialog.setTitle(context.getResources().getString
                (R.string.splasher_wallet_java_dialogTitle));
        privateReqDialog.setIcon(android.R.drawable.ic_dialog_alert);
        privateReqDialog.setMessage(context.getResources()
                .getString(R.string.splasher_wallet_java_dialogMsg));
        privateReqDialog.setPositiveButton(context.getResources()
                .getString(R.string.splasher_wallet_java_dialogYes), new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogAcceptInterface.onAcceptOption();
            }
        });
        privateReqDialog.setNegativeButton(context.getResources().getString(R.string
                .splasher_wallet_java_dialogNo),null);
        privateReqDialog.setCancelable(false);
        privateReqDialog.show();
    }

    public void generalPurposeQuestionDialog(Context context,String title,String msg
            ,String accept,String reject
            ,final DialogAcceptInterface dialogAcceptInterface){
        android.app.AlertDialog.Builder privateReqDialog =
                new android.app.AlertDialog.Builder(context);
        privateReqDialog.setTitle(title);
        privateReqDialog.setIcon(android.R.drawable.ic_dialog_alert);
        privateReqDialog.setMessage(msg);
        privateReqDialog.setPositiveButton(accept, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogAcceptInterface.onAcceptOption();
            }
        });
        if (reject != null) {
            privateReqDialog.setNegativeButton(reject, null);
        }
        privateReqDialog.setCancelable(false);
        privateReqDialog.show();
    }

}
