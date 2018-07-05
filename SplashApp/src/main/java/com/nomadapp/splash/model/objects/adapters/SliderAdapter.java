package com.nomadapp.splash.model.objects.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nomadapp.splash.R;

import java.util.Locale;

/**
 * Created by David on 6/9/2018 for Splash.
 */
public class SliderAdapter extends PagerAdapter{

    private Context context;

    public SliderAdapter(Context ctx){
        this.context = ctx;
    }

    private int[] quickTourImages = {

            R.drawable.quicktourone,
            R.drawable.quicktourtwo,
            R.drawable.quicktourthree,
            R.drawable.quicktourfour,
            R.drawable.quicktourfive

    };

    @Override
    public int getCount() {
        return quickTourImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.quicktour_slide_layout, container
                , false);
        ImageView mSlide_image = view.findViewById(R.id.slide_image);
        mSlide_image.setImageResource(quickTourImages[position]);
        mSlide_image.setScaleType(ImageView.ScaleType.FIT_XY);
        if (Locale.getDefault().getDisplayLanguage().equals("עברית")){
            mSlide_image.setRotationY(180);
        }
        mSlide_image.setAdjustViewBounds(true);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
