/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.nomadapp.splash.model.server;

import android.support.multidex.MultiDexApplication;

import com.parse.Parse;
import com.parse.ParseACL;

public class StarterApplication extends MultiDexApplication {

  @Override
  public void onCreate() {
      super.onCreate();
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
