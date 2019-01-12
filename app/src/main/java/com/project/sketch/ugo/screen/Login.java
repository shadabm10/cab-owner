package com.project.sketch.ugo.screen;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel.LoginResponce;
import com.project.sketch.ugo.utils.ConnectivityReceiver;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.SharedPref;
import com.project.sketch.ugo.utils.ValidationClass;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.notice.Notice;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/8/18.
 */

public class Login extends AppCompatActivity {


    private EditText input_mobile, input_password;
    private Button action_signin;
    private TextView tv_forgot_password;

    ProgressDialog pDialog;
    ValidationClass validationClass;

    SharedPref sharedPref;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        initViews();




    }


    public void initViews(){

        UpdateChecker checker = new UpdateChecker(this);
        checker.setNotice(Notice.NOTIFICATION);
        checker.start();

        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_password = (EditText) findViewById(R.id.input_password);
        action_signin = (Button) findViewById(R.id.action_signin);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);

        input_mobile.setFocusableInTouchMode(true);
        input_password.setFocusableInTouchMode(true);

        validationClass = new ValidationClass(this);
        sharedPref = new SharedPref(this);

        action_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkValidation();


            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);

            }
        });



        requestPermission();
    }




    private static final int PERMISSION_REQUEST_CODE = 1;

    private void requestPermission(){

        ActivityCompat.requestPermissions((Activity)Login.this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("RYD", "PERMISSION_GRANTED >>>>");

                } else {
                    //code for deny
                }
                break;
        }
    }



    private void checkValidation(){

        if (!validationClass.validateMobileNo(input_mobile))
            return;

        if (!validationClass.validatePassword1(input_password))
            return;



        if (ConnectivityReceiver.isConnected()){

            login();

        } else {

            Toast.makeText(getApplicationContext(), "Please connect to internet", Toast.LENGTH_SHORT).show();
        }


    }


    private void login(){

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging In...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponce> call = apiService.login(
                input_mobile.getText().toString(),
                input_password.getText().toString(),
                Constants.device,
                validationClass.getAndroid_id(),
                validationClass.getFcm_token()
        );

        call.enqueue(new Callback<LoginResponce>() {
            @Override
            public void onResponse(Call<LoginResponce>call, retrofit2.Response<LoginResponce> response) {

                try{
                    if (response != null){

                        if (response.body().getStatus() == 1){

                           // Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                          //  Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());
                          //  Log.d(Constants.TAG, "onResponse: " + response.body().getCustomerInfo().getName());
                          //  Log.d(Constants.TAG, "onResponse: " + response.body().getCustomerInfo().getEmail());
                          //  Log.d(Constants.TAG, "onResponse: " + response.body().getCustomerInfo().getPhone());
                          //  Log.d(Constants.TAG, "onResponse: " + response.body().getCustomerInfo().getUid());

                            sharedPref.setFirstLogin(true);

                            sharedPref.setUserId(response.body().getCustomerInfo().getUid());

                            sharedPref.setUserInfo(
                                    response.body().getCustomerInfo().getName(),
                                    response.body().getCustomerInfo().getEmail(),
                                    response.body().getCustomerInfo().getPhone(),
                                    response.body().getCustomerInfo().getImage(),
                                    response.body().getCustomerInfo().getCustomer_rating()
                            );


                            Intent intent = new Intent(Login.this, LocationPermission.class);
                            startActivity(intent);
                            Splash.fa.finish();
                            finish();

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
            public void onFailure(Call<LoginResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pDialog.dismiss();
            }
        });



    }





}
