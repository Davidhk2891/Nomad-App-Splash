package com.nomadapp.splash.model.imagehandler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.nomadapp.splash.R;

/**
 * Created by David on 5/24/2018 for Splash.
 */
public class GlideImagePlacement {

    private Context context;

    public GlideImagePlacement(Context ctx){
        this.context = ctx;
    }

    /**
     * Glide template for circular pictures. Most likely profile pictures.
     * @param string used to get the picture path in String format(It can also be Uri or Drawable)
     * @param imageView used hold the picture path and display it (usually an ImageView)
     */
    public void roundImagePlacementFromString(String string, ImageView imageView){
        Drawable defaultBg = context.getResources().getDrawable(R.drawable.theemptyface);
        GlideApp
                .with(context.getApplicationContext())
                .load(string)
                .placeholder(R.drawable.custom_progress_bar)
                .error(defaultBg)
                .apply(RequestOptions.circleCropTransform())
                .timeout(20000)
                .into(imageView);
    }
    public void roundImagePlacementFromUri(Uri uri, ImageView imageView){
        Drawable defaultBg = context.getResources().getDrawable(R.drawable.theemptyface);
        GlideApp
                .with(context.getApplicationContext())
                .load(uri)
                .placeholder(R.drawable.custom_progress_bar)
                .error(defaultBg)
                .apply(RequestOptions.circleCropTransform())
                .timeout(20000)
                .into(imageView);
    }
    public void roundImagePlacementFromDrawable(int drawables, ImageView imageView){
        Drawable defaultBg = context.getResources().getDrawable(drawables);
        GlideApp
                .with(context.getApplicationContext())
                .load(drawables)
                .placeholder(R.drawable.custom_progress_bar)
                .error(defaultBg)
                .apply(RequestOptions.circleCropTransform())
                .timeout(20000)
                .into(imageView);
    }
    //********************************************************************************************//
    public void squaredImagePlacementFromString(String string, ImageView imageView, int drawables){
        Drawable defaultBg = context.getResources().getDrawable(drawables);
        GlideApp
                .with(context.getApplicationContext())
                .load(string)
                .placeholder(R.drawable.custom_progress_bar)
                .error(defaultBg)
                .timeout(20000)
                .into(imageView);
    }
    public void squaredImagePlacementFromUri(Uri uri, ImageView imageView, int drawables){
        Drawable defaultBg = context.getResources().getDrawable(drawables);
        GlideApp
                .with(context.getApplicationContext())
                .load(uri)
                .placeholder(R.drawable.custom_progress_bar)
                .error(defaultBg)
                .timeout(20000)
                .into(imageView);
    }
    public void squaredImagePlacementFromDrawable(int drawables, ImageView imageView){
        Drawable defaultBg = context.getResources().getDrawable(drawables);
        GlideApp
                .with(context.getApplicationContext())
                .load(drawables)
                .placeholder(R.drawable.custom_progress_bar)
                .error(defaultBg)
                .timeout(20000)
                .into(imageView);
    }
}
