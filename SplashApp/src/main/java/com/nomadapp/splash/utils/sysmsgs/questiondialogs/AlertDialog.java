package com.nomadapp.splash.utils.sysmsgs.questiondialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.activity.splasherside.WashRequestsActivity;

/**
 * Created by David on 7/12/2018 for Splash.
 */
public class AlertDialog {

    private Context context;

    public AlertDialog(Context ctx){
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

}
