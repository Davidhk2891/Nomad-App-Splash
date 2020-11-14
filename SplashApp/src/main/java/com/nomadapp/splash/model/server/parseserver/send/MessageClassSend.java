package com.nomadapp.splash.model.server.parseserver.send;

import com.nomadapp.splash.model.server.parseserver.MessageClassInterface;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by David on 7/30/2018 for Splash.
 */
public class MessageClassSend {

    public void sendMessagesToServer(String currentUsername, String currentEmail
            , String lockedMessage, ParseFile compressedFile1
            , final MessageClassInterface mci){
        ParseObject message = new ParseObject("Messages");
        message.put("username", currentUsername);
        message.put("userEmail", currentEmail);
        message.put("message", lockedMessage);
        message.put("file", compressedFile1);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mci.afterMessageSent(e);
            }
        });
    }

    public void sendMessagesToServer2(String currentUsername, String currentEmail
            , String lockedMessage, String fileText, ParseFile compressedFile1
            , final MessageClassInterface mci){
        ParseObject message = new ParseObject("Messages");
        message.put("username", currentUsername);
        message.put("userEmail", currentEmail);
        message.put("message", lockedMessage);
        if (!fileText.isEmpty() || !fileText.equals("")) {
            message.put("file", compressedFile1);
        }
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mci.afterMessageSent(e);
            }
        });
    }

}
