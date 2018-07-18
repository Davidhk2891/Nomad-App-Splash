package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by David on 7/18/2018 for Splash.
 */
public class MetricsClassQuery {

    private Context context;
    private UserClassQuery userClassQuery;

    public MetricsClassQuery(Context ctx){
        this.context = ctx;
        userClassQuery = new UserClassQuery(ctx);
    }

    /**
     * This method is a counter. It is going to add 1 to the existing number the server pulled.
     * @param parseVarToUpdate has to be the exact var name from its column in the server.
     */
    public void queryMetricsToUpdate(final String parseVarToUpdate){
        final ParseQuery<ParseObject> metQuery = ParseQuery.getQuery("Metrics");
        metQuery.whereEqualTo("username",userClassQuery.userName());
        metQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject metObject : objects){
                            int intParseVarCounter;
                            String finalParseVarCounter;
                            finalParseVarCounter = metObject.getString(parseVarToUpdate);
                            intParseVarCounter = Integer.parseInt(finalParseVarCounter);
                            intParseVarCounter++;
                            finalParseVarCounter = String.valueOf(intParseVarCounter);
                            metObject.put(parseVarToUpdate,finalParseVarCounter);
                            metObject.saveInBackground();
                        }
                    }
                }
            }
        });
    }

}
