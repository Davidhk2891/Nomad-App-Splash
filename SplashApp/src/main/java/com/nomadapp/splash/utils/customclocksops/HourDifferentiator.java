package com.nomadapp.splash.utils.customclocksops;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

import java.util.concurrent.TimeUnit;

/**
 * Created by David on 11/2/2018 for Splash.
 */
public class HourDifferentiator {

    private Context context;
    private WriteReadDataInFile writeReadDataInFile;
    private String todayOrTomorrow;

    public HourDifferentiator(Context ctx){
        this.context = ctx;
        writeReadDataInFile = new WriteReadDataInFile(context);
    }

    public void hourDiffCalculator(String localTimeDate, String untilTimeDate
        , TextView untilTimeStamp){
        //format time://
        //String timeString1="12:34"; <--LOCAL TIME
        //String timeString2="06:31"; <--UNTIL TIME
        String localTime = dateToTimeConverter(localTimeDate);
        Log.i("timeFetched45","is: " + localTime + " phone time");
        String untilTime = dateToTimeConverter(untilTimeDate);
        Log.i("timeFetched46","is: " + untilTime + " until time");
        String[] fractions1=localTime.split(":");
        String[] fractions2=untilTime.split(":");
        Integer hours1=Integer.parseInt(fractions1[0]);
        Integer hours2=Integer.parseInt(fractions2[0]);
        Integer minutes1=Integer.parseInt(fractions1[1]);
        Integer minutes2=Integer.parseInt(fractions2[1]);
        int hourDiff=hours1-hours2;
        int minutesDiff=minutes1-minutes2;

        Log.i("timeFetchedTodTom",writeReadDataInFile.readFromFile("todayTomorrow"));
        todayOrTomorrow = writeReadDataInFile.readFromFile("todayTomorrow");

        if (minutesDiff < 0) {
            //minutesDiff = 60 + minutesDiff;
            //hourDiff--;
            minutesDiff = minutesDiff * (-1);
        }
        if (hourDiff < 0) {
            //hourDiff = 24 + hourDiff;
            hourDiff = hourDiff * (-1);
        }
        if (todayOrTomorrow.equals("true")){
            hourDiff = 24 - hourDiff;//LEFT HERE. BIG BUG, ASKED ISAAC FOR HALP
        }
        //huge problem with (tomorrow). cdc still thinks is request for today.
        //possible solution: have 'hourDiff' be 24h - actual 'hourDiff'
        Log.i("timeFetchedAre: ","There are " + hourDiff + " hours and " + minutesDiff +
                " minutes of difference");

        //timeConverterForCdc(hourDiff, minutesDiff, cdcText);//If enabled, set cdcText as parameter
        untilTimeStamp.setText(dateToTimeConverter(untilTimeDate));
    }

    private String dateToTimeConverter(String dateTime){

        String firstCut = new StringBuilder(dateTime).insert(dateTime
                .length()-5, "s").toString();
        String secondCut = firstCut.substring(firstCut.indexOf("s")+1);
        String time = secondCut.trim();
        return time;
    }

    private void timeConverterForCdc(int hourDiff, int minuteDiff, final TextView cdcText){
        //1 hour is 3,600,000 milliseconds
        //formula: x * 3,600,000

        //1 minute is 60000 milliseconds
        //formula: x * 60000

        int hourDiffInMillis = hourDiff * 3600000;

        int minuteDiffInMillis = minuteDiff * 60000;

        int totalTimeDiffInMillis = hourDiffInMillis + minuteDiffInMillis;

        int interval = 1200;

        new CountDownTimer(totalTimeDiffInMillis,interval){
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                //review egg timer see how I did it back then
                cdcText.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS
                                .toHours(millisUntilFinished)
                        , TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(millisUntilFinished))
                        , TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                cdcText.setText(context.getResources().getString(R.string.act_car_owner_cdcTime));
                //test
            }
        }.start();
    }

}
