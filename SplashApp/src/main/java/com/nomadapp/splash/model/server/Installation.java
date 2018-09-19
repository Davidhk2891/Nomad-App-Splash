package com.nomadapp.splash.model.server;

import android.content.Context;

import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.parse.ParseInstallation;

import java.util.ArrayList;
/**
 * Created by David on 8/18/2018 for Splash.
 */
public class Installation {

    private Context context;
    private UserClassQuery userClassQuery;

    public Installation(Context ctx){
        this.context = ctx;
        userClassQuery = new UserClassQuery(ctx);
    }

    public void parseInstallationProcess(String channelName){
        ArrayList<String> channels = new ArrayList<>();
        channels.add(channelName);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("username", userClassQuery.userName());
        installation.put("GCMSenderId", "381056848565");
        installation.put("channels", channels);
        installation.saveInBackground();
    }

}
