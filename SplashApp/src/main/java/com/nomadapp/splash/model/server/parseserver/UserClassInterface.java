package com.nomadapp.splash.model.server.parseserver;

import com.parse.ParseException;

/**
 * Created by David on 7/21/2018 for Splash.
 */
public interface UserClassInterface {

    void getCurrentUserRow();

    interface updateUserRow{
        void updateUser(ParseException e);
    }
}
