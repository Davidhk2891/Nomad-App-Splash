package com.nomadapp.splash.model.imagehandler;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by David on 7/30/2018 for Splash.
 */
public class ImageFileStorage {

    private static File mGalleryFolder;

    public static void createImageGallery(){
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //path = Environment.getExternalStorageDirectory() + "/" + "Splash/";
        String GALLERY_LOCATION = "Splasher";
        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if(!mGalleryFolder.exists()){
            //noinspection ResultOfMethodCallIgnored
            mGalleryFolder.mkdirs();
        }
        Log.i("AND", mGalleryFolder.toString());
    }

    public static File createImageFile(String prefix) throws IOException {
        String timeStamp = DateFormat.getDateTimeInstance().format(new Date());
        String imageFileName = prefix + timeStamp + "_";
        //For imageFileName2:------------
        Random rand = new Random();
        int min = 1, max = 10000;
        int randomNumForFile = rand.nextInt((max - min) + 1) + min;
        String imageFileName2 = prefix + randomNumForFile + "_";
        //-------------------------------
        File savedImage;
        if(Build.VERSION.SDK_INT > 19){
            savedImage = File.createTempFile(imageFileName, ".jpg", mGalleryFolder);
            Log.i("AND3", savedImage.toString());
        }else {
            savedImage = File.createTempFile(imageFileName2, ".jpg", mGalleryFolder);
            Log.i("AND2", savedImage.toString());
        }
        return savedImage;
    }

    public static Uri contentCamFile(Context ctx, File Image1){
        String authorities = ctx.getApplicationContext().getPackageName() + ".fileProvider";
        return FileProvider.getUriForFile(ctx, authorities, Image1);
    }
}
