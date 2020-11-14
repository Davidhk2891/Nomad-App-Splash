package com.nomadapp.splash.ui.activity.standard.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.nomadapp.splash.R;

import com.nomadapp.splash.model.constants.WebConstants;
import com.nomadapp.splash.utils.sysmsgs.ConnectionLost;

import java.util.Locale;

public class WebActivity extends AppCompatActivity {

    private String webUrlHolderString;
    private boolean doNotRun = false;
    private ConnectionLost clm = new ConnectionLost(WebActivity.this);

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        clm.connectivityStatus(WebActivity.this);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        if (!doNotRun)
        dataRecieverAlternator();

        //WebView for App's Feedback on Google forms
        WebView cFeedBackWebView = findViewById(R.id.feedBackWebView);
        final LinearLayout cFdLoadingLinear = findViewById(R.id.fdLoadingLinear);
        cFeedBackWebView.loadUrl(webUrlHolderString);
        cFeedBackWebView.getSettings().setJavaScriptEnabled(true);
        cFeedBackWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        cFdLoadingLinear.setVisibility(View.GONE);
                    }
                }.start();
            }
            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                cFdLoadingLinear.setVisibility(View.GONE);
            }

        });
    }

    public void dataRecieverAlternator(){
        //DELETE SPLASH LOGO FROM ANY PAGE THT HAS IT ON SPLASHMYCAR.COM
        //TELL ISAAC ABOUT THE PAYME TOU IN GOOGLE DRIVE
        //TEST PAYMENT_SETTINGS IN HEBREW
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String webUrlHolderKey = bundle.getString("webKey");
            if (webUrlHolderKey != null){
                switch (webUrlHolderKey) {
                    case "feedBack":
                        webUrlHolderString = WebConstants.FEEDBACK_URL
                                + WebConstants.packageName(WebActivity.this);
                        feedBackOperation(webUrlHolderString);
                        doNotRun = true;
                        break;
                    case "faq":
                        setTitle("FAQ");
                        webUrlHolderString = WebConstants.FAQ;
                        break;
                    case "termsOfUse":
                        setTitle("Terms of use");
                        webUrlHolderString = WebConstants.TERMS_OF_USE;
                        break;
                    case "privacyPolicy":
                        setTitle("Privacy Policy");
                        webUrlHolderString = WebConstants.PRIVACY_POLICY;
                        break;
                    case "paymeTermsOfUse":
                        setTitle("Payment TOU");
                        webUrlHolderString = WebConstants.PAYME_TERMS_OF_USE;
                        break;
                    case "store":
                        setTitle("Store");
                        webUrlHolderString = WebConstants.SPLASHER_STORE_LINK;
                        break;
                    case "splasherFaq":
                        setTitle("Splasher FAQ");
                        if (Locale.getDefault().getDisplayLanguage().equals("עברית")) {
                            webUrlHolderString = WebConstants.SPLASHER_FAQ_IW;
                        }else{
                            webUrlHolderString = WebConstants.SPLASHER_FAQ;
                        }
                }
            }
        }
    }

    public void setTitle(String title){
        if (getSupportActionBar() != null)
        getSupportActionBar().setTitle(title);
    }

    public void feedBackOperation(String feedBackUrl){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(feedBackUrl)));
            finish();
        } catch (android.content.ActivityNotFoundException anfe) {
            webUrlHolderString = WebConstants.FEEDBACK_URL_BACKUP + WebConstants
                    .packageName(WebActivity.this);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrlHolderString)));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
