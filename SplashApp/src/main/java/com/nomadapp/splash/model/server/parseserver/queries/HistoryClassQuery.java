package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;

import com.nomadapp.splash.model.server.parseserver.HistoryClassInterface;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by David on 7/22/2018 for Splash.
 */
public class HistoryClassQuery {

    private Context context;
    private UserClassQuery userClassQuery;

    public HistoryClassQuery(Context ctx){
        this.context = ctx;
        userClassQuery = new UserClassQuery(context);
    }

    public void querySplasherNonDepositedMoney(final HistoryClassInterface.queryNonDepositedMoney
                                                       queryNonDepositedMoney){
        final ParseQuery<ParseObject> actualMoneyQuery = ParseQuery.getQuery("History");
        actualMoneyQuery.whereEqualTo("splasherUsername", userClassQuery.userName());
        actualMoneyQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        queryNonDepositedMoney.beforeMainOperation();
                        for (ParseObject moneyObj : objects){
                            queryNonDepositedMoney.queryMoney(moneyObj);
                        }
                    }
                }
            }
        });
    }

}
