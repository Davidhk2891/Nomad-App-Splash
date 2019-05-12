package com.nomadapp.splash.model.payment.paymeapis.seller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nomadapp.splash.model.constants.PaymeConstants;
import com.nomadapp.splash.ui.activity.standard.HomeActivity;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by David on 5/31/2018 for Splash.
 */
public class SplashCreateSeller {

    private Context context;
    private ToastMessages toastMessages = new ToastMessages();

    public SplashCreateSeller(Context ctx){
        this.context = ctx;
    }

    public void sendDataToPaymentServer(String paymeClientKey, String sellerFirstName, String
                                        sellerLastName, String sellerSocialId, String sellerBirthDate
    ,String sellerSocialIdIssued, int sellerGender, String sellerEmail, String sellerPhoneNumber
    ,int sellerBankCode, int sellerBankBranch, int sellerBankAccountNumber, String sellerDescription
    ,String sellerSiteUrl, String sellerPersonBussinessType, int sellerInc, String sellerAddressCity
    ,String sellerAddressStreet, int sellerAddressStreetNumber, String sellerAddressCountry
    ,Double sellerMarketFee, String sellerFileSocialId, String sellerFileCheque
    ,String sellerFileCorporate, String sellerPlan){

        //----Send data to Payme API through a network client with a POST request-----//

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        params.put("payme_client_key", paymeClientKey);

        params.put("seller_first_name", sellerFirstName);
        params.put("seller_last_name", sellerLastName);
        params.put("seller_social_id", sellerSocialId);
        params.put("seller_birthdate", sellerBirthDate);
        params.put("seller_social_id_issued", sellerSocialIdIssued);
        params.put("seller_gender", sellerGender);
        params.put("seller_email", sellerEmail);
        params.put("seller_phone", sellerPhoneNumber);

        params.put("seller_bank_code", sellerBankCode);
        params.put("seller_bank_branch", sellerBankBranch);
        params.put("seller_bank_account_number", sellerBankAccountNumber);

        params.put("seller_description", sellerDescription);
        params.put("seller_site_url", sellerSiteUrl);
        params.put("seller_person_business_type", sellerPersonBussinessType);
        params.put("seller_inc", sellerInc);

        params.put("seller_address_city", sellerAddressCity);
        params.put("seller_address_street", sellerAddressStreet);
        params.put("seller_address_street_number", sellerAddressStreetNumber);
        params.put("seller_address_country", sellerAddressCountry);

        params.put("market_fee", sellerMarketFee);

        params.put("seller_file_social_id", sellerFileSocialId);
        params.put("seller_file_cheque", sellerFileCheque);
        params.put("seller_file_corporate", sellerFileCorporate);

        //--PARAMETER RELEVANT TO APP IN PRODUCTION ONLY!--//
        params.put("seller_plan", sellerPlan);
        //-------------------------------------------------//
        JSONObject parameter = new JSONObject(params);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url(PaymeConstants.CREATE_SELLER_URL)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("error response", call.request().body().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String allResponseString = response.body().string();
                Log.i("responseString", allResponseString);

                //newUntilTime = uncutFullUntilTime.substring(9,17);
                //jsonDataPaymeId

                //{"status_code":0,"seller_payme_id":"MPL15220-39260RZZ-NFFWR8LX-S619FGPK","seller_payme_secret":"p0mFfJMTe5Kltg8EguuvrQ74V5jczZ","seller_id":null}
                //0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234
                //0---------10--------20--------30---35---40--------50--------60--------70--------80--------90--------100-------110-------120-------130-------140--144

                String jsonDataPaymeId = allResponseString.substring(36, 71);
                Log.i("paymeIdSubS", jsonDataPaymeId);
                sendSellerId(jsonDataPaymeId);
            }
        });
        //----------------------------------------------------------------------------//
    }

    //Seller id to parse
    private void sendSellerId(final String sellerId){
        ParseQuery<ParseObject> filesQuery2 = new ParseQuery<>("Documents");
        filesQuery2.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        filesQuery2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject fileObject : objects){
                            fileObject.put("sellerId", sellerId);
                            fileObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        toastMessages.productionMessage(context.getApplicationContext(),
                                                "You are now an independent splasher",1);
                                        context.startActivity(new Intent(context, HomeActivity.class));
                                    }else{
                                        toastMessages.productionMessage(context.getApplicationContext(),
                                                "Something went wrong. Try again",1);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
