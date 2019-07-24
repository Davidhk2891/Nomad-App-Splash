package com.nomadapp.splash.model.mapops;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.nomadapp.splash.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 5/16/2019 for Splash.
 */
public class ServiceLimits {

    private Context context;
    private GoogleMap map;
    private List<LatLng> areaLimits;

    public ServiceLimits(Context ctx, GoogleMap mMap){
        this.context = ctx;
        this.map = mMap;
    }

    public void serviceLimitsChecker(LatLng currentPoint, OnAreaLimitsChecker onAreaLimitsChecker){
        if (PolyUtil.containsLocation(currentPoint, areaLimits, true)){
            //Point is inside the area
            onAreaLimitsChecker.insideLimits();
        }else{
            //Point is outside the area
            onAreaLimitsChecker.outsideLimits();
        }
    }

    private List<LatLng> area(){
        areaLimits = new ArrayList<>();
        areaLimits.add(new LatLng(32.146116,34.789551));
        areaLimits.add(new LatLng(32.144444,34.802512));
        areaLimits.add(new LatLng(32.140302,34.800967));
        areaLimits.add(new LatLng(32.136159,34.797877));
        areaLimits.add(new LatLng(32.133761,34.796675));
        areaLimits.add(new LatLng(32.128891,34.795645));
        areaLimits.add(new LatLng(32.127219,34.809292));
        areaLimits.add(new LatLng(32.123948,34.811782));
        areaLimits.add(new LatLng(32.123003,34.823197));
        areaLimits.add(new LatLng(32.123875,34.823798));
        areaLimits.add(new LatLng(32.125547,34.821566));
        areaLimits.add(new LatLng(32.129545,34.819335));
        areaLimits.add(new LatLng(32.132452,34.818820));
        areaLimits.add(new LatLng(32.132670,34.823626));
        areaLimits.add(new LatLng(32.132598,34.828776));
        areaLimits.add(new LatLng(32.128527,34.827489));
        areaLimits.add(new LatLng(32.128382,34.828948));
        areaLimits.add(new LatLng(32.128745,34.829377));
        areaLimits.add(new LatLng(32.128673,34.833239));
        areaLimits.add(new LatLng(32.127510,34.837273));
        areaLimits.add(new LatLng(32.125329,34.836501));
        areaLimits.add(new LatLng(32.125256,34.839848));
        areaLimits.add(new LatLng(32.124021,34.840277));
        areaLimits.add(new LatLng(32.123730,34.841307));
        areaLimits.add(new LatLng(32.123003,34.841822));
        areaLimits.add(new LatLng(32.121476,34.842166));
        areaLimits.add(new LatLng(32.120677,34.843796));
        areaLimits.add(new LatLng(32.118133,34.844311));
        areaLimits.add(new LatLng(32.115734,34.845513));
        areaLimits.add(new LatLng(32.110136,34.844054));
        areaLimits.add(new LatLng(32.108900,34.844826));
        areaLimits.add(new LatLng(32.107361,34.844212));
        areaLimits.add(new LatLng(32.106634,34.841208));
        areaLimits.add(new LatLng(32.106852,34.840007));
        areaLimits.add(new LatLng(32.104816,34.836144));
        areaLimits.add(new LatLng(32.104889,34.833741));
        areaLimits.add(new LatLng(32.105471,34.830737));
        areaLimits.add(new LatLng(32.105325,34.828248));
        areaLimits.add(new LatLng(32.104598,34.825244));
        areaLimits.add(new LatLng(32.103435,34.822755));
        areaLimits.add(new LatLng(32.102635,34.824300));
        areaLimits.add(new LatLng(32.102199,34.823355));
        areaLimits.add(new LatLng(32.101545,34.821381));
        areaLimits.add(new LatLng(32.100527,34.821210));
        areaLimits.add(new LatLng(32.099218,34.822240));
        areaLimits.add(new LatLng(32.096382,34.819665));
        areaLimits.add(new LatLng(32.097909,34.818549));
        areaLimits.add(new LatLng(32.098345,34.815802));
        areaLimits.add(new LatLng(32.096019,34.813657));
        areaLimits.add(new LatLng(32.095582,34.812369));
        areaLimits.add(new LatLng(32.096164,34.809537));
        areaLimits.add(new LatLng(32.095001,34.809108));
        areaLimits.add(new LatLng(32.093837,34.808507));
        areaLimits.add(new LatLng(32.090783,34.809279));
        areaLimits.add(new LatLng(32.089765,34.807133));
        areaLimits.add(new LatLng(32.087147,34.806332));
        areaLimits.add(new LatLng(32.082929,34.803500));
        areaLimits.add(new LatLng(32.081984,34.801612));
        areaLimits.add(new LatLng(32.080748,34.801869));
        areaLimits.add(new LatLng(32.079730,34.801268));
        areaLimits.add(new LatLng(32.079657,34.805646));
        areaLimits.add(new LatLng(32.080239,34.806161));
        areaLimits.add(new LatLng(32.080311,34.809079));
        areaLimits.add(new LatLng(32.080675,34.809422));
        areaLimits.add(new LatLng(32.080239,34.811482));
        areaLimits.add(new LatLng(32.079220,34.812941));
        areaLimits.add(new LatLng(32.078566,34.814229));
        areaLimits.add(new LatLng(32.079511,34.817490));
        areaLimits.add(new LatLng(32.080311,34.820323));
        areaLimits.add(new LatLng(32.079730,34.820237));
        areaLimits.add(new LatLng(32.075148,34.819207));
        areaLimits.add(new LatLng(32.072020,34.818692));
        areaLimits.add(new LatLng(32.069038,34.817490));
        areaLimits.add(new LatLng(32.066711,34.816804));
        areaLimits.add(new LatLng(32.065111,34.815774));
        areaLimits.add(new LatLng(32.063510,34.819035));
        areaLimits.add(new LatLng(32.062638,34.820237));
        areaLimits.add(new LatLng(32.061765,34.820924));
        areaLimits.add(new LatLng(32.060164,34.821610));
        areaLimits.add(new LatLng(32.059291,34.821782));
        areaLimits.add(new LatLng(32.058564,34.818177));
        areaLimits.add(new LatLng(32.058055,34.815688));
        areaLimits.add(new LatLng(32.058055,34.812855));
        areaLimits.add(new LatLng(32.058200,34.812169));
        areaLimits.add(new LatLng(32.055000,34.810881));
        areaLimits.add(new LatLng(32.053254,34.812855));
        areaLimits.add(new LatLng(32.051144,34.814143));
        areaLimits.add(new LatLng(32.048816,34.814400));
        areaLimits.add(new LatLng(32.046706,34.814400));
        areaLimits.add(new LatLng(32.044815,34.813971));
        areaLimits.add(new LatLng(32.040522,34.812684));
        areaLimits.add(new LatLng(32.041104,34.810796));
        areaLimits.add(new LatLng(32.041905,34.809251));
        areaLimits.add(new LatLng(32.043360,34.808907));
        areaLimits.add(new LatLng(32.043287,34.800925));
        areaLimits.add(new LatLng(32.041177,34.800582));
        areaLimits.add(new LatLng(32.041323,34.798951));
        areaLimits.add(new LatLng(32.042414,34.798522));
        areaLimits.add(new LatLng(32.042196,34.796376));
        areaLimits.add(new LatLng(32.040886,34.796805));
        areaLimits.add(new LatLng(32.040886,34.795689));
        areaLimits.add(new LatLng(32.041614,34.794316));
        areaLimits.add(new LatLng(32.041250,34.788995));
        areaLimits.add(new LatLng(32.044524,34.786420));
        areaLimits.add(new LatLng(32.044451,34.783759));
        areaLimits.add(new LatLng(32.040886,34.782386));
        areaLimits.add(new LatLng(32.038267,34.779639));
        areaLimits.add(new LatLng(32.036754,34.777236));
        areaLimits.add(new LatLng(32.032534,34.774661));
        areaLimits.add(new LatLng(32.036027,34.764104));
        areaLimits.add(new LatLng(32.036027,34.761100));
        areaLimits.add(new LatLng(32.035445,34.759040));
        areaLimits.add(new LatLng(32.032825,34.758782));
        areaLimits.add(new LatLng(32.033262,34.756121));
        areaLimits.add(new LatLng(32.030934,34.755521));
        areaLimits.add(new LatLng(32.031079,34.753117));
        areaLimits.add(new LatLng(32.030642,34.752946));
        areaLimits.add(new LatLng(32.030133,34.752688));
        areaLimits.add(new LatLng(32.030279,34.751229));
        areaLimits.add(new LatLng(32.028969,34.750199));
        areaLimits.add(new LatLng(32.032971,34.742217));
        areaLimits.add(new LatLng(32.033699,34.740758));
        areaLimits.add(new LatLng(32.038864,34.743161));
        areaLimits.add(new LatLng(32.042866,34.743504));
        areaLimits.add(new LatLng(32.045485,34.742646));
        areaLimits.add(new LatLng(32.048832,34.744534));
        areaLimits.add(new LatLng(32.056397,34.749684));
        areaLimits.add(new LatLng(32.056907,34.754233));
        areaLimits.add(new LatLng(32.061126,34.758010));
        areaLimits.add(new LatLng(32.067527,34.760241));
        areaLimits.add(new LatLng(32.069636,34.760155));
        areaLimits.add(new LatLng(32.070072,34.761014));
        areaLimits.add(new LatLng(32.084399,34.765992));
        areaLimits.add(new LatLng(32.086581,34.764962));
        areaLimits.add(new LatLng(32.096616,34.768395));
        areaLimits.add(new LatLng(32.103450,34.774146));
        areaLimits.add(new LatLng(32.107667,34.771056));
        areaLimits.add(new LatLng(32.110212,34.772601));
        areaLimits.add(new LatLng(32.110575,34.777064));
        areaLimits.add(new LatLng(32.119735,34.780841));
        areaLimits.add(new LatLng(32.120389,34.779811));
        areaLimits.add(new LatLng(32.123443,34.781270));
        areaLimits.add(new LatLng(32.123588,34.782643));
        areaLimits.add(new LatLng(32.144520,34.789853));
        areaLimits.add(new LatLng(32.146116,34.789551));

        return areaLimits;
    }

    public void setServiceLimits(){
        PolygonOptions serviceAreaOptions = new PolygonOptions();
        for (LatLng areaPoint : area()){
            serviceAreaOptions.add(areaPoint);
        }
        Polygon servicePolygon = map
                .addPolygon(serviceAreaOptions
                        .fillColor(Color.parseColor("#2640c4ff"))
                        .strokeColor(context.getResources().getColor(R.color.ColorPrimaryDark))
                        .strokeWidth(6f));

        servicePolygon.setTag("Service Area");
        Log.i("black5", "ran");
    }
}
