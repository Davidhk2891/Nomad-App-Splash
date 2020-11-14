package com.nomadapp.splash.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.constants.PaymeConstants;
import com.nomadapp.splash.model.constants.serverconstants.ProfileConstants;
import com.nomadapp.splash.ui.activity.splasherside.SplasherServicesActivity;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.CustomAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.DialogAcceptInterface;
import com.nomadapp.splash.utils.sysmsgs.ForcedAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SplasherOnboarding2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SplasherOnboarding2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplasherOnboarding2 extends Fragment {

    private TextView mSplasher_onboarding_email, mSplasher_onboarding_id,
            mSplasher_onboarding_address, mSplasher_onboarding_password,
            mSplasher_onboarding_repeat_password;

    private Button mSplasher_onboarding_button2;

    private String mName;
    private String mLast;
    private String mPhone;

    private ToastMessages toastMessages;
    private BoxedLoadingDialog boxedLoadingDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    public SplasherOnboarding2() {
        // Required empty public constructor
    }

    public static SplasherOnboarding2 newInstance(String param1, String param2) {
        SplasherOnboarding2 fragment = new SplasherOnboarding2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString("name");
            mLast = getArguments().getString("last");
            mPhone = getArguments().getString("phone");
            Log.i("grapes", "are " + mName + " " + mLast + " " + mPhone);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_splasher_onboarding2, container
                , false);
        mSplasher_onboarding_email = v.findViewById(R.id.splasher_onboarding_email);
        mSplasher_onboarding_id = v.findViewById(R.id.splasher_onboarding_id);
        mSplasher_onboarding_address = v.findViewById(R.id.splasher_onboarding_address);
        mSplasher_onboarding_password = v.findViewById(R.id.splasher_onboarding_password);
        mSplasher_onboarding_button2 = v.findViewById(R.id.splasher_onboarding_button2);
        mSplasher_onboarding_repeat_password = v.findViewById
                (R.id.splasher_onboarding_repeat_password);

        toastMessages = new ToastMessages();
        boxedLoadingDialog = new BoxedLoadingDialog(getActivity());

        beginSplasherAccountCreation();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void beginSplasherAccountCreation() {
        mSplasher_onboarding_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSplasher_onboarding_email.getText().toString().isEmpty() ||
                        mSplasher_onboarding_id.getText().toString().isEmpty() ||
                        mSplasher_onboarding_address.getText().toString().isEmpty() ||
                        mSplasher_onboarding_password.getText().toString().isEmpty() ||
                        mSplasher_onboarding_repeat_password.getText().toString().isEmpty()) {

                    completeFormDialog(getActivity().getResources().getString(R.string
                            .becomeSplasher_act_java_missingFields)
                            ,getActivity().getResources().getString(R.string
                            .becomeSplasher_act_java_pleaseFillAll));

                } else if (!mSplasher_onboarding_password.getText().toString()
                        .equals(mSplasher_onboarding_repeat_password.getText().toString())) {

                    completeFormDialog(getActivity().getResources().getString(R.string
                            .splasher_onboarding_passwordsDontMatch),getActivity().getResources()
                            .getString(R.string.becomeSplasher_act_java_bothPasswords));

                } else if (!mSplasher_onboarding_email.getText().toString().contains("@")) {

                    completeFormDialog(getActivity().getResources().getString(R.string
                            .paymentSettings_act_java_wrongEmail),getResources().getString
                            (R.string.becomeSplasher_act_java_pleaseEnterValidEmail));
                } else {
                    boxedLoadingDialog.showLoadingDialog();
                    createParseSplasherUser();
                }
            }
        });
    }

    private void createParseSplasherUser(){
        final String splasher = ProfileConstants.CLASS_PROFILE_SPLAHER;
        ParseUser splasherUser = new ParseUser();
        splasherUser.setUsername(mSplasher_onboarding_email.getText().toString());
        splasherUser.setPassword(mSplasher_onboarding_password.getText().toString());
        splasherUser.setEmail(mSplasher_onboarding_email.getText().toString());
        splasherUser.put("city", "not set");
        splasherUser.put("phonenumber", mPhone);
        splasherUser.put("name", mName);
        splasherUser.put("lastname", mLast);
        splasherUser.put("CarOwnerOrSplasher", splasher);
        splasherUser.signUpInBackground(new SignUpCallback(){
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    createSplasherProfile();
                }else{
                    boxedLoadingDialog.hideLoadingDialog();
                    signUpFailed(e);
                }
            }
        });
    }

    private void createSplasherProfile(){

        ParseObject profile = new ParseObject(ProfileConstants.CLASS_PROFILE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_USERNAME,
                mName + " " + mLast);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_CAR_OWNER_OR_SPLASHER,
                ProfileConstants.CLASS_PROFILE_SPLAHER);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_EMAIL, mSplasher_onboarding_email
                .getText().toString());
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SPLASHER_TYPE,
                ProfileConstants.CLASS_PROFILE_SET_INDEPENDENT);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_OLD_AVG_RATING
                , ProfileConstants.CLASS_PROFILE_OLD_AVG_RATING);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_WASHES,
                ProfileConstants.CLASS_PROFILE_WASHES);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_WASHES_CANCELED,
                ProfileConstants.CLASS_PROFILE_WASHES_CANCELED);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_NUMERICAL_BADGE,
                ProfileConstants.CLASS_PROFILE_NUMERICAL_BADGE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SET_RPICE,
                ProfileConstants.CLASS_PROFILE_SET_PRICE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SET_PRICE_E_INT,
                ProfileConstants.CLASS_PROFILE_SET_PRICE_E_INT);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SET_PRICE_MOTO,
                ProfileConstants.CLASS_PROFILE_SET_PRICE_MOTO);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_SERVICE_RANGE,
                ProfileConstants.CLASS_PROFILE_SERVICE_RANGE);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_SERVICE_EC,
                ProfileConstants.CLASS_PROFILE_SERVICE_EC);
        profile.put(ProfileConstants.CLASS_PROFILE_COL_EC_COORDS,
                ProfileConstants.CLASS_PROFILE_ECCOORDS);

        profile.put(ProfileConstants.CLASS_PROFILE_SET_COL_SPLASHER_PROF_PIC,
                ProfileConstants.CLASS_PROFILE_SPLASHER_PROF_PIC_DEFAULT(getActivity()));

        profile.put(ProfileConstants.CLASS_PROFILE_COL_STATUS,
                ProfileConstants.CLASS_PROFILE_SET_ACTIVE);

        profile.put(ProfileConstants.CLASS_PROFILE_COL_VERIFIED,
                ProfileConstants.CLASS_PROFILE_SET_TRUE);

        profile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(getActivity());
                    customAlertDialog.generalPurposeQuestionDialog(getActivity()
                            , getResources().getString(R.string
                                    .splasher_onboarding_alert_accBeing_title)
                            , getResources().getString(R.string
                                    .splasher_onboarding_alert_accBeing_message)
                            , getResources().getString(R.string
                                    .splasher_onboarding_alert_accBeing_ok)
                            , null, new DialogAcceptInterface() {
                                @Override
                                public void onAcceptOption() {
                                    createSplasherDocument();
                                }
                            });
                }else{
                    boxedLoadingDialog.hideLoadingDialog();
                    signUpFailed(e);
                }
            }
        });
    }

    private void createSplasherDocument(){
        final ParseObject documents = new ParseObject("Documents");
        documents.put("username", mSplasher_onboarding_email.getText().toString());
        documents.put("sellerId", PaymeConstants.SPLASH_SELLER_KEY);
        documents.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    boxedLoadingDialog.hideLoadingDialog();
                    Intent intent = new Intent(getActivity(), SplasherServicesActivity.class);
                    intent.putExtra("signup", "redirect");
                    startActivity(intent);
                }else{
                    boxedLoadingDialog.hideLoadingDialog();
                    signUpFailed(e);
                }
            }
        });
    }

    private void signUpFailed(ParseException e){
        boxedLoadingDialog.hideLoadingDialog();
        Log.i("splasherSignUp", "SignUp Failed");
        toastMessages.productionMessage(getActivity().getApplicationContext(),"SignUp Failed."
                + " " + e.getMessage(), 1);
    }

    private void completeFormDialog(String title, String message){
        ForcedAlertDialog fad = new ForcedAlertDialog(getActivity());
        fad.generalPurposeForcedDialogNoAction(title,message,getResources().getString(R.string
                .becomeSplasher_act_java_ok));
    }
}
