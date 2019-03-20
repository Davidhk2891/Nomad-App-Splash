package com.nomadapp.splash.ui.activity.splasherside;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.nomadapp.splash.utils.sysmsgs.connectionlost.ConnectionLost;
import com.nomadapp.splash.model.imagehandler.ImageRotation;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;

import static com.nomadapp.splash.R.id.pictureHolder;
import static com.nomadapp.splash.R.id.pictureHolder2;
import static com.nomadapp.splash.R.id.pictureHolder3;
import static com.nomadapp.splash.R.id.pictureHolder4;
import static com.nomadapp.splash.R.id.picturesSentStatus;

public class SplasherCameraActivity extends AppCompatActivity {

    private RelativeLayout cWashingCarRelative;
    private Button cDispatchPictures, cDispatchPictures2;
    private TextView cBeforeAfterTitle;
    private LinearLayout mPicturesSentStatus;
    //--//
    private Uri imageFileLocation;
    private Uri imageFileLocation2;
    private Uri imageFileLocation3;
    private Uri imageFileLocation4;
    //--//

    private File mGalleryFolder;
    Intent i, ii, iii, iv;
    private ImageView cPictureHolder1, cPictureHolder2, cPictureHolder3, cPictureHolder4;
    File Image1;
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 1;
    private boolean check1 = false;
    private boolean check2 = false;
    private boolean check3 = false;
    private boolean check4 = false;

    private Bitmap finalBitmap1, finalBitmap2, finalBitmap3, finalBitmap4;

    private TextView cPicSendingAnim1, cPicSendingAnim2, cPicSendingAnim3, cPicSendingAnim4;
    private ProgressBar cAnimationSending;
    private ImageView cPicSent1, cPicSent2, cPicSent3, cPicSent4;
    private Handler handler = new Handler();
    private boolean weAreInAfter = false;
    private boolean afterIsGood2Go = false;
    private String rawImageString1, rawImageString2, rawImageString3, rawImageString4;
    private ParseFile afFrontFile, afRearFile, afLeftFile, afRightFile;
    private ParseFile bfFrontFile, bfRearFile, bfLeftFile, bfRightFile;

    private ToastMessages toastMessages = new ToastMessages();
    private ConnectionLost clm = new ConnectionLost(SplasherCameraActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_camera);

        cWashingCarRelative = findViewById(R.id.washingCarRelative);
        cDispatchPictures = findViewById(R.id.dispatchPictures);
        cDispatchPictures2 = findViewById(R.id.dispatchPictures2);
        cBeforeAfterTitle = findViewById(R.id.beforeAfterTitle);
        cPictureHolder1 = findViewById(pictureHolder);
        cPictureHolder2 = findViewById(pictureHolder2);
        cPictureHolder3 = findViewById(pictureHolder3);
        cPictureHolder4 = findViewById(pictureHolder4);
        mPicturesSentStatus = findViewById(picturesSentStatus);
        cWashingCarRelative.setVisibility(View.GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(getSupportActionBar() != null)
        getSupportActionBar().hide();
        createImageGallery();

        Bundle newBundle = getIntent().getExtras();
        if(newBundle != null){
            String fetchedUntilTime2 = newBundle.getString("fetchedUntilTime");
            String fetchedPrice2 = newBundle.getString("fetchedPrice");
            String picStatus = newBundle.getString("picStatus");
            WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(SplasherCameraActivity.this);
            writeReadDataInFile.writeToFile(fetchedUntilTime2, "untilTime12Hours");
            writeReadDataInFile.writeToFile(fetchedPrice2, "fetchedPrice12Hours");
            if (picStatus != null) {
                Log.i("purple1", "ran");
                if (picStatus.equals("after")) {
                    Log.i("purple1.1", "ran");
                    cWashingCarRelative.setVisibility(View.VISIBLE);
                    mPicturesSentStatus.setVisibility(View.GONE);
                }else{
                    Log.i("purple1.2", "ran");
                }
            }else{
                Log.i("purple2", "ran");
            }
        }

        //Receiving images1-------------------------------------------------//
        //TextView cSendingPicsText = findViewById(R.id.sendingPicsText);

        cPicSendingAnim1 = findViewById(R.id.picSendingAnim1);
        cPicSendingAnim2 = findViewById(R.id.picSendingAnim2);
        cPicSendingAnim3 = findViewById(R.id.picSendingAnim3);
        cPicSendingAnim4 = findViewById(R.id.picSendingAnim4);

        cPicSent1 = findViewById(R.id.picSent1);
        cPicSent2 = findViewById(R.id.picSent2);
        cPicSent3 = findViewById(R.id.picSent3);
        cPicSent4 = findViewById(R.id.picSent4);

        cAnimationSending = findViewById(R.id.animationSending);
        //Receiving images1-------------------------------------------------//

        cDispatchPictures.setEnabled(false);
        cDispatchPictures2.setEnabled(false);

        clm.connectivityStatus(SplasherCameraActivity.this);
    }

