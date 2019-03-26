package com.project.sketch.ugo.screen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.sketch.ugo.Payment.StartPaymentActivity;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Developer on 2/8/18.
 */

public class PaymentMode extends AppCompatActivity {
    String TAG="payment";
    Toolbar toolbar;
    RadioButton radioCash,radioWallet;
    RelativeLayout rl_cash, rl_wallet_amt, rl_JioMoney;
    TextView tv_msg,tv_wallet_amount;
    SharedPref sharedPref;
    GlobalClass globalClass;
    EditText edt_add_amount;
    RelativeLayout rl_add_money;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_mode_screen);
        initViews();
        checkWallet();


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
        globalClass = (GlobalClass) getApplicationContext();

        pDialog  = new ProgressDialog(PaymentMode.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Please wait...");

        rl_cash = findViewById(R.id.rl_cash);
        tv_wallet_amount = findViewById(R.id.tv_wallet_amount);
        rl_wallet_amt = (RelativeLayout) findViewById(R.id.rl_wallet_amt);
        rl_add_money = (RelativeLayout) findViewById(R.id.rl_add_money);


        radioCash = (RadioButton) findViewById(R.id.radioCash);
        radioWallet = (RadioButton) findViewById(R.id.radioWallet);
        edt_add_amount =  findViewById(R.id.edt_add_amount);

        tv_msg = (TextView) findViewById(R.id.tv_msg);
       // tv_msg.setVisibility(View.GONE);


        if (sharedPref.getPaymentMode().matches(Constants.paymentMode_cash)){
            radioCash.setChecked(true);
            radioWallet.setChecked(false);
           // radioJioMoney.setChecked(false);
        }else if (sharedPref.getPaymentMode().matches(Constants.paymentMode_Wallet)){
            radioCash.setChecked(false);
            radioWallet.setChecked(true);

        }/*else if (sharedPref.getPaymentMode().matches(Constants.paymentMode_jiomoney)){
            radioCash.setChecked(false);
            radioPaytm.setChecked(false);
            radioJioMoney.setChecked(true);
        }*/



        onClickAction();



    }

    public void onClickAction(){

        rl_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioCash.setChecked(true);
                radioWallet.setChecked(false);
              //  radioJioMoney.setChecked(false);

                sharedPref.setPaymentMode(Constants.paymentMode_cash);
                edt_add_amount.setVisibility(View.GONE);
              //  tv_msg.setVisibility(View.GONE);
                rl_add_money.setVisibility(View.GONE);
            }
        });


        rl_wallet_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioCash.setChecked(false);
                radioWallet.setChecked(true);
               // radioJioMoney.setChecked(false);

                sharedPref.setPaymentMode(Constants.paymentMode_Wallet);

               // tv_msg.setVisibility(View.VISIBLE);
                edt_add_amount.setVisibility(View.VISIBLE);
                rl_add_money.setVisibility(View.VISIBLE);

            }
        });

        rl_add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_add_amount.getText().toString().trim().length() == 0){
                    Toast.makeText(PaymentMode.this, "Enter amount", Toast.LENGTH_SHORT).show();

                    edt_add_amount.setError("Enter amount");
                    return;
                }

                String amt = edt_add_amount.getText().toString();


                try {

                    if (Double.parseDouble(amt) <= 0){
                        Toast.makeText(PaymentMode.this, "Enter valid amount", Toast.LENGTH_SHORT).show();

                        edt_add_amount.setError("Enter valid amount");
                        return;
                    }

                }catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(PaymentMode.this, "Enter valid amount", Toast.LENGTH_SHORT).show();
                    edt_add_amount.setError("Enter valid amount");

                    return;
                }




                String email=sharedPref.getUserEmail();
                String phone=sharedPref.getUserPhone();
                String name=sharedPref.getUserName();
                Intent intent = new Intent(PaymentMode.this, StartPaymentActivity.class);
                intent.putExtra("phone",phone);
                intent.putExtra("email",email);
                intent.putExtra("name",name);
                intent.putExtra("amount", amt);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
      /*  rl_JioMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioCash.setChecked(false);
                radioPaytm.setChecked(false);
                radioJioMoney.setChecked(false);

               // sharedPref.setPaymentMode(Constants.paymentMode_jiomoney);
                tv_msg.setVisibility(View.VISIBLE);
                tv_msg.setText(getResources().getString(R.string.msg_paymentMyJioMode));

            }
        });*/


/*
        radioCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    radioCash.setChecked(true);
                   // radioPaytm.setChecked(false);
                   // radioJioMoney.setChecked(false);

                    sharedPref.setPaymentMode(Constants.paymentMode_cash);

                    tv_msg.setVisibility(View.GONE);
                }

            }
        });
*/

       /* radioPaytm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    radioCash.setChecked(false);
                    radioPaytm.setChecked(true);
                    radioJioMoney.setChecked(false);

                    sharedPref.setPaymentMode(Constants.paymentMode_Wallet);

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
*/

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

    public void checkWallet(){

        pDialog.show();

        String url ="http://u-go.in/api/user_wallet";
        AsyncHttpClient cl = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("id",  sharedPref.getUserId());
        Log.d(TAG , "URL "+url);
        Log.d(TAG , "params "+params.toString());


        int DEFAULT_TIMEOUT = 30 * 1000;
        cl.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        cl.post(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "user_main_category_url- " + response.toString());
                if (response != null) {
                    Log.d(TAG, "user_main_category_url- " + response.toString());
                    try {



                        int status = response.getInt("status");
                        String message = response.getString("message");

                        if (status == 1) {

                            pDialog.dismiss();
                            String wallet_amount = response.getString("wallet_amount");
                           // float new_amt = Float.parseFloat(wallet_amount);

                            tv_wallet_amount.setText(wallet_amount);


                        }


                        else{


                            Toast.makeText(PaymentMode.this, ""+message, Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "responseString- " + responseString.toString());
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });


    }


}
