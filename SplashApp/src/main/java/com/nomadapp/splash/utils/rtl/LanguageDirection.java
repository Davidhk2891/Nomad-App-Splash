package com.nomadapp.splash.utils.rtl;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by David on 3/23/2019 for Splash.
 */
public class LanguageDirection {

    //Constructor:
    public LanguageDirection(){

    }

    /**
     * Language orientation: 1 = left; 0 || * = right
     * @param textView
     */
    public void widgetLanguageDirection(TextView textView, int directionNum){
        if (directionNum == 1){
            //RIGHT
            Log.i("black1","ran");
            textView.setGravity(Gravity.END);
            textView.setGravity(Gravity.RIGHT);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }else{
            //LEFT
            Log.i("black2","ran");
            textView.setGravity(Gravity.START);
            textView.setGravity(Gravity.LEFT);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }
    }

    /**
     * Language orientation: 1 = left; 0 || * = right
     * @param editText
     */
    public void widgetLanguageDirection(EditText editText, int directionNum){
        if (directionNum == 1){
            //RIGHT
            editText.setGravity(Gravity.END);
            editText.setGravity(Gravity.RIGHT);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }else{
            //LEFT
            editText.setGravity(Gravity.START);
            editText.setGravity(Gravity.LEFT);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }
    }
}
