package com.project.sketch.ugo.screen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel.OtpVerifyResponce;
import com.project.sketch.ugo.httpRequest.apiModel6.ResendOtpResponse;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.ValidationClass;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/8/18.
 */

public class OtpVerification extends AppCompatActivity{

     EditText input_otp;
    private Button action_verify;
    private TextView tv_resend_otp, tv_show_timer;
    private Toolbar toolbar;

    private String otp, phone;

    private Handler handler = new Handler();
    private Runnable runnable;
    private long milliSeconds = 0;


    ProgressDialog pDialog;


    ValidationClass validationClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification_screen);

        initViews();




    }


    public void initViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_otp_verification));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        input_otp = (EditText) findViewById(R.id.input_otp);
        action_verify = (Button) findViewById(R.id.action_verify);
        tv_resend_otp = (TextView) findViewById(R.id.tv_resend_otp);
        tv_show_timer = (TextView) findViewById(R.id.tv_show_timer);

        validationClass = new ValidationClass(getApplicationContext());


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            phone = bundle.getString("phone");
            otp = bundle.getString("otp");
            input_otp.setText(otp);

        }



        action_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (!validationClass.validateOtp(input_otp))
                    return;
                /* if(input_otp.getText().toString().equalsIgnoreCase("123456")){

                     Toast.makeText(getApplicationContext(), "Mobile number successfully verified", Toast.LENGTH_LONG).show();
                     Registration.regActivity.finish();
                     finish();
                 }
                 else {
                     Toast.makeText(getApplicationContext(), "Mobile number not verified", Toast.LENGTH_LONG).show();

                 }*/
                verifyOtp();

            }
        });


        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tv_resend_otp.setVisibility(View.GONE);
                tv_show_timer.setVisibility(View.VISIBLE);

                resendOtp();

            }
        });



        tv_resend_otp.setVisibility(View.GONE);
        tv_show_timer.setVisibility(View.VISIBLE);

        timerStart();



    }


    private void timerStart() {
        milliSeconds = 0;

        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                milliSeconds = milliSeconds + 1000;

                int hours = (int) ((milliSeconds / (1000 * 60 * 60)) % 24);
                int minutes = (int) ((milliSeconds / (1000 * 60)) % 60);
                int seconds = (int) (milliSeconds / 1000) % 60;

                /*tv_show_timer.setText("Wait for "+String.format("%02d", hours)
                        + ":" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));*/


                tv_show_timer.setText("Wait "
                        + ": " + String.format("%02d", seconds) + " sec");


                if (seconds == 30){
                    handler.removeCallbacks(runnable);
                    tv_resend_otp.setVisibility(View.VISIBLE);
                    tv_show_timer.setVisibility(View.GONE);
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }



    private void verifyOtp(){

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<OtpVerifyResponce> call = apiService.verify_otp(
                phone,
                input_otp.getText().toString()
        );

        call.enqueue(new Callback<OtpVerifyResponce>() {
            @Override
            public void onResponse(Call<OtpVerifyResponce>call, retrofit2.Response<OtpVerifyResponce> response) {


                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getId());

                        if (response.body().getStatus() == 1){

                            Toast.makeText(getApplicationContext(), "Mobile number successfully verified", Toast.LENGTH_LONG).show();
                            Registration.regActivity.finish();
                            finish();

                        }else {

                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<OtpVerifyResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pDialog.dismiss();
            }
        });

    }


    private void resendOtp(){

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResendOtpResponse> call = apiService.customer_resend_otp(phone);

        call.enqueue(new Callback<ResendOtpResponse>() {
            @Override
            public void onResponse(Call<ResendOtpResponse>call, retrofit2.Response<ResendOtpResponse> response) {

                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());


                        if (response.body().getStatus() == 1){

                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                            timerStart();

                        }else {

                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResendOtpResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pDialog.dismiss();
            }
        });

    }




}
