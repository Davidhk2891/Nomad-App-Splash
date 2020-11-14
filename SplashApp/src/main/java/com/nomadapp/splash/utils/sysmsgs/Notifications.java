package com.nomadapp.splash.utils.sysmsgs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nomadapp.splash.R;

/**
 * Created by David on 2/7/2018.
 */

public class Notifications{

    private Context notiContext;

    public Notifications(Context ctx){
        notiContext = ctx;
    }

    //--------------------------Application Notifications here:-------------------------------//
    //Call this method wherever you may need to call the notification
    public void newLocalNotification(Class class1, String title, String msg, int id){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(notiContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(notiContext.getApplicationContext(), class1);
        PendingIntent pendingIntent = PendingIntent.getActivity(notiContext, 0, ii, 0);
        Vibrator vibi = (Vibrator) notiContext.getSystemService(Context.VIBRATOR_SERVICE);
        //NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        //bigText.bigText(verseurl);
        //bigText.setBigContentTitle("Today's Bible Verse");
        //bigText.setSummaryText("Text in detail");
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.splashiconnewnoti);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(msg);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);
        //mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) notiContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mBuilder.build());
        }
        Log.i("NEWBackNotiStatus","Notification ran");
        assert vibi != null;
        vibi.vibrate(500);
    }
    //----------------------------------------------------------------------------------------//
}
