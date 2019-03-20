package com.nomadapp.splash.ui.activity.carownerside.payment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.nomadapp.splash.model.payment.paymeapis.ssale.SplashGenerateSaleManual;
import com.nomadapp.splash.R;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

public class PaymentBillActivity extends AppCompatActivity {

    //₪ XX.XX

    //transactionSplashFee: 8.5% of originalPrice
    //orgPricePlusFee = originalPrice + transactionSplashFee

    private String splasherRating;
    private String buyer_buyer_key;
    private String car, color ,brand, model, plate;
    private String untilTime;

    private WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile
            (PaymentBillActivity.this);
    private SplashGenerateSaleManual splashGenerateSaleManual = new SplashGenerateSaleManual
            (PaymentBillActivity.this,this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_bill);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            splasherRating = bundle.getString("splasherRating");
        }

        TextView mBillCarWashPrice = findViewById(R.id.billCarWashPrice);
        TextView mBillSplashFeePrice = findViewById(R.id.billSplashFeePrice);
        TextView mBillCCFeePrice = findViewById(R.id.billCCFeePrice);
        TextView mBillTipPrice = findViewById(R.id.billTipPrice);
        TextView mBillTotalPrice = findViewById(R.id.billTotalPrice);
        //Button mBillCheckout = findViewById(R.id.billCheckout);

        //buyer_key deduction//
        if(writeReadDataInFile.readFromFile("buyer_key_permanent").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_temporal").equals("")){
            //Temporal buyer_key
            //getDataFromServerToPay();//<<<<FINAL-->DATA TO PAY
            Log.i("buyer_key_temporal","this should not trigger");

        }else if(writeReadDataInFile.readFromFile("buyer_key_temporal").equals("") &&
                !writeReadDataInFile.readFromFile("buyer_key_permanent").equals("")){

            //Permanent buyer_key
            getDataFromServerToPay();
            buyer_buyer_key = writeReadDataInFile
                    .readFromFile("buyer_key_permanent");//<<<<FINAL-->DATA TO PAY
        }
        //-------------------//

        splashGenerateSaleManual.getAllDataForManualPayment(mBillCarWashPrice, mBillTipPrice
                , mBillSplashFeePrice, mBillCCFeePrice, mBillTotalPrice);

    }

    public void getDataFromServerToPay(){
        ParseQuery<ParseObject> tempKey = ParseQuery.getQuery("Request");
        tempKey.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        tempKey.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for (ParseObject keyObj : objects){
                            //buyer_buyer_key = keyObj.getString("buyerKey");
                            color = keyObj.getString("carColor");
                            brand = keyObj.getString("carBrand");
                            model = keyObj.getString("carModel");
                            plate = keyObj.getString("carplateNumber");

                            untilTime = keyObj.getString("untilTime");
                            car = color + " " + brand + " " + model + " " + plate;
                            Log.i("carFINAL", "is " + car);
                        }
                    }
                }
            }
        });
    }

    public void checkout(View view){
        Log.i("1st payment params", "are " + splasherRating + " " + buyer_buyer_key
        + " " + untilTime + " " + car);
        splashGenerateSaleManual.checkout(splasherRating, buyer_buyer_key, untilTime, car);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        /*
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //Do nothing for now
        }
        */
        return false;
    }
}
