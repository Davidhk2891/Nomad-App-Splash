package com.nomadapp.splash.model.objects.users;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.nomadapp.splash.R;

/**
 * Created by David on 4/1/2019 for Splash.
 */
public class SplasherSelector {

    private Context context;

    public SplasherSelector(Context ctx){
        this.context = ctx;
    }

    public void selectSplashers(CardView cardView, TextView textView1, TextView textView2){
        if (cardView.getCardBackgroundColor().getDefaultColor() == -1){
            //we change cardView color
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
            textView1.setTextColor(context.getResources().getColor(R.color.pureWhite));
            textView2.setTextColor(context.getResources().getColor(R.color.pureWhite));
        }else{
            //we change back default color (white)
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pureWhite));
            textView1.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            textView2.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    public void selectSplashers(){

    }
}
