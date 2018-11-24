package com.nomadapp.splash.model.mapops;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.graphics.Bitmap.createScaledBitmap;


/**
 * Created by David on 9/1/2018 for Splash.
 */
public class MarkersOps {
    private Context context;

    public MarkersOps(Context ctx){
        this.context = ctx;
    }

    public Bitmap smallMarker(int drawable){
        int width = 120;
        int height = 153;
        BitmapDrawable bitmapdraw = (BitmapDrawable)context.getResources().getDrawable(drawable);
        Bitmap b = bitmapdraw.getBitmap();
        return createScaledBitmap(b, width, height, false);
    }

    public void customMapSmallMarkerPlacer(int drawable, String title, GoogleMap map
            , LatLng position){
        //height and width is a proper ratio for the car and bike markers
        int width = 180;
        int height = 230;
        BitmapDrawable bitmapdraw = (BitmapDrawable)context.getResources().getDrawable(drawable);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = createScaledBitmap(b, width, height, false);
        map.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
        );
    }

    public Bitmap customMapSmallMarkerAdjust(int drawable){
        int width = 180;
        int height = 230;
        BitmapDrawable bitmapdraw = (BitmapDrawable)context.getResources().getDrawable(drawable);
        Bitmap b = bitmapdraw.getBitmap();
        return createScaledBitmap(b, width, height, false);
    }
}
