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
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
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
import com.google.android.gms.maps.model.Marker;
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
import com.project.sketch.ugo.screen.CitytaxiCabBooking;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.MapUtils;
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

    float waiting_fee ;
    float start_rotation;
    String TAG="Rating";
    private GoogleApiClient googleApiClient;
    private GoogleMap map = null;
    float toRotation;
    SupportMapFragment mapFrag;
    LocationManager lm;
    Geocoder geoCoder;
    double grand_total;
    TextView tv_pickup_address, tv_drop_address;
    Button btn_help;

    SharedPref  sharedPref;
    GlobalClass globalClass;
    Marker m1;
    boolean isMarkerRotating=false;
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
      //  moveVechile(m1, location);
      //  Log.d(TAG, "onLocationChanged: "+location);
       // Log.d(TAG, "onLocationChanged: "+m1);
       // rotateMarker(m1, location.getBearing(), start_rotation);

    }

    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
    public void moveVechile(final Marker myMarker, final Location finalPosition) {

        final LatLng startPosition = myMarker.getPosition();
        Log.d(TAG, "moveVechile: "+myMarker);
        Log.d(TAG, "moveVechile: "+finalPosition);
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });
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
                           // moveVechile(m1, driverLoc2);
                            //drawMarker(driverLoc1);

                            // animateMarker(driverLoc1, driverLoc2, false);

                    /*MapUtils mapUtils = new MapUtils(getActivity());
                    mapUtils.drawPolyline2(map, driverLoc1.latitude, driverLoc1.longitude,
                            driverLoc2.latitude, driverLoc2.longitude, true);*/

                            float zoom = 16f;
                            LatLng target = globalClass.PICKUP_LATLNG;

                           // map.clear();

                            MarkerOptions markerOptions1 = new MarkerOptions();
                            markerOptions1.position(target);
                            markerOptions1.title("Driver Location");
                            markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(globalClass.cab_image));
                            map.addMarker(markerOptions1);
                            map.moveCamera(CameraUpdateFactory.newLatLng(target));
                            map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(map.getCameraPosition().target)
                                    .zoom(16)
                                    .bearing(30)
                                    .tilt(45)
                                    .build()));

                            MapUtils mapUtils = new MapUtils(getActivity());
                            mapUtils.drawPolyline(map, true);

                            bearingBetweenLocations(driverLoc1,driverLoc2);
                            toRotation = (float) bearingBetweenLocations(driverLoc1, driverLoc2);
                            Log.d("TAG", "onResponse: " +toRotation);
                            rotateMarker(m1, toRotation);


                        }
                        else if (response.body().getStatus() == 2){




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
    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
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

        Log.d(Constants.TAG, "Fare Info: "+fareInfo.getGst_rate());
        double total_fare=fareInfo.getTotal_fare();
        double gst=fareInfo.getGst_fair();
        double coupon=fareInfo.getCoupon_amount();
        double subtotal=fareInfo.getSub_total_fare();
        double commission=fareInfo.getCommission_fair();
        grand_total = subtotal + commission;

        String stringdouble= Double.toString(total_fare);
        String stringgstfair= Double.toString(gst);
        String subtotal1= Double.toString(grand_total);
        String coupon1= Double.toString(coupon);

        Log.d(TAG, "coupon1: "+coupon1);


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


        RatingBar rating_driver =  dialogView.findViewById(R.id.rating_driver);
        stars = (LayerDrawable) rating_driver.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFA100"), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for empty stars

        final EditText edt_comment =  dialogView.findViewById(R.id.edt_comment);
        Button btn_submit =  dialogView.findViewById(R.id.btn_submit);

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



        TextView tv_fare =  dialogView.findViewById(R.id.tv_fare);
        TextView tv_taxAmount =  dialogView.findViewById(R.id.tv_taxAmount);
      //  TextView tv_guideFee = dialogView.findViewById(R.id.tv_guideFee);
        TextView tv_totalFare =  dialogView.findViewById(R.id.tv_totalFare);
        TextView coupon_text =  dialogView.findViewById(R.id.tv_coupon);
        TextView tv_waitFee = dialogView.findViewById(R.id.tv_waitFee);

        tv_totalFare.setText(stringdouble);
        tv_taxAmount.setText(stringgstfair);
        tv_waitFee.setText(fareInfo.getWaiting_fair());
        tv_fare.setText(subtotal1);
        coupon_text.setText(coupon1);
        Log.d(TAG, "waiting_fee: "+waiting_fee);





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

                            if (sharedPref.getPaymentMode().matches(Constants.paymentMode_Wallet)){

                             //   showPaytmPayDialog();

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


        final EditText edt_payee_number =  dialogView.findViewById(R.id.edt_payee_number);
        Button btn_pay_paytm = dialogView.findViewById(R.id.btn_pay_paytm);


        btn_pay_paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidationClass validationClass = new ValidationClass(getActivity());

                if (!validationClass.validateMobileNo(edt_payee_number)){

                    return;
                }else {

                  //  updatePaytmNumber(alertDialog, edt_payee_number.getText().toString());
                }

            }
        });



    }


/*
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

                            */
/*Fragment fragment = new BookYourRide();
                            ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.container, fragment);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.commit();*//*



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
*/


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
        Log.d(Constants.TAG, "Book ID: "+driver.getBooking_id());
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
                            Log.d(Constants.TAG, "Fare Info: "+response.body().getFareInfo());
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
