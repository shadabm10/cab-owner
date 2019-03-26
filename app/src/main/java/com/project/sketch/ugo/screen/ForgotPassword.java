package com.project.sketch.ugo.screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel.ForgotPassResponce;
import com.project.sketch.ugo.httpRequest.apiModel.OtpVerifyResponce;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.ValidationClass;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/8/18.
 */

public class ForgotPassword extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText input_mobile, input_otp;
    private Button action_sendOtp, action_submit;
    RelativeLayout rl_1, rl_2;
    ProgressDialog pDialog;
    ValidationClass validationClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        initViews();


    }


    public void initViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);


        input_mobile = (EditText) findViewById(R.id.input_mobile);
        action_sendOtp = (Button) findViewById(R.id.action_sendOtp);

        input_otp = (EditText) findViewById(R.id.input_otp);
        action_submit = (Button) findViewById(R.id.action_submit);

        rl_1 = (RelativeLayout) findViewById(R.id.rl_1);
        rl_2 = (RelativeLayout) findViewById(R.id.rl_2);

        rl_1.setVisibility(View.VISIBLE);
        rl_2.setVisibility(View.GONE);



        validationClass = new ValidationClass(this);


        action_sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validationClass.validateMobileNo(input_mobile))
                    return;


                    forgotPassword();

            }
        });


        action_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validationClass.validateOtp(input_otp))
                    return;
              /*  if(input_otp.getText().toString().equalsIgnoreCase("123456")){

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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu  ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                if (rl_1.getVisibility() == View.VISIBLE){

                    overridePendingTransition(R.anim.stay, R.anim.slide_down);
                    finish();

                }else {

                    rl_1.setVisibility(View.VISIBLE);
                    rl_2.setVisibility(View.GONE);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (rl_1.getVisibility() == View.VISIBLE){

            overridePendingTransition(R.anim.stay, R.anim.slide_down);
            finish();

            super.onBackPressed();

        }else {

            rl_1.setVisibility(View.VISIBLE);
            rl_2.setVisibility(View.GONE);
        }

    }



    private void forgotPassword(){


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ForgotPassResponce> call = apiService.forgotPassword(
                input_mobile.getText().toString()
        );

        call.enqueue(new Callback<ForgotPassResponce>() {
            @Override
            public void onResponse(Call<ForgotPassResponce>call, retrofit2.Response<ForgotPassResponce> response) {

                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getOTP());

                        if (response.body().getStatus() == 1){

                            rl_1.setVisibility(View.GONE);
                            rl_2.setVisibility(View.VISIBLE);

                            // input_otp.setText(response.body().getOTP());

                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                            pDialog.dismiss();

                        }else {

                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                            pDialog.dismiss();
                        }


                    }

                }catch (Exception e){
                    e.printStackTrace();
                }




            }

            @Override
            public void onFailure(Call<ForgotPassResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pDialog.dismiss();
            }
        });



    }



    private void verifyOtp(){

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<OtpVerifyResponce> call = apiService.verify_otp(
                input_mobile.getText().toString(),
                input_otp.getText().toString()
        );

        call.enqueue(new Callback<OtpVerifyResponce>() {
            @Override
            public void onResponse(Call<OtpVerifyResponce>call, retrofit2.Response<OtpVerifyResponce> response) {


                Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());
                Log.d(Constants.TAG, "onResponse: " + response.body().getId());


                if (response.body().getStatus() == 1){

                    Intent intent = new Intent(ForgotPassword.this, ResetPassword.class);
                    intent.putExtra("id", response.body().getId());
                    startActivity(intent);

                    finish();

                }else {

                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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



}
