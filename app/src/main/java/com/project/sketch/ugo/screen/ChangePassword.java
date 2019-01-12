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
import com.project.sketch.ugo.utils.SharedPref;
import com.project.sketch.ugo.utils.ValidationClass;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/16/18.
 */

public class ChangePassword extends AppCompatActivity {

    Toolbar toolbar;
    EditText edt_old_password, edt_password, edt_confirm_password;
    Button btn_submit;


    ProgressDialog pDialog;
    private SharedPref sharedPref;
    private ValidationClass validationClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_screen);
        initViews();




    }

    public void initViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_change_password));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        sharedPref = new SharedPref(this);
        validationClass = new ValidationClass(this);

        edt_old_password = (EditText) findViewById(R.id.edt_old_password);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_confirm_password = (EditText) findViewById(R.id.edt_confirm_password);
        btn_submit = (Button) findViewById(R.id.btn_submit);



        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkValidate();
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


    private void checkValidate(){

        if (edt_old_password.getText().toString().isEmpty()) {
            edt_old_password.setError("Enter old password");
            return;
        }

        if (!validationClass.validatePassword1(edt_password))
            return;

        if (!validationClass.validatePassword2(edt_password, edt_confirm_password))
            return;




        resetPassword();


    }


    private void resetPassword(){

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResetPassResponce> call = apiService.changePassword(
                sharedPref.getUserId(),
                edt_old_password.getText().toString(),
                edt_password.getText().toString()
        );

        call.enqueue(new Callback<ResetPassResponce>() {
            @Override
            public void onResponse(Call<ResetPassResponce>call, retrofit2.Response<ResetPassResponce> response) {


                try{
                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
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
