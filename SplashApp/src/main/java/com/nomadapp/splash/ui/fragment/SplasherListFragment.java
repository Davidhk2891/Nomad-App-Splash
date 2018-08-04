package com.nomadapp.splash.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.imagehandler.GlideImagePlacement;
import com.nomadapp.splash.model.localdatastorage.WriteReadDataInFile;
import com.nomadapp.splash.model.objects.MySplasher;
import com.nomadapp.splash.model.objects.adapters.SplasherListAdapter;
import com.nomadapp.splash.model.server.parseserver.send.RequestClassSend;
import com.nomadapp.splash.ui.activity.carownerside.WashReqParamsActivity;
import com.nomadapp.splash.ui.activity.carownerside.payment.PaymentSettingsActivity;
import com.nomadapp.splash.utils.sysmsgs.loadingdialog.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SplasherListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SplasherListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplasherListFragment extends Fragment {

    private String splasherName,splasherPrice,splasherNumWash,splasherProfPic;
    private int splasherAvgRating;

    //Splasher list data fixed in arrayLists (per category)//
    private ArrayList<String> splasherUserNames = new ArrayList<>();
    private ArrayList<String> splasherUserProfPic = new ArrayList<>();
    private ArrayList<String> splasherUserAvgRat = new ArrayList<>();
    private ArrayList<String> splasherUserPrice = new ArrayList<>();
    private ArrayList<String> splasherUserNumWash = new ArrayList<>();
    //----------------------------------------------------//

    //Splasher Details window (bottom sheet layout)//
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView mRequestAwashTo;
    private TextView mSlUsername;
    private TextView mSlNumWashes;
    private TextView mSlStatus;
    private TextView mSlPriceData;
    private ImageButton mExitBtmSheet;
    private ImageView mSlProfilePic;
    private MaterialRatingBar mSlRatingBar;
    private Button mSlFinallyOrder;
    private boolean bottomSheetUp = false;
    //---------------------------------------------//

    //Splasher details window fields//
    private String fetchedAddress,fetchedAddressDesc;
    private LatLng fetchedCoords;
    private String fetchedFullDate, fetchedSelectedTime;
    private String fetchedServiceType;
    private String fetchedCBrand,fetchedCModel,fetchedCColor,fetchedCPlate;
    private String fetchedDollar;
    private int fetchedNumericalBadge;
    private boolean isTemporalKeyActive;
    private String wash;
    private String washes;
    //------------------------------//

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Added fields//
    private GridView gridView;
    private SplasherListAdapter splasherListAdapter;
    private ArrayList<MySplasher> splasherList = new ArrayList<>();
    //------------//

    private ToastMessages toastMessages = new ToastMessages();
    private BoxedLoadingDialog boxedLoadingDialog;
    private WashReqParamsActivity washReqParamsActivity;
    private RequestClassSend requestClassSend;
    private WriteReadDataInFile writeReadDataInFile;
    //-------------------------------------------------------------------------------------------//

    public SplasherListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplasherListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplasherListFragment newInstance(String param1, String param2) {
        SplasherListFragment fragment = new SplasherListFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_splasher_list, container, false);
        View mBottomSheetSplasherList = v.findViewById(R.id.bottomSheetSplasherList);
        gridView = v.findViewById(R.id.splasherGrid);
        mRequestAwashTo = v.findViewById(R.id.requestAWashTo);
        mExitBtmSheet = v.findViewById(R.id.exitBtmSheet);
        mSlProfilePic = v.findViewById(R.id.slProfPic);
        mSlRatingBar = v.findViewById(R.id.slRatingBar);
        mSlFinallyOrder = v.findViewById(R.id.slFinallyOrder);
        mSlUsername = v.findViewById(R.id.slUsername);
        mSlNumWashes = v.findViewById(R.id.slNumWashes);
        mSlStatus = v.findViewById(R.id.slStatus);
        mSlPriceData = v.findViewById(R.id.slPriceData);

        if (getActivity() != null) {
            splasherListAdapter = new SplasherListAdapter(getActivity(), R.layout.splasher_row
                    , splasherList);
            splasherList.clear();
            gridView.setAdapter(splasherListAdapter);

            queryActiveSplashers();
            onCardViewClick();

            mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetSplasherList);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            collapseBtmSheetFromX();
            btmSheetStateControl();
            sendReqOrderToSl();
        }
        return v;
    }

    public void fetchRequestDataToOrder(){
        //values to get from getters//
        Log.i("a1",washReqParamsActivity.getAddress());
        fetchedAddress = washReqParamsActivity.getAddress();
        fetchedAddressDesc = washReqParamsActivity.getCarAddressDescription();
        fetchedCoords = washReqParamsActivity.getCarCoordinates();
        fetchedFullDate = washReqParamsActivity.getFullDate();
        fetchedSelectedTime = washReqParamsActivity.getSelectedTime();
        fetchedServiceType = washReqParamsActivity.getGetServiceType();
        fetchedCBrand = washReqParamsActivity.getCarBrandToUpload();
        fetchedCModel = washReqParamsActivity.getCarModelToUpload();
        fetchedCColor = washReqParamsActivity.getCarColorToUpload();
        fetchedCPlate = washReqParamsActivity.getCarPlateToUpload();
        fetchedDollar = splasherPrice;
        fetchedNumericalBadge = washReqParamsActivity.getNumericalBadge();
        isTemporalKeyActive = washReqParamsActivity.isTemporalKeyActive();
        Log.i("f1",fetchedAddress);Log.i("f2",fetchedAddressDesc);
        Log.i("f3",fetchedCoords.toString());Log.i("f4",fetchedFullDate);
        Log.i("f5",fetchedSelectedTime);Log.i("f6",fetchedServiceType);
        Log.i("f7",fetchedCBrand);Log.i("f8",fetchedCModel);
        Log.i("f9",fetchedCColor);Log.i("f10",fetchedCPlate);
        Log.i("f11",fetchedDollar);Log.i("f12",String.valueOf(fetchedNumericalBadge));
        Log.i("f13",String.valueOf(isTemporalKeyActive));
        //--------------------------//
    }

    public void sendReqOrderToSl(){
        mSlFinallyOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if((writeReadDataInFile.readFromFile("buyer_key_temporal")
                                .isEmpty()) && writeReadDataInFile
                                .readFromFile("buyer_key_permanent").isEmpty()) {
                    toastMessages.productionMessage(getActivity().getApplicationContext()
                            ,getResources().getString(R.string.act_wash_my_car_pleaseChoosePaymentM)
                            ,1);
                    startActivity(new Intent(getActivity().getApplicationContext()
                            , PaymentSettingsActivity.class));
                }else{
                    boxedLoadingDialog.showLoadingDialog();
                    fetchRequestDataToOrder();
                    String splasherUsername = mSlUsername.getText().toString();
                    String requestType = "private";
                    requestClassSend.loadRequest(fetchedAddress,fetchedCoords,fetchedAddressDesc
                            ,fetchedFullDate,fetchedSelectedTime,fetchedServiceType,fetchedCBrand
                            ,fetchedCModel,fetchedCColor,fetchedCPlate,fetchedDollar,
                            fetchedNumericalBadge,isTemporalKeyActive,splasherUsername
                            ,requestType);
                }
            }
        });
    }

    public void collapseBtmSheetFromX(){
        mExitBtmSheet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Collapse down the bottom sheet
                toastMessages.debugMesssage(getActivity().getApplicationContext()
                        ,"pressed",1);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetUp = false;
            }
        });
    }

    public void btmSheetStateControl(){
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_HIDDEN:
                        bottomSheetUp = false;
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    //-----API 23 AND UP-----//
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("onAttach", "called");
        wash = getResources().getString(R.string.act_wash_my_car_wash);
        washes = getResources().getString(R.string.act_wash_my_car_washes);
        boxedLoadingDialog = new BoxedLoadingDialog(getActivity());
        washReqParamsActivity = new WashReqParamsActivity();
        writeReadDataInFile = new WriteReadDataInFile(getActivity());
        requestClassSend = new RequestClassSend(getActivity());
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    //----------------------//

    //-----API 22 AND DOWN-----//
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("onAttach", "called");
        wash = getResources().getString(R.string.act_wash_my_car_wash);
        washes = getResources().getString(R.string.act_wash_my_car_washes);
        boxedLoadingDialog = new BoxedLoadingDialog(getActivity());
        washReqParamsActivity = new WashReqParamsActivity();
        writeReadDataInFile = new WriteReadDataInFile(getActivity());
        requestClassSend = new RequestClassSend(getActivity());
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    //-------------------------//

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

    public void onCardViewClick(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSelectedGridItemData(position);
            }
        });
    }

    public void getSelectedGridItemData(final int position){
        //Get data for selected card from grid
        //Slide up the bottom_sheet
        if (!bottomSheetUp) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            allocateDataForSL(position);
            bottomSheetUp = true;
        }else{
            if (!splasherUserNames.get(position).equals(mSlUsername.getText()
                    .toString())){
                allocateDataForSL(position);
                bottomSheetUp = true;
            }else {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetUp = false;
            }
        }
    }

    public void allocateDataForSL(int position){
        String request_wash_from = getResources().getString(R.string
                .act_wash_my_car_requestAWashFrom);

        mSlUsername.setText(splasherUserNames.get(position));

        String fullRequestTitle = request_wash_from + " " + splasherUserNames.get(position);
        mRequestAwashTo.setText(fullRequestTitle);

        mSlRatingBar.setProgress(Integer.parseInt(splasherUserAvgRat.get(position)) * 2);

        String fullNumWashes;
        if (Integer.parseInt(splasherUserNumWash.get(position)) > 1) {
            fullNumWashes = splasherUserNumWash.get(position) + " " + washes;
        }else {
            fullNumWashes = splasherUserNumWash.get(position) + " " + wash;
        }
        mSlNumWashes.setText(fullNumWashes);

        String fullUserPrice;
        if (!splasherUserPrice.get(position).contains(".")) {
            //a.k.a the number is whole...then
            fullUserPrice = "₪" + " " + splasherUserPrice.get(position) + ".00";
            mSlPriceData.setText(fullUserPrice);
        } else {
            fullUserPrice = "₪" + " " + splasherUserPrice.get(position);
            mSlPriceData.setText(fullUserPrice);
        }

        GlideImagePlacement gip = new GlideImagePlacement(getActivity().getApplicationContext());
        gip.roundImagePlacementFromString(splasherUserProfPic.get(position), mSlProfilePic);
    }

    public void queryActiveSplashers(){
        ParseQuery<ParseObject> profileQuery = ParseQuery.getQuery("Profile");
        profileQuery.whereEqualTo("CarOwnerOrSplasher", "splasher");
        profileQuery.whereEqualTo("status", "active");
        profileQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){

                        splasherList.clear();
                        splasherUserNames.clear();
                        splasherUserAvgRat.clear();
                        splasherUserProfPic.clear();
                        splasherUserNumWash.clear();
                        splasherUserPrice.clear();

                        for (ParseObject splasherObj : objects){
                            splasherName = splasherObj.getString("username");//<<<<<<<<<<<<<<<<<

                            ParseFile localProfPic = splasherObj
                                    .getParseFile("splasherProfPic");
                            splasherProfPic = String.valueOf(localProfPic.getUrl());//<<<<<<<<<<<<<<

                            if (Integer.parseInt(splasherObj
                                    .getString("oldAvgRating")) <= 5) {
                                splasherAvgRating = Integer.parseInt(splasherObj
                                        .getString("oldAvgRating"));//<<<<<<<<<<<<<<<<<<<<<<<<<<
                            }else{
                                splasherAvgRating = 5;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                            }
                            splasherPrice = splasherObj.getString("setPrice");//<<<<<<<<<<<<<<<<
                            splasherNumWash = splasherObj.getString("washes");//<<<<<<<<<<<<<<<<

                            //Apply Data to MySplasher object//
                            //left here. need to add arraylist in which to deposit the
                            // indivudual data in strings. nums etc
                            MySplasher splasher = new MySplasher();
                            splasher.setSplasherUsername(splasherName);
                            splasher.setSplasherProfPic(splasherProfPic);
                            String price = "₪ " + splasherPrice;
                            splasher.setSplasherPrice(price);
                            splasher.setSplasherAvgRating(splasherAvgRating);

                            String numOfWashes;
                            if (Integer.parseInt(splasherNumWash) > 1) {
                                numOfWashes = splasherNumWash + " " + washes;
                            }else{
                                numOfWashes = splasherNumWash + " " + wash;
                            }
                            splasher.setSplasherNumOfWashes(numOfWashes);

                            Log.i("splasherName", splasherName);
                            Log.i("splasherProfPic", splasherProfPic);
                            Log.i("splasherPrice", splasherPrice);
                            Log.i("splasherRating", String.valueOf(splasherAvgRating));
                            Log.i("splasherWashes", numOfWashes);
                            splasherList.add(splasher);
                            //-------------------------------//

                            //Adding each individual splasher data to their respective ArrayLists//
                            splasherUserNames.add(splasherName);
                            splasherUserPrice.add(splasherPrice);
                            splasherUserNumWash.add(splasherNumWash);
                            splasherUserProfPic.add(splasherProfPic);
                            splasherUserAvgRat.add(String.valueOf(splasherAvgRating));
                            //-------------------------------------------------------------------//
                        }
                    }
                }
            }
        });
    }
}
