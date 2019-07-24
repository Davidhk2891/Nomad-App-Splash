package com.nomadapp.splash.utils.customclocksops;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nomadapp.splash.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by David on 7/2/2019 for Splash.
 */
public class TimePick {
    private Context context;
    private String selectedTime;
    private String selectedDate;
    private boolean untilTimeWithinRules = false;
    private boolean timePickerMoved = false;
    private DateTime dateTime;

    private static final int TIME_PICKER_INTERVAL = 10;

    public TimePick(Context ctx){
        this.context = ctx;
        dateTime = new DateTime(context);
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public boolean isUntilTimeWithinRules() {
        return untilTimeWithinRules;
    }

    public boolean isTimePickerMoved() {
        return timePickerMoved;
    }

    public void setTimePickerMoved(boolean timePickerMoved) {
        this.timePickerMoved = timePickerMoved;
    }

    public void pickTime(final TimePicker timePicker, final TextView timeShow
            , final AlertDialog timePickerAlertDialog){
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                /*
                 If hourOfDay is 12 or higher, the AM/PM indicator will be PM. Of course, this has
                 nothing to do with Android, and everything to do with knowing hour
                 12- vs. 24-hour time conventions work

                 if hour of Day is 12 or higher AND less than 24

                 int currentSoleHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                 int currentSoleMinute = Calendar.getInstance().get(Calendar.MINUTE);
                 */

                timePickerMoved = true;
                Log.i("oldMinute", String.valueOf(minute));

                Calendar timeChanged = Calendar.getInstance();
                int currentSoleHourToSet = timeChanged.get(Calendar.HOUR_OF_DAY);

                boolean hourNotOK = hourOfDay == currentSoleHourToSet
                        || hourOfDay == currentSoleHourToSet + 1
                        || hourOfDay == currentSoleHourToSet + 2
                        || hourOfDay == currentSoleHourToSet + 3
                        || hourOfDay == currentSoleHourToSet + 4
                        || hourOfDay == currentSoleHourToSet + 5;

                if(hourOfDay >= 12 && hourOfDay < 24){

                    //We are in PM
                    //SET UP A SYSTEM TO TELL THE USER HE HAS TO SET UP A TIME AT
                    //LEAST 2 HOURS AHEAD OF THE ACTUAL CURRENT TIME.

                    if(hourNotOK){
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText = String.format("%02d:%02d", hourOfDay
                                , displayedMinuteFormatted(minute))
                                .toUpperCase(Locale.getDefault()) + " PM";
                        timePickerAlertDialog.setCancelable(false);
                        timeShow.setText(deadLinePrefix(cCrUntilTimeText));
                        untilTimeWithinRules = false;
                        Log.i("blue1", "ran");
                    }else {
                        //compare also for automatic set time of current time that
                        //happens as soon as timepicker opens
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText2 = String.format("%02d:%02d", hourOfDay
                                , displayedMinuteFormatted(minute))
                                .toUpperCase(Locale.getDefault()) + " PM";
                        timePickerAlertDialog.setCancelable(true);
                        timeShow.setText(deadLinePrefix(cCrUntilTimeText2));
                        untilTimeWithinRules = true;
                        Log.i("blue2", "ran");
                    }

                } else {

                    //We are in AM
                    //SET UP A SYSTEM TO TELL THE USER HE HAS TO SET UP AT
                    //TIME AT LEAST 2 HOURS AHEAD OF THE ACTUAL CURRENT TIME.

                    if(hourNotOK){
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText = String.format("%02d:%02d", hourOfDay
                                , displayedMinuteFormatted(minute))
                                .toUpperCase(Locale.getDefault()) + " AM";
                        timePickerAlertDialog.setCancelable(false);
                        timeShow.setText(deadLinePrefix(cCrUntilTimeText));
                        untilTimeWithinRules = false;
                        Log.i("blue3", "ran");
                    }else {
                        @SuppressLint("DefaultLocale")
                        String cCrUntilTimeText2 = String.format("%02d:%02d", hourOfDay
                                , displayedMinuteFormatted(minute))
                                .toUpperCase(Locale.getDefault()) + " AM";
                        timePickerAlertDialog.setCancelable(true);
                        timeShow.setText(deadLinePrefix(cCrUntilTimeText2));
                        Log.i("blue4", "ran");
                        Log.i("redmug0", "CURRENT HOUR IS " + dateTime.getLocalHour()
                                + " AND SELECTED HOUR IS " + hourOfDay);
                        if (dateTime.getLocalHour() >= 19) {
                            //if cancelled, give ability to reset ti same hour after opening if picker wasn't touch(set on currently displayed hour)//test whole hour system tomorrow
                            Log.i("redmug0.5", String.valueOf(dateTime.getLocalHour()));
                            Log.i("redmug0.6", String.valueOf(hourOfDay));
                            Log.i("redmug0.7", String.valueOf(hoursDifferenceChecker(hourOfDay)));
                            Log.i("redmug1", "ran");
                            if (hoursDifferenceChecker(hourOfDay) < 5) {
                                //The time difference is not enough
                                untilTimeWithinRules = false;
                                Log.i("redmug1.1", "ran");
                            } else {
                                untilTimeWithinRules = true;
                                Log.i("redmug1.2", "ran");
                            }
                            Log.i("blue2", "ran");
                        }else{
                            untilTimeWithinRules = true;
                        }
                    }
                }

                timePicker.setIs24HourView(false);

                if (timeShow.getText().toString().contains(context.getResources()
                        .getString(R.string.act_wash_my_car_until2))) {
                    String selectedTimePre = timeShow.getText().toString();
                    selectedTime = selectedTimePre.replace(context.getResources()
                            .getString(R.string.act_wash_my_car_until2), ""); //<--FINAL
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void timePickerAutoSet(TimePicker timePicker, TextView textView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String stringedTime;
            if (hour >= 12){
                //PM
                stringedTime = String.format("%02d:%02d", hour
                        , displayedMinuteFormatted(minute))
                        .toUpperCase(Locale.getDefault()) + " PM";
            }else{
                //AM
                stringedTime = String.format("%02d:%02d", hour
                        , displayedMinuteFormatted(minute))
                        .toUpperCase(Locale.getDefault()) + " AM";
            }
            textView.setText(deadLinePrefix(stringedTime));
        }
    }

    private int hoursDifferenceChecker(int selectedHour){
        int dtm = 24 - dateTime.getLocalHour();
        Log.i("redmugA", String.valueOf(dtm));
        return selectedHour + dtm;
    }

    public void setTimePickerInterval(TimePicker timePicker) {
        try {
            NumberPicker minutePicker = timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android"));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                //Could trigger a bug
                String formattedVals = String.format("%02d", i);
                displayedValues.add(formattedVals);
                Log.i("displayedTimeVals", formattedVals);
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            Log.e("Custom minute picker", "Exception: " + e);
        }
    }

    private int displayedMinuteFormatted(int minute){
        int formattedMin = 0;
        if (minute == 1){
            //minute: 10
            formattedMin = 10;
        }else if (minute == 2){
            //minute: 20
            formattedMin = 20;
        }else if (minute == 3){
            //minute: 30
            formattedMin = 30;
        }else if (minute == 4){
            //minute: 40
            formattedMin = 40;
        }else if (minute == 5){
            //minute: 50
            formattedMin = 50;
        }
        Log.i("new minute", String.valueOf(formattedMin));
        return formattedMin;
    }

    public void dayChecker(){
        String selectedDatetime = dateTime.rawCurrentDate() + " " +
                selectedTime;
        if (selectedDatetime.contains("AM"))
            selectedDatetime = selectedDatetime.replace(" AM", "");
        else if (selectedDatetime.contains("PM"))
            selectedDatetime = selectedDatetime.replace(" PM", "");

        String currentDateTime = dateTime.rawCurrentTimeDate();

        Log.i("bananaDateSel", selectedDatetime);
        Log.i("bananaDateCurr", currentDateTime);

        datesCompare(currentDateTime, selectedDatetime, new DateCheckerInterface() {
            @Override
            public void selectedDateisBefore() {
                selectedDate = dateTime.rawCurrentDatePlusOne();
            }

            @Override
            public void selectedDateIsAfter() {
                selectedDate = dateTime.rawCurrentDate();
            }
        });
    }

    private void datesCompare(String currentDateTime, String selectedDateTime
            , DateCheckerInterface dateCheckerInterface){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat
                ("dd-MM-yyyy HH:mm");
        Date currentSSDate = null;
        Date selectedSSDate = null;
        try {
            currentSSDate = sdf.parse(currentDateTime);
            selectedSSDate = sdf.parse(selectedDateTime);
            Log.i("bananaDateCurrFor", currentSSDate.toString());
            Log.i("bananaDateSelFor", selectedSSDate.toString());
        } catch (java.text.ParseException eDate) {
            eDate.printStackTrace();
        }
        if (currentSSDate != null) {
            if (!(currentSSDate.compareTo(selectedSSDate) > 0)) {
                Log.i("bananaResult", "current date is BEFORE selected date");
                dateCheckerInterface.selectedDateIsAfter();
            } else {
                Log.i("bananaResult", "current date is AFTER selected date");
                dateCheckerInterface.selectedDateisBefore();
            }
        }
    }

    private String deadLinePrefix(String string){
        return context.getResources().getString(R.string.act_wash_my_car_until2) + string;
    }
}
