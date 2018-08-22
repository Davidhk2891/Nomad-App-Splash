package com.nomadapp.splash.ui.activity.carownerside;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.nomadapp.splash.R;

import java.util.Locale;

import com.nomadapp.splash.model.localdatastorage.CarLocalDatabaseHandler;
import com.nomadapp.splash.model.objects.MyCar;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.CarsClassSend;

public class CarAdditionActivity extends AppCompatActivity {

    private Context ctx = CarAdditionActivity.this;

    //CarOwner's car details
    private CarLocalDatabaseHandler dbh;
    private EditText cCarBrandEdit, cCarModelEdit, cCarColorEdit, cCarYearEdit, cCarPlateNEdit;

    //semi-primitive variables
    private String carBrandEdit;
    private String carModelEdit;
    private String carColorEdit;
    private String carPlateEdit;

    private MetricsClassQuery metricsClassQuery;
    private UserClassQuery ucq = new UserClassQuery(ctx);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_addition);

        //Navigate back to parent activity1
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        //---------------------------------

        metricsClassQuery = new MetricsClassQuery(ctx);
        metricsClassQuery.queryMetricsToUpdate("addNewCar");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //CarOwner's car details
        dbh = new CarLocalDatabaseHandler(ctx);
        cCarBrandEdit = findViewById(R.id.carBrandEdit);
        cCarModelEdit = findViewById(R.id.carModelEdit);
        cCarColorEdit = findViewById(R.id.carColorEdit);
        cCarYearEdit = findViewById(R.id.carYearEdit);
        cCarPlateNEdit = findViewById(R.id.carPlateNEdit);

        if(Locale.getDefault().getDisplayLanguage().equals("עברית")) {

            cCarBrandEdit.setGravity(Gravity.END);
            cCarBrandEdit.setGravity(Gravity.RIGHT);
            cCarBrandEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarModelEdit.setGravity(Gravity.END);
            cCarModelEdit.setGravity(Gravity.RIGHT);
            cCarModelEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarColorEdit.setGravity(Gravity.END);
            cCarColorEdit.setGravity(Gravity.RIGHT);
            cCarColorEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarYearEdit.setGravity(Gravity.END);
            cCarYearEdit.setGravity(Gravity.RIGHT);
            cCarYearEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarPlateNEdit.setGravity(Gravity.END);
            cCarPlateNEdit.setGravity(Gravity.RIGHT);
            cCarPlateNEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        }else if(Locale.getDefault().getDisplayLanguage().equals("English")){

            cCarBrandEdit.setGravity(Gravity.START);
            cCarBrandEdit.setGravity(Gravity.LEFT);
            cCarBrandEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarModelEdit.setGravity(Gravity.START);
            cCarModelEdit.setGravity(Gravity.LEFT);
            cCarModelEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarColorEdit.setGravity(Gravity.START);
            cCarColorEdit.setGravity(Gravity.LEFT);
            cCarColorEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarYearEdit.setGravity(Gravity.START);
            cCarYearEdit.setGravity(Gravity.LEFT);
            cCarYearEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            cCarPlateNEdit.setGravity(Gravity.START);
            cCarPlateNEdit.setGravity(Gravity.LEFT);
            cCarPlateNEdit.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Navigate back to parent activity2
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        //---------------------------------
        return super.onOptionsItemSelected(item);
    }

    //CarOwner's car details 1
    public void finitoAddingCar(View view){

        AlertDialog.Builder missingCarDataDialog;

        carBrandEdit = cCarBrandEdit.getText().toString();
        carModelEdit = cCarModelEdit.getText().toString();
        carColorEdit = cCarColorEdit.getText().toString();
        carPlateEdit = cCarPlateNEdit.getText().toString();
        if(carBrandEdit.isEmpty() || carModelEdit.isEmpty() || carColorEdit.isEmpty()
                || carPlateEdit.isEmpty()){
            missingCarDataDialog = new AlertDialog.Builder(ctx);
            missingCarDataDialog.setTitle(getResources()
                    .getString(R.string.AddCar_act_java_missingFields));
            missingCarDataDialog.setIcon(android.R.drawable.ic_dialog_alert);
            missingCarDataDialog.setMessage(getResources()
                    .getString(R.string.AddCar_act_java_pleaseFillAllFields));
            missingCarDataDialog.setPositiveButton(getResources()
                    .getString(R.string.AddCar_act_java_ok), null);
            AlertDialog alertD = missingCarDataDialog.create();
            alertD.show();
        } else {
            saveToDB();
        }
    }

    //CarOwner's car details 2
    private void saveToDB(){
        //ArrayList<MyCar> addData = new ArrayList<>();
        //final WashReqParamsActivity.CarAdapter addCarRow = new CarAdditionActivity()
        // .CarAdapter(this, R.layout.activity_wash_req_params, addData);
        saveCarToServer();
        metricsClassQuery.queryMetricsToUpdate("carAdded");

        MyCar thisCar = new MyCar();
        //wish.setTitle(title.getText().toString().trim());
        thisCar.setBrand(carBrandEdit);
        thisCar.setModel(carModelEdit);
        thisCar.setColorz(carColorEdit);
        thisCar.setPlateNum(carPlateEdit);

        dbh.addCars(thisCar);
        dbh.close();
        //THIS GOES TO NEW ACTIVITY
        //clear the editText(s)
        //title.setText("");
        cCarBrandEdit.setText("");
        cCarModelEdit.setText("");
        cCarColorEdit.setText("");
        cCarYearEdit.setText("");
        cCarPlateNEdit.setText("");

        Intent intent = new Intent(ctx,WashReqParamsActivity. class);
        intent.putExtra("listStatus", "keepOpen");
        intent.putExtra("carBrand", carBrandEdit);
        intent.putExtra("carModel", carModelEdit);
        intent.putExtra("carColor", carColorEdit);
        intent.putExtra("carPlate", carPlateEdit);
        startActivity(intent);
    }

    public void saveCarToServer(){
        CarsClassSend carsClassSend = new CarsClassSend(ctx);
        carsClassSend.sendSavedClassToServer(carBrandEdit,carModelEdit,carPlateEdit);
    }
}
