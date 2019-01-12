package com.project.sketch.ugo.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.apiModel2.EstimateData;
import com.project.sketch.ugo.utils.GlobalClass;

/**
 * Created by Developer on 1/18/18.
 */

public class CostFareCalculation extends AppCompatActivity{

    private Toolbar toolbar;
    CheckBox checkbox_guider;
    TextView tv_ride_fare, tv_guide_fees, tv_tax, tv_total, tv_coupon_amt;
    Button btn_gotit;
    RelativeLayout rl_guider,rl_checkbox, rl_coupon;
    String guide_option;
    EstimateData estimateData;

    GlobalClass globalClass;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cost_calculation);
        initViews();



    }


    public void initViews(){

        globalClass = (GlobalClass) getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_estimate_cost_details));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);


        checkbox_guider = (CheckBox) findViewById(R.id.checkbox_guider);
        tv_ride_fare = (TextView) findViewById(R.id.tv_ride_fare);
        tv_guide_fees = (TextView) findViewById(R.id.tv_guide_fees);
        tv_tax = (TextView) findViewById(R.id.tv_tax);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_coupon_amt = (TextView) findViewById(R.id.tv_coupon_amt);
        rl_guider = (RelativeLayout) findViewById(R.id.rl_3);
        rl_checkbox = (RelativeLayout) findViewById(R.id.rl_1);
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_coupon);
        btn_gotit = (Button) findViewById(R.id.btn_gotit);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            estimateData = (EstimateData) bundle.getSerializable("data");
            guide_option = bundle.getString("guide_option");

            tv_ride_fare.setText("₹ "+(int)estimateData.getEstimate_fare() +".00 - "
                    +(int)estimateData.getEstimate_fare_maximum() +".00");

            tv_guide_fees.setText("₹ "+(int)estimateData.getEstimate_guide_charge() +".00");
            tv_tax.setText("₹ "+(int)estimateData.getTotal_fees_and_charges() + ".00");

            tv_coupon_amt.setText("₹ "+(int)globalClass.getCoupon_amount() + ".00");

            float totalWithGuideMin = estimateData.getEstimate_fare()
                    + estimateData.getEstimate_guide_charge()
                    + estimateData.getTotal_fees_and_charges()
                    - globalClass.getCoupon_amount();

            float totalWithGuideMax = estimateData.getEstimate_fare_maximum()
                    + estimateData.getEstimate_guide_charge()
                    + estimateData.getTotal_fees_and_charges()
                    - globalClass.getCoupon_amount();

            tv_total.setText("₹ "+(int)totalWithGuideMin +".00"
                    +" - "+(int)totalWithGuideMax +".00");


        }

        if (globalClass.getCoupon_applied().matches("Y")){
            rl_coupon.setVisibility(View.VISIBLE);
        }else {
            rl_coupon.setVisibility(View.GONE);
        }

        onClickHit();


        if (guide_option.matches("Y")){
            checkbox_guider.setChecked(true);
        }else {
            checkbox_guider.setChecked(false);
        }





    }

    private void onClickHit(){

        checkbox_guider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    guide_option = "Y";

                    float totalWithGuideMin = estimateData.getEstimate_fare()
                            + estimateData.getEstimate_guide_charge()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    float totalWithGuideMax = estimateData.getEstimate_fare_maximum()
                            + estimateData.getEstimate_guide_charge()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    tv_total.setText("₹ "+(int)totalWithGuideMin +".00"
                            +" - "+(int)totalWithGuideMax +".00");


                    rl_guider.setVisibility(View.VISIBLE);

                }else {
                    guide_option = "N";

                    float totalWithOutGuideMin = estimateData.getEstimate_fare()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    float totalWithOutGuideMax = estimateData.getEstimate_fare_maximum()
                            + estimateData.getTotal_fees_and_charges()
                            - globalClass.getCoupon_amount();

                    tv_total.setText("₹ "+(int)totalWithOutGuideMin +".00"
                            +" - "+(int)totalWithOutGuideMax +".00");

                    rl_guider.setVisibility(View.GONE);
                }


            }
        });


        btn_gotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("guide_option",guide_option);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }
        });


        rl_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkbox_guider.isChecked()){
                    checkbox_guider.setChecked(false);
                }else {
                    checkbox_guider.setChecked(true);
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                Intent returnIntent = new Intent();
                returnIntent.putExtra("guide_option",guide_option);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("guide_option",guide_option);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

        super.onBackPressed();

    }



}
