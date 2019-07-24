package com.nomadapp.splash.model.server.parseserver.queries;

import android.content.Context;

import com.nomadapp.splash.model.server.parseserver.CarsClassInterface;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by David on 6/29/2019 for Splash.
 */
public class CarsClassQuery {

    private UserClassQuery userClassQuery;

    public CarsClassQuery(Context ctx){
        userClassQuery = new UserClassQuery(ctx);
    }

    public void getUserCars(final CarsClassInterface carsClassInterface){
        final ParseQuery<ParseObject> carsListQuery = ParseQuery.getQuery("Cars");
        carsListQuery.whereEqualTo("username", userClassQuery.userName());
        carsListQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    carsClassInterface.fetchCars(objects);
                }
            }
        });
    }

    public void deleteCar(final CarsClassInterface.carsDeletion carsToDelete){
        ParseQuery<ParseObject> carToDelete = ParseQuery.getQuery("Cars");
        carToDelete.whereEqualTo("username", userClassQuery.userName());
        carToDelete.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    carsToDelete.deleteCar(objects);
                }
            }
        });
    }
}
