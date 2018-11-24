package com.nomadapp.splash.model.server.parseserver;

import com.parse.ParseObject;

/**
 * Created by David on 7/22/2018 for Splash.
 */
public interface HistoryClassInterface {

    interface queryNonDepositedMoney{
        void beforeMainOperation();
        void queryMoney(ParseObject moneyObj);
    }

    interface onCreateReqRecord{
        void onRecordCreated();
        void recordFailedToCreate();
    }

}
