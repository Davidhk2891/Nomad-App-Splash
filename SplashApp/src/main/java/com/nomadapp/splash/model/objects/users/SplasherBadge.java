package com.nomadapp.splash.model.objects.users;

import android.content.Context;
import android.widget.ImageView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.ParseObject;

/**
 * Created by David on 6/21/2018 for Splash.
 */
public class SplasherBadge {
    private Context context;
    private ToastMessages toastMessages = new ToastMessages();
    public SplasherBadge(Context ctx){
        this.context = ctx;
    }
    private void showBadge(int drawable, ImageView iv){
        GlideImagePlacement glideImagePlacement = new GlideImagePlacement(context);
        glideImagePlacement.roundImagePlacementFromDrawable(drawable, iv);
    }
    public void showBadge2(int drawable, ImageView iv){
        GlideImagePlacement glideImagePlacement = new GlideImagePlacement(context);
        glideImagePlacement.roundImagePlacementFromDrawable(drawable, iv);
    }
    public int numBadgeSelector(ParseObject parseObject, ImageView iv){
        int numBadge = 2;
        String badgeDeterminator = parseObject.getString("numericalBadge");
        int numBadgeDeterminator = Integer.parseInt(badgeDeterminator);
        if (numBadgeDeterminator == 4) {
            toastMessages.debugMesssage(context.getApplicationContext(),
                    "EXELLENT", 1);
            showBadge(R.drawable.platbadge, iv);
            numBadge = 4; //EXCELLENT
        } else if (numBadgeDeterminator == 3) {
            toastMessages.debugMesssage(context.getApplicationContext(),
                    "GOOD", 1);
            showBadge(R.drawable.goldbadge, iv);
            numBadge = 3; //GOOD
        } else if (numBadgeDeterminator == 2) {
            toastMessages.debugMesssage(context.getApplicationContext(),
                    "REGULAR", 1);
            showBadge(R.drawable.silverbadge, iv);
            numBadge = 2; //REGULAR
        } else if (numBadgeDeterminator == 1) {
            toastMessages.debugMesssage(context.getApplicationContext(),
                    "BAD", 1);
            showBadge(R.drawable.bronzebadge, iv);
            numBadge = 1; //BAD
        }
        return numBadge;
    }
}
