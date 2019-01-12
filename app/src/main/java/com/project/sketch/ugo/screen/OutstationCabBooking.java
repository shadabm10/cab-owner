package com.project.sketch.ugo.screen;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel2.ConfirmBookResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;
import com.project.sketch.ugo.httpRequest.apiModel2.OutstationEstimateData;
import com.project.sketch.ugo.httpRequest.apiModel2.OutstationEstimateResponse;
import com.project.sketch.ugo.httpRequest.apiModel4.CancelBooking;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import static com.project.sketch.ugo.utils.Constants.MaxOutstationDistance;

/**
 * Created by Developer on 1/13/18.
 */

public class OutstationCabBooking extends AppCompatActivity {

    Toolbar toolbar;

    TextView tv_pickup_address, tv_drop_address, tv_distance, tv_estimate_fare, tv_cab_name;
    RelativeLayout rl_addressBar, rl_set_time, rl_top_2;
    CheckBox checkbox_guider;
    RadioGroup radioGroup_type;
    RadioButton hours_12, hours_24;
    TextView tv_time, tv_guider_fee, tv_msg;
    Button btn_confirm_booking;
    RelativeLayout rl_paymentMode;

    RelativeLayout rl_in_2,rl_call_driver,rl_cancel_booking;
    CircleImageView profile_image;
    TextView tv_driver_name, tv_car_name_number, tv_otp, tv_paymentMode;
    RatingBar rating_driver;
    RelativeLayout rl_coupon;

    LayerDrawable stars;

    GlobalClass globalClass;
    SharedPref sharedPref;

    String guide_option = "Y";
    String which_hour = "12_hour";
    String selectedDateTime_SendFormat;
    String driver_phone_number;
    String booking_id;

    Animation show_animation, hide_animation;

    Dialog progressDialog;
    private ProgressDialog pDialog;

    private OutstationEstimateData outstationEstimateData;

    private static final int ACTIVITY_RESULT_CODE_Fare = 100;
    private static final int ACTIVITY_RESULT_CODE_PaymentMode = 200;

    boolean isCancelByDriver = false;


