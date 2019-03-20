package com.nomadapp.splash.ui.activity.carownerside;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.model.objects.adapters.SliderAdapter;
import com.nomadapp.splash.ui.activity.standard.SignUpLogInActivity;

import java.util.Locale;

public class QuickTourActivity extends AppCompatActivity {

    private ViewPager mQuickTourViewP;
    private LinearLayout mQuickTourInterface;
    private TextView[] mDots;

    private Button mPreviousQT;
    private Button mNextQT;

    private int mCurrentPage;

    private SliderAdapter sliderAdapter = new SliderAdapter(QuickTourActivity.this);
    private WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile
            (QuickTourActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_tour);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        mQuickTourViewP = findViewById(R.id.quickTourViewP);
        mQuickTourInterface = findViewById(R.id.quickTourInterface);
        mPreviousQT = findViewById(R.id.previousQT);
        mNextQT = findViewById(R.id.nextQT);

        mPreviousQT.setVisibility(View.INVISIBLE);

        mQuickTourViewP.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mQuickTourViewP.addOnPageChangeListener(viewListener);

        viewPagerOrientation();

        Log.i("autoQuickTourRan",writeReadDataInFile.readFromFile("autoQuickTourRan"));

        goBackPage();
        goNextPage();
    }

    public void viewPagerOrientation(){
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "עברית":
                mQuickTourViewP.setRotationY(180);
                break;
        }
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[5];
        mQuickTourInterface.removeAllViews();
        for(int i = 0; i < mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.parseColor("#cccccc"));

            mQuickTourInterface.addView(mDots[i]);

        }
        if (mDots.length > 0){
            mDots[position].setTextColor(Color.parseColor("#ffffff"));
        }
    }

    public void saveFileToNotRunAgain(){
        writeReadDataInFile.writeToFile("ran", "autoQuickTourRan");
    }

    public void goNextPage(){
        mNextQT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mNextQT.getText().toString().equals(getResources()
                        .getString(R.string.act_quick_tour_finish))){
                    if (!writeReadDataInFile.readFromFile("autoQuickTourRan").equals("ran")){
                        Log.i("whereToGoQT", "nothing");
                        saveFileToNotRunAgain();
                        Intent intent = new Intent(QuickTourActivity.this,
                                SignUpLogInActivity.class);
                        intent.putExtra("quickTourKey","on");
                        startActivity(intent);
                    }else if (writeReadDataInFile.readFromFile("autoQuickTourRan")
                            .equals("ran")){
                        Log.i("whereToGoQT", "something");
                        finish();
                    }
                }else {
                    mQuickTourViewP.setCurrentItem(mCurrentPage + 1);
                }
            }
        });
    }

    public void goBackPage(){
        mPreviousQT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mQuickTourViewP.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;
            if (position == 0){
                mNextQT.setEnabled(true);
                mPreviousQT.setEnabled(false);
                mPreviousQT.setVisibility(View.INVISIBLE);
            }else if(position == mDots.length - 1){
                mNextQT.setEnabled(true);
                mPreviousQT.setEnabled(true);
                mPreviousQT.setVisibility(View.VISIBLE);
                mNextQT.setText(getResources().getString(R.string.act_quick_tour_finish));
            }else{
                mNextQT.setEnabled(true);
                mPreviousQT.setEnabled(true);
                mPreviousQT.setVisibility(View.VISIBLE);
                mNextQT.setText(getResources().getString(R.string.act_quick_tour_next));
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveFileToNotRunAgain();
        }
        return false;
    }

}
