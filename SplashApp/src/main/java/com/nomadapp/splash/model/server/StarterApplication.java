/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.nomadapp.splash.model.server;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.nomadapp.splash.utils.forcedupdate.ForceUpdateChecker;
import com.parse.Parse;
import com.parse.ParseACL;

import java.util.HashMap;
import java.util.Map;

public class StarterApplication extends MultiDexApplication {

    private static final String TAG = StarterApplication.class.getSimpleName();

  @Override
  public void onCreate() {
      super.onCreate();

      //Firebase//
      final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

      // set in-app defaults
      Map<String, Object> remoteConfigDefaults = new HashMap<>();
      remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
      remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0.0");
      remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
              "https://play.google.com/store/apps/details?id=com.nomadapp.splash");

      firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
      firebaseRemoteConfig.fetch(60) // fetch every minutes
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()) {
                          Log.i("onComplete", ">>>RAN<<<");
                          Log.d(TAG, "remote config is fetched.");
                          firebaseRemoteConfig.activateFetched();
                      }
                  }
              });
      //--------//

          // Enable Local DataStore.
          // Parse.enableLocalDataStore(this);
          // Add your initialization code here

         //NEW ELASTIC IP OF WORKING SERVER WITH FRESH FREE TIER
         //http://18.197.183.79:80/parse/
         /*
         Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
           .applicationId("")
           .clientKey("")
           .server("http://18.197.183.79:80/parse/")
           .build()
      );
         */
         //---------------------------------------------------//

         //OLD STATIC IP WORKING ON ALREADY USED FREE TIER
         //http://18.196.112.78:80/parse/
         /*
         Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
           .applicationId("9f7dad90ac3b53e3b6b039479d34f25d3f8374e2")
           .clientKey("53073afdecd892bba4eab2f0686d73ac8a3d794f")
           .server("http://18.196.112.78:80/parse/")
           .build()
      );
         */
         //--------------------------------------------//
      //CHANGE IP TO ACTUAL ONE
      Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
           .applicationId("7d47144127c5aff85ea549497ef60cd3c11a99fa")
           .clientKey("9f402152713abf7701ab5123719dbbd1e2b588f3")
           //PAY ATTENTION TO WETHER IT STANDS ON HTTP OR HTTPS//
           .server("https://backdoor.splashmycar.com/parse/")
           .build()
      );

      /*
      Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
           .applicationId("myappid")
           .clientKey(null)
           .server("http://SplashELB1-174571188.us-east-2.elb.amazonaws.com/parse/")
           .build()
      );
      */

      //ParseUser.enableAutomaticUser();
      ParseACL defaultACL = new ParseACL();
      defaultACL.setPublicReadAccess(true);
      defaultACL.setPublicWriteAccess(true);
      ParseACL.setDefaultACL(defaultACL, true);
  }
}
