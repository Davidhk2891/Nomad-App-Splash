package com.nomadapp.splash.model.constants;

import android.content.Context;

/**
 * Created by David on 5/23/2018 for Splash.
 */

public class WebConstants {

    //App's package//
    public static String packageName(Context ctx){
        return ctx.getPackageName();
    }

    public static final String FEEDBACK_URL = "market://details?id=";
    public static final String FEEDBACK_URL_BACKUP = "https://play.google.com/store/apps/details?id=";
    public static final String PAYME_TERMS_OF_USE = "https://drive.google.com/file/d/0B2Ol6r5T5ofLRXY3eE8xN1JTUW12WUp6VlVNXzh2MmcxendZ/view?usp=drivesdk";
    public static final String PRIVACY_POLICY = "http://splashmycar.com/privacy-policy/";
    public static final String TERMS_OF_USE = "http://splashmycar.com/terms-of-use/";
    public static final String FAQ = "http://splashmycar.com/faq/";

}
