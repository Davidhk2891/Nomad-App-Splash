package com.nomadapp.splash.utils.phonedialer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by David on 8/14/2018 for Splash.
 */
public class DialCall {

    private Context context;

    public DialCall(Context ctx){
        this.context = ctx;
    }

    public void fetchPhoneNumToDial(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }
}
