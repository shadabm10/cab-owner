package com.project.sketch.ugo.fragments;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;
import com.project.sketch.ugo.httpRequest.apiModel2.DriverResponce;
import com.project.sketch.ugo.httpRequest.apiModel4.CancelBooking;
import com.project.sketch.ugo.httpRequest.apiModel4.FeedbackRatingResponse;
import com.project.sketch.ugo.httpRequest.apiModel8.FareDetails;
import com.project.sketch.ugo.httpRequest.apiModel8.FareInfo;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;
import com.project.sketch.ugo.utils.ValidationClass;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/12/18.
 */

public class TrackYourRide extends Fragment implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient googleApiClient;
    private GoogleMap map = null;

    SupportMapFragment mapFrag;
    LocationManager lm;
    Geocoder geoCoder;

    TextView tv_pickup_address, tv_drop_address;
    Button btn_help;

    SharedPref  sharedPref;
    GlobalClass globalClass;

    public FragmentTransaction ft;

    boolean click_pay_paytm = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_trackyourride, container, false);
        initViews(rootView);



        return rootView;
    }




    public void initViews(View view){

        mapFrag = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map2);
        mapFrag.getMapAsync(this);

        sharedPref = new SharedPref(getActivity());
        globalClass = (GlobalClass) getActivity().getApplicationContext();

        tv_pickup_address = (TextView) view.findViewById(R.id.tv_pickup_address);
        tv_drop_address = (TextView) view.findViewById(R.id.tv_drop_address);
        btn_help = (Button) view.findViewById(R.id.btn_help);

        tv_pickup_address.setText(sharedPref.getPickup_addr());
        tv_drop_address.setText(sharedPref.getDrop_addr());

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

            }
        }
        else {

        }




        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                helpOptionDialog();
            }
        });


    }


    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.key_trackyourride));


        if (click_pay_paytm){

            globalClass.DROP_LATLNG = null;


            Fragment fragment = new BookYourRide();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

        }

      //  alertDialog_rating.dismiss();
        super.onResume();
    }


    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();

        if (timer != null){
            timer.cancel();
        }

        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap mapp) {

        map = mapp;


        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(true);


        map.setTrafficEnabled(false);

        // map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                map.setMyLocationEnabled(true);
            }
        }
        else {

            map.setMyLocationEnabled(true);
        }


        callForDriverLocation();

    }

    private Timer timer;
    private void callForDriverLocation(){

        int delay = 1000; // delay for 1 sec.
        int period = 5000; // repeat every 5 sec.

        if (timer == null){

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                public void run()
                {
                    getDriverLocation();
                }
            }, delay, period);
        }

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    LatLng driverLoc1;
    LatLng driverLoc2;
    private void getDriverLocation(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<DriverResponce> call = apiService.getDriverLocation(sharedPref.getDriver_id());

        call.enqueue(new Callback<DriverResponce>() {
            @Override
            public void onResponse(Call<DriverResponce>call, retrofit2.Response<DriverResponce> response) {


                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            Log.d(Constants.TAG, "onResponse: " + response.body().getDriver_Details().getName());
                            double lati = 0.0, longi = 0.0;
                            try {

                                lati = Double.parseDouble(response.body().getDriver_Details().getLat());
                                longi = Double.parseDouble(response.body().getDriver_Details().getLng());

                            }catch (Exception e){
                                e.printStackTrace();
                            }


                            if (driverLoc2 == null && driverLoc1 == null){
                                driverLoc2 = new LatLng(lati, longi);
                                driverLoc1 = new LatLng(lati, longi);
                            }else {
                                driverLoc1 = driverLoc2;
                                driverLoc2 = new LatLng(lati, longi);
                            }

                            //drawMarker(driverLoc1);

                            // animateMarker(driverLoc1, driverLoc2, false);

                    /*MapUtils mapUtils = new MapUtils(getActivity());
                    mapUtils.drawPolyline2(map, driverLoc1.latitude, driverLoc1.longitude,
                            driverLoc2.latitude, driverLoc2.longitude, true);*/

                            float zoom = 16f;
                            LatLng target = new LatLng(lati, longi);

                            map.clear();

                            MarkerOptions markerOptions1 = new MarkerOptions();
                            markerOptions1.position(target);
                            markerOptions1.title("Driver Location");
                            markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(globalClass.cab_image));
                            map.addMarker(markerOptions1);

                            CameraPosition position = new CameraPosition.Builder().target(target).zoom(zoom).build();
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(position));



                        }else if (response.body().getStatus() == 2){




                        }else {




                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(Call<DriverResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });


    }

    //////////////////////////////////////////////////
    ///////////////


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            //do other stuff here
            Log.d(Constants.TAG, "Message = "+message);

            getFareDetails();




        }

    };


    /// Rating Dialog ....
    float rating_ = 0;
    LayerDrawable stars;
    AlertDialog alertDialog_rating;
    public void showRatingDialog(FareInfo fareInfo){
        rating_ = 0;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rating_dialog, null);
        dialogBuilder.setView(dialogView);

        if (alertDialog_rating != null){
            if (!alertDialog_rating.isShowing()){
                alertDialog_rating = dialogBuilder.create();
                alertDialog_rating.show();
            }
        }else {
            alertDialog_rating = dialogBuilder.create();
            alertDialog_rating.show();
        }


        RatingBar rating_driver = (RatingBar) dialogView.findViewById(R.id.rating_driver);
        stars = (LayerDrawable) rating_driver.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFA100"), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for empty stars

        final EditText edt_comment = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button btn_submit = (Button) dialogView.findViewById(R.id.btn_submit);

        rating_driver.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_ = rating;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postFeedback(alertDialog_rating, String.valueOf(rating_),
                        edt_comment.getText().toString());
            }
        });


        /////// Fare
        DecimalFormat precision = new DecimalFormat("0.00");

        RelativeLayout relative_guider_fee
                = (RelativeLayout) dialogView.findViewById(R.id.relative_guider_fee);
        TextView tv_fare = (TextView) dialogView.findViewById(R.id.tv_fare);
        TextView tv_taxAmount = (TextView) dialogView.findViewById(R.id.tv_taxAmount);
        TextView tv_guideFee = (TextView) dialogView.findViewById(R.id.tv_guideFee);
        TextView tv_totalFare = (TextView) dialogView.findViewById(R.id.tv_totalFare);

        if (fareInfo.getGuide_charges() == 0){
            relative_guider_fee.setVisibility(View.GONE);
        }
        tv_guideFee.setText(precision.format(fareInfo.getGuide_charges()));


        tv_totalFare.setText(
                precision.format(
                        Math.round(fareInfo.getTotal_fare() + fareInfo.getGuide_charges())));


        double baseFare = 0;
        if (fareInfo.getCommission_rate_type().matches("percent")){

            baseFare = fareInfo.getTotal_fare() /
                    (1 + fareInfo.getGst_rate() + fareInfo.getCommission_rate());

        }else {

            baseFare = (fareInfo.getTotal_fare()
                    +  (fareInfo.getCommission_rate() * (1 + fareInfo.getGst_rate())))
                    /
                    (1 + fareInfo.getGst_rate());

        }


        tv_fare.setText(precision.format(baseFare));
        tv_taxAmount.setText(precision.format(baseFare * fareInfo.getGst_rate()));


    }


    private void postFeedback(final AlertDialog alertDialog, String rating, String comment){

        Driver driver = sharedPref.getDriverData();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<FeedbackRatingResponse> call = apiService.feedbackRating(
                sharedPref.getUserId(),
                driver.getBooking_id(),
                rating,
                comment
        );

        Log.d(Constants.TAG, "getUserId: " + sharedPref.getUserId());
        Log.d(Constants.TAG, "getBookingId: " + driver.getBooking_id());

        call.enqueue(new Callback<FeedbackRatingResponse>() {
            @Override
            public void onResponse(Call<FeedbackRatingResponse>call, retrofit2.Response<FeedbackRatingResponse> response) {

                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_LONG).show();


                            alertDialog.dismiss();

                            sharedPref.setFreeForBooking(true);

                            if (sharedPref.getPaymentMode().matches(Constants.paymentMode_paytm)){

                                showPaytmPayDialog();

                            }else {

                                sharedPref.resetRideData();
                                globalClass.DROP_LATLNG = null;

                                Fragment fragment = new BookYourRide();
                                ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.container, fragment);
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                ft.commit();

                            }


                        }else if (response.body().getStatus() == 2){

                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }else {

                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<FeedbackRatingResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });

    }


    // Payment ...

    public void showPaytmPayDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pay_via_paytm, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


        final EditText edt_payee_number = (EditText) dialogView.findViewById(R.id.edt_payee_number);
        Button btn_pay_paytm = (Button) dialogView.findViewById(R.id.btn_pay_paytm);


        btn_pay_paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidationClass validationClass = new ValidationClass(getActivity());

                if (!validationClass.validateMobileNo(edt_payee_number)){

                    return;
                }else {

                    updatePaytmNumber(alertDialog, edt_payee_number.getText().toString());
                }

            }
        });



    }


    public void updatePaytmNumber(final AlertDialog alertDialog, String number){

        Driver driver = sharedPref.getDriverData();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<FeedbackRatingResponse> call = apiService.update_paytm_number(
                driver.getBooking_id(),
                number
        );

        Log.d(Constants.TAG, "getBookingId: " + driver.getBooking_id());

        call.enqueue(new Callback<FeedbackRatingResponse>() {
            @Override
            public void onResponse(Call<FeedbackRatingResponse>call, retrofit2.Response<FeedbackRatingResponse> response) {

                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                          //  Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                            click_pay_paytm = true;

                            sharedPref.resetRideData();
                            alertDialog.dismiss();
                            globalClass.DROP_LATLNG = null;


                            // https://play.google.com/store/apps/details?id=net.one97.paytm

                            try {
                                String url = "net.one97.paytm";
                                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(url);

                               // Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );

                                startActivity(launchIntent);

                            }
                            catch ( ActivityNotFoundException ex  ) {
                                Intent intent = new Intent( Intent.ACTION_VIEW,
                                                Uri.parse("market://details?id=net.one97.paytm"));
                                startActivity(intent);
                            }

                            /*Fragment fragment = new BookYourRide();
                            ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.container, fragment);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.commit();*/


                        }else{

                            Toast.makeText(getActivity(), "Something error. Try again", Toast.LENGTH_LONG).show();

                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<FeedbackRatingResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });
    }


    // Help ...

    String reason_text;
    int checkedId_;
    private void helpOptionDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.help_option_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        RadioGroup radioGroup1 = (RadioGroup) dialogView.findViewById(R.id.radioGroup1);
        Button bt_submit = (Button) dialogView.findViewById(R.id.bt_submit);
        final EditText edt_help = (EditText) dialogView.findViewById(R.id.edt_help);
        edt_help.setVisibility(View.GONE);


        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                checkedId_ = checkedId;


                if (checkedId == R.id.radio8){

                    edt_help.setVisibility(View.VISIBLE);

                }else {

                    edt_help.setVisibility(View.GONE);

                    if (isChecked){

                        reason_text = checkedRadioButton.getText().toString();

                    }else {

                        reason_text = "";
                    }
                }



            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkedId_ == R.id.radio8){

                    reason_text = edt_help.getText().toString();

                    if (reason_text.isEmpty()){

                        Toast.makeText(getActivity(), "Please write something", Toast.LENGTH_SHORT).show();

                    }else {

                        sendHelpMail();
                        alertDialog.dismiss();
                    }


                }else {

                    if (reason_text.isEmpty()){

                        Toast.makeText(getActivity(), "Please select one reason", Toast.LENGTH_SHORT).show();

                    }else {

                        sendHelpMail();
                        alertDialog.dismiss();
                    }
                }


            }
        });

    }

    private void sendHelpMail(){


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelBooking> call = apiService.helpMail(
                sharedPref.getUserId(),
                sharedPref.getBookingId(),
                reason_text
        );

        call.enqueue(new retrofit2.Callback<CancelBooking>() {
            @Override
            public void onResponse(Call<CancelBooking>call, retrofit2.Response<CancelBooking> response) {


                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());


                        if (response.body().getStatus() == 1){

                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }else if (response.body().getStatus() == 2){

                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }else {

                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(Call<CancelBooking>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });


    }


    // Get fare details ...

    private void getFareDetails(){


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Driver driver = sharedPref.getDriverData();
        Call<FareDetails> call = apiService.get_fare_details(driver.getBooking_id()
        );

        call.enqueue(new retrofit2.Callback<FareDetails>() {
            @Override
            public void onResponse(Call<FareDetails>call, retrofit2.Response<FareDetails> response) {


                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());


                        if (response.body().getStatus() == 1){

                            showRatingDialog(response.body().getFareInfo());

                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(Call<FareDetails>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });


    }




}
