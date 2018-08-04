package com.nomadapp.splash.ui.activity.splasherside;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.ImageFileStorage;
import com.nomadapp.splash.model.localdatastorage.StoragePermission;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.model.server.parseserver.HistoryClassInterface;
import com.nomadapp.splash.model.server.parseserver.MessageClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.HistoryClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.model.server.parseserver.send.MessageClassSend;
import com.nomadapp.splash.utils.sysmsgs.DialogAcceptInterface;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.questiondialogs.AlertDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class SplasherWalletActivity extends AppCompatActivity {

    private Context ctx = SplasherWalletActivity.this;

    private TextView mBalanceNum;
    private TextView mSplasherSignUpBankCodeNum2,mSplasherSignUpBankBranchNum2
            ,mSplasherSignUpBankAccNum2,mBankAccProofTextview2;
    private ImageView mBankAccProofCamUpload2,mBankAccProofFileUpload2;

    private Button mUpdateFilesWallet;

    private String allBankDetails;
    private String salesWithTip;
    private String stringFinal;
    private String stringFinalShekel = "₪ 00.00";
    private double doubleSalesWithTip,doubleFinal;
    private DecimalFormat df = new DecimalFormat("#.##");

    private Uri targetUriBankString;
    private String rawImageString2;
    private Bitmap bankBitmapGal;
    private ParseFile bankFile;
    private String currentUsername,currentEmail;
    private boolean fromGallery = false;
    private boolean fromCamera = false;

    private WriteReadDataInFile writeReadDataInFile = new WriteReadDataInFile(ctx);
    private AlertDialog alertDialog = new AlertDialog(ctx);
    private BoxedLoadingDialog boxedLoadingDialog = new BoxedLoadingDialog(ctx);
    private UserClassQuery userClassQuery = new UserClassQuery(ctx);
    private ToastMessages toastMessages = new ToastMessages();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher_wallet);

        //Navigate back to parent activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //--------------------------------

        mSplasherSignUpBankCodeNum2 = findViewById(R.id.SplasherSignUpBankCodeNum2);
        mSplasherSignUpBankBranchNum2 = findViewById(R.id.SplasherSignUpBankBranchNum2);
        mSplasherSignUpBankAccNum2 = findViewById(R.id.splasherSignUpBankAccNum2);
        mBankAccProofTextview2 = findViewById(R.id.bankAccProofTextView2);
        mBalanceNum = findViewById(R.id.balanceNum);
        mUpdateFilesWallet = findViewById(R.id.updateFilesWallet);
        mBankAccProofCamUpload2 = findViewById(R.id.bankAccProofCameraUpload2);
        mBankAccProofFileUpload2 = findViewById(R.id.bankAccProofFileUpload2);

        ImageFileStorage.createImageGallery();

        queryNonDepMoney();
        fetchBankAccDets();

    }

    public void editBankDets(View v){
        alertDialog.editBankDetsDialog(new DialogAcceptInterface() {
            @Override
            public void onAcceptOption() {
                bankTextViewsState(true,R.drawable.btn_shape);
                mSplasherSignUpBankCodeNum2.requestFocus();
            }
        });
    }

    public void goToGallery(View v){
        StoragePermission.isStoragePermissionGranted(ctx,SplasherWalletActivity.this);
        if (StoragePermission.isStoragePermissionGranted(ctx,SplasherWalletActivity.this)){
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }

    public void goToCamera(View v) throws IOException {
        StoragePermission.isWrittingToStoragePermissionGranted(getApplicationContext()
                ,SplasherWalletActivity.this);
        if (StoragePermission.isWrittingToStoragePermissionGranted(getApplicationContext()
                ,SplasherWalletActivity.this)){
            String bankAccCamPic = "bankAcc_";
            File bankAccFile = ImageFileStorage.createImageFile(bankAccCamPic);
            targetUriBankString = ImageFileStorage.contentCamFile(ctx,bankAccFile);
            rawImageString2 = bankAccFile.getAbsolutePath();
            Intent ii = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ii.putExtra(MediaStore.EXTRA_OUTPUT, targetUriBankString);
            startActivityForResult(ii, 5);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    fromGallery = true;
                    fromCamera = false;
                    Uri targetUriID = data.getData();
                    if (targetUriID != null) {
                        String onlyFileNameID = targetUriID.toString().substring(targetUriID
                                .getPath().lastIndexOf(File.separator) + 1);
                        String onlyFileNameID2 = onlyFileNameID.substring(onlyFileNameID
                                .lastIndexOf("/") + 1);
                        try {
                            bankBitmapGal = MediaStore.Images.Media
                                    .getBitmap(this.getContentResolver(), targetUriID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("uriID", targetUriID.toString());
                        Log.i("uri name onlyID", onlyFileNameID);
                        Log.i("uri name only 2ID", onlyFileNameID2);
                        mBankAccProofTextview2.setText(onlyFileNameID2);
                    }
                }
                break;
            case 5:
                if (resultCode == RESULT_OK){
                    fromGallery = false;
                    fromCamera = true;
                    if (targetUriBankString != null) {
                        String stringedBank = targetUriBankString.toString();
                        String onlyFileNameBankCam = stringedBank.substring(stringedBank.
                                lastIndexOf(File.separator) + 1);
                        String onlyFileNameBankCam2 = onlyFileNameBankCam
                                .substring(onlyFileNameBankCam.lastIndexOf("/") + 1);
                        Log.i("uriBankCam", stringedBank);
                        Log.i("uri name onlyBankCam", onlyFileNameBankCam);
                        Log.i("uri name only 2BankCam", onlyFileNameBankCam2);
                        mBankAccProofTextview2.setText(onlyFileNameBankCam2);
                    }
                }
        }
    }

    /**
        you wanna update the bank details, provided that:
    1. Details to be updated are not the same as the ones already saved.(bank acc num)
    2. All EditTexts and the ParseFile have something to be sent(none are empty)
    3. You will save to local files all bank details and the text_name for the file
    4. you will print out the newly saved data from the local file into the EditTexts
     */
    public void updateBankDetails(View v){
        if (!writeReadDataInFile.readFromFile("sellerBankAccountNumber")
                .equals(mSplasherSignUpBankAccNum2.getText().toString())){
            if (!mSplasherSignUpBankCodeNum2.getText().toString().isEmpty() &&
                    !mSplasherSignUpBankBranchNum2.getText().toString().isEmpty() &&
                    !mSplasherSignUpBankAccNum2.getText().toString().isEmpty() &&
                    !mBankAccProofTextview2.getText().toString().isEmpty()){
                boxedLoadingDialog.showLoadingDialog();
                getUserData();
                allBankDetails = ">BANK ACC UPDATE REQUEST<" + " New bank code: "
                        + mSplasherSignUpBankCodeNum2.getText().toString()
                        + "," + " New bank branch: " + mSplasherSignUpBankBranchNum2.getText()
                        .toString()
                        + "," + " New bank Account number: " + mSplasherSignUpBankAccNum2.getText()
                        .toString();
                //Set up and send the Parse file
                byte[] bytes1;
                if (fromGallery && !fromCamera){
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bankBitmapGal.compress(Bitmap.CompressFormat.JPEG, 20, stream1);
                    bytes1 = stream1.toByteArray();//READY//
                    bankFile = new ParseFile("bankFileUpd.jpg", bytes1);
                }else if (!fromGallery && fromCamera){
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    Bitmap socialBitmap = BitmapFactory.decodeFile(rawImageString2);
                    socialBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream1);
                    bytes1 = stream1.toByteArray();//READY//
                    bankFile = new ParseFile("bankFileUpd.jpeg", bytes1);
                }
                sendUpdateToMsgClass();
            }else{
                toastMessages.productionMessage(ctx,getResources().getString(R.string
                .splasher_wallet_java_pleaseFillAll),1);
            }
        }else{
            toastMessages.productionMessage(ctx,getResources().getString(R.string
            .splasher_wallet_java_cantUpdate),1);
        }
    }

    public void getUserData(){
        if (userClassQuery.userExists()){
            currentUsername = userClassQuery.userName();
            currentEmail = userClassQuery.email();
        }
    }

    public void sendUpdateToMsgClass(){
        MessageClassSend mcs = new MessageClassSend();
        mcs.sendMessagesToServer(currentUsername, currentEmail, allBankDetails, bankFile,
                new MessageClassInterface() {
                    @Override
                    public void afterMessageSent(ParseException e) {
                        if (e == null){
                            writeReadDataInFile.writeToFile(mSplasherSignUpBankCodeNum2.getText()
                                            .toString(),"sellerBankCode");
                            writeReadDataInFile.writeToFile(mSplasherSignUpBankBranchNum2.getText()
                                    .toString(),"sellerBankBranch");
                            writeReadDataInFile.writeToFile(mSplasherSignUpBankAccNum2.getText()
                                    .toString(),"sellerBankAccountNumber");
                            boxedLoadingDialog.hideLoadingDialog();
                            finish();
                            toastMessages.productionMessage(ctx,getResources()
                                    .getString(R.string.splasher_wallet_java_customerSupport),1);
                        }
                    }
                });
    }

    public void queryNonDepMoney(){
        try {
            HistoryClassQuery historyClassQuery = new HistoryClassQuery(ctx);
            historyClassQuery.querySplasherNonDepositedMoney
                    (new HistoryClassInterface.queryNonDepositedMoney() {
                        @Override
                        public void beforeMainOperation() {
                            doubleSalesWithTip = 0;
                        }

                        @Override
                        public void queryMoney(ParseObject moneyObj) {
                            String shekel = getResources().getString(R.string.shekel);
                            //carOwnerUsernameCo = dotObject.getString("username");//2
                            double salesWithTipNum = moneyObj.getDouble("priceWithTip");
                            double saleTipTwoDP = Double.parseDouble(df.format(salesWithTipNum));
                            salesWithTip = String.valueOf(saleTipTwoDP);
                            doubleSalesWithTip = Double.parseDouble(salesWithTip);
                            doubleFinal += doubleSalesWithTip;
                            stringFinal = String.valueOf(df.format(doubleFinal));
                            stringFinalShekel = shekel + " " + stringFinal;
                            mBalanceNum.setText(stringFinalShekel);
                        }
                    });
        }catch(NullPointerException npe2){
            npe2.printStackTrace();
        }
    }

    public void fetchBankAccDets(){

        String defaultCode = "XXX";
        String defaultBranch = "XXX";
        String defaultBankAcc = "XXXXXX";

        mSplasherSignUpBankCodeNum2.setText(defaultCode);
        mSplasherSignUpBankBranchNum2.setText(defaultBranch);
        mSplasherSignUpBankAccNum2.setText(defaultBankAcc);

        if ((writeReadDataInFile.readFromFile("sellerBankCode") != null ||
                !writeReadDataInFile.readFromFile("sellerBankCode").isEmpty()) &&
                (writeReadDataInFile.readFromFile("sellerBankBranch") != null ||
                !writeReadDataInFile.readFromFile("sellerBankBranch").isEmpty()) &&
                (writeReadDataInFile.readFromFile("sellerBankAccountNumber") != null ||
                        !writeReadDataInFile.readFromFile("sellerBankAccountNumber")
                                .isEmpty())){

            String bankAccCode = writeReadDataInFile.readFromFile("sellerBankCode");
            String bankAccBranch = writeReadDataInFile.readFromFile("sellerBankBranch");
            String bankAccNum = writeReadDataInFile.readFromFile("sellerBankAccountNumber");

            mSplasherSignUpBankCodeNum2.setText(bankAccCode);
            mSplasherSignUpBankBranchNum2.setText(bankAccBranch);
            mSplasherSignUpBankAccNum2.setText(bankAccNum);

        }
        bankTextViewsState(false,R.drawable.btn_shape_grey);
    }

    public void bankTextViewsState(boolean state, int bgType){
        mSplasherSignUpBankCodeNum2.setClickable(state);
        mSplasherSignUpBankCodeNum2.setEnabled(state);

        mSplasherSignUpBankBranchNum2.setClickable(state);
        mSplasherSignUpBankBranchNum2.setEnabled(state);

        mSplasherSignUpBankAccNum2.setClickable(state);
        mSplasherSignUpBankAccNum2.setEnabled(state);

        mUpdateFilesWallet.setClickable(state);
        mUpdateFilesWallet.setEnabled(state);
        mUpdateFilesWallet.setBackgroundResource(bgType);

        mBankAccProofCamUpload2.setClickable(state);
        mBankAccProofCamUpload2.setEnabled(state);

        mBankAccProofFileUpload2.setClickable(state);
        mBankAccProofFileUpload2.setEnabled(state);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        int REQUEST_EXTERNAL_STORAGE_RESULT = 1;
        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                toastMessages.productionMessage(getApplicationContext(),getResources()
                        .getString(R.string.cameraIntent_act_java_permissionGranted),1);
            } else {
                toastMessages.productionMessage(getApplicationContext(),getResources()
                        .getString(R.string.cameraIntent_act_java_permissionToWrite),1);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
