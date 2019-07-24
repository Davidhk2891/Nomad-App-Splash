package com.nomadapp.splash.model.constants.serverconstants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.nomadapp.splash.R;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.io.ByteArrayOutputStream;

/**
 * Created by David on 2/12/2019 for Splash.
 */
public class ProfileConstants {

//********************CONSTANT CLASS**************************//
    public static final String CLASS_PROFILE = "Profile";
//************************************************************//

//********************CONSTANT COLUMNS************************//
    public static final String CLASS_PROFILE_COL_USERNAME = "username";
    public static final String CLASS_PROFILE_COL_EMAIL = "email";
    public static final String CLASS_PROFILE_COL_CAR_OWNER_OR_SPLASHER = "CarOwnerOrSplasher";
    public static final String CLASS_PROFILE_COL_SPLASHER_TYPE = "splasherType";
    public static final String CLASS_PROFILE_COL_OLD_AVG_RATING = "oldAvgRating";
    public static final String CLASS_PROFILE_COL_WASHES = "washes";
    public static final String CLASS_PROFILE_COL_WASHES_CANCELED = "washesCanceled";
    public static final String CLASS_PROFILE_COL_NUMERICAL_BADGE = "numericalBadge";
    public static final String CLASS_PROFILE_COL_SET_RPICE = "setPrice";
    public static final String CLASS_PROFILE_COL_SET_PRICE_E_INT = "setPriceEInt";
    public static final String CLASS_PROFILE_COL_SET_PRICE_MOTO = "setPriceMoto";
    public static final String CLASS_PROFILE_COL_SERVICE_RANGE = "serviceRange";
    public static final String CLASS_PROFILE_COL_SERVICE_EC = "serviceEC";
    public static final String CLASS_PROFILE_COL_EC_COORDS = "ECCoords";
    public static final String CLASS_PROFILE_COL_STATUS = "status";
    public static final String CLASS_PROFILE_COL_VERIFIED = "accountverified";
    public static final String CLASS_PROFILE_SET_COL_SPLASHER_PROF_PIC = "splasherProfPic";
//************************************************************//

//********************CONSTANT VALUES************************//
    public static final String CLASS_PROFILE_SPLAHER = "splasher";
    public static final String CLASS_PROFILE_WASHES = "5";
    public static final String CLASS_PROFILE_OLD_AVG_RATING = "3";
    public static final String CLASS_PROFILE_WASHES_CANCELED = "0";
    public static final String CLASS_PROFILE_NUMERICAL_BADGE = "2";
    public static final String CLASS_PROFILE_SET_PRICE = "30.0";
    public static final String CLASS_PROFILE_SET_PRICE_E_INT = "70.0";
    public static final String CLASS_PROFILE_SET_PRICE_MOTO = "20.0";
    public static final String CLASS_PROFILE_SET_NOT_SET = "not set";
    public static final String CLASS_PROFILE_SET_TRUE = "true";
    public static final String CLASS_PROFILE_SET_FALSE = "false";
    public static final String CLASS_PROFILE_SET_ACTIVE = "active";
    public static final String CLASS_PRIFLE_SET_INACTIVE = "inactive";
    public static final String CLASS_PROFILE_SET_INDEPENDENT = "independent";
    public static final String CLASS_PROFILE_SET_EMPLOYEE= "employee";

    //Default values for Splasher creation//
    public static final String CLASS_PROFILE_SERVICE_EC = "King George 40-46, Tel Aviv-Yafo, Israel";
    public static final String CLASS_PROFILE_SERVICE_RANGE =  "2.0 Km";
    public static final ParseGeoPoint CLASS_PROFILE_ECCOORDS = new ParseGeoPoint
            (32.0736315, 34.7730953);
    public static ParseFile CLASS_PROFILE_SPLASHER_PROF_PIC_DEFAULT(Context context){
        byte[] bytesProfPic;
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        Bitmap defaultSplasherPic = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.theemptyface);
        defaultSplasherPic.compress(Bitmap.CompressFormat.JPEG, 20, stream1);
        bytesProfPic = stream1.toByteArray();
        return new ParseFile("defaultProfPic.png", bytesProfPic);
    }
    //need to implement all of this during splasher creation. these values are just DEFAULT.
    //the splasher will be forced to changed them. these will prevent crashing tho.
//***********************************************************//
}
