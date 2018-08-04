package com.nomadapp.splash.model.server.parseserver;

import com.parse.ParseObject;

/**
 * Created by David on 7/21/2018 for Splash.
 */
public interface RequestClassInterface {

    void requestClassMethod();

    void setCarWasherFinderToFalse();

    interface TakenRequest{
        void fetchThisTakenRequest(ParseObject object);
    }

    interface clearData{
        void clearAllDataOnly();
    }

    interface requestDeletion{
        void deleteRequest();
        void handleRest();
    }
}
