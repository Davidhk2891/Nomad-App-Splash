package com.nomadapp.splash.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nomadapp.splash.R;
import com.nomadapp.splash.model.objects.MySplasher;
import com.nomadapp.splash.model.objects.adapters.SplasherListAdapter;
import com.nomadapp.splash.utils.sysmsgs.toastmessages.ToastMessages;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

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
        gridView = v.findViewById(R.id.splasherGrid);

        if (getActivity() != null) {
            splasherListAdapter = new SplasherListAdapter(getActivity(), R.layout.splasher_row
                    , splasherList);
            splasherList.clear();
            gridView.setAdapter(splasherListAdapter);

            queryActiveSplashers();

            onCardViewClick();
        }
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

    public void onCardViewClick(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSelectedGridItemData();
            }
        });
    }

    public void getSelectedGridItemData(){
        //Get data for selected card from grid

        toastMessages.debugMesssage(getActivity().getApplicationContext(),"selected",1);
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
                        for (ParseObject splasherObj : objects){
                            splasherName = splasherObj.getString("username");//<<<<<<<<<<<<<<<<<

                            ParseFile localProfPic = splasherObj.getParseFile("splasherProfPic");
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
                            MySplasher splasher = new MySplasher();
                            splasher.setSplasherUsername(splasherName);
                            splasher.setSplasherProfPic(splasherProfPic);
                            String price = "₪ " + splasherPrice;
                            splasher.setSplasherPrice(price);
                            splasher.setSplasherAvgRating(splasherAvgRating);
                            String numOfWashes = splasherNumWash + " washes";
                            splasher.setSplasherNumOfWashes(numOfWashes);
                            Log.i("splasherName", splasherName);
                            Log.i("splasherProfPic", splasherProfPic);
                            Log.i("splasherPrice", splasherPrice);
                            Log.i("splasherRating", String.valueOf(splasherAvgRating));
                            Log.i("splasherWashes", numOfWashes);
                            splasherList.add(splasher);
                            //-------------------------------//
                        }
                    }
                }
            }
        });
    }
}
