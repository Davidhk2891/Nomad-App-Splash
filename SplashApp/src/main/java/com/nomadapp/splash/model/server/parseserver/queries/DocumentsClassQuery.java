package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;

import com.nomadapp.splash.model.server.parseserver.DocumentsClassInterface;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by David on 5/7/2019 for Splash.
 */
public class DocumentsClassQuery {

    private Context context;

    public DocumentsClassQuery(Context ctx){
        this.context = ctx;
    }

    public void fetchSplasherDocuments(String splasherUsername, final DocumentsClassInterface
            .getSplasherDocs splasherDocs){
        ParseQuery<ParseObject> allSplashersQuery = ParseQuery.getQuery("Documents");
        allSplashersQuery.whereNotEqualTo("email", splasherUsername);
        allSplashersQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for(ParseObject splasherObj : objects){
                            splasherDocs.getDocs(splasherObj);
                        }
                    }
                }
            }
        });
    }

}
