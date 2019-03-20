package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;
import android.content.Intent;

import com.nomadapp.splash.model.server.parseserver.UserClassInterface;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by David on 6/21/2018 for Splash.
 */
public class UserClassQuery {

    private ParseGeoPoint userFetchedLocation;
    private Context context;

    private BoxedLoadingDialog boxedLoadingDialog;

    public ParseGeoPoint getUserFetchedLocation()    {
        return userFetchedLocation;
    }
    public UserClassQuery(Context ctx){
        this.context = ctx;
        boxedLoadingDialog = new BoxedLoadingDialog(context);
    }
    public boolean userExists(){
        return ParseUser.getCurrentUser() != null;
    }
    public boolean userIsCarOwnerOrSplasher(String userType){
        return ParseUser.getCurrentUser().getString("CarOwnerOrSplasher").equals(userType);
    }
    public ParseUser currentUserObject(){
        ParseUser userObj = null;
        if (userExists()){
            userObj = ParseUser.getCurrentUser();
        }
        return userObj;
    }
    public String userName(){
        String username = "";
        if (userExists()) {
            username = ParseUser.getCurrentUser().getUsername();
        }
        return username;
    }
    public String email(){
        String email = "";
        if (userExists()) {
            email = ParseUser.getCurrentUser().getEmail();
        }
        return email;
    }
    public String phone(){
        String phoneNum = "";
        if (userExists()){
            phoneNum = ParseUser.getCurrentUser().getString("phonenumber");
        }
        return phoneNum;
    }
    public String getUserStringAttribute(String attr){
        return ParseUser.getCurrentUser().getString(attr);
    }
    public ParseFile getUserParseFileAttribute(String file){
        return ParseUser.getCurrentUser().getParseFile(file);
    }
    public void getCurrentUserDocument(String isThereASplasher,
                                       final UserClassInterface userClassInterface){
        ParseQuery<ParseUser> currentUserQ = ParseUser.getQuery();
        currentUserQ.whereEqualTo("username", isThereASplasher);
        currentUserQ.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0){
                    //you need to pass the string as a result.
                    userFetchedLocation = objects.get(0).getParseGeoPoint("location");
                    userClassInterface.getCurrentUserRow();
                }
            }
        });
    }
    public void updateUser(final UserClassInterface.updateUserRow updateUserRow){
        if (userExists()){
            currentUserObject().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    updateUserRow.updateUser(e);
                }
            });
        }
    }
    public void logOutUserEP(Class targetClass){
        if (userExists()){
            boxedLoadingDialog.showLoadingDialog();
            ParseUser.logOut();
            context.startActivity(new Intent(context,targetClass));
        }
    }

    public void logOutUserNoEP(){
        if (userExists()){
            ParseUser.logOut();
        }
    }
}
