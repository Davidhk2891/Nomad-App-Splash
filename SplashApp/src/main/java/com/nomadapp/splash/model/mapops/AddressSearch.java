package com.nomadapp.splash.model.mapops;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by David on 5/28/2019 for Splash.
 */
public class AddressSearch {

    private Context context;
    private Activity activity;

    public AddressSearch(Context ctx, Activity ac){
        this.context = ctx;
        this.activity = ac;
    }

    public void launchAutoCompleteAddressSearch(int autoCompleteReqCode){
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(context);
        activity.startActivityForResult(intent, autoCompleteReqCode);
    }
}
