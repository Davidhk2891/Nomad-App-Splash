package com.nomadapp.splash.model.server.parseserver.send;

import android.content.Context;

import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by David on 6/21/2018 for Splash.
 */
public class AddressesClass {

    private Context context;
    private ToastMessages toastMessages = new ToastMessages();

    public AddressesClass(Context ctx){
        this.context = ctx;
    }

    public void createAddressClassForUser(String parseUserName){
        ParseObject addressObject = new ParseObject("Addresses");
        addressObject.put("Username", parseUserName);
        addressObject.put("HomeAddress", "none");
        addressObject.put("WorkAddress", "none");
        addressObject.put("OtherAddress1", "none");
        addressObject.put("OtherAddress2", "none");
        addressObject.put("OtherAddress3", "none");
        addressObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    toastMessages.debugMesssage(context.getApplicationContext()
                            ,"Address Object created",0);
                }else{
                    toastMessages.productionMessage(context.getApplicationContext()
                            ,e.getMessage(),1);
                }
            }
        });
    }
}
