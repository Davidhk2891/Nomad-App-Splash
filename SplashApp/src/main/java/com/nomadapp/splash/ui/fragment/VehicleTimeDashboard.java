package com.nomadapp.splash.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.nomadapp.splash.R;
import com.nomadapp.splash.model.objects.MyCar;
import com.nomadapp.splash.model.objects.adapters.CarAdapter;
import com.nomadapp.splash.model.server.parseserver.CarsClassInterface;
import com.nomadapp.splash.model.server.parseserver.queries.CarsClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.MetricsClassQuery;
import com.nomadapp.splash.model.server.parseserver.queries.UserClassQuery;
import com.nomadapp.splash.ui.activity.carownerside.CarAdditionActivity;
import com.nomadapp.splash.ui.activity.carownerside.WashReqParamsActivity;
import com.nomadapp.splash.ui.activity.standard.SignUpLogInActivity;
import com.nomadapp.splash.utils.customclocksops.DateTime;
import com.nomadapp.splash.utils.customclocksops.TimePick;
import com.nomadapp.splash.utils.sysmsgs.BoxedLoadingDialog;
import com.nomadapp.splash.utils.sysmsgs.ForcedAlertDialog;
import com.nomadapp.splash.utils.sysmsgs.ToastMessages;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VehicleTimeDashboard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VehicleTimeDashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VehicleTimeDashboard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    //Car Selection
    public TextView mCar_for_request;
    private TextView mTime_until;
    private Button mRequest_next;

    //DateTime Selection
    private TimePicker mTime_picker;
    private Button mSet_date_time, mCancel_date_time;
    private TimePick timePick;
    private DateTime dateTime;

    public static AlertDialog.Builder carSelectinoDialogBuilder;
    public static AlertDialog.Builder timePickerDialogBuilder;
    public static AlertDialog carSelectionFinalDialog;
    public static AlertDialog timePickerFinalDialog;
    private Button btn;
    private TextView mEmpty_car_list_main;
    private ArrayList<MyCar> dbCars = new ArrayList<>();
    private ListView lv;

    public static ArrayList<String> carBrandList = new ArrayList<>();
    public static ArrayList<String> carModelList = new ArrayList<>();
    public static ArrayList<String> carColorList = new ArrayList<>();
    public static ArrayList<String> carPlateList = new ArrayList<>();

    private String brand, model, color, plate;

    private CarAdapter myCarAdapter;

    //Final data to be forwarded
    public static String ADDRESS, ADDRESS_DETAILS;
    public static LatLng ADDRESS_COORDS;
    private String carBrandSelected, carModelSelected, carColorSelected, carPlateSelected;
    private String finalDateTime;

    private BoxedLoadingDialog boxedLoadingDialog;
    private ToastMessages toastMessages;
    private ForcedAlertDialog fad;
    private UserClassQuery userClassQuery;

    public Button getmRequest_next() {
        return mRequest_next;
    }

    public VehicleTimeDashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VehicleTimeDashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static VehicleTimeDashboard newInstance(String param1, String param2) {
        VehicleTimeDashboard fragment = new VehicleTimeDashboard();
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
            String mArg1 = getArguments().getString("arg1");
            String mArg2 = getArguments().getString("arg2");
            Log.i("Frag_Args", "are " + mArg1 + " " + mArg2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vehicle_time_dashboard, container
                , false);
        mCar_for_request = v.findViewById(R.id.car_for_request);
        mTime_until = v.findViewById(R.id.time_until);
        mRequest_next = v.findViewById(R.id.request_next);

        boxedLoadingDialog = new BoxedLoadingDialog(getActivity());
        toastMessages = new ToastMessages();
        carSelectinoDialogBuilder = new AlertDialog.Builder(getActivity());
        timePickerDialogBuilder = new AlertDialog.Builder(getActivity());
        timePick = new TimePick(getActivity());
        dateTime = new DateTime(getActivity());
        fad = new ForcedAlertDialog(getActivity());
        userClassQuery = new UserClassQuery(getActivity());

        ADDRESS_DETAILS = getActivity().getResources().getString(R.string.threeDots);

        carSelectionDialog();
        untilTimePickerDialog();

        chooseCar();
        setUntilTime();
        setRequestData();
        return v;
    }

    private void carSelectionDialog(){
        dialog_creation(R.layout.car_action_items, new DialogCreation() {
            @Override
            public void dialogAction(View view) {
                //Operations for custom car selection list
                lv = view.findViewById(R.id.carList);
                btn = view.findViewById(R.id.addCar_main);
                mEmpty_car_list_main = view.findViewById(R.id.emptyCarList_main);

                myCarAdapter = new CarAdapter(getActivity(), R.layout.car_row, dbCars);
                dbCars.clear();
                lv.setAdapter(myCarAdapter);
                refreshData();

                carSelectinoDialogBuilder.setView(view);

                carSelectionFinalDialog = carSelectinoDialogBuilder.create();

                onCarClick();
                onCarLongClick();
            }
        });
    }

    private void untilTimePickerDialog(){
        dialog_creation(R.layout.custom_time_picker, new DialogCreation() {
            @Override
            public void dialogAction(View view) {
                //Operations for custom time picker
                mTime_picker = view.findViewById(R.id.time_pick_timePicker);
                mSet_date_time = view.findViewById(R.id.time_pick_set);
                mCancel_date_time = view.findViewById(R.id.time_pick_cancel);
                timePickerDialogBuilder.setView(view);
                timePickerFinalDialog = timePickerDialogBuilder.create();
            }
        });
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

    private void chooseCar(){
        mCar_for_request.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (userClassQuery.userExists()) {
                    Log.i("Orange1", "Choose car pressed");
                    open_car_dialog();
                }else{
                    notLoggedIn(getActivity().getResources().getString
                            (R.string.carOwner_act_java_youNeedToLogInAddCar));
                }
            }
        });
    }

    private void setUntilTime(){
        mTime_until.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (userClassQuery.userExists()) {
                    Log.i("Orange1", "Choose until time pressed");
                    open_time_picker_dialog();
                }else{
                    notLoggedIn(getActivity().getResources().getString
                            (R.string.carOwner_act_java_youNeedToLogInRequestSetTime));
                }
            }
        });
    }

    private void refreshData(){
        CarsClassQuery carsClassQuery = new CarsClassQuery(getActivity());
        carsClassQuery.getUserCars(new CarsClassInterface() {
            @Override
            public void fetchCars(List<ParseObject> objects) {
                if (objects.size() > 0){

                    dbCars.clear();
                    carBrandList.clear();
                    carModelList.clear();
                    carColorList.clear();
                    carPlateList.clear();

                    carListIsEmptyChecker(View.GONE, getActivity()
                            .getResources().getString(R.string.act_wash_my_car_chooseACar));
                    showLastestAddedCar();

                    for (ParseObject carObj : objects){

                        carBrandSelected = carObj.getString("brand");
                        carModelSelected = carObj.getString("model");
                        carColorSelected = carObj.getString("color");
                        carPlateSelected = carObj.getString("plateNum");

                        //OBJECT INSTANTIATION MUST BE INITIALIZED INSIDE THE LOOP!!!
                        MyCar refreshedCar = new MyCar();
                        refreshedCar.setBrand(carBrandSelected);
                        refreshedCar.setModel(carModelSelected);
                        refreshedCar.setColorz(carColorSelected);
                        refreshedCar.setPlateNum(carPlateSelected);

                        dbCars.add(refreshedCar);

                        carBrandList.add(carBrandSelected);
                        carModelList.add(carModelSelected);
                        carColorList.add(carColorSelected);
                        carPlateList.add(carPlateSelected);

                        myCarAdapter.notifyDataSetChanged();
                    }

                }else{
                    carListIsEmptyChecker(View.VISIBLE, getActivity().getResources()
                            .getString(R.string.act_wash_my_car_chooseACar));

                    dbCars.clear();
                    carBrandList.clear();
                    carModelList.clear();
                    carColorList.clear();
                    carPlateList.clear();
                }
            }
        });
    }

    private void showLastestAddedCar(){
        Bundle keyToKeepListOpen = getActivity().getIntent().getExtras();
        if (keyToKeepListOpen != null) {
            String keyToOpen = keyToKeepListOpen.getString("listStatus");
            if (keyToOpen != null && keyToOpen.equals("keepOpen")) {
                String carBrandToUpload = keyToKeepListOpen.getString("carBrand");
                String carModelToUpload = keyToKeepListOpen.getString("carModel");
                String carColorToUpload = keyToKeepListOpen.getString("carColor");
                String carPlateToUpload = keyToKeepListOpen.getString("carPlate");

                String carSelectedHolder = carBrandToUpload + " " + carModelToUpload + " "
                       + carColorToUpload + " " + carPlateToUpload;

                setSelectedCar(carBrandToUpload, carModelToUpload, carColorToUpload, carPlateToUpload);

                carListIsEmptyChecker(View.GONE, carSelectedHolder);
            }
        }
    }

    private void dialog_creation(int layout
            , DialogCreation dialogCreation){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View view = inflater.inflate(layout, null);
            dialogCreation.dialogAction(view);
        }
    }

    private void onCarClick(){
       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Log.i("orange1", "is " + position);
               allocateDataForSelectedCar(position);
           }
       });
    }

    private void onCarLongClick(){
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position
                    , long id) {
                android.app.AlertDialog.Builder deleteCarDialog = new android.app.AlertDialog
                        .Builder(getActivity());
                deleteCarDialog.setMessage(getActivity().getResources()
                        .getString(R.string.washMyCar_act_java_deleteCar));
                deleteCarDialog.setPositiveButton(getActivity().getResources()
                                .getString(R.string.washMyCar_act_java_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boxedLoadingDialog.showLoadingDialog();
                                CarsClassQuery carsClassQuery = new CarsClassQuery(getActivity());
                                carsClassQuery.deleteCar(new CarsClassInterface.carsDeletion() {
                                    @Override
                                    public void deleteCar(List<ParseObject> objects) {
                                        if (objects.size() > 0){
                                            for (ParseObject carObj : objects){
                                                if (carObj.get("plateNum")
                                                        .equals(carPlateList.get(position))){
                                                    carObj.deleteInBackground(new DeleteCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            dbCars.remove(position);
                                                            myCarAdapter.notifyDataSetChanged();
                                                            refreshData();
                                                            boxedLoadingDialog.hideLoadingDialog();
                                                        }
                                                    });
                                                }
                                            }
                                        }else{
                                            toastMessages.productionMessage(getActivity()
                                                    , "Something went wrong!"
                                                    , 1);
                                            boxedLoadingDialog.hideLoadingDialog();
                                        }
                                    }
                                });
                            }
                        });
                deleteCarDialog.setNegativeButton(getActivity().getResources().getString(R.string
                        .act_wash_my_car_cancel), null);
                android.app.AlertDialog alertD = deleteCarDialog.create();
                alertD.show();
                return true;
            }
        });
    }

    private void carListIsEmptyChecker(int state, String text){
        mCar_for_request.setText(text);
        mEmpty_car_list_main.setVisibility(state);
        Log.i("orange2", "rano " + text);
    }

    private void allocateDataForSelectedCar(int pos){
        String visibleSelectedCar = carBrandList.get(pos) + " " + carModelList.get(pos)
                + " " + carColorList.get(pos) + " " + carPlateList.get(pos);
        mCar_for_request.setText(visibleSelectedCar);

        setSelectedCar(carBrandList.get(pos), carModelList.get(pos), carColorList.get(pos)
                , carPlateList.get(pos));

        carSelectionFinalDialog.cancel();
    }

    private void open_car_dialog(){
        carSelectionFinalDialog.show();
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CarAdditionActivity.class);
                intent.putExtra("coords", "LatLng");
                intent.putExtra("lat", ADDRESS_COORDS.latitude);
                intent.putExtra("lon", ADDRESS_COORDS.longitude);
                startActivity(intent);
            }
        });
    }

    private void open_time_picker_dialog(){
        timePickerFinalDialog.show();
        time_picker_operations();
    }

    private void time_picker_operations(){
        timePick.setTimePickerInterval(mTime_picker);
        timePick.pickTime(mTime_picker, mTime_until, timePickerFinalDialog);
        mSet_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userClassQuery.userExists()) {
                    setDateTime();
                }
            }
        });

        mCancel_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDateTime();
            }
        });
    }

    private void setSelectedCar(String brand, String model, String color, String plate){
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.plate = plate;
    }

    private void setDateTime(){
        if (!timePick.isTimePickerMoved()) {
            if (!timePick.isUntilTimeWithinRules()) {
                //If timePicker ISN'T touched AND whatever hour ISN'T within rules,
                //then selectedTime == currentTime
                toastMessages.productionMessage(getActivity().getApplicationContext()
                        , getResources().getString(R.string.washMyCar_act_java_minimumDuration)
                        , 1);
                Log.i("DateTimeStatus", "NOT GOOD TO GO");
            } else {
                //If timePicker ISN'T touched AND whatever hour IS within rules,
                //then selectedTime > currentTime
                //You get to this case once you have already selected an OK time and you press
                //on set again without touching the time picker
                timePick.timePickerAutoSet(mTime_picker, mTime_until);
                Log.i("DateTimeStatus", "GOOD TO GO");
                timePickerFinalDialog.cancel();
            }
        }else{
            if (!timePick.isUntilTimeWithinRules()) {
                toastMessages.productionMessage(getActivity().getApplicationContext()
                        , getResources().getString(R.string.washMyCar_act_java_minimumDuration)
                        , 1);
                Log.i("DateTimeStatus", " NOT GOOD TO GO");
            } else {
                timePick.dayChecker();
                String finalSelectedTime;
                String finalSelectedDate;
                if (!timePick.isTimePickerMoved()) {
                    finalSelectedDate = dateTime.rawCurrentDate();
                } else {
                    finalSelectedDate = timePick.getSelectedDate();
                }
                finalSelectedTime = timePick.getSelectedTime();
                Log.i("green1", finalSelectedTime);
                finalDateTime = finalSelectedDate + " " + finalSelectedTime;
                Log.i("green2", finalDateTime);
                //EVERY SCENARIO SEEMS TO WORK. GOOD TO GO. TEST AFTER 19 FEATURE AFTER 19
                Log.i("DateTimeStatus", "GOOD TO GO " + finalDateTime);
                timePickerFinalDialog.cancel();
            }
        }
    }

    private void cancelDateTime(){
        mTime_until.setText("");
        mTime_until.setHint(getActivity().getResources().getString(R.string.act_wash_my_car_until));
        timePick.setTimePickerMoved(false);
        timePickerFinalDialog.setCancelable(true);
        timePickerFinalDialog.cancel();
    }

    private void setRequestData(){
        mRequest_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (userClassQuery.userExists()) {
                    if (ADDRESS.equals(getActivity().getResources().getString(R.string
                            .failed_to_retrieve_address))) {
                        //If this is the case and the button is active, then the dialog below
                        //should happen.
                        fad.generalPurposeForcedDialogNoAction(getActivity().getResources()
                                        .getString(R.string.no_address)
                                , getActivity().getResources().getString(R.string
                                        .there_was_problem_address)
                                , getActivity().getResources().getString(R.string
                                        .washMyCar_act_java_ok));
                    } else {
                        if (mCar_for_request.getText().toString().isEmpty()
                                || mTime_until.getText().toString().isEmpty()) {
                            fad.generalPurposeForcedDialogNoAction(getActivity().getResources()
                                            .getString(R.string.washMyCar_act_java_missingFields)
                                    , getActivity().getResources().getString(R.string
                                            .splasher_wallet_java_pleaseFillAll)
                                    , getActivity().getResources().getString(R.string
                                            .washMyCar_act_java_ok));
                        } else {
                            updateWashMyCarBtnCount();
                            Intent requestIntent = new Intent(getActivity(), WashReqParamsActivity.class);
                            requestIntent.putExtra("address", ADDRESS);
                            requestIntent.putExtra("address_details", ADDRESS_DETAILS);

                            requestIntent.putExtra("address_coords_lat", ADDRESS_COORDS.latitude);
                            requestIntent.putExtra("address_coords_lon", ADDRESS_COORDS.longitude);

                            requestIntent.putExtra("until_time", finalDateTime);

                            requestIntent.putExtra("carBrand", brand);
                            requestIntent.putExtra("carModel", model);
                            requestIntent.putExtra("carColor", color);
                            requestIntent.putExtra("carPlate", plate);

                            Log.i("apple_address", ADDRESS);
                            Log.i("apple_address_dets", ADDRESS_DETAILS);
                            startActivity(requestIntent);
                        }
                    }
                }else{
                    notLoggedIn(getResources().getString
                            (R.string.carOwner_act_java_youNeedToLogInRequestWash));
                }
            }
        });
    }
    public void updateWashMyCarBtnCount(){
        MetricsClassQuery metricsClassQuery = new MetricsClassQuery(getActivity());
        metricsClassQuery.queryMetricsToUpdate("washMyCar");
    }

    private void notLoggedIn(String string) {
        AlertDialog.Builder requestDialog = new AlertDialog.Builder(getActivity());
        requestDialog.setTitle(getResources().getString(R.string.carOwner_act_java_notLoggedIn));
        requestDialog.setIcon(android.R.drawable.ic_dialog_alert);
        requestDialog.setMessage(string);
        requestDialog.setPositiveButton(getResources().getString(R.string.carOwner_act_java_ok)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(),
                                SignUpLogInActivity.class);
                        startActivity(intent);
                    }
                });
        requestDialog.setNegativeButton(getResources().getString(R.string
                .carOwner_act_java_cancel), null);
        requestDialog.show();
    }
}
