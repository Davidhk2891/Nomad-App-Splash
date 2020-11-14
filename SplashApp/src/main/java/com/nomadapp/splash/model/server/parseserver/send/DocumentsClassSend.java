package com.nomadapp.splash.model.server.parseserver.send;

import android.content.Context;

import com.nomadapp.splash.model.server.parseserver.DocumentsClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by David on 7/22/2018 for Splash.
 */
public class DocumentsClassSend {

    private Context context;

    private UserClassQuery userClassQuery;

    public DocumentsClassSend(Context ctx){
        this.context = ctx;
        userClassQuery = new UserClassQuery(context);
    }

    public void sendSplasherDocumentsToServer(final DocumentsClassInterface.sendSplasherDocs
                                                      sendSplasherDocs){
        ParseQuery<ParseObject> filesQuery = new ParseQuery<>("Documents");
        filesQuery.whereEqualTo("username", userClassQuery.userName());
        filesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseObject filesObj : objects){
                            sendSplasherDocs.sendDocs(filesObj);
                        }
                    }
                }
            }
        });
    }

}
