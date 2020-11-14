package com.nomadapp.splash.ui.activity.standard;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.ui.fragment.SplasherOnboarding2;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.ForcedAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class SplasherOnboarding extends AppCompatActivity implements
        SplasherOnboarding2.OnFragmentInteractionListener {

    private TextView mSplasher_onboarding_name, mSplasher_onboarding_last,
            mSplasher_onboarding_phone;

    private RelativeLayout mSplasher_onboarding_2_frag_container,
            mSplasher_onboarding_scroller_relative;

    private Button mSplasher_onboarding_button;

    private BoxedLoadingDialog boxedLoadingDialog;

    private SplasherOnboarding2 so2 = new SplasherOnboarding2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_onboarding);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        createSplasherAccount();
    }

    private void createSplasherAccount(){
        boxedLoadingDialog = new BoxedLoadingDialog(SplasherOnboarding.this);
        mSplasher_onboarding_scroller_relative = findViewById(R.id
                .splasher_onboarding_scroller_relative);
        mSplasher_onboarding_button = findViewById(R.id.splasher_onboarding_button);
        mSplasher_onboarding_name = findViewById(R.id.splasher_onboarding_name);
        mSplasher_onboarding_last = findViewById(R.id.splasher_onboarding_last);
        mSplasher_onboarding_phone = findViewById(R.id.splasher_onboarding_phone);
        mSplasher_onboarding_2_frag_container = findViewById
                (R.id.splasher_onboarding_2_frag_container);

        Button mSplasher_onboarding_button = findViewById(R.id.splasher_onboarding_button);

        mSplasher_onboarding_name.requestFocus();

        mSplasher_onboarding_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mSplasher_onboarding_name.getText().toString().isEmpty() ||
                    mSplasher_onboarding_last.getText().toString().isEmpty() ||
                    mSplasher_onboarding_phone.getText().toString().isEmpty()) {
                    completeFormDialog();
                }else{
                    boxedLoadingDialog.showLoadingDialog();
                    applicationCreation();
                }
            }
        });
    }

    private void applicationCreation(){
        //Create the ProvApplicant class in parse, submit these details and move one to the
        //next Splasher SignUp step
        final ParseObject provApplicants = new ParseObject("ProvApplicants");
        provApplicants.put("name", mSplasher_onboarding_name.getText().toString());
        provApplicants.put("lastname", mSplasher_onboarding_last.getText().toString());
        provApplicants.put("phone", mSplasher_onboarding_phone.getText().toString());
        provApplicants.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    boxedLoadingDialog.hideLoadingDialog();
                    vehicleTimeFragmentInit();
                }else{
                    boxedLoadingDialog.hideLoadingDialog();
                    ToastMessages toastMessages = new ToastMessages();
                    toastMessages.productionMessage(SplasherOnboarding.this
                            , "Something went wrong. Check your internet" +
                                    " connection and try again.", 1);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if (mSplasher_onboarding_2_frag_container.getVisibility() == View.VISIBLE){
                finish();
            }else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void vehicleTimeFragmentInit(){
        UIHandler();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.splasher_onboarding_2_frag_container, so2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        dataMover();
    }

    private void dataMover(){
        Bundle bundle = new Bundle();
        bundle.putString("name", mSplasher_onboarding_name.getText().toString());
        bundle.putString("last", mSplasher_onboarding_last.getText().toString());
        bundle.putString("phone", mSplasher_onboarding_phone.getText().toString());
        so2.setArguments(bundle);
    }

    private void UIHandler(){
        mSplasher_onboarding_scroller_relative.setVisibility(View.GONE);
        mSplasher_onboarding_button.setVisibility(View.GONE);
        mSplasher_onboarding_2_frag_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void completeFormDialog(){
        ForcedAlertDialog fad = new ForcedAlertDialog(SplasherOnboarding.this);
        fad.generalPurposeForcedDialogNoAction(getResources().getString(R.string
                .becomeSplasher_act_java_missingFields),getResources().getString(R.string
                .splasher_onboarding_pleaseFillAllBeforeNext), getResources().getString(R.string
                .becomeSplasher_act_java_ok));
    }
}