    public void takePic(View v){

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            try {
                switch (v.getId()) {
                    case pictureHolder:
                        String front = "FRONT_";
                        File photo1 = createImageFile(front);
                        imageFileLocation = contentCamFile(photo1);
                        rawImageString1 = photo1.getAbsolutePath();
                        Log.i("photo1 path", rawImageString1);
                        i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        toastMessages.productionMessage(getApplicationContext()
                        ,getResources().getString(R.string.cameraIntent_act_java_takePicFront)
                        ,1);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileLocation);
                        startActivityForResult(i, 1);
                        break;
                    case pictureHolder2:
                        String back = "BACK_";
                        File photo2 = createImageFile(back);
                        imageFileLocation2 = contentCamFile(photo2);
                        rawImageString2 = photo2.getAbsolutePath();
                        Log.i("photo2 path", rawImageString2);
                        ii = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        toastMessages.productionMessage(getApplicationContext()
                                ,getResources().getString(R.string.cameraIntent_act_java_takePicRear)
                                ,1);
                        ii.putExtra(MediaStore.EXTRA_OUTPUT, imageFileLocation2);
                        startActivityForResult(ii, 2);
                        break;
                    case pictureHolder3:
                        String left = "LEFT_";
                        File photo3 = createImageFile(left);
                        imageFileLocation3 = contentCamFile(photo3);
                        rawImageString3 = photo3.getAbsolutePath();
                        Log.i("photo3 path", rawImageString3);
                        iii = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        toastMessages.productionMessage(getApplicationContext()
                                ,getResources().getString(R.string.cameraIntent_act_java_takePicLeft)
                                ,1);
                        iii.putExtra(MediaStore.EXTRA_OUTPUT, imageFileLocation3);
                        startActivityForResult(iii, 3);
                        break;
                    case pictureHolder4:
                        String right = "RIGHT_";
                        File photo4 = createImageFile(right);
                        imageFileLocation4 = contentCamFile(photo4);
                        rawImageString4 = photo4.getAbsolutePath();
                        Log.i("photo4 path", rawImageString4);
                        iv = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        toastMessages.productionMessage(getApplicationContext()
                                ,getResources().getString(R.string.cameraIntent_act_java_takePicRight)
                                ,1);
                        iv.putExtra(MediaStore.EXTRA_OUTPUT, imageFileLocation4);
                        startActivityForResult(iv, 4);
                        break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    toastMessages.productionMessage(getApplicationContext()
                            ,getResources().getString(R.string.cameraIntent_act_java_externalStoragePermissionReq)
                            ,1);
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_RESULT);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                toastMessages.productionMessage(getApplicationContext()
                        ,getResources().getString(R.string.cameraIntent_act_java_permissionGranted)
                        ,1);
            } else {
                toastMessages.productionMessage(getApplicationContext()
                        ,getResources().getString(R.string.cameraIntent_act_java_permissionToWrite)
                        ,1);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void createImageGallery(){
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //path = Environment.getExternalStorageDirectory() + "/" + "Splash/";
        String GALLERY_LOCATION = "Splash";
        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if(!mGalleryFolder.exists()){
            //This below is a command. not a variable assignment. This is why it throws the warning.(warning disabled)
            mGalleryFolder.mkdirs();
        }
        Log.i("AND", mGalleryFolder.toString());
    }
    //----------------------------------------------------------------------------------------------

    public File createImageFile(String prefix) throws IOException{
        String timeStamp = DateFormat.getDateTimeInstance().format(new Date());
        String imageFileName = prefix + timeStamp + "_";
        //For imageFileName2:------------
        Random rand = new Random();
        int min = 1, max = 10000;
        int randomNumForFile = rand.nextInt((max - min) + 1) + min;
        String imageFileName2 = prefix + randomNumForFile + "_";
        //-------------------------------

        if(Build.VERSION.SDK_INT > 19){
            Image1 = File.createTempFile(imageFileName, ".jpg", mGalleryFolder);
            Log.i("AND3", Image1.toString());
        }else{
            Image1 = File.createTempFile(imageFileName2, ".jpg", mGalleryFolder);
            Log.i("AND2", Image1.toString());
        }
        return Image1;
    }

    public Uri contentCamFile(File Image1){
        String authorities = getApplicationContext().getPackageName() + ".fileProvider";
        return FileProvider.getUriForFile(this, authorities, Image1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GlideImagePlacement glideImagePlacement = new GlideImagePlacement(SplasherCameraActivity.this);

        switch(requestCode) {
            case 1:
                check1 = false;
                if (resultCode == RESULT_OK) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    glideImagePlacement.squaredImagePlacementFromUri(imageFileLocation
                            , cPictureHolder1, R.drawable.servicetypeiconblue);
                    check1 = true;
                }
                break;
            case 2:
                check2 = false;
                if(resultCode == RESULT_OK){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    glideImagePlacement.squaredImagePlacementFromUri(imageFileLocation2
                            , cPictureHolder2, R.drawable.servicetypeiconblue);
                    check2 = true;
                }
                break;
            case 3:
                check3 = false;
                if(resultCode == RESULT_OK){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    glideImagePlacement.squaredImagePlacementFromUri(imageFileLocation3
                            , cPictureHolder3, R.drawable.servicetypeiconblue);
                    check3 = true;
                }
                break;
            case 4:
                check4 = false;
                if(resultCode == RESULT_OK){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    glideImagePlacement.squaredImagePlacementFromUri(imageFileLocation4
                            , cPictureHolder4, R.drawable.servicetypeiconblue);
                    check4 = true;
                }
                break;
        }

        if(check1 && check2 && check3 && check4){
            toastMessages.debugMesssage(getApplicationContext(),"enabled",1);
            cDispatchPictures.setEnabled(true);
            cDispatchPictures2.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();//Check this
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(weAreInAfter && afterIsGood2Go){
            dispatchPicturesSystem();
        }
    }

    public void dispatchAfterPictures(View view){
        if(weAreInAfter){
            afterIsGood2Go = true;
            Intent intent = new Intent(SplasherCameraActivity. this, WashRequestsActivity. class);
            intent.putExtra("picKey", "key1");
            startActivity(intent);
        }
    }

    public void dispatchBeforePictures(View view){
        dispatchPicturesSystem();
    }

    public void dispatchPicturesSystem(){

        //finalBitmap1, finalBitmap2, finalBitmap3, finalBitmap4
        if(check1 && check2 && check3 && check4) {
            cWashingCarRelative.setVisibility(View.VISIBLE);
            cDispatchPictures.setVisibility(View.GONE);

            requestUpdateChecker.run();

            final ParseQuery<ParseObject> queryForPic = new ParseQuery<>("Request");
            //change this for testing
            queryForPic.whereEqualTo("splasherUsername", ParseUser.getCurrentUser().getUsername());

            queryForPic.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            //MOVE IT ALL TO JPEG 60
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            Bitmap finalBit1;
                            finalBitmap1 = BitmapFactory.decodeFile(rawImageString1);
                            if (finalBitmap1 != null){
                                try {
                                    //use raw here
                                    Uri uriImageFileLoc1 = Uri.parse(rawImageString1);
                                    Log.i("uriFileLocation", "is: " + String.valueOf(uriImageFileLoc1));
                                    finalBit1 = ImageRotation.rotateImageIfRequired(finalBitmap1,
                                            SplasherCameraActivity.this, uriImageFileLoc1);
                                    finalBit1.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                                }catch(IOException e1){
                                    e1.printStackTrace();
                                }
                            }
                            byte[] byteArray = stream.toByteArray();

                            if(weAreInAfter){//DELETE LOCAL DECLARATIONS
                                afFrontFile = new ParseFile("afterfront.jpeg", byteArray);
                            }else {
                                bfFrontFile = new ParseFile("beforefront.jpeg", byteArray);
                            }
                            for (final ParseObject object : objects) {
                                if(weAreInAfter){
                                    object.put("afterFront", afFrontFile);
                                }else {
                                    object.put("beforeFront", bfFrontFile);
                                }
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            toastMessages.productionMessage(getApplicationContext()
                                            ,getResources().getString(R.string.cameraIntent_act_java_pic1Sent)
                                            ,1);
                                            //-------------------------------------------

                                            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                                            Bitmap finalBit2;
                                            finalBitmap2 = BitmapFactory.decodeFile(rawImageString2);
                                            if (finalBitmap2 != null){
                                                try {
                                                    Uri uriImageFileLoc2 = Uri.parse(rawImageString2);
                                                    Log.i("uriFileLocation", "is: " + String.valueOf(uriImageFileLoc2));
                                                    finalBit2 = ImageRotation.rotateImageIfRequired(finalBitmap2,
                                                            SplasherCameraActivity.this, uriImageFileLoc2);
                                                    finalBit2.compress(Bitmap.CompressFormat.JPEG, 60, stream2);
                                                }catch(IOException e1){
                                                    e1.printStackTrace();
                                                }
                                            }
                                            byte[] byteArray2 = stream2.toByteArray();

                                            if(weAreInAfter){
                                                afRearFile = new ParseFile("afterrear.jpeg", byteArray2);
                                                object.put("afterRear", afRearFile);
                                            }else {
                                                bfRearFile = new ParseFile("beforerear.jpeg", byteArray2);
                                                object.put("beforeRear", bfRearFile);
                                            }
                                            object.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        toastMessages.productionMessage(getApplicationContext()
                                                                ,getResources().getString(R.string.cameraIntent_act_java_pic2Sent)
                                                                ,1);
                                                        //-------------------------------------------

                                                        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                                                        Bitmap finalBit3;
                                                        finalBitmap3 = BitmapFactory.decodeFile(rawImageString3);
                                                        if (finalBitmap3 != null){
                                                            try {
                                                                Uri uriImageFileLoc3 = Uri.parse(rawImageString3);
                                                                Log.i("uriFileLocation", "is: " + String.valueOf(uriImageFileLoc3));
                                                                finalBit3 = ImageRotation.rotateImageIfRequired(finalBitmap3,
                                                                        SplasherCameraActivity.this, uriImageFileLoc3);
                                                                finalBit3.compress(Bitmap.CompressFormat.JPEG, 60, stream3);
                                                            }catch(IOException e1){
                                                                e1.printStackTrace();
                                                            }
                                                        }
                                                        byte[] byteArray3 = stream3.toByteArray();

                                                        if(weAreInAfter){
                                                            afLeftFile = new ParseFile("afterleft.jpeg", byteArray3);
                                                            object.put("afterLeft", afLeftFile);
                                                        }else {
                                                            bfLeftFile = new ParseFile("beforeleft.jpeg", byteArray3);
                                                            object.put("beforeLeft", bfLeftFile);
                                                        }
                                                        object.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    toastMessages.productionMessage(getApplicationContext()
                                                                            ,getResources().getString(R.string.cameraIntent_act_java_pic3Sent)
                                                                            ,1);
                                                                    //-------------------------------------------

                                                                    ByteArrayOutputStream stream4 = new ByteArrayOutputStream();
                                                                    Bitmap finalBit4;
                                                                    finalBitmap4 = BitmapFactory.decodeFile(rawImageString4);
                                                                    if (finalBitmap4 != null){
                                                                        try {
                                                                            Uri uriImageFileLoc4 = Uri.parse(rawImageString4);
                                                                            Log.i("uriFileLocation", "is: " + String.valueOf(uriImageFileLoc4));
                                                                            finalBit4 = ImageRotation.rotateImageIfRequired(finalBitmap4,
                                                                                    SplasherCameraActivity.this, uriImageFileLoc4);
                                                                            finalBit4.compress(Bitmap.CompressFormat.JPEG, 60, stream4);
                                                                        }catch(IOException e1){
                                                                            e1.printStackTrace();
                                                                        }
                                                                    }
                                                                    byte[] byteArray4 = stream4.toByteArray();

                                                                    if(weAreInAfter){
                                                                        afRightFile = new ParseFile("afterRight.jpeg", byteArray4);
                                                                        object.put("afterRight", afRightFile);
                                                                        object.put("washFinished", "yes");
                                                                    }else {
                                                                        bfRightFile = new ParseFile("beforeright.jpeg", byteArray4);
                                                                        object.put("beforeRight", bfRightFile);
                                                                    }
                                                                    object.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                toastMessages.productionMessage(getApplicationContext()
                                                                                        ,getResources().getString(R.string.cameraIntent_act_java_pic4sent)
                                                                                        ,1);
                                                                            }else{
                                                                                /*
                                                                                 * Connection Lost Message<%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                                                                                 */
                                                                                clm.connectionLostDialog();
                                                                            }
                                                                            //This should stop the Runnable class--
                                                                            if(weAreInAfter) {
                                                                                handler.removeCallbacks(requestUpdateChecker);
                                                                            }
                                                                            //-------------------------------------
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

        }else{
            toastMessages.productionMessage(getApplicationContext()
                    ,getResources().getString(R.string.cameraIntent_act_java_take4Pics)
                    ,1);
        }

    }

    public void checkForRecievedPictures(String keyTitle,
                                         final TextView bar1, final ImageView mark1){
        ParseQuery<ParseObject> picQuery = ParseQuery.getQuery("Request");
                                                    //"clear" only for TESTING!
        picQuery.whereEqualTo("splasherUsername", ParseUser.getCurrentUser().getUsername());
        picQuery.whereExists(keyTitle);
        picQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    if(!weAreInAfter) {
                        bar1.setVisibility(View.INVISIBLE);
                        mark1.setVisibility(View.VISIBLE);
                        Log.i("Image status", "THERE IS SOMETHING IN BEFORE");
                    }else{
                        bar1.setVisibility(View.INVISIBLE);
                        mark1.setVisibility(View.VISIBLE);
                        Log.i("Image status", "THERE IS SOMETHING IN AFTER");
                    }
                }else if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                    if(!weAreInAfter) {
                        Log.i("Image status", "nothing here in before");
                    }else{
                        Log.i("Image status", "nothing here in after");
                    }
                }
            }
        });

    }

    public Runnable requestUpdateChecker = new Runnable() {
        @Override
        public void run() {
            if(cPicSent4.getVisibility() == View.VISIBLE){
                cAnimationSending.setVisibility(View.INVISIBLE);
            }
            if(!weAreInAfter){
                checkForRecievedPictures("beforeFront", cPicSendingAnim1, cPicSent1);
                checkForRecievedPictures("beforeRear", cPicSendingAnim2, cPicSent2);
                checkForRecievedPictures("beforeLeft", cPicSendingAnim3, cPicSent3);
                checkForRecievedPictures("beforeRight", cPicSendingAnim4, cPicSent4);
                Log.i("Where are we", " WE ARE IN BEFORE");
            }else{
                checkForRecievedPictures("afterFront", cPicSendingAnim1, cPicSent1);
                checkForRecievedPictures("afterRear", cPicSendingAnim2, cPicSent2);
                checkForRecievedPictures("afterLeft", cPicSendingAnim3, cPicSent3);
                checkForRecievedPictures("afterRight", cPicSendingAnim4, cPicSent4);
                Log.i("Where are we", " WE ARE IN AFTER");
            }
            int delay = 8000;
            handler.postDelayed(this, delay); //2 seconds
        }
    };

    public void iAmDone(View view){
        AlertDialog.Builder accessingAfterPicturesToTake = new AlertDialog.Builder(SplasherCameraActivity.this);
        accessingAfterPicturesToTake.setTitle(getResources().getString(R.string.cameraIntent_act_java_areYouDoneWashing));
        accessingAfterPicturesToTake.setIcon(android.R.drawable.ic_dialog_alert);
        accessingAfterPicturesToTake.setMessage(getResources().getString(R.string.cameraIntent_act_java_byPressingImDone));
        accessingAfterPicturesToTake.setPositiveButton(getResources().getString(R.string.cameraIntent_act_java_imDone),
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //This should stop the Runnable--
                    handler.removeCallbacks(requestUpdateChecker);
                    //-------------------------------
                    weAreInAfter = true;
                    Log.i("are we in after?", String.valueOf(true));

                    check1=false;check2=false;check3=false;check4=false;

                    cPictureHolder1.setImageDrawable(null);
                    cPictureHolder2.setImageDrawable(null);
                    cPictureHolder3.setImageDrawable(null);
                    cPictureHolder4.setImageDrawable(null);

                    cPictureHolder1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.frontcar, null));
                    cPictureHolder2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.backcar, null));
                    cPictureHolder3.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.leftcar, null));
                    cPictureHolder4.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rightcar, null));

                    cBeforeAfterTitle.setText(getResources().getString(R.string.afterWash));
                    cWashingCarRelative.setVisibility(View.GONE);
                    cDispatchPictures.setVisibility(View.GONE);
                    cDispatchPictures2.setVisibility(View.VISIBLE);

                    if(!weAreInAfter){
                        Log.i("are we in after?", "false");
                    }
                }
            });
        accessingAfterPicturesToTake.setNegativeButton(getResources().getString(R.string.cameraIntent_act_java_cancel), null);
        accessingAfterPicturesToTake.setCancelable(false);
        accessingAfterPicturesToTake.show();
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

//                    fileLocationToBitmap1 = imageFileLocation;
//                    bitmap1 = BitmapFactory.decodeFile(imageFileLocation);
//                    try {
//                        exif = new ExifInterface(imageFileLocation);
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//                    Log.d("EXIF", "Exif: " + orientation);
//                    Matrix matrix = new Matrix();
//                    if (orientation == 6) {
//                        matrix.postRotate(90);
//                    } else if (orientation == 3) {
//                        matrix.postRotate(180);
//                    } else if (orientation == 8) {
//                        matrix.postRotate(270);
//                    }
//                    finalBitmap1 = Bitmap.createBitmap(bitmap1, 0, 0,
//                            bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
//                    cPictureHolder1.setImageBitmap(finalBitmap1);
}
