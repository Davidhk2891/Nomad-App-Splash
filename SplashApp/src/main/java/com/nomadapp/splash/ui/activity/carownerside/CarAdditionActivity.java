package com.nomadapp.splash.ui.activity.carownerside;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.nomadapp.splash.R;

import java.util.Locale;

import com.nomadapp.splash.model.localdatastorage.CarLocalDatabaseHandler;
import com.nomadapp.splash.model.objects.MyCar;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.CarsClassSend;
import com.nomadapp.splash.utils.rtl.LanguageDirection;

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
    private LanguageDirection languageDirection = new LanguageDirection();

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

            languageDirection.widgetLanguageDirection(cCarBrandEdit,1);
            languageDirection.widgetLanguageDirection(cCarModelEdit,1);
            languageDirection.widgetLanguageDirection(cCarColorEdit,1);
            languageDirection.widgetLanguageDirection(cCarYearEdit,1);
            languageDirection.widgetLanguageDirection(cCarPlateNEdit,1);

        }else{

            languageDirection.widgetLanguageDirection(cCarBrandEdit,0);
            languageDirection.widgetLanguageDirection(cCarModelEdit,0);
            languageDirection.widgetLanguageDirection(cCarColorEdit,0);
            languageDirection.widgetLanguageDirection(cCarYearEdit,0);
            languageDirection.widgetLanguageDirection(cCarPlateNEdit,0);
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
        carsClassSend.sendSavedClassToServer(carBrandEdit,carModelEdit,carColorEdit,carPlateEdit);
    }
}
