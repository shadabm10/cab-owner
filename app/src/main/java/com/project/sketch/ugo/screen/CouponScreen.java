package com.project.sketch.ugo.screen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel6.CouponModel;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by developer on 24/3/18.
 */

public class CouponScreen extends AppCompatActivity {

    Toolbar toolbar;
    EditText edt_coupon_code;
    ImageView iv_cancel;
    TextView tv_msg;
    Button btn_apply_coupon;

    SharedPref sharedPref;
    GlobalClass globalClass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_screen);
        initViews();




    }

    private void initViews(){

        sharedPref = new SharedPref(this);
        globalClass = (GlobalClass) getApplicationContext();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_coupon));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);


        edt_coupon_code = (EditText) findViewById(R.id.edt_coupon_code);
        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);
        btn_apply_coupon = (Button) findViewById(R.id.btn_apply_coupon);


        iv_cancel.setVisibility(View.INVISIBLE);
        btn_apply_coupon.setVisibility(View.INVISIBLE);


        actionViews();

    }


    public void actionViews(){


        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_coupon_code.setText("");
                tv_msg.setText("");
                tv_msg.setVisibility(View.GONE);
                globalClass.setCoupon_code("");
                globalClass.setCoupon_applied("N");
                globalClass.setCoupon_amount(0);
            }
        });

        edt_coupon_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 0){

                    iv_cancel.setVisibility(View.VISIBLE);
                    btn_apply_coupon.setVisibility(View.VISIBLE);
                    tv_msg.setText("");
                    tv_msg.setVisibility(View.GONE);
                }else {
                    iv_cancel.setVisibility(View.INVISIBLE);
                    btn_apply_coupon.setVisibility(View.INVISIBLE);
                    tv_msg.setText("");
                    tv_msg.setVisibility(View.GONE);
                }

            }
        });


        btn_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                globalClass.setCoupon_code("");
                globalClass.setCoupon_applied("N");
                globalClass.setCoupon_amount(0);


                tv_msg.setText("");
                getCouponAmount(edt_coupon_code.getText().toString());
            }
        });

    }



    private void getCouponAmount(final String coupon){

        progressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CouponModel> call = apiService.getpromocodeamount(
                sharedPref.getUserId(),
                coupon
        );

        call.enqueue(new Callback<CouponModel>() {
            @Override
            public void onResponse(Call<CouponModel>call, retrofit2.Response<CouponModel> response) {

                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            tv_msg.setText("Coupon Applied\nAmount is : ₹"
                                    +response.body().getPromocode_info());
                            tv_msg.setTextColor(getResources().getColor(R.color.green));
                            tv_msg.setVisibility(View.VISIBLE);

                            globalClass.setCoupon_code(coupon);
                            globalClass.setCoupon_applied("Y");
                            globalClass.setCoupon_amount(Float.parseFloat(response.body().getPromocode_info()));

                        }else {

                            tv_msg.setText("Invalid Coupon Code.\nCheck the code and try again");
                            tv_msg.setTextColor(getResources().getColor(R.color.red));
                            tv_msg.setVisibility(View.VISIBLE);

                            globalClass.setCoupon_code("");
                            globalClass.setCoupon_applied("N");
                            globalClass.setCoupon_amount(0);
                        }



                    }

                }catch (Exception e){
                    e.printStackTrace();
                }




                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<CouponModel>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                progressDialog.dismiss();
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



}
