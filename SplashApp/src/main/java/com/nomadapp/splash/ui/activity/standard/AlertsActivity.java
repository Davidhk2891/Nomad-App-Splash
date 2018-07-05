package com.nomadapp.splash.ui.activity.standard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.nomadapp.splash.R;

import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;

public class AlertsActivity extends AppCompatActivity {

    //Notifications Switch
    private Switch cSwitchAlerts;
    private String switchOnOffCode;
    private WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(AlertsActivity.this);
    private ToastMessages toastMessages = new ToastMessages();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        //--------------------------------

        cSwitchAlerts = findViewById(R.id.switchAlerts);

        toastMessages.debugMesssage(getApplicationContext()
                ,String.valueOf(writeReadDataInFile.readFromFile("notificationStatus"))
                ,1);
        if(writeReadDataInFile.readFromFile("notificationStatus") != null) {
            if (writeReadDataInFile.readFromFile("notificationStatus").equals("0")) {
                cSwitchAlerts.setChecked(false);
                toastMessages.debugMesssage(getApplicationContext()
                ,"switch is off",1);
            }else{
                cSwitchAlerts.setChecked(true);
                toastMessages.debugMesssage(getApplicationContext()
                        ,"switch is on",1);
            }
        }else{
            cSwitchAlerts.setChecked(true);
            toastMessages.debugMesssage(getApplicationContext()
                    ,"switch is on but untouched",1);
        }
        switchOnOffFunc();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void switchOnOffFunc(){
        cSwitchAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    switchOnOffCode = "1";
                    toastMessages.debugMesssage(getApplicationContext(),"1",1);
                }else{
                    switchOnOffCode = "0";
                    toastMessages.debugMesssage(getApplicationContext(),"0",1);
                }
                writeReadDataInFile.writeToFile(switchOnOffCode, "notificationStatus");
                toastMessages.debugMesssage(getApplicationContext()
                ,"switchCode: " + switchOnOffCode + " saved to file.",1);
            }
        });
    }
}
