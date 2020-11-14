package com.nomadapp.splash.model.server.parseserver;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by David on 8/1/2018 for Splash.
 */
public interface ProfileClassInterface {
    void beforeQueryFetched();

    void updateChanges(List<ParseObject> objects, ParseException e);

    interface verificationStatus{
        void accVerifiedStatus(String splasherStatus);
    }

    interface walletStatus{
        void onWalletStatusAccess(String splasherType);
    }

    interface allSplashersInfo{
        void getInfo(ParseObject object);
        void afterLoop(List<ParseObject> objects);
    }
}
