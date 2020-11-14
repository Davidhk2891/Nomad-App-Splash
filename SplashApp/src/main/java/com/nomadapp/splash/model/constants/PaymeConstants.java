package com.nomadapp.splash.model.constants;

import java.text.DecimalFormat;

/**
 * Created by David on 3/14/2018.
 */

public class PaymeConstants {

    //********************DEVELOPMENT Environment************************//
    /*
    //---------------------------------------------------------//
    public static final String PAYMENT_CLIENT_KEY_DEV = "splash_Fhdsn79C";
    public static final String SPLASH_SELLER_KEY_DEV = "MPL15222-59799S9S-EDC9CPLF-QORGLDSS";
    public static final String CREATE_SELLER_URL = "https://preprod.paymeservice.com/api/create-seller";
    public static final String GENERATE_SALE_URL = "https://preprod.paymeservice.com/api/generate-sale";
    //---------------------------------------------------------//
     */
    //******************************************************************//

    //********************PRODUCTION Environment************************//
    /*
    //---------------------------------------------------------//
    public static final String PAYMENT_CLIENT_KEY_PROD = "splash_572R4hCA";
    public static final String SPLASH_SELLER_KEY_PROD = "MPL15575-867793F7-TOBBUPL4-FIDAY350";
    public static final String CREATE_SELLER_PRODUCTION = "https://ng.paymeservice.com/api/create-seller";
    public static final String GENERATE_SALE_PRODUCTION = "https://ng.paymeservice.com/api/generate-sale";
    //---------------------------------------------------------//
     */
    //******************************************************************//

    //********************CURRENT Environment************************//
    public static final String PAYMENT_CLIENT_KEY = "splash_572R4hCA";
    public static final String SPLASH_SELLER_KEY = "MPL15575-867793F7-TOBBUPL4-FIDAY350";
    public static final String CREATE_SELLER_URL = "https://ng.paymeservice.com/api/create-seller";
    public static final String GENERATE_SALE_URL = "https://ng.paymeservice.com/api/generate-sale";
    //***************************************************************//

    //**********Splasher(Seller) background data for create_seller API**********//
    public static final String SELLER_DESCRIPTION = "Independent car washer";
    public static final String SELLER_SITE_URL = "https://www.splashymycar.com";
    public static final String SELLER_PERSON_BUSINESS_TYPE = "1119";
    public static final int SELLER_INC = 0;
    public static final String SELLER_COUNTRY = "IL";
    public static final Double MARKET_FEE = 25.5;//Splash's Take
    private static final Double CONSTANT_TRANS_FEE = 8.5;
    private static final Double CONSTANT_ISRAEL_TAX = 17.0;
    public static final String MONTHLY_CREDIT_TRACK = "VPLN1528-093431ZI-EYGTGGMH-D5LMKQXL";
    //public static final String NO_CLEARING_NO_PAY = "VPLN1528-092885I3-HOVKGDM9-LRDKKWYQ";
    //*************************************************************************//

    //*************Sale background data for generate_sale API*************//
    public static final String PRODUCT_NAME = "External vehicle wash";
    public static final String CURRENCY = "ILS";
    public static final int INSTALLMENTS = 1;
    //********************************************************************//

    //*************static price***************//
    //15 SHEKEL promo = 12.217 (15)//
    //30 SHEKEL promo = 25.792 (30)//
    //70 SHEKEL promo =  (70)//
    //20 SHEKEL promo =  (20)//
    public static final double STATIC_TEMPORAL_PRICE = 25.792;
    public static final double STATIC_TEMPORAL_PRICE_INTER = 61.990;
    public static final double STATIC_TEMPORAL_PRICE_MOTO = 16.742;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    //****************************************//

    //*************Car Owner Splash Fee Processor*******************//
    public static double splashFeeProcessor(double originalPrice){
        double constantFee = CONSTANT_TRANS_FEE;
        double oneHundred = 100.00;
        return (constantFee * originalPrice) / oneHundred;
    }
    //**************************************************************//

    //*************Car Owner Splash Fee Processor*******************//
    public static double creditCardFeeProcessor(double originalPricePlusTip){
        return ((originalPricePlusTip) * 0.02 + 1.5);
    }
    //**************************************************************//

    //*************Actual Price Shown to Car Owner******************//
    //Pricing new formula which includes taxes in Israel
    public static double priceShownToCarOwner(double splasherPrice){
        return Double.parseDouble(df2.format((1.275 * splasherPrice) + 1.5));
    }
    //**************************************************************//

    //******Taxes calculator for Splasher price: ISRAEL TAXES******//
    public static double taxIsraelCalculator(double splasherPrice){
        double oneHundred = 100.00;
        return (CONSTANT_ISRAEL_TAX * splasherPrice) / oneHundred;
    }
    //*************************************************************//
}
