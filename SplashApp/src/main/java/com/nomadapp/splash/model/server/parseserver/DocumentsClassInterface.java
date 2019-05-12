package com.nomadapp.splash.model.server.parseserver;

import com.parse.ParseObject;

/**
 * Created by David on 7/22/2018 for Splash.
 */
public interface DocumentsClassInterface {

    interface sendSplasherDocs{
        void sendDocs(ParseObject fileObject);
    }

    interface getSplasherDocs{
        void getDocs(ParseObject object);
    }

}
