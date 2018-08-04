package com.nomadapp.splash.model.server.parseserver;

import com.parse.ParseException;

/**
 * Created by David on 7/30/2018 for Splash.
 */
public interface MessageClassInterface {
    void afterMessageSent(ParseException e);
}
