package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;
import android.widget.ImageView;

import com.nomadapp.splash.model.objects.users.SplasherBadge;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by David on 6/21/2018 for Splash.
 */
public class ProfileClass {
    private Context context;
    private ToastMessages toastMessages = new ToastMessages();
    private SplasherBadge splasherBadge = new SplasherBadge(context);
    public ProfileClass(Context ctx){
        this.context = ctx;
    }
    public void getMyRating(final ImageView iv, final int sNumBadge){
        UserClass userClass = new UserClass(context);
        final ParseQuery<ParseObject> queryGetMyRating = ParseQuery.getQuery("Profile");
        queryGetMyRating.whereEqualTo("username", userClass.userName());
        queryGetMyRating.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //R.drawable.platbadge,, iv,, splasherNumericalBadge
                    for (ParseObject ratingObject : objects) {
                        //sNumBadge = splasherBadge.numBadgeSelector(ratingObject, iv);
                    }
                } else {
                    toastMessages.productionMessage(context.getApplicationContext(),
                            e.getMessage(), 1);
                }
            }
        });

    }
}
