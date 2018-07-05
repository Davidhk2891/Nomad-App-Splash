package com.nomadapp.splash.model.localdatastorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nomadapp.splash.model.objects.MyCar;

import java.util.ArrayList;

import com.nomadapp.splash.model.constants.CarConstantsLocalDB;

/**
 * Created by David on 6/25/2017.
 */

public class CarLocalDatabaseHandler extends SQLiteOpenHelper {

    private final ArrayList<MyCar> theCarList = new ArrayList<>();

    public CarLocalDatabaseHandler(Context context) {
        super(context, CarConstantsLocalDB.DATABASE_NAME, null, CarConstantsLocalDB.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
            String CREATE_WISHES_TABLE = "CREATE TABLE " + CarConstantsLocalDB.TABLE_NAME +
                "(" + CarConstantsLocalDB.KEY_ID + " INTEGER PRIMARY KEY, " +
                CarConstantsLocalDB.TITLE_NAME + " TEXT, " + CarConstantsLocalDB.CONTENT_NAME + " TEXT, " +
                CarConstantsLocalDB.DATE_NAME + " LONG);";

            db.execSQL(CREATE_WISHES_TABLE);
         */

        String CREATE_CARS_TABLE = "CREATE TABLE " + CarConstantsLocalDB.TABLE_NAME +
                "(" + CarConstantsLocalDB.KEY_ID + " INTEGER PRIMARY KEY, " +
                CarConstantsLocalDB.BRAND_NAME + " TEXT, " + CarConstantsLocalDB.MODEL_NAME + " TEXT, " +
                CarConstantsLocalDB.COLOR_NAME + " TEXT, " + " TEXT, " +
                CarConstantsLocalDB.PLATE_NAME + " TEXT);";

        db.execSQL(CREATE_CARS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CarConstantsLocalDB.TABLE_NAME);

        //create a new one
        onCreate(db);

    }

    public void deleteCar(int id){

        SQLiteDatabase deletefromDB = this.getWritableDatabase();

        deletefromDB.delete(CarConstantsLocalDB.TABLE_NAME, CarConstantsLocalDB.KEY_ID + " = ? ",
                new String[]{String.valueOf(id)});

        deletefromDB.close();


    }

    public void addCars(MyCar car){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues myValues = new ContentValues();

        myValues.put(CarConstantsLocalDB.BRAND_NAME, car.getBrand());

        myValues.put(CarConstantsLocalDB.MODEL_NAME, car.getModel());

        myValues.put(CarConstantsLocalDB.COLOR_NAME, car.getColorz());

        myValues.put(CarConstantsLocalDB.PLATE_NAME, car.getPlateNum());

        db.insert(CarConstantsLocalDB.TABLE_NAME, null, myValues);

        Log.i("Car Table", "Car added");

        db.close();

    }

    //Get all of the Wishes
    public ArrayList<MyCar> getCars(){

        String selectQuery = "SELECT * FROM" + CarConstantsLocalDB.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor theCursor = db.query(CarConstantsLocalDB.TABLE_NAME, new String[]{
                        CarConstantsLocalDB.KEY_ID, CarConstantsLocalDB.BRAND_NAME
                        , CarConstantsLocalDB.MODEL_NAME, CarConstantsLocalDB.COLOR_NAME
                        , CarConstantsLocalDB.PLATE_NAME}
                        , null, null, null, null
                        , CarConstantsLocalDB.BRAND_NAME + "   DESC");

        if (theCursor.moveToFirst()){

            do{

                MyCar getCar = new MyCar();

                getCar.setBrand(theCursor.getString(theCursor
                        .getColumnIndex(CarConstantsLocalDB.BRAND_NAME)));

                getCar.setModel(theCursor.getString(theCursor
                        .getColumnIndex(CarConstantsLocalDB.MODEL_NAME)));

                getCar.setColorz(theCursor.getString(theCursor
                        .getColumnIndex(CarConstantsLocalDB.COLOR_NAME)));

                getCar.setPlateNum(theCursor.getString(theCursor
                        .getColumnIndex(CarConstantsLocalDB.PLATE_NAME)));

                getCar.setItemId(theCursor.getInt(theCursor
                        .getColumnIndex(CarConstantsLocalDB.KEY_ID)));

                theCarList.add(getCar);

            } while (theCursor.moveToNext());

        }

        theCursor.close();
        db.close();

        return theCarList;

    }

}

