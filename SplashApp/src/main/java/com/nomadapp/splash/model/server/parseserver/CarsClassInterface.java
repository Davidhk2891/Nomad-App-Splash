package com.nomadapp.splash.model.server.parseserver;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by David on 6/29/2019 for Splash.
 */
public interface CarsClassInterface {
    void fetchCars(List<ParseObject> objects);

    interface carsDeletion{
        void deleteCar(List<ParseObject> objects);
    }
}
