package com.nomadapp.splash.model.server;

import com.parse.ParseCloud;

import java.util.HashMap;

/**
 * Created by David on 8/17/2018 for Splash.
 */
public class CloudTriggers {

    public CloudTriggers(){
        //Something
    }

    public static void triggerCloudFuncNoti(){
        HashMap<String, Object> params = new HashMap<>();
        //params.put("title", "Requests available!");
        //params.put("alert", "you have new requests available near you");
        ParseCloud.callFunctionInBackground("pushnewreq", params);
    }
}
