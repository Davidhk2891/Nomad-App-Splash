package com.nomadapp.splash.ui.activity.standard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.activity.standard.web.WebActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        RelativeLayout cContactUsRelativeFirst = findViewById(R.id.contactUsRelativeFirst);
        RelativeLayout cPrivacyPolicyRelativeFirst = findViewById(R.id.privacyPolicyRelativeFirst);
        RelativeLayout cTermsRelativeFirst = findViewById(R.id.termsRelativeFirst);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        cContactUsRelativeFirst.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, ContactUsActivity.class));
            }
        });
        cPrivacyPolicyRelativeFirst.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent ppIntent = new Intent(AboutActivity.this,WebActivity.class);
                ppIntent.putExtra("webKey","privacyPolicy");
                startActivity(ppIntent);
            }
        });
        cTermsRelativeFirst.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent touIntent = new Intent(AboutActivity.this,WebActivity.class);
                touIntent.putExtra("webKey","termsOfUse");
                startActivity(touIntent);
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
