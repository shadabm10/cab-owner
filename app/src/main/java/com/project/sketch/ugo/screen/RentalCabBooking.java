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
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.adapter.RentalEstimateFareAdapter;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel2.ConfirmBookResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;
import com.project.sketch.ugo.httpRequest.apiModel4.CancelBooking;
import com.project.sketch.ugo.httpRequest.apiModel5.RentalRate;
import com.project.sketch.ugo.httpRequest.apiModel5.RentalRateList;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by developer on 6/3/18.
 */

public class RentalCabBooking extends AppCompatActivity {

    Toolbar toolbar;
    TextView tv_pickup_address, tv_cab_name;
    ListView lv_rental;
    Button btn_confirm;

    RelativeLayout rl_in_2,rl_call_driver,rl_cancel_booking, rl_buttons, rl_paymentMode;
    CircleImageView profile_image;
    TextView tv_driver_name, tv_car_name_number, tv_otp, tv_paymentMode;
    RatingBar rating_driver;
    CheckBox checkbox_guider;
    RelativeLayout rl_coupon;

    LayerDrawable stars;

    Animation show_animation, hide_animation;

    GlobalClass globalClass;
    ProgressDialog pdialog;
    Dialog progressDialog;
    SharedPref sharedPref;

    List<RentalRate> rentalRateList;

    String selected_plan_id, booking_id, driver_phone_number;
    String guide_option;

