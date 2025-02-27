package com.nomadapp.splash.model.server.parseserver.send;

import android.content.Context;

import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by David on 7/18/2018 for Splash.
 */
public class MetricsClassSend {

    private Context context;
    private ToastMessages toastMessages = new ToastMessages();

    public MetricsClassSend(Context ctx){
        this.context = ctx;
    }

    public void createMetricsClassForUser(String parseUserName, String parseUserType){
        ParseObject metricsObject = new ParseObject("Metrics");
        metricsObject.put("username", parseUserName);
        metricsObject.put("userType", parseUserType);
        metricsObject.put("openedApp", "0");
        metricsObject.put("washMyCar", "0");
        metricsObject.put("orderWash", "0");
        metricsObject.put("allSplashers", "0");
        metricsObject.put("canceledWash", "0");
        metricsObject.put("internalWash", "0");
        metricsObject.put("addNewCar", "0");
        metricsObject.put("carAdded", "0");
        metricsObject.put("addCC", "0");
        metricsObject.put("CCAdded", "0");
        metricsObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    toastMessages.debugMesssage(context.getApplicationContext()
                            ,"Metrics Object created",0);
                }else{
                    toastMessages.productionMessage(context.getApplicationContext()
                            ,e.getMessage(),1);
                }
            }
        });
    }

}
