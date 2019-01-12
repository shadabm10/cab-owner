package com.project.sketch.ugo.screen;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel2.ConfirmBookResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;
import com.project.sketch.ugo.httpRequest.apiModel2.DriverResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.EstimateData;
import com.project.sketch.ugo.httpRequest.apiModel2.EstimateValueResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.SpecialFareEstimateResponse;
import com.project.sketch.ugo.httpRequest.apiModel4.CancelBooking;
import com.project.sketch.ugo.httpRequest.apiModel4.FeedbackRatingResponse;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.MapUtils;
import com.project.sketch.ugo.utils.SharedPref;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import static com.project.sketch.ugo.utils.Constants.API_KEY;

/**
 * Created by Developer on 1/13/18.
 */

public class CitytaxiCabBooking extends AppCompatActivity implements
        OnMapReadyCallback {

    Toolbar toolbar;
    private GoogleMap map = null;
    SupportMapFragment mapFrag;
    TextView tv_pickup_address, tv_drop_address, tv_distance, tv_estimate_time,
            tv_estimate_fare, tv_paymentMode, tv_details;
    RelativeLayout rl_back, rl_confirm_booking;
    RelativeLayout rl_addressBar, rl_in_1, rl_in_2, rl_call_driver,
            rl_cancel_booking, rl_loader;
    CheckBox checkbox_guider;
    RelativeLayout rl_paymentMode;

    CircleImageView profile_image;
    TextView tv_driver_name, tv_car_name_number, tv_otp, tv_reached_time;
    RatingBar rating_driver;
    RelativeLayout rl_coupon;

    LayerDrawable stars;
    GlobalClass globalClass;
    SharedPref sharedPref;

    String booking_id;
    String guide_option = "N";
    String driver_phone_number;
    String special_booking_type;

    Animation show_animation, hide_animation;

    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    Dialog progressDialog;
    private ProgressDialog pDialog;

    private EstimateData estimateData;

    private static final int ACTIVITY_RESULT_CODE_Fare = 100;
    private static final int ACTIVITY_RESULT_CODE_PaymentMode = 200;

    boolean isCancelByDriver = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citytaxi_booking_screen);

        initViews();



    }

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.registerReceiver(mMessageReceiver, new IntentFilter(Constants.key_confirmbooking));
        estimateData();

        super.onResume();
    }

    public void initViews(){

        globalClass = (GlobalClass) getApplicationContext();
        sharedPref = new SharedPref(this);

        pDialog  = new ProgressDialog(CitytaxiCabBooking.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Please wait...");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_confirm_booking));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map3);
        mapFrag.getMapAsync(this);


        tv_pickup_address = (TextView) findViewById(R.id.tv_pickup_address);
        tv_drop_address = (TextView) findViewById(R.id.tv_drop_address);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_estimate_time = (TextView) findViewById(R.id.tv_estimate_time);
        tv_estimate_fare = (TextView) findViewById(R.id.tv_estimate_fare);
        tv_paymentMode = (TextView) findViewById(R.id.tv_paymentMode);
        tv_details = (TextView) findViewById(R.id.tv_details);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_confirm_booking = (RelativeLayout) findViewById(R.id.rl_confirm_booking);
        rl_addressBar = (RelativeLayout) findViewById(R.id.rl_addressBar);
        checkbox_guider = (CheckBox) findViewById(R.id.checkbox_guider);
        checkbox_guider.setChecked(false);
        tv_reached_time = (TextView) findViewById(R.id.tv_reached_time);
        tv_reached_time.setVisibility(View.GONE);


        rl_paymentMode = (RelativeLayout) findViewById(R.id.rl_paymentMode);
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_coupon);
        rl_in_1 = (RelativeLayout) findViewById(R.id.rl_in_1);
        rl_in_1.setVisibility(View.GONE);
        rl_in_2 = (RelativeLayout) findViewById(R.id.rl_in_2);
        rl_in_2.setVisibility(View.GONE);
        rl_loader = (RelativeLayout) findViewById(R.id.rl_loader);
        rl_loader.setVisibility(View.VISIBLE);
        rl_call_driver = (RelativeLayout) findViewById(R.id.rl_call_driver);
        rl_cancel_booking = (RelativeLayout) findViewById(R.id.rl_cancel_booking);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        tv_driver_name = (TextView) findViewById(R.id.tv_driver_name);
        tv_car_name_number = (TextView) findViewById(R.id.tv_car_name_number);
        tv_otp = (TextView) findViewById(R.id.tv_otp);
        rating_driver = (RatingBar) findViewById(R.id.rating_driver);

        stars = (LayerDrawable) rating_driver.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFA100"), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for empty stars


        show_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in_animation);
        hide_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out_animation);

        tv_pickup_address.setText(globalClass.PICUP_ADDRESS);
        tv_drop_address.setText(globalClass.DROP_ADDRESS);

        sharedPref.setPaymentMode(Constants.paymentMode_cash);
        tv_paymentMode.setText("Cash");


        buttonOnClick();

        setupLoader();

        if (globalClass.isSpecialFareSelect){
            getEstimateValueSpecialFare();
        }else {
            special_booking_type = "city_taxi";
            getEstimateValue();
        }



        /*if (sharedPref.isDriverAccept()){
            if (!sharedPref.isReachingThePickupLocation()){

                rl_in_1.setVisibility(View.GONE);
                rl_in_2.setVisibility(View.VISIBLE);

                setDriverData(sharedPref.getDriverData());

                tv_pickup_address.setText(sharedPref.getPickup_addr());
                tv_drop_address.setText(sharedPref.getDrop_addr());

            }

        }else {

            tv_pickup_address.setText(globalClass.PICUP_ADDRESS);
            tv_drop_address.setText(globalClass.DROP_ADDRESS);

            setupLoader();
            getEstimateValue();

        }*/



    }

    public void estimateData(){

        try{

            if (globalClass.getCoupon_applied().matches("Y")){

                if (guide_option.matches("Y")){

                    float totalWithGuideMin = estimateData.getEstimate_fare()
                            + estimateData.getEstimate_guide_charge()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    float totalWithGuideMax = estimateData.getEstimate_fare_maximum()
                            + estimateData.getEstimate_guide_charge()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithGuideMin
                            +" - ₹ "+(int)totalWithGuideMax);

                }else {

                    float totalWithOutGuideMin = estimateData.getEstimate_fare()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    float totalWithOutGuideMax = estimateData.getEstimate_fare_maximum()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithOutGuideMin
                            +" - ₹ "+(int)totalWithOutGuideMax);


                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                globalClass.setCoupon_applied("N");
                globalClass.setCoupon_code("");
                globalClass.setCoupon_amount(0);

                finish();
                if (timer != null){
                    timer.cancel();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        globalClass.setCoupon_applied("N");
        globalClass.setCoupon_code("");
        globalClass.setCoupon_amount(0);

        finish();
        if (timer != null){
            timer.cancel();
        }
        super.onBackPressed();

    }

    @Override
    public void onMapReady(GoogleMap mapp) {

        map = mapp;

        map.setPadding(10, 60, 10, 10);

        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(true);


        map.setTrafficEnabled(false);
        map.setIndoorEnabled(false);
        map.setBuildingsEnabled(false);



        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

      //  double lat1 = 22.567438;
      //  double long1 = 88.375214;

      //  double lat2 = 22.586448;
      //  double long2 = 88.339047;

        /*if (sharedPref.isDriverAccept()) {
            if (!sharedPref.isReachingThePickupLocation()) {

                getDriverLocation();

            }

        }else {


        }*/



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                map.setMyLocationEnabled(true);
            }
        }
        else {

            map.setMyLocationEnabled(true);
        }


        LatLng myLoc = globalClass.PICKUP_LATLNG;

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(myLoc);
        markerOptions1.icon( BitmapDescriptorFactory.fromBitmap(globalClass.cab_image));
        map.addMarker(markerOptions1);

        map.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mapp.getCameraPosition().target)
                .zoom(16)
                .bearing(30)
                .tilt(45)
                .build()));

        MapUtils mapUtils = new MapUtils(CitytaxiCabBooking.this);
        mapUtils.drawPolyline(map, false);



    }


    public void buttonOnClick(){

        rl_paymentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_mode = new Intent(CitytaxiCabBooking.this, PaymentMode.class);
                startActivityForResult(intent_mode, ACTIVITY_RESULT_CODE_PaymentMode);
            }
        });


        tv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_cost = new Intent(CitytaxiCabBooking.this, CostFareCalculation.class);
                intent_cost.putExtra("data", estimateData);
                intent_cost.putExtra("guide_option", guide_option);
                startActivityForResult(intent_cost, ACTIVITY_RESULT_CODE_Fare);

            }
        });


        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (timer != null){
                    timer.cancel();
                }

            }
        });


        rl_confirm_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // callForBooking();
                //guiderConfirmDialog();

                if (globalClass.Later_Ride.matches("N")){
                    progressDialog.show();
                    callForBooking();
                }else if (globalClass.Later_Ride.matches("Y")){
                    pDialog.show();

                    confirmBookingCity_taxi();

                }



            }
        });


        rl_cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelReasonDialog();

            }
        });


        checkbox_guider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    guide_option = "Y";

                    float totalWithGuideMin = estimateData.getEstimate_fare()
                            + estimateData.getEstimate_guide_charge()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    float totalWithGuideMax = estimateData.getEstimate_fare_maximum()
                            + estimateData.getEstimate_guide_charge()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithGuideMin
                            +" - ₹ "+(int)totalWithGuideMax);

                }else {

                    guide_option = "N";

                    float totalWithOutGuideMin = estimateData.getEstimate_fare()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    float totalWithOutGuideMax = estimateData.getEstimate_fare_maximum()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();


                    tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithOutGuideMin
                            +" - ₹ "+(int)totalWithOutGuideMax);
                }


            }
        });


        rl_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_coupon = new Intent(CitytaxiCabBooking.this, CouponScreen.class);
                startActivity(intent_coupon);

            }
        });

    }

    public void setupLoader(){

        progressDialog = new Dialog(this, android.R.style.Theme_Translucent);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setContentView(R.layout.driver_search_loader);
        progressDialog.setCancelable(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == ACTIVITY_RESULT_CODE_Fare) {
                guide_option = data.getStringExtra("guide_option");
                if (guide_option.matches("Y")){
                    checkbox_guider.setChecked(true);
                }else {
                    checkbox_guider.setChecked(false);
                }
            }else if (requestCode == ACTIVITY_RESULT_CODE_PaymentMode){

                if (sharedPref.getPaymentMode().matches(Constants.paymentMode_cash)){
                    tv_paymentMode.setText("Cash");
                }else if (sharedPref.getPaymentMode().matches(Constants.paymentMode_paytm)){
                    tv_paymentMode.setText("Paytm");
                }else if (sharedPref.getPaymentMode().matches(Constants.paymentMode_jiomoney)){
                    tv_paymentMode.setText("Jio Money");
                }

            }

        }else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }



    //////////////////////////////////////////////////
    /// Get estimate time and fare ....


    private void getEstimateValue(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Log.d(Constants.TAG, "test: " + globalClass.PICKUP_LATLNG.latitude);
        Log.d(Constants.TAG, "test: " + globalClass.PICKUP_LATLNG.longitude);
        Log.d(Constants.TAG, "test: " + globalClass.DROP_LATLNG.latitude);
        Log.d(Constants.TAG, "test: " + globalClass.DROP_LATLNG.longitude);
        Log.d(Constants.TAG, "test: " + globalClass.Selected_Cab_Id);


        Call<EstimateValueResponce> call = apiService.getEstimateCost(
                globalClass.PICKUP_LATLNG.latitude,
                globalClass.PICKUP_LATLNG.longitude,
                globalClass.DROP_LATLNG.latitude,
                globalClass.DROP_LATLNG.longitude,
                globalClass.Selected_Cab_Id
        );

        call.enqueue(new Callback<EstimateValueResponce>() {
            @Override
            public void onResponse(Call<EstimateValueResponce>call,
                                   retrofit2.Response<EstimateValueResponce> response) {

                try {

                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            estimateData = response.body().getEstimateData();

                            Log.d(Constants.TAG, "onResponse: " + response.body().getEstimateData().getEstimate_fare());

                            float dis = response.body().getEstimateData().getEstimate_distance_value()/1000;
                            tv_distance.setText("Distance : "+ dis + " Km.");

                            int time = response.body().getEstimateData().getEstimate_time_value();
                            tv_estimate_time.setText("Aprx. Time : "+secondMinHour(time));



                            float totalWithGuideMin = estimateData.getEstimate_fare()
                                    + estimateData.getTotal_fees_and_charges();
                            float totalWithGuideMax = estimateData.getEstimate_fare_maximum()
                                    + estimateData.getTotal_fees_and_charges();

                            tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithGuideMin +" - ₹ "+(int)totalWithGuideMax);

                            //tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)response.body().getEstimateData().getEstimate_fare());

                            rl_loader.setAnimation(hide_animation);
                            rl_loader.setVisibility(View.GONE);

                            rl_in_1.setAnimation(show_animation);
                            rl_in_1.setVisibility(View.VISIBLE);


                        }else if (response.body().getStatus() == 2){


                        }else {


                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<EstimateValueResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });

    }

    private void getEstimateValueSpecialFare(){

        Log.d(Constants.TAG, "Pickup_City > "+globalClass.Pickup_City);
        Log.d(Constants.TAG, "DropOff_City > "+globalClass.DropOff_City);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<SpecialFareEstimateResponse> call = apiService.estimate_specialfare(
                globalClass.PICKUP_LATLNG.latitude,
                globalClass.PICKUP_LATLNG.longitude,
                globalClass.DROP_LATLNG.latitude,
                globalClass.DROP_LATLNG.longitude,
                globalClass.Selected_Cab_Id,
                globalClass.Pickup_City,
                globalClass.DropOff_City
        );

        call.enqueue(new Callback<SpecialFareEstimateResponse>() {
            @Override
            public void onResponse(Call<SpecialFareEstimateResponse>call, retrofit2.Response<SpecialFareEstimateResponse> response) {

                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            special_booking_type = response.body().getSpecialFareType();

                            estimateData = response.body().getEstimateData();

                            Log.d(Constants.TAG, "onResponse: " + response.body().getEstimateData().getEstimate_fare());

                            float dis = response.body().getEstimateData().getEstimate_distance_value()/1000;
                            tv_distance.setText("Distance : "+(int)Math.round(dis)+ " Km.");

                            int time = response.body().getEstimateData().getEstimate_time_value();
                            tv_estimate_time.setText("Aprx. Time : "+secondMinHour(time));



                            float totalWithOutGuideMin = estimateData.getEstimate_fare()
                                    + estimateData.getTotal_fees_and_charges()
                                    - globalClass.getCoupon_amount();

                            float totalWithOutGuideMax = estimateData.getEstimate_fare_maximum()
                                    + estimateData.getTotal_fees_and_charges()
                                    - globalClass.getCoupon_amount();

                            tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithOutGuideMin +" - ₹ "+(int)totalWithOutGuideMax);

                            //tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)response.body().getEstimateData().getEstimate_fare());

                            rl_loader.setAnimation(hide_animation);
                            rl_loader.setVisibility(View.GONE);

                            rl_in_1.setAnimation(show_animation);
                            rl_in_1.setVisibility(View.VISIBLE);


                        }else if (response.body().getStatus() == 2){


                        }else {


                        }


                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<SpecialFareEstimateResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });

    }


    private String secondMinHour(int i){
        long hours = i / 3600;
        long minutes = (i % 3600) / 60;
        long seconds = i % 60;
        String time = String.format("%02d:%02d", hours, minutes);
        return time;
    }

    //////////////////////////////////////////////////
    /// Confirm booking ....


    private int counter;
    private Timer timer;
    private void callForBooking(){

        final int delay = 1000; // delay for 1 sec.
        final int period = 15000; // repeat every 14 sec.
        timer = new Timer();
        counter = 1;

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run()
            {
                if (counter < 4){
                    counter++;

                    Log.d(Constants.TAG, "call booking");

                    confirmBookingCity_taxi();

                   // confirmbooking();

                }else {

                    timer.cancel();

                    progressDialog.dismiss();

                }

            }
        }, delay, period);


    }

    private void confirmBookingCity_taxi(){

        Log.d(Constants.TAG, "1  - "+ sharedPref.getUserId());
        Log.d(Constants.TAG, "2  - "+special_booking_type);
        Log.d(Constants.TAG, "3  - "+globalClass.PICUP_ADDRESS);
        Log.d(Constants.TAG, "4  - "+globalClass.PICKUP_LATLNG.latitude
                +"  "+ globalClass.PICKUP_LATLNG.longitude);
        Log.d(Constants.TAG, "5  - "+ globalClass.DROP_ADDRESS);
        Log.d(Constants.TAG, "6  - "+globalClass.DROP_LATLNG.latitude
                +"  "+ globalClass.DROP_LATLNG.longitude);
        Log.d(Constants.TAG, "7  - "+globalClass.Selected_Cab_Id);
        Log.d(Constants.TAG, "8  - "+sharedPref.getPaymentMode());
        Log.d(Constants.TAG, "9  - "+guide_option);
        Log.d(Constants.TAG, "10  - "+globalClass.Later_Ride);
        Log.d(Constants.TAG, "10  - "+globalClass.getCoupon_applied());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ConfirmBookResponce> call = apiService.confirmBooking(
                sharedPref.getUserId(),
                special_booking_type,
                "",
                globalClass.PICUP_ADDRESS,
                globalClass.PICKUP_LATLNG.latitude,
                globalClass.PICKUP_LATLNG.longitude,
                globalClass.DROP_ADDRESS,
                globalClass.DROP_LATLNG.latitude,
                globalClass.DROP_LATLNG.longitude,
                globalClass.Selected_Cab_Id,
                sharedPref.getPaymentMode(),
                guide_option,
                globalClass.Booking_later_time,
                globalClass.Booking_later_time,
                globalClass.Later_Ride,
                "",
                "",
                globalClass.getCoupon_applied(),
                globalClass.getCoupon_code(),
                globalClass.getCoupon_amount()
        );


        call.enqueue(new Callback<ConfirmBookResponce>() {
            @Override
            public void onResponse(Call<ConfirmBookResponce>call, retrofit2.Response<ConfirmBookResponce> response) {

                try {

                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            counter = 4;

                            Log.d(Constants.TAG, "onResponse: " + response.body().getDriver().getName());

                            rl_in_1.startAnimation(hide_animation);
                            rl_in_1.setVisibility(View.GONE);


                            rl_in_2.startAnimation(show_animation);
                            rl_in_2.setVisibility(View.VISIBLE);

                            map.clear();

                            progressDialog.dismiss();

                            globalClass.setDriver(response.body().getDriver());
                            sharedPref.saveDriverInfo(response.body().getDriver());

                            setDriverData(globalClass.getDriver());

                            callForDriverLocation();

                        }else if (response.body().getStatus() == 3){

                            if (counter > 3){

                                progressDialog.dismiss();

                                Toast.makeText(CitytaxiCabBooking.this,
                                        "Driver not available for this time",
                                        Toast.LENGTH_LONG).show();

                            }

                        } else if (response.body().getStatus() == 4){
                            pDialog.dismiss();

                            dialogForLaterBooking();

                        }else if (response.body().getStatus() == 5){
                            pDialog.dismiss();

                            Toast.makeText(CitytaxiCabBooking.this,
                                    "Booking not confirmed.\nPlease try again.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            pDialog.dismiss();

                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();

                    if (counter > 3){

                        progressDialog.dismiss();

                        Toast.makeText(CitytaxiCabBooking.this,
                                "Driver not available for this time",
                                Toast.LENGTH_LONG).show();

                        Toast.makeText(CitytaxiCabBooking.this,
                                "Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ConfirmBookResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });


    }


///////////////////////////// test

    public void confirmbooking(){


        String url = ApiClient.BASE_URL + Constants.confirmbooking;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.setSSLSocketFactory(
                new SSLSocketFactory(getSslContext(),
                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));

        params.put(Constants.id, sharedPref.getUserId());
        params.put(Constants.booking_type, special_booking_type);
        params.put(Constants.outstation_type, "");
        params.put(Constants.pickup_address, globalClass.PICUP_ADDRESS);
        params.put(Constants.pickup_latitude, globalClass.PICKUP_LATLNG.latitude);
        params.put(Constants.pickup_longitude, globalClass.PICKUP_LATLNG.longitude);
        params.put(Constants.drop_address, globalClass.DROP_ADDRESS);
        params.put(Constants.drop_latitude, globalClass.DROP_LATLNG.latitude);
        params.put(Constants.drop_longitude, globalClass.DROP_LATLNG.longitude);
        params.put(Constants.cab_variant_id, globalClass.Selected_Cab_Id);
        params.put(Constants.payment_mode, sharedPref.getPaymentMode());
        params.put(Constants.guide_option, guide_option);
        params.put(Constants.scheduled_date, globalClass.Booking_later_time);
        params.put(Constants.scheduled_time, globalClass.Booking_later_time);
        params.put(Constants.later, globalClass.Later_Ride);
        params.put(Constants.outstation_trip_starttime, "");
        params.put(Constants.rental_distance_package, "");
        params.put(Constants.coupon_applied, globalClass.getCoupon_applied());
        params.put(Constants.coupon_code, globalClass.getCoupon_code());
        params.put(Constants.coupon_amount, globalClass.getCoupon_amount());



        Log.d(Constants.TAG , "update_profile - " + url);
        Log.d(Constants.TAG , "update_profile - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_profile- " + response.toString());

                if (response != null) {
                    try {






                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {

            }

        });


    }


    public SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }

/////////////////////////////

    private void dialogForLaterBooking(){

        String msg = "Your booking is confirmed.\nDriver will contact you on your time.";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gain Cabs")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPref.setFreeForBooking(true);
                        dialog.dismiss();
                        finish();
                        globalClass.DROP_ADDRESS = "";

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void setDriverData(final Driver driverData){

        booking_id = driverData.getBooking_id();
        sharedPref.saveBookingId(booking_id);
        sharedPref.saveRiding(true);

        sharedPref.saveDriver_id(driverData.getDid());
        sharedPref.savePickup_addr(globalClass.PICUP_ADDRESS);
        sharedPref.saveDrop_addr(globalClass.DROP_ADDRESS);

        tv_driver_name.setText(driverData.getName());
        tv_car_name_number.setText(driverData.getCar_name()+"\n"+driverData.getCar_number());
        tv_otp.setText("OTP : "+driverData.getOtp());

        if (driverData.getRating() == null || driverData.getRating().equals(null)){

        }else {
            rating_driver.setRating(Float.parseFloat(driverData.getRating()));
        }

        Picasso.with(this).load(driverData.getCar_image()).error(R.mipmap.avatar).into(profile_image, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                //  Log.d("TAG", "onSuccess");
            }
            @Override
            public void onError() {
                //  Toast.makeText(mactivity, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });


        rl_call_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // driverData.getPhone()
                driver_phone_number = driverData.getPhone();
                checkPermission();
            }
        });

    }

    //////////////////////////////////////////////////
    /// Get Driver location ....

    private Timer timer2;
    private void callForDriverLocation(){

        int delay = 1000; // delay for 1 sec.
        int period = 5000; // repeat every 5 sec.

        if (timer2 == null){

            timer2 = new Timer();
            timer2.scheduleAtFixedRate(new TimerTask()
            {
                public void run()
                {
                    getDriverLocation();
                }
            }, delay, period);
        }

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

                            getReachingTime(lati, longi);


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


    String requestUrl;

    public void getReachingTime(double lati, double longi){

        requestUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + globalClass.PICKUP_LATLNG.latitude +","
                + globalClass.PICKUP_LATLNG.longitude
                + "&destinations=" + lati +"," + longi
                + "&mode=driving&language=EN"
                + "&key=" + API_KEY;

        try {
            /*requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + lat11 + "," + long11 + "&"
                    + "destination=" + lat22 + "," + long22 + "&"
                    + "key=" + context.getResources().getString(R.string.map_api_key);*/

            Log.d(Constants.TAG, requestUrl);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (response != null){

                                Log.d(Constants.TAG, response + "");

                                try {
                                    JSONArray jsonArray = response.getJSONArray("rows");
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    JSONArray jsonA_ele = jsonObject.getJSONArray("elements");
                                    JSONObject obj_ele = jsonA_ele.getJSONObject(0);
                                    if (obj_ele.getString("status").matches("OK")){

                                        JSONObject duration_obj = obj_ele.getJSONObject("duration");

                                        String text = duration_obj.getString("text");

                                        tv_reached_time.setText(text+"\naway");
                                        tv_reached_time.setVisibility(View.VISIBLE);
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(Constants.TAG, error + "");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(CitytaxiCabBooking.this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    ///////////////////////////////////////////////////
    /// Cancel booking ....

    String reason_text = "";
    int checkedId_;
    public void cancelReasonDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.user_cancel_booking_reason, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        RadioGroup radioGroup1 = (RadioGroup) dialogView.findViewById(R.id.radioGroup1);
        Button bt_cancel_booking = (Button) dialogView.findViewById(R.id.bt_cancel_booking);
        final EditText edt_reason = (EditText) dialogView.findViewById(R.id.edt_reason);
        edt_reason.setVisibility(View.GONE);


        reason_text = "";
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                checkedId_ = checkedId;

                if (checkedId == R.id.radio9){

                    edt_reason.setVisibility(View.VISIBLE);

                }else {

                    edt_reason.setVisibility(View.GONE);

                    if (isChecked){

                        reason_text = checkedRadioButton.getText().toString();

                    }else {

                        reason_text = "";
                    }

                }

            }
        });

        bt_cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkedId_ == R.id.radio9){

                    reason_text = edt_reason.getText().toString();

                    if (reason_text.isEmpty()){

                        Toast.makeText(CitytaxiCabBooking.this, "Please enter reason", Toast.LENGTH_SHORT).show();

                    }else {

                        cancelBooking(reason_text);
                    }


                }else {

                    if (reason_text.isEmpty()){

                        Toast.makeText(CitytaxiCabBooking.this, "Please select one reason", Toast.LENGTH_SHORT).show();

                    }else {

                        cancelBooking(reason_text);
                    }
                }

            }
        });
    }

    public void cancelBooking(String cancel_reason){

        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelBooking> call = apiService.cancelBooking(
                sharedPref.getUserId(),
                booking_id,
                cancel_reason
        );

        call.enqueue(new Callback<CancelBooking>() {
            @Override
            public void onResponse(Call<CancelBooking>call, retrofit2.Response<CancelBooking> response) {

                try{

                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        reason_text = "";

                        if (response.body().getStatus() == 1){

                            Toast.makeText(CitytaxiCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                            globalClass.is_Cancel = true;

                            pDialog.dismiss();

                            finish();

                        }else if (response.body().getStatus() == 2){
                            pDialog.dismiss();

                            Toast.makeText(CitytaxiCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }else {
                            pDialog.dismiss();

                            Toast.makeText(CitytaxiCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

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
                pDialog.dismiss();

            }
        });


    }


    ////////////////////////////////////////////////////

    /// Rating Dialog ....
    float rating_ = 0;

    public void showRatingDialog(){
        rating_ = 0;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rating_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


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

                postFeedback(String.valueOf(rating_), edt_comment.getText().toString());
            }
        });

    }


    private void postFeedback(String rating, String comment){

        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<FeedbackRatingResponse> call = apiService.feedbackRating(
                sharedPref.getUserId(),
                booking_id,
                rating,
                comment
        );

        call.enqueue(new Callback<FeedbackRatingResponse>() {
            @Override
            public void onResponse(Call<FeedbackRatingResponse>call, retrofit2.Response<FeedbackRatingResponse> response) {

                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            Toast.makeText(CitytaxiCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                            finish();

                        }else if (response.body().getStatus() == 2){

                            Toast.makeText(CitytaxiCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }else {

                            Toast.makeText(CitytaxiCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }

                        pDialog.dismiss();


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


    ////////////////////////////////////////////////////
    // response from FCM


    public CitytaxiCabBooking() {
        super();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            int type = intent.getIntExtra("type", 0);
            //do other stuff here
            Log.d(Constants.TAG, "Message = "+message);
            Log.d(Constants.TAG, "type = "+type);


            if (type == 2){
                // start trip

                finish();

            }else if (type == 3){
                // end trip

                showRatingDialog();

            }else if (type == 4){
                // reach driver on your pickup address

                rl_cancel_booking.setVisibility(View.GONE);

            }else if (type == 5){

                isCancelByDriver = true;

                rl_in_2.startAnimation(hide_animation);
                rl_in_2.setVisibility(View.GONE);

                rl_in_1.startAnimation(show_animation);
                rl_in_1.setVisibility(View.VISIBLE);

                rl_cancel_booking.setVisibility(View.VISIBLE);



                map.clear();
                tv_reached_time.setVisibility(View.GONE);

                if (timer2 != null)
                timer2.cancel();

                drawPolyline();


                String msg = getResources().getString(R.string.msg_cancelDriver);
                AlertDialog alertDialog =
                        new AlertDialog.Builder(CitytaxiCabBooking.this).create();
                alertDialog.setTitle("Gain Cabs");
                alertDialog.setMessage(msg);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();


            }


        }

    };

    //////////////////////////////////////////////////////
    //// Check call permission ...
    private static final int REQUEST_CODE_CALL_PHONE_PERMISSIONS = 1122;

    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(CitytaxiCabBooking.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CALL_PHONE);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) CitytaxiCabBooking.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_CALL_PHONE_PERMISSIONS);
            return false;
        } else {

            callDriver();

        }


        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CALL_PHONE_PERMISSIONS:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    //list is still empty

                    callDriver();

                } else {

                    checkPermission();
                    // Permission Denied

                }
                break;
        }
    }

    private void callDriver(){

        try {

            Intent myIntent = new Intent(Intent.ACTION_CALL);
            String phNum = "tel:" + driver_phone_number;
            myIntent.setData(Uri.parse(phNum));
            startActivity( myIntent);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }


    ///////////////////////////////

    public void drawPolyline(){

        LatLng myLoc = globalClass.PICKUP_LATLNG;

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(myLoc);
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(globalClass.cab_image));
        map.addMarker(markerOptions1);

        map.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(map.getCameraPosition().target)
                .zoom(16)
                .bearing(30)
                .tilt(45)
                .build()));

        MapUtils mapUtils = new MapUtils(CitytaxiCabBooking.this);
        mapUtils.drawPolyline(map, false);

    }



    ////////////////////////////////////////////////////

    //This methos is used to move the marker of each car smoothly when there are any updates of their position
    public void animateMarker(final LatLng startPosition, final LatLng toPosition,
                              final boolean hideMarker) {


        final Marker marker = map.addMarker(new MarkerOptions()
                .position(startPosition)
                //.title(mCarParcelableListCurrentLation.get(position).mCarName)
                //.snippet(mCarParcelableListCurrentLation.get(position).mAddress)
                .icon(BitmapDescriptorFactory.fromBitmap(globalClass.cab_image)));


        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

}
