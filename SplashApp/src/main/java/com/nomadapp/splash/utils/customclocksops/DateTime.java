package com.nomadapp.splash.utils.customclocksops;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by David on 9/1/2018 for Splash.
 */
public class DateTime {

    public Context context;
    private int localHour,localMinute,date,month,year;
    private Calendar calendarObject;
    private boolean dateMovedForward = false;

    public DateTime(Context ctx){
        this.context = ctx;
        calendarObject = Calendar.getInstance();
        localHour = calendarObject.get(Calendar.HOUR_OF_DAY);
        localMinute = calendarObject.get(Calendar.MINUTE);
        date = calendarObject.get(Calendar.DATE);
        month = calendarObject.get(Calendar.MONTH);
        year = calendarObject.get(Calendar.YEAR);
        currentMonthHandler();
    }

    private void currentMonthHandler(){
        month = month + 1;
        if (month == 13){
            month = 1;
        }
        Log.i("month", "is " + month);
    }

    public int getLocalHour() {
        return localHour;
    }

    @SuppressLint("DefaultLocale")
    public String rawCurrentTime(){
        return String.format("%02d:%02d", localHour, localMinute).toUpperCase(Locale.getDefault());
    }

    public String rawCurrentDate(){
        String currentDate = date + "-" + month + "-" + year;
        Log.i("currentDate1", "is " + currentDate + " and month is " + month);
        return currentDate;
    }

    public String rawCurrentDatePlusOne(){
        dateMovedForwardChecker();
        String currentDate = calendarObject.get(Calendar.DATE) + "-" + month + "-" + year;
        Log.i("currentDate2", "is " + currentDate + " and month is " + month);
        return currentDate;
    }

    private void dateMovedForwardChecker(){
        if (!dateMovedForward){
            calendarObject.add(Calendar.DATE, 1);
            dateMovedForward = true;
        }else{
            calendarObject.add(Calendar.DATE, -1);
            calendarObject.add(Calendar.DATE, 1);
            dateMovedForward = true;
        }
    }

    public String rawCurrentTimeDate(){

        //Phone's Actual time and date--------------------------------//
        String newFullDateSS;
        newFullDateSS = date + "-" + month + "-" +
                year;

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
