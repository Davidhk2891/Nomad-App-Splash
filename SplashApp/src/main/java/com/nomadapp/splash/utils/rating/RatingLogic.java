package com.nomadapp.splash.utils.rating;

import android.content.Context;

import com.nomadapp.splash.utils.sysmsgs.ToastMessages;

/**
 * Created by David & Isaac on 1/14/2018 for Splash.
 */

public class RatingLogic {

    private ToastMessages toastMessages = new ToastMessages();

    //--------------------------RATING ALGORITHM--------------------------//
    //SPLASHER SIDE//
    /*
        ****************************************
        INITIAL VALUES: washes = 0(stored in server),
                        oldAvgRating = 0(stored in server),
                        washesCanceled = 0(stored in server),
                        stars = 0(deductible in function only),
                        newAvgRating = x(deductible in function only). <<<quality calculator
        if washes = 0 then REGULAR BADGE
        else if washes =/ 0 then USE EQUATIONS
        ****************************************
        ****************************************
        EQUATIONS THAT DETERMINE BADGE (ALTOGETHER)
        *experience = washes
        *quality = newAvgRating = (oldAvgRating * washes + stars) / washes
        *responsability = [1 - (washesCanceled/washes + washesCanceled)] * 100
        ****************************************
     */
    Context context;
    //*****************

    public RatingLogic(Context ctx){
        this.context = ctx;
    }

    public int splasherBadgeCalculator(int totalWashes, int canceledWashes, int oldAvgRating, int currentNumStars){

        int numericalBadgeMatch;

        int experience = totalWashes;

        //quality = newAvgRating

        int quality = (oldAvgRating * totalWashes + currentNumStars) / totalWashes;

        int responsability = (1 - (canceledWashes/totalWashes + canceledWashes)) * 100; //<<< Gives % value

        if(experience >= 100 && quality >= 4 && responsability >= 70){

            toastMessages.debugMesssage(context, "EXCELLENT_BADGE", 1);
            numericalBadgeMatch = 4;//EXCELLENT

        }else if(experience >= 15 && quality >= 3 && responsability >= 50){

            toastMessages.debugMesssage(context, "GOOD_BADGE", 1);
            numericalBadgeMatch = 3;//GOOD

        }else if(quality >= 2 && responsability >= 30){

            toastMessages.debugMesssage(context, "REGULAR_BADGE", 1);
            numericalBadgeMatch = 2;//REGULAR

        }else{

            toastMessages.debugMesssage(context, "BAD_BADGE", 1);
            numericalBadgeMatch = 1;//BAD

        }
        return numericalBadgeMatch;
    }
}
