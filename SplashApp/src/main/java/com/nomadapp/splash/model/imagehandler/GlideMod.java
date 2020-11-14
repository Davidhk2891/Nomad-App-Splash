package com.nomadapp.splash.model.imagehandler;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Created by David on 9/30/2017.
 */
@GlideModule
public class GlideMod extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {

//        OkHttpClient m_client = new OkHttpClient.Builder()
//                .connectTimeout(15, TimeUnit.SECONDS)
//                .writeTimeout(15, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//
//        registry.replace(GlideUrl.class, InputStream.class,
//                new OkHttpUrlLoader.Factory(m_client));

    }
}