    SimpleDateFormat send_format;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outstation_booking_screen);

        initViews();



    }


    public void initViews(){

        globalClass = (GlobalClass) getApplicationContext();
        sharedPref = new SharedPref(this);

        pDialog  = new ProgressDialog(OutstationCabBooking.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Please wait...");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_outstation_booking));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);


        tv_pickup_address = (TextView) findViewById(R.id.tv_pickup_address);
        tv_drop_address = (TextView) findViewById(R.id.tv_drop_address);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_estimate_fare = (TextView) findViewById(R.id.tv_estimate_fare);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_guider_fee = (TextView) findViewById(R.id.tv_guider_fee);
        tv_paymentMode = (TextView) findViewById(R.id.tv_paymentMode);
        tv_cab_name = (TextView) findViewById(R.id.tv_cab_name);
        tv_cab_name.setText("Cab : "+globalClass.Selected_Cab_Name);

        rl_addressBar = (RelativeLayout) findViewById(R.id.rl_addressBar);
        rl_set_time = (RelativeLayout) findViewById(R.id.rl_set_time);
        rl_top_2 = (RelativeLayout) findViewById(R.id.rl_top_2);
        rl_top_2.setVisibility(View.GONE);
        checkbox_guider = (CheckBox) findViewById(R.id.checkbox_guider);
        radioGroup_type = (RadioGroup) findViewById(R.id.radioGroup_type);
        hours_12 = (RadioButton) findViewById(R.id.hours_12);
        hours_12.setChecked(true);
        hours_24 = (RadioButton) findViewById(R.id.hours_24);
        btn_confirm_booking = (Button) findViewById(R.id.btn_confirm_booking);
        btn_confirm_booking.setVisibility(View.VISIBLE);

        rl_paymentMode = (RelativeLayout) findViewById(R.id.rl_paymentMode);
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_coupon);
        rl_in_2 = (RelativeLayout) findViewById(R.id.rl_in_2);
        rl_in_2.setVisibility(View.GONE);
        rl_call_driver = (RelativeLayout) findViewById(R.id.rl_call_driver);
        rl_cancel_booking = (RelativeLayout) findViewById(R.id.rl_cancel_booking);

        profile_image = (CircleImageView) findViewById(R.id.profile_image);

        tv_driver_name = (TextView) findViewById(R.id.tv_driver_name);
        tv_car_name_number = (TextView) findViewById(R.id.tv_car_name_number);
        tv_otp = (TextView) findViewById(R.id.tv_otp);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

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


        SimpleDateFormat format = new SimpleDateFormat("EEE dd-MMM h:mm a");

        Calendar cal1 = Calendar.getInstance();
        Log.d(Constants.TAG, "current time = "+format.format(cal1.getTime()));
        tv_time.setText(format.format(cal1.getTime()));

        send_format = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        selectedDateTime_SendFormat = send_format.format(cal1.getTime());

        buttonOnClick();




        if (globalClass.PICKUP_LATLNG != null && globalClass.DROP_LATLNG != null){
            getEstimateValue();
        }else {
            finish();
        }


        setupLoader();


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

        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.registerReceiver(mMessageReceiver, new IntentFilter(Constants.key_confirmbooking));

        if (globalClass.getCoupon_applied().matches("Y")){

            if (which_hour.matches("12_hour")){

                setEstimate_12H();

            }else {

                setEstimate_24H();

            }
        }

        super.onResume();
    }


    public void buttonOnClick(){

        rl_paymentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_mode = new Intent(OutstationCabBooking.this, PaymentMode.class);
                startActivityForResult(intent_mode, ACTIVITY_RESULT_CODE_PaymentMode);

            }
        });

        rl_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDateAndTimeDialog();

            }
        });



        checkbox_guider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    guide_option = "Y";

                    tv_guider_fee.setText("Guider Charge : ₹ "
                            + outstationEstimateData.getEstimate_guide_charge_12_h()
                            + "  *Excluded" );

                }else {

                    guide_option = "N";

                    tv_guider_fee.setText("Guider Charge : ₹ "
                            + outstationEstimateData.getEstimate_guide_charge_12_h()
                            + "  *Excluded" );

                }

            }
        });


        radioGroup_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.hours_12:
                        which_hour = "12_hour";
                        setEstimate_12H();
                        break;

                    case R.id.hours_24:
                        which_hour = "24_hour";
                        setEstimate_24H();
                        break;
                }

            }
        });


        btn_confirm_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callForBooking();
            }
        });


        rl_cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelReasonDialog();
            }
        });


        rl_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_coupon = new Intent(OutstationCabBooking.this, CouponScreen.class);
                startActivity(intent_coupon);
            }
        });

    }

    private void setEstimate_12H(){

        float dis = outstationEstimateData.getEstimate_distance_value()/1000;
        tv_distance.setText("Distance : "
                + (int)Math.round(dis) + " Km.");

        if (globalClass.getCoupon_applied().matches("Y")){

            float totalWithOutGuideMin_12 = outstationEstimateData.getEstimate_fare_12_hour()
                    + outstationEstimateData.getTotal_fees_and_charges()
                    - globalClass.getCoupon_amount();

            float totalWithOutGuideMax_12 = outstationEstimateData.getEstimate_fare_12_hour_max()
                    + outstationEstimateData.getTotal_fees_and_charges()
                    - globalClass.getCoupon_amount();


            tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithOutGuideMin_12
                    +" - ₹ "+(int)totalWithOutGuideMax_12);

        }else {

            float totalWithOutGuideMin_12 = outstationEstimateData.getEstimate_fare_12_hour()
                    + outstationEstimateData.getTotal_fees_and_charges();

            float totalWithOutGuideMax_12 = outstationEstimateData.getEstimate_fare_12_hour_max()
                    + outstationEstimateData.getTotal_fees_and_charges();


            tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithOutGuideMin_12
                    +" - ₹ "+(int)totalWithOutGuideMax_12);

        }

        tv_guider_fee.setText("Guider Charge : ₹ "
                + outstationEstimateData.getEstimate_guide_charge_12_h()
                + "  *Excluded" );

    }

    private void setEstimate_24H(){

        float dis = outstationEstimateData.getEstimate_distance_value()/1000;
        tv_distance.setText("Distance : "
                + (int)Math.round(dis * 2) + " Km.");

        if (globalClass.getCoupon_applied().matches("Y")){

            float totalWithOutGuideMin_24 = outstationEstimateData.getEstimate_fare_24_hour()
                    + outstationEstimateData.getTotal_fees_and_charges()
                    - globalClass.getCoupon_amount();

            float totalWithOutGuideMax_24 = outstationEstimateData.getEstimate_fare_24_hour_max()
                    + outstationEstimateData.getTotal_fees_and_charges()
                    - globalClass.getCoupon_amount();


            tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithOutGuideMin_24
                    +" - ₹ "+(int)totalWithOutGuideMax_24);

        }else {

            float totalWithOutGuideMin_24 = outstationEstimateData.getEstimate_fare_24_hour()
                    + outstationEstimateData.getTotal_fees_and_charges();

            float totalWithOutGuideMax_24 = outstationEstimateData.getEstimate_fare_24_hour_max()
                    + outstationEstimateData.getTotal_fees_and_charges();


            tv_estimate_fare.setText("Fare Estimate : ₹ "+(int)totalWithOutGuideMin_24
                    +" - ₹ "+(int)totalWithOutGuideMax_24);

        }


        tv_guider_fee.setText("Guider Charge : ₹ "
                + outstationEstimateData.getEstimate_guide_charge_24_h()
                + "  *Excluded" );

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
    /// Get estimate time and fare ...

    private void getEstimateValue(){

        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<OutstationEstimateResponse> call = apiService.getOutstationEstimateCost(
                globalClass.PICKUP_LATLNG.latitude,
                globalClass.PICKUP_LATLNG.longitude,
                globalClass.DROP_LATLNG.latitude,
                globalClass.DROP_LATLNG.longitude,
                globalClass.Selected_Cab_Id
        );

        call.enqueue(new Callback<OutstationEstimateResponse>() {
            @Override
            public void onResponse(Call<OutstationEstimateResponse>call, retrofit2.Response<OutstationEstimateResponse> response) {

                try {
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            outstationEstimateData = response.body().getEstimateData();

                            float dis = response.body().getEstimateData().getEstimate_distance_value()/1000;
                            tv_distance.setText("Distance : "+ (int)Math.round(dis) + " Km.");

                            if (dis >= MaxOutstationDistance){
                                hours_12.setChecked(false);
                                hours_12.setEnabled(false);
                                hours_24.setChecked(true);

                                tv_distance.setText("Distance : "
                                        + (int)Math.round(dis * 2) + " Km.");

                                tv_msg.setVisibility(View.VISIBLE);

                            }else {

                                setEstimateData();
                            }


                            rl_top_2.setVisibility(View.VISIBLE);


                        }else if (response.body().getStatus() == 2){


                        }else {

                        }

                    }



                }catch (Exception e){
                    e.printStackTrace();
                }

                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<OutstationEstimateResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

                pDialog.dismiss();

            }
        });

    }


    public void setEstimateData(){

        float totalWithGuideMin_12 =
                outstationEstimateData.getEstimate_fare_12_hour()
                        + outstationEstimateData.getTotal_fees_and_charges();

        float totalWithGuideMax_12 =
                outstationEstimateData.getEstimate_fare_12_hour_max()
                        + outstationEstimateData.getTotal_fees_and_charges();

        tv_estimate_fare.setText("Fare Estimate : ₹ "
                +(int)totalWithGuideMin_12
                +" - ₹ "
                +(int)totalWithGuideMax_12);

        tv_guider_fee.setText("Guider Charge : ₹ "
                + outstationEstimateData.getEstimate_guide_charge_12_h()
                + "  *Excluded" );


    }


    //////////////////////////////////////////////////

    // set date and time ...
    String selected_date, selected_time;
    TextView tv_time_2;
    private void setDateAndTimeDialog(){


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_outstaion_calender, null);
        dialogBuilder.setView(dialogView);


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Spinner spinner_date = (Spinner) dialogView.findViewById(R.id.spinner_date);
        Button okButton = (Button) dialogView.findViewById(R.id.okButton);
        ImageView iv_clock = (ImageView) dialogView.findViewById(R.id.iv_clock);
        tv_time_2 = (TextView) dialogView.findViewById(R.id.tv_time);
        tv_time_2.setText(getCurrentTime());


        SimpleDateFormat format = new SimpleDateFormat("EEE, dd-MMM-yy");
        Calendar cal = Calendar.getInstance();

        String[] array_date = new String[8];
        for(int i = 0; i < 8;i++){
            array_date[i] = format.format(cal.getTime());
            cal.add(Calendar.DATE  , 1);
            //System.out.println("date :" + array_date[i]);
        }

        ArrayAdapter spinnerDateArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                array_date);
        spinner_date.setAdapter(spinnerDateArrayAdapter);


        spinner_date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_date = parent.getSelectedItem().toString();

                Log.d(Constants.TAG, "selected_date = "+selected_date);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_time.setText(selected_date + ", " +selected_time);

                try {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd-MMM-yy, hh:mm aa");
                    Date date = simpleDateFormat.parse(selected_date + " " +selected_time);

                    send_format = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");

                    String formattedDate = send_format.format(date);
                    selectedDateTime_SendFormat = formattedDate;

                  //  Log.d(Constants.TAG, "selected= "+tv_time.getText().toString());
                  //  Log.d(Constants.TAG, "selectedDateTime_SendFormat = "+selectedDateTime_SendFormat);

                }catch (ParseException e){
                    e.getErrorOffset();
                }


                alertDialog.dismiss();
            }
        });


        iv_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePickerDialog();
            }
        });


    }


    String format;
    public void timePickerDialog(){

        Calendar calendar;
        calendar = Calendar.getInstance();
        int CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
        int CalendarMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timepickerdialog = new TimePickerDialog(OutstationCabBooking.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        }
                        else if (hourOfDay == 12) {
                            format = "PM";
                        }
                        else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        }
                        else {
                            format = "AM";
                        }


                        String time_h = String.format("%2d", hourOfDay);
                        String time_m = "";
                        if (String.valueOf(minute).length() == 1){
                            time_m = "0"+minute;
                        }else {
                            time_m = String.format("%2d", minute);
                        }


                      //  DisplayTime.setText(hourOfDay + ":" + minute + format);

                        selected_time = time_h + ":" + time_m + format;

                        tv_time_2.setText(selected_time);

                    }
                }, CalendarHour, CalendarMinute, false);
        timepickerdialog.show();

    }



    private String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        Calendar cal1 = Calendar.getInstance();
        //Log.d(Constants.TAG, "current time = "+format.format(cal1.getTime()));

        return format.format(cal1.getTime());
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
                "outstation",
                which_hour,
                globalClass.PICUP_ADDRESS,
                globalClass.PICKUP_LATLNG.latitude,
                globalClass.PICKUP_LATLNG.longitude,
                globalClass.DROP_ADDRESS,
                globalClass.DROP_LATLNG.latitude,
                globalClass.DROP_LATLNG.longitude,
                globalClass.Selected_Cab_Id,
                sharedPref.getPaymentMode(),
                guide_option,
                selectedDateTime_SendFormat,
                selectedDateTime_SendFormat,
                "",
                selectedDateTime_SendFormat,
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

                            Log.d(Constants.TAG, "onResponse: " + response.body().getDriver().getName());

                            counter = 4;

                            progressDialog.dismiss();

                            globalClass.setDriver(response.body().getDriver());
                            sharedPref.saveDriverInfo(response.body().getDriver());

                            setDriverData(globalClass.getDriver());

                            btn_confirm_booking.startAnimation(hide_animation);
                            btn_confirm_booking.setVisibility(View.GONE);

                            rl_in_2.startAnimation(show_animation);
                            rl_in_2.setVisibility(View.VISIBLE);


                        } else if (response.body().getStatus() == 3){

                            if (counter > 3){

                                progressDialog.dismiss();

                                Toast.makeText(OutstationCabBooking.this,
                                        "Driver not available for this time",
                                        Toast.LENGTH_LONG).show();

                                Toast.makeText(OutstationCabBooking.this,
                                        "Please try again",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else if (response.body().getStatus() == 4){
                            pDialog.dismiss();


                        } else if (response.body().getStatus() == 5){
                            pDialog.dismiss();

                            Toast.makeText(OutstationCabBooking.this,
                                    "Booking not confirmed.\nPlease try again.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            pDialog.dismiss();

                        }

                    }

                } catch (Exception e){
                    e.printStackTrace();

                    if (counter > 3){

                        progressDialog.dismiss();

                        Toast.makeText(OutstationCabBooking.this,
                                "Driver not available for this time",
                                Toast.LENGTH_LONG).show();

                        Toast.makeText(OutstationCabBooking.this,
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

    //////////////////////////////////////////////////////
    //// Check call permission ...

    private static final int REQUEST_CODE_CALL_PHONE_PERMISSIONS = 1122;

    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(OutstationCabBooking.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CALL_PHONE);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) OutstationCabBooking.this, permissionsList.toArray(new String[permissionsList.size()]),
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

    ///////////////////////////////////////////////////


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

                        Toast.makeText(OutstationCabBooking.this, "Please enter reason", Toast.LENGTH_SHORT).show();

                    }else {

                        cancelBooking(reason_text);
                    }


                }else {

                    if (reason_text.isEmpty()){

                        Toast.makeText(OutstationCabBooking.this, "Please select one reason", Toast.LENGTH_SHORT).show();

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

                            Toast.makeText(OutstationCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                            globalClass.is_Cancel = true;

                            pDialog.dismiss();

                            finish();

                        }else if (response.body().getStatus() == 2){
                            pDialog.dismiss();

                            Toast.makeText(OutstationCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }else {
                            pDialog.dismiss();

                            Toast.makeText(OutstationCabBooking.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

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


    public OutstationCabBooking() {
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
                        new AlertDialog.Builder(OutstationCabBooking.this).create();
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
