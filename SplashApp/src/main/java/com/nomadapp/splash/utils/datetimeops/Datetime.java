package com.nomadapp.splash.utils.datetimeops;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by David on 9/1/2018 for Splash.
 */
public class Datetime {

    public Context context;
    private int localHour,localMinute,date,month,year;

    public Datetime(Context ctx){
        this.context = ctx;
        Calendar calendarObject = Calendar.getInstance();
        localHour = calendarObject.get(Calendar.HOUR_OF_DAY);
        localMinute = calendarObject.get(Calendar.MINUTE);
        date = calendarObject.get(Calendar.DATE);
        month = calendarObject.get(Calendar.MONTH);
        year = calendarObject.get(Calendar.YEAR);
    }

    @SuppressLint("DefaultLocale")
    public String rawCurrentTime(){
        return String.format("%02d:%02d", localHour, localMinute).toUpperCase(Locale.getDefault());
    }

    public String rawCurrentDate(){
        return String.valueOf(date) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
    }

    public String rawCurrentTimeDate(){

        //Phone's Actual time and date--------------------------------//
        String newFullDateSS;
        newFullDateSS = String.valueOf(date) + "-" + String.valueOf(month) + "-" +
                String.valueOf(year);

        @SuppressLint("DefaultLocale")
        final String fullTotalDateSS
                = newFullDateSS + " " +
                String.format("%02d:%02d", localHour,
                        localMinute).toUpperCase(Locale
                        .getDefault());
        //------------------------------------------------------------//

        return fullTotalDateSS;
    }

}