    private static final int ACTIVITY_RESULT_CODE_Fare = 100;
    private static final int ACTIVITY_RESULT_CODE_PaymentMode = 200;
    boolean isCancelByDriver = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rental_cab_booking);

        initViews();


    }


    private void initViews(){

        globalClass = (GlobalClass) getApplicationContext();
        sharedPref = new SharedPref(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_rental_booking));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        tv_pickup_address = (TextView) findViewById(R.id.tv_pickup_address);
        tv_cab_name = (TextView) findViewById(R.id.tv_cab_name);
        lv_rental = (ListView) findViewById(R.id.lv_rental);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);

        rl_in_2 = (RelativeLayout) findViewById(R.id.rl_in_2);
        rl_in_2.setVisibility(View.GONE);
        rl_call_driver = (RelativeLayout) findViewById(R.id.rl_call_driver);
        rl_cancel_booking = (RelativeLayout) findViewById(R.id.rl_cancel_booking);
        rl_buttons = (RelativeLayout) findViewById(R.id.rl_buttons);
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_coupon);

        profile_image = (CircleImageView) findViewById(R.id.profile_image);

        rl_paymentMode = (RelativeLayout) findViewById(R.id.rl_paymentMode);

        tv_driver_name = (TextView) findViewById(R.id.tv_driver_name);
        tv_car_name_number = (TextView) findViewById(R.id.tv_car_name_number);
        tv_paymentMode = (TextView) findViewById(R.id.tv_paymentMode);
        tv_otp = (TextView) findViewById(R.id.tv_otp);
        checkbox_guider = (CheckBox) findViewById(R.id.checkbox_guider);
        checkbox_guider.setChecked(false);
        guide_option = "N";

        rating_driver = (RatingBar) findViewById(R.id.rating_driver);
        stars = (LayerDrawable) rating_driver.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFA100"), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for empty stars


        show_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in_animation);
        hide_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out_animation);



        tv_pickup_address.setText(globalClass.PICUP_ADDRESS);
        tv_cab_name.setText("Cab : "+globalClass.Selected_Cab_Name);

        pdialog = new ProgressDialog(this);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);
        pdialog.setMessage("Please wait...");

        rentalRateList = new ArrayList<>();

        setupLoader();

        getRentalCabRate();

        onClick();

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


    private void onClick(){

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callForBooking();

            }
        });


        checkbox_guider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    guide_option = "Y";

                }else {

                    guide_option = "N";

                }

            }
        });


        rl_paymentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_mode = new Intent(RentalCabBooking.this, PaymentMode.class);
                startActivityForResult(intent_mode, ACTIVITY_RESULT_CODE_PaymentMode);

            }
        });

        rl_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_coupon = new Intent(RentalCabBooking.this, CouponScreen.class);
                startActivity(intent_coupon);
            }
        });


        rl_cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelReasonDialog();
            }
        });

    }


    RentalEstimateFareAdapter rentalEstimateFareAdapter;
    private void getRentalCabRate(){

        pdialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<RentalRateList> call = apiService.rental_fare(
                globalClass.PICKUP_LATLNG.latitude,
                globalClass.PICKUP_LATLNG.longitude,
                globalClass.Selected_Cab_Id
        );

        call.enqueue(new Callback<RentalRateList>() {
            @Override
            public void onResponse(Call<RentalRateList>call, retrofit2.Response<RentalRateList> response) {


                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            rentalRateList = response.body().getRental_info();

                            rentalEstimateFareAdapter = new RentalEstimateFareAdapter(
                                    RentalCabBooking.this,
                                    rentalRateList);

                            lv_rental.setAdapter(rentalEstimateFareAdapter);



                            rentalEstimateFareAdapter.setViewClickListener(
                                    new RentalEstimateFareAdapter.ViewClickListener() {
                                        @Override
                                        public void onItemClicked(int position) {
                                            selected_plan_id = rentalRateList.get(position).getId();
                                            Log.d(Constants.TAG, "selected_plan_id = "+selected_plan_id);
                                        }
                                    });


                        }else {


                        }



                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



                pdialog.dismiss();
            }

            @Override
            public void onFailure(Call<RentalRateList> call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pdialog.dismiss();
            }
        });




    }



    /////////////////////////////


    public void setupLoader(){

        progressDialog = new Dialog(this, android.R.style.Theme_Translucent);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setContentView(R.layout.driver_search_loader);
        progressDialog.setCancelable(false);

    }

    //////////////////////////////////
    // timer booking ...


    private int counter;
    private Timer timer;
    private void callForBooking(){

        progressDialog.show();

        int delay = 1000; // delay for 1 sec.
        int period = 14000; // repeat every 14 sec.
        timer = new Timer();
        counter = 1;

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run()
            {
                if (counter < 4){
                    counter++;

                    Log.d(Constants.TAG, "call booking");

                    confirmBookingOutstation();

                }else {

                    timer.cancel();

                    progressDialog.dismiss();

                }

            }
        }, delay, period);

    }


    //////////////////////////////////////////////////
    /// Confirm booking ...

    private void confirmBookingOutstation(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ConfirmBookResponce> call = apiService.confirmBooking(
                sharedPref.getUserId(),
                "rental",
                "",
                globalClass.PICUP_ADDRESS,
                globalClass.PICKUP_LATLNG.latitude,
                globalClass.PICKUP_LATLNG.longitude,
                "",
                0,
                0,
                globalClass.Selected_Cab_Id,
                sharedPref.getPaymentMode(),
                guide_option,
                globalClass.Booking_later_time,
                globalClass.Booking_later_time,
                globalClass.Later_Ride,
                "",
                selected_plan_id,
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

                            Log.d(Constants.TAG, "onResponse: " + response.body().getDriver().getName());

                            counter = 4;

                            progressDialog.dismiss();

                            globalClass.setDriver(response.body().getDriver());
                            sharedPref.saveDriverInfo(response.body().getDriver());

                            setDriverData(globalClass.getDriver());

                            rl_buttons.startAnimation(hide_animation);
                            rl_buttons.setVisibility(View.GONE);

                            rl_in_2.startAnimation(show_animation);
                            rl_in_2.setVisibility(View.VISIBLE);


                        } else if (response.body().getStatus() == 3){

                            if (counter > 3){

                                progressDialog.dismiss();

                                Toast.makeText(RentalCabBooking.this,
                                        "Driver not available for this time",
                                        Toast.LENGTH_LONG).show();

                                Toast.makeText(RentalCabBooking.this,
                                        "Please try again",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else if (response.body().getStatus() == 4){
                            pdialog.dismiss();


                        } else if (response.body().getStatus() == 5){
                            pdialog.dismiss();

                            Toast.makeText(RentalCabBooking.this,
                                    "Booking not confirmed.\nPlease try again.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            pdialog.dismiss();

                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();

                    if (counter > 3){

                        progressDialog.dismiss();

                        Toast.makeText(RentalCabBooking.this,
                                "Driver not available for this time",
                                Toast.LENGTH_LONG).show();

                        Toast.makeText(RentalCabBooking.this,
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

                driver_phone_number = driverData.getPhone();
                checkPermission();
            }
        });

    }


    private static final int REQUEST_CODE_CALL_PHONE_PERMISSIONS = 1122;
    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(RentalCabBooking.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CALL_PHONE);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) RentalCabBooking.this, permissionsList.toArray(new String[permissionsList.size()]),
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){

            if (requestCode == ACTIVITY_RESULT_CODE_PaymentMode){

                if (sharedPref.getPaymentMode().matches(Constants.paymentMode_cash)){
                    tv_paymentMode.setText("Cash");
                }else if (sharedPref.getPaymentMode().matches(Constants.paymentMode_Wallet)){
                    tv_paymentMode.setText("Wallet");
                }

            }

        }else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }

    /////////////////////////////


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

                        Toast.makeText(RentalCabBooking.this, "Please enter reason", Toast.LENGTH_SHORT).show();

                    }else {

                        cancelBooking(reason_text);
                    }


                }else {

                    if (reason_text.isEmpty()){

                        Toast.makeText(RentalCabBooking.this, "Please select one reason", Toast.LENGTH_SHORT).show();

                    }else {

                        cancelBooking(reason_text);
                    }
                }

            }
        });
    }

    public void cancelBooking(String cancel_reason){

        pdialog.show();

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

                            Toast.makeText(RentalCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                            globalClass.is_Cancel = true;

                            pdialog.dismiss();

                            finish();

                        }else if (response.body().getStatus() == 2){
                            pdialog.dismiss();

                            Toast.makeText(RentalCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }else {
                            pdialog.dismiss();

                            Toast.makeText(RentalCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

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
                pdialog.dismiss();

            }
        });


    }

    //////////////////////////////

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.registerReceiver(mMessageReceiver, new IntentFilter(Constants.key_confirmbooking));

        if (globalClass.getCoupon_applied().matches("Y")){

            if (rentalEstimateFareAdapter != null){

                rentalEstimateFareAdapter.notifyDataSetChanged();

            }else {
                rentalEstimateFareAdapter = new RentalEstimateFareAdapter(
                        RentalCabBooking.this,
                        rentalRateList);

                lv_rental.setAdapter(rentalEstimateFareAdapter);
            }

        }

        super.onResume();
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


            }else if (type == 4){
                // reach driver on your pickup address

                rl_cancel_booking.setVisibility(View.GONE);


            }else if (type == 5){

                isCancelByDriver = true;

                rl_in_2.startAnimation(hide_animation);
                rl_in_2.setVisibility(View.GONE);

                //rl_in_1.startAnimation(show_animation);
                //rl_in_1.setVisibility(View.VISIBLE);

                rl_cancel_booking.setVisibility(View.VISIBLE);

                String msg = getResources().getString(R.string.msg_cancelDriver);
                AlertDialog alertDialog =
                        new AlertDialog.Builder(RentalCabBooking.this).create();
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




}
