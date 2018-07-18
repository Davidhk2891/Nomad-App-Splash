package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;

import com.parse.ParseUser;

/**
 * Created by David on 6/21/2018 for Splash.
 */
public class UserClassQuery {

    private Context context;

    public UserClassQuery(Context ctx){
        this.context = ctx;
    }
    public boolean userExists(){
        return ParseUser.getCurrentUser() != null;
    }
    public boolean userIsCarOwnerOrSplasher(String userType){
        return ParseUser.getCurrentUser().getString("CarOwnerOrSplasher").equals(userType);
    }
    public String userName(){
        String username = "";
        if (userExists()) {
            username = ParseUser.getCurrentUser().getUsername();
        }
        return username;
    }
}
