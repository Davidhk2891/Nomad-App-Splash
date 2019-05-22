package com.nomadapp.splash.model.mapops;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.nomadapp.splash.R;

/**
 * Created by David on 5/16/2019 for Splash.
 */
public class ServiceLimits {

    private Context context;
    private GoogleMap map;


    public ServiceLimits(Context ctx, GoogleMap mMap){
        this.context = ctx;
        this.map = mMap;
    }

    public void setServiceLimits(){
        Polygon servicePolygon = map.addPolygon(new PolygonOptions()
                .add(
                        new LatLng(32.146116,34.789551),
                        new LatLng(32.144444,34.802512),
                        new LatLng(32.140302,34.800967),
                        new LatLng(32.136159,34.797877),
                        new LatLng(32.133761,34.796675),
                        new LatLng(32.128891,34.795645),
                        new LatLng(32.127219,34.809292),
                        new LatLng(32.123948,34.811782),
                        new LatLng(32.123003,34.823197),
                        new LatLng(32.123875,34.823798),
                        new LatLng(32.125547,34.821566),
                        new LatLng(32.129545,34.819335),
                        new LatLng(32.132452,34.818820),
                        new LatLng(32.132670,34.823626),
                        new LatLng(32.132598,34.828776),
                        new LatLng(32.128527,34.827489),
                        new LatLng(32.128382,34.828948),
                        new LatLng(32.128745,34.829377),
                        new LatLng(32.128673,34.833239),
                        new LatLng(32.127510,34.837273),
                        new LatLng(32.125329,34.836501),
                        new LatLng(32.125256,34.839848),
                        new LatLng(32.124021,34.840277),
                        new LatLng(32.123730,34.841307),
                        new LatLng(32.123003,34.841822),
                        new LatLng(32.121476,34.842166),
                        new LatLng(32.120677,34.843796),
                        new LatLng(32.118133,34.844311),
                        new LatLng(32.115734,34.845513),
                        new LatLng(32.110136,34.844054),
                        new LatLng(32.108900,34.844826),
                        new LatLng(32.107361,34.844212),
                        new LatLng(32.106634,34.841208),
                        new LatLng(32.106852,34.840007),
                        new LatLng(32.104816,34.836144),
                        new LatLng(32.104889,34.833741),
                        new LatLng(32.105471,34.830737),
                        new LatLng(32.105325,34.828248),
                        new LatLng(32.104598,34.825244),
                        new LatLng(32.103435,34.822755),
                        new LatLng(32.102635,34.824300),
                        new LatLng(32.102199,34.823355),
                        new LatLng(32.101545,34.821381),
                        new LatLng(32.100527,34.821210),
                        new LatLng(32.099218,34.822240),
                        new LatLng(32.096382,34.819665),
                        new LatLng(32.097909,34.818549),
                        new LatLng(32.098345,34.815802),
                        new LatLng(32.096019,34.813657),
                        new LatLng(32.095582,34.812369),
                        new LatLng(32.096164,34.809537),
                        new LatLng(32.095001,34.809108),
                        new LatLng(32.093837,34.808507),
                        new LatLng(32.090783,34.809279),
                        new LatLng(32.089765,34.807133),
                        new LatLng(32.087147,34.806332),
                        new LatLng(32.082929,34.803500),
                        new LatLng(32.081984,34.801612),
                        new LatLng(32.080748,34.801869),
                        new LatLng(32.079730,34.801268),
                        new LatLng(32.079657,34.805646),
                        new LatLng(32.080239,34.806161),
                        new LatLng(32.080311,34.809079),
                        new LatLng(32.080675,34.809422),
                        new LatLng(32.080239,34.811482),
                        new LatLng(32.079220,34.812941),
                        new LatLng(32.078566,34.814229),
                        new LatLng(32.079511,34.817490),
                        new LatLng(32.080311,34.820323),
                        new LatLng(32.079730,34.820237),
                        new LatLng(32.075148,34.819207),
                        new LatLng(32.072020,34.818692),
                        new LatLng(32.069038,34.817490),
                        new LatLng(32.066711,34.816804),
                        new LatLng(32.065111,34.815774),
                        new LatLng(32.063510,34.819035),
                        new LatLng(32.062638,34.820237),
                        new LatLng(32.061765,34.820924),
                        new LatLng(32.060164,34.821610),
                        new LatLng(32.059291,34.821782),
                        new LatLng(32.058564,34.818177),
                        new LatLng(32.058055,34.815688),
                        new LatLng(32.058055,34.812855),
                        new LatLng(32.058200,34.812169),
                        new LatLng(32.055000,34.810881),
                        new LatLng(32.053254,34.812855),
                        new LatLng(32.051144,34.814143),
                        new LatLng(32.048816,34.814400),
                        new LatLng(32.046706,34.814400),
                        new LatLng(32.044815,34.813971),
                        new LatLng(32.040522,34.812684),
                        new LatLng(32.041104,34.810796),
                        new LatLng(32.041905,34.809251),
                        new LatLng(32.043360,34.808907),
                        new LatLng(32.043287,34.800925),
                        new LatLng(32.041177,34.800582),
                        new LatLng(32.041323,34.798951),
                        new LatLng(32.042414,34.798522),
                        new LatLng(32.042196,34.796376),
                        new LatLng(32.040886,34.796805),
                        new LatLng(32.040886,34.795689),
                        new LatLng(32.041614,34.794316),
                        new LatLng(32.041250,34.788995),
                        new LatLng(32.044524,34.786420),
                        new LatLng(32.044451,34.783759),
                        new LatLng(32.040886,34.782386),
                        new LatLng(32.038267,34.779639),
                        new LatLng(32.036754,34.777236),
                        new LatLng(32.032534,34.774661),
                        new LatLng(32.036027,34.764104),
                        new LatLng(32.036027,34.761100),
                        new LatLng(32.035445,34.759040),
                        new LatLng(32.032825,34.758782),
                        new LatLng(32.033262,34.756121),
                        new LatLng(32.030934,34.755521),
                        new LatLng(32.031079,34.753117),
                        new LatLng(32.030642,34.752946),
                        new LatLng(32.030133,34.752688),
                        new LatLng(32.030279,34.751229),
                        new LatLng(32.028969,34.750199),
                        new LatLng(32.032971,34.742217),
                        new LatLng(32.033699,34.740758),
                        new LatLng(32.038864,34.743161),
                        new LatLng(32.042866,34.743504),
                        new LatLng(32.045485,34.742646),
                        new LatLng(32.048832,34.744534),
                        new LatLng(32.056397,34.749684),
                        new LatLng(32.056907,34.754233),
                        new LatLng(32.061126,34.758010),
                        new LatLng(32.067527,34.760241),
                        new LatLng(32.069636,34.760155),
                        new LatLng(32.070072,34.761014),
                        new LatLng(32.084399,34.765992),
                        new LatLng(32.086581,34.764962),
                        new LatLng(32.096616,34.768395),
                        new LatLng(32.103450,34.774146),
                        new LatLng(32.107667,34.771056),
                        new LatLng(32.110212,34.772601),
                        new LatLng(32.110575,34.777064),
                        new LatLng(32.119735,34.780841),
                        new LatLng(32.120389,34.779811),
                        new LatLng(32.123443,34.781270),
                        new LatLng(32.123588,34.782643),
                        new LatLng(32.144520,34.789853),
                        new LatLng(32.146116,34.789551))
        .fillColor(Color.parseColor("#2640c4ff"))
        .strokeColor(context.getResources().getColor(R.color.ColorPrimaryDark))
        .strokeWidth(6f));

        servicePolygon.setTag("Service Area");
        Log.i("black5", "ran");
    }
}
