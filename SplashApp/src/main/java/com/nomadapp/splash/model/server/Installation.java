package com.nomadapp.splash.model.server;

import com.parse.ParseInstallation;

import java.util.ArrayList;
/**
 * Created by David on 8/18/2018 for Splash.
 */
public class Installation {

    public Installation(){
        //Something
    }

    public static void parseInstallationProcess(String channelName){
        ArrayList<String> channels = new ArrayList<>();
        channels.add(channelName);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "381056848565");
        installation.put("channels", channels);
        installation.saveInBackground();
    }

}
