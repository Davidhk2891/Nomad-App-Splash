package com.nomadapp.splash.ui.activity.carownerside;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.ui.activity.carownerside.payment.PaymentBillActivity;
import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.activity.standard.ContactUsActivity;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.nomadapp.splash.utils.sysmsgs.connectionlost.ConnectionLost;
import com.nomadapp.splash.utils.sysmsgs.localnotifications.Notifications;
import com.nomadapp.splash.utils.rating.RatingLogic;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.qiujuer.genius.ui.widget.SeekBar;

import java.io.File;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class WashServiceShowActivity extends AppCompatActivity {

    private ImageView cBfFrontFetched, cBfRearFetched, cBfLeftFetched, cBfRightFetched;
    private HorizontalScrollView cfetchedBfPicsHorizontalScroll;
    private RelativeLayout cLoadingBeforePicsRelative;
    private TextView cWashingStatusTitle;
    private TextView cWashingStatusSubtitle;
    private TextView cDoneWashingStatusTitle;
    private ProgressBar cBarAnimationWashingCar;
    private Button cGoToCheckout;
    private RelativeLayout cWashingInboundPicsRelative;
    private SeekBar cUpdatedSeekBarTwo;
    private MaterialRatingBar cRateThisSplasherAction;
    private EditText cTipThisSplasher;
    private TextView mTipMoneySymbol;

    private ScrollView cScrollViewGrandPa;
    private ImageView cBfFinalFrontFetched, cAfFinalFrontFetched, cBfFinalRearFetched
            , cAfFinalRearFetched, cBfFinalLeftFetched, cAfFinalLeftFetched, cBfFinalRightFetched
            , cAfFinalRightFetched;

    //Checkers ----------------------------------------//
    private boolean picFetchedCheck1 = false;
    private boolean picFetchedCheck2 = false;
    private boolean picFetchedCheck3 = false;
    private boolean picFetchedCheck4 = false;
    private boolean picAFFetchedCheck1 = false;
    private boolean picAFFetchedCheck2 = false;
    private boolean picAFFetchedCheck3 = false;
    private boolean picAFFetchedCheck4 = false;
    private boolean alreadyExecuted = false;
    private boolean alreadyExecuted2 = false;
    private boolean alreadyExecuted3 = false;
    private boolean weAreInAfter2 = false;
    private boolean background = false;
    //-------------------------------------------------//

    private String imageUrl, imageUrl2, imageUrl3, imageUrl4;
    private String afImageUrl, afImageUrl2, afImageUrl3, afImageUrl4;

    private String aboutToBeRatedSplasher;

    private Handler picFetchCheckHandler = new Handler();
    private Animation FadeInOut;
    private RelativeLayout mGettingAllPicsWait;

    //Rating Badge//
    int numericalBadge;
    //------------//

    //Image Load//
    private GlideImagePlacement glideImagePlacement = new GlideImagePlacement
            (WashServiceShowActivity.this);
    //----------//

    //Local Notification//
    private Notifications carWashedNoti = new Notifications(WashServiceShowActivity.this);
    //------------------//

    private WriteReadDataInFile writeReadDataInFile = new
            WriteReadDataInFile(WashServiceShowActivity.this);
    private ConnectionLost clm = new ConnectionLost(WashServiceShowActivity.this);
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(WashServiceShowActivity.this);
    private ToastMessages toastMessages = new ToastMessages();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_service_show);

        background = false;

        createImageGallery();

        cBfFrontFetched = findViewById(R.id.bfFrontFetched);
        cBfRearFetched = findViewById(R.id.bfRearFetched);
        cBfLeftFetched = findViewById(R.id.bfLeftFetched);
        cBfRightFetched = findViewById(R.id.bfRightFetched);

        cGoToCheckout = findViewById(R.id.goToCheckout);

        cWashingInboundPicsRelative = findViewById(R.id.washingInboundPicsRelative);
        cLoadingBeforePicsRelative = findViewById(R.id.loadingBeforePicsRelative);
        cWashingStatusTitle = findViewById(R.id.washingStatusTitle);
        cDoneWashingStatusTitle = findViewById(R.id.doneWashingStatusTitle);
        cWashingStatusSubtitle = findViewById(R.id.washingStatusSubtitle);
        cBarAnimationWashingCar = findViewById(R.id.barAnimationWashingCar);
        FadeInOut = AnimationUtils.loadAnimation(WashServiceShowActivity.this, R.anim.fadeinout);
        cUpdatedSeekBarTwo = findViewById(R.id.updatedSeekBarTwo);
        cRateThisSplasherAction = findViewById(R.id.rateThisSplasherAction);
        cTipThisSplasher = findViewById(R.id.tipThisSplasherAction);
        mTipMoneySymbol = findViewById(R.id.tipMoneySymbol);

        //Before-and-After Slider-------------------------------
        cfetchedBfPicsHorizontalScroll = findViewById(R.id.fetchedBfPicsHorizontalScroll);
        cScrollViewGrandPa = findViewById(R.id.scrollViewGrandPa);

        cBfFinalFrontFetched = findViewById(R.id.bfFinalFrontFetched);
        cBfFinalRearFetched = findViewById(R.id.bfFinalRearFetched);
        cBfFinalLeftFetched = findViewById(R.id.bfFinalLeftFetched);
        cBfFinalRightFetched = findViewById(R.id.bfFinalRightFetched);

        cAfFinalFrontFetched = findViewById(R.id.afFinalFrontFetched);
        cAfFinalRearFetched = findViewById(R.id.afFinalRearFetched);
        cAfFinalLeftFetched = findViewById(R.id.afFinalLeftFetched);
        cAfFinalRightFetched = findViewById(R.id.afFinalRightFetched);
        mGettingAllPicsWait = findViewById(R.id.gettingAllPicsWait);
        ProgressBar mBarAnimationBfPics = findViewById(R.id.barAnimationBfPics);
        TextView mGettingTheBfPicsTitle = findViewById(R.id.gettingTheBfPicsTitle);
        //Before-and-After Slider-------------------------------

        picturesFetchedChecker.run();

        cScrollViewGrandPa.setVisibility(View.GONE);
        cWashingStatusSubtitle.setVisibility(View.GONE);
        cBarAnimationWashingCar.setVisibility(View.GONE);
        cDoneWashingStatusTitle.setVisibility(View.GONE);
        cGoToCheckout.setVisibility(View.GONE);
        mTipMoneySymbol.setVisibility(View.GONE);
        mGettingAllPicsWait.setVisibility(View.GONE);
        cRateThisSplasherAction.setMax(5);

        //SeekbarTwo for actual status guidance (Disabling onTouchListener)
        cUpdatedSeekBarTwo.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //If this code block below runs, then the Car Owner was prompted here stragiht soonafter
        //he opened the app, meaning he had a pending request to pay.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String scratchKey = bundle.getString("key");
            if (scratchKey != null) {
                mGettingAllPicsWait.setVisibility(View.VISIBLE);
                mBarAnimationBfPics.setVisibility(View.GONE);
                mGettingTheBfPicsTitle.setVisibility(View.GONE);
                boxedLoadingDialog.showLoadingDialog();
                weAreInAfter2 = true;
                alreadyExecuted3 = true;
            }
        }
        //----------------------------------------------------------------------------------//

        clm.connectivityStatus(WashServiceShowActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        background = true;
    }

    public Runnable picturesFetchedChecker = new Runnable() {
        @Override
        public void run() {

            getFourPictures();

            Log.i("New Runnable check", " picFetchedChecker working");
            int delay = 7000;
            picFetchCheckHandler.postDelayed(this, delay);//4 secs

        }
    };

    public void getFourPictures(){

        try {

            Log.i("another runnable", "getFourPictures is running");

            ParseQuery<ParseObject> getFourQuery = new ParseQuery<>("Request");

            getFourQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            getFourQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    //Should be a single request. The sole one the Car Owner did
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (final ParseObject object : objects) {
                                //FRONT SIDE--BEFORE AND AFTER//////////////////////////////////////////
                                final ParseFile bfFrontFile = object.getParseFile("beforeFront");
                                if (bfFrontFile != null) {
                                    if (!picFetchedCheck1) {
                                        //THE PICTURE FROM PARSE SERVER HAS AROUND 2.5 SECONDS TO BE FETCHED. OTHERWISE THE LOG
                                        //WILL THROW A SOCKETTIMEOUT EXCEPTION AND THE IMAGE WONT LOAD. IA HAVE TO FIND A WAY TO EXTEND
                                        // THE TIME SPAN OF SOCKETTIMEOUT E.
                                        // DELETE LATEST IMPLEMENTATION: GETTING DATA OF PARSE FILE.
                                        imageUrl = bfFrontFile.getUrl();//live url
                                        glideImagePlacement.squaredImagePlacementFromString(imageUrl
                                                , cBfFrontFetched, R.drawable.servicetypeiconblue);
                                        picFetchedCheck1 = true;
                                    }
                                }
                                if (weAreInAfter2) {
                                    final ParseFile afFrontFile = object.getParseFile("afterFront");
                                    if (afFrontFile != null) {
                                        if (!picAFFetchedCheck1) {
                                            afImageUrl = afFrontFile.getUrl();//live url
                                            glideImagePlacement.squaredImagePlacementFromString
                                                    (afImageUrl, cAfFinalFrontFetched
                                                            , R.drawable.servicetypeiconblue);
                                            picAFFetchedCheck1 = true;
                                        }
                                    }
                                }
                                ////////////////////////////////////////////////////////////////////////

                                //REAR SIDE--BEFORE AND AFTER///////////////////////////////////////////
                                final ParseFile bfRearFile = object.getParseFile("beforeRear");
                                if (bfRearFile != null) {
                                    if (!picFetchedCheck2) {
                                        imageUrl2 = bfRearFile.getUrl();//live url
                                        glideImagePlacement.squaredImagePlacementFromString(imageUrl2
                                                , cBfRearFetched, R.drawable.servicetypeiconblue);
                                        picFetchedCheck2 = true;
                                    }
                                }
                                if (weAreInAfter2) {
                                    final ParseFile afRearFile = object.getParseFile("afterRear");
                                    if (afRearFile != null) {
                                        if (!picAFFetchedCheck2) {
                                            afImageUrl2 = afRearFile.getUrl();//live url
                                            glideImagePlacement.squaredImagePlacementFromString(afImageUrl2
                                                    , cAfFinalRearFetched, R.drawable.servicetypeiconblue);
                                            picAFFetchedCheck2 = true;
                                        }
                                    }
                                }
                                ////////////////////////////////////////////////////////////////////////

                                //LEFT SIDE--BEFORE AND AFTER///////////////////////////////////////////
                                final ParseFile bfLeftFile = object.getParseFile("beforeLeft");
                                if (bfLeftFile != null) {
                                    if (!picFetchedCheck3) {
                                        imageUrl3 = bfLeftFile.getUrl();//live url
                                        glideImagePlacement.squaredImagePlacementFromString(imageUrl3
                                                , cBfLeftFetched, R.drawable.servicetypeiconblue);
                                        picFetchedCheck3 = true;
                                    }
                                }
                                if (weAreInAfter2) {
                                    final ParseFile afLeftFile = object.getParseFile("afterLeft");
                                    if (afLeftFile != null) {
                                        if (!picAFFetchedCheck3) {
                                            afImageUrl3 = afLeftFile.getUrl();//live url
                                            glideImagePlacement.squaredImagePlacementFromString(afImageUrl3
                                                    , cAfFinalLeftFetched, R.drawable.servicetypeiconblue);
                                            picAFFetchedCheck3 = true;
                                        }
                                    }
                                }
                                ////////////////////////////////////////////////////////////////////////

                                //RIGHT SIDE--BEFORE AND AFTER//////////////////////////////////////////
                                final ParseFile bfRightFile = object.getParseFile("beforeRight");
                                if (bfRightFile != null) {
                                    if (!picFetchedCheck4) {
                                        imageUrl4 = bfRightFile.getUrl();//live url
                                        glideImagePlacement.squaredImagePlacementFromString(imageUrl4
                                                , cBfRightFetched, R.drawable.servicetypeiconblue);
                                        picFetchedCheck4 = true;
                                    }
                                }
                                if (weAreInAfter2) {
                                    final ParseFile afRightFile = object.getParseFile("afterRight");
                                    if (afRightFile != null) {
                                        if (!picAFFetchedCheck4) {
                                            afImageUrl4 = afRightFile.getUrl();//live url
                                            glideImagePlacement.squaredImagePlacementFromString(afImageUrl4
                                                    , cAfFinalRightFetched, R.drawable.servicetypeiconblue);
                                            picAFFetchedCheck4 = true;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        /*
                         * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                         */
                        clm.connectionLostDialog();
                    }
                }
            });

            if (picFetchedCheck1 && picFetchedCheck2 && picFetchedCheck3 && picFetchedCheck4) {

                if (!alreadyExecuted) {
                    cLoadingBeforePicsRelative.setVisibility(View.GONE);

                    cWashingStatusTitle.startAnimation(FadeInOut);
                    cWashingStatusSubtitle.setVisibility(View.VISIBLE);
                    cBarAnimationWashingCar.setVisibility(View.VISIBLE);

                    weAreInAfter2 = true;
                    alreadyExecuted = true;
                }
            }

            if (picAFFetchedCheck1 && picAFFetchedCheck2 && picAFFetchedCheck3 && picAFFetchedCheck4) {

                if (weAreInAfter2) {

                    if (!alreadyExecuted2) {
                        cWashingStatusTitle.clearAnimation();
                        cDoneWashingStatusTitle.setVisibility(View.VISIBLE);
                        cGoToCheckout.setVisibility(View.VISIBLE);

                        cWashingInboundPicsRelative.setVisibility(View.GONE);
                        cWashingStatusTitle.setVisibility(View.GONE);
                        cWashingStatusSubtitle.setVisibility(View.INVISIBLE);
                        cBarAnimationWashingCar.setVisibility(View.GONE);

                        cBfFrontFetched.setImageDrawable(null);
                        cBfRearFetched.setImageDrawable(null);
                        cBfLeftFetched.setImageDrawable(null);
                        cBfRightFetched.setImageDrawable(null);

                        cUpdatedSeekBarTwo.setProgress(3);

                        //TODO: Car washed notification here:
                        //Notification here: Your Car has been washed//
                        String requestTakenTitle3 = getResources().getString(R.string.carBeingWashed_act_java_status);
                        String requestTakenContent3 = getResources().getString(R.string.carBeingWashed_act_java_youCarHasBeenWashed);
                        int Id3 = 23426;
                        if (writeReadDataInFile.readFromFile("notificationStatus") != null) {
                            if (writeReadDataInFile.readFromFile("notificationStatus").equals("1")) {
                                newNotificationsFunction(requestTakenTitle3, requestTakenContent3, Id3);
                            } else if (writeReadDataInFile.readFromFile("notificationStatus").equals("")) {
                                //If the code lands here, it means the settings were untouched, hence true
                                newNotificationsFunction(requestTakenTitle3, requestTakenContent3, Id3);
                            }
                        } else {
                            //if its null, it means the switch was never touched, hence it's ON
                            newNotificationsFunction(requestTakenTitle3, requestTakenContent3, Id3);
                        }
                        //-------------------------------------------//

                        //There are no Bundle objects being used. So use bundles for turning off blackish
                        //screen, which you'll turn on on this activity onCreate, with its loading text in white,
                        //will be turned off when it reaches this point in code.(SIMPLY USE BUNDLE FOR THIS FEATURE)

                        cfetchedBfPicsHorizontalScroll.setVisibility(View.GONE);
                        cScrollViewGrandPa.setVisibility(View.VISIBLE);
                        mTipMoneySymbol.setVisibility(View.VISIBLE);
                        //before and after whatever animation HERE!!!

                        glideImagePlacement.squaredImagePlacementFromString(imageUrl
                                , cBfFinalFrontFetched, R.drawable.servicetypeiconblue);

                        glideImagePlacement.squaredImagePlacementFromString(imageUrl2
                                , cBfFinalRearFetched, R.drawable.servicetypeiconblue);

                        glideImagePlacement.squaredImagePlacementFromString(imageUrl3
                                , cBfFinalLeftFetched, R.drawable.servicetypeiconblue);

                        glideImagePlacement.squaredImagePlacementFromString(imageUrl4
                                , cBfFinalRightFetched, R.drawable.servicetypeiconblue);
                        picFetchCheckHandler.removeCallbacks(picturesFetchedChecker);

                        alreadyExecuted2 = true;
                        if (alreadyExecuted3) {
                            mGettingAllPicsWait.setVisibility(View.GONE);
                            boxedLoadingDialog.hideLoadingDialog();
                            picFetchCheckHandler.removeCallbacks(picturesFetchedChecker);
                        }
                    }
                }
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    public void toDetail(View view){

        switch(view.getId()){

            case R.id.comparison1:
                Intent i = new Intent(WashServiceShowActivity. this, DetailedImageComparisonActivity. class);
                i.putExtra("comparisonBefore", imageUrl);
                i.putExtra("comparisonAfter", afImageUrl);
                startActivity(i);
                break;
            case R.id.comparison2:
                Intent i2 = new Intent(WashServiceShowActivity. this, DetailedImageComparisonActivity. class);
                i2.putExtra("comparisonBefore", imageUrl2);
                i2.putExtra("comparisonAfter", afImageUrl2);
                startActivity(i2);
                break;
            case R.id.comparison3:
                Intent i3 = new Intent(WashServiceShowActivity. this, DetailedImageComparisonActivity. class);
                i3.putExtra("comparisonBefore", imageUrl3);
                i3.putExtra("comparisonAfter", afImageUrl3);
                startActivity(i3);
                break;
            case R.id.comparison4:
                Intent i4 = new Intent(WashServiceShowActivity. this, DetailedImageComparisonActivity. class);
                i4.putExtra("comparisonBefore", imageUrl4);
                i4.putExtra("comparisonAfter", afImageUrl4);
                startActivity(i4);
                break;

        }

    }

    public void goToCheckout(View view){

        if(cRateThisSplasherAction.getRating() != 0) {
            boxedLoadingDialog.showLoadingDialog();
            final int intRatingToSet = (int)(cRateThisSplasherAction.getRating());

            if(cTipThisSplasher.toString().isEmpty()){

                cTipThisSplasher.setText("0.0");

            }

            final String tipToSend = cTipThisSplasher.getText().toString();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");

            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername()); //Specifying the sole row that contains the carOwner using this phone

            query.whereExists("splasherUsername");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (final ParseObject object : objects) {
                                //The sole request

                                aboutToBeRatedSplasher = object.getString("splasherUsername");

                                Log.i("00ToBeRatedSplash", aboutToBeRatedSplasher + " is in da house"); //test with this

                                //Add tip:
                                object.put("tip", tipToSend);

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            ParseQuery<ParseObject> ratingsQuery = ParseQuery.getQuery("Profile");
                                            ratingsQuery.whereEqualTo("username", aboutToBeRatedSplasher);
                                            Log.i("11ToBeRatedSplash", aboutToBeRatedSplasher + " is in da house"); //00 makes it through. 11 never does!
                                            ratingsQuery.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {
                                                    //There should be 1, and only 1 object from this Parse class.
                                                    if(e == null) {
                                                        if(objects.size() > 0) {
                                                            for (ParseObject userObject : objects) {//Should be a single row on DB
                                                                //Here i have to craft the avarage... (First relocate the checkout button)
                                                                //////////////////RATING LOGIC////////////////////
                                                                /////////////////Rating Avg vars//////////////////
                                                                //Num of stars to set(is up) final int intRatingToSet = (int)(cRateThisSplasherAction.getRating());//<<<1
                                                                //Rating Avg:
                                                                String currentRatingAvg = userObject.getString("oldAvgRating");
                                                                //Log.i("11ratingbeforenew", currentRatingAvg);
                                                                int intCurrentRatingAvg = Integer.parseInt(currentRatingAvg);//<<<2
                                                                //Num of Washes:
                                                                String currentNOW = userObject.getString("washes");
                                                                //Log.i("11currentNOW", currentNOW);
                                                                int intCurrentNOW = Integer.parseInt(currentNOW); //<<<3
                                                                //Num of Washes canceled:
                                                                String currentCanceledWashes = userObject.getString("washesCanceled");
                                                                //Log.i("11currentCanceledWashes", currentCanceledWashes);
                                                                int intCurrentCanceledWashes = Integer.parseInt(currentCanceledWashes);//<<<4
                                                                //////////////////////////////////////////////////

                                                                /////////////////Rating Avg Math//////////////////
                                                                int newWash = 1;

                                                                int newNOW = intCurrentNOW + newWash; //New Total Num of Washes to be sent back to this splasher
                                                                final String stringedNewNOW = String.valueOf(newNOW);

                                                                //(oldAvgRating * newNOW + intRatingToSet) / newNOW
                                                                //newAvg is the quality. need to process it in parallel to upload newst Avg to server
                                                                int newAvg = (intCurrentRatingAvg * newNOW + intRatingToSet) / newNOW;
                                                                final String stringedNewAvg = String.valueOf(newAvg);

                                                                //Badge Calculator
                                                                RatingLogic ratingLogic = new RatingLogic(WashServiceShowActivity.this);
                                                                numericalBadge = ratingLogic.splasherBadgeCalculator(newNOW, intCurrentCanceledWashes, intCurrentRatingAvg, intRatingToSet);
                                                                String stringedNumBadge = String.valueOf(numericalBadge);
                                                                //////////////////////////////////////////////////
                                                                //////////////////////////////////////////////////
                                                                userObject.put("oldAvgRating", stringedNewAvg);
                                                                userObject.put("washes", stringedNewNOW);
                                                                userObject.put("numericalBadge", stringedNumBadge);
                                                                userObject.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e == null) {
                                                                            Log.i("11avg rating: ", stringedNewAvg);
                                                                            Log.i("11new num of washes: ", stringedNewNOW);
                                                                            toastMessages.debugMesssage(getApplicationContext()
                                                                            ,"Rating sent: "
                                                                                            + stringedNewAvg + " " +
                                                                                            stringedNewNOW,1);
                                                                            Intent intent = new Intent(WashServiceShowActivity.this, PaymentBillActivity.class);
                                                                            intent.putExtra("splasherRating", String.valueOf(intRatingToSet));
                                                                            startActivity(intent);
                                                                        } else {
                                                                            toastMessages.productionMessage(getApplicationContext()
                                                                            ,getResources().getString(R.string.carBeingWashed_act_java_slowConnectionPlease)
                                                                            ,1);
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }else{
                                                    clm.connectionLostDialog();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }else{
                        /*
                         * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                         */
                        clm.connectionLostDialog();
                    }
                }
            });

        } else {

            //Alert Dialog: The Car Owner must rate the Splasher with at least 1 star
            toastMessages.productionMessage(getApplicationContext(),
                    getResources().getString(R.string.carBeingWashed_act_java_pleaseRateSplasher),1);
        }
    }
    private void createImageGallery(){
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //path = Environment.getExternalStorageDirectory() + "/" + "Splash/";
        String GALLERY_LOCATION = "Splash";
        File mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if(!mGalleryFolder.exists()){
            mGalleryFolder.mkdirs();
        }
        Log.i("AND", mGalleryFolder.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            picFetchCheckHandler.removeCallbacks(picturesFetchedChecker);
            moveTaskToBack(true);
        }
        /*
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //Do nothing for now
        }
        */
        return false;
    }

    public void newNotificationsFunction(String title, String content, int id){
        if(background) {
            carWashedNoti.newLocalNotification(WashServiceShowActivity.class, title, content, id);
            background = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.service_show_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_report){
            startActivity(new Intent(WashServiceShowActivity.this
                    , ContactUsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
