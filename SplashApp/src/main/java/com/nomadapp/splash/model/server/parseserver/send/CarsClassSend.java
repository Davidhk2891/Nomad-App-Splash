package com.nomadapp.splash.model.server.parseserver.send;

import android.content.Context;

import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.parse.ParseObject;

/**
 * Created by David on 8/8/2018 for Splash.
 */
public class CarsClassSend {

    private UserClassQuery userClassQuery;

    public CarsClassSend(Context ctx){
        userClassQuery = new UserClassQuery(ctx);
    }

    public void sendSavedClassToServer(String brand, String model, String plateNum){
        ParseObject carObject = new ParseObject("Cars");
        carObject.put("username",userClassQuery.userName());
        carObject.put("brand", brand);
        carObject.put("model", model);
        carObject.put("plateNum", plateNum);
        carObject.saveInBackground();
    }
}
