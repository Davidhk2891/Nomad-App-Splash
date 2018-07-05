package com.nomadapp.splash.utils.sysmsgs.loadingdialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;

import com.nomadapp.splash.R;

/**
 * Created by David on 5/6/2018 for Splash.
 */
public class BoxedLoadingDialog {

    private ProgressDialog progressDialog;

    private Context context;

    public BoxedLoadingDialog(Context ctx){
        this.context = ctx;
    }

    public void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources()
                    .getString(R.string.boxedLoadingDialog_act_java_pleasewait));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            timerForError(context);
        }
    }

    public void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void timerForError(final Context context){
        new CountDownTimer(45000, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                if (progressDialog.isShowing()){
                    messageForError(context);
                }
            }

        }.start();
    }

    private void messageForError(Context context){
        AlertDialog.Builder timesUpDialog = new AlertDialog.Builder(context);
        timesUpDialog.setTitle(context.getResources().getString(R.string
                .boxedLoadingDialog_act_java_errTitle));
        timesUpDialog.setIcon(android.R.drawable.ic_dialog_alert);
        timesUpDialog.setMessage(context.getResources().getString(R.string
                .boxedLoadingDialog_act_java_errMessage));
        timesUpDialog.setPositiveButton(context.getResources().getString(R.string
                        .boxedLoadingDialog_act_java_errOk)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideLoadingDialog();
            }
        });
        timesUpDialog.setCancelable(false);
        timesUpDialog.show();
    }

}
