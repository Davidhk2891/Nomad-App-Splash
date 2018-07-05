package com.nomadapp.splash.ui.activity.standard;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nomadapp.splash.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (getSupportActionBar() != null)
        getSupportActionBar().hide();

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                startActivity(new Intent(SplashScreenActivity.this,
                        HomeActivity.class));
            }
        }.start();
    }
}
