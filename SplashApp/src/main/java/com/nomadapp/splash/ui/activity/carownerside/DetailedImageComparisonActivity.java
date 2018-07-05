package com.nomadapp.splash.ui.activity.carownerside;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.nomadapp.splash.R;

import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;

public class DetailedImageComparisonActivity extends AppCompatActivity {

    private ImageView cBeforeWashFull, cAfterWashFull;
    private Switch cSwitchBeforeAfter;
    private GlideImagePlacement glideImagePlacement = new GlideImagePlacement(DetailedImageComparisonActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_image_comparison);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        String pathBefore = "";
        String pathAfter = "";

        cBeforeWashFull = findViewById(R.id.beforeWashFull);
        cAfterWashFull = findViewById(R.id.afterWashFull);
        cSwitchBeforeAfter = findViewById(R.id.switchBeforeAfter);

        Bundle bundle = getIntent().getExtras();

        String stringBefore = null;
        String stringAfter = null;
        if (bundle != null) {
            stringBefore = bundle.getString("comparisonBefore");
            stringAfter = bundle.getString("comparisonAfter");
        }

        Log.i("var content before", pathBefore);
        Log.i("var content after", pathAfter);

        loadBeforeAfterPcitures(stringBefore, stringAfter);

        cAfterWashFull.setAlpha(0f);
        switchCheckForBeforeAfter();

    }

    public void loadBeforeAfterPcitures(String stringBefore, String stringAfter){
        glideImagePlacement.squaredImagePlacementFromString(stringBefore
                , cBeforeWashFull, R.drawable.servicetypeiconblue);
        glideImagePlacement.squaredImagePlacementFromString(stringAfter
                , cAfterWashFull, R.drawable.servicetypeiconblue);
    }

    public void switchCheckForBeforeAfter(){

        cSwitchBeforeAfter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    //switch is on(after):
                    cBeforeWashFull.animate().alpha(0f).setDuration(800);
                    cAfterWashFull.animate().alpha(1f).setDuration(800);
                }else{
                    //switch is off(before):
                    cBeforeWashFull.animate().alpha(1f).setDuration(800);
                    cAfterWashFull.animate().alpha(0f).setDuration(800);
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
