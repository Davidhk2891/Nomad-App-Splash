package com.nomadapp.splash.model.mapops;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by David on 10/6/2018 for Splash.
 */
public class AddressCoordsLocator {

    private Context context;

    public AddressCoordsLocator(Context ctx){
        this.context = ctx;
    }

    public LatLng locationCoordsFetcher(GoogleMap gMap){
        LatLng areaPointerLocation = gMap.getCameraPosition().target;
        Log.i("areaPointerLocation",String.valueOf(areaPointerLocation));
        return areaPointerLocation;
    }

    public String locationAddressFetcher(LatLng pointerLocation,OnAddressFetched onAddressFetched){
        String readyPointerAddress = "";
        List<Address> pointerAddress;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            pointerAddress = geocoder.getFromLocation(pointerLocation.latitude
                    , pointerLocation.longitude, 1);
            if (pointerAddress != null) {
                readyPointerAddress = pointerAddress.get(0).getAddressLine(0);
                //String readyPointerCity = pointerAddress.get(0).getLocality();
                //String readyPointerCountry = pointerAddress.get(0).getCountryName();
                //String finalSetPointerAddress = readyPointerAddress + " " + readyPointerCity
                //        + " " + readyPointerCountry;
                onAddressFetched.setOnAddressFetched();
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            readyPointerAddress = "failed to retrieve address";
        }
        return readyPointerAddress;
    }

}
