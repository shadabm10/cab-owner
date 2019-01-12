package com.project.sketch.ugo.screen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel.ResetPassResponce;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.ValidationClass;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/24/18.
 */

public class ResetPassword extends AppCompatActivity {

    private Toolbar toolbar;
    EditText input_password, input_confirm_password;
    Button action_submit;

    ValidationClass validationClass;

    ProgressDialog pDialog;

    String id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        initViews();


    }

    public void initViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        validationClass = new ValidationClass(this);

        input_password = (EditText) findViewById(R.id.input_password);
        input_confirm_password = (EditText) findViewById(R.id.input_confirm_password);

        action_submit = (Button) findViewById(R.id.action_submit);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            id = bundle.getString("id");
        }


        action_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkValidation();

            }
        });

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

    private void checkValidation(){

        if (!validationClass.validatePassword1(input_password))
            return;

        if (!validationClass.validatePassword2(input_password, input_confirm_password))
            return;



        resetPassword();

    }


    private void resetPassword(){

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResetPassResponce> call = apiService.resetPassword(
                id,
                input_password.getText().toString()
        );

        call.enqueue(new Callback<ResetPassResponce>() {
            @Override
            public void onResponse(Call<ResetPassResponce>call, retrofit2.Response<ResetPassResponce> response) {


                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            finish();

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
            public void onFailure(Call<ResetPassResponce>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pDialog.dismiss();
            }
        });




    }


}
