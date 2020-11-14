package com.nomadapp.splash.utils.sysmsgs;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by David on 5/25/2018 for Splash.
 */
public class ToastMessages {
    /**
     * ENABLE TOAST MSG IN debugMessage(...) for better debugging. disable when production
     * @param message is the actual message you want to type
     * @param ml is for long toast (int = 1) or short toast (int = 0)
     */
    public void debugMesssage(Context context, String message, int ml){

        /*
           Theoretically, if i comment out the below "Toast", it should disable all debug messages
           across the app.
         */
        //Toast.makeText(context.getApplicationContext(), message, ml).show();
        Log.i("Message",message);
    }
    public void productionMessage(Context context, String message, int ml){
        Toast.makeText(context.getApplicationContext(), message, ml).show();
    }
}
