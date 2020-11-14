package com.nomadapp.splash.model.objects.users;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.activity.carownerside.WashReqParamsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 4/1/2019 for Splash.
 */
public class SplasherSelector {

    private Context context;

    public static List<String> COLLECTOR_LIST = new ArrayList<>();
    private static String[] COLLECTOR_ARRAY = new String[COLLECTOR_LIST.size()];

    public static List<String> RESPECTIVE_OG_PRICES_LIST = new ArrayList<>();
    private static String[] RESPECTIVE_OG_PRICES_ARRAY = new String[RESPECTIVE_OG_PRICES_LIST.size()];

    public static List<String> RESPECTIVE_PRICES_LIST = new ArrayList<>();
    private static String[] RESPECTIVE_PRICES_ARRAY = new String[RESPECTIVE_PRICES_LIST.size()];

    //TEST IT ALL//
    //WORKS. NOW TEST WITH RANDOM PRICE//

    //Constructor
    public SplasherSelector(Context ctx){
        this.context = ctx;
    }

    public void selectSplashers(CardView cardView, TextView textView1, TextView textView2,
                                String splasherUsername, String splasherPrice, String carOwnerPrice){
        WashReqParamsActivity.individuallyChecked = true;
        if (cardView.getCardBackgroundColor().getDefaultColor() == -1) {
            //ADDING SPLASHER. we change cardView color
            splasherCollector(splasherUsername, splasherPrice, carOwnerPrice);
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
            textView1.setTextColor(context.getResources().getColor(R.color.pureWhite));
            textView2.setTextColor(context.getResources().getColor(R.color.pureWhite));
        } else {
            //DELETING SPLASHER. we change back default color (white)
            splasherEliminator(splasherUsername, splasherPrice, carOwnerPrice);
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pureWhite));
            textView1.setTextColor(context.getResources().getColor(R.color.ColorPrimaryDark));
            textView2.setTextColor(context.getResources().getColor(R.color.ColorPrimaryDark));
            //need to revise everything
        }
    }

    private void splasherCollector(String splasherUsername, String splasherPrice, String carOwnerPrice){
        //check if splasher already exists//TEST
        if(!COLLECTOR_LIST.contains(splasherUsername)){
            COLLECTOR_LIST.add(splasherUsername);
            RESPECTIVE_OG_PRICES_LIST.add(splasherPrice);
            RESPECTIVE_PRICES_LIST.add(carOwnerPrice);

            Log.i("greenList", "added " + COLLECTOR_LIST.toString()
                    + " - " + RESPECTIVE_OG_PRICES_LIST.toString()
                    + " - " + RESPECTIVE_PRICES_LIST.toString());
        }else{
            Log.i("greenList", "Splasher already on the list");
        }
    }

    private void splasherEliminator(String splasherUsername, String splasherPrice, String carOwnerPrice){
        //remove element by element value.
        COLLECTOR_LIST.remove(splasherUsername);
        RESPECTIVE_OG_PRICES_LIST.remove(splasherPrice);
        RESPECTIVE_PRICES_LIST.remove(carOwnerPrice);

        Log.i("greenList", COLLECTOR_LIST.toString()
                + " - " + RESPECTIVE_OG_PRICES_LIST.toString()
                + " - " + RESPECTIVE_PRICES_LIST.toString());

        if (COLLECTOR_LIST.size() == 0){
            WashReqParamsActivity.individuallyChecked = false;
        }
    }

    public void deleteSplasherCollection(){
        COLLECTOR_LIST.clear();
        RESPECTIVE_OG_PRICES_LIST.clear();
        RESPECTIVE_PRICES_LIST.clear();

        Log.i("greenList", COLLECTOR_LIST.toString()
                + " - " + RESPECTIVE_OG_PRICES_LIST.toString()
                + " - " + RESPECTIVE_PRICES_LIST.toString());
    }

    public void addWholeSplasherColletion(ArrayList<String> splasherCollection,
                                          ArrayList<String> ogPricesCollection,
                                          ArrayList<String> pricesCollection){
        COLLECTOR_LIST.clear();
        RESPECTIVE_OG_PRICES_LIST.clear();
        RESPECTIVE_PRICES_LIST.clear();

        COLLECTOR_LIST.addAll(splasherCollection);
        RESPECTIVE_OG_PRICES_LIST.addAll(ogPricesCollection);
        RESPECTIVE_PRICES_LIST.addAll(pricesCollection);
    }

    public String[] getCollectorArrayToSend() {
        COLLECTOR_ARRAY = COLLECTOR_LIST.toArray(COLLECTOR_ARRAY);
        return COLLECTOR_ARRAY;
    }

    public String[] getRespectiveOgPricesArrayToSend() {
        RESPECTIVE_OG_PRICES_ARRAY = RESPECTIVE_OG_PRICES_LIST.toArray(RESPECTIVE_OG_PRICES_ARRAY);
        return RESPECTIVE_OG_PRICES_ARRAY;
    }

    public String[] getRespectivePricesArrayToSend(){
        RESPECTIVE_PRICES_ARRAY = RESPECTIVE_PRICES_LIST.toArray(RESPECTIVE_PRICES_ARRAY);
        return RESPECTIVE_PRICES_ARRAY;
    }

    public void splasherListCheckerToOrder(Button orderButton){
        //add this when adding/deleting all
        if (COLLECTOR_LIST.size() == 0){
            //Ready to order
            orderButton.setBackgroundResource(R.drawable.btn_shape_grey);
        }else{
            //Not ready to order
            orderButton.setBackgroundResource(R.drawable.btn_shape);
        }
    }
}
