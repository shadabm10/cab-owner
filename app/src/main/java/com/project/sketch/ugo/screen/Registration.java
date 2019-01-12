package com.project.sketch.ugo.screen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel.RegistrationResponce;
import com.project.sketch.ugo.utils.ConnectivityReceiver;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.ValidationClass;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/8/18.
 */

public class Registration extends AppCompatActivity {

    private EditText input_name, input_email, input_mobile, input_password, input_re_password;
    private Button action_signup;


    ValidationClass validationClass;

    ProgressDialog pDialog;

    public static Activity regActivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        regActivity = this;

        initViews();




    }


    public void initViews(){

        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_password = (EditText) findViewById(R.id.input_password);
        input_re_password = (EditText) findViewById(R.id.input_re_password);
        action_signup = (Button) findViewById(R.id.action_signup);

        input_name.setFocusableInTouchMode(true);
        input_email.setFocusableInTouchMode(true);
        input_mobile.setFocusableInTouchMode(true);
        input_password.setFocusableInTouchMode(true);
        input_re_password.setFocusableInTouchMode(true);

        validationClass = new ValidationClass(Registration.this);


        action_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkValidation();

            }
        });

    }



    private void checkValidation(){

        if (!validationClass.validateName(input_name))
            return;

        if (!validationClass.validateEmail(input_email))
            return;

        if (!validationClass.validateMobileNo(input_mobile))
            return;

        if (!validationClass.validatePassword1(input_password))
            return;

        if (!validationClass.validatePassword2(input_password, input_re_password))
            return;



        /// call reg

        if (ConnectivityReceiver.isConnected()){

            registration();

        } else {

            Toast.makeText(getApplicationContext(), "Please connect to internet", Toast.LENGTH_SHORT).show();
        }



    }


    private void registration(){

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<RegistrationResponce> call = apiService.userRegistration(
                input_name.getText().toString(),
                input_email.getText().toString(),
                input_mobile.getText().toString(),
                input_password.getText().toString(),
                Constants.device,
                validationClass.getAndroid_id(),
                validationClass.getFcm_token()

        );
        call.enqueue(new Callback<RegistrationResponce>() {
            @Override
            public void onResponse(Call<RegistrationResponce>call, retrofit2.Response<RegistrationResponce> response) {

                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getOTP());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());


                        if (response.body().getStatus() == 1){

                            Intent intent = new Intent(Registration.this, OtpVerification.class);
                            intent.putExtra("phone", input_mobile.getText().toString());
                            intent.putExtra("otp", response.body().getOTP());
                            startActivity(intent);


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
            public void onFailure(Call<RegistrationResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pDialog.dismiss();
            }
        });



    }




}
