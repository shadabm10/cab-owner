package com.project.sketch.ugo.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.SharedPref;

/**
 * Created by Developer on 2/8/18.
 */

public class PaymentMode extends AppCompatActivity {

    Toolbar toolbar;
    RadioButton radioCash, radioPaytm, radioJioMoney;
    RelativeLayout rl_cash, rl_paytm, rl_JioMoney;
    TextView tv_msg;
    SharedPref sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_mode_screen);
        initViews();


    }


    public void initViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_payment_mode));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        sharedPref = new SharedPref(this);

        rl_cash = (RelativeLayout) findViewById(R.id.rl_cash);
        rl_paytm = (RelativeLayout) findViewById(R.id.rl_paytm);
        rl_JioMoney = (RelativeLayout) findViewById(R.id.rl_JioMoney);

        radioCash = (RadioButton) findViewById(R.id.radioCash);
        radioPaytm = (RadioButton) findViewById(R.id.radioPaytm);
        radioJioMoney = (RadioButton) findViewById(R.id.radioJioMoney);

        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);


        if (sharedPref.getPaymentMode().matches(Constants.paymentMode_cash)){
            radioCash.setChecked(true);
            radioPaytm.setChecked(false);
            radioJioMoney.setChecked(false);
        }else if (sharedPref.getPaymentMode().matches(Constants.paymentMode_paytm)){
            radioCash.setChecked(false);
            radioPaytm.setChecked(true);
            radioJioMoney.setChecked(false);
        }else if (sharedPref.getPaymentMode().matches(Constants.paymentMode_jiomoney)){
            radioCash.setChecked(false);
            radioPaytm.setChecked(false);
            radioJioMoney.setChecked(true);
        }



        onClickAction();



    }

    public void onClickAction(){

        rl_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioCash.setChecked(true);
                radioPaytm.setChecked(false);
                radioJioMoney.setChecked(false);

                sharedPref.setPaymentMode(Constants.paymentMode_cash);

                tv_msg.setVisibility(View.GONE);
            }
        });


        rl_paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioCash.setChecked(false);
                radioPaytm.setChecked(true);
                radioJioMoney.setChecked(false);

                sharedPref.setPaymentMode(Constants.paymentMode_paytm);

                tv_msg.setVisibility(View.GONE);
            }
        });


        rl_JioMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioCash.setChecked(false);
                radioPaytm.setChecked(false);
                radioJioMoney.setChecked(false);

               // sharedPref.setPaymentMode(Constants.paymentMode_jiomoney);
                tv_msg.setVisibility(View.VISIBLE);
                tv_msg.setText(getResources().getString(R.string.msg_paymentMyJioMode));

            }
        });


        radioCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    radioCash.setChecked(true);
                    radioPaytm.setChecked(false);
                    radioJioMoney.setChecked(false);

                    sharedPref.setPaymentMode(Constants.paymentMode_cash);

                    tv_msg.setVisibility(View.GONE);
                }

            }
        });

        radioPaytm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    radioCash.setChecked(false);
                    radioPaytm.setChecked(true);
                    radioJioMoney.setChecked(false);

                    sharedPref.setPaymentMode(Constants.paymentMode_paytm);

                    tv_msg.setVisibility(View.GONE);
                }

            }
        });

        radioJioMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    radioCash.setChecked(false);
                    radioPaytm.setChecked(false);
                    radioJioMoney.setChecked(false);

                   // sharedPref.setPaymentMode(Constants.paymentMode_jiomoney);

                    tv_msg.setVisibility(View.VISIBLE);
                    tv_msg.setText(getResources().getString(R.string.msg_paymentMyJioMode));
                }

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();

        super.onBackPressed();

    }



}
